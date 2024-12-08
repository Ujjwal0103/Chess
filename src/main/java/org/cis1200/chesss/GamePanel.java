package org.cis1200.chesss;

import javax.swing.*;
import java.awt.*;
import java.util.LinkedList;

public class GamePanel extends JPanel implements Runnable {
    public static final int WIDTH = 1200;
    public static final int HEIGHT = 800;
    final int FPS = 60;
    Thread gameThread;
    Board board = new Board();
    Mouse mouse = new Mouse();

    public static LinkedList<Piece> pieces = new LinkedList<>();
    public static LinkedList<Piece> simPieces = new LinkedList<>();
    LinkedList<Piece> promoPieces = new LinkedList<>();
    Piece activeP, checkingP;
    public static Piece castlingP;

    public static final int WHITE = 0;
    public static final int BLACK = 1;
    int currentColor = WHITE;
    boolean canMove;
    boolean validSquare;
    boolean promotion;
    boolean gameOver;
    boolean staleMate;

    public GamePanel() {
        setPreferredSize(new Dimension(WIDTH, HEIGHT));
        setPieces();
        copyPieces(pieces, simPieces);
        addMouseMotionListener(mouse);
        addMouseListener(mouse);
    }

    public void launchGame() {
        gameThread = new Thread(this);
        gameThread.start(); // calling run method
    }

    public void setPieces() {
        int[][] pawnRows = {{6, WHITE}, {1, BLACK}};
        int[][] majorPieceRows = {{7, WHITE}, {0, BLACK}};

        for (int[] pawnRow : pawnRows) {
            int row = pawnRow[0];
            int color = pawnRow[1];
            for (int col = 0; col < 8; col++) {
                pieces.add(new Pawn(color, col, row));
            }
        }

        for (int[] majorPieceRow : majorPieceRows) {
            int row = majorPieceRow[0];
            int color = majorPieceRow[1];

            pieces.add(new Rook(color, 0, row));
            pieces.add(new Knight(color, 1, row));
            pieces.add(new Bishop(color, 2, row));
            pieces.add(new Queen(color, 3, row));
            pieces.add(new King(color, 4, row));
            pieces.add(new Bishop(color, 5, row));
            pieces.add(new Knight(color, 6, row));
            pieces.add(new Rook(color, 7, row));
        }
    }

    private void copyPieces(LinkedList<Piece> source, LinkedList<Piece> target) {
        target.clear();
        for (Piece piece : source) {
            target.add(piece);
        }
    }

    private void update() {
        if (promotion) {
            // Handle piece promotion
            promoting();
        } else if (!gameOver && !staleMate) {
            if (mouse.pressed) {
                if (activeP == null) {
                    // Select active piece if clicked on a piece of the correct color
                    selectActivePiece();
                } else {
                    // Simulate the piece's movement while dragging
                    simulate();
                }
            } else {
                if (activeP != null) {
                    // Handle the release of the mouse button
                    handlePieceRelease();
                }
            }
        }
    }

    // Select the piece that matches the mouse position and is of the current player's color
    private void selectActivePiece() {
        for (Piece piece : simPieces) {
            if (piece.color == currentColor && piece.col == mouse.x / Board.SQUARE_SIZE && piece.row == mouse.y / Board.SQUARE_SIZE) {
                activeP = piece;
                break;
            }
        }
    }

    // Handle the release of the active piece
    private void handlePieceRelease() {
        if (validSquare) {
            // Update pieces after a valid move
            updatePiecesAfterMove();
            // Check for checkmate or stalemate
            checkGameEndConditions();
        } else {
            // Reset piece position if the move is invalid
            resetPiecePosition();
        }
    }

    // Update the pieces after a valid move
    private void updatePiecesAfterMove() {
        copyPieces(simPieces, pieces);  // Copy simulated pieces back to main list
        activeP.updatePosition();
        if (castlingP != null) {
            castlingP.updatePosition();
        }
    }

    // Check if the game should end due to checkmate or stalemate
    private void checkGameEndConditions() {
        if (isKingInCheck() && isCheckmate()) {
            gameOver = true;
        } else if (isStaleMate() && !isKingInCheck()) {
            staleMate = true;
        } else {
            // If the game is still ongoing, check for promotion or change the player
            if (canPromote()) {
                promotion = true;
            } else {
                changePlayer();
            }
        }
    }

    // Reset the active piece position if the move is invalid
    private void resetPiecePosition() {
        copyPieces(pieces, simPieces);  // Restore original piece positions
        activeP.resetPosition();
        activeP = null;
    }
    public void simulate(){// update position
        canMove = false;
        validSquare = false;

        copyPieces(pieces, simPieces);

        if(castlingP != null){ //cancelled castle
            castlingP.col = castlingP.preCol;
            castlingP.x = castlingP.getX(castlingP.col);
            castlingP = null;
        }

        activeP.x = mouse.x - Board.HALF_SQUARE_SIZE;
        activeP.y = mouse.y - Board.HALF_SQUARE_SIZE;
        activeP.col = activeP.getCol(activeP.x);
        activeP.row = activeP.getRow(activeP.y);
        //check if right square
        if(activeP.canMove(activeP.col, activeP.row)){
            canMove = true;
            if(activeP.hittingP != null){
                simPieces.remove(activeP.hittingP.getIndex());
            }
            checkCastling();
            if(!isIllegal(activeP) && !opponentCanCaptureKing()){
                validSquare = true;
            }
        }
    }
    private void changePlayer(){
        if(currentColor == WHITE){
            currentColor = BLACK;
            for(Piece piece : pieces){
                if(piece.color == BLACK){
                    piece.twoStepped = false;
                }
            }
        }
        else {
            currentColor = WHITE;
            for(Piece piece : pieces){
                if(piece.color == WHITE){
                    piece.twoStepped = false;
                }
            }
        }
        activeP = null;
    }
    private boolean isIllegal(Piece king){
        if(king.type == Type.KING){
            for(Piece piece : simPieces){
                if(piece != king && piece.color != king.color && piece.canMove(king.col, king.row)){
                    return true;
                }
            }
        }
        return false;
    }
    public boolean opponentCanCaptureKing(){
        Piece king = getKing(false);
        for(Piece piece : simPieces){
            if(piece.color != king.color && piece.canMove(king.col, king.row)){
                return true;
            }
        }
        return false;
    }
    public void checkCastling(){
        if(castlingP != null){
            if(castlingP.col == 0){
                castlingP.col += 3;
            }else if(castlingP.col == 7){
                castlingP.col -= 2;
            }
            castlingP.x = castlingP.getX(castlingP.col);
        }
    }
    private boolean isStaleMate(){
        int count = 0;
        for(Piece piece : simPieces){
            if(piece.color != currentColor){
                count++;
            }
        }
        if(count == 1){ //only one king
            if(!kingCanMove(getKing(true))){
                return true;
            }
        }
        return false;
    }
    private boolean isKingInCheck(){
        Piece king = getKing(true);
        if(activeP.canMove(king.col, king.row)){
            checkingP = activeP;
            return true;
        }else{
            checkingP = null;
        }
        return false;
    }
    private Piece getKing(boolean opponent){
        Piece king = null;
        for(Piece piece : simPieces){
            if(opponent){
                if(piece.type == Type.KING && piece.color != currentColor){
                    king = piece;
                }
            }
            else{
                if(piece.type == Type.KING && piece.color == currentColor){
                    king = piece;
                }
            }
        }
        return king;
    }
    private boolean isCheckmate(){
        Piece king = getKing(true);
        if(kingCanMove(king)){
            return false;
        }else{ //chance
            int colDiff = Math.abs(checkingP.col - king.col);
            int rowDiff = Math.abs(checkingP.row - king.row);

            if (colDiff == 0) { //vertically
                if(checkingP.row < king.row){
                    for(int row = checkingP.row; row < king.row; row++){
                        for(Piece piece: simPieces){
                            if(piece != king && piece.color != currentColor && piece.canMove(checkingP.col, row)){
                                return false;
                            }
                        }
                    }
                }
                if(checkingP.row > king.row){
                    for(int row = checkingP.row; row > king.row; row--){
                        for(Piece piece: simPieces){
                            if(piece != king && piece.color != currentColor && piece.canMove(checkingP.col, row)){
                                return false;
                            }
                        }
                    }
                }
            }else if(rowDiff == 0){ // horizontally
                if(checkingP.col > king.col){
                    for(int col = checkingP.col; col < king.row; col++){
                        for(Piece piece: simPieces){
                            if(piece != king && piece.color != currentColor && piece.canMove(col, checkingP.row)){
                                return false;
                            }
                        }
                    }
                }
                if(checkingP.col < king.col){
                    for(int col = checkingP.col; col > king.row; col--){
                        for(Piece piece: simPieces){
                            if(piece != king && piece.color != currentColor && piece.canMove(col, checkingP.row)){
                                return false;
                            }
                        }
                    }
                }
            }else if(colDiff == rowDiff){ // diagonally
                if(checkingP.row < king.row){ //above king
                    if(checkingP.col < king.col){ // upper left
                        for(int col = checkingP.col, row = checkingP.row; col < king.row; col++, row++){
                            for(Piece piece: simPieces){
                                if(piece != king && piece.color != currentColor && piece.canMove(col, row)){
                                    return false ;
                                }
                            }
                        }
                    }
                    if(checkingP.col > king.col){ // upper right
                        for(int col = checkingP.col, row = checkingP.row; col > king.row; col--, row++){
                            for(Piece piece: simPieces){
                                if(piece != king && piece.color != currentColor && piece.canMove(col, row)){
                                    return false ;
                                }
                            }
                        }
                    }
                }
                if(checkingP.row > king.row){ //below king
                    if(checkingP.col < king.col){ // lower left
                        for(int col = checkingP.col, row = checkingP.row; col < king.row; col++, row--){
                            for(Piece piece: simPieces){
                                if(piece != king && piece.color != currentColor && piece.canMove(col, row)){
                                    return false ;
                                }
                            }
                        }
                    }
                    if(checkingP.col > king.col){//lower right
                        for(int col = checkingP.col, row = checkingP.row; col > king.row; col--, row--){
                            for(Piece piece: simPieces){
                                if(piece != king && piece.color != currentColor && piece.canMove(col, row)){
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
    private boolean kingCanMove(Piece king){
        if (isValidMove(king, -1, -1)) { return true; }
        if (isValidMove(king,  0, -1)) { return true; }
        if (isValidMove(king,  1, -1)) { return true; }
        if (isValidMove(king, -1,  0)) { return true; }
        if (isValidMove(king,  1,  0)) { return true; }
        if (isValidMove(king, -1,  1)) { return true; }
        if (isValidMove(king,  0,  1)) { return true; }
        if (isValidMove(king,  1,  1)) { return true; }
        return false;
    }
    private boolean isValidMove(Piece king, int colPlus, int rowPlus){
        boolean isValidMove = false;
        king.col += colPlus;
        king.row += rowPlus;

        if(king.canMove(king.col, king.row)){
            if(king.hittingP != null){
                simPieces.remove(king.hittingP.getIndex());
            }
            if(!isIllegal(king)){
                isValidMove = true;
            }
        }
        king.resetPosition();
        copyPieces(pieces, simPieces);
        return isValidMove;
    }
    private boolean canPromote(){
        if(activeP.type == Type.PAWN){
            if(currentColor == WHITE && activeP.row == 0 || currentColor == BLACK && activeP.row == 7){
                promoPieces.clear();
                promoPieces.add(new Rook(currentColor, 9,2 ));
                promoPieces.add(new Knight(currentColor, 9, 3));
                promoPieces.add(new Bishop(currentColor, 9, 4));
                promoPieces.add(new Queen(currentColor, 9, 5));
                return true;
            }
        }
        return false;
    }
    public void promoting(){
        if(mouse.pressed){
            for(Piece piece: promoPieces){
                if(piece.col == mouse.x/Board.SQUARE_SIZE && piece.row == mouse.y/Board.SQUARE_SIZE){
                    switch(piece.type){
                        case ROOK: simPieces.add(new Rook(currentColor,
                                activeP.col, activeP.row)); break;
                        case KNIGHT: simPieces.add(new Knight(currentColor,
                                activeP.col, activeP.row)); break;
                        case BISHOP: simPieces.add(new Bishop(currentColor,
                                activeP.col, activeP.row)); break;
                        case QUEEN: simPieces.add(new Queen(currentColor, activeP.col, activeP.row)); break;
                        default: break;

                    }
                    simPieces.remove(activeP.getIndex());
                    copyPieces(simPieces, pieces);
                    activeP  = null;
                    promotion = false;
                    changePlayer();
                }
            }
        }
    }
    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;

        // Draw the chessboard and pieces
        board.draw(g2);
        for (Piece p : simPieces) {
            p.draw(g2);
        }

        // Highlighting active piece
        if (activeP != null) {
            if (canMove) {
                if (isIllegal(activeP) || opponentCanCaptureKing()) {
                    g2.setColor(Color.GRAY);
                    g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.7f));
                } else {
                    g2.setColor(Color.WHITE);
                    g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.7f));
                }
                g2.fillRect(activeP.col * Board.SQUARE_SIZE,
                        activeP.row * Board.SQUARE_SIZE, Board.SQUARE_SIZE, Board.SQUARE_SIZE);
                g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1f));
            }
            activeP.draw(g2);
        }

        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // Draw the right-side message panel
        g2.setColor(new Color(30, 30, 30)); // Dark gray background
        g2.fillRect(801, 0, 400, getHeight()); // Right panel dimensions

        g2.setColor(Color.WHITE);
        g2.setFont(new Font("Book Antiqua", Font.PLAIN, 30));
        g2.drawString("Game Status", 830, 50);

        // Draw the message content
        g2.setFont(new Font("Book Antiqua", Font.PLAIN, 20));
        int messageY = 100; // Starting Y-coordinate for messages
        int lineSpacing = 30;

        if (promotion) {
            g2.setColor(Color.YELLOW);
            g2.drawString("Promote to:", 830, messageY);
            messageY += lineSpacing + 10;

            for (Piece piece : promoPieces) {
                g2.drawImage(piece.image, piece.getX(piece.col),
                        piece.getY(piece.row), Board.SQUARE_SIZE, Board.SQUARE_SIZE, null);
            }
        } else {
            g2.setColor(Color.WHITE);
            if (currentColor == WHITE) {
                g2.drawString("White's Turn", 830, messageY);
                messageY += lineSpacing;

                if (checkingP != null && checkingP.color == BLACK) {
                    g2.setColor(Color.RED);
                    g2.drawString("The King is in Check!", 830, messageY);
                    messageY += lineSpacing;
                }
            } else {
                g2.drawString("Black's Turn", 830, messageY);
                messageY += lineSpacing;

                if (checkingP != null && checkingP.color == WHITE) {
                    g2.setColor(Color.RED);
                    g2.drawString("The King is in Check!", 830, messageY);
                    messageY += lineSpacing;
                }
            }
        }

        // Draw game over messages
        if (gameOver) {
            g2.setFont(new Font("Book Antiqua", Font.BOLD, 30));
            g2.setColor(Color.PINK);
            g2.drawString(currentColor == WHITE ? "White Wins" : "Black Wins", 830, messageY);
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
                update();
                repaint();
                delta--;
            }
        }

    }
}
