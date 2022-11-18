package Model;

public class Pos {

    public int x;
    public int y;

    public Pos(int x, int y) {
        this.x = x;
        this.y = y;
    }

    @Override
    public boolean equals(Object rhs) {
        // If the object is compared with itself then return true
        if (rhs == this) {
            return true;
        }

        /* Check if o is an instance of Complex or not
          "null instanceof [type]" also returns false */
        if (!(rhs instanceof Pos)) {
            return false;
        }

        // typecast o to Complex so that we can compare data members
        Pos pos = (Pos) rhs;

        return pos.x == this.x && pos.y == this.y;
    }
}
