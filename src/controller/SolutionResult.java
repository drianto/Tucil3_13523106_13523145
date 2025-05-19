package controller;

import java.util.ArrayList;
import java.util.List;
import model.core.Board;
import model.core.Move;

public class SolutionResult {
    private final boolean solved;
    private final ArrayList<Move> moves;
    private final int visitedNodesCount;
    private final long executionTime;
    private final ArrayList<Board> boardStates;

    public SolutionResult(boolean solved, List<Move> moves, int visitedNodesCount, long executionTime, List<Board> boardStates) {
        this.solved = solved;
        this.moves = new ArrayList<>(moves);
        this.visitedNodesCount = visitedNodesCount;
        this.executionTime = executionTime;
        this.boardStates = new ArrayList<>(boardStates);
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
