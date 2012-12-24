package layout;

import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import planar.Vertex;

public class BaseCircularLayout {
    
    public Map<Vertex, Point2D> circularLayout(
            List<Vertex> vertices, 
            double cx, double cy, double r, 
            Vertex fromVertex, Point2D fromPt) {
        Map<Vertex, Point2D> positions = new HashMap<Vertex, Point2D>();
        int vcount = vertices.size();
        double alpha = Math.toRadians(360.0 / vcount);
        
        double currentAngle = 0.0;
        for (int vindex = 0; vindex < vcount; vindex++) {
            Vertex vertex = vertices.get(vindex);
            if (fromVertex != null && vertex.equals(fromVertex)) {
                currentAngle = angle(new Point2D.Double(cx, cy), fromPt);
            } else {
                double xp = cx + (r * Math.cos(currentAngle));
                double yp = cy + (r * Math.sin(currentAngle));
                positions.put(vertex, new Point2D.Double(xp, yp));
            }
            currentAngle += alpha;
            if (currentAngle >= 2 * Math.PI) {
                currentAngle -= 2 * Math.PI;
            }
        }
        return positions;
    }
    
    public void circularLayout(
            Iterable<Vertex> vertices, int vcount, 
            double cx, double cy, double r, 
            Vertex fromVertex, Point2D fromPt, Representation rep) {
        double alpha = Math.toRadians(360.0 / vcount);
        
        double currentAngle = 0.0;
        for (Vertex vertex : vertices) {
            if (fromVertex != null && vertex.equals(fromVertex)) {
                currentAngle = angle(new Point2D.Double(cx, cy), fromPt);
            } else {
                double xp = cx + (r * Math.cos(currentAngle));
                double yp = cy + (r * Math.sin(currentAngle));
                rep.addPoint(vertex, new Point2D.Double(xp, yp));
            }
            currentAngle += alpha;
            if (currentAngle >= 2 * Math.PI) {
                currentAngle -= 2 * Math.PI;
            }
        }
    }
    
    public double angle(Point2D pA, Point2D pB) {
        double a = Math.atan2((pB.getY() - pA.getY()), pB.getX() - pA.getX());
        if (a < 0) {
            return 2 * Math.PI + a;
        } else {
            return a;
        }
    }
    
    public Point2D getNextCenter(Point2D fromCenter, Point2D pA, Point2D pB) {
        double eX = (pA.getX() + pB.getX()) / 2;
        double eY = (pA.getY() + pB.getY()) / 2;
        double cX = fromCenter.getX();
        double cY = fromCenter.getY();
        double dX = eX - cX;
        double dY = eY - cY;
        double l = Line2D.ptLineDist(pA.getX(), pA.getY(), pB.getX(), pB.getY(), cX, cY);
        double separation = l * 2;  // XXX only true if the next face is the same size as this one!
        return new Point2D.Double(cX + ((dX / l) * separation), cY + ((dY / l) * separation));
    }

}
