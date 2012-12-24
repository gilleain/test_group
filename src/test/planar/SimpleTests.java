package test.planar;

import org.junit.Test;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.templates.MoleculeFactory;

import planar.Block;
import planar.BlockEmbedding;
import planar.AtomContainerEmbedder;
import planar.AtomContainerEmbedding;

public class SimpleTests {
    
    public void layout(IAtomContainer atomContainer) {
        AtomContainerEmbedding embedding = AtomContainerEmbedder.embed(atomContainer);
        int blockIndex = 0;
        for (Block block : embedding.getBlockParts()) {
            BlockEmbedding blockEmbedding = embedding.getBlockEmbedding(blockIndex);
            System.out.println(blockIndex + "\t" + block + "\t" + blockEmbedding);
            blockIndex++;
        }
    }

    @Test
    public void testAdenine() {
        IAtomContainer adenine = MoleculeFactory.makeAdenine();
        layout(adenine);
    }
    
    @Test
    public void testSteran() {
        IAtomContainer steran = MoleculeFactory.makeSteran();
        layout(steran);
    }
    
    @Test
    public void testBiphenyl() {
        IAtomContainer biphenyl = MoleculeFactory.makeBiphenyl();
        layout(biphenyl);
    }
}
