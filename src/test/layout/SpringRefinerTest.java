package test.layout;

import java.awt.geom.Rectangle2D;

import layout.ConcentricCircularLayout;
import layout.Representation;

import org.junit.Test;

import planar.Block;
import planar.BlockEmbedding;
import planar.PlanarBlockEmbedder;
import refiner.SpringRefiner;

public class SpringRefinerTest {
    
    @Test
    public void testSquare() {
        Block square = new Block(4);
        square.add(0, 1, 2);
        square.add(1, 3);
        square.add(2, 3);
        BlockEmbedding embedding = PlanarBlockEmbedder.embed(square, null);
        Rectangle2D canvas = new Rectangle2D.Double(0, 0, 500, 500);
        Representation rep = new ConcentricCircularLayout().layout(embedding, canvas);
        System.out.println(rep);
        SpringRefiner refiner = new SpringRefiner(10);
        rep = refiner.refine(rep, embedding);
        System.out.println(rep);
    }

}
