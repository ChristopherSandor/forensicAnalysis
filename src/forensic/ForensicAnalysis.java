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

    public Profile createSingleProfile() {

        // Given amount of STR for this particular profile:
        int profile_STR_Count = StdIn.readInt();

        // Creating an array to store all the STRs for this profile:
        STR[] strArray = new STR[profile_STR_Count];

        // Going through the given amount of STR's and creating STR Objects then adding them to strArray: 
        for(int i = 0; i < profile_STR_Count; i++){

            // Reading Inputs:
            String strName = StdIn.readString();
            int occurrences = StdIn.readInt();

            // Creating STR Object and Assigning the position in Array to the newly created object:
            STR strObject = new STR(strName, occurrences);
            strArray[i] = strObject;
        
        }

        // Creating the profile with its given information of STRs in an STR object:
        Profile profileObject = new Profile(strArray);

        return profileObject; 
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

        TreeNode pointer = treeRoot;
        TreeNode insertNode = new TreeNode(name, newProfile, null, null);
        boolean jailBreak = false;

        if(treeRoot == null){
            treeRoot =  insertNode;
        } else {

        while(jailBreak == false){

            int comparison = name.compareTo(pointer.getName());

            if(comparison < 0){

               if(pointer.getLeft() == null){
                    pointer.setLeft(insertNode);
                    jailBreak = true;
                    break;
               } 
               pointer = pointer.getLeft();

            } 
            // Comparison > 0
            else{

                if(pointer.getRight() == null){
                    pointer.setRight(insertNode);
                    jailBreak = true;
                    break;
                }
                pointer = pointer.getRight();

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
    public int getMatchingProfileCount(boolean isOfInterest) {
        
        // gets the total count:
        int totalCount = traverseOfGetMatchingProfileCount(treeRoot, isOfInterest);

        return totalCount; 
    }

    private int traverseOfGetMatchingProfileCount(TreeNode pointer, boolean check){
        int count = 0;

        // Ends if null
        if(pointer == null){
            return 0;
        }

        // Goes to left:
        count = count + traverseOfGetMatchingProfileCount(pointer.getLeft(), check);

        // Checks itself:
        count = count + matchingProfileCountJudgement(pointer, check);

        // Goes to right:
        count = count + traverseOfGetMatchingProfileCount(pointer.getRight(), check);

        return count;
    }

    private int matchingProfileCountJudgement(TreeNode pointer, boolean check){

        if(pointer.getProfile().getMarkedStatus() == check){
            return 1;
        }
        return 0;
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

    /**
     * Traverses the BST at treeRoot to mark profiles if:
     * - For each STR in profile STRs: at least half of STR occurrences match (round
     * UP)
     * - If occurrences THROUGHOUT DNA (first + second sequence combined) matches
     * occurrences, add a match
     */
    public void flagProfilesOfInterest() {
        traverseOfFlagProfilesOfInterest(treeRoot);
    }

    // Traverse the tree and calls judgement:
    private void traverseOfFlagProfilesOfInterest(TreeNode pointer){

        // Checks if Current is null;
        if(pointer == null){
            return;
        }

        // Goes Left:
        traverseOfFlagProfilesOfInterest(pointer.getLeft());

        // Checks current data:
        treeJudgement(pointer);

        // Goes Right:
        traverseOfFlagProfilesOfInterest(pointer.getRight());
    }

    // Checks and makes a judgement on each Node:
    private void treeJudgement(TreeNode pointer){

            // For each tree node:
            Profile profile = pointer.getProfile();
            int totalNumberOfStrs = profile.getStrs().length;
            int countOfMatches = 0;

            // For each STR in each Profile:
            for(STR str: profile.getStrs()){
                int numOfStrOccurrences = str.getOccurrences();
                int totalOccurrences = (numberOfOccurrences(this.firstUnknownSequence, str.getStrString()) + numberOfOccurrences(this.secondUnknownSequence, str.getStrString()));
                boolean matchTotal = (totalOccurrences == numOfStrOccurrences);
                if(matchTotal){
                    countOfMatches++;
                }
            }

            // Sets the threshold and rounds up if needed:
            int strThreshhold = 0;
            if(totalNumberOfStrs % 2 == 1){
                strThreshhold = ((int)(totalNumberOfStrs / 2) + 1);
            } else {
                strThreshhold = (totalNumberOfStrs / 2);
            }

            // Sets the status of the profile:
            if(countOfMatches >= strThreshhold){
                profile.setInterestStatus(true);
            }

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

        // Default list creation:
        int length = getMatchingProfileCount(false);
        String[] unmarkedPeople = new String[length];
        int positionCounter = 0;

        // Default Queue creation:
        Queue<TreeNode> queue = new Queue<>();
        queue.enqueue(treeRoot);

        // Cycles through tree (started from Root):
        while(queue.isEmpty() == false){

            // Grabs Node from the Queue:
            TreeNode node = queue.dequeue();
            // Assigning profile from node:
            Profile profile = node.getProfile();

            // Checks the profile and adds to list and moves counter up +1:
            if(profile.getMarkedStatus() == false){
                unmarkedPeople[positionCounter] = node.getName();
                positionCounter++;
            }

            // Adds children to queue (if they exist), first left then right [ORDER MATTERS]:
            if(node.getLeft() != null){
                queue.enqueue(node.getLeft());
            }
            if(node.getRight() != null){
                queue.enqueue(node.getRight());
            }

        }

        return unmarkedPeople;
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
    public void removePerson(String fullName) {
        
        // Node to be deleted:
        TreeNode foundNode = null;

        // Found Node Parent:
        TreeNode parentNode = null;
        // Left = -1 Right = 1:
        int direction = -1;

        // Pointer for the while loop:
        TreeNode pointer = treeRoot;

        // Goes through tree and finds the Node to Delete, but does not delete and only assigns to foundNode:
        while(pointer != null){
            
            // Checks to see if pointer is the node we want:
            if(pointer.getName().equals(fullName)){
                foundNode = pointer;
                pointer = null;
                break;
            } 

            //  If Not then we determine if we want to go left or right to find the node we want:
            else {
                int comparison = fullName.compareTo(pointer.getName());

                if(comparison < 0){
                    // Left
                    parentNode = pointer;
                    direction = -1;
                    pointer = pointer.getLeft();
                } 
                else {
                    // Right
                    parentNode = pointer;
                    direction = 1;
                    pointer = pointer.getRight();
                }
            }
            
        }

        // Checks to see the condition of foundNode (children or not) and performs the proper delete method:
        if(foundNode.getLeft() == null && foundNode.getRight() == null){
            // 0 Children:

            // Left Node Deleted:
            if(direction == -1){
                parentNode.setLeft(null);
            }

            // Right Node Deleted:
            else if(direction == 1){
                parentNode.setRight(null);
            }

            // jailbreak we are done 
            return;

        } 
        else {
            // Node with two children
            if(foundNode.getLeft() != null && foundNode.getRight() != null) {
                // Find in-order successor (smallest in the right subtree)
                TreeNode successorParent = foundNode;
                TreeNode successor = foundNode.getRight();
                while(successor.getLeft() != null) {
                    successorParent = successor;
                    successor = successor.getLeft();
                }
                
                // Copy successor data to foundNode
                foundNode.setName(successor.getName());
                foundNode.setProfile(successor.getProfile());
                
                // Delete successor
                if(successorParent != foundNode) {
                    successorParent.setLeft(successor.getRight());
                } else {
                    successorParent.setRight(successor.getRight());
                }
            } 
            // Node with only one child
            else {
                
                TreeNode child;
                if (foundNode.getLeft() != null) {
                    child = foundNode.getLeft();
                } else {
                    child = foundNode.getRight();
                }     
                // If deleting the root node with one child
                if(parentNode == null) {
                    treeRoot = child;
                } else {
                    if(direction == -1) {
                        parentNode.setLeft(child);
                    } else {
                        parentNode.setRight(child);
                    }
                }
            }
        }
        
    }

    /**
     * Clean up the tree by using previously written methods to remove unmarked
     * profiles.
     * Requires the use of getUnmarkedPeople and removePerson.
     */
    public void cleanupTree() {
        
        // List of people:
        String[] list = getUnmarkedPeople();

        // Loops through List and removes each person:
        for(int i = 0; i < list.length; i++){
            removePerson(list[i]);
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
