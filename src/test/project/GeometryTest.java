package test.project;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import javax.vecmath.Point2d;

import org.junit.Test;
import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IBond;
import org.openscience.cdk.io.PDBWriter;
import org.openscience.cdk.silent.Atom;
import org.openscience.cdk.silent.AtomContainer;

import projection.StereoProjector;

public class GeometryTest {
    
    public IAtomContainer makeSquashedCube() {
        IAtomContainer ac = new AtomContainer();
        ac.addAtom(new Atom("C", new Point2d(0.9, -0.9)));
        ac.addAtom(new Atom("C", new Point2d(0.9, 0.9)));
        ac.addAtom(new Atom("C", new Point2d(-0.9, 0.9)));
        ac.addAtom(new Atom("C", new Point2d(-0.9, -0.9)));
        
        ac.addAtom(new Atom("C", new Point2d(0.25, -0.25)));
        ac.addAtom(new Atom("C", new Point2d(0.25, 0.25)));
        ac.addAtom(new Atom("C", new Point2d(-0.25, 0.25)));
        ac.addAtom(new Atom("C", new Point2d(-0.25, -0.25)));
        
        ac.addBond(0, 1, IBond.Order.SINGLE);
        ac.addBond(0, 3, IBond.Order.SINGLE);
        ac.addBond(0, 4, IBond.Order.SINGLE);
        ac.addBond(1, 2, IBond.Order.SINGLE);
        ac.addBond(1, 5, IBond.Order.SINGLE);
        ac.addBond(2, 3, IBond.Order.SINGLE);
        ac.addBond(2, 6, IBond.Order.SINGLE);
        ac.addBond(3, 7, IBond.Order.SINGLE);
        ac.addBond(4, 5, IBond.Order.SINGLE);
        ac.addBond(4, 7, IBond.Order.SINGLE);
        ac.addBond(5, 6, IBond.Order.SINGLE);
        ac.addBond(6, 7, IBond.Order.SINGLE);
        return ac;
    }
    
    public void write(String name, File outDir, IAtomContainer atomContainer) throws IOException, CDKException {
        if (!outDir.exists()) {
            outDir.mkdirs();
        }
        File outFile = new File(outDir, name + ".pdb");
        PDBWriter writer = new PDBWriter(new FileWriter(outFile));
        writer.writeMolecule(atomContainer);
        writer.close();
    }
    
    @Test
    public void testSquashedCube() throws IOException, CDKException {
        IAtomContainer ac = makeSquashedCube();
        StereoProjector.projectUpwards(ac);
        write("cube", new File("output/threeDee"), ac);
    }

}
