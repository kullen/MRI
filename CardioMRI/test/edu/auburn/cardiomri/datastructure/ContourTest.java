package edu.auburn.cardiomri.datastructure;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import edu.auburn.cardiomri.util.ContourCalc;

public class ContourTest {

    private Contour contour;

    @Before
    public void setUp() {
        contour = new Contour(Contour.Type.LV_EPI);
    }

    @After
    public void tearDown() {
        contour = null;
    }

    @Test
    public void testContourConstructorSetsContourType() {
        Contour.Type expected = Contour.Type.LV_EPI;
        Contour.Type actual = contour.getType();
        assertEquals(expected, actual);
    }

    @Test(expected = NullPointerException.class)
    public void testSetControlPointsThrowsNullPointerExceptionWhenGivenNullList() {
        contour.setControlPoints(null);
        fail("Exception not thrown");
    }

    @Test(expected = IllegalArgumentException.class)
    public void testSetControlPointsThrowsIllegalArgumentExceptionWhenGivenPointsWithNegativeCoordinates() {
        List<ControlPoint> list = new Vector<ControlPoint>();

        list.add(new ControlPoint(1, 2));
        list.add(new ControlPoint(-3, 4)); // The bad point
        list.add(new ControlPoint(5, 6));

        contour.setControlPoints(list);
        fail("Exception not thrown");
    }

    @Test
    public void testSetControlPointsFillsInternalListWithEquivalentPoints() {
        List<ControlPoint> list = new Vector<ControlPoint>();

        list.add(new ControlPoint(1, 2));
        list.add(new ControlPoint(3, 4));
        list.add(new ControlPoint(5, 6));

        contour.setControlPoints(list);
        List<ControlPoint> contourList = contour.getControlPoints();

        for (ControlPoint point : list) {
            assertTrue(contourList.contains(point));
        }
    }

    @Test(expected = IllegalArgumentException.class)
    public void testAddControlPointThrowsIllegalArgumentExceptionWhenGivenNegativeXCoordinate() {
        contour.addControlPoint(-1, 2);
        fail("Exception not thrown");
    }

    @Test(expected = IllegalArgumentException.class)
    public void testAddControlPointThrowsIllegalArgumentExceptionWhenGivenNegativeYCoordinate() {
        contour.addControlPoint(1, -2);
        fail("Exception not thrown");
    }

    @Test
    public void testAddControlPointThrowsIllegalArgumentExceptionWhenPointHasAlreadyBeenAdded() {
        contour.addControlPoint(1, 2);
        contour.addControlPoint(1, 2);
        assertEquals(1, contour.getControlPoints().size());
    }

    @Test
    public void testAddControlPointCreatesPoint2DAndAddsItToControlPointsList() {
        contour.addControlPoint(1, 2);

        ControlPoint expected = new ControlPoint(1, 2);
        ControlPoint actual = contour.getControlPoints().get(0);
        assertEquals(expected, actual);
    }
    
    
    @Test
    public void testControlPointsTooCloseDontAdd() {
        contour.addControlPoint(1, 2);
        contour.addControlPoint(2, 1);
        List<ControlPoint> points = contour.getControlPoints();

        assertEquals(1, points.size());
    }

    @Test
    public void testGetControlPointsReturnsACopyOfTheInternalList() {
        contour.addControlPoint(1, 2);
        contour.addControlPoint(5, 6);
        List<ControlPoint> listWithTwoPoints = contour.getControlPoints();

        contour.addControlPoint(10, 12);
        List<ControlPoint> listWithThreePoints = contour.getControlPoints();

        assertEquals(2, listWithTwoPoints.size());
        assertEquals(3, listWithThreePoints.size());
    }

    @Test
    public void testGetGeneratedPointsReturnsPointsThatIntersectAllControlPoints() {
        contour.addControlPoint(10, 10);
        contour.addControlPoint(15, 15);
        contour.addControlPoint(5, 15);

        List<ControlPoint> controlPoints = contour.getControlPoints();
        List<Point> generatedPoints = contour.getGeneratedPoints();

        for (ControlPoint controlPoint : controlPoints) {
            double minDistance = Double.MAX_VALUE;
            for (Point generatedPoint : generatedPoints) {
                minDistance = Math.min(minDistance,
                        controlPoint.distance(generatedPoint));
            }
            if (minDistance > ContourCalc.SEPARATION_DISTANCE / 2.0) {
                fail(String.format(
                        "Minimum distance from (%1$f, %2$f) to curve is %3$f",
                        controlPoint.getX(), controlPoint.getY(), minDistance));
            }
        }
    }

    @Test
    public void testGetGeneratedPointsReturnsACopyOfTheInternalList() {
        contour.addControlPoint(1, 2);
        contour.addControlPoint(3, 4);
        contour.addControlPoint(5, 6);

        List<Point> copy1 = contour.getGeneratedPoints();
        List<Point> copy2 = contour.getGeneratedPoints();
        copy1.clear();

        assertNotEquals(copy1, copy2);
    }

//    @Test
//    public void testSetContourTypeAffectsReturnValueOfIsClosedCurve() {
//        contour.setContourType(Contour.Type.DEFAULT_CLOSED);
//        boolean initialValue = contour.isClosedCurve();
//
//        contour.setContourType(Contour.Type.DEFAULT_OPEN);
//        boolean finalValue = contour.isClosedCurve();
//
//        assertNotEquals(initialValue, finalValue);
//    }
}
