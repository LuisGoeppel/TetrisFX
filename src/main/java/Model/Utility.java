package Model;

public class Utility {

    public static boolean isGreaterX(Pos lhs, Pos rhs) {
        if (lhs.x == rhs.x) {
            return lhs.y > rhs.y;
        }
        return lhs.x > rhs.x;
    }
    public static boolean isGreaterY(Pos lhs, Pos rhs) {
        if (lhs.y == rhs.y) {
            return lhs.x > rhs.x;
        }
        return lhs.y > rhs.y;
    }

    public static int getLowestY(Pos[] pos) {
        int lowest = Integer.MAX_VALUE;
        for (Pos po : pos) {
            if (po.y < lowest) {
                lowest = po.y;
            }
        }
        return lowest;
    }

    public static int getLowestX(Pos[] pos) {
        int lowest = Integer.MAX_VALUE;
        for (Pos po : pos) {
            if (po.x < lowest) {
                lowest = po.x;
            }
        }
        return lowest;
    }

    public static int getHighestX(Pos[] pos) {
        int highest = Integer.MIN_VALUE;
        for (Pos po : pos) {
            if (po.x > highest) {
                highest = po.x;
            }
        }
        return highest;
    }
}
