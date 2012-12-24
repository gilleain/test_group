package refiner;

import layout.Representation;
import planar.BlockEmbedding;

public interface Refiner {
	
	public Representation refine(Representation representation, BlockEmbedding embedding);

}
