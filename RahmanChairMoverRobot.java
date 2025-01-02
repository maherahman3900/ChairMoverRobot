package unit_2_Robots;
import becker.robots.*;
import java.lang.Math;

/**
 * Template class for making a ChairMoverRobot, which is capable of taking all the chairs in a cafeteria
 * and storing them in the storage room 1 by 1.
 * @author Maher Rahman
 * @version Nov. 11, 2023
 */
public class RahmanChairMoverRobot extends RobotSEV2 {
    // variables to be used throughoutt program
    // check if all the Chair have been cleaned
    private boolean finishedMoving = false;
    // the Street the robot is currently cleaning
    private int currStr = 0;
    // the Spot the robot took the last chair from
    private int[] lastSpot = new int[2];
    // count the number of Chairs which have been picked up so far
    private int numChairsPicked = 0;
    // check if the exit has been found or not
    private boolean exitFound = false;
    // coordinates of the exit
    private int[] exitSpot = new int[2];
    // coordinates of the storage room
    private int[] srSpot = new int[2];
    // number of chairs at the current spot
    private int chairsAtSpot = -1;
    // maximum amount of chairs per stack in the storage room
    private final int MAX_CHAIRS_PER_STACK = 10;

    /**
     * Constructor method for creating a ChairMoverRobot
     * @param c - City to make the robot in
     * @param s - street/y-coordinate to spawn the robot
     * @param a - avenue/x-coordinate to spawn the robot
     * @param d - direction the robot should be facing
     */
    public RahmanChairMoverRobot(City c, int s, int a, Direction d) {
        super(c, s, a, d);
    }

    /**
     * This method has the robot clean up the cafeteria and move the chairs to the storage room
     * pre: robot is in the cafeteria and has an empty backpack
     * post: all the Chairs have been moved from the cafeteria to the storage room
     */
    public void moveChairs() {
        // the robot will start cleaning from the top left corner
        this.getToTopLeft();

        // as long as the robot has not finished cleaning the entire cafeteria, continue
        // finished when the robot has reached the bottom right corner and put away any Chairs there
        while (true) {
            this.getChair();
            System.out.println("about to check if");
            if (finishedMoving) {
                System.out.println("breaking");
                break;
            }
            this.putAwayChair();
            this.returnToSpot();
        }
        System.out.println("finished cleaning!");
    }

    /**
     * this method gets the Robot back to the spot it picked up the last Chair from
     */
    private void returnToSpot() {
        this.backToExit();
        this.backToSpot();
    }

    /**
     * this method gets the Robot from the Exit to the location of the lasat Chair it picked up
     * helper method for the returnToSpot() method
     */
    private void backToSpot() {
        // get the robot's current coordinates
        int[] currCoors = this.getCoordinates();
        // get the robot from its current spot to the Last Spot where the Chair was
        this.oneSpotToNext(currCoors, lastSpot);
    }

    /**
     * this method gets the Robot back from the Storage Room to the Exit
     * helper method for the returnToSpot() and toExit() methods
     */
    private void backToExit() {
        // get the robot's current coordinates
        int[] currCoors = this.getCoordinates();
        // get the robot from its current spot to the Exit
        int[] adjustedExitSpot = exitSpot;
        adjustedExitSpot[0] += 1;
        this.oneSpotToNext(currCoors, adjustedExitSpot);
    }

    /**
     * takes the robot to the storage room and puts down the Chair
     */
    private void putAwayChair() {
        this.toExit();
        this.toStorageRoom();
        this.placeChair();
    }

    /**
     * places the Chair where it can
     * helper method for the putAwayChair() method
     */
    private void placeChair() {
        // get the coordinates of the current spot
        srSpot = this.getCoordinates();
        // as long as the robot has not yet placed the Chair, keep checking if it can
        boolean placedChair = false;
        while (!placedChair) {
            // get the number of chairs at the spot if it is unknown
            if (chairsAtSpot == -1) {
                chairsAtSpot = this.countThings();
            } else if (chairsAtSpot == MAX_CHAIRS_PER_STACK) {
                // if the stack is full, check the next stack
                srSpot[1] += 1;
                this.toEast(this.getDirection());
                this.move();
                chairsAtSpot = this.countThings();
            } else if (chairsAtSpot == MAX_CHAIRS_PER_STACK - 1) {
                // if the stack is about to be full, place the chair and next time go to the next stack over
                this.putThing();
                srSpot[1] += 1;
                chairsAtSpot = -1;
                placedChair = true;
            } else {
                // if there is space in the stack, place a chair
                this.putThing();
                chairsAtSpot += 1;
                placedChair = true;
            }
        }
    }

    /**
     * checks the amount of chairs at the spot in the Storage Room
     * @return - whether or not the robot can place a chair here
     */
    private boolean checkChairs() {
        boolean canPlace = false;
        int numChairsAtSpot = this.countThings();
        // if the number of chairs at this spot is less than the max per stack, the robot can place a chair here
        if (numChairsAtSpot < MAX_CHAIRS_PER_STACK) {
            canPlace = true;
        }
        return canPlace;
    }

    /**
     * takes the robot to the storage room
     * helper method for the putAwayChair() method
     */
    private void toStorageRoom() {
        // if the robot has only picked up 1 chair so far, it has to find the storage room
        if (numChairsPicked == 1) {
            // move down until the robot hits the storage room's bot wall
            this.toSouth(this.getDirection());
            while (this.frontIsClear()) {
                this.move();
            }
            // move to the left until the robot hits the storage room's left wall
            this.toWest(this.getDirection());
            while (this.frontIsClear()) {
                this.move();
            }
            // get the coordinates of the first stack in the Storage Room
            srSpot = this.getCoordinates();
        } else { // if the robot has been to the storage room before, it can just go to the known coordinates
            // get from the robot's current spot to the Storage Room
            int[] currCoors = this.getCoordinates();
            this.oneSpotToNext(currCoors, srSpot);
        }

    }


    /**
     * takes the robot to the exit of the cafeteria
     * helper method for the putAwayChair() method
     */
    private void toExit() {
        // if the robot has only picked up 1 chair so far, it needs to find the exit
        if (numChairsPicked == 1) {
            this.findExit();
        } else { // otherwise, the exit location is already known, so move to the exit
            // get the robots current coordinates
            int[] currCoors = this.getCoordinates();
            int [] adjustedExitSpot = exitSpot;
            adjustedExitSpot[0] -= 1;
            this.oneSpotToNext(currCoors, adjustedExitSpot);
        }
    }

    /**
     * has the robot find the exit the first time it is putting away a Chair
     * helper method for the toExit() method
     */
    private void findExit() {
        // get to the bottom left corner
        this.getToBotLeft();
        // check if there is an Exit at each x-coordinate along the bottom row
        while (! exitFound) {
            this.toSouth(this.getDirection());
            // if the exit has been found, get its coordinates
            if (this.frontIsClear()) {
                exitFound = true;
                exitSpot = this.getCoordinates();
                // System.out.println("Exit spot: " + exitSpot[0] + " " + exitSpot[1]);
            } else { // otherwise, move to the next x coordinate to check it
                // move eastwards, as the robot is at the West wall
                this.toEast(this.getDirection());
                this.move();
            }
        }
        // System.out.print(exitSpot[0] + " ");
        // System.out.println(exitSpot[1]);
    }

    /**
     * this method gets the robot to the bottom left corner of the cafeteria
     * helper method for the findExit() method
     */
    private void getToBotLeft() {
        this.moveToLeftWall();
        this.moveToBotWall();
    }

    /**
     * gets the robot to the South wall
     * helper method for the findExit() method
     */
    private void moveToBotWall() {
        this.toSouth(this.getDirection());
        // move until the Robot reaches a wall
        while (this.frontIsClear()) {
            // check that there is a wall to the left of the robot as it tries to find the bottom row
            this.toWest(this.getDirection());
            if (! this.frontIsClear()) {
                this.toSouth(this.getDirection());
                this.move();
            } else { // if there is no longer a wall to the robot's left, it has gone through the exit, so  get the exit's coordinates
                exitFound = true;
                exitSpot = this.getCoordinates();
                exitSpot[0] -= 1; // the Street is one less than the current Street
                // System.out.println("Exit spot: " + exitSpot[0] + " " + exitSpot[1]);
                break;
            }
        }
    }

    /**
     * has the robot pick up the first chair in its current row
     */
    private void getChair() {
        this.moveToChair();
        this.saveSpot();
        /*
        System.out.print(currSpot[0] + " ");
        System.out.println(currSpot[1]);
         */
        this.pickChair();
    }

    /**
     * has the robot pick up ONE Chair at its current location
     * helper method for the getChair() method
     */
    private void pickChair() {
        if (this.canPickThing()){
            this.pickThing();
            numChairsPicked += 1;
        }
    }

    /**
     * gets the location of the Thing the robot is picking up
     * helper method for the getChair() method
     */
    private void saveSpot() {
        System.out.println("saving");
        // store the location of the chair in currSpot
        lastSpot = this.getCoordinates();
    }

    /**
     * moves the robot to the closes chair in its current path
     * helper method for getChair() method
     */
    private void moveToChair() {
        // move until the robot can pick up a Thing
        while (! this.canPickThing() && ! finishedMoving) {
            // turn to the right direction based off the Street
            this.toStreetDirection();
            if (this.frontIsClear()) {
                this.move();
            } else {
                // System.out.println("going next street");
                // move to the next Street if the robot has run into a wall
                this.toNextStreet();
            }
        }
    }

    /**
     * takes the Robot to the next street if it has reached the end of its current street
     * helper method for the moveToChair() method
     */
    private void toNextStreet() {
        // on odd rows, turn right, and on even rows, turn left
        if (currStr % 2 != 0) {
            this.turnRight();
        } else {
            this.turnLeft();
        }
        // move down to the next Street
        if (this.frontIsClear()) {
            this.move();
            // System.out.println(finishedMoving);
            // check that the robot has not exited the cafeteria, thus having finished cleaning
            int[] currCoors = this.getCoordinates();
            //System.out.println("curr" + " " + currCoors[0] + " " + currCoors[1]);
            //System.out.println("exit" + " " + exitSpot[0] + " " + exitSpot[1]);
            if ((currCoors[0] == exitSpot[0]) && (currCoors[1] == exitSpot[1])) {
                finishedMoving = true;
                System.out.println("next street: " + finishedMoving);
            }
        } else {
            // if the robot can no longer pick up any Things nor go to a next Street, it has cleaned up the cafeteria
            finishedMoving = true;
            System.out.println("next street: " + finishedMoving);
        }

        System.out.println("finished next street");
        // update the Street the robot is on
        currStr += 1;
    }

    /**
     * turns the robot to either East or West based off what Street it is on
     * helper method for the moveToChair() and findExit() methods
     */
    private void toStreetDirection() {
        // on odd rows, move Eastwards, and on even rows, move Westwards
        if (currStr % 2 != 0) {
            this.toEast(this.getDirection());
        } else {
            this.toWest(this.getDirection());
        }
    }

    /**
     * takes the robot to the top-left corner of the cafeteria
     */
    private void getToTopLeft() {
        this.moveToLeftWall();
        this.moveToTopWall();
        // after getting to the top left corner, the Robot is currently checking the first Street
        currStr = 1;
    }

    /**
     * takes the robot to the West wall
     * helper method for getToTopLeft() and findExit() methods
     */
    private void moveToLeftWall() {
        this.toWest(this.getDirection());
        // move until the Robot reaches a wall
        while(this.frontIsClear()) {
            this.move();
        }
    }

    /**
     * takes the robot to the North wall
     * helper method for getToTopLeft() method
     */
    private void moveToTopWall() {
        this.toNorth(this.getDirection());
        // move until the Robot reaches a wall
        while (this.frontIsClear()) {
            this.move();
        }
    }
}