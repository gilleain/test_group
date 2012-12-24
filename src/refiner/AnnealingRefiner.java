package refiner;

import java.awt.Rectangle;
import java.awt.geom.Point2D;
import java.util.List;
import java.util.Random;

import layout.CircleLayout;
import layout.ParameterSet;
import layout.Representation;
import planar.BlockEmbedding;
import planar.Edge;
import planar.Face;
import planar.Vertex;
import annealing.AdaptiveAnnealingEngine;
import annealing.AnnealerAdapterI;
import annealing.StateListener;

public class AnnealingRefiner implements Refiner {
    
    private Rectangle canvas;
    
    public AnnealingRefiner(Rectangle canvas) {
        this.canvas = canvas;
    }

    @Override
    public Representation refine(Representation representation, BlockEmbedding embedding) {
        int evalSMax = 4;
        Face outerFace = embedding.getExternalFace();
        CircleLayout layout = new CircleLayout(new ParameterSet());
        Representation outer = layout.layout(outerFace, canvas);
        List<Vertex> outerVertices = outer.getVertices();
        Representation initial = new Representation(); 
        for (Vertex v : representation.getVertices()) {
            if (outerVertices.contains(v)) {
                initial.addPoint(v, outer.getPoint(v));
            } else {
                initial.addPoint(v, representation.getPoint(v));
            }
        }
        for (Edge edge : representation.getEdges()) {
            initial.addLine(edge, representation.getLine(edge));
        }
        RepresentationAnnealerAdapter adapter = 
                new RepresentationAnnealerAdapter(representation, embedding, 2);
        AdaptiveAnnealingEngine annealer = new AdaptiveAnnealingEngine(adapter, evalSMax);
        annealer.run();
        return adapter.getBest();
    }
    
    private class RepresentationAnnealerAdapter implements AnnealerAdapterI {
        
        private double bestCost;
        private double currentCost;
        private double nextCost;
        
        private Representation current;
        private Representation best;
        private Representation next;
        
        private Random random;
        private int[][] pathDistances;
        
        private BlockEmbedding embedding;
        
        private int edgeLength;
        
        public RepresentationAnnealerAdapter(Representation rep, BlockEmbedding embedding, int edgeLength) {
            this.embedding = embedding;
            this.edgeLength = edgeLength;
            
            this.current = rep;
            this.next = null;
            this.best = current;
            this.bestCost = this.currentCost = this.nextCost = 0.0;
            
            this.random = new Random();
            this.pathDistances = PathLengthFinder.paths(embedding.getBlock().getConnectionTable());
        }
        
        public Representation getBest() {
            return best;
        }
        
        private double cost(Representation rep) {
            double cost = 0;
            List<Vertex> vertices = rep.getVertices();
            Face outerFace = embedding.getExternalFace();
            for (int v = 0; v < vertices.size(); v++) {
                if (outerFace.contains(vertices.get(v))) continue;
                Point2D pV = rep.getPoint(vertices.get(v));
                for (int w = v + 1; w < vertices.size(); w++) {
                    if (!outerFace.contains(vertices.get(w))) {
                        Point2D pW = rep.getPoint(vertices.get(w));
                        int pathDistance = pathDistances[v][w];
                        int exp = pathDistance * edgeLength;
                        double geomDist = pV.distance(pW);
                        cost += Math.abs(exp - geomDist);
                    }
                }
            }
            return cost;
        }

        @Override
        public void addStateListener(StateListener listener) {
            // don't care about this right now...
        }

        @Override
        public void initialState() {
            // just use the initial rep
            this.currentCost = cost(this.current);
            this.bestCost = this.currentCost;
        }

        @Override
        public void nextState() {
            this.next = makeNext(this.current);
            this.nextCost = cost(this.next);
//            System.out.println(nextCost);
        }
        
        private Representation makeNext(Representation rep) {
            Representation altered = new Representation();
            Face outerFace = embedding.getExternalFace();
            for (Vertex v : rep.getVertices()) {
                Point2D oldP = rep.getPoint(v);
                double newX, newY;
                if (outerFace.contains(v)) {
                    newX = oldP.getX();
                    newY = oldP.getY();
                } else {
                    newX = oldP.getX() + (random.nextDouble() * (random.nextBoolean()? -1 : 1));
                    newY = oldP.getY() + (random.nextDouble() * (random.nextBoolean()? -1 : 1));
                }
                Point2D newP = new Point2D.Double(newX, newY);
                altered.addPoint(v, newP);
            }
            return altered;
        }

        @Override
        public boolean costDecreasing() {
            return this.nextCost < this.currentCost;
        }

        @Override
        public void accept() {
            this.current = this.next;
            this.currentCost = this.nextCost;
            if (this.currentIsBetterThanBest()) {
//              System.out.println("best > current, storing best=" + this.bestCost + " current=" + this.currentCost);
                this.best = this.current;
                this.bestCost = currentCost;
            } else {
//              System.out.println("best !> current, NOT storing best=" + this.bestCost + " current=" + this.currentCost);
            }
        }
        
        private boolean currentIsBetterThanBest() {
            return this.currentCost < this.bestCost;
        }

        @Override
        public void reject() {
            // don't care much...
        }

        @Override
        public double costDifference() {
            return this.currentCost - this.nextCost;
        }
        
    }

}
