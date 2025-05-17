package controller.solver;

import controller.heuristic.Heuristic;
import model.GameState;

public class GreedyBestFirstSearchSolver extends RushHourSolver{
    private Heuristic heuristic;

    public GreedyBestFirstSearchSolver(GameState initialState, Heuristic heuristic) {
        super(initialState);
        this.heuristic = heuristic;
    }

    @Override
    public SolutionResult solve() {
        
    }

    @Override
    public String getName() {
        
    }
}
