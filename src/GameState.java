import java.util.List;

public class GameState {
    private final int cost;
    //  private Board board;
    //  private List<Move> moves; 

     // Constructor
    public GameState() {
        this.cost = 0;
    }

    public GameState(int cost) { // Board board, List<Move> moves
        this.cost = cost;
    }

     // Methods
    public List<GameState> getSuccessors() {
        return null;
    }    

    public Boolean isGoalState() {
        return false;
    }

    // public public List<Move> getMoves() {
    //     return this.moves;
    // }

    // public Board getBoard() {
    //     return this.board;
    // }

    public int getCost() {
        return this.cost;
    }

    @Override
    public boolean equals(Object o) {
        return true;
    }

    @Override
    public int hashCode() {
        return -1;
    }
 }