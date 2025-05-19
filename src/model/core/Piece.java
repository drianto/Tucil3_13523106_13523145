package model.core;

import java.util.ArrayList;
import java.util.List;

public class Piece {
    private char id;
    private ArrayList <Position> positions;
    private Orientation orientation;
    private boolean isPrimary;

    public Piece(char id, ArrayList<Position> positions, Orientation orientation, boolean isPrimary) {
        this.id = id;
        // Create a deep copy of the positions to avoid external modification
        this.positions = new ArrayList<>();
        for (Position p : positions) {
            this.positions.add(new Position(p.getX(), p.getY()));
        }
        this.orientation = orientation;
        this.isPrimary = isPrimary;
    }

    public boolean move(Direction direction, Board board) {
        if (!canMove(direction, board)) return false;

        int x = 0, y = 0;
        switch (direction) {
            case UP:    
                    y = -1; 
                break;
            case DOWN:  
                    y =  1; 
                break;
            case LEFT:  
                    x = -1; 
                break;
            case RIGHT: 
                    x =  1; 
                break;
        }

        for (int i = 0; i < positions.size(); i++) {
            Position p = positions.get(i);
            positions.set(i, new Position(p.getX() + x, p.getY() + y));
        }
        return true;
    }


    public boolean canMove(Direction direction, Board board) {
        Position edge;
        switch (direction) {
            case UP:
                if (orientation != Orientation.VERTICAL) return false;
                edge = positions.get(0);             
                return edge.getY() > 0 &&
                    !board.isOccupied(new Position(edge.getX(), edge.getY() - 1));

            case DOWN:
                if (orientation != Orientation.VERTICAL) return false;
                edge = positions.get(positions.size() - 1); 
                return edge.getY() < board.getHeight() - 1 &&
                    !board.isOccupied(new Position(edge.getX(), edge.getY() + 1));

            case LEFT:
                if (orientation != Orientation.HORIZONTAL) return false;
                edge = positions.get(0);                  
                return edge.getX() > 0 &&
                    !board.isOccupied(new Position(edge.getX() - 1, edge.getY()));

            case RIGHT:
                if (orientation != Orientation.HORIZONTAL) return false;
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
        return this.positions;
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
}
