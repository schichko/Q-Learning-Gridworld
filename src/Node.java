import java.util.*;

public class Node{
    String symbol;  //The Symbol that can be placed in the node, used for description of where the agent is, if its a wall, goal or forbidden
    double[] values = {0.00,0.00,0.00,0.00}; // 0 up, 1 Down, 2 Right, 3 Left
    int position = 0;   //The position of the node according to the homework description (bottom left = 1 top right =12)
    int maxDirection = 0;   //the max direction 
    boolean printNums = false;  //Set true if we just want to print out the numbers for each state (for all directions)
    boolean agentHere = false;  //Set true if the agent is in this space
    boolean justMax = false;    //Set true if we only want to print the max value of the node
    public Node(int position){
        this.position = position;   //Poistion in the actual graph, 1 is bottom left
        symbol = "[ ]"; //Set the symbol to be the default for empty
    }

    public double maxValue(){   //Finds the max value of our node
        double max = values[0]; //Initializes as up (so our learning algorithm tie breaks with up)
        this.maxDirection = 0;  
        int count = 0;
        if(this.symbol.equals("[O]") || this.symbol.equals("[Ø]")){ //We store the value of exit states in 0 so we just return 0
            return values[0];
        }
        else{
            for(double i : values){ //For all of the values
                if (i > max){   //We check if its bigger than our current max and update maxDirection
                    max = i;
                    this.maxDirection = count;
                }
                count ++;
            }
            return max; 
        }
    }
    

    public void setQValue(int direction, double newQ){  //Sets the q value of a current direction based on a value we are given
        values[direction] = newQ;
    }

    public double getQValue(int direction){ //Gets the Q value of a certain direction
        return values[direction];
    }

    public int getMaxDirection(){   //Returns the max direction for finding the optimal way to go
        if(this.symbol.equals("[O]") || this.symbol.equals("[Ø]")){
            return 0;
        }
        else{
            maxValue(); //Calls maxValue to update maxDirection
            return this.maxDirection;
        }
    }

    public void setSymbol(String newSymbol){
        this.symbol = newSymbol;    //Sets the Symbol of a node
    }

    public void setPrintNums(boolean print){    //Sets if we want to print out the numbers or not
        this.printNums = print;
    }

    public void setJustMax(boolean max){    //Sets if we only want to print out the max values of a node
        this.justMax = max;
    }

    public void setAgentHere(boolean agent){    //Sets if the agent is currently in this node
        this.agentHere = agent;
    }

    public String getSymbol(){
        return this.symbol; //Returns the Symbol of this current Node
    }

    public int getPosition(){
        return this.position;   //Returns the position of this current Node
    }

    public void printValues(){  //For printing the values of a node, used for testing purposes as well as when we are given a Q
        int count = 0;
        if(this.symbol.equals("[O]") || this.symbol.equals("[Ø]")){ //If the node we are given is a exit state we return that
            System.out.println("Exit Value Converged to "+values[0]);
        }
        else{
            for(double i : values){ //Otherwise we go through all of our values and print the value with its corresponding direction
                switch(count){
                    case 0:
                    System.out.println("↑ "+values[0]);
                    break;
                    case 1:
                    System.out.println("→ "+values[2]);
                    break;
                    case 2:
                    System.out.println("← "+values[3]);
                    break;
                    case 3:
                    System.out.println("↓ "+values[1]);
                    break;
                }
                count ++;
            }
        }
    }

    public String toString(){   //Override toString so we can print what we want
        if(agentHere == true && printNums == false){
            return("[X]");  //If the agent is here and we dont care about printing numbers we print the symbol for agent
        }
        else if(printNums == false){
            return (symbol);    //Otherwise we print the symbol
        }
        else if(justMax == true){
            return("["+maxValue()+"]"); //If we want just max, we print out the max value
        }
        else{
            return ("["+values[0]+":"+values[1]+":"+values[2]+":"+values[3]+"]");   //Otherwise we print out all of the values 0-up,1-down,2-right,3-left
        }
    }
}