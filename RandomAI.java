import java.util.*;

public class RandomAI extends AIPlayer{


    public RandomAI(boolean isPlayer1)
    {
        super(isPlayer1);

    }

    @Override
    public Move makeMove (PlayableLogic gameLogic)
    {
        List<Position> validMoves = gameLogic.ValidMoves();

        if (validMoves.isEmpty())
        {
            return null;
        }

        Random random = new Random();
        Position randomMove = validMoves.get(random.nextInt(validMoves.size()));

        Disc currDisc = this.isPlayerOne() ? new SimpleDisc(this) : new SimpleDisc(this);

        List<Position> flippedDisccs = new ArrayList<>();
        Map<Position,Disc> originalDiscsAtFlippedPositions = new HashMap<>();

        return new Move(randomMove , currDisc , flippedDisccs , originalDiscsAtFlippedPositions);
    }
}
