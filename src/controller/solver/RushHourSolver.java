package controller.solver;

import controller.SolutionResult;
import model.GameState;

public abstract class RushHourSolver {
    protected GameState initialState;
    protected int visitedNodesCount;
    protected long executionTime;

    public RushHourSolver(GameState initialState) {
        this.initialState = initialState;
        this.visitedNodesCount = 0;
        this.executionTime = 0;
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
