package controller.solver;

import controller.heuristic.Heuristic;
import model.GameState;

public class BeamSearchSolver extends RushHourSolver {
    private Heuristic heuristic;
    private int beamWidth;

    public BeamSearchSolver(GameState initialState, Heuristic heuristic) {
        super(initialState);
        this.heuristic = heuristic;
        this.beamWidth = 10; // Default beam width
    }

    public SolutionResult solve() {
        
    }

    public String getName() {
        
    }
}
