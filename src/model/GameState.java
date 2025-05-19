package model;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import model.core.Board;
import model.core.Move;
import model.core.Piece; // Ditambahkan untuk getSuccessors

// GameState dibuat Comparable untuk digunakan dalam PriorityQueue pada UCS
public class GameState implements Comparable<GameState> { // Ditambahkan Comparable
    private final int cost;
    private Board board;
    private List<Move> moves;

    // Constructor
    public GameState() {
        this.cost = 0;
        this.board = null; // Atau papan default jika ada
        this.moves = new ArrayList<>(); // Inisialisasi dengan daftar kosong
    }

    public GameState(int cost, Board board, List<Move> moves) {
        this.cost = cost;
        this.board = board;
        this.moves = moves;
    }

    // Methods
    public List<GameState> getSuccessors() {
        List<GameState> successors = new ArrayList<>();
        if (this.board == null) {
            // Seharusnya tidak terjadi dalam alur normal jika GameState diinisialisasi dengan benar
            return successors;
        }
        List<Move> validMoves = this.board.getValidMoves();

        for (Move move : validMoves) {
            // PENTING KRITIS: Board.clone() harus merupakan DEEP CLONE.
            // Jika tidak, semua objek GameState akan berbagi dan memodifikasi objek Piece yang sama.
            // Anda HARUS memastikan implementasi Board.clone() melakukan deep clone.
            Board newBoard = this.board.clone();
            Piece pieceToMove = newBoard.getPieceById(move.getPieceId());

            if (pieceToMove != null) {
                // PENTING: newBoard.movePiece juga harus bekerja pada salinan bidak jika Piece.move() memodifikasi state.
                // Jika Piece.move() mengembalikan Piece baru, itu lebih baik.
                // Saat ini, Piece.move() memodifikasi posisi internal Piece.
                boolean moveApplied = newBoard.movePiece(pieceToMove, move.getDirection());
                if (moveApplied) { // Seharusnya selalu true jika getValidMoves benar
                    List<Move> newMovesList = new ArrayList<>(this.moves);
                    newMovesList.add(move);
                    successors.add(new GameState(this.cost + 1, newBoard, newMovesList));
                }
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
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        GameState gameState = (GameState) o;
        // Untuk Set 'visited' pada UCS, kesetaraan papan adalah yang utama.
        // Namun, equals() yang ada juga memeriksa 'moves'. Ini mungkin tidak ideal untuk 'visited'
        // yang hanya peduli konfigurasi papan, tetapi baik untuk perbandingan GameState secara umum.
        // Set 'visitedBoards' di UCSolver akan menggunakan Board.equals().
        return cost == gameState.cost && // Biaya juga relevan untuk UCS jika kita memasukkan GameState ke Set
               Objects.equals(board, gameState.board) &&
               Objects.equals(moves, gameState.moves);
    }

    @Override
    public int hashCode() {
        return Objects.hash(cost, board, moves);
    }

    @Override
    public int compareTo(GameState other) {
        // Membandingkan berdasarkan biaya (jumlah langkah)
        return Integer.compare(this.cost, other.cost);
    }
}