package layout;

import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.BitSet;
import java.util.List;

import planar.Block;
import planar.BlockEmbedding;
import planar.Edge;
import planar.Face;
import planar.Vertex;

/**
 * Layout an outerplanar graph, which is a graph where all the vertices are on the 
 * bounding face.
 * 
 * @author maclean
 *
 */
public class OuterplanarLayout extends BaseCircularLayout implements Layout {
    
    public static final double START_ANGLE = Math.toRadians(270);
    
    private double r;
    
    public OuterplanarLayout(double r) {
        this.r = r;
    }

	public Representation layout(BlockEmbedding em, Rectangle2D canvas) {
	    Representation representation = new Representation();
	    Block dual = em.calculateInnerDual();
	    List<Integer> treeCenter = TreeCenterFinder.findCenter(dual);
	    List<Face> faces = em.getFaces();
	    Point2D centerPoint = new Point2D.Double(canvas.getWidth() / 2, canvas.getHeight() / 2);
	    BitSet visited = new BitSet();
//	    System.out.println("treeCenter = " + treeCenter);
	    if (treeCenter.size() == 1) {
	        int centralFace = treeCenter.get(0);
	        int endVertexIndex = 0; // to close the first face
	        layout(centralFace, faces, dual, visited, centerPoint, START_ANGLE, 0, endVertexIndex, representation);
	    } else {
	        int centralFaceA = treeCenter.get(0);
	        int centralFaceB = treeCenter.get(1);
	        layout(centralFaceA, centralFaceB, faces, dual, visited, centerPoint, representation);
	    }
		return representation;
	}
	
	public Representation layout(BlockEmbedding em, Vertex start, Point2D startPoint, Rectangle2D canvas) {
        // TODO Auto-generated method stub
        return null;
    }

    private void layout(int current, List<Face> faces, 
                        Block dual, BitSet visited, 
                        Point2D center, double startAngle, 
                        int startVertexIndex, int endVertexIndex, Representation representation) {
	    Face face = faces.get(current);
//	    System.out.println("drawing face " + current + " " + face + " c=" + center + " a=" + Math.toDegrees(startAngle));
	    visited.set(current);
	    layoutFace(face, center, startAngle, startVertexIndex, endVertexIndex, representation);
	    for (int neighbour : dual.getConnected(current)) {
	        if (visited.get(neighbour)) {
	            continue;
	        } else {
	            Face neighbourFace = faces.get(neighbour);
	            Edge sharedEdge = face.getSharedEdge(neighbourFace);
	            Vertex vA = sharedEdge.getA();
	            Vertex vB = sharedEdge.getB();
	            Point2D pA = representation.getPoint(vA);
	            Point2D pB = representation.getPoint(vB);
	            Point2D nextCenter = getNextCenter(center, pA, pB);
	            double angleA = angle(nextCenter, pA);
	            double angleB = angle(nextCenter, pB);
	            double nextStartAngle;
	            double alpha = getAlpha(neighbourFace.vsize());
	            int nextStartVertexIndex;
	            int nextEndVertexIndex;
	            if (neighbourFace.antiClockwiseOrderedInFace(vA, vB)) {
	                nextStartAngle = angleB + alpha;
	                nextStartVertexIndex = neighbourFace.indexOf(vB);
	                nextEndVertexIndex = neighbourFace.indexOf(vA);
	            } else {
	                nextStartAngle = angleA - alpha;
	                nextStartVertexIndex = neighbourFace.indexOf(vA);
	                nextEndVertexIndex = neighbourFace.indexOf(vB);
	            }
	            nextStartVertexIndex = (nextStartVertexIndex == neighbourFace.vsize() - 1)? 0 : nextStartVertexIndex + 1;
//	            System.out.println("angleB " + toAStr(angleB) + " + alpha " + toAStr(alpha) + " = " + toAStr(nextStartAngle));
//	            System.out.println("angleA " + toAStr(angleA) + " - alpha " + toAStr(alpha) + " = " + toAStr(nextStartAngle));
	            if (nextStartAngle <= 0.0) {
	                nextStartAngle += 2 * Math.PI;
	            } else if (nextStartAngle >= 2 * Math.PI) {
	                nextStartAngle -= 2 * Math.PI;
	            }
	            layout(neighbour, faces, dual, visited, nextCenter, nextStartAngle, nextStartVertexIndex, nextEndVertexIndex, representation);
	        }
	    }
	}
	
	private void layout(int faceIndexA, int faceIndexB, List<Face> faces, Block dual, BitSet visited, Point2D center, Representation representation) {
	    Face faceA = faces.get(faceIndexA);
	    Face faceB = faces.get(faceIndexB);
	    
        Point2D centerA = new Point2D.Double(center.getX(), center.getY());
        visited.set(faceIndexB);     // prevents the subtree rooted at B from being drawn yet
        int endVertexIndex = 0;
        layout(faceIndexA, faces, dual, visited, centerA, START_ANGLE, 0, endVertexIndex, representation);
        
        Edge sharedEdge = faceA.getSharedEdge(faceB);
        Vertex vA = sharedEdge.getA();
        Vertex vB = sharedEdge.getB();
        Point2D pA = representation.getPoint(vA);
        Point2D pB = representation.getPoint(vB);
        Point2D centerB = getNextCenter(center, pA, pB);
        double angleA = angle(centerB, pA);
        double angleB = angle(centerB, pB);
        double nextStartAngle;
        double alpha = getAlpha(faceB.vsize());
        int nextStartVertexIndex;
        int nextEndVertexIndex;
        if (faceB.antiClockwiseOrderedInFace(vA, vB)) {
            nextStartAngle = angleB + alpha;
            nextStartVertexIndex = faceB.indexOf(vB);
            nextEndVertexIndex = faceB.indexOf(vA);
        } else {
            nextStartAngle = angleA - alpha;
            nextStartVertexIndex = faceB.indexOf(vA);
            nextEndVertexIndex = faceB.indexOf(vB);
        }
        // starting at the vertex after the shared edge
        nextStartVertexIndex = (nextStartVertexIndex == faceB.vsize() - 1)? 0 : nextStartVertexIndex + 1;
//        System.out.println("angleB " + toAStr(angleB) + " + alpha " + toAStr(alpha) + " = " + toAStr(nextStartAngle));
//        System.out.println("angleA " + toAStr(angleA) + " - alpha " + toAStr(alpha) + " = " + toAStr(nextStartAngle));
        visited.clear(faceIndexB);   // now draw this subtree
        layout(faceIndexB, faces, dual, visited, centerB, nextStartAngle, nextStartVertexIndex, nextEndVertexIndex, representation);
    }
	
	private double getAlpha(int n) {
	    return Math.toRadians(360.0 / n);
	}
	
	private void layoutFace(Face face, Point2D center, double startAngle, int startVertexIndex, int endVertexIndex, Representation representation) {
//	    System.out.println("face " + face + " between " + face.getVertex(startVertexIndex) + " to " + face.getVertex(endVertexIndex));
	    double alpha = getAlpha(face.vsize());
        
        double currentAngle = startAngle;
        double cx = center.getX();
        double cy = center.getY();
        Vertex prev = (startVertexIndex == 0)? 
                face.getVertex(face.vsize() - 1) : face.getVertex(startVertexIndex - 1);
        int cyclicIndex = startVertexIndex;
        for (int counter = 0; counter < face.vsize(); counter++) {
            Vertex vertex = face.getVertex(cyclicIndex);
            Point2D p;
            boolean isNew = false;
            if (representation.getPoint(vertex) == null) {
                double xp = cx + (r * Math.cos(currentAngle));
                double yp = cy + (r * Math.sin(currentAngle));
    //          System.out.println("ext face pos : " + positions.get(vertex));
                p = new Point2D.Double(xp, yp);
                representation.addPoint(vertex, p);
                isNew = true;
//                System.out.println("drawn new " + vertex + " at " + String.format("(%2.0f %2.0f)", p.getX(), p.getY()));
            } else {
                p = representation.getPoint(vertex);
//                System.out.println("existing vertex " + vertex + " at " + String.format("(%2.0f %2.0f)", p.getX(), p.getY()));

            }
            if (isNew && representation.getPoint(prev) != null) {
//                System.out.println("line between " + prev + " and " + vertex);
                representation.addLine(new Edge(prev, vertex), new Line2D.Double(representation.getPoint(prev), p));
            }
            currentAngle -= alpha;
            if (currentAngle <= 0.0) {
                currentAngle += 2 * Math.PI;
            }
            prev = vertex;
            if (cyclicIndex == face.vsize() - 1) {
                cyclicIndex = 0;
            } else {
                cyclicIndex++;
            }
        }
        int lastIndex = (endVertexIndex == 0)? face.vsize() - 1 : endVertexIndex - 1;
        Vertex closingVertexA = face.getVertex(lastIndex);
        Vertex closingVertexB = face.getVertex(endVertexIndex);
        Point2D pX = representation.getPoint(closingVertexA);
        Point2D pY = representation.getPoint(closingVertexB);
//        System.out.println("line between " + closingVertexA + " and " + closingVertexB + " CLOSE");
        representation.addLine(new Edge(closingVertexA, closingVertexB), new Line2D.Double(pX, pY));
	}

}
