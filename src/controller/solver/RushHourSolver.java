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

	protected Consumer<Integer> progressEventConsumer;

	public void setProgressEventConsumer(Consumer<Integer> consumer) {
		this.progressEventConsumer = consumer;
	}

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
			 System.err.println("Solver Error: Initial state atau initial board adalah null saat rekonstruksi board state.");
			return boardStatesPath;
		}

		Board currentIterationBoard = this.initialState.getBoard().clone();

		for (Move move : moves) {
			Board nextBoardState = currentIterationBoard.clone();

			boolean success = nextBoardState.movePiece(move.getPieceId(), move.getDirection(), move.getSteps());

			if(success) {
				boardStatesPath.add(nextBoardState);
				currentIterationBoard = nextBoardState;
			} else {
				 System.err.println("Solver Warning: Gagal menerapkan move saat rekonstruksi board state. Move: " +
								   move.getPieceId() + " ke " + move.getDirection() +
								   " sebanyak " + move.getSteps() + " langkah.");
				
				
				 boardStatesPath.add(currentIterationBoard.clone());
			}
		}
		return boardStatesPath;
	}

	public abstract SolutionResult solve();
	public abstract String getName();
}
