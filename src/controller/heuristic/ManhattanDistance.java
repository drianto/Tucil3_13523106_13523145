package controller.heuristic;

import java.util.ArrayList;

import model.GameState;
import model.core.*;

public class ManhattanDistance implements Heuristic{
     // Fields
    
     // Constructor
    public ManhattanDistance() { }

    // Methods
    @Override
    public int calculate(GameState gameState){
        Board board = gameState.getBoard();
        ArrayList<Position> pPos = new ArrayList<>();

        for (Piece piece : gameState.getBoard().getPieces()) {
                if (piece.getId() == 'P') {
                pPos.addAll(piece.getPositions()); 
            }
        }

        if (pPos.isEmpty()) return 0; 

        int result = Integer.MAX_VALUE;
        for (Position p : pPos) {
            int distance = Math.abs(p.getX() - board.getExitPosition().getX()) + Math.abs(p.getY() - board.getExitPosition().getY());
            result = Math.min(result, distance);
        }
        return result;
    }
    
    @Override 
    public String getName() {
        return "Manhattan Distance Heuristic";
    }
}