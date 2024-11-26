import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GreedyAI extends AIPlayer{

    public GreedyAI (boolean isPlayer1)
    {
        super(isPlayer1);
    }

    @Override
    public Move makeMove(PlayableLogic gameLogic) {
        List<Position> validMoves = gameLogic.ValidMoves();  // Get the list of valid moves

        if (validMoves.isEmpty()) {
            return null;  // If no valid moves, return null
        }

        // Greedy AI: Choose the move that flips the most discs
        Position bestMove = null;
        int maxFlips = -1;

        for (Position move : validMoves) {
            int flips = gameLogic.countFlips(move);  // Count the number of discs that would be flipped by this move
            if (flips > maxFlips) {
                maxFlips = flips;
                bestMove = move;
            }
        }

        // Create and return a Move object with the best move, selected disc, and flipped discs
        Disc currentDisc = this.isPlayerOne() ? new SimpleDisc(this) : new SimpleDisc(this);
        List<Position> flippedDiscs = new ArrayList<>();
        Map<Position, Disc> originalDiscsAtFlippedPositions = new HashMap<>();


        return new Move(bestMove, currentDisc, flippedDiscs, originalDiscsAtFlippedPositions);
    }


}
