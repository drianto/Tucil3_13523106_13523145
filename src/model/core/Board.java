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
        this.pieces = pieces;
        this.exitPosition = exitPosition;
    }

    // public boolean movePiece(Piece piece, Direction direction) {
        
    // }

    // public boolean isOccupied(Position position) {

    // }

    // public boolean isPrimaryPieceAtExit() {
    //     List<Position> tempPositions = primaryPiece.getPositions();
    //     for (int i = 0; i < tempPositions.size(); i++) {
    //         if ()
    //     }
    //     return false;
    // }

    // public ArrayList<Move> getValidMoves() {
        
    // }

    public Board clone() {
        Board newBoard = new Board(this.width, this.height, this.pieces, this.exitPosition);
        return newBoard;
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