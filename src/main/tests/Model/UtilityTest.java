package Model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class UtilityTest {

    Pos posLargeX = new Pos(100, 3);
    Pos posLargeY = new Pos(3, 100);
    Pos posSmallValues = new Pos(2, 2);
    Pos posNegativeValues = new Pos(-2, -1);
    Pos posLargeValues = new Pos(100, 100);
    Pos posLargeValues2 = new Pos(100, 100);

    Pos[] allPositions = new Pos[]{posLargeValues, posLargeValues2, posLargeX,
            posLargeY, posNegativeValues, posSmallValues};

    @Test
    public void isGreaterXTrueTest() {
        boolean result = Utility.isGreaterX(posLargeX, posSmallValues);
        assertTrue(result);
    }

    @Test
    public void isGreaterXFalseTest() {
        boolean result = Utility.isGreaterX(posNegativeValues, posLargeValues);
        assertFalse(result);
    }

    @Test
    public void isGreaterYTrueTest() {
        boolean result = Utility.isGreaterY(posLargeY, posSmallValues);
        assertTrue(result);
    }

    @Test
    public void isGreaterYFalseTest() {
        boolean result = Utility.isGreaterY(posNegativeValues, posLargeValues);
        assertFalse(result);
    }

    @Test
    public void findSmallestXText() {
        int result = Utility.getLowestX(allPositions);
        assertEquals(result, -2);
    }

    @Test
    public void findSmallestYText() {
        int result = Utility.getLowestY(allPositions);
        assertEquals(result, -1);
    }

    @Test
    public void findHighestXTest() {
        int result = Utility.getHighestX(allPositions);
        assertEquals(result, 100);
    }

}