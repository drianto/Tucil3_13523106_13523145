// In src/controller/solver/UCSolver.java
package controller.solver;

import controller.SolutionResult;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Set;
import model.GameState;
import model.core.Board;
import model.core.Move;
// Piece tidak perlu diimpor jika tidak digunakan langsung di sini
// import model.core.Piece; 

public class UCSolver extends RushHourSolver {
    public UCSolver(GameState initialState) {
        super(initialState);
    }

    @Override
    public SolutionResult solve() {
        long startTime = System.nanoTime();
        this.visitedNodesCount = 0;

        PriorityQueue<GameState> frontier = new PriorityQueue<>();
        Set<Board> visitedBoards = new HashSet<>();

        if (this.initialState == null || this.initialState.getBoard() == null) {
            System.err.println("UCSolver Error: Initial state or initial board is null.");
            this.executionTime = System.nanoTime() - startTime;
            return new SolutionResult(false, new ArrayList<>(), 0, this.executionTime, new ArrayList<>());
        }
        
        frontier.add(this.initialState);
        visitedBoards.add(this.initialState.getBoard());

        // --- AWAL Variabel untuk Kontrol Publikasi Progres ---
        int nodesProcessedSinceLastPublish = 0;
        final int PUBLISH_INTERVAL_NODES = 500; // Publikasikan progres setiap 500 node (bisa disesuaikan)
        // --- AKHIR Variabel untuk Kontrol Publikasi Progres ---

        while (!frontier.isEmpty()) {
            GameState current = frontier.poll();
            this.visitedNodesCount++;
            nodesProcessedSinceLastPublish++;

            // --- AWAL Panggil Publikasi Progres ---
            if (nodesProcessedSinceLastPublish >= PUBLISH_INTERVAL_NODES) {
                publishVisitedNodesCount(); // Panggil metode dari RushHourSolver
                nodesProcessedSinceLastPublish = 0; // Reset counter
            }
            // --- AKHIR Panggil Publikasi Progres ---

            if (current.isGoalState()) {
                this.executionTime = System.nanoTime() - startTime;
                // Pastikan progres terakhir dipublikasikan sebelum mengembalikan hasil
                if (nodesProcessedSinceLastPublish > 0) {
                    publishVisitedNodesCount();
                }
                List<Board> boardStatesPath = reconstructBoardStates(current);
                return new SolutionResult(true, current.getMoves(), this.visitedNodesCount, this.executionTime, boardStatesPath);
            }

            List<GameState> successors = current.getSuccessors();
            for (GameState successor : successors) {
                if (!visitedBoards.contains(successor.getBoard())) {
                    visitedBoards.add(successor.getBoard());
                    frontier.add(successor);
                }
            }
        }

        this.executionTime = System.nanoTime() - startTime;
        // Jika tidak ada solusi, pastikan progres terakhir juga dipublikasikan
        if (nodesProcessedSinceLastPublish > 0) {
            publishVisitedNodesCount();
        }
        return new SolutionResult(false, new ArrayList<>(), this.visitedNodesCount, this.executionTime, new ArrayList<>());
    }

    private List<Board> reconstructBoardStates(GameState finalState) {
        // ... (implementasi reconstructBoardStates tetap sama)
        List<Board> boardStatesPath = new ArrayList<>();
        List<Move> moves = finalState.getMoves();
        
        if (this.initialState == null || this.initialState.getBoard() == null) {
             System.err.println("UCSolver Error: Initial state or board is null during board state reconstruction.");
            return boardStatesPath; 
        }

        Board currentIterationBoard = this.initialState.getBoard().clone(); 

        for (Move move : moves) {
            Board nextBoardState = currentIterationBoard.clone(); 
            // Piece pieceToMove = nextBoardState.getPieceById(move.getPieceId()); // Tidak perlu lagi jika movePiece terima ID
            // if (pieceToMove != null) {
            //     boolean success = nextBoardState.movePiece(pieceToMove, move.getDirection());
            // ...
            // Dengan asumsi movePiece menerima pieceId:
            boolean success = nextBoardState.movePiece(move.getPieceId(), move.getDirection());
            if(success){
                boardStatesPath.add(nextBoardState);
                currentIterationBoard = nextBoardState; 
            } else {
                 System.err.println("UCSolver Warning: Failed to apply move during board state reconstruction. Move: " + move.getPieceId() + " " + move.getDirection());
                 boardStatesPath.add(currentIterationBoard.clone()); 
            }
            // } else { // getPieceById tidak lagi dipanggil di sini
            //     System.err.println("UCSolver Error in reconstructBoardStates: Piece with ID '" + move.getPieceId() + "' not found for move.");
            //     boardStatesPath.add(currentIterationBoard.clone());
            // }
        }
        return boardStatesPath;
    }

    @Override
    public String getName() {
        return "Uniform Cost Search Solver";
    }
}