package test.cycle;

import java.util.Iterator;

import org.junit.Test;
import org.openscience.cdk.DefaultChemObjectBuilder;
import org.openscience.cdk.group.AtomContainerPrinter;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IChemObjectBuilder;

import planar.Block;
import planar.CycleFinder;

public class CycleFinderTest {
    
    public final static IChemObjectBuilder builder = DefaultChemObjectBuilder.getInstance();
    
    @Test
    public void iterationTest() {
        IAtomContainer atomContainer = AtomContainerPrinter.fromString(
                "C0C1C2C3C4C5C6C7C8C9C0C1 0:1(1),0:3(1),0:4(1)," +
                "1:2(1),1:5(1),2:3(1),2:6(1),3:7(1),4:8(1),4:11(1)," +
                "5:8(1),5:9(1),6:9(1),6:10(1),7:10(1),7:11(1)", builder);
        Block block = new Block(atomContainer);
        Iterator<Block> cycles = CycleFinder.getCycleStream(block, true); 
        while (cycles.hasNext()) {
            Block cycle = cycles.next();
            System.out.println(CycleFinder.isSimpleConnected(cycle) + "\t" + cycle);
        }
    }

}
