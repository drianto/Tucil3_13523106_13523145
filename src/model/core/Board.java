package model.core;

import java.util.ArrayList;
import java.util.List;

public class Board {
    private int width;
    private int height;
    private ArrayList<Piece> pieces;
    private Position exitPosition;
    private Piece primaryPiece;

    public Board(int width, int height, ArrayList<Piece> pieces, Position exitPosition) {
        this.width = width;
        this.height = height;
        
        // Create deep copies of all pieces to prevent external modification
        this.pieces = new ArrayList<>();
        for (Piece p : pieces) {
            Piece pieceCopy = p.clone();
            this.pieces.add(pieceCopy);
            
            // Set the primary piece reference
            if (pieceCopy.isPrimary()) {
                this.primaryPiece = pieceCopy;
            }
        }
        
        // Create a copy of the exit position
        this.exitPosition = new Position(exitPosition.getX(), exitPosition.getY());
    }

    public boolean movePiece(Piece piece, Direction direction) {
        if (piece == null) return false;
        if (!piece.canMove(direction, this)) return false;

        piece.move(direction, this);
        return true;
    }

    public boolean isOccupied(Position position) {
        for(int i = 0; i < pieces.size(); i++) {
            for(int j = 0; j < pieces.get(i).getPositions().size(); j++) {
                if (pieces.get(i).getPositions().get(j).getX() == position.getX() && 
                    pieces.get(i).getPositions().get(j).getY() == position.getY()) {
                    return true;
                }
            }
        }
        return false;
    }   

    public boolean isPrimaryPieceAtExit() {
        if (primaryPiece == null) return false;
        for (Position p : primaryPiece.getPositions()) {
            if (p.equals(exitPosition)) {
                return true;
            }
        }
        return false;
    }

    public ArrayList<Move> getValidMoves() {
        ArrayList<Move> moves = new ArrayList<>();
        for (Piece p : pieces) {
            for (Direction d : Direction.values()) {
                if (p.canMove(d, this)) {
                    moves.add(new Move(p.getId(), d));
                }
            }
        }
        return moves;
    }

    public Board clone() {
        ArrayList<Piece> piecesCopy = new ArrayList<>();
        for (Piece p : this.pieces) {
            piecesCopy.add(p.clone());
        }
        
        Position exitPositionCopy = new Position(this.exitPosition.getX(), this.exitPosition.getY());
        
        return new Board(this.width, this.height, piecesCopy, exitPositionCopy);
    }

    public List<Piece> getPieces() {
        return this.pieces;
    }

    public Piece getPrimaryPiece() {
        return this.primaryPiece;
    }
    
    public Piece getPieceById(char id) {
        for (int i = 0; i < pieces.size(); i++) {
            if (pieces.get(i).getId() == id) {
                return pieces.get(i);
            } 
        }
        return null;
    }

    public int getWidth() {
        return this.width;
    }

    public int getHeight() {
        return this.height;
    }

    public Position getExitPosition() {
        return this.exitPosition;
    }
}