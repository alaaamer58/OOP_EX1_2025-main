public class Position {
    private int row;

    private int col;

    public Position (int row, int col)
    {

        this.row = row;

        this.col = col;

    }

    public int col() {
        return col;
    }

    public int row() {
        return row;
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof Position))
            return false;

        if(this == obj)
            return true;

        Position p1 = (Position) obj;

        return this.row == p1.row && this.col == p1.col;
    }

    @Override
    public String toString() {
        return "("+ row + ", " + col + ")";
    }
}
