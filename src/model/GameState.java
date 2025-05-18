package model;

import java.util.List;
import java.util.Objects;
import model.core.Board;
import model.core.Move;

public class GameState {
    private final int cost;
     private Board board;
     private List<Move> moves; 

     // Constructor
    public GameState() {
        this.cost = 0;
        this.board = null;
        this.moves = null;
    }

    public GameState(int cost, Board board, List<Move> moves) {
        this.cost = cost;
        this.board = board;
        this.moves = moves;
    }

     // Methods
    public List<GameState> getSuccessors() {
        return null;
    }

    public Boolean isGoalState() {
        return false;
    }

    public List<Move> getMoves() {
        return this.moves;
    }

    public Board getBoard() {
        return this.board;
    }

    public int getCost() {
        return this.cost;
    }

    @Override
    public boolean equals(Object o) {
        return this.board.equals(((GameState) o).getBoard()) && this.moves.equals(((GameState) o).getMoves());
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.board, this.moves);
    }
 }