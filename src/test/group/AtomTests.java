package test.group;

import org.junit.Assert;
import org.junit.Test;
import org.openscience.cdk.Atom;
import org.openscience.cdk.AtomContainer;
import org.openscience.cdk.DefaultChemObjectBuilder;
import org.openscience.cdk.aromaticity.CDKHueckelAromaticityDetector;
import org.openscience.cdk.exception.InvalidSmilesException;
import org.openscience.cdk.graph.invariant.EquivalentClassPartitioner;
import org.openscience.cdk.group.AtomContainerPrinter;
import org.openscience.cdk.group.AtomDiscretePartitionRefiner;
import org.openscience.cdk.group.Partition;
import org.openscience.cdk.group.Permutation;
import org.openscience.cdk.group.PermutationGroup;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IBond;
import org.openscience.cdk.interfaces.IChemObjectBuilder;
import org.openscience.cdk.templates.MoleculeFactory;
import org.openscience.cdk.tools.manipulator.AtomContainerManipulator;

import util.ArrayToPartition;

public class AtomTests {
	
	public final static IChemObjectBuilder builder = DefaultChemObjectBuilder.getInstance(); 

	@Test
	public void testQuinone() throws Exception {

		IAtomContainer mol = MoleculeFactory.makeQuinone();
		Assert.assertNotNull("Created molecule was null", mol);

//		AtomContainerManipulator.percieveAtomTypesAndConfigureAtoms(mol);
//		CDKHueckelAromaticityDetector.detectAromaticity(mol);
		
		AtomDiscretePartitionRefiner refiner = new AtomDiscretePartitionRefiner();
		refiner.refine(mol);
		Partition autP = refiner.getAutomorphismPartition();
		
		Assert.assertEquals(
				"Wrong number of equivalent classes", 3, autP.size());
		Partition expected = Partition.fromString("0,7|1,4|2,3,5,6");
		Assert.assertEquals("Wrong class assignment", expected, autP);
	}
	
	@Test
	public void testAromaticSystem() throws Exception {

		IAtomContainer mol = MoleculeFactory.makeAzulene();
		Assert.assertNotNull("Created molecule was null", mol);

		AtomContainerManipulator.percieveAtomTypesAndConfigureAtoms(mol);
		CDKHueckelAromaticityDetector.detectAromaticity(mol);
		
		AtomDiscretePartitionRefiner refiner = new AtomDiscretePartitionRefiner();
		refiner.refine(mol);
		Partition autP = refiner.getAutomorphismPartition();
		
		Assert.assertEquals(
				"Wrong number of equivalent classes", 6, autP.size());
		Partition expected = Partition.fromString("0,4|1,3|2|5,9|6,8|7");
		Assert.assertEquals("Wrong class assignment", expected, autP);
	}
	
	@Test
	public void testAlphaPinene() throws Exception {
		IAtomContainer mol = MoleculeFactory.makeAlphaPinene();
		AtomContainerManipulator.percieveAtomTypesAndConfigureAtoms(mol);
		CDKHueckelAromaticityDetector.detectAromaticity(mol);
		Assert.assertNotNull("Created molecule was null", mol);
		
		AtomDiscretePartitionRefiner refiner = new AtomDiscretePartitionRefiner();
		refiner.refine(mol);
		Partition autP = refiner.getAutomorphismPartition();
		
		Assert.assertEquals("Wrong number of equivalent classes", 9, autP.size());
		Partition expected = Partition.fromString("0|1|2|3|4|5|6|7|8,9");
		Assert.assertEquals("Wrong class assignment", expected, autP);
	}
	
	 /**
     * Test the equivalent classes method in pyrimidine
     * Tests if the position of the single and double bonds in an aromatic ring matter
     * to assign a class.
     *
     * @throws Exception
     */
    @Test
    public void testPyrimidine() throws Exception {
        IAtomContainer mol = MoleculeFactory.makePyrimidine();
        AtomContainerManipulator.percieveAtomTypesAndConfigureAtoms(mol);
        CDKHueckelAromaticityDetector.detectAromaticity(mol);
        Assert.assertNotNull("Created molecule was null", mol);
        
        AtomDiscretePartitionRefiner refiner = new AtomDiscretePartitionRefiner();
		refiner.refine(mol);
		Partition autP = refiner.getAutomorphismPartition();
		
		Assert.assertEquals("Wrong number of equivalent classes", 4, autP.size());
		Partition expected = Partition.fromString("0,4|1,3|2|5");
		Assert.assertEquals("Wrong class assignment", expected, autP);
    }

    /**
     * Test the equivalent classes method in biphenyl,
     * a molecule with two aromatic systems. It has 2 symmetry axis.
     *
     * @throws Exception
     */
    @Test
    public void testBiphenyl() throws Exception {
        IAtomContainer mol = MoleculeFactory.makeBiphenyl();
        AtomContainerManipulator.percieveAtomTypesAndConfigureAtoms(mol);
        CDKHueckelAromaticityDetector.detectAromaticity(mol);
        Assert.assertNotNull("Created molecule was null", mol);
        
        AtomDiscretePartitionRefiner refiner = new AtomDiscretePartitionRefiner();
		refiner.refine(mol);
		Partition autP = refiner.getAutomorphismPartition();
		
		Assert.assertEquals("Wrong number of equivalent classes", 4, autP.size());
		Partition expected = Partition.fromString("0,6|1,5,7,11|2,4,8,10|3,9");
		Assert.assertEquals("Wrong class assignment", expected, autP);
    }

    /**
     * Test the equivalent classes method in imidazole,
     * an aromatic molecule with a proton that can be exchanged between two aromatic nitrogens.
     * The method should have failed because only one tautomer is considered,
     * but there is no priority class for nodes of type ArNH to distinguish the nitrogens.
     *
     * @throws Exception
     */
    @Test
    public void testImidazole() throws Exception {
        IAtomContainer mol = MoleculeFactory.makeImidazole();
        AtomContainerManipulator.percieveAtomTypesAndConfigureAtoms(mol);
        CDKHueckelAromaticityDetector.detectAromaticity(mol);
        Assert.assertNotNull("Created molecule was null", mol);
        EquivalentClassPartitioner it = new EquivalentClassPartitioner(mol);
        int [] equivalentClass = it.getTopoEquivClassbyHuXu(mol);
        char[] arrEquivalent = new char[mol.getAtomCount()];
        for (int i = 1; i < equivalentClass.length; i++)
            arrEquivalent[i - 1] = Integer.toString(equivalentClass[i]).charAt(0);
        String strEquivalent = new String(arrEquivalent);
        Assert.assertNotNull("Equivalent class was null",
                equivalentClass);
        Assert.assertEquals("Wrong number of equivalent classes",
                3, equivalentClass[0]);
        Assert.assertEquals("Wrong class assignment","12321",strEquivalent);
    }


	@Test
	public void one_four_cyclohexadiene() throws InvalidSmilesException {
		String acpString = "C0C1C2C3C4C5 0:1(2),0:5(1),1:2(1),2:3(1),3:4(2),4:5(1)";
		IAtomContainer cyclohexadiene = AtomContainerPrinter.fromString(acpString, builder);
		AtomDiscretePartitionRefiner refiner = new AtomDiscretePartitionRefiner();
		PermutationGroup aut = refiner.getAutomorphismGroup(cyclohexadiene);
		for (Permutation automorphism : aut.all()) {
			System.out.println(automorphism.toCycleString());
		}
	}
	
	@Test
	public void four_H_pyran() throws InvalidSmilesException {
		String acpString = "C0C1C2C3C4O5 0:1(2),0:5(1),1:2(1),2:3(1),3:4(2),4:5(1)";
		IAtomContainer cyclohexadiene = AtomContainerPrinter.fromString(acpString, builder);
		AtomDiscretePartitionRefiner refiner = new AtomDiscretePartitionRefiner();
		PermutationGroup aut = refiner.getAutomorphismGroup(cyclohexadiene);
		for (Permutation automorphism : aut.all()) {
			System.out.println(automorphism.toCycleString());
		}
	}
	
	@Test
	public void testFullereneC24D6D() {
		AtomContainer C24D6D = new org.openscience.cdk.AtomContainer();
		C24D6D.addAtom(new Atom("C")); // 1
		C24D6D.addAtom(new Atom("C")); // 2
		C24D6D.addAtom(new Atom("C")); // 3
		C24D6D.addAtom(new Atom("C")); // 4
		C24D6D.addAtom(new Atom("C")); // 5
		C24D6D.addAtom(new Atom("C")); // 6
		C24D6D.addAtom(new Atom("C")); // 7
		C24D6D.addAtom(new Atom("C")); // 8
		C24D6D.addAtom(new Atom("C")); // 9 
		C24D6D.addAtom(new Atom("C")); // 10
		C24D6D.addAtom(new Atom("C")); // 11
		C24D6D.addAtom(new Atom("C")); // 12
		C24D6D.addAtom(new Atom("C")); // 13
		C24D6D.addAtom(new Atom("C")); // 14
		C24D6D.addAtom(new Atom("C")); // 15
		C24D6D.addAtom(new Atom("C")); // 16
		C24D6D.addAtom(new Atom("C")); // 17
		C24D6D.addAtom(new Atom("C")); // 18
		C24D6D.addAtom(new Atom("C")); // 19 
		C24D6D.addAtom(new Atom("C")); // 20
		C24D6D.addAtom(new Atom("C")); // 21
		C24D6D.addAtom(new Atom("C")); // 22
		C24D6D.addAtom(new Atom("C")); // 23
		C24D6D.addAtom(new Atom("C")); // 24


		C24D6D.addBond(0, 1, IBond.Order.SINGLE); // 1
		C24D6D.addBond(0, 5, IBond.Order.SINGLE); // 2
		C24D6D.addBond(0, 11, IBond.Order.SINGLE); // 3
		C24D6D.addBond(1, 2, IBond.Order.SINGLE); // 4
		C24D6D.addBond(1, 10, IBond.Order.SINGLE); // 5
		C24D6D.addBond(2, 3, IBond.Order.SINGLE); // 6
		C24D6D.addBond(2, 9, IBond.Order.SINGLE); // 7
		C24D6D.addBond(3, 4, IBond.Order.SINGLE); // 8
		C24D6D.addBond(3, 8, IBond.Order.SINGLE); // 9
		C24D6D.addBond(4, 5, IBond.Order.SINGLE); // 10
		C24D6D.addBond(4, 7, IBond.Order.SINGLE); // 11
		C24D6D.addBond(5, 6, IBond.Order.SINGLE); // 12
		C24D6D.addBond(6, 16, IBond.Order.SINGLE); // 13
		C24D6D.addBond(6, 17, IBond.Order.SINGLE); // 14
		C24D6D.addBond(7, 15, IBond.Order.SINGLE); // 15
		C24D6D.addBond(7, 16, IBond.Order.SINGLE); // 16
		C24D6D.addBond(8, 14, IBond.Order.SINGLE); // 17
		C24D6D.addBond(8, 15, IBond.Order.SINGLE); // 18
		C24D6D.addBond(9, 13, IBond.Order.SINGLE); // 19
		C24D6D.addBond(9, 14, IBond.Order.SINGLE); // 20
		C24D6D.addBond(10, 12, IBond.Order.SINGLE); // 21
		C24D6D.addBond(10, 13, IBond.Order.SINGLE); // 22
		C24D6D.addBond(11, 12, IBond.Order.SINGLE); // 23
		C24D6D.addBond(11, 17, IBond.Order.SINGLE); // 24
		C24D6D.addBond(12, 19, IBond.Order.SINGLE); // 25
		C24D6D.addBond(13, 20, IBond.Order.SINGLE); // 26
		C24D6D.addBond(14, 21, IBond.Order.SINGLE); // 27
		C24D6D.addBond(15, 22, IBond.Order.SINGLE); // 28
		C24D6D.addBond(16, 23, IBond.Order.SINGLE); // 29
		C24D6D.addBond(17, 18, IBond.Order.SINGLE); // 30
		C24D6D.addBond(18, 19, IBond.Order.SINGLE); // 31
		C24D6D.addBond(18, 23, IBond.Order.SINGLE); // 32
		C24D6D.addBond(19, 20, IBond.Order.SINGLE); // 33
		C24D6D.addBond(20, 21, IBond.Order.SINGLE); // 34
		C24D6D.addBond(21, 22, IBond.Order.SINGLE); // 35
		C24D6D.addBond(22, 23, IBond.Order.SINGLE); // 36
		
		AtomDiscretePartitionRefiner refiner = new AtomDiscretePartitionRefiner();
		refiner.refine(C24D6D);
		Partition autPart = refiner.getAutomorphismPartition();
		System.out.println(autPart);
	}
	
	@Test public void testEquivalent() throws Exception 
	{
		AtomContainer C40C3V = new org.openscience.cdk.AtomContainer();
		C40C3V.addAtom(new Atom("C")); // 1
		C40C3V.addAtom(new Atom("C")); // 2
		C40C3V.addAtom(new Atom("C")); // 3
		C40C3V.addAtom(new Atom("C")); // 4
		C40C3V.addAtom(new Atom("C")); // 5
		C40C3V.addAtom(new Atom("C")); // 6
		C40C3V.addAtom(new Atom("C")); // 7
		C40C3V.addAtom(new Atom("C")); // 8
		C40C3V.addAtom(new Atom("C")); // 9 
		C40C3V.addAtom(new Atom("C")); // 10
		C40C3V.addAtom(new Atom("C")); // 11
		C40C3V.addAtom(new Atom("C")); // 12
		C40C3V.addAtom(new Atom("C")); // 13
		C40C3V.addAtom(new Atom("C")); // 14
		C40C3V.addAtom(new Atom("C")); // 15
		C40C3V.addAtom(new Atom("C")); // 16
		C40C3V.addAtom(new Atom("C")); // 17
		C40C3V.addAtom(new Atom("C")); // 18
		C40C3V.addAtom(new Atom("C")); // 19 
		C40C3V.addAtom(new Atom("C")); // 20
		C40C3V.addAtom(new Atom("C")); // 21
		C40C3V.addAtom(new Atom("C")); // 22
		C40C3V.addAtom(new Atom("C")); // 23
		C40C3V.addAtom(new Atom("C")); // 24
		C40C3V.addAtom(new Atom("C")); // 25
		C40C3V.addAtom(new Atom("C")); // 26
		C40C3V.addAtom(new Atom("C")); // 27
		C40C3V.addAtom(new Atom("C")); // 28
		C40C3V.addAtom(new Atom("C")); // 29
		C40C3V.addAtom(new Atom("C")); // 30
		C40C3V.addAtom(new Atom("C")); // 31
		C40C3V.addAtom(new Atom("C")); // 32
		C40C3V.addAtom(new Atom("C")); // 33
		C40C3V.addAtom(new Atom("C")); // 34
		C40C3V.addAtom(new Atom("C")); // 35
		C40C3V.addAtom(new Atom("C")); // 36
		C40C3V.addAtom(new Atom("C")); // 37
		C40C3V.addAtom(new Atom("C")); // 38
		C40C3V.addAtom(new Atom("C")); // 39
		C40C3V.addAtom(new Atom("C")); // 40

		C40C3V.addBond(0, 1, IBond.Order.SINGLE); // 1
		C40C3V.addBond(0, 5, IBond.Order.SINGLE); // 2
		C40C3V.addBond(0, 8, IBond.Order.SINGLE); // 3
		C40C3V.addBond(1, 2, IBond.Order.SINGLE); // 4
		C40C3V.addBond(1, 25, IBond.Order.SINGLE); // 5
		C40C3V.addBond(2, 3, IBond.Order.SINGLE); // 6
		C40C3V.addBond(2, 6, IBond.Order.SINGLE); // 7
		C40C3V.addBond(3, 4, IBond.Order.SINGLE); // 8
		C40C3V.addBond(3, 24, IBond.Order.SINGLE); // 9
		C40C3V.addBond(4, 7, IBond.Order.SINGLE); // 10
		C40C3V.addBond(4, 8, IBond.Order.SINGLE); // 11
		C40C3V.addBond(5, 21, IBond.Order.SINGLE); // 12
		C40C3V.addBond(5, 28, IBond.Order.SINGLE); // 13
		C40C3V.addBond(6, 22, IBond.Order.SINGLE); // 14
		C40C3V.addBond(6, 27, IBond.Order.SINGLE); // 15
		C40C3V.addBond(7, 20, IBond.Order.SINGLE); // 16
		C40C3V.addBond(7, 23, IBond.Order.SINGLE); // 17
		C40C3V.addBond(8, 26, IBond.Order.SINGLE); // 18
		C40C3V.addBond(9, 12, IBond.Order.SINGLE); // 19
		C40C3V.addBond(9, 37, IBond.Order.SINGLE); // 20
		C40C3V.addBond(9, 39, IBond.Order.SINGLE); // 21
		C40C3V.addBond(10, 14, IBond.Order.SINGLE); // 22
		C40C3V.addBond(10, 38, IBond.Order.SINGLE); // 23
		C40C3V.addBond(10, 39, IBond.Order.SINGLE); // 24
		C40C3V.addBond(11, 13, IBond.Order.SINGLE); // 25
		C40C3V.addBond(11, 36, IBond.Order.SINGLE); // 26
		C40C3V.addBond(11, 39, IBond.Order.SINGLE); // 27
		C40C3V.addBond(12, 35, IBond.Order.SINGLE); // 28
		C40C3V.addBond(12, 38, IBond.Order.SINGLE); // 29
		C40C3V.addBond(13, 34, IBond.Order.SINGLE); // 30
		C40C3V.addBond(13, 37, IBond.Order.SINGLE); // 31
		C40C3V.addBond(14, 33, IBond.Order.SINGLE); // 32
		C40C3V.addBond(14, 36, IBond.Order.SINGLE); // 33
		C40C3V.addBond(15, 29, IBond.Order.SINGLE); // 34
		C40C3V.addBond(15, 17, IBond.Order.SINGLE); // 35
		C40C3V.addBond(15, 37, IBond.Order.SINGLE); // 36
		C40C3V.addBond(16, 19, IBond.Order.SINGLE); // 37
		C40C3V.addBond(16, 30, IBond.Order.SINGLE); // 38
		C40C3V.addBond(16, 36, IBond.Order.SINGLE); // 39
		C40C3V.addBond(17, 20, IBond.Order.SINGLE); // 40
		C40C3V.addBond(17, 35, IBond.Order.SINGLE); // 41
		C40C3V.addBond(18, 22, IBond.Order.SINGLE); // 42
		C40C3V.addBond(18, 32, IBond.Order.SINGLE); // 43
		C40C3V.addBond(18, 33, IBond.Order.SINGLE); // 44
		C40C3V.addBond(19, 28, IBond.Order.SINGLE); // 45
		C40C3V.addBond(19, 34, IBond.Order.SINGLE); // 46
		C40C3V.addBond(20, 26, IBond.Order.SINGLE); // 47
		C40C3V.addBond(21, 26, IBond.Order.SINGLE); // 48
		C40C3V.addBond(21, 29, IBond.Order.SINGLE); // 49
		C40C3V.addBond(22, 24, IBond.Order.SINGLE); // 50
		C40C3V.addBond(23, 24, IBond.Order.SINGLE); // 51
		C40C3V.addBond(23, 31, IBond.Order.SINGLE); // 52
		C40C3V.addBond(25, 27, IBond.Order.SINGLE); // 53
		C40C3V.addBond(25, 28, IBond.Order.SINGLE); // 54
		C40C3V.addBond(27, 30, IBond.Order.SINGLE); // 55
		C40C3V.addBond(29, 34, IBond.Order.SINGLE); // 56
		C40C3V.addBond(30, 33, IBond.Order.SINGLE); // 57
		C40C3V.addBond(31, 32, IBond.Order.SINGLE); // 58
		C40C3V.addBond(31, 35, IBond.Order.SINGLE); // 59
		C40C3V.addBond(32, 38, IBond.Order.SINGLE); // 60
		
		AtomDiscretePartitionRefiner refiner = new AtomDiscretePartitionRefiner();
		refiner.refine(C40C3V);
		Partition autPart = refiner.getAutomorphismPartition();
		System.out.println(autPart);
		
		EquivalentClassPartitioner it = new EquivalentClassPartitioner(C40C3V);
		int equivalentClass[]=it.getTopoEquivClassbyHuXu(C40C3V);
		Partition huXuPartition = ArrayToPartition.convert(equivalentClass, 1);
		System.out.println(huXuPartition);
	}

}