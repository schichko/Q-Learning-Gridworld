import java.util.*;
import java.io.BufferedReader; 
import java.io.IOException; 
import java.io.InputStreamReader; 
import java.util.Random;
public class Main{
    //static char[] b;
    public static final int EXIT = -1;
    public static final int UP = 0;
    public static final int DOWN = 1;
    public static final int RIGHT = 2;
    public static final int LEFT = 3;
    public static final double discountRate = 0.5;
    public static final double learningRate = 0.1;
    public static final double livingPenalty = -0.1;
    public static BufferedReader reader = new BufferedReader(new InputStreamReader(System.in)); //We have to use a buffered reader instead of a scanner because our input incorporates spaces
            
    public static void main(String[] args) throws IOException{
        int counter = 1;    //Counter is used to set the position according to the assignment
        
        //Location Trackers
        int goalLocation = 0;   //Numerical Location of the goal
        int forbiddenLocation = 0;  //Numerical Location of the forbidden spot
        int wallLocation = 0;   //Numerical Location of the wall
        
        //Tracking Agent
        int currentPlayerLocation = 1; //The player starts at the start position
        int currentPlayeri = -1;
        int currentPlayerj = -1;

        char policyOrQ = ' ';   //Policy, can be p or q
        int nodeQ = 0;
        boolean exit = false;   //exit for our loop
        Node[][] Grid = new Node[3][4]; //The grid itself

        Node[][] checkForConvergeGrid = new Node[3][4]; //Used for checking if the values of a grid of changed

        boolean convereged = false; //If this is true the graph has converged and we set our epislon to 0
        boolean stopChecking = false; //Used to note that we should stop checking for convergence

        while (exit == false) {  //We want to keep going until the user types exit
            counter = 1;    //Reset All variables
            goalLocation = 0; 
            forbiddenLocation = 0;
            wallLocation = 0;
            convereged = false;
            stopChecking = false;
            policyOrQ = ' '; // Reset
            nodeQ = 0;  //used for q (#) value
            System.out.println("Input a position for the goal, forbidden area and wall as well if you\nwant the output for policy or optimal q values in the order\n# # # (p|q), type exit to quit");
            String s = reader.readLine();   //Reads the actual input line
            //s = s.replaceAll("\\s","");   //Used for testing
            //System.out.println(s);
            //b = s.toCharArray();
            String[] splitted = s.split("\\s+");    //Spits our input into an array of strings split up based on spaces
            //System.out.println(splitted[0]);
            if(splitted.length == 5){   //If we have a value in the 5th component we want to set it to nodeQ, which is used to find the Q values of a specific node
                nodeQ = Integer.valueOf(splitted[4]);    
            }
            if(s.compareTo("exit") == 0){   //If our user wants to exit we close our reader and break out of the loop
                exit = true;
                reader.close();
            }
            else if(splitted.length == 4 || (splitted.length == 5 && splitted[3].charAt(0)=='q' && nodeQ<=12 && nodeQ>0)){  //If we have the correct number of inputs
                for (int i = 2;i>=0;i-- ){  //We initilize the grid, we want to start at the bottom left and work our way to the top right
                    for (int j = 0;j<4;j++){
                    Grid[i][j] = new Node(counter);
                    checkForConvergeGrid[i][j] = new Node(counter);
                    counter ++; //For assigning the positions based on the assignment
                    }
                }
        
                
                goalLocation = Integer.valueOf(splitted[0]);    //First input value is the goal location
                forbiddenLocation = Integer.valueOf(splitted[1]);   //Second is the forbidden location
                wallLocation = Integer.valueOf(splitted[2]);    //third is the wall location
                policyOrQ = (splitted[3].charAt(0));      //4th is the policy
                if((goalLocation > 12 || goalLocation < 1) || (forbiddenLocation > 12 || forbiddenLocation < 1) || (wallLocation > 12 || wallLocation < 1) || (policyOrQ != 'p' && policyOrQ != 'q') || (splitted[3].length() != 1)){   //Checks to make sure the input is correct
                    System.out.println("Incorrect input, remember all values must be inbetween 1 and 12 and the letter\nmust be p or q");
                }
                else if(goalLocation == 1 || forbiddenLocation == 1 || wallLocation ==1){   //Checks to make sure nothing is overlapping with the start position
                    System.out.println("Cannot place anything on the starting position");
                }
                else if(goalLocation == forbiddenLocation || goalLocation == wallLocation || forbiddenLocation == wallLocation){    //Checks to make sure none of the inputs are overlapping
                    System.out.println("Cannot have any locations overlapping");
                }
                else{
                    for (int i = 0;i<3;i++ ){   //Goes through the grid and assigns symbols based on the position
                        for (int j = 0;j<4;j++){
                            if(Grid[i][j].getPosition() == 1){
                                Grid[i][j].setSymbol("[S]");
                                checkForConvergeGrid[i][j].setSymbol("[S]");
                            }
                            else if(Grid[i][j].getPosition() == goalLocation){
                                Grid[i][j].setSymbol("[O]");
                                checkForConvergeGrid[i][j].setSymbol("[O]");
                            }
                            else if(Grid[i][j].getPosition() == forbiddenLocation){
                                Grid[i][j].setSymbol("[Ø]");
                                checkForConvergeGrid[i][j].setSymbol("[Ø]");
                            }
                            else if(Grid[i][j].getPosition() == wallLocation){
                                Grid[i][j].setSymbol("[■]");
                                checkForConvergeGrid[i][j].setSymbol("[■]");
                            }
                            
                        }
                    }
                    // System.out.println("Graph AFTEr:");     //Testing
                     printGrid(Grid);
                    
                    // System.out.println("Graph After Values:");
                    // printMaxGrid(Grid);

                    //This is the main loop, goes through 10,000 times along with updating our Q Values
                    for(int x = 0;x<20000;x++){
                        for (int i = 0;i<3;i++ ){   //Goes through the grid and assigns symbols based on the position
                            for (int j = 0;j<4;j++){
                                if(Grid[i][j].getPosition() == 1){
                                    Grid[i][j].setAgentHere(true);
                                    currentPlayerLocation = 1;
                                    currentPlayeri = i;
                                    currentPlayerj = j;

                                    if(x % 500 == 0){    //Assigns the graph after 50 iteratons
                                        //System.out.println("New Checking Grid ");
                                        for (int k = 0; k<3;k++){
                                            for (int l =0; l<4;l++){
                                                for (int m =0; m<4;m++){
                                                    checkForConvergeGrid[k][l].setQValue(m,Grid[k][l].getQValue(m)) ;
                                                }
                                            }
                                        }
                                        //printMaxGrid(checkForConvergeGrid);
                                    }
                                    if(x % 999 == 0 && stopChecking == false && x!=0){
                                       // System.out.println("Comparing with Grid");
                                        //printMaxGrid(checkForConvergeGrid);
                                        convereged = true;
                                        for (int k = 0; k<3;k++){
                                            for (int l =0; l<4;l++){
                                                for (int m =0; m<4;m++){
                                                    //System.out.println("GRID Q:"+Grid[k][l].getQValue(m)+" checkForConvergeGrid Q:"+checkForConvergeGrid[k][l].getQValue(m));
                                                    if(Grid[k][l].getQValue(m)!=checkForConvergeGrid[k][l].getQValue(m)){
                                                        convereged = false;
                                                    }
                                                }
                                            }
                                        }
                                    }

                                }
                            }
                        }
                        if(convereged == true && stopChecking ==false){
                            //System.out.println("Converged after "+x);
                            //printMaxGrid(Grid);
                            stopChecking = true;
                        }
                        //System.out.println("Get Q exited with value:"+episode(Grid,currentPlayeri,currentPlayerj));
                        episode(Grid,currentPlayeri,currentPlayerj,convereged);
                    }
                    
                    //For testing
                    //printMaxGrid(Grid);
                    //printAllGrid(Grid);

                    counter = 1;
                    if(policyOrQ == 'p'){
                        System.out.println("Policy for each node:");
                        //If we were given a p earlier we give the optimal polict
                        for (int i = 2;i>=0;i-- ){   //Goes through the grid and assigns symbols based on the position
                            for (int j = 0;j<4;j++){
                                if(Grid[i][j].getPosition() == goalLocation){
                                    Grid[i][j].setSymbol("[O]");
                                    checkForConvergeGrid[i][j].setSymbol("[O]");
                                }
                                else if(Grid[i][j].getPosition() == forbiddenLocation){
                                    Grid[i][j].setSymbol("[Ø]");
                                    checkForConvergeGrid[i][j].setSymbol("[Ø]");
                                }
                                else if(Grid[i][j].getPosition() == wallLocation){
                                    Grid[i][j].setSymbol("[■]");
                                    checkForConvergeGrid[i][j].setSymbol("[■]");
                                }
                                else{
                                    if(Grid[i][j].getMaxDirection() == 0){
                                        System.out.println(counter+" "+"↑");
                                    }
                                    else if(Grid[i][j].getMaxDirection() == 2){
                                        System.out.println(counter+" "+"→");
                                    }
                                    else if(Grid[i][j].getMaxDirection() == 3){
                                        System.out.println(counter+" "+"←");
                                    }
                                    else if(Grid[i][j].getMaxDirection() == 1){
                                        System.out.println(counter+" "+"↓");
                                    }
                                    
                                }
                                counter ++;
                            }
                        
                        }
                        System.out.println("Optimal Policy:");
                        optimalPolicy(Grid,currentPlayeri,currentPlayerj);
                    }

                

                    else if(policyOrQ == 'q'){
                        //If we were given a q we find the q value of that position
                        //System.out.println("Node Q:"+nodeQ);
                        System.out.println("Q Values for Node "+nodeQ);
                        for (int i = 0;i<3;i++ ){   //Goes through the grid 
                            for (int j = 0;j<4;j++){
                                if(Grid[i][j].getPosition() == nodeQ){
                                    Grid[i][j].printValues();
                                }
                            }
                        }
                    }
                    

                }
            }
            else{
                System.out.println("Wrong input length try agian"); //Reloops and gets another input
            }
        }
    }

    

    public static double episode(Node[][] Grid,int posi,int posj,boolean converged) throws IOException{    
        //Sets up random variable
        Random rand = new Random();

        //System.out.println("In getQ");
        boolean exit = false;
        //int direction = -1;
        int currentPlayeri = posi;
        int currentPlayerj = posj;

        double reward = 0;

        //printGrid(Grid);

        //0 up, 1 Down, 2 Right, 3 Left
        while(exit == false){//Loop until we find an exit
            int Direction = 0;
            int eps = rand.nextInt(10); //Used to implement e greedy 
            if(eps != 9 || converged == true){  //If we have converged we no longer care about randomness
                Direction = Grid[currentPlayeri][currentPlayerj].getMaxDirection();
                //System.out.println("Best Direction:"+Direction);
            }
            else{   //1/10 = .1
                //System.out.println("RANDOM!:"+eps);
                Direction = rand.nextInt(4);
            }
            
            String s = reader.readLine();   //Reads the actual input line
            Direction = Integer.valueOf(s);
            //int randDirection = UP;
            //Exits after it moves to the state to account for the score

            if(Grid[currentPlayeri][currentPlayerj].getSymbol().equals("[O]")){ //If we are currently in a goal state state
                //System.out.println("EXIT Goal");
                reward = reward + 100;  //We get a reward of 100
                exit = true; //And exit
                Grid[currentPlayeri][currentPlayerj].setQValue(UP,getNewQ(Grid,currentPlayeri,currentPlayerj,UP,100.0,null));   //We update the q value of the current state and action, NOTE* I ASSIGNED 0 as the exit action, this is accounted for in the node class
            }
            else if(Grid[currentPlayeri][currentPlayerj].getSymbol().equals("[Ø]")){    //If we are in the terminal state
                //System.out.println("EXIT Forbidden Zone");
                reward = reward - 100;  //We get a reward of -100
                exit = true;    //And want to exit
                Grid[currentPlayeri][currentPlayerj].setQValue(UP,getNewQ(Grid,currentPlayeri,currentPlayerj,UP,-100.0,null)); //We update the q value of the current state and action, NOTE* I ASSIGNED 0 as the exit action, this is accounted for in the node class
            }
            //If nore of the above are true we still have more to move
            else if(Direction == UP){  //Moving up
                //System.out.println("\nUP");
                reward = reward + livingPenalty;    //Add the living penalty to our total reward (Again mainly for testing)
                if (currentPlayeri > 0){    //Movement will be inbounds
                    if(Grid[currentPlayeri-1][currentPlayerj].getSymbol().equals("[■]")){   //If we hit a wall
                        //System.out.println("Hit Wall");
                        Grid[currentPlayeri][currentPlayerj].setQValue(UP,getNewQ(Grid,currentPlayeri,currentPlayerj,UP,livingPenalty,Grid[currentPlayeri][currentPlayerj]));   //Update Q Values
                    }
                    else{   //Else we can freely move up
                        Grid[currentPlayeri][currentPlayerj].setQValue(UP,getNewQ(Grid,currentPlayeri,currentPlayerj,UP,livingPenalty,Grid[currentPlayeri-1][currentPlayerj])); //Update Q Values
                        Grid[currentPlayeri][currentPlayerj].setAgentHere(false);   //Let the grid know that the agent is no longer in this spot
                        currentPlayeri = currentPlayeri -1; //Move the i down one
                        Grid[currentPlayeri][currentPlayerj].setAgentHere(true);    //Let the grid know that the agent is no in this spot
                    }
                }
                else{   //Treat out of bounds movement as hitting a wall and update Q Values
                    Grid[currentPlayeri][currentPlayerj].setQValue(UP,getNewQ(Grid,currentPlayeri,currentPlayerj,UP,livingPenalty,Grid[currentPlayeri][currentPlayerj]));
                }
            }
            else if(Direction == DOWN){ //Reffer to notation for Direction UP, all the same except they have different i and j additions 
                //System.out.println("\nDOWN");
                reward = reward + livingPenalty;
                if (currentPlayeri < 2){
                    if(Grid[currentPlayeri+1][currentPlayerj].getSymbol().equals("[■]")){
                        //System.out.println("Hit Wall");
                        Grid[currentPlayeri][currentPlayerj].setQValue(DOWN,getNewQ(Grid,currentPlayeri,currentPlayerj,DOWN,livingPenalty,Grid[currentPlayeri][currentPlayerj]));
                    }
                    else{
                        Grid[currentPlayeri][currentPlayerj].setQValue(DOWN,getNewQ(Grid,currentPlayeri,currentPlayerj,DOWN,livingPenalty,Grid[currentPlayeri+1][currentPlayerj]));
                        Grid[currentPlayeri][currentPlayerj].setAgentHere(false);
                        currentPlayeri = currentPlayeri +1;
                        Grid[currentPlayeri][currentPlayerj].setAgentHere(true);
                    }
                }
                else{
                    Grid[currentPlayeri][currentPlayerj].setQValue(DOWN,getNewQ(Grid,currentPlayeri,currentPlayerj,DOWN,livingPenalty,Grid[currentPlayeri][currentPlayerj]));
                }
            }
            else if(Direction == RIGHT){//Reffer to notation for Direction UP, all the same except they have different i and j additions 
                //System.out.println("\nRIGHT");
                reward = reward + livingPenalty;
                if (currentPlayerj < 3){
                    if(Grid[currentPlayeri][currentPlayerj + 1].getSymbol().equals("[■]")){
                        //System.out.println("Hit Wall");
                        Grid[currentPlayeri][currentPlayerj].setQValue(RIGHT,getNewQ(Grid,currentPlayeri,currentPlayerj,RIGHT,livingPenalty,Grid[currentPlayeri][currentPlayerj]));
                    }
                    else{
                        Grid[currentPlayeri][currentPlayerj].setQValue(RIGHT,getNewQ(Grid,currentPlayeri,currentPlayerj,RIGHT,livingPenalty,Grid[currentPlayeri][currentPlayerj+1]));
                        Grid[currentPlayeri][currentPlayerj].setAgentHere(false);
                        currentPlayerj = currentPlayerj +1;
                        Grid[currentPlayeri][currentPlayerj].setAgentHere(true);
                    }
                }
                else{
                    Grid[currentPlayeri][currentPlayerj].setQValue(RIGHT,getNewQ(Grid,currentPlayeri,currentPlayerj,RIGHT,livingPenalty,Grid[currentPlayeri][currentPlayerj]));
                }
            }
            else if(Direction == LEFT){//Reffer to notation for Direction UP, all the same except they have different i and j additions 
                //System.out.println("\nLEFT");
                reward = reward + livingPenalty;
                if (currentPlayerj > 0){
                    if(Grid[currentPlayeri][currentPlayerj - 1].getSymbol().equals("[■]")){
                        //System.out.println("Hit Wall");
                        Grid[currentPlayeri][currentPlayerj].setQValue(LEFT,getNewQ(Grid,currentPlayeri,currentPlayerj,LEFT,livingPenalty,Grid[currentPlayeri][currentPlayerj]));
                    }
                    else{
                        Grid[currentPlayeri][currentPlayerj].setQValue(LEFT,getNewQ(Grid,currentPlayeri,currentPlayerj,LEFT,livingPenalty,Grid[currentPlayeri][currentPlayerj-1]));
                        Grid[currentPlayeri][currentPlayerj].setAgentHere(false);
                        currentPlayerj = currentPlayerj -1;
                        Grid[currentPlayeri][currentPlayerj].setAgentHere(true);
                    }
                }
                else{
                    Grid[currentPlayeri][currentPlayerj].setQValue(LEFT,getNewQ(Grid,currentPlayeri,currentPlayerj,LEFT,livingPenalty,Grid[currentPlayeri][currentPlayerj]));
                }
            }
            printGrid(Grid);
        }
        //After we exit we want to set our agent to not be anywhere because it has exited
        Grid[currentPlayeri][currentPlayerj].setAgentHere(false);
        //Returns the reward recieved, not nessasary
        return reward;
    }

    public static double getNewQ(Node[][] Grid,int posi,int posj,int nextMove,double reward,Node nextState){
        boolean negative = false;   //Used because rounding a negative number was giving some issue
        double newQ=0;
        //System.out.println("\nQValue of next Move:"+Grid[posi][posj].getQValue(nextMove));
        newQ = (1-learningRate)*(Grid[posi][posj].getQValue(nextMove)); //We do the equation step by step for testing 
        //System.out.println("newQ:"+newQ);

        if(nextState != null){  //If there was no next state we want to avoid calling it and getting its max value
            newQ = newQ + learningRate*(reward+(discountRate)*(nextState.maxValue()));
          //  System.out.println("Next State Max:"+nextState.maxValue());
        }
        else{
            newQ = newQ + learningRate*(reward);
        }
        //If the q value is negative we want to note that
        if(newQ < 0){
            negative = true;
        }
        newQ = Math.abs(newQ);  //Absolute value newQ (if this was not done it had a rounding error)
        newQ = Math.round(newQ * 100.00)/100.00;//Round to the nearest hundreth
        if(negative == true){
            newQ = newQ * -1;   //If it was negative we make it negative again
        }
        //System.out.println("NEW Q:"+newQ);
        return newQ;
    }

    public static void printGrid(Node[][] Grid){    ///Function goes through the grid and prints our the sybmol at it
        for (int i = 0;i<3;i++ ){
            for (int j = 0;j<4;j++){
                System.out.print(Grid[i][j]);
            }
            System.out.println();
        }
    }

    public static void printAllGrid(Node[][] Grid){
        for (int i = 0;i<3;i++ ){
            for (int j = 0;j<4;j++){
                Grid[i][j].setPrintNums(true);  //Before we print we let the grid know we want to print all the numbers (reffer to Node toStirng)
                System.out.print(Grid[i][j]);
                Grid[i][j].setPrintNums(false); //Turn that Off
            }
            System.out.println();
        }
    }

    public static void printMaxGrid(Node[][] Grid){
        for (int i = 0;i<3;i++ ){
            for (int j = 0;j<4;j++){
                Grid[i][j].setPrintNums(true);  //Let the Grid know we want to print out numbers
                Grid[i][j].setJustMax(true);    //Also let the grid know we want to only print out the maxium number
                System.out.print(Grid[i][j]);
                Grid[i][j].setPrintNums(false); //turn off
                Grid[i][j].setJustMax(false); //turn off
            }
            System.out.println();
        }
    }
    
    public static void optimalPolicy(Node[][] Grid,int posi,int posj) throws IOException{    
        //For printing out the optimal policy
        //This pretty much is the same as an episode except it does not use e greedy or update q values, it just follows the max path
        boolean exit = false;
        int count = 0;  //The movement number
        int currentPlayeri = posi;
        int currentPlayerj = posj;

        double reward = 0;

        //0 up, 1 Down, 2 Right, 3 Left
        while(exit == false){
            int Direction = 0;
            Direction = Grid[currentPlayeri][currentPlayerj].getMaxDirection(); //Gets the best direction to go
            
            if(Grid[currentPlayeri][currentPlayerj].getSymbol().equals("[O]")){ //We are in an exit state
                exit = true;
            }
            else if(Grid[currentPlayeri][currentPlayerj].getSymbol().equals("[Ø]")){    //We are in an exit state
                exit = true;
            }
            else if(Direction == UP){   //We move up
                System.out.println(count+" ↑"); //Print the symbol for up
                if (currentPlayeri > 0){
                    if(Grid[currentPlayeri-1][currentPlayerj].getSymbol().equals("[■]")){   //We hit a wall
                    }
                    else{
                        Grid[currentPlayeri][currentPlayerj].setAgentHere(false);
                        currentPlayeri = currentPlayeri -1;
                        Grid[currentPlayeri][currentPlayerj].setAgentHere(true);
                    }
                }
                else{
                }
            }
            else if(Direction == DOWN){ //We move Down
                System.out.println(count+" ↓"); //Print the symbol for Down
                if (currentPlayeri < 2){
                    if(Grid[currentPlayeri+1][currentPlayerj].getSymbol().equals("[■]")){
                    }
                    else{
                        Grid[currentPlayeri][currentPlayerj].setAgentHere(false);
                        currentPlayeri = currentPlayeri +1;
                        Grid[currentPlayeri][currentPlayerj].setAgentHere(true);
                    }
                }
                else{
                 }
            }
            else if(Direction == RIGHT){    //WE move right
                System.out.println(count+" →"); //Print the symbol for right
                if (currentPlayerj < 3){
                    if(Grid[currentPlayeri][currentPlayerj + 1].getSymbol().equals("[■]")){
                    }
                    else{
                        Grid[currentPlayeri][currentPlayerj].setAgentHere(false);
                        currentPlayerj = currentPlayerj +1;
                        Grid[currentPlayeri][currentPlayerj].setAgentHere(true);
                    }
                }
                else{
                     }
            }
            else if(Direction == LEFT){ //We move left
                System.out.println(count+" ←"); //Print the symbol for left
                if (currentPlayerj > 0){
                    if(Grid[currentPlayeri][currentPlayerj - 1].getSymbol().equals("[■]")){
                    }
                    else{
                        Grid[currentPlayeri][currentPlayerj].setAgentHere(false);
                        currentPlayerj = currentPlayerj -1;
                        Grid[currentPlayeri][currentPlayerj].setAgentHere(true);
                    }
                }
                else{
                
                }
            }
            count++;    //Increment the movement number
        }
        Grid[currentPlayeri][currentPlayerj].setAgentHere(false);   //Makes the agent exit
    }
    
}