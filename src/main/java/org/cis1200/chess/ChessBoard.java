package org.cis1200.chess;

import java.awt.*;

public class ChessBoard {
    final int maxCol = 8;
    final int maxRow = 8;
    public static final int SQUARE_SIZE = 100;
    public static final int HALF_SQUARE_SIZE = SQUARE_SIZE / 2;

    private final Color[][] boardColors = new Color[maxRow][maxCol];

    public ChessBoard() {
        boolean isLight = true;
        for (int row = 0; row < maxRow; row++) {
            for (int col = 0; col < maxCol; col++) {
                if (isLight) {
                    boardColors[row][col] = new Color(236, 240, 206);
                } else {
                    boardColors[row][col] = new Color(119, 149, 86);
                }
                isLight = !isLight;
            }
            isLight = !isLight;
        }
    }

    public void draw(Graphics2D g2) {
        for (int row = 0; row < maxRow; row++) {
            for (int col = 0; col < maxCol; col++) {
                g2.setColor(boardColors[row][col]);
                g2.fillRect(col * SQUARE_SIZE, row * SQUARE_SIZE, SQUARE_SIZE, SQUARE_SIZE);
            }
        }
    }
}