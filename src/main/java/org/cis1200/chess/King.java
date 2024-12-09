package org.cis1200.chess;

public class King extends Piece{

    public King(int color, int col, int row) {
        super(color, col, row);
        type = Type.KING;
        if(color == ChessGamePanel.PLAYER_WHITE){
            image = getImage("/piece/white-king");
        }else{
            image = getImage("/piece/black-king");
        }
    }
    public boolean movePossible(int targetCol, int targetRow) {
        // Check if the target position is within the board boundaries
        if (!isWithinBoard(targetCol, targetRow)) {
            return false;
        }

        // Check for standard king moves (1 step)
        boolean isSingleStepMove = (Math.abs(targetCol - preCol) + Math.abs(targetRow - preRow) == 1)
                || (Math.abs(targetCol - preCol) == 1 && Math.abs(targetRow - preRow) == 1);

        if (isSingleStepMove && isValidPlace(targetCol, targetRow)) {
            return true;
        }

        // Check for castling
        if (!moved) {
            // Castling to the right
            if (targetCol == preCol + 2 && targetRow == preRow && !onStraightLine(targetCol, targetRow)) {
                Piece rook = findRook(preCol + 3, preRow, false);
                if (rook != null) {
                    ChessGamePanel.castlingPiece = rook;
                    return true;
                }
            }

            // Castling to the left
            if (targetCol == preCol - 2 && targetRow == preRow && !onStraightLine(targetCol, targetRow)) {
                Piece[] rooks = findRooksForLeftCastling(preCol - 3, preCol - 4, targetRow);
                if (rooks[0] == null && rooks[1] != null && !rooks[1].moved) {
                    ChessGamePanel.castlingPiece = rooks[1];
                    return true;
                }
            }
        }

        return false;
    }
    // right castling
    private Piece findRook(int rookCol, int rookRow, boolean mustBeUnmoved) {
        for (Piece piece : ChessGamePanel.temporaryPieces) {
            if (piece.col == rookCol && piece.row == rookRow) {
                if (!mustBeUnmoved || !piece.moved) {
                    return piece;
                }
            }
        }
        return null;
    }

    //left castling
    private Piece[] findRooksForLeftCastling(int rook1Col, int rook2Col, int row) {
        Piece[] rooks = new Piece[2];
        for (Piece piece : ChessGamePanel.temporaryPieces) {
            if (piece.col == rook1Col && piece.row == row) {
                rooks[0] = piece;
            }
            if (piece.col == rook2Col && piece.row == row) {
                rooks[1] = piece;
            }
        }
        return rooks;
    }

}