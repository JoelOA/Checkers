/**
 * @author Joel Osei-Asamoah and Wofa Yamoah Attafuah
 * A checker game 
 */
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JButton;
import javax.swing.JPanel;
import java.awt.*;
import java.util.ArrayList;
import java.awt.event.*;

/**
 * A class that represents the the entire checkers, game
 * contains the class for the board and the class for 
 * the state of the board
 */
public class CheckersGame extends JPanel{
    public static void main(String[] args){
        JFrame window = new JFrame("Checkers");
        Dimension screensize = Toolkit.getDefaultToolkit().getScreenSize();
        window.setLocation( (screensize.width + 60), (screensize.height + 80) );
        window.setBounds(450, 50, 800, 550);
        CheckersGame glass = new CheckersGame();
        window.setContentPane(glass);
        window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        window.setVisible(true);
        window.setResizable(false);
    }
    
    private static JLabel message;
    private static JButton startAfreshButton;
    private static JButton exitButton;

    public CheckersGame() {
        setLayout(null);
        setBackground(new Color(0,100,100));

        Board board = new Board();
        add(startAfreshButton);
        add(exitButton);
        add(board);
        add(message);

        board.setBounds(0,0,520, 512);
        startAfreshButton.setBounds(550, 220, 200, 40);
        exitButton.setBounds(550, 300, 200, 40);
        message.setBounds(535, 130, 230, 60);
        message.setText("Start Playing!");
    }

    /**
     * A class that represents the board of checkers, it allows users
     * to click to make moves and select their moves
     */
    public class Board extends JPanel implements MouseListener, ActionListener{

        BoardState state;
        
        static ArrayList<Pair> ValidMoves = new ArrayList<>();
        static boolean pieceSelected = false;
        static int selectedPieceRow;
        static int selectedPieceCol;
        static int selectedPieceColour;
        Pair currentPiece = new Pair(100,100);

        Board(){
            message = new JLabel("",JLabel.CENTER);
            message.setFont(new  Font("Serif", Font.BOLD, 25));
            message.setForeground(Color.white);

            state = new BoardState();
            addMouseListener(this);
            startAfreshButton = new JButton("Reset Game");
            startAfreshButton.addActionListener(this);
            exitButton = new JButton("Exit");
            exitButton.addActionListener(this);
            repaint();
        }

        /**
         * This method paints the board, and all the pieces and the possible moves
         * of any selected piece
         */
        public void paintComponent (Graphics g) {
            Color brown = new Color(139,69,19);
            Color cream = new Color(255,228,181);
            boolean primary = true;
            //Painting board
            for(int row = 0; row < 8; row++){
                for(int col = 0; col < 8; col++){
                    if(!primary){
                        g.setColor(brown);
                    }
                    else{
                        g.setColor(cream);
                    }
                    g.fillRect(col*64, row*64, 64, 64);
                    primary = !primary;

                    //Painting red and black pieces
                    int val = state.checkPiece(row, col);
                    if (val == state.RED){
                        g.setColor(Color.RED);
                        g.fillOval((col*64) + 7, (64*row) + 5, 50, 50);}
                    else if (val == state.BLACK){
                        g.setColor(Color.BLACK);
                        g.fillOval((col*64) + 7, (64*row) + 5, 50, 50);
                    }
                }
                primary = !primary;
            }
            //Drawing the right black border
            Graphics2D g2 = (Graphics2D) g;
            g2.setColor(Color.black);
            g2.setStroke(new BasicStroke(5));
            g2.drawLine(515, 0, 515,512);

            //Painting the possible moves of the highlighted piece
            if( ValidMoves.size() != 0 ){
                g2.setColor(Color.blue);
                g2.setStroke(new BasicStroke(2));
                for( Pair p: ValidMoves ) {
                    g2.drawRect( p.getCol() * 64, p.getRow() * 64 , 64, 64);
                }
                g2.setColor(Color.green);
                g2.drawRect( currentPiece.getCol() * 64, currentPiece.getRow() * 64 , 64, 64);
            }

            
            g.dispose();
            g2.dispose();
        }

        public void mouseClicked(MouseEvent e) {
            int col = e.getX() / 64;
            int row = e.getY() / 64;

            if (pieceSelected == true){
                checkPosition(row, col);
            }
            else if (pieceSelected == false){
                if (state.checkPiece(row, col) != state.getTurn()){
                    System.out.println("Click a piece of your colour");
                }
                else{
                    ValidMoves.clear();
                    this.showMoves(row, col);
                    currentPiece.setRow(row);
                    currentPiece.setCol(col);
                }
            }
            
        }
        public void mouseEntered(MouseEvent e) {} //message.setText("Mouse Entered"); }  
        public void mouseExited(MouseEvent e) {} //message.setText("Mouse Exited"); }  
        public void mousePressed(MouseEvent e) {} //message.setText("Mouse Pressed"); }  
        public void mouseReleased(MouseEvent e) {} //message.setText("Mouse Released"); }

        /**
         * This method checks if the position selected by the user is a valid
         * move, and moves the piece there. 
         * @param row is a representation of the row the selected move is on
         * @param col is a representation of the column the selected move is on
         */
        public void checkPosition(int row, int col){
            // if piece is a different piece and same colour show moves
            if (state.checkPiece(row, col) == selectedPieceColour) {
                ValidMoves.clear();
                this.showMoves(row, col);
                pieceSelected = false;
            }
            // else if location selected is a valid move, move piece to that location
            else if (isValidMove(row, col)) {
                state.changePosition(selectedPieceRow, selectedPieceCol, row, col, selectedPieceColour);
                pieceSelected = false;
                repaint();
            }
            // else print click valid position
            else {
                System.out.println("Click valid move");  }
        }
        
        /**
         * This method checks the to see if the selected position on the board
         * is a piece, if it is a piece it gets the valid moves of the piece
         * by calling the getValidMoves() function
         * @param row
         * @param col
         */
        public void showMoves(int row, int col) {
            int colour = state.checkPiece(row, col); // get the colour the selected piece
            if (colour == state.EMPTY) {
                System.out.println("Select a piece"); // if piece is empty, print select a good piece
            }
            else {
                pieceSelected = true;
                selectedPieceRow = row;
                selectedPieceCol = col;
                if (colour == state.BLACK) {
                    selectedPieceColour = state.BLACK; // assign selected piece variable to black, if piece selected is black
                }
                else {
                    selectedPieceColour = state.RED; // assign selected piece variable to red, if piece selected is red
                }
                getValidMoves(row, col); // call valid moves function
                repaint(); // repaint board, to show moves
            }
            
        }

        /**
         * This method checks if a move is valid one.
         * @param row a representation of the row the move the AI or user wants to make
         * @param col a representation of the coloumn of the move the AI or user wants to make
         * @return it returns a boolean value - true or false. True when the move
         * is valid, false when the move is not valid
         */
        public boolean isValidMove(int row, int col) {
            for (Pair p: ValidMoves) {
                if (p.getRow() == row && p.getCol() == col) {
                    return true;
                }
            }
            return false;
        }

        /**
         * This is a method that computes all the valid moves of a selected piece
         * it does this by considering if there is a free space diagonal to the piece
         * and also if there is an opponent piece to capture.
         * @param row a representation of the row of the selected piece
         * @param col a representation of the column of the selected column
         */
        public void getValidMoves(int row, int col) {
            int Piece = state.checkPiece(row, col);
            if (col == 0) {
                if (Piece == state.BLACK){
                    if (state.checkPiece(row + 1, col + 1) == state.EMPTY) {
                        ValidMoves.add(new Pair(row + 1, col + 1));
                        System.out.printf("(%d, %d) is a valid move\n", row + 1, col + 1);
                    }
                    else {
                        if (state.checkPiece(row + 2, col + 2) == state.EMPTY && state.checkPiece(row + 1, col + 1) == state.RED) {
                            ValidMoves.add(new Pair(row + 2, col + 2));
                            System.out.printf("(%d, %d) is a valid move\n", row + 2, col + 2);
                        }
                    }
                }
                
                else if (Piece == state.RED) {
                    if (state.checkPiece(row - 1, col + 1) == state.EMPTY) {
                        ValidMoves.add(new Pair(row - 1, col + 1));
                        System.out.printf("(%d, %d) is a valid move\n", row - 1, col + 1);
                    }
                    else {
                        if (state.checkPiece(row - 2, col + 2) == state.EMPTY && state.checkPiece(row - 1, col + 1) == state.BLACK) {
                            ValidMoves.add(new Pair(row - 2, col + 2));
                            System.out.printf("(%d, %d) is a valid move\n", row - 2, col + 2);
                        }
                    }
                }
            }
            else if (col == 7) {
                if (Piece == state.BLACK){
                    if (state.checkPiece(row + 1, col - 1) == state.EMPTY) {
                        ValidMoves.add(new Pair(row + 1, col - 1));
                        System.out.printf("(%d, %d) is a valid move\n", row + 1, col - 1);
                    }
                    else {
                        if (state.checkPiece(row + 2, col - 2) == state.EMPTY && state.checkPiece(row + 1, col - 1) == state.RED) {
                            ValidMoves.add(new Pair(row + 2, col - 2));
                            System.out.printf("(%d, %d) is a valid move\n", row + 2, col - 2);
                        }
                    }
                }

                else if (Piece == state.RED) {
                    if (state.checkPiece(row - 1, col - 1) == state.EMPTY) {
                        ValidMoves.add(new Pair(row - 1, col - 1));
                        System.out.printf("(%d, %d) is a valid move\n", row - 1, col - 1);
                    }
                    else {
                        if (state.checkPiece(row - 2, col - 2) == state.EMPTY && state.checkPiece(row - 1, col - 1) == state.BLACK) {
                            ValidMoves.add(new Pair(row - 2, col - 2));
                            System.out.printf("(%d, %d) is a valid move\n", row - 2, col - 2);
                        }
                    }
                }
            }

            else {
                if (Piece == state.BLACK){
                    if (state.checkPiece(row + 1, col + 1) == state.EMPTY) {
                        ValidMoves.add(new Pair(row + 1, col + 1));
                        System.out.printf("(%d, %d) is a valid move\n", row + 1, col + 1);
                    }
                    else {
                        canJump(Piece, row, col);
                        
                    }

                    if (state.checkPiece(row + 1, col - 1) == state.EMPTY) {
                        ValidMoves.add(new Pair(row + 1, col - 1));
                        System.out.printf("(%d, %d) is a valid move\n", row + 1, col - 1);
                    }
                    else {
                        canJump(Piece, row, col);
                    }
                }
                
                else if (Piece == state.RED) {
                    if (state.checkPiece(row - 1, col + 1) == state.EMPTY) {
                        ValidMoves.add(new Pair(row - 1, col + 1));
                        System.out.printf("(%d, %d) is a valid move\n", row - 1, col + 1);
                    }
                    else {
                        canJump(Piece, row, col);
                    }

                    if (state.checkPiece(row - 1, col - 1) == state.EMPTY) {
                        ValidMoves.add(new Pair(row - 1, col - 1));
                        System.out.printf("(%d, %d) is a valid move\n", row - 1, col - 1);
                    }
                    else {
                        canJump(Piece, row, col);
                    }
                }
            }
            if( ValidMoves.size() == 0) System.out.println("No valid moves for this piece");
        }

        /**
         * A method that determines if a piece can make a jump or not
         * This code was suppose to be in the getValidMoves function, as you can 
         * see that is where it is called. But in order to write repetitive code, 
         * we decided to create a method so that anytime it would be checked if
         * that piece can jump
         * @param Piece a representation of the colour of the piece that is selected
         * @param row a representation of the row of the selected piece
         * @param col a representation of the column of the selected piece
         */
        public void canJump(int Piece, int row, int col){
            if (col + 2 > 7){
                if (Piece == state.BLACK) {
                    if (state.checkPiece(row + 2, col - 2) == state.EMPTY && state.checkPiece(row + 1, col - 1) == state.RED) {
                        ValidMoves.add(new Pair(row + 2, col - 2));
                        System.out.printf("(%d, %d) is a valid move\n", row + 2, col - 2);
                    }
                }
                if (Piece == state.RED) {
                    if (state.checkPiece(row - 2, col - 2) == state.EMPTY && state.checkPiece(row - 1, col - 1) == state.BLACK) {
                        ValidMoves.add(new Pair(row - 2, col - 2));
                        System.out.printf("(%d, %d) is a valid move\n", row - 2, col - 2);
                    }
                }
            }
    
            else if(col - 2 < 0){
                if (Piece == state.BLACK) {
                    if (state.checkPiece(row + 2, col + 2) == state.EMPTY && state.checkPiece(row + 1, col + 1) == state.RED) {
                        ValidMoves.add(new Pair(row + 2, col + 2));
                        System.out.printf("(%d, %d) is a valid move\n", row + 2, col + 2);
                    }
                }
                if (Piece == state.RED) {
                    if (state.checkPiece(row - 2, col + 2) == state.EMPTY && state.checkPiece(row - 1, col + 1) == state.BLACK) {
                        ValidMoves.add(new Pair(row - 2, col + 2));
                        System.out.printf("(%d, %d) is a valid move\n", row - 2, col + 2);
                    }
                }
            }
            else {
                if (Piece == state.BLACK) {
                    if (state.checkPiece(row + 2, col + 2) == state.EMPTY && state.checkPiece(row + 1, col + 1) == state.RED) {
                        ValidMoves.add(new Pair(row + 2, col + 2));
                        System.out.printf("(%d, %d) is a valid move\n", row + 2, col + 2);
                    }
                    if (state.checkPiece(row + 2, col - 2) == state.EMPTY && state.checkPiece(row + 1, col - 1) == state.RED) {
                        ValidMoves.add(new Pair(row + 2, col - 2));
                        System.out.printf("(%d, %d) is a valid move\n", row + 2, col - 2);
                    }
                }
                else if (Piece == state.RED) {
                    if (state.checkPiece(row - 2, col + 2) == state.EMPTY && state.checkPiece(row - 1, col + 1) == state.BLACK) {
                        ValidMoves.add(new Pair(row - 2, col + 2));
                        System.out.printf("(%d, %d) is a valid move\n", row - 2, col + 2);
                    }
                    if (state.checkPiece(row - 2, col - 2) == state.EMPTY && state.checkPiece(row - 1, col - 1) == state.BLACK) {
                        ValidMoves.add(new Pair(row - 2, col - 2));
                        System.out.printf("(%d, %d) is a valid move\n", row - 2, col - 2);
                    }
                }
            }
        }

        public void actionPerformed(ActionEvent e) {
            Object event = e.getSource();
            if (event == startAfreshButton)
                state.newGame();
            else if (event == exitButton)
                System.exit(0);
        }
    }

    public class BoardState {
        public final int RED = 1;
        public final int BLACK = -1;
        public final int EMPTY = 0;
        public final int TIE = 0;
        public int redNumber = 12;
        int blackNumber = 12;
        int turn = 1;

        public static int[][] Board;

        public BoardState() {
            Board = new int[8][8];
            initialize();
        }

        /**
         * A method that checks if there is a piece at that position of the board
         * @param row a representation of the row the selected piece is on
         * @param col a representation of the column the selected piece is on
         * @return the colour the selected piece, either, red (1), black (-1) or empty (0)
         */
        public int checkPiece(int row, int col) {
            return Board[row][col];
        }

        /**
         * A method that updates the states of the board, that is when the moves are made
         * it does the corresponding changes in the 2D-array
         * @param old_row a representation of the row the selected piece is moving from
         * @param old_col a representation of the column the selected piece is moving from
         * @param new_row a representaiton of the row the selected piece is moving to
         * @param new_col a representation of the column the selected piece is moving to
         * @param colour a represenation of the colour that is being moved
         */
        public void changePosition(int old_row, int old_col, int new_row, int new_col, int colour) {
            Board[old_row][old_col] = EMPTY;
            Board[new_row][new_col] = colour;
            /**
             * The following block of code, handles cases where pieces are jumped over
             */
            if (new_col - old_col == 2) {
                if (colour == BLACK) {
                    Board[old_row + 1][old_col + 1] = EMPTY;
                    redNumber--;
                }
                else if(colour == RED) {
                    Board[old_row - 1][old_col + 1] = EMPTY;
                    blackNumber--;
                }
            }
            else if (new_col - old_col == -2) {
                if (colour == BLACK) {
                    Board[old_row + 1][old_col - 1] = EMPTY;
                    redNumber--;
                }
                else if (colour == RED) {
                    Board[old_row - 1][old_col - 1] = EMPTY;
                    blackNumber--;
                }
            }
            changeTurn();
            checkWinner();
        }

        /**
         * This is a method, the sets up the board to the default state, that is
         * its starting position. This method is also used to reset the game
         */
        public void initialize() {
            for (int row = 0; row < 3; row++) {
                for (int col = 0; col < 8; col ++) {
                    if ((row % 2 != 0) && (col % 2 == 0)) {
                        Board[row][col] = BLACK;
                    }
                    else if((row % 2 == 0) && (col % 2 != 0)) {
                        Board[row][col] = BLACK;
                    }
                    else {Board[row][col] = EMPTY;}
                }
            }

            for (int row = 5; row < 8; row++) {
                for (int col = 0; col < 8; col ++) {
                    if ((row % 2 != 0) && (col % 2 == 0)) {
                        Board[row][col] = RED;
                    }
                    else if((row % 2 == 0) && (col % 2 != 0)) {
                        Board[row][col] = RED;
                    }
                    else {Board[row][col] = EMPTY;}
                }
            }
        }

        /**
         * A method that changes the turn, from computer to player and vice versa 
         */       
        public void changeTurn(){
            turn = (turn == 1)? -1: 1;
            if( turn == 1) message. setText("Red Player's Turn!");
            else message.setText("Black Player's Turn!");
        }

        int getTurn(){ return turn; }

        void resetTurn(){ turn = 1; }

        void newGame(){
            Board = new int[8][8];
            initialize();
            resetTurn();
        }

        /**
         * A method that checks if there is a winner in the game
         * It does this by checking if any of the pieces are finished
         * @return
         */
        public int checkWinner() {
            if (redNumber == 0) {
                message.setText("Black Wins!");
                return BLACK;
            }
            else if (blackNumber == 0) {
                message.setText("Red Wins!");
                return RED;
            }
            else if (blackNumber == 0 && redNumber == 0){
                message.setText("It's a tie!");
                return TIE;
            }
            return 5;
        }

        /** 
         * @return returns the state of the board
         */
        public static int[][] getState() {
            return Board;
        }
    }

    static class Pair{
        private int row;
        private int col;

        public Pair(int r, int c){
            this.row = r;
            this.col = c;
        }

        public int getRow() {
            return row;
        }

        public int getCol() {
            return col;
        }

        public void setRow(int r) {
            row = r;
        }

        public void setCol(int c) {
            col = c;
        }
    }
}