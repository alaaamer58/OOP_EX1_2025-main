public class HumanPlayer extends Player{

    public HumanPlayer (boolean isPlayer1)
    {
        super(isPlayer1);
    }

    @Override
    boolean isHuman() {
        return true;
    }

    public boolean makeMove (PlayableLogic gameLogic , Position position)
    {
        if (((GameLogic)gameLogic).isMoveValid(position))
        {
            gameLogic.locate_disc(position,new SimpleDisc(this));

            return true;
        }

        return false; // Invalid move

    }
}
