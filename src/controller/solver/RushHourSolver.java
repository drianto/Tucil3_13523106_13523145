// In src/controller/solver/RushHourSolver.java
package controller.solver;

import controller.SolutionResult;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import model.GameState;
import model.core.Board;
import model.core.Move; 

public abstract class RushHourSolver {
    protected GameState initialState;
    protected int visitedNodesCount;
    protected long executionTime;

    // --- AWAL Perubahan untuk Progress Reporting (FIXED) ---
    // Menggunakan Consumer untuk callback publikasi progres.
    // Consumer akan menerima Integer (visitedNodesCount)
    protected Consumer<Integer> progressEventConsumer;

    /**
     * Mengatur callback (Consumer) yang akan dipanggil untuk mempublikasikan progres.
     * @param consumer Consumer yang akan menerima update progres (misalnya, jumlah node yang dikunjungi).
     */
    public void setProgressEventConsumer(Consumer<Integer> consumer) {
        this.progressEventConsumer = consumer;
    }

    /**
     * Metode helper untuk solver konkret mempublikasikan progres.
     * Ini akan memanggil Consumer yang telah diatur, yang kemudian akan memanggil SwingWorker.publish().
     */
    protected void publishVisitedNodesCount() {
        if (this.progressEventConsumer != null) {
            this.progressEventConsumer.accept(this.visitedNodesCount);
        }
    }

    public RushHourSolver(GameState initialState) {
        this.initialState = initialState;
        this.visitedNodesCount = 0;
        this.executionTime = 0;
    }

    public int getVisitedNodesCount() {
        return this.visitedNodesCount;
    }

    public long getExecutionTime() {
        return this.executionTime;
    }

	public List<Board> reconstructBoardStates(GameState finalState) {
		List<Board> boardStatesPath = new ArrayList<>();
		List<Move> moves = finalState.getMoves();
		
		if (this.initialState == null || this.initialState.getBoard() == null) {
			 System.err.println("Solver Error: Initial state or board is null during board state reconstruction.");
			return boardStatesPath; 
		}

		Board currentIterationBoard = this.initialState.getBoard().clone(); 

		for (Move move : moves) {
			Board nextBoardState = currentIterationBoard.clone(); 
			boolean success = nextBoardState.movePiece(move.getPieceId(), move.getDirection());
			if(success) {
				boardStatesPath.add(nextBoardState);
				currentIterationBoard = nextBoardState; 
			} else {
				 System.err.println("Solver Warning: Failed to apply move during board state reconstruction. Move: " + move.getPieceId() + " " + move.getDirection());
				 boardStatesPath.add(currentIterationBoard.clone()); 
			}
		}
		return boardStatesPath;
	}
    
    public abstract SolutionResult solve();
    public abstract String getName();
}