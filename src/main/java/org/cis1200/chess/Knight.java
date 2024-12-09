package org.cis1200.chess;

public class Knight extends Piece {

    public Knight(int color, int col, int row) {
        super(color, col, row);
        type = Type.KNIGHT;
        if (color == ChessGamePanel.PLAYER_WHITE) {
            image = getImage("/piece/white-knight");
        } else {
            image = getImage("/piece/black-knight");
        }
    }

    public boolean movePossible(int targetCol, int targetRow) {
        // Check if within board boundaries
        if (!isWithinBoard(targetCol, targetRow)) {
            return false;
        }
        // Validate with knight movement
        boolean isKnightMove = Math.abs(targetCol - preCol) == 2
                && Math.abs(targetRow - preRow) == 1
                || Math.abs(targetCol - preCol) == 1 && Math.abs(targetRow - preRow) == 2;
        if (isKnightMove && isValidPlace(targetCol, targetRow)) {
            return true;
        }
        return false;
    }

}
