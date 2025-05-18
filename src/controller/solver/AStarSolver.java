package controller.solver;

import controller.SolutionResult;
import controller.heuristic.Heuristic;
import model.GameState;

public class AStarSolver extends RushHourSolver {
    private Heuristic heuristic;

    public AStarSolver(GameState initialState, Heuristic heuristic) {
        super(initialState);
        this.heuristic = heuristic;
    }

    public SolutionResult solve() {
        return null;
    }

    public String getName() {
        return "A* Solver with " + heuristic.getName();
    }
}
