package org.cis1200.chess;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;

public class Piece {
    public Type type;
    public BufferedImage image;
    public int x, y;
    public int col, row, preCol, preRow;
    public int color;
    public Piece hittingPiece;
    public boolean moved, twoStepped;

    public Piece(int color, int col, int row) {
        this.color = color;
        this.col = col;
        this.row = row;
        x = getX(col);
        y = getY(row);
        preCol = col;
        preRow = row;

    }

    public BufferedImage getImage(String imagePath) {
        BufferedImage image = null;
        try {
            image = ImageIO.read(
                    getClass().getResourceAsStream(
                            imagePath +
                                    ".png"
                    )
            );
        } catch (IOException e) {
            e.printStackTrace();
        }
        return image;

    }

    public int getX(int col) {
        return col * ChessBoard.SQUARE_SIZE;
    }

    public int getY(int row) {
        return row * ChessBoard.SQUARE_SIZE;
    }

    public int getColumn(int x) {
        return (x + ChessBoard.HALF_SQUARE_SIZE) / ChessBoard.SQUARE_SIZE;
    }

    public int getRow(int y) {
        return (y + ChessBoard.HALF_SQUARE_SIZE) / ChessBoard.SQUARE_SIZE;
    }

    public int getIndex() {
        for (int index = 0; index < ChessGamePanel.temporaryPieces.size(); index++) {
            if (ChessGamePanel.temporaryPieces.get(index) == this) {
                return index;
            }
        }
        return 0;
    }

    public void updatePosition() {
        if (type == Type.PAWN) { // enpassant
            if (Math.abs(row - preRow) == 2) {
                twoStepped = true;
            }
        }
        x = getX(col);
        y = getY(row);
        preCol = getColumn(x);
        preRow = getRow(y);
        moved = true;
    }

    public void resetPosition() {
        col = preCol;
        row = preRow;
        x = getX(col);
        y = getY(row);
    }

    public boolean movePossible(int targetCol, int targetRow) {
        return false;
    }

    public boolean isWithinBoard(int targetCol, int targetRow) {
        return targetCol >= 0 && targetCol <= 7 && targetRow >= 0 && targetRow <= 7;
    }

    public boolean isSameSquare(int targetCol, int targetRow) {
        return targetCol == preCol && targetRow == preRow;
    }

    public Piece getHittingPiece(int targetCol, int targetRow) {
        for (Piece piece : ChessGamePanel.temporaryPieces) {
            if (piece.col == targetCol && piece.row == targetRow && piece != this) {
                return piece;
            }
        }
        return null;
    }

    public boolean isValidPlace(int targetCol, int targetRow) {
        hittingPiece = getHittingPiece(targetCol, targetRow);
        if (hittingPiece == null) {
            return true;
        } else {
            if (hittingPiece.color != this.color) {
                return true;
            } else {
                hittingPiece = null;
            }
        }
        return false;
    }

    public boolean onStraightLine(int targetCol, int targetRow) {
        int colStep = Integer.compare(targetCol, preCol);
        int rowStep = Integer.compare(targetRow, preRow);
        int col = preCol + colStep;
        int row = preRow + rowStep;
        while (col != targetCol || row != targetRow) {
            for (Piece piece : ChessGamePanel.temporaryPieces) {
                if (piece.col == col && piece.row == row) {
                    hittingPiece = piece;
                    return true;
                }
            }
            if (col != targetCol) {
                col += colStep;
            }
            if (row != targetRow) {
                row += rowStep;
            }
        }

        return false;
    }

    public boolean OnDiagonal(int targetCol, int targetRow) {
        int colStep = (targetCol > preCol) ? 1 : -1; // column direction
        int rowStep = (targetRow > preRow) ? 1 : -1; // row direction

        int col = preCol + colStep;
        int row = preRow + rowStep;

        while (col != targetCol && row != targetRow) {
            for (Piece piece : ChessGamePanel.temporaryPieces) {
                if (piece.col == col && piece.row == row) {
                    hittingPiece = piece;
                    return true;
                }
            }
            col += colStep;
            row += rowStep;
        }

        return false;
    }

    public void draw(Graphics2D g2) {
        g2.drawImage(image, x, y, ChessBoard.SQUARE_SIZE, ChessBoard.SQUARE_SIZE, null);
    }

}