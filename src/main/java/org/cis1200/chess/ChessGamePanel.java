package org.cis1200.chess;

import javax.swing.*;
import java.awt.*;
import java.util.LinkedList;

public class ChessGamePanel extends JPanel implements Runnable {
    public static final int WIDTH = 1200;
    public static final int HEIGHT = 800;
    final int FPS = 60;
    Thread gameThread;
    private ChessBoard chessBoard = new ChessBoard();
    MouseHandler mouseHandler = new MouseHandler();

    public static LinkedList<Piece> activePieces = new LinkedList<>();
    public static LinkedList<Piece> temporaryPieces = new LinkedList<>();
    LinkedList<Piece> promotionOptions = new LinkedList<>();
    Piece activePiece, checkingPiece;
    public static Piece castlingPiece;

    public static final int PLAYER_WHITE = 0;
    public static final int PLAYER_BLACK = 1;
    int currentColor = PLAYER_WHITE;
    boolean moveAllowed;
    boolean validMoveTarget;
    boolean promotion;
    boolean gameOver;
    boolean staleMate;

    public ChessGamePanel() {
        setPreferredSize(new Dimension(WIDTH, HEIGHT));
        initialisePieces();
        clonePieces(activePieces, temporaryPieces);
        addMouseMotionListener(mouseHandler);
        addMouseListener(mouseHandler);
    }

    public void startGame() {
        gameThread = new Thread(this);
        gameThread.start();
    }

    public void initialisePieces() {
        int[][] pawnRows = {{6, PLAYER_WHITE}, {1, PLAYER_BLACK}};
        int[][] majorPieceRows = {{7, PLAYER_WHITE}, {0, PLAYER_BLACK}};

        for (int[] pawnRow : pawnRows) {
            int row = pawnRow[0];
            int color = pawnRow[1];
            for (int col = 0; col < 8; col++) {
                activePieces.add(new Pawn(color, col, row));
            }
        }

        for (int[] majorPieceRow : majorPieceRows) {
            int row = majorPieceRow[0];
            int color = majorPieceRow[1];

            activePieces.add(new Rook(color, 0, row));
            activePieces.add(new Knight(color, 1, row));
            activePieces.add(new Bishop(color, 2, row));
            activePieces.add(new Queen(color, 3, row));
            activePieces.add(new King(color, 4, row));
            activePieces.add(new Bishop(color, 5, row));
            activePieces.add(new Knight(color, 6, row));
            activePieces.add(new Rook(color, 7, row));
        }
    }

    protected void clonePieces(LinkedList<Piece> source,
                            LinkedList<Piece> target) {
        target.clear();
        for (Piece piece : source) {
            target.add(piece);
        }
    }

    private void refreshGameState() {
        if (promotion) {
            promoting();
        } else if (!gameOver && !staleMate) {
            if (mouseHandler.pressed) {
                if (activePiece == null) {
                    selectActivePiece();
                } else {
                    simulate();
                }
            } else {
                if (activePiece != null) {
                    handlePieceRelease();
                }
            }
        }
    }

    private void selectActivePiece() {
        for (Piece piece : temporaryPieces) {
            if (piece.color == currentColor && piece.col == mouseHandler.x / ChessBoard.SQUARE_SIZE && piece.row == mouseHandler.y / ChessBoard.SQUARE_SIZE) {
                activePiece = piece;
                break;
            }
        }
    }

    private void handlePieceRelease() {
        if (validMoveTarget) {
            updatePiecesAfterMove();
            checkGameEndConditions();
        } else {
            resetPiecePosition();
        }
    }
    private void updatePiecesAfterMove() {
        clonePieces(temporaryPieces, activePieces);  // Copy simulated pieces back to main list
        activePiece.updatePosition();
        if (castlingPiece != null) {
            castlingPiece.updatePosition();
        }
    }

    protected void checkGameEndConditions() {
        if (isKingInCheck() && isCheckmate()) {
            gameOver = true;
        } else if (isStaleMate() && !isKingInCheck()) {
            staleMate = true;
        } else {
            if (canPromote()) {
                promotion = true;
            } else {
                switchPlayer();
            }
        }
    }

    private void resetPiecePosition() {
        clonePieces(activePieces, temporaryPieces);
        activePiece.resetPosition();
        activePiece = null;
    }
    public void simulate() {
        moveAllowed = false;
        validMoveTarget = false;

        clonePieces(activePieces, temporaryPieces);
        handleCastlingCancellation();
        updateActivePiecePosition();

        if (activePiece.movePossible(activePiece.col, activePiece.row)) {
            moveAllowed = true;
            handlePieceCapture();
            validateCastling();
            if (!isIllegal(activePiece) && !opponentCanCaptureKing()) {
                validMoveTarget = true;
            }
        }
    }

    private void handleCastlingCancellation() {
        if (castlingPiece != null) {
            castlingPiece.col = castlingPiece.preCol;
            castlingPiece.x = castlingPiece.getX(castlingPiece.col);
            castlingPiece = null;
        }
    }

    private void updateActivePiecePosition() {
        activePiece.x = mouseHandler.x - ChessBoard.HALF_SQUARE_SIZE;
        activePiece.y = mouseHandler.y - ChessBoard.HALF_SQUARE_SIZE;
        activePiece.col = activePiece.getColumn(activePiece.x);
        activePiece.row = activePiece.getRow(activePiece.y);
    }
    private void handlePieceCapture() {
        if (activePiece.hittingPiece != null) {
            temporaryPieces.remove(activePiece.hittingPiece.getIndex());
        }
    }
    protected void switchPlayer() {
        currentColor = (currentColor == PLAYER_WHITE) ? PLAYER_BLACK : PLAYER_WHITE;
        for (Piece piece : activePieces) {
            if (piece.color == currentColor) {
                piece.twoStepped = false;
            }
        }
        activePiece = null;
    }

    private boolean isIllegal(Piece king){
        if(king.type == Type.KING){
            for(Piece piece : temporaryPieces){
                if(piece != king && piece.color != king.color && piece.movePossible(king.col, king.row)){
                    return true;
                }
            }
        }
        return false;
    }
    public boolean opponentCanCaptureKing(){
        Piece king = getKing(false);
        for(Piece piece : temporaryPieces){
            if(piece.color != king.color && piece.movePossible(king.col, king.row)){
                return true;
            }
        }
        return false;
    }
    public void validateCastling(){
        if(castlingPiece != null){
            if(castlingPiece.col == 0){
                castlingPiece.col += 3;
            }else if(castlingPiece.col == 7){
                castlingPiece.col -= 2;
            }
            castlingPiece.x = castlingPiece.getX(castlingPiece.col);
        }
    }
    private boolean isStaleMate(){
        int count = 0;
        for(Piece piece : temporaryPieces){
            if(piece.color != currentColor){
                count++;
            }
        }
        if(count == 1){ //only one king
            if(!canKingMove(getKing(true))){
                return true;
            }
        }
        return false;
    }
    boolean isKingInCheck() {
        Piece king = getKing(true);
        for (Piece piece : temporaryPieces) {
            if (piece.color != king.color && piece.movePossible(king.col, king.row)) {
                checkingPiece = piece;
                return true;
            }
        }

        checkingPiece = null;
        return false;
    }

    private Piece getKing(boolean opponent) {
        for (Piece piece : temporaryPieces) {
            if (piece.type == Type.KING && (opponent == (piece.color != currentColor))) {
                return piece;
            }
        }
        return null;
    }
    private boolean isCheckmate(){
        Piece king = getKing(true);
        if(canKingMove(king)){
            return false;
        }else{ //chance
            int colDiff = Math.abs(checkingPiece.col - king.col);
            int rowDiff = Math.abs(checkingPiece.row - king.row);

            if (colDiff == 0) { //vertically
                if(checkingPiece.row < king.row){
                    for(int row = checkingPiece.row; row < king.row; row++){
                        for(Piece piece: temporaryPieces){
                            if(piece != king && piece.color != currentColor && piece.movePossible(checkingPiece.col, row)){
                                return false;
                            }
                        }
                    }
                }
                if(checkingPiece.row > king.row){
                    for(int row = checkingPiece.row; row > king.row; row--){
                        for(Piece piece: temporaryPieces){
                            if(piece != king && piece.color != currentColor && piece.movePossible(checkingPiece.col, row)){
                                return false;
                            }
                        }
                    }
                }
            }else if(rowDiff == 0){ // horizontally
                if(checkingPiece.col > king.col){
                    for(int col = checkingPiece.col; col < king.row; col++){
                        for(Piece piece: temporaryPieces){
                            if(piece != king && piece.color != currentColor && piece.movePossible(col, checkingPiece.row)){
                                return false;
                            }
                        }
                    }
                }
                if(checkingPiece.col < king.col){
                    for(int col = checkingPiece.col; col > king.row; col--){
                        for(Piece piece: temporaryPieces){
                            if(piece != king && piece.color != currentColor && piece.movePossible(col, checkingPiece.row)){
                                return false;
                            }
                        }
                    }
                }
            }else if(colDiff == rowDiff){ // diagonally
                if(checkingPiece.row < king.row){ //above king
                    if(checkingPiece.col < king.col){ // upper left
                        for(int col = checkingPiece.col, row = checkingPiece.row; col < king.row; col++, row++){
                            for(Piece piece: temporaryPieces){
                                if(piece != king && piece.color != currentColor && piece.movePossible(col, row)){
                                    return false ;
                                }
                            }
                        }
                    }
                    if(checkingPiece.col > king.col){ // upper right
                        for(int col = checkingPiece.col, row = checkingPiece.row; col > king.row; col--, row++){
                            for(Piece piece: temporaryPieces){
                                if(piece != king && piece.color != currentColor && piece.movePossible(col, row)){
                                    return false ;
                                }
                            }
                        }
                    }
                }
                if(checkingPiece.row > king.row){ //below king
                    if(checkingPiece.col < king.col){ // lower left
                        for(int col = checkingPiece.col, row = checkingPiece.row; col < king.row; col++, row--){
                            for(Piece piece: temporaryPieces){
                                if(piece != king && piece.color != currentColor && piece.movePossible(col, row)){
                                    return false ;
                                }
                            }
                        }
                    }
                    if(checkingPiece.col > king.col){//lower right
                        for(int col = checkingPiece.col, row = checkingPiece.row; col > king.row; col--, row--){
                            for(Piece piece: temporaryPieces){
                                if(piece != king && piece.color != currentColor && piece.movePossible(col, row)){
                                    return false ;
                                }
                            }
                        }
                    }
                }
            }
        }
        return true;
    }
    private boolean canKingMove(Piece king) {
        //all places king can move
        int[][] directions = {
                {-1, -1}, {0, -1}, {1, -1},
                {-1,  0},          {1,  0},
                {-1,  1}, {0,  1}, {1,  1}
        };
        for (int[] direction : directions) {
            if (isValidMove(king, direction[0], direction[1])) {
                return true;
            }
        }
        return false;
    }
    private boolean isValidMove(Piece king, int colOffset, int rowOffset) {
        boolean isValid = false;
        king.col += colOffset;
        king.row += rowOffset;
        if (king.movePossible(king.col, king.row)) {
            if (king.hittingPiece != null) {
                temporaryPieces.remove(king.hittingPiece.getIndex());
            }
            if (!isIllegal(king)) {
                isValid = true;
            }
        }
        king.resetPosition();
        clonePieces(activePieces, temporaryPieces);
        return isValid;
    }
    boolean canPromote(){
        if(activePiece.type == Type.PAWN){
            if(currentColor == PLAYER_WHITE && activePiece.row == 0 || currentColor == PLAYER_BLACK && activePiece.row == 7){
                promotionOptions.clear();
                promotionOptions.add(new Rook(currentColor, 9,2 ));
                promotionOptions.add(new Knight(currentColor, 9, 3));
                promotionOptions.add(new Bishop(currentColor, 9, 4));
                promotionOptions.add(new Queen(currentColor, 9, 5));
                return true;
            }
        }
        return false;
    }
    public void promoting() {
        if (mouseHandler.pressed) {
            Piece selectedPiece = getSelectedPromoPiece();
            if (selectedPiece != null) {
                promotePiece(selectedPiece);
                resetPromotion();
            }
        }
    }

    private Piece getSelectedPromoPiece() {
        for (Piece piece : promotionOptions) {
            if (piece.col == mouseHandler.x / ChessBoard.SQUARE_SIZE && piece.row == mouseHandler.y / ChessBoard.SQUARE_SIZE) {
                return piece;
            }
        }
        return null;
    }

    void promotePiece(Piece promoPiece) {
        Piece promoted = null;
        switch (promoPiece.type) {
            case ROOK:
                promoted = new Rook(currentColor, activePiece.col, activePiece.row);
                break;
            case KNIGHT:
                promoted = new Knight(currentColor, activePiece.col, activePiece.row);
                break;
            case BISHOP:
                promoted = new Bishop(currentColor, activePiece.col, activePiece.row);
                break;
            case QUEEN:
                promoted = new Queen(currentColor, activePiece.col, activePiece.row);
                break;
            default:
                break;
        }

        if (promoted != null) {
            int index = activePieces.indexOf(activePiece);
            activePieces.set(index, promoted);
            int tempIndex = temporaryPieces.indexOf(activePiece);
            temporaryPieces.set(tempIndex, promoted);
        }
    }

    private void resetPromotion() {
        clonePieces(temporaryPieces, activePieces);
        activePiece = null;
        promotion = false;
        switchPlayer();
    }
    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        chessBoard.draw(g2);
        for (Piece p : temporaryPieces) {
            p.draw(g2);
        }

        if (activePiece != null) {
            if (moveAllowed) {
                if (isIllegal(activePiece) || opponentCanCaptureKing()) {
                    g2.setColor(Color.GRAY);
                    g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.7f));
                } else {
                    g2.setColor(Color.WHITE);
                    g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.7f));
                }
                g2.fillRect(activePiece.col * ChessBoard.SQUARE_SIZE,
                        activePiece.row * ChessBoard.SQUARE_SIZE, ChessBoard.SQUARE_SIZE, ChessBoard.SQUARE_SIZE);
                g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1f));
            }
            activePiece.draw(g2);
        }

        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        g2.setColor(new Color(30, 30, 30));
        g2.fillRect(801, 0, 400, getHeight());

        g2.setColor(Color.WHITE);
        g2.setFont(new Font("Book Antiqua", Font.PLAIN, 30));
        g2.drawString("Game Status", 830, 50);

        g2.setFont(new Font("Book Antiqua", Font.PLAIN, 20));
        int messageY = 100; // Starting Y-coordinate for messages
        int lineSpacing = 30;

        if (promotion) {
            g2.setColor(Color.YELLOW);
            g2.drawString("Promote to:", 830, messageY);
            messageY += lineSpacing + 10;

            for (Piece piece : promotionOptions) {
                g2.drawImage(piece.image, piece.getX(piece.col),
                        piece.getY(piece.row), ChessBoard.SQUARE_SIZE, ChessBoard.SQUARE_SIZE, null);
            }
        } else {
            g2.setColor(Color.WHITE);
            if (currentColor == PLAYER_WHITE) {
                g2.drawString("White's Turn", 830, messageY);
                messageY += lineSpacing;

                if (checkingPiece != null && checkingPiece.color == PLAYER_BLACK) {
                    g2.setColor(Color.RED);
                    g2.drawString("The King is in Check!", 830, messageY);
                    messageY += lineSpacing;
                }
            } else {
                g2.drawString("Black's Turn", 830, messageY);
                messageY += lineSpacing;

                if (checkingPiece != null && checkingPiece.color == PLAYER_WHITE) {
                    g2.setColor(Color.RED);
                    g2.drawString("The King is in Check!", 830, messageY);
                    messageY += lineSpacing;
                }
            }
        }
        if (gameOver) {
            g2.setFont(new Font("Book Antiqua", Font.BOLD, 30));
            g2.setColor(Color.PINK);
            g2.drawString(currentColor == PLAYER_WHITE ? "White Wins" : "Black Wins", 830, messageY);
            messageY += lineSpacing;
        } else if (staleMate) {
            g2.setFont(new Font("Book Antiqua", Font.BOLD, 30));
            g2.setColor(Color.PINK);
            g2.drawString("Stalemate", 830, messageY);
            messageY += lineSpacing;
        }
    }



    @Override
    public void run() {
        double drawInterval = 1000000000/FPS;
        double delta = 0;
        long lastTime = System.nanoTime();
        long currentTime;

        while(gameThread != null){
            currentTime = System.nanoTime();
            delta += (currentTime - lastTime)/drawInterval;
            lastTime = currentTime;

            if(delta >= 1){
                refreshGameState();
                repaint();
                delta--;
            }
        }

    }
}