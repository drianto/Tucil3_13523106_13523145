package controller.solver;

import model.GameState;
import controller.SolutionResult;

public abstract class RushHourSolver {
    protected GameState initialState;
    protected int visitedNodesCount;
    protected long executionTime;

    public RushHourSolver(GameState initialState) {
        this.initialState = initialState;
    }

    public int getVisitedNodesCount() {
        return this.visitedNodesCount;
    }

    public long getExecutionTime() {
        return this.executionTime;
    }

    public abstract SolutionResult solve();
    public abstract String getName();

}
