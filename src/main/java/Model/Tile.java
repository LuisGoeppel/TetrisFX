package Model;

public class Tile {

    private int shapeID;
    private boolean isActive;
    private Shape shape;

    public Tile(int shapeID, boolean isActive, Shape shape) {
        this.shapeID = shapeID;
        this.isActive = isActive;
        this.shape = shape;
    }

    public Tile() {
        shapeID = 0;
        isActive = false;
        shape = Shape.NULL;
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        isActive = active;
    }

    public int getShapeID() {
        return shapeID;
    }

    public void setShapeID(int shapeID) {
        this.shapeID = shapeID;
    }

    public Shape getShape() {
        return shape;
    }

    public void setShape(Shape shape) {
        this.shape = shape;
    }

    public boolean isFree() {
        return shapeID == 0;
    }

    @Override
    public String toString() {
        if (shapeID == 0) {
            return "--";
        } else if (shapeID < 10) {
            return "0" + shapeID;
        }
        return Integer.toString(shapeID);
    }
}
