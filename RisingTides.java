package tides;
import java.util.*;

/**
 * This class contains methods that provide information about select terrains 
 * using 2D arrays. Uses floodfill to flood given maps and uses that 
 * information to understand the potential impacts. 
 * Instance Variables:
 *  - a double array for all the heights for each cell
 *  - a GridLocation array for the sources of water on empty terrain 
 * 
 * @author Original Creator Keith Scharz (NIFTY STANFORD) 
 * @author Vian Miranda (Rutgers University)
 */
public class RisingTides {

    // Instance variables
    private double[][] terrain;     // an array for all the heights for each cell
    private GridLocation[] sources; // an array for the sources of water on empty terrain 

    /**
     * DO NOT EDIT!
     * Constructor for RisingTides.
     * @param terrain passes in the selected terrain 
     */
    public RisingTides(Terrain terrain) {
        this.terrain = terrain.heights;
        this.sources = terrain.sources;
    }

    /**
     * Find the lowest and highest point of the terrain and output it.
     * 
     * @return double[], with index 0 and index 1 being the lowest and 
     * highest points of the terrain, respectively
     */
    public double[] elevationExtrema() {

        /* WRITE YOUR CODE BELOW */
        if (terrain == null || terrain.length == 0 || terrain[0].length == 0) //checks to see whether the terrain is null or empty
            throw new IllegalArgumentException("Terrain can't be null or empty");
        
        double lowestPoint = Double.MAX_VALUE; //lowest elevation parameter
        double highestPoint = Double.MIN_VALUE; //highest elevation parameter

        for (int i = 0; i < terrain.length; i++)
        {
            for (int j = 0; j < terrain[i].length; j++)
            {
                if (terrain[i][j] < lowestPoint)
                    lowestPoint = terrain[i][j];
                if (terrain[i][j] > highestPoint)
                    highestPoint = terrain[i][j];
            }
        }
        return new double[]{lowestPoint, highestPoint}; // substitute this line. It is provided so that the code compiles.
    }

    /**
     * Implement the floodfill algorithm using the provided terrain and sources.
     * 
     * All water originates from the source GridLocation. If the height of the 
     * water is greater than that of the neighboring terrain, flood the cells. 
     * Repeat iteratively till the neighboring terrain is higher than the water 
     * height.
     * 
     * 
     * @param height of the water
     * @return boolean[][], where flooded cells are true, otherwise false
     */
    public boolean[][] floodedRegionsIn(double height) {
        
        /* WRITE YOUR CODE BELOW */
        int rows = terrain.length;
        int cols = terrain[0].length;

        boolean[][] resultingArray = new boolean[rows][cols]; 
        ArrayList<GridLocation> listOfGridLocations = new ArrayList<GridLocation>();

        for (GridLocation currSource : sources) //uses a shorthand foreach statement to traverse the gridlocation array
        {
            listOfGridLocations.add(currSource);
            resultingArray[currSource.row][currSource.col] = true;
        }

        //directions of the 4 cardinal points:
        int[] rowDirection = {-1,1,0,0};
        int[] colDirection = {0,0,-1,1};

        while (!listOfGridLocations.isEmpty())  //performs breadth first search 
        {
            GridLocation currGridLoc = listOfGridLocations.remove(0); //removes first element from the arraylist of gridlocations.
            for (int i = 0; i < 4; i++) //traverses the cadinal point arrays
            {
                int newRow = currGridLoc.row + rowDirection[i]; //adds the gridlocation row with the row direction of the cardinal point
                int newCol = currGridLoc.col + colDirection[i]; //adds the gridlocation col with the col direction of the cardinal point

                if (newRow >= 0 && newRow < rows && newCol >= 0 && newCol < cols) //checks if each of the 4 neighbors are flooded or not
                {
                    if (terrain[newRow][newCol] <= height && !resultingArray[newRow][newCol]) //checks if the terrain height is below the water height
                    {
                        resultingArray[newRow][newCol] = true; //marks the location as flooded (true).
                        listOfGridLocations.add(new GridLocation(newRow,newCol)); //adds the new gridlocation to the list of grid locations
                    }
                }
            }
        }
        return resultingArray; // substitute this line. It is provided so that the code compiles.
    }

    /**
     * Checks if a given cell is flooded at a certain water height.
     * 
     * @param height of the water
     * @param cell location 
     * @return boolean, true if cell is flooded, otherwise false
     */
    public boolean isFlooded(double height, GridLocation cell) {
        
        /* WRITE YOUR CODE BELOW */
        return floodedRegionsIn(height)[cell.row][cell.col]; // substitute this line. It is provided so that the code compiles.
    }

    /**
     * Given the water height and a GridLocation find the difference between 
     * the chosen cells height and the water height.
     * 
     * If the return value is negative, the Driver will display "meters below"
     * If the return value is positive, the Driver will display "meters above"
     * The value displayed will be positive.
     * 
     * @param height of the water
     * @param cell location
     * @return double, representing how high/deep a cell is above/below water
     */
    public double heightAboveWater(double height, GridLocation cell) {
        
        /* WRITE YOUR CODE BELOW */
        return terrain[cell.row][cell.col] - height; // substitute this line. It is provided so that the code compiles.
    }

    /**
     * Total land available (not underwater) given a certain water height.
     * 
     * @param height of the water
     * @return int, representing every cell above water
     */
    public int totalVisibleLand(double height) {
        
        /* WRITE YOUR CODE BELOW */
        boolean[][] regionsFlooded = floodedRegionsIn(height); //declares a boolean array of all the flooded regions
        int landCount = 0; 
        for (int i = 0; i < terrain.length; i++) //traverses the row height of the terrain
        {
            for (int j = 0; j < terrain[0].length; j++) //traverses the column height of the terrain
            {
                if (!regionsFlooded[i][j]) //checks if the regions are NOT flooded 
                    landCount++; //increments the land count if this is true
            }
        }
        return landCount; // substitute this line. It is provided so that the code compiles.
    } 


    /**
     * Given 2 heights, find the difference in land available at each height. 
     * 
     * If the return value is negative, the Driver will display "Will gain"
     * If the return value is positive, the Driver will display "Will lose"
     * The value displayed will be positive.
     * 
     * @param height of the water
     * @param newHeight the future height of the water
     * @return int, representing the amount of land lost or gained
     */
    public int landLost(double height, double newHeight) {
        
        /* WRITE YOUR CODE BELOW */
        int changeInLand = totalVisibleLand(height) - totalVisibleLand(newHeight);
        return changeInLand; // substitute this line. It is provided so that the code compiles.
    }

    /**
     * Count the total number of islands on the flooded terrain.
     * 
     * Parts of the terrain are considered "islands" if they are completely 
     * surround by water in all 8-directions. Should there be a direction (ie. 
     * left corner) where a certain piece of land is connected to another 
     * landmass, this should be considered as one island. A better example 
     * would be if there were two landmasses connected by one cell. Although 
     * seemingly two islands, after further inspection it should be realized 
     * this is one single island. Only if this connection were to be removed 
     * (height of water increased) should these two landmasses be considered 
     * two separate islands.
     * 
     * @param height of the water
     * @return int, representing the total number of islands
     */
    public int numOfIslands(double height) {
        
        /* WRITE YOUR CODE BELOW */
        int row = terrain.length;
        int col = terrain[0].length;
        WeightedQuickUnionUF uf = new WeightedQuickUnionUF(row, col); //Weighted Quick union object 
        boolean[][] isFlooded = floodedRegionsIn(height); //floodedregionsin returns a 2D boolean array that checks whether a specific island region has been flooded or not

        int[][] cardinalDirections = {{-1,1},{1,0}, {0,1}, {-1,0}, {0,-1}, {1,1}, {-1,-1}, {1,-1}}; //cardinal directions
        for (int i = 0; i < terrain.length; i++)
        {
            for (int j = 0; j < terrain[0].length; j++)
            {
                if (!isFlooded[i][j]) //checks whether the terrain is not flooded
                {
                    GridLocation islandCell = new GridLocation(i,j); //current island
                    for (int[] cardDir : cardinalDirections) //traversal of cardinalDirections using a shorthand for each statement
                    {
                        int newRow = i + cardDir[0]; //row parameter of neighborcell
                        int newCol = j + cardDir[1]; //col parameter of neighborcell

                        if (newRow >= 0 && newRow < row && newCol >= 0 && newCol < col && !isFlooded[newRow][newCol]) //if the row and column parameters are legal parameters for the neighbor island and if there exists a connection between the neighbor and island cells.
                        {
                            GridLocation neighborCell = new GridLocation(newRow, newCol); //neighboring island
                            uf.union(islandCell, neighborCell); //uses the union method to connect the island and neighbor cells.
                        }
                    }
                }
            }
        }
        HashSet<GridLocation> uniqueRoots = new HashSet<GridLocation>(); //hashset of gridlocation objects
        for (int i = 0; i < terrain.length; i++)
        {
            for (int j = 0; j < terrain[0].length; j++)
            {
                if (!isFlooded[i][j]) //checks whether the terrain area is flooded or not
                    uniqueRoots.add(uf.find(new GridLocation(i,j))); //counts the number of unique roots and adds it to the hashset.
            }
        }
        
        return uniqueRoots.size(); // substitute this line. It is provided so that the code compiles.
    }
}
