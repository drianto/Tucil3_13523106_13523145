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
        this.positions = positions;
        this.orientation = orientation;
        this.isPrimary = isPrimary;
    }

    // public boolean move(Direction direction) {
    //     if 
    // }

    // public boolean canMove(Direction direction, Board board) {
    //     switch (orientation) {
    //         case VERTICAL:
    //                 switch (direction) {
    //                     case UP:
    //                             for (int i = 0; i < this.positions.size(); i++) {
                                    
    //                             }
    //                         break;

    //                     case DOWN:
                             
    //                         break;
    //                     default:
    //                         break;
    //                 }
    //             break;
            
    //         case HORIZONTAL:
    //                 switch (direction) {
    //                     case RIGHT:
                            
    //                         break;
                        
    //                     case LEFT:

    //                         break;
    //                     default:
    //                         break;
    //                 }
    //             break;

    //         default:
    //             break;
    //     }
    // }

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
