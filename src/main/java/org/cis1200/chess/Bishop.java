package org.cis1200.chess;

public class Bishop extends Piece {

    public Bishop(int color, int col, int row) {
        super(color, col, row);
        type = Type.BISHOP;
        if (color == ChessGamePanel.PLAYER_WHITE) {
            image = getImage("/piece/white-bishop");
        } else {
            image = getImage("/piece/black-bishop");
        }
    }

    public boolean movePossible(int targetCol, int targetRow) {
        if (!isWithinBoard(targetCol, targetRow) || isSameSquare(targetCol, targetRow)) {
            return false;
        }
        boolean isDiagonalMove = Math.abs(targetCol - preCol) == Math.abs(targetRow - preRow);

        if (isDiagonalMove) {
            boolean isSquareValid = isValidPlace(targetCol, targetRow);
            boolean isPathClear = !OnDiagonal(targetCol, targetRow);
            return isSquareValid && isPathClear;
        }
        return false;
    }

}
