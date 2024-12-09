package org.cis1200.chess;

public class Pawn extends Piece{

    public Pawn(int color, int col, int row) {
        super(color, col, row);
        type = Type.PAWN;
        if(color == ChessGamePanel.PLAYER_WHITE){
            image = getImage("/piece/white-pawn");
        }else{
            image = getImage("/piece/black-pawn");
        }
    }
    public boolean movePossible(int targetCol, int targetRow) {
        if (!isWithinBoard(targetCol, targetRow) || isSameSquare(targetCol, targetRow)) {
            return false;
        }

        int moveDirection = (color == ChessGamePanel.PLAYER_WHITE) ? -1 : 1;
        hittingPiece = getHittingPiece(targetCol, targetRow);

        // Single step move
        boolean isSingleStepMove = (targetCol == preCol && targetRow == preRow + moveDirection && hittingPiece == null);
        if (isSingleStepMove) {
            return true; // 1
        }

        // Double step move
        boolean isDoubleStepMove = (targetCol == preCol && targetRow == preRow + moveDirection * 2 && hittingPiece == null && !moved && !onStraightLine(targetCol, targetRow));
        if (isDoubleStepMove) {
            return true; // 2
        }

        // Diagonal capture
        boolean isDiagonalCapture = (Math.abs(targetCol - preCol) == 1 && targetRow == preRow + moveDirection && hittingPiece != null && hittingPiece.color != color);
        if (isDiagonalCapture) {
            return true;
        }

        // En passant
        boolean isEnPassant = (Math.abs(targetCol - preCol) == 1 && targetRow == preRow + moveDirection);
        if (isEnPassant) {
            for (Piece piece : ChessGamePanel.temporaryPieces) {
                if (piece.col == targetCol && piece.row == preRow && piece.twoStepped) {
                    hittingPiece = piece;
                    return true;
                }
            }
        }

        return false;
    }

}
