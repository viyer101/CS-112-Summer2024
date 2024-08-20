package forensic;

/**
 * This class represents a forensic analysis system that manages DNA data using
 * BSTs.
 * Contains methods to create, read, update, delete, and flag profiles.
 * 
 * @author Kal Pandit
 */
public class ForensicAnalysis {

    private TreeNode treeRoot;            // BST's root
    private String firstUnknownSequence;
    private String secondUnknownSequence;
    private int numOfMatchProfiles = 0;

    public ForensicAnalysis () {
        treeRoot = null;
        firstUnknownSequence = null;
        secondUnknownSequence = null;
    }

    /**
     * Builds a simplified forensic analysis database as a BST and populates unknown sequences.
     * The input file is formatted as follows:
     * 1. one line containing the number of people in the database, say p
     * 2. one line containing first unknown sequence
     * 3. one line containing second unknown sequence
     * 2. for each person (p), this method:
     * - reads the person's name
     * - calls buildSingleProfile to return a single profile.
     * - calls insertPerson on the profile built to insert into BST.
     *      Use the BST insertion algorithm from class to insert.
     * 
     * DO NOT EDIT this method, IMPLEMENT buildSingleProfile and insertPerson.
     * 
     * @param filename the name of the file to read from
     */
    public void buildTree(String filename) {
        // DO NOT EDIT THIS CODE
        StdIn.setFile(filename); // DO NOT remove this line

        // Reads unknown sequences
        String sequence1 = StdIn.readLine();
        firstUnknownSequence = sequence1;
        String sequence2 = StdIn.readLine();
        secondUnknownSequence = sequence2;
        
        int numberOfPeople = Integer.parseInt(StdIn.readLine()); 

        for (int i = 0; i < numberOfPeople; i++) {
            // Reads name, count of STRs
            String fname = StdIn.readString();
            String lname = StdIn.readString();
            String fullName = lname + ", " + fname;
            // Calls buildSingleProfile to create
            Profile profileToAdd = createSingleProfile();
            // Calls insertPerson on that profile: inserts a key-value pair (name, profile)
            insertPerson(fullName, profileToAdd);
        }
    }

    /** 
     * Reads ONE profile from input file and returns a new Profile.
     * Do not add a StdIn.setFile statement, that is done for you in buildTree.
    */
    public Profile createSingleProfile() {

        // WRITE YOUR CODE HERE
        int numOfSTRs = StdIn.readInt();
        STR[] strArray = new STR[numOfSTRs];
        for (int i = 0; i < numOfSTRs; i++)
        {
            String strString = StdIn.readString();
            int numOfOccurrences = StdIn.readInt();
            strArray[i] = new STR(strString, numOfOccurrences);
        }
        Profile str = new Profile(strArray);

        return str; // update this line
    }

    /**
     * Inserts a node with a new (key, value) pair into
     * the binary search tree rooted at treeRoot.
     * 
     * Names are the keys, Profiles are the values.
     * USE the compareTo method on keys.
     * 
     * @param newProfile the profile to be inserted
     */
    public void insertPerson(String name, Profile newProfile) {

        // WRITE YOUR CODE HERE
        TreeNode newTreeNode = new TreeNode(name, newProfile, null, null);
        if (treeRoot == null)
        {
            treeRoot = newTreeNode;
        }
        else
        {
            TreeNode currTreeNode = treeRoot;
            while (true)
            {
                TreeNode parentTreeNode = currTreeNode;
                int comp = name.compareTo(currTreeNode.getName());
                if (comp < 0)
                {
                    currTreeNode = currTreeNode.getLeft();
                    if (currTreeNode == null)
                    {
                        parentTreeNode.setLeft(newTreeNode);
                        return;
                    }
                }
                else if (comp > 0)
                {
                    currTreeNode = currTreeNode.getRight();
                    if (currTreeNode == null)
                    {
                        parentTreeNode.setRight(newTreeNode);
                        return;
                    }
                }
            }
        }
    }

    /**
     * Finds the number of profiles in the BST whose interest status matches
     * isOfInterest.
     *
     * @param isOfInterest the search mode: whether we are searching for unmarked or
     *                     marked profiles. true if yes, false otherwise
     * @return the number of profiles according to the search mode marked
     */

    private boolean getMarker(TreeNode treeRoot, boolean ofInterest)
    {
        return (treeRoot.getProfile().getMarkedStatus() == ofInterest);
    }
    private void matchingProfileCount(TreeNode treeRoot, boolean ofInterest)
    {
        if (treeRoot != null)
        {
            matchingProfileCount(treeRoot.getLeft(), ofInterest);
            if (getMarker(treeRoot, ofInterest))
                numOfMatchProfiles++;
            matchingProfileCount(treeRoot.getRight(), ofInterest);
        }
    }
    public int getMatchingProfileCount(boolean isOfInterest) {
        
        // WRITE YOUR CODE HERE
        numOfMatchProfiles = 0;
        matchingProfileCount(treeRoot, isOfInterest);
        return numOfMatchProfiles; // update this line
    }


    /**
     * Helper method that counts the # of STR occurrences in a sequence.
     * Provided method - DO NOT UPDATE.
     * 
     * @param sequence the sequence to search
     * @param STR      the STR to count occurrences of
     * @return the number of times STR appears in sequence
     */
    private int numberOfOccurrences(String sequence, String STR) {
        
        // DO NOT EDIT THIS CODE
        
        int repeats = 0;
        // STRs can't be greater than a sequence
        if (STR.length() > sequence.length())
            return 0;
        
            // indexOf returns the first index of STR in sequence, -1 if not found
        int lastOccurrence = sequence.indexOf(STR);
        
        while (lastOccurrence != -1) {
            repeats++;
            // Move start index beyond the last found occurrence
            lastOccurrence = sequence.indexOf(STR, lastOccurrence + STR.length());
        }
        return repeats;
    }

    private void setProfileMarker(TreeNode currTreeNode)
    {
        Profile currProfile = currTreeNode.getProfile();
        STR[] strArray = currProfile.getStrs();
        int strMatches = 0;
        for (int i = 0; i < strArray.length; i++)
        {
            int totalOccurrences = numberOfOccurrences(firstUnknownSequence, strArray[i].getStrString()) + numberOfOccurrences(secondUnknownSequence, strArray[i].getStrString());
            if (totalOccurrences == strArray[i].getOccurrences())
                strMatches++;
        }
        int halfOccurrences;
        if (strArray.length % 2 == 0)
            halfOccurrences = (strArray.length) / 2;
        else
            halfOccurrences = (strArray.length + 1) / 2;
        if (strMatches >= halfOccurrences)
            currProfile.setInterestStatus(true);
    }

    /**
     * Traverses the BST at treeRoot to mark profiles if:
     * - For each STR in profile STRs: at least half of STR occurrences match (round
     * UP)
     * - If occurrences THROUGHOUT DNA (first + second sequence combined) matches
     * occurrences, add a match
     */
    private void flagProfilesOfInterest(TreeNode root)
    {
        if (root != null)
        {
            flagProfilesOfInterest(root.getLeft());
            setProfileMarker(root);
            flagProfilesOfInterest(root.getRight());
        }
    }
    public void flagProfilesOfInterest() {

        // WRITE YOUR CODE HERE
        flagProfilesOfInterest(treeRoot);
    }

    /**
     * Uses a level-order traversal to populate an array of unmarked Strings representing unmarked people's names.
     * - USE the getMatchingProfileCount method to get the resulting array length.
     * - USE the provided Queue class to investigate a node and enqueue its
     * neighbors.
     * 
     * @return the array of unmarked people
     */
    public String[] getUnmarkedPeople() {

        // WRITE YOUR CODE HERE
        int numOfUnmarkedProfiles = getMatchingProfileCount(false);
        String[] unmarkedProfiles = new String[numOfUnmarkedProfiles];
        Queue<TreeNode> treeNodeQueue = new Queue<>();
        treeNodeQueue.enqueue(treeRoot);
        int i = 0;
        while (!treeNodeQueue.isEmpty())
        {
            TreeNode tempTreeNode = treeNodeQueue.dequeue();
            if (!tempTreeNode.getProfile().getMarkedStatus()) {
                unmarkedProfiles[i] = tempTreeNode.getName();
                i++;
            }
            
            if (tempTreeNode.getLeft() != null)
                treeNodeQueue.enqueue(tempTreeNode.getLeft());
            if (tempTreeNode.getRight() != null)
                treeNodeQueue.enqueue(tempTreeNode.getRight());
        }
        return unmarkedProfiles; // update this line
    }

    /**
     * Removes a SINGLE node from the BST rooted at treeRoot, given a full name (Last, First)
     * This is similar to the BST delete we have seen in class.
     * 
     * If a profile containing fullName doesn't exist, do nothing.
     * You may assume that all names are distinct.
     * 
     * @param fullName the full name of the person to delete
     */
    private TreeNode deleteMin(TreeNode root)
    {
        if (root.getLeft() == null)
            return root.getRight();
        root.setLeft(deleteMin(root.getLeft()));
        return root;
    }
    private TreeNode minNode(TreeNode root)
    {
        if (root.getLeft() == null)
            return root;

        return minNode(root.getLeft());
    }
    private TreeNode removePerson(TreeNode root, String fullName)
    {
        if (root == null)
            return null;
        int comp = fullName.compareTo(root.getName());
        if (comp < 0)
            root.setLeft(removePerson(root.getLeft(), fullName));
        else if (comp > 0)
            root.setRight(removePerson(root.getRight(), fullName));
        else
        {
            if (root.getRight() == null)
                return root.getLeft();
            if (root.getLeft() == null)
                return root.getRight();
            
            TreeNode replacedNode = root;
            root = minNode(replacedNode.getRight());
            root.setRight(deleteMin(replacedNode.getRight()));
            root.setLeft(replacedNode.getLeft());
        }
        return root;
    }

    public void removePerson(String fullName) {
        // WRITE YOUR CODE HERE
        treeRoot = removePerson(treeRoot, fullName);
        
    }

    /**
     * Clean up the tree by using previously written methods to remove unmarked
     * profiles.
     * Requires the use of getUnmarkedPeople and removePerson.
     */
    public void cleanupTree() {
        // WRITE YOUR CODE HERE
        String[] unmarkedPeople = getUnmarkedPeople();
        for (int i = 0; i < unmarkedPeople.length; i++)
        {
            removePerson(unmarkedPeople[i]);
        }
    }

    /**
     * Gets the root of the binary search tree.
     *
     * @return The root of the binary search tree.
     */
    public TreeNode getTreeRoot() {
        return treeRoot;
    }

    /**
     * Sets the root of the binary search tree.
     *
     * @param newRoot The new root of the binary search tree.
     */
    public void setTreeRoot(TreeNode newRoot) {
        treeRoot = newRoot;
    }

    /**
     * Gets the first unknown sequence.
     * 
     * @return the first unknown sequence.
     */
    public String getFirstUnknownSequence() {
        return firstUnknownSequence;
    }

    /**
     * Sets the first unknown sequence.
     * 
     * @param newFirst the value to set.
     */
    public void setFirstUnknownSequence(String newFirst) {
        firstUnknownSequence = newFirst;
    }

    /**
     * Gets the second unknown sequence.
     * 
     * @return the second unknown sequence.
     */
    public String getSecondUnknownSequence() {
        return secondUnknownSequence;
    }

    /**
     * Sets the second unknown sequence.
     * 
     * @param newSecond the value to set.
     */
    public void setSecondUnknownSequence(String newSecond) {
        secondUnknownSequence = newSecond;
    }

}
