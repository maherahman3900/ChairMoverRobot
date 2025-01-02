package unit_2_Robots;
import becker.robots.*;

/**
 * This is a child class of the parent class RobotSE. It inherited all of RobotSE, and I added some of my
 * own methods to it.
 * @author Maher Rahman
 * @version Nov. 6, 2023
 */
public class RobotSEV2 extends RobotSE{
    public RobotSEV2(City c, int s, int a, Direction d) {
        // use the parent/super class, Robot, constructor
        super(c, s, a,d);
    }

    /**
     * This method turns the robot to the North direction based off
     * the direction it is currently facing.
     * @param direction - direction robot is currently facing
     */
    public void toNorth(Direction direction) {
        // turn the appropriate amount/directions to get from one direction to North
        if (direction == Direction.EAST) {
            this.turnLeft();
        } else if (direction == Direction.SOUTH) {
            this.turnLeft();
            this.turnLeft();
        } else if (direction == Direction.WEST) { // direction == Direction.SOUTH
            this.turnRight();
        }
        // did not use an else statement in case the direction provided was already North
    }

    /**
     * This method turns the robot to the East direction based off
     * the direction it is currently facing.
     * @param direction - direction robot is currently facing
     */
    public void toEast(Direction direction) {
        // turn the appropriate amount/directions to get from one direction to East
        if (direction == Direction.NORTH) {
            this.turnRight();
        } else if (direction == Direction.WEST) {
            this.turnLeft();
            this.turnLeft();
        } else if (direction == Direction.SOUTH) { // direction == Direction.SOUTH
            this.turnLeft();
        }
        // did not use an else statement in case the direction provided was already East
    }

    /**
     * This method turns the robot to the South direction based off
     * the direction it is currently facing.
     * @param direction - direction robot is currently facing
     */
    public void toSouth(Direction direction) {
        // turn the appropriate amount/directions to get from one direction to South
        if (direction == Direction.NORTH) {
            this.turnLeft();
            this.turnLeft();
        } else if (direction == Direction.WEST) {
            this.turnLeft();
        } else if (direction == Direction.EAST) { // direction == Direction.EAST
            this.turnRight();
        }
        // did not use an else statement in case the direction provided was already South
    }

    /**
     * This method turns the robot to the West direction based off
     * the direction it is currently facing
     * @param direction - direction robot is currently facing
     */
    public void toWest(Direction direction) {
        // turn the appropriate amount/directions to get from one direction to West
        if (direction == Direction.NORTH) {
            this.turnLeft();
        } else if (direction == Direction.EAST) {
            this.turnLeft();
            this.turnLeft();
        } else if (direction == Direction.SOUTH) { // direction == Direction.SOUTH
            this.turnRight();
        }
        // did not use an else statement in case the direction provided was already West
    }

    /**
     * This method tells the user the amount of Things the robot has in its backpack
     * @return - return the number of Things in the robot's backpack
     */
    public int countThings() {
        int currentThings = this.countThingsInBackpack();
        this.pickAllThings();
        // the amount at the spot is how much the Robot picked up - how much it had before picking them up
        int thingsAtSpot = this.countThingsInBackpack() - currentThings;
        // put the Things back down at the spot
        this.putNumThings(thingsAtSpot);
        return thingsAtSpot;
    }

    /**
     * Puts down a specified number of Things
     * @param numThings - how many Things to put down
     */
    public void putNumThings(int numThings) {
        for (int i = 0; i < numThings; i++) {
            this.putThing();
        }
    }

    /**
     * gets the robots current coordinates
     * @return an integer array which contains the coordinates as [street, avenue]
     */
    public int[] getCoordinates() {
        // get the robots coordinates
        int street = this.getStreet();
        int avenue = this.getAvenue();
        // assign the coordinates to an int[] array
        int[] coordinates = new int[2];
        coordinates[0] = street;
        coordinates[1] = avenue;
        // return the coordinates
        return coordinates;
    }

    /**
     * this method gets the robot from one spot to another
     * @param spot1 - starting spot in [street, avenue] form
     * @param spot2 - end spot in [street, avenue] form
     */
    public void oneSpotToNext(int[] spot1, int[] spot2) {
        // get the difference in the Streets
        int streetDiff = spot2[0] - spot1[0];
        // move in the direction of the end Street
        if (! (streetDiff == 0)) { // ensure that the Robot is not already at the End street
            if (streetDiff < 0){ // if the end Street was a smaller number, turn North
                this.toNorth(this.getDirection());
            } else { // otherwise (streetDiff > 0), turn South
                this.toSouth(this.getDirection());
            }
            // move to the end Street
            this.move(Math.abs(streetDiff));
        }

        // get the difference in the Avenue
        int avenueDiff = spot2[1] - spot1[1];
        // move in the direction of the end avenue
        if (! (avenueDiff == 0)) { // ensure that the Robot is not already at the End avenue
            if (avenueDiff < 0){ // if the end Street was a smaller number, turn West
                this.toWest(this.getDirection());
            } else { // otherwise (streetDiff > 0), turn East
                this.toEast(this.getDirection());
            }
            // move to the end Avenue
            this.move(Math.abs(avenueDiff));
        }
    }
}