package test.group;

import junit.framework.Assert;

import org.junit.Test;
import org.openscience.cdk.DefaultChemObjectBuilder;
import org.openscience.cdk.exception.NoSuchAtomException;
import org.openscience.cdk.graph.invariant.EquivalentClassPartitioner;
import org.openscience.cdk.group.AtomContainerPrinter;
import org.openscience.cdk.group.AtomDiscretePartitionRefiner;
import org.openscience.cdk.group.Partition;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IChemObjectBuilder;
import org.openscience.cdk.smiles.SmilesGenerator;

import util.ArrayToPartition;

public class HuXuPaperTests {
	
	public final static IChemObjectBuilder builder = DefaultChemObjectBuilder.getInstance();
	
	public void compareResults(String acpString, Partition expected) throws NoSuchAtomException {
		IAtomContainer atomContainer = AtomContainerPrinter.fromString(acpString, builder);
		verifyDegreeThree(atomContainer);
		AtomDiscretePartitionRefiner refiner = new AtomDiscretePartitionRefiner();
		refiner.refine(atomContainer);
		Partition refinedPartition = refiner.getAutomorphismPartition();
		
		EquivalentClassPartitioner it = new EquivalentClassPartitioner(atomContainer);
		int equivalentClass[]=it.getTopoEquivClassbyHuXu(atomContainer);
		Partition huXuPartition = ArrayToPartition.convert(equivalentClass, 1);
		
		if (refinedPartition.equals(huXuPartition)) {
			System.out.println(" Equal");
		} else {
			System.out.println(" Failed");
			System.out.println(AtomContainerPrinter.toString(atomContainer));
			System.out.println(refinedPartition);
			System.out.println(huXuPartition);
		}
		SmilesGenerator smileGen = new SmilesGenerator();
		System.out.println(smileGen.createSMILES(atomContainer));
		Assert.assertEquals(expected, refinedPartition);
	}
	
	private void verifyDegreeThree(IAtomContainer ac) {
		for (IAtom atom : ac.atoms()) {
			assert ac.getConnectedAtomsCount(atom) == 3;
		}
	}

	@Test
	public void pentacycloOctane() throws NoSuchAtomException {
		compareResults("C0C1C2C3C4C5C6C7 0:1(1),0:2(1),0:3(1),1:3(1),1:4(1),"
					 + "2:3(1),2:5(1),4:6(1),4:7(1),5:6(1),5:7(1),6:7(1)",
					 Partition.fromString("0,3,6,7|1,2,4,5"));
	}
	
	@Test
	public void pentacycloOctene() throws NoSuchAtomException {
		compareResults("C0C1C2C3C4C5C6C7 0:1(2),0:2(1),0:3(1),1:3(1),1:4(1),"
					 + "2:3(2),2:5(1),4:6(2),4:7(1),5:6(1),5:7(2),6:7(1)",
					 Partition.fromString("0,3,6,7|1,2,4,5"));
	}
	
	@Test
	public void cubane() throws NoSuchAtomException {
		compareResults("C0C1C2C3C4C5C6C7 0:1(1),0:2(1),0:4(1),1:3(1),1:5(1),"
					 + "2:3(1),2:6(1),3:7(1),4:5(1),4:6(1),5:7(1),6:7(1)",
					 Partition.unit(8));
	}
	
	@Test
	public void cubene() throws NoSuchAtomException {
		compareResults("C0C1C2C3C4C5C6C7 0:1(2),0:2(1),0:4(1),1:3(1),1:5(1),"
					 + "2:3(2),2:6(1),3:7(1),4:5(1),4:6(2),5:7(2),6:7(1)",
					 Partition.unit(8));
	}
	
	@Test
	public void cunene() throws NoSuchAtomException {
		compareResults("C0C1C2C3C4C5C6C7 0:1(1),0:2(1),0:3(2),1:2(1),1:4(2),"
					 + "2:5(2),3:4(1),3:6(1),4:7(1),5:6(1),5:7(1),6:7(2)",
					 Partition.fromString("0,1|2|3,4|5|6,7"));
	}
	
	@Test
	public void antiCuneane() throws NoSuchAtomException {
		compareResults("C0C1C2C3C4C5C6C7 0:1(1),0:2(1),0:3(1),1:2(1),1:4(1),"
					 + "2:5(1),3:6(1),3:7(1),4:6(1),4:7(1),5:6(1),5:7(1)",
					 Partition.fromString("0,1,2|3,4,5|6,7"));
	}
	
	@Test
	public void antiCunene() throws NoSuchAtomException {
		compareResults("C0C1C2C3C4C5C6C7 0:1(1),0:2(1),0:3(2),1:2(2),1:4(1),"
					 + "2:5(1),3:6(1),3:7(1),4:6(2),4:7(1),5:6(1),5:7(2)",
					 Partition.fromString("0|1,2|3|4,5|6,7"));
	}
	
	@Test
	public void cage() throws NoSuchAtomException {
		compareResults("C0C1C2C3C4C5C6C7C8C9 0:1(1),0:5(1),0:6(1),1:2(1),1:6(1),"
					 + "2:3(1),2:7(1),3:4(1),3:7(1),4:5(1),4:8(1),5:8(1)," 
					 + "6:9(1),7:9(1),8:9(1)",
					 Partition.fromString("0,1,2,3,4,5|6,7,8|9"));
	}
}
