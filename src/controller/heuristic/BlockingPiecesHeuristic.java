package controller.heuristic;

import model.GameState;

public class BlockingPiecesHeuristic implements Heuristic {
     // Fields

     // Constructor
    public BlockingPiecesHeuristic () {
        
    }

     // Methods
    @Override
    public int calculate(GameState gameState) {
        return 0;
    }

    @Override
    public String getName() {
        return "Blocking Pieces Heuristic";
    }
    
}