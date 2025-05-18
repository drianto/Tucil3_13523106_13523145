package controller.heuristic;

import model.GameState;

public class ManhattanDistance implements Heuristic{
     // Fields
    
     // Constructor
    public ManhattanDistance() {
     
    }

    // Methods
    @Override
    public int calculate(GameState gameState){
        return 0;
    }
    
    @Override 
    public String getName() {
        return "Manhattan Distance Heuristic";
    }
}