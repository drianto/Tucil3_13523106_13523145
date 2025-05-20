package model.core;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Piece {
	private final char id;
	private final ArrayList <Position> positions;
	private final Orientation orientation;
	private final boolean isPrimary;

	public Piece(char id, ArrayList<Position> positions, Orientation orientation, boolean isPrimary) {
		this.id = id;
		this.positions = new ArrayList<>();
		for (Position p : positions) {
			this.positions.add(new Position(p.getX(), p.getY()));
		}
		this.orientation = orientation;
		this.isPrimary = isPrimary;
	}

	public void moveBySteps(Direction direction, int steps) {
		if (steps == 0) {
			return;
		}

		int dx = 0, dy = 0;
		switch (direction) {
			case UP:
				dy = -steps;
				break;
			case DOWN:
				dy = steps;
				break;
			case LEFT:
				dx = -steps;
				break;
			case RIGHT:
				dx = steps;
				break;
		}

		for (int i = 0; i < positions.size(); i++) {
			Position p = positions.get(i);
			positions.set(i, new Position(p.getX() + dx, p.getY() + dy));
		}
	}


	public boolean canMove(Direction direction, Board board) {
		Position edge;
		switch (direction) {
			case UP:
				if (orientation != Orientation.VERTICAL) return false;
				if (positions.isEmpty()) return false;
				edge = positions.get(0);
				return edge.getY() > 0 &&
					!board.isOccupied(new Position(edge.getX(), edge.getY() - 1));

			case DOWN:
				if (orientation != Orientation.VERTICAL) return false;
				if (positions.isEmpty()) return false;
				edge = positions.get(positions.size() - 1);
				return edge.getY() < board.getHeight() - 1 &&
					!board.isOccupied(new Position(edge.getX(), edge.getY() + 1));

			case LEFT:
				if (orientation != Orientation.HORIZONTAL) return false;
				if (positions.isEmpty()) return false;
				edge = positions.get(0);
				return edge.getX() > 0 &&
					!board.isOccupied(new Position(edge.getX() - 1, edge.getY()));

			case RIGHT:
				if (orientation != Orientation.HORIZONTAL) return false;
				if (positions.isEmpty()) return false;
				edge = positions.get(positions.size() - 1);
				return edge.getX() < board.getWidth() - 1 &&
					!board.isOccupied(new Position(edge.getX() + 1, edge.getY()));

			default:
				return false;
		}
	}

	@Override
	public Piece clone() {
		ArrayList<Position> positionsCopy = new ArrayList<>();
		for (Position p : this.positions) {
			positionsCopy.add(new Position(p.getX(), p.getY()));
		}
		return new Piece(this.id, positionsCopy, this.orientation, this.isPrimary);
	}

	public List<Position> getPositions() {
		return new ArrayList<>(this.positions);
	}

	public Orientation getOrientation() {
		return this.orientation;
	}

	public boolean isPrimary() {
		return this.isPrimary;
	}

	public char getId() {
		return this.id;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		Piece piece = (Piece) o;
		return id == piece.id &&
			   isPrimary == piece.isPrimary &&
			   orientation == piece.orientation &&
			   Objects.equals(positions, piece.positions);
	}

	@Override
	public int hashCode() {
		return Objects.hash(id, positions, orientation, isPrimary);
	}
}
