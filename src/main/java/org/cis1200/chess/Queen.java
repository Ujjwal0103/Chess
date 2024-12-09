package org.cis1200.chess;

public class Queen extends Piece {

    public Queen(int color, int col, int row) {
        super(color, col, row);
        type = Type.QUEEN;
        if (color == ChessGamePanel.PLAYER_WHITE) {
            image = getImage("/piece/white-queen");
        } else {
            image = getImage("/piece/black-queen");
        }
    }

    public boolean movePossible(int targetCol, int targetRow) {
        // Validate that it is within the board
        if (!isWithinBoard(targetCol, targetRow) || isSameSquare(targetCol, targetRow)) {
            return false;
        }
        // straight line moves
        boolean isStraightMove = (targetCol == preCol || targetRow == preRow);
        if (isStraightMove) {
            if (isValidPlace(targetCol, targetRow) && !onStraightLine(targetCol, targetRow)) {
                return true;
            }
        }

        // diagonal moves
        boolean isDiagonalMove = Math.abs(targetCol - preCol) == Math.abs(targetRow - preRow);
        if (isDiagonalMove) {
            if (isValidPlace(targetCol, targetRow) && !OnDiagonal(targetCol, targetRow)) {
                return true;
            }
        }
        return false;
    }

}
