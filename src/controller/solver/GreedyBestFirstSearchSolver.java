package controller.solver;

import controller.SolutionResult;
import controller.heuristic.*;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Set;
import model.GameState;
import model.core.Board;

public class GreedyBestFirstSearchSolver extends RushHourSolver{
    private final Heuristic heuristic;

    public GreedyBestFirstSearchSolver(GameState initialState, Heuristic heuristic) {
        super(initialState);
        this.heuristic = heuristic;
    }

    @Override
    public SolutionResult solve() {
        long startTime = System.nanoTime();
        this.visitedNodesCount = 0;

        PriorityQueue<GameState> frontier = new PriorityQueue<>(Comparator.comparingInt(GameState::gethCost));
        Set<Board> visitedBoards = new HashSet<>();

        if (this.initialState == null || this.initialState.getBoard() == null) {
			System.err.println("GBFSolver Error: Initial state or initial board is null.");
			this.executionTime = System.nanoTime() - startTime;
			return new SolutionResult(false, new ArrayList<>(), 0, this.executionTime, new ArrayList<>());
		}

        frontier.add(this.initialState);
		visitedBoards.add(this.initialState.getBoard());

        int nodesProcessedSinceLastPublish = 0;
		final int PUBLISH_INTERVAL_NODES = 500;

        while(!frontier.isEmpty()) {
            GameState currentGameState = frontier.poll();
            this.visitedNodesCount++;
            nodesProcessedSinceLastPublish++;

            if (nodesProcessedSinceLastPublish >= PUBLISH_INTERVAL_NODES) {
				publishVisitedNodesCount();
				nodesProcessedSinceLastPublish = 0;
			}

            if (currentGameState.isGoalState()) {
				this.executionTime = System.nanoTime() - startTime;
				if (nodesProcessedSinceLastPublish > 0) {
					publishVisitedNodesCount();
				}
				List<Board> boardStatesPath = reconstructBoardStates(currentGameState);
				return new SolutionResult(true, currentGameState.getMoves(), this.visitedNodesCount, this.executionTime, boardStatesPath);
			}

            List<GameState> successors = currentGameState.getSuccessors();
			for (GameState successor : successors) {
				if (!visitedBoards.contains(successor.getBoard())) {
                    int hCostSuccessor = heuristic.calculate(successor);
                    if (hCostSuccessor == Integer.MAX_VALUE) {
                        continue;
                    }
                    successor.setHeuristicCosts(hCostSuccessor);
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
        return "Greedy Best First Search Solver";
    }
}
