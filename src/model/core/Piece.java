package model.core;

import java.util.ArrayList;
import java.util.List;

import model.core.Position;
import model.core.Orientation;
import model.core.Direction;

public class Piece {
    private char id;
    private ArrayList <Position> positions;
    private Orientation orientation;
    private boolean isPrimary;

    public Piece(char id, ArrayList<Position> positions, Orientation orientation, boolean isPrimary) {
        
    }

    public void move(Direction direction) {
        
    }

    public boolean canMove(Direction direction) {
        return true;
    }

    public List<Position> getPositions() {
        return null;
    }

    public Orientation getOrientation() {
        return null;
    }

    public boolean isPrimary() {
        return true;
    }

    public char getId() {
        return 'i';
    }
}
