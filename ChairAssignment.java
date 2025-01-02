package unit_2_Robots;
import becker.robots.*;
import java.util.Random;

/**
 * Application class for the ChairRobot which takes all the chairs from a cafeteria and puts them
 * in the storage room.
 * @author Maher Rahman
 * @version Nov. 10, 2023
 */
public class ChairAssignment {
    // constants to be used throughout program
    static final int CAF_STR = 1;
    static final int CAF_AVE = 1;
    static final int STORAGE_HEIGHT = 1;
    static final int MAX_WIDTH = 10;
    static final int MAX_HEIGHT = 6;
    static final int MAX_DISTANCE = 5;
    static final int MAX_CHAIRS_PER_STACK = 10;

    public static void main(String[] args) {
        // determine random dimensions for the cafeteria and storage room
        Random generator = new Random();
        int width = generator.nextInt(MAX_WIDTH) + 1;
        int height = generator.nextInt(MAX_HEIGHT) + 1;
        int distance = generator.nextInt(MAX_DISTANCE) + 1;
        // System.out.println(width);
        // System.out.println(height);
        // System.out.println(distance);

        // make the cafeteria and storage rooms
        City oakville = new City();
        oakville.showThingCounts(true);
        makeMap(oakville, width, height, distance, generator);

        // make the ChairMoverRobot at a random location within the cafeteria
        int robotStr = generator.nextInt(height) + CAF_STR;
        int robotAve = generator.nextInt(width) + CAF_AVE;
        RahmanChairMoverRobot r = new RahmanChairMoverRobot(oakville, robotStr, robotAve, Direction.NORTH);

        // have the robot clean the cafeteria
        r.moveChairs();
    }

    /**
     * Creates the cafeteria and storage room
     * @param c - City to build in
     * @param w - width of cafeteria and storage room
     * @param h - height of cafeteria
     * @param d - distance between bottom of cafeteria and the storage room
     */
    private static void makeMap(City c, int w, int h, int d, Random g) {
        // make the CAFETERIA
        // make an array to store the Walls of the cafeteria
        Wall[][] cafWalls = new Wall[h][w];

        // get a random location for the door
        int doorLocation = g.nextInt(w) + CAF_AVE;
        // System.out.println(doorLocation);

        // make the top and bottom walls of the cafeteria
        for (int i = 0; i < w; i ++) {
            // top walls
            cafWalls[0][i] = new Wall(c, CAF_STR, i + CAF_AVE, Direction.NORTH);
            // don't make a bottom wall for the door location
            if (i + 1 != doorLocation) {
                //bot walls
                cafWalls[h - 1][i] = new Wall(c, CAF_STR + h - 1, i + CAF_AVE, Direction.SOUTH);
            }
        }
        // make the right and left walls of the cafeteria
        for (int i = 0; i < h; i ++) {
            // left walls
            cafWalls[i][0] = new Wall(c, i + CAF_STR, CAF_AVE, Direction.WEST);
            // right walls
            cafWalls[i][w - 1] = new Wall(c, i + CAF_STR, CAF_AVE + w - 1, Direction.EAST);
        }

        // make the STORAGE ROOM
        // make an array to store the Walls of the storage room
        Wall[][] srWalls = new Wall[STORAGE_HEIGHT + 1][w];
        int srStr = CAF_STR + h + d;

        // make the left and right walls of the storage room
        for (int i = 0; i < STORAGE_HEIGHT; i ++) {
            // left walls
            srWalls[i][0] = new Wall(c, srStr + i, CAF_AVE, Direction.WEST);
            // right walls
            srWalls[i][w - 1] = new Wall(c, srStr + i, CAF_AVE + w - 1, Direction.EAST);
        }
        // make the bottom walls of the storage room
        for (int i = 0; i < w; i ++) {
            srWalls[STORAGE_HEIGHT][i] = new Wall(c, srStr + STORAGE_HEIGHT - 1, CAF_AVE + i, Direction.SOUTH);
        }

        // make the CHAIRS
        // the max chairs is the width of the storage room times the max amount per stack in the storage room
        int maxChairs = w * MAX_CHAIRS_PER_STACK;
        // determine a random number of chairs for the cafeteria <= to the max
        int numChairsCaf = g.nextInt(maxChairs) + 1;
        int numChairsStorageRoom = maxChairs - numChairsCaf;
        // System.out.println(numChairsCaf);

        // spawn in the chairs in the cafeteria
        for (int i = 0; i < numChairsCaf; i++) {
            int chairStr = g.nextInt(h) + CAF_STR;
            int chairAve = g.nextInt(w) + CAF_AVE;
            Thing chair = new Thing(c, chairStr, chairAve);
        }
        /*
        System.out.println(CAF_AVE + w);
        System.out.println(CAF_STR + h);
         */

        // spawn in the chairs in the storage room
        int numChairsLeft = numChairsStorageRoom;
        // put a random amount of chairs for each stack in the storage room
        for (int i = 0; i < w; i ++) {
            int numForStack = g.nextInt(10) + 1;
            // ensure that the amount of chairs left is not being exceeded
            if (! (numForStack <= numChairsLeft)) {
                numForStack = numChairsLeft;
            }
            numChairsLeft -= numForStack;
            // place the chairs at the stack
            for (int j = 0; j < numForStack; j++) {
                Thing chair = new Thing(c, srStr + STORAGE_HEIGHT - 1, CAF_AVE + i);
            }
        }
    }
}