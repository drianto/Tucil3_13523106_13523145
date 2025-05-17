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

    public boolean MovePiece(Piece piece, Direction direction) {
        return true;
    }

    public boolean isOccupied(Position position) {
        return true;
    }

    public boolean isPrimaryPieceAtExit() {
        return true;
    }

    public ArrayList<Move> getValidMoves() {
        return null;
    }

    public Board clone() {
        return null;
    }

    public List<Piece> getPieces() {
        return null;
    }

    public Piece getPrimaryPiece() {
        return null;
    }
    
    public Piece getPieceById(char id) {
        return null;   
    }

    public int getWidth() {
        return 1;
    }

    public int getHeight() {
        return 1;   
    }

    public Position getExitPosition() {
        return null;
    }
}