package controller.heuristic;

import model.GameState;

public interface Heuristic {
    public int calculate(GameState gameState);
    public String getName();
}