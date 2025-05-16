import java.util.ArrayList;

public class CombinedHeuristic implements Heuristic {
     // Fields
    private ArrayList<Heuristic> heuristics = new ArrayList<>();
    private ArrayList<Double> weights = new ArrayList<>();

     // Constructor
    public CombinedHeuristic() {
        
    }

     // Methods
    @Override
    public void calculate(GameState gameState) {
    
    }
    
}