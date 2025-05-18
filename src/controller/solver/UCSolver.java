package controller.solver;

import controller.SolutionResult;
import model.GameState;

public class UCSolver extends RushHourSolver {
    public UCSolver(GameState initialState) {
        super(initialState);
    }

    @Override
    public SolutionResult solve() {
        return null;
    }

    @Override
    public String getName() {
        return "Uniform Cost Search Solver";
    }
}