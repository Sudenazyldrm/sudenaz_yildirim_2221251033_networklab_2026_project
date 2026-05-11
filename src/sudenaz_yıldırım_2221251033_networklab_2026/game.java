/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package sudenaz_yıldırım_2221251033_networklab_2026;

import java.awt.Color;
import javax.swing.JButton;
import javax.swing.Icon;
import javax.swing.JOptionPane;
import java.util.Locale;

/**
 *
 * @author ASUS
 */
public class game extends javax.swing.JFrame {

    /**
     * Creates new form game
     */
    JButton[][] boardButtons = new JButton[8][8];
    String[][] pieces = new String[8][8];

    private ChessClient client;

    int selectedRow = -1;
    int selectedCol = -1;

    Color highlightColor = Color.YELLOW;
    boolean isWhiteTurn = true;
    boolean gameOver = false;

    public game() {
        initComponents();
        boardButtons = new JButton[][]{
            {jButton1, jButton2, jButton3, jButton4, jButton5, jButton6, jButton7, jButton8},
            {jButton9, jButton10, jButton11, jButton12, jButton13, jButton14, jButton15, jButton16},
            {jButton17, jButton18, jButton19, jButton20, jButton21, jButton22, jButton23, jButton24},
            {jButton25, jButton26, jButton27, jButton28, jButton29, jButton30, jButton31, jButton32},
            {jButton33, jButton34, jButton35, jButton36, jButton37, jButton38, jButton39, jButton40},
            {jButton41, jButton42, jButton43, jButton44, jButton45, jButton46, jButton47, jButton48},
            {jButton49, jButton50, jButton51, jButton52, jButton53, jButton54, jButton55, jButton56},
            {jButton57, jButton58, jButton59, jButton60, jButton61, jButton62, jButton63, jButton64}
        };

        for (int row = 0; row < 8; row++) {
            for (int col = 0; col < 8; col++) {

                final int r = row;
                final int c = col;

                boardButtons[row][col].addActionListener(e -> squareClicked(r, c));
                boardButtons[row][col].setText("");
                boardButtons[row][col].setContentAreaFilled(false);
                boardButtons[row][col].setBorderPainted(false);
                boardButtons[row][col].setFocusPainted(false);
                boardButtons[row][col].setOpaque(false);
                boardButtons[row][col].setBorder(null);
            }
        }
        setupPieces();
        updateTurnLabel();
    }

    public game(ChessClient client) {
        this();
        this.client = client;

        this.client.setGameScreen(this);
        this.client.listenForMessages();
    }

private void squareClicked(int row, int col) {

    if (gameOver) {
        JOptionPane.showMessageDialog(this, "Oyun bitti!");
        return;
    }

    if (selectedRow == -1) {

        if (pieces[row][col] == null) {
            JOptionPane.showMessageDialog(this, "Burada taş yok!");
            return;
        }

        String piece = pieces[row][col];

        if (client != null) {

            String myColor = client.getPlayerColor();

            if (myColor == null) {
                JOptionPane.showMessageDialog(this,
                        "Player color could not be found!");
                return;
            }

            myColor = myColor.trim().toLowerCase(Locale.ENGLISH);
            String pieceColor = getPieceColor(piece).trim().toLowerCase(Locale.ENGLISH);

            if (!pieceColor.equals(myColor)) {
                JOptionPane.showMessageDialog(this,
                        "You can only move your own pieces!");
                return;
            }
        }

        if (isWhiteTurn && !piece.startsWith("white")) {
            JOptionPane.showMessageDialog(this, "Beyazın sırası!");
            return;
        }

        if (!isWhiteTurn && !piece.startsWith("black")) {
            JOptionPane.showMessageDialog(this, "Siyahın sırası!");
            return;
        }

        selectedRow = row;
        selectedCol = col;

        resetHighlights();
        showPossibleMoves(row, col);
        return;
    }

    if (isValidMove(selectedRow, selectedCol, row, col)) {

        String movingPiece = pieces[selectedRow][selectedCol];
        String movingColor = getPieceColor(movingPiece);
        String opponentColor = movingColor.equals("white") ? "black" : "white";

        String capturedPiece = pieces[row][col];

        if (capturedPiece != null && capturedPiece.contains("king")) {

            movePiece(selectedRow, selectedCol, row, col);

            if (client != null) {
                client.sendMove(
                        selectedRow,
                        selectedCol,
                        row,
                        col
                );
            }

            gameOver = true;
            turnLabel.setText("Game Over");

            JOptionPane.showMessageDialog(
                    this,
                    "Rakibin şahı yenildi! Oyunu kazandınız."
            );

            int choice = JOptionPane.showConfirmDialog(
                    this,
                    "Tekrar oynamak ister misiniz?",
                    "Oyun Bitti",
                    JOptionPane.YES_NO_OPTION
            );

            if (choice == JOptionPane.YES_OPTION) {

                startpage start = new startpage();
                start.setVisible(true);
                this.dispose();

            } else {
                System.exit(0);
            }

            return;
        }

        movePiece(selectedRow, selectedCol, row, col);

        if (client != null) {
            client.sendMove(
                    selectedRow,
                    selectedCol,
                    row,
                    col
            );
        }

        if (isCheckmate(opponentColor)) {

            gameOver = true;
            turnLabel.setText("Game Over");

            int choice = JOptionPane.showConfirmDialog(
                    this,
                    "Mat! Tekrar oynamak ister misiniz?",
                    "Oyun Bitti",
                    JOptionPane.YES_NO_OPTION
            );

            if (choice == JOptionPane.YES_OPTION) {

                startpage start = new startpage();
                start.setVisible(true);
                this.dispose();

            } else {

                JOptionPane.showMessageDialog(this, "Oyun kapatılıyor.");
                System.exit(0);
            }

            return;
        }

        if (isKingInCheck(opponentColor)) {
            JOptionPane.showMessageDialog(this, "Şah!");
        }

        isWhiteTurn = !isWhiteTurn;
        updateTurnLabel();
        selectedRow = -1;
        selectedCol = -1;
        resetHighlights();

    } else {
        JOptionPane.showMessageDialog(this, "Bu taş buraya gidemez!");

        selectedRow = -1;
        selectedCol = -1;
        resetHighlights();
    }
}
    private void setupPieces() {
        pieces[0][1] = "white_knight";
        pieces[0][6] = "white_knight";

        pieces[7][1] = "black_knight";
        pieces[7][6] = "black_knight";

        pieces[0][0] = "white_rook";
        pieces[0][7] = "white_rook";

        pieces[7][0] = "black_rook";
        pieces[7][7] = "black_rook";

        pieces[0][2] = "white_bishop";
        pieces[0][5] = "white_bishop";

        pieces[7][2] = "black_bishop";
        pieces[7][5] = "black_bishop";

        pieces[0][3] = "white_queen";
        pieces[7][3] = "black_queen";

        pieces[0][4] = "white_king";
        pieces[7][4] = "black_king";

        for (int col = 0; col < 8; col++) {
            pieces[1][col] = "white_pawn";
            pieces[6][col] = "black_pawn";
        }
    }

    private void showPossibleMoves(int row, int col) {
        String piece = pieces[row][col];

        if (piece == null) {
            return;
        }

        int[][] moves;

        if (piece.contains("knight")) {
            moves = new int[][]{
                {-2, -1}, {-2, 1},
                {-1, -2}, {-1, 2},
                {1, -2}, {1, 2},
                {2, -1}, {2, 1}
            };
        } else if (piece.contains("rook")) {
            int[][] directions = {
                {-1, 0}, {1, 0}, {0, -1}, {0, 1}
            };

            for (int[] dir : directions) {
                int newRow = row + dir[0];
                int newCol = col + dir[1];

                while (isInsideBoard(newRow, newCol)) {
                    if (pieces[newRow][newCol] != null) {
                        break;
                    }

                    boardButtons[newRow][newCol].setBorder(
                            javax.swing.BorderFactory.createLineBorder(Color.YELLOW, 10)
                    );
                    boardButtons[newRow][newCol].setBorderPainted(true);

                    newRow += dir[0];
                    newCol += dir[1];
                }
            }
            return;
        } else if (piece.contains("bishop")) {
            int[][] directions = {
                {-1, -1}, {-1, 1}, {1, -1}, {1, 1}
            };

            for (int[] dir : directions) {
                int newRow = row + dir[0];
                int newCol = col + dir[1];

                while (isInsideBoard(newRow, newCol)) {
                    if (pieces[newRow][newCol] != null) {
                        break;
                    }

                    boardButtons[newRow][newCol].setBorder(
                            javax.swing.BorderFactory.createLineBorder(Color.YELLOW, 10)
                    );
                    boardButtons[newRow][newCol].setBorderPainted(true);

                    newRow += dir[0];
                    newCol += dir[1];
                }
            }
            return;
        } else if (piece.contains("queen")) {
            int[][] directions = {
                {-1, 0}, {1, 0}, {0, -1}, {0, 1},
                {-1, -1}, {-1, 1}, {1, -1}, {1, 1}
            };

            for (int[] dir : directions) {
                int newRow = row + dir[0];
                int newCol = col + dir[1];

                while (isInsideBoard(newRow, newCol)) {
                    if (pieces[newRow][newCol] != null) {
                        break;
                    }

                    boardButtons[newRow][newCol].setBorder(
                            javax.swing.BorderFactory.createLineBorder(Color.YELLOW, 10)
                    );
                    boardButtons[newRow][newCol].setBorderPainted(true);

                    newRow += dir[0];
                    newCol += dir[1];
                }
            }
            return;
        } else if (piece.contains("king")) {
            int[][] kingMoves = {
                {-1, 0}, {1, 0}, {0, -1}, {0, 1},
                {-1, -1}, {-1, 1}, {1, -1}, {1, 1}
            };

            for (int[] move : kingMoves) {
                int newRow = row + move[0];
                int newCol = col + move[1];

                if (isInsideBoard(newRow, newCol) && pieces[newRow][newCol] == null) {
                    boardButtons[newRow][newCol].setBorder(
                            javax.swing.BorderFactory.createLineBorder(Color.YELLOW, 10)
                    );
                    boardButtons[newRow][newCol].setBorderPainted(true);
                }
            }
            return;
        } else if (piece.contains("pawn")) {
            int direction;

            if (piece.startsWith("white")) {
                direction = 1;
            } else {
                direction = -1;
            }

            int oneStepRow = row + direction;

            if (isInsideBoard(oneStepRow, col) && pieces[oneStepRow][col] == null) {
                boardButtons[oneStepRow][col].setBorder(
                        javax.swing.BorderFactory.createLineBorder(Color.YELLOW, 10)
                );
                boardButtons[oneStepRow][col].setBorderPainted(true);

                int startRow;

                if (piece.startsWith("white")) {
                    startRow = 1;
                } else {
                    startRow = 6;
                }

                int twoStepRow = row + (2 * direction);

                if (row == startRow
                        && isInsideBoard(twoStepRow, col)
                        && pieces[twoStepRow][col] == null) {

                    boardButtons[twoStepRow][col].setBorder(
                            javax.swing.BorderFactory.createLineBorder(Color.YELLOW, 10)
                    );
                    boardButtons[twoStepRow][col].setBorderPainted(true);
                }
            }

            return;
        } else {
            return;
        }

        for (int[] move : moves) {
            int newRow = row + move[0];
            int newCol = col + move[1];

            if (isInsideBoard(newRow, newCol)) {
                boardButtons[newRow][newCol].setBorder(
                        javax.swing.BorderFactory.createLineBorder(Color.YELLOW, 10)
                );
                boardButtons[newRow][newCol].setBorderPainted(true);
            }
        }
    }

    private boolean isValidMove(int fromRow, int fromCol, int toRow, int toCol) {
        String piece = pieces[fromRow][fromCol];

        if (piece == null) {
            return false;
        }
        if (isSameColor(piece, pieces[toRow][toCol])) {
            return false;
        }

        int rowDiff = toRow - fromRow;
        int colDiff = Math.abs(toCol - fromCol);

        if (piece.contains("knight")) {
            int rowDifference = Math.abs(toRow - fromRow);
            int colDifference = Math.abs(toCol - fromCol);

            return (rowDifference == 2 && colDifference == 1)
                    || (rowDifference == 1 && colDifference == 2);
        }

        if (piece.contains("rook")) {
            boolean straightMove = fromRow == toRow || fromCol == toCol;

            return straightMove && isPathClear(fromRow, fromCol, toRow, toCol);
        }
        if (piece.contains("bishop")) {
            int rowDifference = Math.abs(toRow - fromRow);
            int colDifference = Math.abs(toCol - fromCol);

            boolean diagonalMove = rowDifference == colDifference;

            return diagonalMove && isPathClear(fromRow, fromCol, toRow, toCol);
        }
        if (piece.contains("queen")) {
            int rowDifference = Math.abs(toRow - fromRow);
            int colDifference = Math.abs(toCol - fromCol);

            boolean straightMove = fromRow == toRow || fromCol == toCol;
            boolean diagonalMove = rowDifference == colDifference;

            return (straightMove || diagonalMove)
                    && isPathClear(fromRow, fromCol, toRow, toCol);
        }
        if (piece.contains("king")) {
            int rowDifference = Math.abs(toRow - fromRow);
            int colDifference = Math.abs(toCol - fromCol);

            return rowDifference <= 1 && colDifference <= 1;
        }
        if (piece.contains("pawn")) {
            int direction;

            if (piece.startsWith("white")) {
                direction = 1;
            } else {
                direction = -1;
            }

            int rowDiffPawn = toRow - fromRow;
            int colDiffPawn = Math.abs(toCol - fromCol);

            int startRow;

            if (piece.startsWith("white")) {
                startRow = 1;
            } else {
                startRow = 6;
            }

            // 1 kare düz ilerleme
            if (colDiffPawn == 0
                    && rowDiffPawn == direction
                    && pieces[toRow][toCol] == null) {
                return true;
            }

            // ilk hamlede 2 kare düz ilerleme
            if (colDiffPawn == 0
                    && fromRow == startRow
                    && rowDiffPawn == 2 * direction
                    && pieces[toRow][toCol] == null
                    && pieces[fromRow + direction][fromCol] == null) {
                return true;
            }

            // çapraz taş yeme
            if (colDiffPawn == 1
                    && rowDiffPawn == direction
                    && isEnemyPiece(piece, pieces[toRow][toCol])) {
                return true;
            }

            return false;
        }

        return false;
    }

    private void movePiece(int fromRow, int fromCol, int toRow, int toCol) {
        pieces[toRow][toCol] = pieces[fromRow][fromCol];
        pieces[fromRow][fromCol] = null;

        Icon icon = boardButtons[fromRow][fromCol].getIcon();

        boardButtons[toRow][toCol].setIcon(icon);
        boardButtons[fromRow][fromCol].setIcon(null);
    }

public void applyOpponentMove(int oldRow, int oldCol, int newRow, int newCol) {

    String capturedPiece = pieces[newRow][newCol];

    movePiece(oldRow, oldCol, newRow, newCol);

    if (capturedPiece != null && capturedPiece.contains("king")) {

        gameOver = true;
        turnLabel.setText("Game Over");

        JOptionPane.showMessageDialog(
                this,
                "Şahınız yenildi! Oyun bitti."
        );

        int choice = JOptionPane.showConfirmDialog(
                this,
                "Tekrar oynamak ister misiniz?",
                "Oyun Bitti",
                JOptionPane.YES_NO_OPTION
        );

        if (choice == JOptionPane.YES_OPTION) {

            startpage start = new startpage();
            start.setVisible(true);
            this.dispose();

        } else {
            System.exit(0);
        }

        return;
    }

    isWhiteTurn = !isWhiteTurn;
    updateTurnLabel();
    selectedRow = -1;
    selectedCol = -1;

    resetHighlights();
}
    private boolean isInsideBoard(int row, int col) {
        return row >= 0 && row < 8 && col >= 0 && col < 8;
    }

    private void resetHighlights() {
        for (int row = 0; row < 8; row++) {
            for (int col = 0; col < 8; col++) {
                boardButtons[row][col].setBorder(null);
                boardButtons[row][col].setBorderPainted(false);
            }
        }
    }

    private void updateTurnLabel() {

        if (client == null) {

            if (isWhiteTurn) {
                turnLabel.setText("White's Turn");
            } else {
                turnLabel.setText("Black's Turn");
            }

            return;
        }

        String myColor = client.getPlayerColor();

        if (myColor == null) {
            return;
        }

        boolean myTurn
                = (isWhiteTurn && myColor.equalsIgnoreCase("WHITE"))
                || (!isWhiteTurn && myColor.equalsIgnoreCase("BLACK"));

        if (myTurn) {
            turnLabel.setText("Your Turn");
        } else {
            turnLabel.setText("Waiting for Opponent...");
        }
    }

    private boolean isPathClear(int fromRow, int fromCol, int toRow, int toCol) {
        int rowStep = Integer.compare(toRow, fromRow);
        int colStep = Integer.compare(toCol, fromCol);

        int currentRow = fromRow + rowStep;
        int currentCol = fromCol + colStep;

        while (currentRow != toRow || currentCol != toCol) {
            if (pieces[currentRow][currentCol] != null) {
                return false;
            }

            currentRow += rowStep;
            currentCol += colStep;
        }

        return true;
    }

    private boolean isSameColor(String piece1, String piece2) {
        if (piece1 == null || piece2 == null) {
            return false;
        }

        return (piece1.startsWith("white") && piece2.startsWith("white"))
                || (piece1.startsWith("black") && piece2.startsWith("black"));
    }

    private boolean isEnemyPiece(String piece1, String piece2) {
        if (piece1 == null || piece2 == null) {
            return false;
        }

        return !isSameColor(piece1, piece2);
    }

    private String getPieceColor(String piece) {
        if (piece == null) {
            return "";
        }

        if (piece.startsWith("white")) {
            return "white";
        }

        if (piece.startsWith("black")) {
            return "black";
        }

        return "";
    }

    private boolean isKingInCheck(String kingColor) {
        int kingRow = -1;
        int kingCol = -1;

        String kingName = kingColor + "_king";

        for (int row = 0; row < 8; row++) {
            for (int col = 0; col < 8; col++) {
                if (kingName.equals(pieces[row][col])) {
                    kingRow = row;
                    kingCol = col;
                }
            }
        }

        if (kingRow == -1) {
            return false;
        }

        for (int row = 0; row < 8; row++) {
            for (int col = 0; col < 8; col++) {
                String piece = pieces[row][col];

                if (piece != null && !getPieceColor(piece).equals(kingColor)) {
                    if (canAttackSquare(row, col, kingRow, kingCol)) {
                        return true;
                    }
                }
            }
        }

        return false;
    }

    private boolean canAttackSquare(int fromRow, int fromCol, int toRow, int toCol) {
        String piece = pieces[fromRow][fromCol];

        if (piece == null) {
            return false;
        }

        int rowDiff = toRow - fromRow;
        int colDiff = toCol - fromCol;

        int absRow = Math.abs(rowDiff);
        int absCol = Math.abs(colDiff);

        if (piece.contains("pawn")) {
            int direction = piece.startsWith("white") ? 1 : -1;
            return rowDiff == direction && absCol == 1;
        }

        if (piece.contains("knight")) {
            return (absRow == 2 && absCol == 1)
                    || (absRow == 1 && absCol == 2);
        }

        if (piece.contains("rook")) {
            boolean straightMove = fromRow == toRow || fromCol == toCol;
            return straightMove && isPathClear(fromRow, fromCol, toRow, toCol);
        }

        if (piece.contains("bishop")) {
            boolean diagonalMove = absRow == absCol;
            return diagonalMove && isPathClear(fromRow, fromCol, toRow, toCol);
        }

        if (piece.contains("queen")) {
            boolean straightMove = fromRow == toRow || fromCol == toCol;
            boolean diagonalMove = absRow == absCol;

            return (straightMove || diagonalMove)
                    && isPathClear(fromRow, fromCol, toRow, toCol);
        }

        if (piece.contains("king")) {
            return absRow <= 1 && absCol <= 1;
        }

        return false;
    }

    private boolean wouldLeaveKingInCheck(int fromRow, int fromCol, int toRow, int toCol) {
        String movingPiece = pieces[fromRow][fromCol];
        String capturedPiece = pieces[toRow][toCol];

        pieces[toRow][toCol] = movingPiece;
        pieces[fromRow][fromCol] = null;

        boolean result = isKingInCheck(getPieceColor(movingPiece));

        pieces[fromRow][fromCol] = movingPiece;
        pieces[toRow][toCol] = capturedPiece;

        return result;
    }

    private boolean isCheckmate(String kingColor) {

        if (!isKingInCheck(kingColor)) {
            return false;
        }

        for (int fromRow = 0; fromRow < 8; fromRow++) {
            for (int fromCol = 0; fromCol < 8; fromCol++) {

                String piece = pieces[fromRow][fromCol];

                if (piece != null && getPieceColor(piece).equals(kingColor)) {

                    for (int toRow = 0; toRow < 8; toRow++) {
                        for (int toCol = 0; toCol < 8; toCol++) {

                            if (isValidMove(fromRow, fromCol, toRow, toCol)) {
                                if (!wouldLeaveKingInCheck(fromRow, fromCol, toRow, toCol)) {
                                    return false;
                                }
                            }

                        }
                    }
                }
            }
        }

        return true;
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        jButton1 = new javax.swing.JButton();
        jButton2 = new javax.swing.JButton();
        jButton3 = new javax.swing.JButton();
        jButton4 = new javax.swing.JButton();
        jButton5 = new javax.swing.JButton();
        jButton6 = new javax.swing.JButton();
        jButton7 = new javax.swing.JButton();
        jButton8 = new javax.swing.JButton();
        jButton9 = new javax.swing.JButton();
        jButton10 = new javax.swing.JButton();
        jButton11 = new javax.swing.JButton();
        jButton12 = new javax.swing.JButton();
        jButton13 = new javax.swing.JButton();
        jButton14 = new javax.swing.JButton();
        jButton15 = new javax.swing.JButton();
        jButton16 = new javax.swing.JButton();
        jButton17 = new javax.swing.JButton();
        jButton18 = new javax.swing.JButton();
        jButton19 = new javax.swing.JButton();
        jButton20 = new javax.swing.JButton();
        jButton21 = new javax.swing.JButton();
        jButton22 = new javax.swing.JButton();
        jButton23 = new javax.swing.JButton();
        jButton24 = new javax.swing.JButton();
        jButton25 = new javax.swing.JButton();
        jButton26 = new javax.swing.JButton();
        jButton27 = new javax.swing.JButton();
        jButton28 = new javax.swing.JButton();
        jButton29 = new javax.swing.JButton();
        jButton30 = new javax.swing.JButton();
        jButton31 = new javax.swing.JButton();
        jButton32 = new javax.swing.JButton();
        jButton33 = new javax.swing.JButton();
        jButton34 = new javax.swing.JButton();
        jButton35 = new javax.swing.JButton();
        jButton36 = new javax.swing.JButton();
        jButton37 = new javax.swing.JButton();
        jButton38 = new javax.swing.JButton();
        jButton39 = new javax.swing.JButton();
        jButton40 = new javax.swing.JButton();
        jButton41 = new javax.swing.JButton();
        jButton42 = new javax.swing.JButton();
        jButton43 = new javax.swing.JButton();
        jButton44 = new javax.swing.JButton();
        jButton45 = new javax.swing.JButton();
        jButton46 = new javax.swing.JButton();
        jButton47 = new javax.swing.JButton();
        jButton48 = new javax.swing.JButton();
        jButton49 = new javax.swing.JButton();
        jButton50 = new javax.swing.JButton();
        jButton51 = new javax.swing.JButton();
        jButton52 = new javax.swing.JButton();
        jButton53 = new javax.swing.JButton();
        jButton54 = new javax.swing.JButton();
        jButton55 = new javax.swing.JButton();
        jButton56 = new javax.swing.JButton();
        jButton57 = new javax.swing.JButton();
        jButton58 = new javax.swing.JButton();
        jButton59 = new javax.swing.JButton();
        jButton60 = new javax.swing.JButton();
        jButton61 = new javax.swing.JButton();
        jButton62 = new javax.swing.JButton();
        jButton63 = new javax.swing.JButton();
        jButton64 = new javax.swing.JButton();
        turnLabel = new javax.swing.JLabel();
        board_lbl = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jPanel1.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jButton1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/bkale.png"))); // NOI18N
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });
        jPanel1.add(jButton1, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 10, 80, 90));

        jButton2.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/bat.png"))); // NOI18N
        jPanel1.add(jButton2, new org.netbeans.lib.awtextra.AbsoluteConstraints(120, 20, 80, 80));

        jButton3.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/bfil.png"))); // NOI18N
        jButton3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton3ActionPerformed(evt);
            }
        });
        jPanel1.add(jButton3, new org.netbeans.lib.awtextra.AbsoluteConstraints(210, 20, 90, 90));

        jButton4.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/bvezir.png"))); // NOI18N
        jPanel1.add(jButton4, new org.netbeans.lib.awtextra.AbsoluteConstraints(310, 20, 90, 90));

        jButton5.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/bsah.png"))); // NOI18N
        jPanel1.add(jButton5, new org.netbeans.lib.awtextra.AbsoluteConstraints(410, 20, 80, 80));

        jButton6.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/bfil.png"))); // NOI18N
        jPanel1.add(jButton6, new org.netbeans.lib.awtextra.AbsoluteConstraints(510, 30, 70, 80));

        jButton7.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/bat.png"))); // NOI18N
        jPanel1.add(jButton7, new org.netbeans.lib.awtextra.AbsoluteConstraints(597, 25, 90, 80));

        jButton8.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/bkale.png"))); // NOI18N
        jPanel1.add(jButton8, new org.netbeans.lib.awtextra.AbsoluteConstraints(700, 25, 70, 80));

        jButton9.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/bpiyon.png"))); // NOI18N
        jPanel1.add(jButton9, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 120, 80, 80));

        jButton10.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/bpiyon.png"))); // NOI18N
        jPanel1.add(jButton10, new org.netbeans.lib.awtextra.AbsoluteConstraints(110, 120, 90, 80));

        jButton11.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/bpiyon.png"))); // NOI18N
        jPanel1.add(jButton11, new org.netbeans.lib.awtextra.AbsoluteConstraints(210, 120, 90, 80));

        jButton12.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/bpiyon.png"))); // NOI18N
        jPanel1.add(jButton12, new org.netbeans.lib.awtextra.AbsoluteConstraints(310, 120, 80, 80));

        jButton13.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/bpiyon.png"))); // NOI18N
        jPanel1.add(jButton13, new org.netbeans.lib.awtextra.AbsoluteConstraints(410, 120, 80, 80));

        jButton14.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/bpiyon.png"))); // NOI18N
        jPanel1.add(jButton14, new org.netbeans.lib.awtextra.AbsoluteConstraints(510, 120, 70, 80));

        jButton15.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/bpiyon.png"))); // NOI18N
        jPanel1.add(jButton15, new org.netbeans.lib.awtextra.AbsoluteConstraints(600, 120, 80, 80));

        jButton16.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/bpiyon.png"))); // NOI18N
        jPanel1.add(jButton16, new org.netbeans.lib.awtextra.AbsoluteConstraints(700, 120, 70, 80));

        jButton17.setText("jButton17");
        jPanel1.add(jButton17, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 215, -1, 70));

        jButton18.setText("jButton18");
        jPanel1.add(jButton18, new org.netbeans.lib.awtextra.AbsoluteConstraints(120, 210, -1, 80));

        jButton19.setText("jButton19");
        jPanel1.add(jButton19, new org.netbeans.lib.awtextra.AbsoluteConstraints(220, 220, -1, 60));

        jButton20.setText("jButton20");
        jPanel1.add(jButton20, new org.netbeans.lib.awtextra.AbsoluteConstraints(310, 230, -1, 60));

        jButton21.setText("jButton21");
        jPanel1.add(jButton21, new org.netbeans.lib.awtextra.AbsoluteConstraints(410, 220, -1, 60));

        jButton22.setText("jButton22");
        jPanel1.add(jButton22, new org.netbeans.lib.awtextra.AbsoluteConstraints(500, 215, -1, 70));

        jButton23.setText("jButton23");
        jPanel1.add(jButton23, new org.netbeans.lib.awtextra.AbsoluteConstraints(600, 220, -1, 60));

        jButton24.setText("jButton24");
        jPanel1.add(jButton24, new org.netbeans.lib.awtextra.AbsoluteConstraints(700, 215, -1, 60));

        jButton25.setText("jButton25");
        jPanel1.add(jButton25, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 320, -1, 60));

        jButton26.setText("jButton26");
        jPanel1.add(jButton26, new org.netbeans.lib.awtextra.AbsoluteConstraints(110, 315, -1, 60));

        jButton27.setText("jButton27");
        jPanel1.add(jButton27, new org.netbeans.lib.awtextra.AbsoluteConstraints(210, 315, -1, 60));

        jButton28.setText("jButton28");
        jPanel1.add(jButton28, new org.netbeans.lib.awtextra.AbsoluteConstraints(310, 315, -1, 60));

        jButton29.setText("jButton29");
        jPanel1.add(jButton29, new org.netbeans.lib.awtextra.AbsoluteConstraints(410, 305, -1, 80));

        jButton30.setText("jButton30");
        jPanel1.add(jButton30, new org.netbeans.lib.awtextra.AbsoluteConstraints(510, 305, -1, 60));

        jButton31.setText("jButton31");
        jPanel1.add(jButton31, new org.netbeans.lib.awtextra.AbsoluteConstraints(600, 305, -1, 70));

        jButton32.setText("jButton32");
        jPanel1.add(jButton32, new org.netbeans.lib.awtextra.AbsoluteConstraints(700, 305, -1, 60));

        jButton33.setText("jButton33");
        jPanel1.add(jButton33, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 420, -1, 60));

        jButton34.setText("jButton34");
        jPanel1.add(jButton34, new org.netbeans.lib.awtextra.AbsoluteConstraints(120, 415, -1, 70));

        jButton35.setText("jButton35");
        jPanel1.add(jButton35, new org.netbeans.lib.awtextra.AbsoluteConstraints(210, 410, -1, 70));

        jButton36.setText("jButton36");
        jPanel1.add(jButton36, new org.netbeans.lib.awtextra.AbsoluteConstraints(310, 420, -1, 70));

        jButton37.setText("jButton37");
        jPanel1.add(jButton37, new org.netbeans.lib.awtextra.AbsoluteConstraints(410, 420, -1, 60));

        jButton38.setText("jButton38");
        jPanel1.add(jButton38, new org.netbeans.lib.awtextra.AbsoluteConstraints(500, 420, -1, 70));

        jButton39.setText("jButton39");
        jPanel1.add(jButton39, new org.netbeans.lib.awtextra.AbsoluteConstraints(600, 420, -1, 60));

        jButton40.setText("jButton40");
        jPanel1.add(jButton40, new org.netbeans.lib.awtextra.AbsoluteConstraints(700, 420, -1, 60));

        jButton41.setText("jButton41");
        jButton41.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton41ActionPerformed(evt);
            }
        });
        jPanel1.add(jButton41, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 505, -1, 70));

        jButton42.setText("jButton42");
        jPanel1.add(jButton42, new org.netbeans.lib.awtextra.AbsoluteConstraints(120, 530, -1, 50));

        jButton43.setText("jButton43");
        jPanel1.add(jButton43, new org.netbeans.lib.awtextra.AbsoluteConstraints(210, 520, -1, 60));

        jButton44.setText("jButton44");
        jPanel1.add(jButton44, new org.netbeans.lib.awtextra.AbsoluteConstraints(310, 505, -1, 70));

        jButton45.setText("jButton45");
        jPanel1.add(jButton45, new org.netbeans.lib.awtextra.AbsoluteConstraints(410, 520, -1, 60));

        jButton46.setText("jButton46");
        jPanel1.add(jButton46, new org.netbeans.lib.awtextra.AbsoluteConstraints(510, 520, -1, 60));

        jButton47.setText("jButton47");
        jPanel1.add(jButton47, new org.netbeans.lib.awtextra.AbsoluteConstraints(600, 505, -1, 70));

        jButton48.setText("jButton48");
        jPanel1.add(jButton48, new org.netbeans.lib.awtextra.AbsoluteConstraints(700, 520, -1, 60));

        jButton49.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/spiyon.png"))); // NOI18N
        jPanel1.add(jButton49, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 610, 80, 70));

        jButton50.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/spiyon.png"))); // NOI18N
        jPanel1.add(jButton50, new org.netbeans.lib.awtextra.AbsoluteConstraints(110, 600, 90, 80));

        jButton51.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/spiyon.png"))); // NOI18N
        jPanel1.add(jButton51, new org.netbeans.lib.awtextra.AbsoluteConstraints(220, 610, 80, 70));

        jButton52.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/spiyon.png"))); // NOI18N
        jPanel1.add(jButton52, new org.netbeans.lib.awtextra.AbsoluteConstraints(310, 605, 80, 80));

        jButton53.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/spiyon.png"))); // NOI18N
        jPanel1.add(jButton53, new org.netbeans.lib.awtextra.AbsoluteConstraints(410, 610, 80, 70));

        jButton54.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/spiyon.png"))); // NOI18N
        jPanel1.add(jButton54, new org.netbeans.lib.awtextra.AbsoluteConstraints(510, 605, 80, 80));

        jButton55.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/spiyon.png"))); // NOI18N
        jPanel1.add(jButton55, new org.netbeans.lib.awtextra.AbsoluteConstraints(600, 600, 90, 70));

        jButton56.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/spiyon.png"))); // NOI18N
        jPanel1.add(jButton56, new org.netbeans.lib.awtextra.AbsoluteConstraints(700, 605, 80, 80));

        jButton57.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/skale.png"))); // NOI18N
        jPanel1.add(jButton57, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 700, 80, 80));

        jButton58.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/sat.png"))); // NOI18N
        jPanel1.add(jButton58, new org.netbeans.lib.awtextra.AbsoluteConstraints(120, 710, 80, 70));

        jButton59.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/sfil.png"))); // NOI18N
        jPanel1.add(jButton59, new org.netbeans.lib.awtextra.AbsoluteConstraints(210, 700, 90, 80));

        jButton60.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/svezir.png"))); // NOI18N
        jPanel1.add(jButton60, new org.netbeans.lib.awtextra.AbsoluteConstraints(310, 700, 80, 80));

        jButton61.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/ssah.png"))); // NOI18N
        jPanel1.add(jButton61, new org.netbeans.lib.awtextra.AbsoluteConstraints(410, 700, 80, 80));

        jButton62.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/sfil.png"))); // NOI18N
        jPanel1.add(jButton62, new org.netbeans.lib.awtextra.AbsoluteConstraints(500, 705, 90, 80));

        jButton63.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/sat.png"))); // NOI18N
        jPanel1.add(jButton63, new org.netbeans.lib.awtextra.AbsoluteConstraints(600, 710, 90, 70));

        jButton64.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/skale.png"))); // NOI18N
        jPanel1.add(jButton64, new org.netbeans.lib.awtextra.AbsoluteConstraints(700, 710, 80, 70));

        turnLabel.setForeground(new java.awt.Color(0, 255, 51));
        turnLabel.setText("TURN");
        jPanel1.add(turnLabel, new org.netbeans.lib.awtextra.AbsoluteConstraints(600, 0, 150, 20));

        board_lbl.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/tahta.png"))); // NOI18N
        jPanel1.add(board_lbl, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 800, 800));

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addGap(0, 0, Short.MAX_VALUE)
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addGap(0, 0, Short.MAX_VALUE)
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, 0))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jButton41ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton41ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jButton41ActionPerformed

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jButton1ActionPerformed

    private void jButton3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton3ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jButton3ActionPerformed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(game.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(game.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(game.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(game.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new game().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel board_lbl;
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton10;
    private javax.swing.JButton jButton11;
    private javax.swing.JButton jButton12;
    private javax.swing.JButton jButton13;
    private javax.swing.JButton jButton14;
    private javax.swing.JButton jButton15;
    private javax.swing.JButton jButton16;
    private javax.swing.JButton jButton17;
    private javax.swing.JButton jButton18;
    private javax.swing.JButton jButton19;
    private javax.swing.JButton jButton2;
    private javax.swing.JButton jButton20;
    private javax.swing.JButton jButton21;
    private javax.swing.JButton jButton22;
    private javax.swing.JButton jButton23;
    private javax.swing.JButton jButton24;
    private javax.swing.JButton jButton25;
    private javax.swing.JButton jButton26;
    private javax.swing.JButton jButton27;
    private javax.swing.JButton jButton28;
    private javax.swing.JButton jButton29;
    private javax.swing.JButton jButton3;
    private javax.swing.JButton jButton30;
    private javax.swing.JButton jButton31;
    private javax.swing.JButton jButton32;
    private javax.swing.JButton jButton33;
    private javax.swing.JButton jButton34;
    private javax.swing.JButton jButton35;
    private javax.swing.JButton jButton36;
    private javax.swing.JButton jButton37;
    private javax.swing.JButton jButton38;
    private javax.swing.JButton jButton39;
    private javax.swing.JButton jButton4;
    private javax.swing.JButton jButton40;
    private javax.swing.JButton jButton41;
    private javax.swing.JButton jButton42;
    private javax.swing.JButton jButton43;
    private javax.swing.JButton jButton44;
    private javax.swing.JButton jButton45;
    private javax.swing.JButton jButton46;
    private javax.swing.JButton jButton47;
    private javax.swing.JButton jButton48;
    private javax.swing.JButton jButton49;
    private javax.swing.JButton jButton5;
    private javax.swing.JButton jButton50;
    private javax.swing.JButton jButton51;
    private javax.swing.JButton jButton52;
    private javax.swing.JButton jButton53;
    private javax.swing.JButton jButton54;
    private javax.swing.JButton jButton55;
    private javax.swing.JButton jButton56;
    private javax.swing.JButton jButton57;
    private javax.swing.JButton jButton58;
    private javax.swing.JButton jButton59;
    private javax.swing.JButton jButton6;
    private javax.swing.JButton jButton60;
    private javax.swing.JButton jButton61;
    private javax.swing.JButton jButton62;
    private javax.swing.JButton jButton63;
    private javax.swing.JButton jButton64;
    private javax.swing.JButton jButton7;
    private javax.swing.JButton jButton8;
    private javax.swing.JButton jButton9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JLabel turnLabel;
    // End of variables declaration//GEN-END:variables
}
