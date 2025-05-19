package controller.solver;

import controller.SolutionResult;
import java.util.ArrayList;
import java.util.HashSet; // Diperlukan untuk Set visitedBoards dan reconstructBoardStates
import java.util.List;  // Diperlukan untuk reconstructBoardStates
import java.util.PriorityQueue; // Diperlukan untuk reconstructBoardStates
import java.util.Set;
import model.GameState;
import model.core.Board;
import model.core.Move;
import model.core.Piece;

public class UCSolver extends RushHourSolver {
    public UCSolver(GameState initialState) {
        super(initialState);
    }

    @Override
    public SolutionResult solve() {
        long startTime = System.nanoTime();
        this.visitedNodesCount = 0;

        // PriorityQueue diurutkan berdasarkan GameState.cost (karena implementasi Comparable)
        PriorityQueue<GameState> frontier = new PriorityQueue<>();
        
        // Set untuk menyimpan konfigurasi papan yang sudah dikunjungi.
        // PENTING KRITIS: Kelas Board HARUS memiliki implementasi equals() dan hashCode() yang benar
        // berdasarkan konfigurasi bidaknya agar ini berfungsi dengan benar.
        Set<Board> visitedBoards = new HashSet<>();

        if (this.initialState == null || this.initialState.getBoard() == null) {
            System.err.println("UCSolver Error: Initial state or initial board is null.");
            this.executionTime = System.nanoTime() - startTime;
            return new SolutionResult(false, new ArrayList<>(), 0, this.executionTime, new ArrayList<>());
        }
        
        frontier.add(this.initialState);
        visitedBoards.add(this.initialState.getBoard());

        while (!frontier.isEmpty()) {
            GameState current = frontier.poll();
            this.visitedNodesCount++;

            if (current.isGoalState()) {
                this.executionTime = System.nanoTime() - startTime;
                List<Board> boardStatesPath = reconstructBoardStates(current);
                return new SolutionResult(true, current.getMoves(), this.visitedNodesCount, this.executionTime, boardStatesPath);
            }

            List<GameState> successors = current.getSuccessors();
            for (GameState successor : successors) {
                // Periksa apakah konfigurasi papan dari successor sudah pernah dikunjungi
                if (!visitedBoards.contains(successor.getBoard())) {
                    visitedBoards.add(successor.getBoard());
                    frontier.add(successor);
                }
            }
        }

        // Jika loop selesai dan tidak ada solusi ditemukan
        this.executionTime = System.nanoTime() - startTime;
        return new SolutionResult(false, new ArrayList<>(), this.visitedNodesCount, this.executionTime, new ArrayList<>());
    }

    private List<Board> reconstructBoardStates(GameState finalState) {
        List<Board> boardStatesPath = new ArrayList<>();
        List<Move> moves = finalState.getMoves();
        
        if (this.initialState == null || this.initialState.getBoard() == null) {
             System.err.println("UCSolver Error: Initial state or board is null during board state reconstruction.");
            return boardStatesPath; // Kembalikan daftar kosong jika state awal tidak valid
        }

        // PENTING KRITIS: this.initialState.getBoard().clone() HARUS berupa DEEP CLONE.
        Board currentIterationBoard = this.initialState.getBoard().clone(); 

        // Sesuai komentar di SolutionResult: boardStates[i] adalah keadaan SETELAH moves[i].
        for (Move move : moves) {
            // PENTING KRITIS: currentIterationBoard.clone() HARUS berupa DEEP CLONE.
            Board nextBoardState = currentIterationBoard.clone(); 
            Piece pieceToMove = nextBoardState.getPieceById(move.getPieceId());

            if (pieceToMove != null) {
                boolean success = nextBoardState.movePiece(pieceToMove, move.getDirection());
                if(success){
                    boardStatesPath.add(nextBoardState);
                    currentIterationBoard = nextBoardState; // Perbarui currentIterationBoard untuk iterasi berikutnya
                } else {
                     System.err.println("UCSolver Warning: Failed to apply move during board state reconstruction. Move: " + move.getPieceId() + " " + move.getDirection());
                     // Mungkin tambahkan state terakhir yang valid atau placeholder jika gagal
                     boardStatesPath.add(currentIterationBoard.clone()); // Tambahkan state sebelum move yang gagal
                }
            } else {
                System.err.println("UCSolver Error in reconstructBoardStates: Piece with ID '" + move.getPieceId() + "' not found for move.");
                // Tambahkan state terakhir yang valid sebagai placeholder
                boardStatesPath.add(currentIterationBoard.clone());
            }
        }
        return boardStatesPath;
    }

    @Override
    public String getName() {
        return "Uniform Cost Search Solver";
    }
}