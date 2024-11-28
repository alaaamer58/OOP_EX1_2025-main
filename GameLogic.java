import java.util.*;

public class GameLogic implements PlayableLogic{
    private  Disc[][] board; //8x8

    private Stack<Disc[][]> boardStateStack = new Stack<>();

    private int[][] directions = {{-1, 0}, // Up
            {1, 0},  // Down
            {0, -1}, // Left
            {0, 1},  // Right
            {-1, -1}, // Up-Left
            {-1, 1},  // Up-Right
            {1, -1},  // Down-Left
            {1, 1}    // Down-Right
    };

    private Player player1;

    private Player player2;

    private boolean isPlayer1Turn; //return true if its player1's turn

    private int boardSize = 8 ;

    private Stack<Move> moveStack = new Stack<>();

    public GameLogic()
    {
        board = new Disc[boardSize][boardSize];
        initializeBoard();
        isPlayer1Turn =true;
    }

    private void initializeBoard()
    {
        player1 = new HumanPlayer(true);
        player2 = new HumanPlayer(false);

        for (int i =0 ; i< boardSize ; i++)
        {
            for (int j = 0 ; j < boardSize ; j++)
            {
                board[i][j] = null;
            }
        }
        board[3][3] = new SimpleDisc(player1);
        board[3][4] = new SimpleDisc(player2);
        board[4][3] = new SimpleDisc(player2);
        board[4][4] = new SimpleDisc(player1);

    }

    public void flipDisc(Position p, Disc disc) {
        int row = p.row();
        int col = p.col();
        Player currPlayer = disc.getOwner();
        List<Position> flippedBombPositions = new ArrayList<>();

        // Standard flipping logic (like SimpleDisc)
        for (int[] direction : directions) {
            List<Position> discsToFlip = new ArrayList<>();
            int drow = direction[0];
            int dcol = direction[1];
            int currRow = row + drow;
            int currCol = col + dcol;

            while (isInBounds(currRow, currCol)) {
                Disc currDisc = board[currRow][currCol];

                if (currDisc == null) {
                    break; // Stop if we hit an empty space
                }

                if (currDisc instanceof UnflippableDisc) {
                    break; // Stop flipping in this direction if an UnflippableDisc is encountered
                }

                if (currDisc.getOwner().isPlayerOne() == currPlayer.isPlayerOne()) {
                    // Flip all opponent discs in between
                    for (Position pos : discsToFlip) {
                        Disc flippedDisc = board[pos.row()][pos.col()];
                        System.out.println("Player " + (currPlayer.isPlayerOne() ? "1" : "2") + " flipped the ⬤ in (" + pos.row() + ", " + pos.col() + ")");

                        if (flippedDisc instanceof BombDisc) {
                            flippedBombPositions.add(pos); // Track flipped BombDisc
                            board[pos.row()][pos.col()] = new BombDisc(currPlayer); // Keep as BombDisc but change ownership
                        } else {
                            board[pos.row()][pos.col()] = new SimpleDisc(currPlayer); // Flip to SimpleDisc
                        }
                    }
                    break;
                } else {
                    discsToFlip.add(new Position(currRow, currCol)); // Add opponent discs to be flipped
                }

                currRow += drow;
                currCol += dcol;
            }
        }

        // Special handling for flipped BombDiscs
        for (Position bombPos : flippedBombPositions) {
            flipSurroundingDiscs(bombPos, currPlayer);
        }
    }

    // Helper function to flip all surrounding discs of a BombDisc
    private void flipSurroundingDiscs(Position bombPos, Player currPlayer) {
        for (int[] direction : directions) {
            int drow = direction[0];
            int dcol = direction[1];
            int currRow = bombPos.row() + drow;
            int currCol = bombPos.col() + dcol;

            if (isInBounds(currRow, currCol)) {
                Disc currDisc = board[currRow][currCol];

                if (currDisc != null && currDisc.getOwner().isPlayerOne() != currPlayer.isPlayerOne() && !(currDisc instanceof UnflippableDisc)) {
                    System.out.println("Player " + (currPlayer.isPlayerOne() ? "1" : "2") + " flipped the ⬤ in (" + currRow + ", " + currCol + ")");
                    board[currRow][currCol] = new SimpleDisc(currPlayer); // Flip to SimpleDisc
                }
            }
        }
    }













    private boolean isInBounds(int row, int col) {
        return row >= 0 && row < board.length && col >= 0 && col < board[row].length;
    }


    public boolean isMoveValid(Position position) {
        if (position.row() < 0 || position.row() >= boardSize ||
                position.col() < 0 || position.col() >= boardSize ||
                board[position.row()][position.col()] != null) {
            return false;  // If the position is out of bounds or already occupied
        }

        return true;

    }



    @Override
    public boolean locate_disc(Position position, Disc disc) {
        if (!isMoveValid(position)) {
            return false;
        }


        saveBoardState();


        board[position.row()][position.col()] = disc;


        flipDisc(position, disc);


        Move move = new Move(position, disc);
        moveStack.push(move);


        changeTurn();

        return true;
    }

    private void saveBoardState() {
        Disc [][] boardCopy = new Disc[boardSize][boardSize];

        for (int i = 0 ; i < boardSize; i++)
        {
            for (int j = 0 ; j < boardSize ; j++)
            {
                boardCopy[i][j] = board[i][j];
            }
        }
        boardStateStack.push(boardCopy);

    }


    @Override
    public Disc getDiscAtPosition(Position position)
    {
        return board[position.row()][position.col()];
    }

    @Override
    public int getBoardSize() {
        return boardSize;
    }

    @Override
    public List<Position> ValidMoves()
    {
        List <Position> validMOves = new ArrayList<>();

        for (int i = 0 ; i < boardSize; i++)
        {
            for (int j = 0 ; j < boardSize ; j++)
            {
                Position pos = new Position(i,j);
                if (isMoveValid(pos) && countFlips(pos)>0)
                {
                    validMOves.add(pos);
                    System.out.println("Valid move at: " + pos.row() + ", " + pos.col());
                }
            }
        }
        return validMOves;
    }

    @Override
    public int countFlips(Position a) {
        int totalFlips = 0;
        Player currPlayer = isPlayer1Turn ? player1 : player2;
        int row = a.row();
        int col = a.col();

        // Check if the position is valid
        if (board[row][col] != null) {
            return 0; // The position is already occupied
        }

        // Track BombDisc positions to handle cascading flips later
        List<Position> flippedBombPositions = new ArrayList<>();

        // Count flips in all directions
        for (int[] direction : directions) {
            int dRow = direction[0];
            int dCol = direction[1];
            int currRow = row + dRow;
            int currCol = col + dCol;

            int flipsInDirection = 0;
            boolean hasOpponentDisc = false;

            while (isInBounds(currRow, currCol)) {
                Disc currentDisc = board[currRow][currCol];

                if (currentDisc == null) {
                    break; // No more discs to flip if there's an empty space
                }

                if (currentDisc instanceof UnflippableDisc) {
                    // Stop counting in this direction if an UnflippableDisc is encountered
                    flipsInDirection = 0;
                    break;
                }

                if (currentDisc.getOwner().isPlayerOne() == currPlayer.isPlayerOne()) {
                    // If we encounter our own disc
                    if (hasOpponentDisc) {
                        totalFlips += flipsInDirection; // Add the flips in this direction
                    }
                    break; // Stop once we encounter our own disc
                } else {
                    hasOpponentDisc = true;
                    flipsInDirection++; // Count opponent's discs

                    if (currentDisc instanceof BombDisc) {
                        flippedBombPositions.add(new Position(currRow, currCol)); // Track BombDisc for cascading flips
                    }
                }

                currRow += dRow;
                currCol += dCol;
            }
        }


        // Handle cascading flips for BombDisc
        for (Position bombPos : flippedBombPositions) {
            for (int[] direction : directions) {
                int dRow = direction[0];
                int dCol = direction[1];
                int bombRow = bombPos.row() + dRow;
                int bombCol = bombPos.col() + dCol;

                if (isInBounds(bombRow, bombCol)) {
                    Disc surroundingDisc = board[bombRow][bombCol];

                    // Count valid flips for discs surrounding the BombDisc
                    if (surroundingDisc != null && surroundingDisc.getOwner().isPlayerOne() != currPlayer.isPlayerOne() && !(surroundingDisc instanceof UnflippableDisc)) {
                        totalFlips++; // Increment flip count for surrounding discs
                    }
                }
            }
        }

        return totalFlips;
    }





    @Override
    public Player getFirstPlayer() {
        return player1;
    }

    @Override
    public Player getSecondPlayer() {
        return player2;
    }

    @Override
    public void setPlayers(Player player1, Player player2) {
        this.player1 =player1;
        this.player2=player2;
    }

    @Override
    public boolean isFirstPlayerTurn() {
        return isPlayer1Turn;
    }

    @Override
    public boolean isGameFinished() {
        return ValidMoves().isEmpty();
    }

    @Override
    public void reset() {
        board = new Disc[boardSize][boardSize];
        initializeBoard();

        isPlayer1Turn = true;
    }

    @Override
    public void undoLastMove() {
        if (!moveStack.isEmpty()) {
            Move lastMove = moveStack.pop();
            Position lastPosition = lastMove.position();

            if (!boardStateStack.isEmpty()) {
                Disc[][] previousBoardState = boardStateStack.pop();
                for (int i = 0; i < board.length; i++) {
                    for (int j = 0; j < board[i].length; j++) {
                        board[i][j] = previousBoardState[i][j];
                    }
                }
            }


            changeTurn();
        }
    }



    public void changeTurn()
    {
        isPlayer1Turn = !isPlayer1Turn;
    }


}
