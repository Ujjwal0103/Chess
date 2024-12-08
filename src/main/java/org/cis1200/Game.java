package org.cis1200;

import org.cis1200.chess.StartMenu;
import org.cis1200.chesss.GamePanel;

import javax.swing.JFrame;

//public class Game {
//    /**
//     * Main method run to start and run the game. Initializes the runnable game
//     * class of your choosing and runs it. IMPORTANT: Do NOT delete! You MUST
//     * include a main method in your final submission.
//     */
//    public static void main(String[] args) {
//        // Set the game you want to run here
//        Runnable game = new org.cis1200.tictactoe.RunTicTacToe();
//
//        SwingUtilities.invokeLater(game);
//    }
//}

//public class Game implements Runnable {
//    public void run() {
//        SwingUtilities.invokeLater(new StartMenu());
//    }
//
//    public static void main(String[] args) {
//        SwingUtilities.invokeLater(new Game());
//    }
//}

public class Game{
    public static void main(String[] args) {
        JFrame window = new JFrame("Chess");
        window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        window.setResizable(false);
        GamePanel gp = new GamePanel();
        window.add(gp);
        window.pack();
        window.setLocationRelativeTo(null);
        window.setVisible(true);

        gp.launchGame();

    }
}