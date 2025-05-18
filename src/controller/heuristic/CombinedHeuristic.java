package controller.heuristic;

import java.util.ArrayList;
import model.GameState;

public class CombinedHeuristic implements Heuristic {
     // Fields
    private ArrayList<Heuristic> heuristics = new ArrayList<>();
    private ArrayList<Double> weights = new ArrayList<>();

     // Constructor
    public CombinedHeuristic() {
        
    }

     // Methods
    @Override
    public int calculate(GameState gameState) {
        return 0;
    }
    
    @Override
    public String getName() {
        return "Combined Heuristic";
    }
}