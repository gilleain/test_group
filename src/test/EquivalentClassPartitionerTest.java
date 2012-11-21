package test;

import java.util.Arrays;

import org.junit.Test;
import org.openscience.cdk.DefaultChemObjectBuilder;
import org.openscience.cdk.exception.NoSuchAtomException;
import org.openscience.cdk.graph.invariant.EquivalentClassPartitioner;
import org.openscience.cdk.group.AtomContainerPrinter;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IChemObjectBuilder;

public class EquivalentClassPartitionerTest {
	
	public final static IChemObjectBuilder builder = DefaultChemObjectBuilder.getInstance();
	
	@Test
	public void one_four_cyclohexadiene() throws NoSuchAtomException {
		String acpString = "C0C1C2C3C4C5 0:1(2),0:5(1),1:2(1),2:3(1),3:4(2),4:5(1)";
		IAtomContainer cyclohexadiene = AtomContainerPrinter.fromString(acpString, builder);
		EquivalentClassPartitioner partitioner = new EquivalentClassPartitioner(cyclohexadiene);
		int[] eqCl = partitioner.getTopoEquivClassbyHuXu(cyclohexadiene);
		System.out.println(Arrays.toString(eqCl));
		System.out.println(ArrayToPartition.convert(eqCl, 1));
	}
	
	@Test
	public void four_H_pyran() throws NoSuchAtomException {
		String acpString = "C0C1C2C3C4O5 0:1(2),0:5(1),1:2(1),2:3(1),3:4(2),4:5(1)";
		IAtomContainer fourHpyran = AtomContainerPrinter.fromString(acpString, builder);
		EquivalentClassPartitioner partitioner = new EquivalentClassPartitioner(fourHpyran);
		int[] eqCl = partitioner.getTopoEquivClassbyHuXu(fourHpyran);
		System.out.println(Arrays.toString(eqCl));
		System.out.println(ArrayToPartition.convert(eqCl, 1));
	}
	
	@Test
	public void cuneane() throws NoSuchAtomException {
		String acpString = "C0C1C2C3C4C5C6C7 0:1(1),0:5(1),1:2(1),1:7(1),2:3(1),2:7(1),3:4(1),4:5(1),4:6(1),5:6(1),6:7(1)";
		IAtomContainer cuneane = AtomContainerPrinter.fromString(acpString, builder);
		EquivalentClassPartitioner partitioner = new EquivalentClassPartitioner(cuneane);
		int[] eqCl = partitioner.getTopoEquivClassbyHuXu(cuneane);
		System.out.println(Arrays.toString(eqCl));
		System.out.println(ArrayToPartition.convert(eqCl, 1));
	}

}
