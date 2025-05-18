package controller;

import java.util.ArrayList;
import java.util.List;
import model.core.Board;
import model.core.Move;

public class SolutionResult {
    private boolean solved;
    private ArrayList<Move> moves;
    private int visitedNodesCount;
    private long executionTime;
    private ArrayList<Board> boardStates;

    public SolutionResult(boolean solved, List<Move> moves, int visitedNodesCount, long executionTime, List<Board> boardStates) {

    }

    public boolean isSolved() {
        return solved;
    }
    
    public ArrayList<Move> getMoves() {
        return this.moves;
    }

    public int getVisitedNodesCount() {
        return this.visitedNodesCount;
    }

    public long getExecutionTime() {
        return this.executionTime;
    }

    public List<Board> getBoardStates() {
        return this.boardStates;
    }
}
