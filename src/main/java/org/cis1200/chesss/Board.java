package org.cis1200.chesss;

import java.awt.*;

public class Board {
    final int MAX_COL = 8;
    final int MAX_ROW = 8;
    public static final int SQUARE_SIZE = 100;
    public static final int HALF_SQUARE_SIZE = SQUARE_SIZE / 2;

    private final Color[][] boardColors = new Color[MAX_ROW][MAX_COL];

    public Board() {
        boolean isLight = true;
        for (int row = 0; row < MAX_ROW; row++) {
            for (int col = 0; col < MAX_COL; col++) {
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
        for (int row = 0; row < MAX_ROW; row++) {
            for (int col = 0; col < MAX_COL; col++) {
                g2.setColor(boardColors[row][col]);
                g2.fillRect(col * SQUARE_SIZE, row * SQUARE_SIZE, SQUARE_SIZE, SQUARE_SIZE);
            }
        }
    }
}