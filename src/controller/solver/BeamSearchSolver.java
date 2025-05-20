package controller.solver;

import controller.SolutionResult;
import controller.heuristic.Heuristic;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Set;
import model.GameState;
import model.core.Board;

public class BeamSearchSolver extends RushHourSolver {
	private final Heuristic heuristic;
	private final int beamWidth;

	private int nodesProcessedSinceLastPublish = 0;
	private static final int PUBLISH_INTERVAL_NODES = 500;

	public BeamSearchSolver(GameState initialState, Heuristic heuristic, int beamWidth) {
		super(initialState);
		this.heuristic = heuristic;
		this.beamWidth = beamWidth > 0 ? beamWidth : 1;
	}

	public BeamSearchSolver(GameState initialState, Heuristic heuristic) {
		this(initialState, heuristic, 10);
	}


	@Override
	public SolutionResult solve() {
		long startTime = System.nanoTime();
		this.visitedNodesCount = 0;
		this.nodesProcessedSinceLastPublish = 0;

		if (this.initialState == null || this.initialState.getBoard() == null) {
			System.err.println("BeamSearchSolver Error: Initial state atau initial board adalah null.");
			this.executionTime = System.nanoTime() - startTime;
			return new SolutionResult(false, new ArrayList<>(), 0, this.executionTime, new ArrayList<>());
		}

		if (this.heuristic == null) {
			System.err.println("BeamSearchSolver Error: Heuristic tidak boleh null.");
			this.executionTime = System.nanoTime() - startTime;
			return new SolutionResult(false, new ArrayList<>(), 0, this.executionTime, new ArrayList<>());
		}

		List<GameState> currentBeam = new ArrayList<>();
		int initialHCost = heuristic.calculate(this.initialState);
		if (initialHCost == Integer.MAX_VALUE) {
			 this.executionTime = System.nanoTime() - startTime;
			 publishFinalProgress();
			 return new SolutionResult(false, new ArrayList<>(), this.visitedNodesCount, this.executionTime, new ArrayList<>());
		}
		this.initialState.setHeuristicCosts(initialHCost);
		currentBeam.add(this.initialState);

		Set<Board> visitedBoards = new HashSet<>();
		visitedBoards.add(this.initialState.getBoard());

		while (!currentBeam.isEmpty()) {
			PriorityQueue<GameState> candidates = new PriorityQueue<>(Comparator.comparingInt(GameState::gethCost));

			for (GameState currentState : currentBeam) {
				this.visitedNodesCount++;
				this.nodesProcessedSinceLastPublish++;

				if (nodesProcessedSinceLastPublish >= PUBLISH_INTERVAL_NODES) {
					publishVisitedNodesCount();
					nodesProcessedSinceLastPublish = 0;
				}

				if (currentState.isGoalState()) {
					this.executionTime = System.nanoTime() - startTime;
					publishFinalProgress();
					List<Board> boardStatesPath = reconstructBoardStates(currentState);
					return new SolutionResult(true, currentState.getMoves(), this.visitedNodesCount, this.executionTime, boardStatesPath);
				}

				List<GameState> successors = currentState.getSuccessors();
				for (GameState successor : successors) {
					if (!visitedBoards.contains(successor.getBoard())) {
						int hCostSuccessor = heuristic.calculate(successor);
						if (hCostSuccessor == Integer.MAX_VALUE) {
							continue;
						}
						successor.setHeuristicCosts(hCostSuccessor);
						
						candidates.add(successor);
						visitedBoards.add(successor.getBoard()); 
					}
				}
			}

			currentBeam.clear();
			int count = 0;
			while (!candidates.isEmpty() && count < this.beamWidth) {
				currentBeam.add(candidates.poll());
				count++;
			}

			if (currentBeam.isEmpty() && !candidates.isEmpty()) {
			}
		}

		this.executionTime = System.nanoTime() - startTime;
		publishFinalProgress();
		return new SolutionResult(false, new ArrayList<>(), this.visitedNodesCount, this.executionTime, new ArrayList<>());
	}
	
	private void publishFinalProgress() {
		if (nodesProcessedSinceLastPublish > 0) {
			publishVisitedNodesCount();
			nodesProcessedSinceLastPublish = 0;
		}
	}

	@Override
	public String getName() {
		return "Beam Search Solver (w=" + this.beamWidth + ") with " + (heuristic != null ? heuristic.getName() : "No Heuristic");
	}
}