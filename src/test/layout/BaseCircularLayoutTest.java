package test.layout;

import java.awt.geom.Point2D;

import layout.BaseCircularLayout;

import org.junit.Test;

public class BaseCircularLayoutTest {
    
    @Test
    public void testGetOpposingPoint() {
        // XXX colinear points!
        Point2D pC = new Point2D.Double(-5.0, 15.4);
        Point2D pP = new Point2D.Double(-16.2, 11.8);
        Point2D pN = new Point2D.Double(6.2, 19.0);
        Point2D p = new BaseCircularLayout().getOpposingPoint(pC, pP, pN, 10);
        System.out.println(p);
        System.out.println(new BaseCircularLayout().getOpposingPoint(
                new Point2D.Double(-5, 11), pP, 10));
    }

}
