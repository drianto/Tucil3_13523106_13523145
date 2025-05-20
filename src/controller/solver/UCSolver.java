package controller.solver;

import controller.SolutionResult;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Set;
import model.GameState;
import model.core.Board;

public class UCSolver extends RushHourSolver {
	public UCSolver(GameState initialState) {
		super(initialState);
	}

	@Override
	public SolutionResult solve() {
		long startTime = System.nanoTime();
		this.visitedNodesCount = 0;

		PriorityQueue<GameState> frontier = new PriorityQueue<>(Comparator.comparingInt(GameState::getCost));
		Set<Board> visitedBoards = new HashSet<>();

		if (this.initialState == null || this.initialState.getBoard() == null) {
			System.err.println("UCSolver Error: Initial state or initial board is null.");
			this.executionTime = System.nanoTime() - startTime;
			return new SolutionResult(false, new ArrayList<>(), 0, this.executionTime, new ArrayList<>());
		}
		
		frontier.add(this.initialState);
		visitedBoards.add(this.initialState.getBoard());

		int nodesProcessedSinceLastPublish = 0;
		final int PUBLISH_INTERVAL_NODES = 500;

		while (!frontier.isEmpty()) {
			GameState current = frontier.poll();
			this.visitedNodesCount++;
			nodesProcessedSinceLastPublish++;

			if (nodesProcessedSinceLastPublish >= PUBLISH_INTERVAL_NODES) {
				publishVisitedNodesCount();
				nodesProcessedSinceLastPublish = 0;
			}

			if (current.isGoalState()) {
				this.executionTime = System.nanoTime() - startTime;
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
		if (nodesProcessedSinceLastPublish > 0) {
			publishVisitedNodesCount();
		}
		return new SolutionResult(false, new ArrayList<>(), this.visitedNodesCount, this.executionTime, new ArrayList<>());
	}

	@Override
	public String getName() {
		return "Uniform Cost Search Solver";
	}
}