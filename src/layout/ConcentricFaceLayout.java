package layout;

import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.List;

import planar.Block;
import planar.BlockEmbedding;
import planar.Edge;
import planar.Face;
import planar.Vertex;

public class ConcentricFaceLayout extends BaseCircularLayout implements Layout {

    private double r;
    
    public ConcentricFaceLayout(double r) {
        this.r = r;
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
        int faceLayerIndex = faceLayers.size() - 1;
        List<Integer> core = faceLayers.get(faceLayerIndex);
        List<Edge> outerPath = layoutCore(core, faces, rep);
        faceLayerIndex--;
        for (; faceLayerIndex > 0; faceLayerIndex--) {
            List<Integer> layer = faceLayers.get(faceLayerIndex);
            Face currentFace = null;
            int currentFaceIndex = -1;
            List<Edge> nextOuterPath = new ArrayList<Edge>();
            for (Edge edge : outerPath) {
                if (currentFace == null || !currentFace.contains(edge)) {
                    currentFaceIndex = lookupFaceIndex(edge, layer, faces);
                    currentFace = faces.get(currentFaceIndex);
                }
                List<Edge> connectedEdges = currentFace.getConnectedEdges(edge);
                assert connectedEdges.size() == 2;
                Point2D archStart = layoutSpoke(connectedEdges.get(0), edge, rep);
                Point2D archEnd = layoutSpoke(connectedEdges.get(1), edge, rep);
                layoutArch(archStart, archEnd, currentFace.vsize() - 4);
            }
            outerPath = nextOuterPath;
        }
        
        return rep;
    }
    
    private void layoutArch(Point2D archStart, Point2D archEnd, int vsize) {
        // TODO Auto-generated method stub
        
    }

    private Point2D layoutSpoke(Edge spoke, Edge edge, Representation rep) {
        // TODO Auto-generated method stub
        return null;
    }

    /**
     * Order the layer of faces by the edges in outerPath, which is the path of edges around the
     * previous layer.
     *  
     * @param layer
     * @param outerPath
     * @param faces
     * @return
     */
    private List<Integer> orderLayer(List<Integer> layer, List<Edge> outerPath, List<Face> faces) {
        List<Integer> cyclicOrderedLayer = new ArrayList<Integer>();
        Face currentFace = null;
        int currentFaceIndex = -1;
        for (Edge edge : outerPath) {
            if (currentFace == null || !currentFace.contains(edge)) {
                currentFaceIndex = lookupFaceIndex(edge, layer, faces);
                currentFace = faces.get(currentFaceIndex);
            }
            cyclicOrderedLayer.add(currentFaceIndex);
        }
        return cyclicOrderedLayer;
    }
    
    /**
     * Find the first face in the list that contains this edge.
     * 
     * @param edge
     * @param layer
     * @param faces
     * @return
     */
    private int lookupFaceIndex(Edge edge, List<Integer> layer, List<Face> faces) {
        for (int faceIndex : layer) {
            Face face = faces.get(faceIndex);
            if (face.contains(edge)) {
                return faceIndex;
            }
        }
        return -1;
    }
    
    private List<Edge> layoutCore(List<Integer> core, List<Face> faces, Representation rep) {
        List<Edge> outerPath = null;
        if (core.size() == 1) { // simple cyclic core
            Face face = faces.get(core.get(0));
            circularLayout(face, face.vsize(), 0, 0, r, null, null, rep);
            outerPath = face.getEdges();
        } else if (core.size() == 2) {  // pair-core
            Face faceA = faces.get(core.get(0));
            Face faceB = faces.get(core.get(1));
            Edge sharedEdge = faceA.getSharedEdge(faceB);
            // TODO
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
