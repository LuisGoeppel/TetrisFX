package Model;

import java.util.Random;

public class Field implements TetrisField{

    private final int WIDTH = 10;
    private final int HEIGHT = 20;
    private final int HEIGHT_BUFFER = 4;
    private final int SHAPE_SIZE = 4;
    private int currentShapeID;
    private int currentTurnPos;
    private boolean hasStarted;
    private int nextShape;
    private int score;
    private boolean processFinished;
    Random random;

    private Tile[][] field;

    private Pos[] activeTiles;

    private Pos[][] shapeSpawnPositions;

    public Field() {
        random = new Random();
        currentShapeID = 1;
        currentTurnPos = 0;
        nextShape = 0;
        activeTiles = new Pos[SHAPE_SIZE];
        field = new Tile[WIDTH][HEIGHT + HEIGHT_BUFFER];
        for (int x = 0; x < WIDTH; x++) {
            for (int y = 0; y < HEIGHT + HEIGHT_BUFFER; y++) {
                field[x][y] = new Tile();
            }
        }
        initSpawnPositions();
        hasStarted = false;
        processFinished = true;
    }


    @Override
    public boolean gameTick() {
        if (processFinished) {
            processFinished = false;
            boolean canGoDown = true;

            if (Utility.getLowestY(activeTiles) <= 0) {
                canGoDown = false;
            } else {
                for (Pos activeTile : activeTiles) {
                    if (field[activeTile.x][activeTile.y - 1].getShapeID() != 0 &&
                            field[activeTile.x][activeTile.y - 1].getShapeID() !=
                                    field[activeTile.x][activeTile.y].getShapeID()) {

                        canGoDown = false;
                        break;
                    }
                }
            }

            if (canGoDown) {
                sortActiveTilesByY();
                for (int i = 0; i < activeTiles.length; i++) {
                    field[activeTiles[i].x][activeTiles[i].y - 1] = field[activeTiles[i].x][activeTiles[i].y];
                    field[activeTiles[i].x][activeTiles[i].y] = new Tile();
                    activeTiles[i] = new Pos(activeTiles[i].x, activeTiles[i].y - 1);
                }
                processFinished = true;
                return false;
            } else {
                for (int i = 0; i < SHAPE_SIZE; i++) {
                    field[activeTiles[i].x][activeTiles[i].y].setActive(false);
                }
                checkRows();
                spawnNewShape();
                currentTurnPos = 0;
                score += 5;
                processFinished = true;
                return true;
            }
        }
        processFinished = true;
        return false;
    }

    @Override
    public void startGame() {
        if (!hasStarted) {
            hasStarted = true;
            spawnNewShape();
        }
    }

    @Override
    public int getScore() {
        return score;
    }

    @Override
    public Shape getNextShape() {
        switch (nextShape) {
            case 1:
                return Shape.SQUARE;
            case 2:
                return Shape.LINE;
            case 3:
                return Shape.L_SHAPE;
            case 4:
                return Shape.J_SHAPE;
            case 5:
                return Shape.ZIGZAG_LEFT;
            case 6:
                return Shape.ZIGZAG_RIGHT;
            case 7:
                return Shape.PYRAMID;
            default:
                return Shape.NULL;
        }
    }

    @Override
    public int getNextShapeId() {
        return currentShapeID;
    }

    @Override
    public void moveLeft() {
        if (Utility.getLowestX(activeTiles) > 0) {
            boolean canGoLeft = true;
            for (Pos activeTile : activeTiles) {
                if (field[activeTile.x - 1][activeTile.y].getShapeID() != 0 &&
                        field[activeTile.x - 1][activeTile.y].getShapeID() !=
                                field[activeTile.x][activeTile.y].getShapeID()) {

                    canGoLeft = false;
                    break;
                }
            }
            if (canGoLeft) {
                sortActiveTilesByX();
                for (int i = 0; i < activeTiles.length; i++) {
                    field[activeTiles[i].x - 1][activeTiles[i].y] = field[activeTiles[i].x][activeTiles[i].y];
                    field[activeTiles[i].x][activeTiles[i].y] = new Tile();

                    activeTiles[i] = new Pos(activeTiles[i].x - 1, activeTiles[i].y);
                }
            }
        }
    }

    @Override
    public void moveRight() {
        if (Utility.getHighestX(activeTiles) < WIDTH - 1) {
            boolean canGoRight = true;
            for (Pos activeTile : activeTiles) {
                if (field[activeTile.x + 1][activeTile.y].getShapeID() != 0 &&
                        field[activeTile.x + 1][activeTile.y].getShapeID() !=
                                field[activeTile.x][activeTile.y].getShapeID()) {

                    canGoRight = false;
                    break;
                }
            }
            if (canGoRight) {
                sortActiveTilesByX();
                for (int i = activeTiles.length - 1; i >= 0; i--) {
                    field[activeTiles[i].x + 1][activeTiles[i].y] = field[activeTiles[i].x][activeTiles[i].y];
                    field[activeTiles[i].x][activeTiles[i].y] = new Tile();

                    activeTiles[i] = new Pos(activeTiles[i].x + 1, activeTiles[i].y);
                }
            }
        }
    }

    @Override
    public boolean isLost() {
        for (int x = 0; x < WIDTH; x++) {
            if (!field[x][19].isFree() && !field[x][19].isActive()) {
                return true;
            }
        }
        return false;
    }

    @Override
    public int[][] getField() {
        int[][] fieldOfShapeIDs = new int[WIDTH][HEIGHT];
        for (int x = 0; x < WIDTH; x++) {
            for (int y = 0; y < HEIGHT; y++) {
                fieldOfShapeIDs[x][y] = field[x][y].getShapeID();
            }
        }
        return fieldOfShapeIDs;
    }

    @Override
    public void reset() {
        currentShapeID = 1;
        currentTurnPos = 0;
        score = 0;
        for (int x = 0; x < WIDTH; x++) {
            for (int y = 0; y < HEIGHT + HEIGHT_BUFFER; y++) {
                field[x][y] = new Tile();
            }
        }
        hasStarted = false;
        nextShape = 0;
    }

    @Override
    public void turnActiveShape() {
        Shape currentShape = fieldAt(activeTiles[1]).getShape();
        Pos[] newPos;

        if (isAllowedToTurn() && processFinished) {
            processFinished = false;
            switch (currentShape) {
                case LINE:
                    switch (currentTurnPos) {
                        case 0:
                        case 2:
                            newPos = new Pos[]{new Pos(0, -1), new Pos(0, 0),
                                    new Pos(0, 1), new Pos(0, 2)};
                            turnShape(newPos, 1, true);
                            break;
                        case 1:
                        case 3:
                            newPos = new Pos[]{new Pos(-1, 0), new Pos(0, 0),
                                    new Pos(1, 0), new Pos(2, 0)};
                            turnShape(newPos,1, false);
                            break;
                    }
                    break;
                case L_SHAPE:
                    switch (currentTurnPos) {
                        case 0:
                            newPos = new Pos[]{new Pos(-1, 0), new Pos(0, 0),
                                    new Pos(1, 0), new Pos(-1, -1)};
                            turnShape(newPos, 2, false);
                            break;
                        case 1:
                            newPos = new Pos[]{new Pos(-1, 1), new Pos(0, 1),
                                    new Pos(0, 0), new Pos(0, -1)};
                            turnShape(newPos, 2, true);
                            break;
                        case 2:
                            newPos = new Pos[]{new Pos(-1, 0), new Pos(0, 0),
                                    new Pos(1, 0), new Pos(1, 1)};
                            turnShape(newPos, 1, false);
                            break;
                        case 3:
                            newPos = new Pos[]{new Pos(0, 1), new Pos(0, 0),
                                    new Pos(0, -1), new Pos(1, -1)};
                            turnShape(newPos, 1, true);
                            break;
                    }
                    break;
                case J_SHAPE:
                    switch (currentTurnPos) {
                        case 0:
                            newPos = new Pos[]{new Pos(-1, 0), new Pos(0, 0),
                                    new Pos(1, 0), new Pos(-1, 1)};
                            turnShape(newPos, 2, false);
                            break;
                        case 1:
                            newPos = new Pos[]{new Pos(1, 1), new Pos(0, 1),
                                    new Pos(0, 0), new Pos(0, -1)};
                            turnShape(newPos, 2, true);
                            break;
                        case 2:
                            newPos = new Pos[]{new Pos(-1, 0), new Pos(0, 0),
                                    new Pos(1, 0), new Pos(1, -1)};
                            turnShape(newPos, 1, false);
                            break;
                        case 3:
                            newPos = new Pos[]{new Pos(0, 1), new Pos(0, 0),
                                    new Pos(0, -1), new Pos(-1, -1)};
                            turnShape(newPos, 1, true);
                            break;
                    }
                    break;
                case ZIGZAG_LEFT:
                    switch (currentTurnPos) {
                        case 0:
                            newPos = new Pos[]{new Pos(0, 0),new Pos(-1, 0),
                                    new Pos(0, 1), new Pos(1, 1)};
                            turnShape(newPos, 2, true);
                            break;
                        case 1:
                            newPos = new Pos[]{new Pos(0, 0),new Pos(1, 0),
                                    new Pos(0, 1), new Pos(1, -1)};
                            turnShape(newPos, 1, false);
                            break;
                        case 2:
                            newPos = new Pos[]{new Pos(0, 0),new Pos(1, 0),
                                new Pos(0, -1), new Pos(-1, -1)};
                            turnShape(newPos, 1, true);
                            break;
                        case 3:
                            newPos = new Pos[]{new Pos(0, 0),new Pos(-1, 0),
                                    new Pos(0, -1), new Pos(-1, 1)};
                            turnShape(newPos, 2, false);
                            break;
                    }
                    break;
                case ZIGZAG_RIGHT:
                    switch (currentTurnPos) {
                        case 0:
                            newPos = new Pos[]{new Pos(0, 0),new Pos(-1, 0),
                                    new Pos(0, -1), new Pos(1, -1)};
                            turnShape(newPos, 1, true);
                            break;
                        case 1:
                            newPos = new Pos[]{new Pos(0, 0),new Pos(0, 1),
                                    new Pos(-1, 0), new Pos(-1, -1)};
                            turnShape(newPos, 2, true);
                            break;
                        case 2:
                            newPos = new Pos[]{new Pos(0, 0),new Pos(1, 0),
                                    new Pos(0, 1), new Pos(-1, 1)};
                            turnShape(newPos, 2, true);
                            break;
                        case 3:
                            newPos = new Pos[]{new Pos(0, 0),new Pos(1, 0),
                                    new Pos(0, -1), new Pos(1, 1)};
                            turnShape(newPos, 1, true);
                            break;
                    }
                    break;
                case PYRAMID:
                    switch (currentTurnPos) {
                        case 0:
                            newPos = new Pos[]{new Pos(0, 0), new Pos(0, 1),
                                    new Pos(1, 0), new Pos(0, -1)};
                            turnShape(newPos, 1, true);
                            break;
                        case 1:
                            newPos = new Pos[]{new Pos(0, 0), new Pos(-1, 0),
                                    new Pos(1, 0), new Pos(0, -1)};
                            turnShape(newPos, 1, true);
                            break;
                        case 2:
                            newPos = new Pos[]{new Pos(0, 0), new Pos(0, 1),
                                    new Pos(-1, 0), new Pos(0, -1)};
                            turnShape(newPos, 2, true);
                            break;
                        case 3:
                            newPos = new Pos[]{new Pos(0, 0), new Pos(0, 1),
                                    new Pos(1, 0), new Pos(-1, 0)};
                            turnShape(newPos, 2, true);
                            break;
                    }
                    break;
                default:
                    break;
            }
            currentTurnPos++;
            if (currentTurnPos > 3) {
                currentTurnPos = 0;
            }
            processFinished = true;
        }
    }

    private void turnShape(Pos[] pos, int index, boolean sortByX) {
        if (sortByX) {
            sortActiveTilesByX();
        } else {
            sortActiveTilesByY();
        }
        int x = activeTiles[index].x;
        int y = activeTiles[index].y;
        int id = fieldAt(activeTiles[index]).getShapeID();
        Shape shape = fieldAt(activeTiles[index]).getShape();

        clearAll(activeTiles);
        for (int i = 0; i < SHAPE_SIZE; i++) {
            activeTiles[i] = new Pos(x + pos[i].x, y + pos[i].y);
        }
        fillAll(activeTiles, shape, id);
    }

    private void initSpawnPositions() {
        shapeSpawnPositions = new Pos[8][4];
        shapeSpawnPositions[1] = new Pos[]{new Pos(4, 21), new Pos(5, 21),
                new Pos(4, 20), new Pos(5, 20)};
        shapeSpawnPositions[2] = new Pos[]{new Pos(3, 20), new Pos(4, 20),
                new Pos(5, 20), new Pos(6, 20)};
        shapeSpawnPositions[3] = new Pos[]{new Pos(4, 22), new Pos(4, 21),
                new Pos(4, 20), new Pos(5, 20)};
        shapeSpawnPositions[4] = new Pos[]{new Pos(5, 22), new Pos(5, 21),
                new Pos(5, 20), new Pos(4, 20)};
        shapeSpawnPositions[5] = new Pos[]{new Pos(4, 22), new Pos(4, 21),
                new Pos(5, 21), new Pos(5, 20)};
        shapeSpawnPositions[6] = new Pos[]{new Pos(5, 22), new Pos(5, 21),
                new Pos(4, 21), new Pos(4, 20)};
        shapeSpawnPositions[7] = new Pos[]{new Pos(4, 21), new Pos(3, 20),
                new Pos(4, 20), new Pos(5, 20)};
    }

    private void spawnNewShape() {

        int shapeType;
        if (nextShape == 0) {
            nextShape = random.nextInt(7) + 1;
            shapeType = random.nextInt(7) + 1;
        } else {
            shapeType = nextShape;
            nextShape = random.nextInt(7) + 1;
        }

        switch (shapeType) {
            case 1:
                initShape(shapeSpawnPositions[1], Shape.SQUARE);
                break;
            case 2:
                initShape(shapeSpawnPositions[2], Shape.LINE);
                break;
            case 3:
                initShape(shapeSpawnPositions[3], Shape.L_SHAPE);
                break;
            case 4:
                initShape(shapeSpawnPositions[4], Shape.J_SHAPE);
                break;
            case 5:
                initShape(shapeSpawnPositions[5], Shape.ZIGZAG_LEFT);
                break;
            case 6:
                initShape(shapeSpawnPositions[6], Shape.ZIGZAG_RIGHT);
                break;
            case 7:
                initShape(shapeSpawnPositions[7], Shape.PYRAMID);
                break;
            default:
                break;
        }
    }

    private void checkRows() {
        int fullRows = 0;
        for (int y = 0; y < HEIGHT; y++) {
            boolean rowIsFull = true;
            for (int x = 0; x < WIDTH; x++) {
                if (fieldAt(new Pos(x, y)).isFree()) {
                    rowIsFull = false;
                    break;
                }
            }
            if (rowIsFull) {
                fullRows++;
                removeRow(y);
                y--;
            }
        }
        switch (fullRows) {
            case 1:
                score += 100;
                break;
            case 2:
                score += 275;
                break;
            case 3:
                score += 500;
                break;
            case 4:
                score += 1000;
                break;
            default:
                break;
        }
    }

    private void removeRow(int rowY) {
        for (int y = rowY; y < HEIGHT; y++) {
            int tilesMoved = 0;
            for (int x = 0; x < WIDTH; x++) {
                if (!field[x][y + 1].isFree()) {
                    tilesMoved++;
                }
                field[x][y] = field[x][y + 1];
            }
            if (tilesMoved == 0) {
                break;
            }
        }
    }

    private boolean isAllowedToTurn() {
        Shape currentShape = fieldAt(activeTiles[1]).getShape();
        Pos[] newPos;

        switch (currentShape) {
            case LINE:
                switch (currentTurnPos) {
                    case 0:
                    case 2:
                        newPos = new Pos[]{new Pos(0, - 1), new Pos(0, 1), new Pos(0, 2)};
                        return calculateAllowedToTurn(newPos, 2, 1, true);
                    case 1:
                    case 3:
                        newPos = new Pos[]{new Pos(-1, 0), new Pos(1, 0), new Pos(2, 0)};
                        return calculateAllowedToTurn(newPos, 3, 1, false) &&
                                calculateAllowedToTurn(newPos, 4, 1, false);
                }
            case L_SHAPE:
                switch (currentTurnPos) {
                    case 0:
                        newPos = new Pos[]{new Pos(-1, 0), new Pos(-1, -1), new Pos(1, 0)};
                        return calculateAllowedToTurn(newPos, 1, 2, false);
                    case 1:
                        newPos = new Pos[]{new Pos(-1, 1), new Pos(0, 1), new Pos(0, -1)};
                        return calculateAllowedToTurn(newPos, 0, 2, true);
                    case 2:
                        newPos = new Pos[]{new Pos(-1, 0), new Pos(1, 0), new Pos(1, 1)};
                        return calculateAllowedToTurn(newPos, 3, 1, false);
                    case 3:
                        newPos = new Pos[]{new Pos(0, 1), new Pos(0, -1), new Pos(1, -1)};
                        return calculateAllowedToTurn(newPos, 2, 1, true);
                }
                break;
            case J_SHAPE:
                switch (currentTurnPos) {
                    case 0:
                        newPos = new Pos[]{new Pos(-1, 0), new Pos(-1, 1), new Pos(1, 0)};
                        return calculateAllowedToTurn(newPos, 3, 2, false);
                    case 1:
                        newPos = new Pos[]{new Pos(1, 1), new Pos(0, 1), new Pos(0, -1)};
                        return calculateAllowedToTurn(newPos, 0, 2, true);
                    case 2:
                        newPos = new Pos[]{new Pos(-1, 0), new Pos(1, 0), new Pos(1, -1)};
                        return calculateAllowedToTurn(newPos, 1, 1, false);
                    case 3:
                        newPos = new Pos[]{new Pos(0, 1), new Pos(0, -1), new Pos(-1, -1)};
                        return calculateAllowedToTurn(newPos, 0, 1, true);
                }
                break;
            case ZIGZAG_LEFT:
                switch (currentTurnPos) {
                    case 0:
                        newPos = new Pos[]{new Pos(0, 1), new Pos(1, 1)};
                        return calculateAllowedToTurn(newPos, 3, 3, true);
                    case 1:
                        newPos = new Pos[]{new Pos(1, 0), new Pos(1, -1)};
                        return calculateAllowedToTurn(newPos, 2, 1, false);
                    case 2:
                        newPos = new Pos[]{new Pos(0, -1), new Pos(-1, -1)};
                        return calculateAllowedToTurn(newPos, 1, 0, true);
                    case 3:
                        newPos = new Pos[]{new Pos(-1, 0), new Pos(-1, 1)};
                        return calculateAllowedToTurn(newPos, 0, 2, false);
                }
                break;
            case ZIGZAG_RIGHT:
                switch (currentTurnPos) {
                    case 0:
                        newPos = new Pos[]{new Pos(-1, 0), new Pos(1, -1)};
                        return calculateAllowedToTurn(newPos, 1, 1, true);
                    case 1:
                        newPos = new Pos[]{new Pos(0, 1), new Pos(-1, -1)};
                        return calculateAllowedToTurn(newPos, 0, 2, true);
                    case 2:
                        newPos = new Pos[]{new Pos(1, 0), new Pos(-1, 1)};
                        return calculateAllowedToTurn(newPos, 3, 2, true);
                    case 3:
                        newPos = new Pos[]{new Pos(0, -1), new Pos(1, 1)};
                        return calculateAllowedToTurn(newPos, 2, 1, true);
                }
                break;
            case PYRAMID:
                switch (currentTurnPos) {
                    case 0:
                        newPos = new Pos[]{new Pos(0, -1)};
                        return calculateAllowedToTurn(newPos, 2, 1, true);
                    case 1:
                        newPos = new Pos[]{new Pos(-1, 0)};
                        return calculateAllowedToTurn(newPos, 1, 1, true);
                    case 2:
                        newPos = new Pos[]{new Pos(0, 1)};
                        return calculateAllowedToTurn(newPos, 0, 2, true);
                    case 3:
                        newPos = new Pos[]{new Pos(1, 0)};
                        return calculateAllowedToTurn(newPos, 3, 2, true);
                }
                break;
        }
        return true;
    }

    private boolean calculateAllowedToTurn(Pos[] checkPositions, int checkSides,
                                           int index, boolean sortByX) {
        if (sortByX) {
            sortActiveTilesByX();
        } else {
            sortActiveTilesByY();
        }
        int x = activeTiles[index].x;
        int y = activeTiles[index].y;

        switch (checkSides) {
            case 1:
                if (x <= 0) {
                    return false;
                }
                break;
            case 2:
                if (y <= 0) {
                    return false;
                }
                break;
            case 3:
                if (!(x + 1 < WIDTH)) {
                    return false;
                }
                break;
            case 4:
                if (!(x + 2 < WIDTH)) {
                    return false;
                }
            default:
                break;
        }

        for (Pos pos : checkPositions) {
            if (!fieldAt(x + pos.x, y + pos.y).isFree()) {
                return false;
            }
        }
        return true;
    }

    private void initShape(Pos[] positions, Shape shape) {
        for (Pos position : positions) {
            initTile(position, shape);
        }
        currentShapeID++;

        System.arraycopy(positions, 0, activeTiles, 0, positions.length);
    }

    private void initTile(Pos pos, Shape shape) {
        int x = pos.x;
        int y = pos.y;
        field[x][y].setActive(true);
        field[x][y].setShape(shape);
        field[x][y].setShapeID(currentShapeID);
    }

    private void sortActiveTilesByX() {
        int n = activeTiles.length;
        for (int j = 1; j < n; j++) {
            Pos key = activeTiles[j];
            int i = j - 1;
            while ((i > -1) && (Utility.isGreaterX(activeTiles[i], key))) {
                activeTiles[i + 1] = activeTiles[i];
                i--;
            }
            activeTiles[i + 1] = key;
        }
    }

    private void sortActiveTilesByY() {
        int n = activeTiles.length;
        for (int j = 1; j < n; j++) {
            Pos key = activeTiles[j];
            int i = j - 1;
            while ((i > -1) && (Utility.isGreaterY(activeTiles[i], key))) {
                activeTiles[i + 1] = activeTiles[i];
                i--;
            }
            activeTiles[i + 1] = key;
        }
    }

    private Tile fieldAt(Pos pos) {
        return field[pos.x][pos.y];
    }

    private Tile fieldAt(int x, int y) {
        return fieldAt(new Pos(x, y));
    }

    private void clearAll(Pos[] pos) {
        for (Pos po : pos) {
            field[po.x][po.y] = new Tile();
        }
    }

    private void fillAll(Pos[] pos, Shape shape, int id) {
        for (Pos po : pos) {
            field[po.x][po.y].setActive(true);
            field[po.x][po.y].setShapeID(id);
            field[po.x][po.y].setShape(shape);
        }
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        for (int y = HEIGHT - 1; y >= 0; y--) {
            for (int x = 0; x < WIDTH; x++) {
                builder.append(field[x][y]).append(" ");
            }
            builder.append("\n");
        }
        return builder.toString();
    }
}
