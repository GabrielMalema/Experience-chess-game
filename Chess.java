import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class Chess extends JFrame {

    private final JButton[][] squares = new JButton[8][8];
    private final String[][] board = new String[8][8];
    private int selectedRow = -1;
    private int selectedCol = -1;
    private boolean whiteToMove = true;

    private static final Color LIGHT_COLOR = new Color(240, 217, 181);
    private static final Color DARK_COLOR = new Color(181, 136, 99);
    private static final Color SELECTED_COLOR = new Color(106, 168, 79);

    public Chess() {
        setTitle("Chess");
        setSize(560, 620);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);

        setupBoard();

        JPanel boardPanel = new JPanel(new GridLayout(8, 8));
        for (int row = 0; row < 8; row++) {
            for (int col = 0; col < 8; col++) {
                JButton square = new JButton();
                square.setFont(new Font("SansSerif", Font.PLAIN, 36));
                square.setFocusPainted(false);
                final int r = row;
                final int c = col;
                square.addActionListener(e -> handleClick(r, c));
                squares[row][col] = square;
                boardPanel.add(square);
            }
        }

        add(boardPanel, BorderLayout.CENTER);
        refreshBoard();
    }

    private void setupBoard() {
        String[] backRow = {"R", "N", "B", "Q", "K", "B", "N", "R"};

        for (int col = 0; col < 8; col++) {
            board[0][col] = "b" + backRow[col];
            board[1][col] = "bP";
            board[6][col] = "wP";
            board[7][col] = "w" + backRow[col];
        }
        for (int row = 2; row < 6; row++) {
            for (int col = 0; col < 8; col++) {
                board[row][col] = null;
            }
        }
    }

    private void handleClick(int row, int col) {
        if (selectedRow == -1) {
            String piece = board[row][col];
            if (piece == null) return;
            boolean isWhitePiece = piece.startsWith("w");
            if (isWhitePiece != whiteToMove) return;

            selectedRow = row;
            selectedCol = col;
            highlightSelection();
        } else {
            if (row == selectedRow && col == selectedCol) {
                selectedRow = -1;
                selectedCol = -1;
                refreshBoard();
                return;
            }

            if (isLegalMove(selectedRow, selectedCol, row, col)) {
                board[row][col] = board[selectedRow][selectedCol];
                board[selectedRow][selectedCol] = null;
                whiteToMove = !whiteToMove;
            }

            selectedRow = -1;
            selectedCol = -1;
            refreshBoard();
        }
    }

    private boolean isLegalMove(int fromRow, int fromCol, int toRow, int toCol) {
        String piece = board[fromRow][fromCol];
        if (piece == null) return false;

        String target = board[toRow][toCol];
        if (target != null && target.charAt(0) == piece.charAt(0)) {
            return false;
        }

        char type = piece.charAt(1);
        int rowDiff = toRow - fromRow;
        int colDiff = toCol - fromCol;
        boolean isWhite = piece.startsWith("w");

        switch (type) {
            case 'P':
                return isLegalPawnMove(fromRow, fromCol, toRow, toCol, isWhite);
            case 'R':
                return (rowDiff == 0 || colDiff == 0) && isPathClear(fromRow, fromCol, toRow, toCol);
            case 'B':
                return Math.abs(rowDiff) == Math.abs(colDiff) && isPathClear(fromRow, fromCol, toRow, toCol);
            case 'Q':
                return (rowDiff == 0 || colDiff == 0 || Math.abs(rowDiff) == Math.abs(colDiff))
                        && isPathClear(fromRow, fromCol, toRow, toCol);
            case 'N':
                return (Math.abs(rowDiff) == 2 && Math.abs(colDiff) == 1)
                        || (Math.abs(rowDiff) == 1 && Math.abs(colDiff) == 2);
            case 'K':
                return Math.abs(rowDiff) <= 1 && Math.abs(colDiff) <= 1;
            default:
                return false;
        }
    }

    private boolean isLegalPawnMove(int fromRow, int fromCol, int toRow, int toCol, boolean isWhite) {
        int direction = isWhite ? -1 : 1;
        int startRow = isWhite ? 6 : 1;
        int rowDiff = toRow - fromRow;
        int colDiff = toCol - fromCol;
        String target = board[toRow][toCol];

        if (colDiff == 0 && target == null) {
            if (rowDiff == direction) return true;
            if (fromRow == startRow && rowDiff == 2 * direction
                    && board[fromRow + direction][fromCol] == null) {
                return true;
            }
        }

        if (Math.abs(colDiff) == 1 && rowDiff == direction && target != null) {
            return true;
        }

        return false;
    }

    private boolean isPathClear(int fromRow, int fromCol, int toRow, int toCol) {
        int rowStep = Integer.signum(toRow - fromRow);
        int colStep = Integer.signum(toCol - fromCol);

        int row = fromRow + rowStep;
        int col = fromCol + colStep;

        while (row != toRow || col != toCol) {
            if (board[row][col] != null) return false;
            row += rowStep;
            col += colStep;
        }
        return true;
    }

    private void highlightSelection() {
        refreshBoard();
        squares[selectedRow][selectedCol].setBackground(SELECTED_COLOR);
    }

    private void refreshBoard() {
        for (int row = 0; row < 8; row++) {
            for (int col = 0; col < 8; col++) {
                JButton square = squares[row][col];
                square.setText(getSymbol(board[row][col]));
                square.setBackground((row + col) % 2 == 0 ? LIGHT_COLOR : DARK_COLOR);
                square.setForeground(board[row][col] != null && board[row][col].startsWith("w")
                        ? Color.WHITE : Color.BLACK);
            }
        }
        setTitle("Chess - " + (whiteToMove ? "White" : "Black") + " to move");
    }

    private String getSymbol(String piece) {
    if (piece == null) return "";
    char color = piece.charAt(0);
    char type = piece.charAt(1);

    switch (type) {
        case 'K':
            return color == 'w' ? "♔" : "♚";
        case 'Q':
            return color == 'w' ? "♕" : "♛";
        case 'R':
            return color == 'w' ? "♖" : "♜";
        case 'B':
            return color == 'w' ? "♗" : "♝";
        case 'N':
            return color == 'w' ? "♘" : "♞";
        case 'P':
            return color == 'w' ? "♙" : "♟";
        default:
            return "";
    }
}

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            Chess chess = new Chess();
            chess.setVisible(true);
        });
    }
}
