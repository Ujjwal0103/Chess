package org.cis1200.chess;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class StartMenu implements Runnable {
    @Override
    public void run() {
        JFrame frame = new JFrame("Welcome to Chess by Ujjwal Rastogi");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setResizable(false);
        frame.setSize(600, 400);
        frame.setLayout(new BorderLayout());

        JLabel titleLabel = new JLabel("Ujjwal Rastogi's Final Chess Project", JLabel.CENTER);
        titleLabel.setFont(new Font("Serif", Font.BOLD, 32));
        titleLabel.setBorder(BorderFactory.createEmptyBorder(20, 10, 20, 10));

        JTextArea description = new JTextArea(
                "Welcome to this untimed Chess game! \n"
                        + "All standard functionality has been implemented, including moves, captures, and special rules. \n"
                        + "You just have to drag and drop pieces using your " +
                        "cursor in order to play the game" +
                        " \n" +
                        "Enjoy your game, and challenge your friends or hone your skills."
        );
        description.setFont(new Font("SansSerif", Font.PLAIN, 16));
        description.setEditable(false);
        description.setFocusable(false);
        description.setOpaque(false);
        description.setLineWrap(true);
        description.setWrapStyleWord(true);
        description.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));

        JButton startButton = new JButton("Start Game");
        startButton.setFont(new Font("SansSerif", Font.BOLD, 20));
        startButton.setFocusPainted(false);
        startButton.setForeground(Color.BLACK);
        startButton.setPreferredSize(new Dimension(200, 50));
        startButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        startButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                frame.dispose();
                SwingUtilities.invokeLater(new RunChess());
            }
        });

        JPanel centerPanel = new JPanel();
        centerPanel.setLayout(new BoxLayout(centerPanel, BoxLayout.Y_AXIS));
        centerPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        centerPanel.add(description);
        centerPanel.add(Box.createRigidArea(new Dimension(0, 20)));
        centerPanel.add(startButton);
        frame.add(titleLabel, BorderLayout.NORTH);
        frame.add(centerPanel, BorderLayout.CENTER);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }
}
