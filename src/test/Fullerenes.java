package test;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.junit.Test;
import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.exception.NoSuchAtomException;
import org.openscience.cdk.graph.invariant.EquivalentClassPartitioner;
import org.openscience.cdk.group.AtomContainerPrinter;
import org.openscience.cdk.group.AtomDiscretePartitionRefiner;
import org.openscience.cdk.group.Partition;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.silent.AtomContainer;

public class Fullerenes {
	
	public static final String DIR = 
			"/Users/maclean/Documents/molecules/FullereneLib/";
	
	public IAtomContainer readFile(String filepath) throws CDKException, IOException {
		return readFile(new File(DIR, filepath));
	}
	
	public IAtomContainer readFile(File file) throws CDKException, IOException {	
		Chem3DCartesian1Reader reader = 
				new Chem3DCartesian1Reader(new FileReader(file));
		IAtomContainer atomContainer = reader.read(new AtomContainer());
		reader.close();
		return atomContainer;
	}

	@Test
	public void testSmallest() throws CDKException, IOException {
		IAtomContainer ac = readFile("C20-30/c20ih.cc1");
		verifyDegreeThree(ac);
	}

	private void verifyDegreeThree(IAtomContainer ac) {
		for (IAtom atom : ac.atoms()) {
			assert ac.getConnectedAtomsCount(atom) == 3;
		}
	}
	
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
	
	public void timeFile(File dir, String filename) throws CDKException, IOException {
		IAtomContainer atomContainer = readFile(new File(dir, filename));
		long start;
		
		start = System.currentTimeMillis();
		EquivalentClassPartitioner it = new EquivalentClassPartitioner(atomContainer);
		int equivalentClass[]=it.getTopoEquivClassbyHuXu(atomContainer);
		Partition huXuPartition = ArrayToPartition.convert(equivalentClass, 1);
		long elapsedHuXu = System.currentTimeMillis() - start;
		
		start = System.currentTimeMillis();
		AtomDiscretePartitionRefiner refiner = new AtomDiscretePartitionRefiner();
		refiner.refine(atomContainer);
		Partition refinedPartition = refiner.getAutomorphismPartition();
		long elapsedRef = System.currentTimeMillis() - start;
		long order = refiner.getAutomorphismGroup().order();
		
		boolean partitionsEqual = refinedPartition.equals(huXuPartition);
		
		System.out.println(filename 
						   + "\t" + atomContainer.getAtomCount()
						   + "\t" + elapsedRef
						   + "\t" + elapsedHuXu
						   + "\t" + order
						   + "\t" + partitionsEqual
						   );
	}
	
	public void timeDir(String dirName) throws CDKException, IOException {
		File dirFile = new File(DIR, dirName);
		List<String> filenames = Arrays.asList(dirFile.list());
		Collections.shuffle(filenames);
		for (String filename : filenames) {
			timeFile(dirFile, filename);
		}
	}
	
	@Test
	public void compareSmallest() throws CDKException, IOException {
		IAtomContainer ac = readFile("C20-30/c20ih.cc1");
		compareResults(ac, 0);
	}
	
	@Test
	public void timeSmallest() throws CDKException, IOException {
		timeFile(new File(DIR, "C20-30"), "c20ih.cc1");
	}
	
	@Test
	public void compareLargest() throws CDKException, IOException {
		IAtomContainer ac = readFile("C180.cc1");
		compareResults(ac, 0);
	}
	
	@Test
	public void timeC20_C30() throws CDKException, IOException {
		timeDir("C20-30");
	}
	
	@Test
	public void timeC32() throws CDKException, IOException {
		timeDir("C32");
	}
	
	@Test
	public void timeC34() throws CDKException, IOException {
		timeDir("C34");
	}
	
	@Test
	public void timeC36() throws CDKException, IOException {
		timeDir("C36");
	}
	
	@Test
	public void timeC38() throws CDKException, IOException {
		timeDir("C38");
	}
	
	@Test
	public void timeC40() throws CDKException, IOException {
		timeDir("C40");
	}
	
	@Test
	public void timeC46() throws CDKException, IOException {
		timeDir("C46");
	}
	
	@Test
	public void timeC50() throws CDKException, IOException {
		timeDir("C50");
	}
	
	@Test
	public void timeC92() throws CDKException, IOException {
		timeDir("C92");
	}
}
