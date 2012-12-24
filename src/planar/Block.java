package planar;

import java.util.ArrayList;
import java.util.List;

import org.openscience.cdk.interfaces.IAtomContainer;

public class Block extends GraphObject {

    public Block() {
        super(new ArrayList<Vertex>());
    }

    public Block(List<Vertex> vertices) {
        super(vertices);
    }

    public Block(List<Vertex> vertices, List<Edge> edges) {
        super(vertices, edges);
    }

    public Block(int i) {
        super(i);
    }

    public Block(IAtomContainer g) {
        super(g);
    }

    public Block(Block other) {
        super(new ArrayList<Vertex>(other.vertices), new ArrayList<Edge>(
                other.edges));
    }

}
