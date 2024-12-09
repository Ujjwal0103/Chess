package org.cis1200.chess;

public class Rook extends Piece {

    public Rook(int color, int col, int row) {
        super(color, col, row);
        type = Type.ROOK;
        if (color == ChessGamePanel.PLAYER_WHITE) {
            image = getImage("/piece/white-rook");
        } else {
            image = getImage("/piece/black-rook");
        }
    }

    public boolean movePossible(int targetCol, int targetRow) {
        if (!isWithinBoard(targetCol, targetRow) || isSameSquare(targetCol, targetRow)) {
            return false;
        }
        boolean isStraightLineMove = (targetCol == preCol || targetRow == preRow);
        if (isStraightLineMove) {
            boolean isPathClear = !onStraightLine(targetCol, targetRow);
            boolean isSquareValid = isValidPlace(targetCol, targetRow);
            return isSquareValid && isPathClear;
        }

        return false;
    }
}