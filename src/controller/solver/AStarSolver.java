package controller.solver;

import controller.heuristic.Heuristic;
import model.GameState;

public class AStarSolver extends RushHourSolver {
    private Heuristic heuristic;

    public AStarSolver(GameState initialState, Heuristic heuristic) {
        super(initialState);
        this.heuristic = heuristic;
    }

    public SolutionResult solve() {
        
    }

    public String getName() {
        
    }
}
