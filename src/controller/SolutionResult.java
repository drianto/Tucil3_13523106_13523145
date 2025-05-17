package controller;

import model.core.Board;
import model.core.Move;

import java.util.ArrayList;
import java.util.List;

public class SolutionResult {
    private boolean solved;
    private ArrayList<Move> moves;
    private int visitedNodesCount;
    private long executionTime;
    private ArrayList<Board> boardStates;

    public SolutionResult(boolean solved, List<Move> moves, int visitedNodesCount, long executionTime, List<Board> boardStates) {

    }

    public boolean isSolved() {
        return true;
    }
    
    public ArrayList<Move> getMoves() {
        return null;
    }

    public int getVisitedNodesCount() {
        return 1;
    }

    public long getExecutionTime() {
        return 1L;
    }

    public List<Board> getBoardStates() {
        return null;
    }
}
