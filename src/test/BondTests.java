package test;

import org.junit.Test;
import org.openscience.cdk.DefaultChemObjectBuilder;
import org.openscience.cdk.exception.InvalidSmilesException;
import org.openscience.cdk.group.AtomContainerPrinter;
import org.openscience.cdk.group.BondDiscretePartitionRefiner;
import org.openscience.cdk.group.Permutation;
import org.openscience.cdk.group.PermutationGroup;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IChemObjectBuilder;

public class BondTests {
	
	public final static IChemObjectBuilder builder = DefaultChemObjectBuilder.getInstance(); 

	@Test
	public void napthaleneA() throws InvalidSmilesException {
		String acpString = "C0C1C2C3C4C5C6C7C8C9 0:1(2),0:9(1),1:2(1),2:3(2)," +
						   "3:4(1),3:8(1),4:5(2),5:6(1),6:7(2),7:8(1),8:9(2)";
		IAtomContainer cyclohexadiene = AtomContainerPrinter.fromString(acpString, builder);
		BondDiscretePartitionRefiner refiner = new BondDiscretePartitionRefiner();
		PermutationGroup aut = refiner.getAutomorphismGroup(cyclohexadiene);
		for (Permutation automorphism : aut.all()) {
			System.out.println(automorphism.toCycleString());
		}
	}
	
	@Test
	public void napthaleneB() throws InvalidSmilesException {
		String acpString = "C0C1C2C3C4C5C6C7C8C9 0:1(1),0:9(2),1:2(2),2:3(1)," +
				   		   "3:4(1),3:8(2),4:5(2),5:6(1),6:7(2),7:8(1),8:9(1)";
		IAtomContainer cyclohexadiene = AtomContainerPrinter.fromString(acpString, builder);
		BondDiscretePartitionRefiner refiner = new BondDiscretePartitionRefiner();
		PermutationGroup aut = refiner.getAutomorphismGroup(cyclohexadiene);
		for (Permutation automorphism : aut.all()) {
			System.out.println(automorphism.toCycleString());
		}
	}

}
