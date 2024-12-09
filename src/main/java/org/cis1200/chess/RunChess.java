package org.cis1200.chess;

import javax.swing.*;

public class RunChess implements Runnable {
    @Override
    public void run() {
        JFrame frame = new JFrame("Chess");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setResizable(false);
        ChessGamePanel gp = new ChessGamePanel();
        frame.add(gp);
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);

        gp.startGame();
    }
}
