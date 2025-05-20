package controller.heuristic;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import model.GameState;
import model.core.Board;
import model.core.Orientation;
import model.core.Piece;
import model.core.Position;

public class BlockingPiecesHeuristic implements Heuristic {

	public BlockingPiecesHeuristic() {
	
	}

	@Override
	public int calculate(GameState gameState) {
		Board board = gameState.getBoard();
		if (board == null) {
			return Integer.MAX_VALUE;
		}

		Piece primaryPiece = board.getPrimaryPiece();
		if (primaryPiece == null) {
			return Integer.MAX_VALUE;
		}

		Position exitPos = board.getExitPosition();
		if (exitPos == null) {
			return Integer.MAX_VALUE;
		}

		Set<Character> blockingPieceIds = new HashSet<>();
		List<Piece> allPieces = board.getPieces();

		if (primaryPiece.getOrientation() == Orientation.HORIZONTAL) {
			int pieceRow = primaryPiece.getPositions().get(0).getY();
			int primaryPieceMinX = Integer.MAX_VALUE;
			int primaryPieceMaxX = Integer.MIN_VALUE;

			for (Position pos : primaryPiece.getPositions()) {
				if (pos.getX() < primaryPieceMinX) {
					primaryPieceMinX = pos.getX();
				}
				if (pos.getX() > primaryPieceMaxX) {
					primaryPieceMaxX = pos.getX();
				}
			}

			int pathStartX, pathEndX;
			if (exitPos.getX() > primaryPieceMaxX) {
				pathStartX = primaryPieceMaxX + 1;
				pathEndX = board.getWidth() - 1;
			} else if (exitPos.getX() < primaryPieceMinX) {
				pathStartX = primaryPieceMinX - 1;
				pathEndX = 0;
			} else {
				return 0;
			}

			for (int x = Math.min(pathStartX, pathEndX); x <= Math.max(pathStartX, pathEndX); x++) {
				Position currentPathPos = new Position(x, pieceRow);
				for (Piece otherPiece : allPieces) {
					if (otherPiece.isPrimary()) {
						continue;
					}
					if (otherPiece.getPositions().contains(currentPathPos)) {
						blockingPieceIds.add(otherPiece.getId());
					}
				}
			}

		} else {
			int pieceCol = primaryPiece.getPositions().get(0).getX();
			int primaryPieceMinY = Integer.MAX_VALUE;
			int primaryPieceMaxY = Integer.MIN_VALUE;

			for (Position pos : primaryPiece.getPositions()) {
				if (pos.getY() < primaryPieceMinY) {
					primaryPieceMinY = pos.getY();
				}
				if (pos.getY() > primaryPieceMaxY) {
					primaryPieceMaxY = pos.getY();
				}
			}

			int pathStartY, pathEndY;
			if (exitPos.getY() > primaryPieceMaxY) {
				pathStartY = primaryPieceMaxY + 1;
				pathEndY = board.getHeight() - 1;
			} else if (exitPos.getY() < primaryPieceMinY) {
				pathStartY = primaryPieceMinY - 1;
				pathEndY = 0;
			} else {
				return 0;
			}
			
			for (int y = Math.min(pathStartY, pathEndY); y <= Math.max(pathStartY, pathEndY); y++) {
				Position currentPathPos = new Position(pieceCol, y);
				for (Piece otherPiece : allPieces) {
					if (otherPiece.isPrimary()) {
						continue;
					}
					if (otherPiece.getPositions().contains(currentPathPos)) {
						blockingPieceIds.add(otherPiece.getId());
					}
				}
			}
		}
		return blockingPieceIds.size();
	}

	@Override
	public String getName() {
		return "Blocking Pieces Heuristic";
	}
}