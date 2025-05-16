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
        
    }

    public boolean isOccupied(Position position) {

    }

    public boolean isPrimaryPieceAtExit() {

    }

    public ArrayList<Move> getValidMoves() {

    }

    public Board clone() {

    }

    public List<Piece> getPieces() {

    }

    public Piece getPrimaryPiece() {

    }

    public Piece getPieceById(char id) {
        
    }

    public int getWidth() {

    }

    public int getHeight() {

    }

    public getExitPosition() {
        
    }
}