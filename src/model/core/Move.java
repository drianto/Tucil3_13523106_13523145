package model.core;

public class Move {
     // Fields
     private final char pieceId;
     private final Direction direction;
     private final int steps;

     // Constructor
    public Move(char pieceId, Direction direction, int steps) {
        if (steps < 1) {
            throw new IllegalArgumentException("Jumlah langkah (steps) harus minimal 1.");
        }
        this.pieceId = pieceId;
        this.direction = direction;
        this.steps = steps;
    }

     // Methods
    public char getPieceId() {
        return this.pieceId;
    }

    public Direction getDirection() {
        return this.direction;
    }

    public int getSteps() {
        return this.steps;
    }

    @Override
    public String toString() {
        return "Move{" +
               "pieceId=" + pieceId +
               ", direction=" + direction +
               ", steps=" + steps +
               '}';
    }   
}
