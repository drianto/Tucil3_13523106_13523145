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
		populateGridFromPieces();
	}

	private void populateGridFromPieces() {
		for (int r = 0; r < this.height; r++) {
			for (int c = 0; c < this.width; c++) {
				this.grid[r][c] = null;
			}
		}
		for (Piece piece : this.pieces) {
			if (piece.getPositions() != null) {
				for (Position pos : piece.getPositions()) {
					if (pos.getY() >= 0 && pos.getY() < this.height && pos.getX() >= 0 && pos.getX() < this.width) {
						this.grid[pos.getY()][pos.getX()] = piece;
					} else {
						System.err.println("Peringatan: Posisi bidak " + piece.getId() + " di (" + pos.getX() + "," + pos.getY() + ") berada di luar batas papan saat mengisi grid.");
					}
				}
			}
		}
	}

	public boolean movePiece(char pieceId, Direction direction, int steps) {
		if (steps < 1) {
			System.err.println("Error: Jumlah langkah tidak valid (" + steps + ") untuk movePiece.");
			return false;
		}

		Piece pieceToMove = this.getPieceById(pieceId);

		if (pieceToMove == null) {
			System.err.println("Error: Bidak dengan ID '" + pieceId + "' tidak ditemukan di papan untuk dipindahkan.");
			return false;
		}

		List<Position> currentPiecePositions = pieceToMove.getPositions();
		for (Position pos : currentPiecePositions) {
			if (pos.getY() >= 0 && pos.getY() < this.height && pos.getX() >= 0 && pos.getX() < this.width) {
				this.grid[pos.getY()][pos.getX()] = null;
			}
		}

		pieceToMove.moveBySteps(direction, steps);

		List<Position> newPiecePositions = pieceToMove.getPositions();
		for (Position pos : newPiecePositions) {
			if (pos.getY() >= 0 && pos.getY() < this.height && pos.getX() >= 0 && pos.getX() < this.width) {
				if (this.grid[pos.getY()][pos.getX()] != null && this.grid[pos.getY()][pos.getX()] != pieceToMove) {
					 System.err.println("Error Kritis: Tabrakan terdeteksi saat memindahkan piece " + pieceId + " ke ("+pos.getX()+","+pos.getY()+") yang sudah ditempati oleh " + this.grid[pos.getY()][pos.getX()].getId() +". Rollback tidak diimplementasikan di sini.");
					
					 return false;
				}
				this.grid[pos.getY()][pos.getX()] = pieceToMove;
			} else {
				System.err.println("Peringatan Kritis: Posisi baru bidak " + pieceToMove.getId() + " di (" + pos.getX() + "," + pos.getY() + ") berada di luar batas papan SETELAH bergerak. Ini mengindikasikan masalah pada validasi sebelumnya.");
				 for (Position oldPos : currentPiecePositions) {
					if (oldPos.getY() >= 0 && oldPos.getY() < this.height && oldPos.getX() >= 0 && oldPos.getX() < this.width) {
						 this.grid[oldPos.getY()][oldPos.getX()] = pieceToMove;
					}
				}
				return false;
			}
		}
		return true;
	}

	public boolean isOccupied(Position position) {
		int x = position.getX();
		int y = position.getY();

		if (y >= 0 && y < this.height && x >= 0 && x < this.width) {
			return this.grid[y][x] != null;
		}
		return true;
	}

	public boolean isPrimaryPieceAtExit() {
		if (primaryPiece == null) return false;



		for (Position p : primaryPiece.getPositions()) {
			if (p.equals(exitPosition)) {
				return true;
			}
		}

		 for (Position p : primaryPiece.getPositions()) {
			if (primaryPiece.getOrientation() == Orientation.HORIZONTAL) {
				if (p.getY() == exitPosition.getY()) {
					if (exitPosition.getX() == this.width && p.getX() == this.width - primaryPiece.getPositions().size()) {
						
						
						
						 List<Position> pPositions = primaryPiece.getPositions();
						 Position rightMostP = pPositions.get(pPositions.size()-1);
						 if (exitPosition.getX() == this.width && rightMostP.getX() == this.width -1) return true;
						 Position leftMostP = pPositions.get(0);
						 if (exitPosition.getX() == -1 && leftMostP.getX() == 0) return true;

					}
				}
			} else {
				 if (p.getX() == exitPosition.getX()) {
					List<Position> pPositions = primaryPiece.getPositions();
					Position bottomMostP = pPositions.get(pPositions.size()-1);
					if (exitPosition.getY() == this.height && bottomMostP.getY() == this.height -1) return true;
					Position topMostP = pPositions.get(0);
					if (exitPosition.getY() == -1 && topMostP.getY() == 0) return true;
				 }
			}
		}
		return false;
	}

	public ArrayList<Move> getValidMoves() {
		ArrayList<Move> validMoves = new ArrayList<>();
		for (Piece piece : this.pieces) {
			List<Position> piecePositions = piece.getPositions();
			if (piecePositions.isEmpty()) {
				continue;
			}

			for (Direction direction : Direction.values()) {
				if ((piece.getOrientation() == Orientation.HORIZONTAL && (direction == Direction.UP || direction == Direction.DOWN)) ||
					(piece.getOrientation() == Orientation.VERTICAL && (direction == Direction.LEFT || direction == Direction.RIGHT))) {
					continue;
				}

				for (int s = 1; ; s++) {
					Position positionToTest;

					switch (direction) {
						case UP:
							Position currentTop = piecePositions.get(0);
							positionToTest = new Position(currentTop.getX(), currentTop.getY() - s);
							break;
						case DOWN:
							Position currentBottom = piecePositions.get(piecePositions.size() - 1);
							positionToTest = new Position(currentBottom.getX(), currentBottom.getY() + s);
							break;
						case LEFT:
							Position currentLeft = piecePositions.get(0);
							positionToTest = new Position(currentLeft.getX() - s, currentLeft.getY());
							break;
						case RIGHT:
							Position currentRight = piecePositions.get(piecePositions.size() - 1);
							positionToTest = new Position(currentRight.getX() + s, currentRight.getY());
							break;
						default:
							throw new IllegalStateException("Arah tidak dikenal: " + direction);
					}

					if (positionToTest.getX() < 0 || positionToTest.getX() >= this.width ||
						positionToTest.getY() < 0 || positionToTest.getY() >= this.height) {
						break;
					}

					if (this.grid[positionToTest.getY()][positionToTest.getX()] != null) {
						
						break;
					}

					validMoves.add(new Move(piece.getId(), direction, s));
				}
			}
		}
		return validMoves;
	}

	@Override
	public Board clone() {
		ArrayList<Piece> piecesCopy = new ArrayList<>();
		for (Piece p : this.pieces) {
			piecesCopy.add(p.clone());
		}
		Position exitPositionCopy = new Position(this.exitPosition.getX(), this.exitPosition.getY());
		return new Board(this.width, this.height, piecesCopy, exitPositionCopy);
	}

	public List<Piece> getPieces() {
		ArrayList<Piece> piecesCopy = new ArrayList<>();
		for(Piece p : this.pieces){
			piecesCopy.add(p.clone());
		}
		return piecesCopy;
	}

	public Piece getPrimaryPiece() {
		return this.primaryPiece;
	}

	public Piece getPieceById(char id) {
		for (Piece p : this.pieces) {
			if (p.getId() == id) {
				return p;
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
		return new Position(this.exitPosition.getX(), this.exitPosition.getY());
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
		} else if (this.pieces == null || board.pieces == null) {
			return false;
		} else {
			if (this.pieces.size() != board.pieces.size()) {
				return false;
			}
			 if (!new HashSet<>(this.pieces).equals(new HashSet<>(board.pieces))) {
				 return false;
			 }
		}
		return true;
	}

	@Override
	public int hashCode() {
		Set<Piece> piecesSet = (this.pieces == null) ? null : new HashSet<>(this.pieces);
		return Objects.hash(width, height, piecesSet, exitPosition);
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		char[][] charGrid = new char[height][width];
		for(int r=0; r<height; r++) {
			for(int c=0; c<width; c++) {
				charGrid[r][c] = '.';
			}
		}
		for(Piece p : this.pieces) {
			for(Position pos : p.getPositions()) {
				if(pos.getX() >= 0 && pos.getX() < width && pos.getY() >=0 && pos.getY() < height) {
					charGrid[pos.getY()][pos.getX()] = p.getId();
				}
			}
		}
		for(int r=0; r<height; r++) {
			for(int c=0; c<width; c++) {
				sb.append(charGrid[r][c]);
			}
			if (exitPosition.getY() == r && exitPosition.getX() == width) sb.append('K');
			if (exitPosition.getY() == r && exitPosition.getX() == -1) sb.insert(0, 'K');
			sb.append("\n");
		}
		if (exitPosition.getY() == -1) {
			StringBuilder topExitLine = new StringBuilder();
			for (int c=0; c<width; c++) topExitLine.append(c == exitPosition.getX() ? 'K' : '.');
			sb.insert(0, topExitLine.toString() + "\n");
		}
		if (exitPosition.getY() == height) {
			StringBuilder bottomExitLine = new StringBuilder();
			for (int c=0; c<width; c++) bottomExitLine.append(c == exitPosition.getX() ? 'K' : '.');
			sb.append(bottomExitLine.toString()).append("\n");
		}
		return sb.toString();
	}
}
