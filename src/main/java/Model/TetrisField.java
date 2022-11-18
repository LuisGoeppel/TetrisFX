package Model;

public interface TetrisField {

    /**
     * starts the Game
     */
    void startGame();

    /**
     * Simulates a tick of the game: The currently active tile
     * moves down by one unit, eventually a new Shape is spawned
     * @return if a new Shape was spawned in the tick
     */
    boolean gameTick();

    /**
     * Returns the current Score of the game
     * @return the current score
     */
    int getScore();

    /**
     * Returns the next Shape which will appear
     * @return the next Shape
     */
    Shape getNextShape();

    /**
     * Returns the id of the next Shape, can be used to get information
     * about the next shape's color
     * @return the id of the next shape
     */
    int getNextShapeId();

    /**
     * Moves the currently active Shape to the left, if the movement is possible
     */
    void moveLeft();

    /**
     * Moves the currently active Shape to the right, if the movement is possible
     */
    void moveRight();

    /**
     * Turns the currently active Shape by 90 degrees, if the turn is possible
     */
    void turnActiveShape();

    /**
     * Returns if the game is already lost
     * @return if the game is lost
     */
    boolean isLost();

    /**
     * Returns the current field as an array of Integers
     * @return
     */
    int[][] getField();

    /**
     * Resets the field
     */
    void reset();

}
