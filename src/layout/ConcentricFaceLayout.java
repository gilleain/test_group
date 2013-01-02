package layout;

import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.List;

import planar.Block;
import planar.BlockEmbedding;
import planar.Edge;
import planar.Face;
import planar.Path;
import planar.Vertex;

public class ConcentricFaceLayout extends BaseCircularLayout implements Layout {

    private double r;
    
    private double edgeLen;
    
    public ConcentricFaceLayout(double r, double edgeLen) {
        this.r = r;
        this.edgeLen = edgeLen;
    }
    
    @Override
    public Representation layout(BlockEmbedding em, Rectangle2D canvas) {
        return layout(em, null, null, canvas);
    }

    @Override
    public Representation layout(
            BlockEmbedding em, Vertex start, Point2D startPoint, Rectangle2D canvas) {
        Representation rep = new Representation();
        
        List<List<Integer>> faceLayers = calculateFaceLayers(em);
        List<Face> faces = em.getFaces();
        
        // layout the core as the N-1th layer
        List<Integer> core = faceLayers.get(faceLayers.size() - 1);
        Point2D center = new Point2D.Double(canvas.getCenterX(), canvas.getCenterY());
        List<Edge> outerPath = layoutCore(core, faces, center, rep);
        
        // for the rest of the layers, layout spokes and arches
        for (int faceLayerIndex = faceLayers.size() - 2; faceLayerIndex > -1; faceLayerIndex--) {
            List<Integer> layer = faceLayers.get(faceLayerIndex);
            List<Edge> nextOuterPath = new ArrayList<Edge>();
            for (int faceIndex : layer) {
                Face face = faces.get(faceIndex);
                layoutFace(face, outerPath, nextOuterPath, center, rep);
            }
          
            outerPath = nextOuterPath;
        }
        
        return rep;
    }
    
    private void layoutFace(Face face, List<Edge> outerPath, 
                            List<Edge> nextOP, Point2D totalCenter, Representation rep) {
        Edge spokeA = null;
        Edge pathEdgeA2 = null;
        Edge pathEdgeA1 = null;
        Edge spokeB = null;
        Edge pathEdgeB1 = null;
        Edge pathEdgeB2 = null;
        System.out.println("laying out Face " + face + " in " + outerPath);
        
        int opSize = outerPath.size();
        for (Edge faceEdge : face.getEdges()) {
            for (int outerPathIndex = 0; outerPathIndex < outerPath.size(); outerPathIndex++) {
                Edge pathEdge = outerPath.get(outerPathIndex); 
                if (faceEdge.adjacent(pathEdge) && !outerPath.contains(faceEdge)) {
                    if (spokeA == null) {
                        spokeA = faceEdge;
                        pathEdgeA1 = pathEdge;
                        int nextIndex = (outerPathIndex < opSize - 1)? outerPathIndex + 1 : 0; 
                        Edge nextPathEdge = outerPath.get(nextIndex);
                        if (spokeA.adjacent(nextPathEdge)) {
                            pathEdgeA2 = nextPathEdge;
                        } else {
                            int prevIndex = (outerPathIndex > 0)? outerPathIndex - 1 : opSize - 1;
                            pathEdgeA2 = outerPath.get(prevIndex);
                        }
                    } else {
                        spokeB = faceEdge;
                        pathEdgeB1 = pathEdge;
                        int nextIndex = (outerPathIndex < opSize - 1)? outerPathIndex + 1 : 0; 
                        Edge nextPathEdge = outerPath.get(nextIndex);
                        if (spokeB.adjacent(nextPathEdge)) {
                            pathEdgeB2 = nextPathEdge;
                        } else {
                            int prevIndex = (outerPathIndex > 0)? outerPathIndex - 1 : opSize - 1;
                            pathEdgeB2 = outerPath.get(prevIndex);
                        } 
                    }
                }
            }
        }
        System.out.println("spoke A " + spokeA + " pathA1 " + pathEdgeA1 + " pathA2 " + pathEdgeA2 + " in " + face);
        Vertex archStart = layoutSpoke(spokeA, pathEdgeA1, pathEdgeA2, face, rep);
        System.out.println("spoke B " + spokeB + " pathB1 " + pathEdgeB1 + " pathB2 " + pathEdgeB2 + " in " + face);
        Vertex archEnd   = layoutSpoke(spokeB, pathEdgeB1, pathEdgeB2, face, rep);
        nextOP.addAll(layoutArch(
                archStart, spokeA, archEnd, spokeB, face, outerPath, totalCenter, rep));
    }
    
    private Vertex layoutSpoke(Edge spoke, Edge edgeA, Edge edgeB, Face face, Representation rep) {
        Vertex centerVertex = spoke.getSharedVertex(edgeA);
        Vertex otherVertex = edgeB.other(centerVertex);
        assert centerVertex != null;
        Point2D pC = rep.getPoint(centerVertex);
        Point2D pP = rep.getPoint(otherVertex);
        Point2D pN = rep.getPoint(edgeA.other(centerVertex));
        System.out.println("pC " + centerVertex + " = " + f(pC) + 
                " pP " + otherVertex + " = " + f(pP) + 
                " pN " + edgeA.other(centerVertex) + " = " + f(pN));
        Point2D p = getOpposingPoint(pC, pP, pN, r);
        if (Double.isNaN(p.getX()) || Double.isNaN(p.getY())) {
            Point2D fc = getCenter(face, rep);
            System.out.println("FACE CENTER = " + fc);
            p = getOpposingPoint(pC, fc, r);    // XXX wrong way round?
        }
        Vertex spokeVertex = spoke.other(centerVertex); 
        rep.addPoint(spokeVertex, p);
        System.out.println("spoke " + spokeVertex + " @ " + f(p));
        rep.addLine(spoke, new Line2D.Double(pC, p));
        return spokeVertex;
    }
    
    private Point2D getCenter(Face face, Representation rep) {
        double avgX = 0;
        double avgY = 0;
        for (Vertex v : face) {
            Point2D p = rep.getPoint(v);
            if (p != null) {
                avgX += p.getX();
                avgY += p.getY();
            }
        }
        return new Point2D.Double(avgX / face.vsize(), avgY / face.vsize());
    }
    
    private String f(Point2D p) {
        if (p == null) {
            return "NULL";
        } else {
            return String.format("(%.1f,  %.1f)", p.getX(), p.getY());
        }
    }
    
    private String d(double rad) {
        return String.valueOf(Math.round(Math.toDegrees(rad)));
    }
    
    private List<Edge> layoutArch(Vertex archStart, Edge spokeA, 
                                   Vertex archEnd, Edge spokeB, 
                                   Face face, List<Edge> outerPath,
                                   Point2D totalCenter,
                                   Representation rep) {
        System.out.println("Getting arch of " + face + " from " + archStart + " to " + archEnd);
        Path arch = new Path();
        List<Edge> edges = face.getEdges();
        
        // get the index of the spoke 
        int startIndex = edges.indexOf(spokeA);

        // the arch edge should be the next one in the face
        int nextIndex = startIndex + 1;
        if (nextIndex >= face.esize()) {
            nextIndex = 0; 
        }
        Edge archEdge = edges.get(nextIndex);
        
        // check that the next edge is forward (CW) around the face
        boolean forwards = true;
        int edgeIndex;
        if (outerPath.contains(archEdge)) {
            int prevIndex = (startIndex == 0)? edges.size() - 1 : startIndex - 1;
            archEdge = edges.get(prevIndex);
            forwards = false;
            edgeIndex = prevIndex;
        } else {
            edgeIndex = nextIndex;
        }
        
        // check for single-edge arches
        if (archEdge.other(archStart).equals(archEnd)) {
            arch.addEdge(archEdge);
        } else {
        
            Vertex prev = archStart;
            while (prev != archEnd) {
                archEdge = edges.get(edgeIndex);
                Vertex next = archEdge.other(prev);
                arch.add(prev);
                arch.add(next);
                arch.add(prev, next);
                System.out.println("prev " + prev + " archEdge " + archEdge);
               
                if (forwards) {
                    if (edgeIndex < edges.size()) {
                        edgeIndex++;
                    } else {
                        edgeIndex = 0;
                    }
                } else {
                    if (edgeIndex > 0) {
                        edgeIndex--;
                    } else {
                        edgeIndex = edges.size() - 1;
                    }
                }
                prev = archEdge.other(prev);
            }
        }
        
        System.out.println("Laying out arch " + arch);
        Point2D archStartP = rep.getPoint(archStart);
        Point2D archEndP = rep.getPoint(archEnd);
        List<Edge> newOuterPath = new ArrayList<Edge>();
        if (arch.vsize() == 2) {
            Edge edge = face.getEdge(archStart, archEnd);
            rep.addLine(edge, new Line2D.Double(archStartP, archEndP));
            newOuterPath.add(edge);
        } else {
            Point2D archCenter = getArchCenter(archStartP, archEndP);
            double currentAngle = angle(archCenter, archStartP);
            
            boolean isLeft = super.isLeft(totalCenter, archStartP, archEndP);
            System.out.println("ISLEFT " + isLeft + " ARCH " + arch);
            
            double addAngle = Math.toRadians(180 / (arch.vsize() - 1));
            Point2D prevPoint = archStartP;
            
            List<Edge> archEdges = arch.getEdges();
    
            int archEdgeIndex = 0;
            int vertexIndex = 1;
            System.out.println("Arch from " + d(currentAngle) + 
                               " deg by " + d(addAngle) + " deg , Forwards = " + forwards);
            for (int counter = 1; counter < arch.vsize() - 1; counter++) {
                Vertex vertex = arch.getVertex(vertexIndex);
                if (isLeft) {
                    currentAngle += addAngle;
                    if (currentAngle >= 2 * Math.PI) {
                        currentAngle -= 2 * Math.PI;
                    }
                } else {
                    currentAngle -= addAngle;
                    if (currentAngle <= 0) {
                        currentAngle += 2 * Math.PI;
                    }
                }
                Point2D nextP = makeNextPoint(archCenter, currentAngle, edgeLen);
                rep.addPoint(vertex, nextP);
                System.out.println("setting " + vertex + " to " + 
                                   f(nextP) + " ang = " + d(currentAngle));
                Line2D line = new Line2D.Double(prevPoint, nextP);
                vertexIndex++;
                prevPoint = nextP;

                if (archEdgeIndex < arch.esize()) {
                    Edge edge = archEdges.get(archEdgeIndex);
                    rep.addLine(edge, line);
                    newOuterPath.add(edge);
                    archEdgeIndex++;
                }
            }
        }
//        System.out.println("Returning outer path " + newOuterPath);
        System.out.println("Returning outer path " + arch.getEdges());
//        return newOuterPath;
        return arch.getEdges();
    }

    private Point2D getArchCenter(Point2D archStartP, Point2D archEndP) {
        double mx = (archStartP.getX() + archEndP.getX()) / 2;
        double my = (archStartP.getY() + archEndP.getY()) / 2;
        // XXX - for now, just use the midpoint
        return new Point2D.Double(mx, my);
    }

    private List<Edge> layoutCore(
            List<Integer> core, List<Face> faces, Point2D center, Representation rep) {
        List<Edge> outerPath = null;
        if (core.size() == 1) { // simple cyclic core
            Face face = faces.get(core.get(0));
            circularLayout(face, face.vsize(), center.getX(), center.getY(), r, null, null, rep);
            for (Edge e : face.getEdges()) {
                Line2D line = new Line2D.Double(rep.getPoint(e.getA()), rep.getPoint(e.getB()));
                rep.addLine(e, line);
            }
            outerPath = face.getEdges();
        } else if (core.size() == 2) {  // pair-core
            Face faceA = faces.get(core.get(0));
            Face faceB = faces.get(core.get(1));
            Edge sharedEdge = faceA.getSharedEdge(faceB);
            outerPath = new ArrayList<Edge>();
            double cx = center.getX();
            double cy = center.getY();
            double pAx = cx - r;
            double pBx = cx + r;
            circularLayout(faceA, faceA.vsize(), pAx, cy, r, null, null, rep);
            circularLayout(faceB, faceB.vsize(), pBx, cy, r, null, null, rep);
            int edgeIndexA = 0;
            int edgeIndexB = 0;
            List<Edge> edgesA = faceA.getEdges();
            List<Edge> edgesB = faceB.getEdges();
            boolean inA = true;
            for (int counter = 0; counter < faceA.esize() + faceB.esize() - 2; counter++) {
                Edge currentEdge;
                if (inA) {
                    currentEdge = edgesA.get(edgeIndexA);
                } else {
                    currentEdge = edgesB.get(edgeIndexB);
                }
                if (currentEdge.equals(sharedEdge)) {
                    inA = !inA;
                    counter--;
                    
                }
                
            }
        } else {
            // TODO
        }
        return outerPath;
    }
    
    private List<List<Integer>> calculateFaceLayers(BlockEmbedding embedding) {
        Block dual = embedding.calculateInnerDual();
        int count = dual.vsize();
        List<List<Integer>> faceLayers = new ArrayList<List<Integer>>();
        
        Face outerFace = embedding.getExternalFace();
        List<Face> faces = embedding.getFaces();
        BitSet seen = new BitSet(count);
        
        // get the outer edge of faces
        List<Integer> currentLayer = new ArrayList<Integer>();
        for (int faceIndex = 0; faceIndex < count; faceIndex++) {
            Face face = faces.get(faceIndex);
            if (face.sharesEdge(outerFace)) {
                currentLayer.add(faceIndex);
                seen.set(faceIndex);
            }
        }
        faceLayers.add(currentLayer);
        
        // go inwards
        while (seen.cardinality() < count) {
            List<Integer> nextLayer = new ArrayList<Integer>();
            for (int faceIndex : currentLayer) {
                for (int connectedFace : dual.getConnected(faceIndex)) {
                    if (!seen.get(connectedFace)) {
                        nextLayer.add(connectedFace);
                        seen.set(connectedFace);
                    }
                }
            }
            faceLayers.add(nextLayer);
            currentLayer = nextLayer;
        }
        
        return faceLayers;
    }

}