package model.core;

public class Move {
     // Fields
     private final char pieceId;
     private final Direction direction;

     // Constructor
    public Move(char pieceId, Direction direction) {
        this.pieceId = pieceId;
        this.direction = direction;
    }

     // Methods
    public char getPieceId() {
        return this.pieceId;
    }

    public Direction getDirection() {
        return this.direction;
    }
}