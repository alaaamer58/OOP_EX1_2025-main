import java.util.List;
import java.util.Map;

public class Move {
    private final Position position;
    private final Disc disc;
    private List<Position> flippedDiscs;
    private Map<Position, Disc> originalDiscsAtFlippedPositions;

    public Move(Position position, Disc disc, List<Position> flippedDiscs, Map<Position, Disc> originalDiscsAtFlippedPositions) {
        this.position = position;
        this.disc = disc;
        this.flippedDiscs = flippedDiscs;
        this.originalDiscsAtFlippedPositions = originalDiscsAtFlippedPositions;
    }

    public Position position() {
        return this.position;
    }

    public Disc disc() {
        return this.disc;
    }

    // Changed method name to lowercase following Java conventions
    public List<Position> flippedDiscs() {
        return this.flippedDiscs;
    }

    // Changed method name to lowercase following Java conventions
    public Map<Position, Disc> originalDiscsAtFlippedPositions() {
        return originalDiscsAtFlippedPositions;
    }
}
