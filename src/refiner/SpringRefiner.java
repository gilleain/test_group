package refiner;

import java.awt.geom.Point2D;
import java.util.Arrays;
import java.util.List;

import javax.vecmath.Point2d;
import javax.vecmath.Vector2d;

import layout.Representation;
import planar.Block;
import planar.BlockEmbedding;
import planar.Vertex;

public class SpringRefiner implements Refiner {
    
    private int maxIter;
    
    public SpringRefiner(int maxIter) {
        this.maxIter = maxIter;
    }
    
    @Override
    public Representation refine(Representation representation, BlockEmbedding embedding) {
        List<Vertex> vertices = representation.getVertices();
        Block block = embedding.getBlock();
        int[][] pathDistances = PathLengthFinder.paths(block.getConnectionTable());
        System.out.println(Arrays.deepToString(pathDistances));
        
        int n = vertices.size();
        Point2d[] points = new Point2d[n];
        for (int i = 0; i < n; i++) {
            Point2D point = representation.getPoint(vertices.get(i)); 
            points[i] = new Point2d(point.getX(), point.getY());
        }
        
        double totalKineticEnergy;
        double temp = 3 * n;
        do {
            totalKineticEnergy = 0;
            for (int i = 0; i < n; i++) {
                Point2d pointV = points[i];
                double netForce = 0;
                Vector2d averageDirection = new Vector2d();
                
                for (int j = 0; j < n; j++) {
                    if (i != j) {
                        Point2d pointW = points[j];
                        int graphDistance = pathDistances[i][j];
                        Vector2d v = new Vector2d(pointV);
                        v.sub(pointW);
                        v.normalize();
                        netForce += Math.abs(v.length() - graphDistance);
                        averageDirection.add(v);
                    }
                }
                double f = netForce * temp * 0.00001;
                averageDirection.x *= f;
                averageDirection.y *= f;
                
                // calculate velocites
                points[i].add(averageDirection);
                totalKineticEnergy += f;
            }
            temp--;
            System.out.println(totalKineticEnergy);
        } while (totalKineticEnergy > 0);
        
        return toRepresentation(points, vertices);
    }
    
    private Representation toRepresentation(Point2d[] points, List<Vertex> vertices) {
        Representation refined = new Representation();
        for (int i = 0; i < points.length; i++) {
            Point2d p = points[i];
            refined.addPoint(vertices.get(i), new Point2D.Double(p.x, p.y));
        }
        return refined;
    }

}
