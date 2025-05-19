package controller.solver;

import controller.SolutionResult;
import controller.heuristic.Heuristic;
import model.GameState;

public class GreedyBestFirstSearchSolver extends RushHourSolver{
    private final Heuristic heuristic;

    public GreedyBestFirstSearchSolver(GameState initialState, Heuristic heuristic) {
        super(initialState);
        this.heuristic = heuristic;
    }

    @Override
    public SolutionResult solve() {
        return null;
    }

    @Override
    public String getName() {
        return "Greedy Best First Search Solver";
    }
}
