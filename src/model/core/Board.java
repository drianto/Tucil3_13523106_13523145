package model.core;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List; 
import java.util.Objects;
import java.util.Set;

public class Board {
    private final int width;
    private final int height;
    private final ArrayList<Piece> pieces;
    private final Position exitPosition;
    private Piece primaryPiece;
    private final Piece[][] grid;

    public Board(int width, int height, ArrayList<Piece> pieces, Position exitPosition) {
        this.width = width;
        this.height = height;
        this.exitPosition = new Position(exitPosition.getX(), exitPosition.getY());
        
        this.pieces = new ArrayList<>();
        for (Piece p : pieces) {
            Piece pieceCopy = p.clone();
            this.pieces.add(pieceCopy);
            
            if (pieceCopy.isPrimary()) {
                this.primaryPiece = pieceCopy;
            }
        }
        this.grid = new Piece[this.height][this.width];
        // Panggil metode helper untuk mengisi grid berdasarkan daftar pieces yang sudah di-clone
        populateGridFromPieces();
    }

    private void populateGridFromPieces() {
        // Grid diinisialisasi dengan null secara default untuk sel kosong
        for (Piece piece : this.pieces) {
            if (piece.getPositions() != null) {
                for (Position pos : piece.getPositions()) {
                    // Pastikan posisi valid sebelum mengakses grid
                    if (pos.getY() >= 0 && pos.getY() < this.height && pos.getX() >= 0 && pos.getX() < this.width) {
                        this.grid[pos.getY()][pos.getX()] = piece;
                    } else {
                        // Tambahkan penanganan error atau logging jika posisi bidak di luar batas papan
                        // Seharusnya ini tidak terjadi jika input file dan logika Piece valid
                        System.err.println("Peringatan: Posisi bidak " + piece.getId() + " di (" + pos.getX() + "," + pos.getY() + ") berada di luar batas papan saat mengisi grid.");
                    }
                }
            }
        }
    }

    public boolean movePiece(char pieceId, Direction direction) {
        Piece pieceToMove = this.getPieceById(pieceId);

        if (pieceToMove == null) {
            System.err.println("Error: Bidak dengan ID '" + pieceId + "' tidak ditemukan di papan untuk dipindahkan.");
            return false;
        }

        
        if (!pieceToMove.canMove(direction, this)) {
            return false;
        }

        List<Position> oldPositions = pieceToMove.getPositions(); // Ambil posisi saat ini
        for (Position pos : oldPositions) {
            if (pos.getY() >= 0 && pos.getY() < this.height && pos.getX() >= 0 && pos.getX() < this.width) {
                this.grid[pos.getY()][pos.getX()] = null;
            }
        }

        boolean moveSuccessful = pieceToMove.move(direction, this);

        if (moveSuccessful) {
            List<Position> newPositions = pieceToMove.getPositions(); // Ambil posisi yang sudah diperbarui
            for (Position pos : newPositions) {
                if (pos.getY() >= 0 && pos.getY() < this.height && pos.getX() >= 0 && pos.getX() < this.width) {
                    this.grid[pos.getY()][pos.getX()] = pieceToMove;
                } else {
                    System.err.println("Peringatan: Posisi baru bidak " + pieceToMove.getId() + " di (" + pos.getX() + "," + pos.getY() + ") berada di luar batas papan setelah bergerak.");
                }
            }
            return true;
        } else {
            for (Position pos : oldPositions) {
                if (pos.getY() >= 0 && pos.getY() < this.height && pos.getX() >= 0 && pos.getX() < this.width) {
                    this.grid[pos.getY()][pos.getX()] = pieceToMove;
                }
            }
            return false;
        }
    }

    public boolean isOccupied(Position position) {
        int x = position.getX();
        int y = position.getY();

        if (y >= 0 && y < this.height && x >= 0 && x < this.width) {
            return this.grid[y][x] != null;
        }
        return false; 
    }

    public boolean isPrimaryPieceAtExit() {
        if (primaryPiece == null) return false;
        for (Position p : primaryPiece.getPositions()) {
            boolean atExit = (p.getX() == exitPosition.getX() &&
                             p.getY() == exitPosition.getY() + 1) ||
                             (p.getX() == exitPosition.getX() + 1 &&
                             p.getY() == exitPosition.getY()) ||
                             (p.getX() == exitPosition.getX() &&
                             p.getY() == exitPosition.getY() - 1) ||
                             (p.getX() == exitPosition.getX() - 1 &&
                             p.getY() == exitPosition.getY());
            if (atExit) {
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Board board = (Board) o;

        if (width != board.width || height != board.height) {
            return false;
        }
        if (!Objects.equals(exitPosition, board.exitPosition)) {
            return false;
        }

        if (this.pieces == null && board.pieces == null) {
            // Keduanya null, dianggap sama
        } else if (this.pieces == null || board.pieces == null) {
            return false; // Salah satunya null, yang lain tidak
        } else {
            if (this.pieces.size() != board.pieces.size()) {
                return false; // Jumlah bidak berbeda
            }
             if (!new HashSet<>(this.pieces).equals(new HashSet<>(board.pieces))) {
                 return false;
             }
        }
        return true;
    }

    @Override
    public int hashCode() {
        Set<Piece> piecesSet = (pieces == null) ? null : new HashSet<>(pieces);
        return Objects.hash(width, height, piecesSet, exitPosition);
    }
}