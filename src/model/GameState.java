package model;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import model.core.Board;
import model.core.Move;

// GameState dibuat Comparable untuk digunakan dalam PriorityQueue pada UCS
public class GameState implements Comparable<GameState> { // Ditambahkan Comparable
    private final int cost;
    private final Board board;
    private final List<Move> moves;
    private transient int hCost; // Nilai heuristik (sebaiknya transient)
    private transient int fCost; // fCost = gCost + hCost (sebaiknya transient)

    // Constructor
    public GameState() {
        this.cost = 0;
        this.board = null;
        this.moves = new ArrayList<>();
        this.hCost = Integer.MAX_VALUE;
        this.fCost = Integer.MAX_VALUE;
    }

    public GameState(int cost, Board board, List<Move> moves) {
        this.cost = cost;
        this.board = board;
        this.moves = moves;
        this.hCost = Integer.MAX_VALUE;
        this.fCost = Integer.MAX_VALUE;
    }

    // Methods
    public List<GameState> getSuccessors() {
        List<GameState> successors = new ArrayList<>();
        if (this.board == null) {
            System.err.println("Peringatan: Mencoba mendapatkan successor dari GameState dengan board null.");
            return successors;
        }

        List<Move> validMoves = this.board.getValidMoves();

        for (Move move : validMoves) {
            Board newBoard = this.board.clone(); 

            boolean moveApplied = newBoard.movePiece(move.getPieceId(), move.getDirection());

            if (moveApplied) {
                List<Move> newMovesList = new ArrayList<>(this.moves);
                newMovesList.add(move);

                successors.add(new GameState(this.cost + 1, newBoard, newMovesList));
            } else {
                System.err.println("Peringatan: Move yang dianggap valid (" + move.getPieceId() + " ke " + move.getDirection() + ") gagal diterapkan pada board kloningan.");
            }
        }
        return successors;
    }

    public Boolean isGoalState() {
        if (this.board == null) {
            return false;
        }
        // Memanfaatkan metode yang sudah ada di Board.java
        return this.board.isPrimaryPieceAtExit();
    }

    public void setHeuristicCosts(int hCost) {
        this.hCost = hCost;
        if (this.cost == Integer.MAX_VALUE || hCost == Integer.MAX_VALUE) {
            this.fCost = Integer.MAX_VALUE;
        } else {
            this.fCost = this.cost + hCost; // this.cost adalah gCost
        }
    }

    public List<Move> getMoves() {
        return new ArrayList<>(this.moves);
    }

    public Board getBoard() {
        return this.board;
    }

    public int getCost() {
        return this.cost;
    }

    public int gethCost() {
        return this.hCost;
    }

    public int getfCost() {
        return this.fCost;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        GameState gameState = (GameState) o;
        return cost == gameState.cost &&
               Objects.equals(board, gameState.board) && 
               Objects.equals(moves, gameState.moves); 
    }

    @Override
    public int hashCode() {
        return Objects.hash(cost, board, moves);
    }

    @Override
    public int compareTo(GameState other) {
        int fCompare = Integer.compare(this.fCost, other.fCost);
        if (fCompare == 0) {
            return Integer.compare(this.cost, other.cost);
        }
        return fCompare;
    }
}