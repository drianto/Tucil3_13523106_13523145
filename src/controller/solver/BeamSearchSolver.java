package controller.solver;

import controller.SolutionResult;
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
        return null; 
    }

    public String getName() {
        return null;
    }
}
