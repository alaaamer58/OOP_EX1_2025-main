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

        // Loop through all 8 possible directions
        for (int[] direction : directions) {
            List<Position> discsToFlip = new ArrayList<>();
            int drow = direction[0];
            int dcol = direction[1];
            int currRow = row + drow;
            int currCol = col + dcol;

            while (isInBounds(currRow, currCol)) {
                Disc currDisc = board[currRow][currCol];

                if (currDisc == null) {
                    break;  // Stop if we hit an empty space
                }

                if (currDisc.getOwner().isPlayerOne() == currPlayer.isPlayerOne()) {
                    // Flip the opponent's discs between the player's discs
                    for (Position pos : discsToFlip) {
                        board[pos.row()][pos.col()] = disc;  // Flip to the player's disc
                    }
                    break;
                } else {
                    // Collect opponent's discs that might be flipped
                    discsToFlip.add(new Position(currRow, currCol));

                }

                currCol += dcol;
                currRow += drow;
            }
        }

        // Special case for BombDisc - Flip opponent's discs to SimpleDisc
        if (disc instanceof BombDisc) {
            for (int[] direction : directions) {
                int drow = direction[0];
                int dcol = direction[1];
                int currRow = row + drow;
                int currCol = col + dcol;

                while (isInBounds(currRow, currCol)) {
                    Disc currDisc = board[currRow][currCol];

                    if (currDisc == null) {
                        break;  // Stop if we hit an empty space
                    }

                    // Flip the opponent's discs to SimpleDisc
                    if (currDisc.getOwner().isPlayerOne() != currPlayer.isPlayerOne()) {
                        board[currRow][currCol] = new SimpleDisc(currPlayer);  // Flip to SimpleDisc

                    }

                    currRow += drow;
                    currCol += dcol;
                }
            }
        }
    }






    private boolean isInBounds(int row, int col) {
        return row >= 0 && row < board.length && col >= 0 && col < board[row].length;
    }


    public boolean isMoveValid(Position position) {
        if (isInBounds(position.row(),position.col()) ||
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
        int flips = 0;
        Player currPlayer = isPlayer1Turn ? player1 : player2;
        int row = a.row();
        int col = a.col();

        // Check if the position is valid
        if (board[row][col] != null) {
            return 0; // The position is already occupied
        }

        // For BombDisc, check all 8 directions and flip all the opponent's discs
        if (board[row][col] instanceof BombDisc) {
            for (int[] direction : directions) {
                int drow = direction[0];
                int dcol = direction[1];
                int currRow = row + drow;
                int currCol = col + dcol;

                while (isInBounds(currRow, currCol)) {
                    Disc currDisc = board[currRow][currCol];

                    if (currDisc == null) {
                        break; // Stop if an empty space is encountered
                    }

                    if (currDisc.getOwner().isPlayerOne() != currPlayer.isPlayerOne()) {
                        flips++; // Count each opponent's disc
                    }

                    currRow += drow;
                    currCol += dcol;
                }
            }
        } else {
            // For SimpleDisc, check flips in each direction
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

                    if (currentDisc.getOwner().isPlayerOne() == currPlayer.isPlayerOne()) {
                        // If we encounter our own disc, check if we've passed opponent discs
                        if (hasOpponentDisc) {
                            flips += flipsInDirection;
                        }
                        break; // Stop once we encounter our own disc
                    } else {
                        hasOpponentDisc = true;
                        flipsInDirection++; // Count opponent's discs
                    }

                    currRow += dRow;
                    currCol += dCol;
                }
            }
        }

        // If it's UnflippableDisc, return 0 since no flips happen
        if (board[row][col] instanceof UnflippableDisc) {
            return 0;
        }


        return flips;
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
