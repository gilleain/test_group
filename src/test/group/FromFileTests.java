package test.group;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import org.junit.Test;
import org.openscience.cdk.DefaultChemObjectBuilder;
import org.openscience.cdk.exception.NoSuchAtomException;
import org.openscience.cdk.graph.invariant.EquivalentClassPartitioner;
import org.openscience.cdk.group.AtomContainerPrinter;
import org.openscience.cdk.group.AtomDiscretePartitionRefiner;
import org.openscience.cdk.group.Partition;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IChemObjectBuilder;
import org.openscience.cdk.io.iterator.IIteratingChemObjectReader;
import org.openscience.cdk.io.iterator.IteratingSDFReader;
import org.openscience.cdk.io.iterator.IteratingSMILESReader;

import util.ArrayToPartition;

public class FromFileTests {
	
	public final static IChemObjectBuilder builder = DefaultChemObjectBuilder.getInstance();
	
	public void compareResults(IAtomContainer atomContainer, int count) throws NoSuchAtomException {
		AtomDiscretePartitionRefiner refiner = new AtomDiscretePartitionRefiner();
		refiner.refine(atomContainer);
		Partition refinedPartition = refiner.getAutomorphismPartition();
		
		EquivalentClassPartitioner it = new EquivalentClassPartitioner(atomContainer);
		int equivalentClass[]=it.getTopoEquivClassbyHuXu(atomContainer);
		Partition huXuPartition = ArrayToPartition.convert(equivalentClass, 1);
		
		if (refinedPartition.equals(huXuPartition)) {
			System.out.println(count + " Equal");
		} else {
			System.out.println(count + " Failed");
			System.out.println(AtomContainerPrinter.toString(atomContainer));
			System.out.println(refinedPartition);
			System.out.println(huXuPartition);
		}
	}
	
	public void compareFile(IIteratingChemObjectReader<IAtomContainer> reader)
				throws IOException, NoSuchAtomException {
		
		int count = 0;
		while (reader.hasNext()) {
			IAtomContainer atomContainer = reader.next();
			compareResults(atomContainer, count);
			count++;
		}
		reader.close();
	}
	
	
	public void compareSmilesFile(String filename) throws NoSuchAtomException, FileNotFoundException, IOException {
		compareFile(new IteratingSMILESReader(new FileReader(filename), builder));
	}
	
	public void compareSDFFile(String filename) throws NoSuchAtomException, FileNotFoundException, IOException {
		compareFile(new IteratingSDFReader(new FileReader(filename), builder));
	}
	
	@Test
	public void testC2H7NO3() throws NoSuchAtomException, IOException {
		compareSmilesFile("../../external/moleculegen/output/C2H7NO3.smi");
	}
	
	@Test
	public void testC4H4BrClFI() throws NoSuchAtomException, IOException {
		compareSmilesFile("../../external/moleculegen/output/C4H4BrClFI.smi");
	}
	
	@Test
	public void testC4H8N2O1S1() throws NoSuchAtomException, IOException {
		compareSDFFile("/Users/maclean/research/seneca/generator/spaces/spaces/C4H8N2O1S1.sdf");
	}
}
