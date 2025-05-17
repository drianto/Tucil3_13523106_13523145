package controller.heuristic;

import model.GameState;

public interface Heuristic {
    public void calculate(GameState gameState);
}