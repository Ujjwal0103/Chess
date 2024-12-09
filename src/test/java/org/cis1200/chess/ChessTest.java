package org.cis1200.chess;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class ChessTest {

    @Test
    void testSwitchPlayer() {
        ChessGamePanel panel = new ChessGamePanel();
        assertEquals(ChessGamePanel.PLAYER_WHITE, panel.currentColor, "Initial player should be white.");
        panel.switchPlayer();
        assertEquals(ChessGamePanel.PLAYER_BLACK, panel.currentColor, "Player should switch to black.");
        panel.switchPlayer();
        assertEquals(ChessGamePanel.PLAYER_WHITE, panel.currentColor, "Player should switch back to white.");
    }
    @Test
    void testPromotionEligibility() {
        ChessGamePanel panel = new ChessGamePanel();
        ChessGamePanel.activePieces.clear();
        Pawn whitePawn = new Pawn(ChessGamePanel.PLAYER_WHITE, 0, 0);
        Pawn blackPawn = new Pawn(ChessGamePanel.PLAYER_BLACK, 0, 7);

        ChessGamePanel.activePieces.add(whitePawn);
        ChessGamePanel.activePieces.add(blackPawn);

        panel.activePiece = whitePawn;
        assertTrue(panel.canPromote(), "White pawn should be eligible for promotion.");

        panel.activePiece = blackPawn;
        panel.currentColor = ChessGamePanel.PLAYER_BLACK;
        assertTrue(panel.canPromote(), "Black pawn should be eligible for promotion.");
    }
    @Test
    void testGameOver() {
        ChessGamePanel panel = new ChessGamePanel();

        King blackKing = new King(ChessGamePanel.PLAYER_BLACK, 0, 0);
        Queen whiteQueen = new Queen(ChessGamePanel.PLAYER_WHITE, 1, 1);
        Rook whiteRook = new Rook(ChessGamePanel.PLAYER_WHITE, 0, 1);

        ChessGamePanel.activePieces.clear();
        ChessGamePanel.activePieces.add(blackKing);
        ChessGamePanel.activePieces.add(whiteQueen);
        ChessGamePanel.activePieces.add(whiteRook);

        panel.clonePieces(ChessGamePanel.activePieces, ChessGamePanel.temporaryPieces);
        panel.checkGameEndConditions();
        assertTrue(panel.gameOver, "Game should be over due to checkmate.");
    }


}
