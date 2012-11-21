package test;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;

import org.openscience.cdk.annotations.TestMethod;
import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IBond;
import org.openscience.cdk.interfaces.IChemObject;
import org.openscience.cdk.interfaces.IChemObjectBuilder;
import org.openscience.cdk.io.DefaultChemObjectReader;
import org.openscience.cdk.io.formats.Chem3D_Cartesian_1Format;
import org.openscience.cdk.io.formats.IResourceFormat;

public class Chem3DCartesian1Reader extends DefaultChemObjectReader {
	
	private BufferedReader input;
	
	public Chem3DCartesian1Reader(Reader reader) throws CDKException {
		setReader(reader);
	}

	@Override
	@SuppressWarnings("unchecked")
	public <T extends IChemObject> T read(T object) throws CDKException {
		if (object instanceof IAtomContainer) {
			return (T)read((IAtomContainer)object);
		} else {
			throw new CDKException("Only IAtomContainer supported.");
		}
	}
	
	private IAtomContainer read(IAtomContainer molecule) {
		String line;
		int lineCount = 0;
		int atomCount;
		IChemObjectBuilder builder = molecule.getBuilder();
		int[][] bondMap = null;
		try {
			while ((line = input.readLine()) != null) {
				if (lineCount == 0) {
					atomCount = Integer.parseInt(line.trim());
//					System.out.println(atomCount + " atoms");
					bondMap = new int[atomCount][];
				} else {
					String[] bits = line.trim().split("\\s+");
//					System.out.println(java.util.Arrays.toString(bits));
					String symbol = bits[0];
					int number = Integer.parseInt(bits[1]);
//					String xCoord = bits[2];
//					String yCoord = bits[3];
//					String zCoord = bits[4];
//					String unknown = bits[5];	// bond order?
					IAtom atom = builder.newInstance(IAtom.class);
					atom.setSymbol(symbol);
//					atom.setPoint3d(...)	 TODO
					molecule.addAtom(atom);
					bondMap[number - 1] = new int[bits.length - 6];
					for (int index = 6; index < bits.length; index++) {
						int partner = Integer.parseInt(bits[index]) - 1;
						bondMap[number - 1][index - 6] = partner;
					}
				}
				lineCount++;
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if (bondMap != null) {
			for (int atomIndex = 0; atomIndex < bondMap.length; atomIndex++) {
				IAtom atomI = molecule.getAtom(atomIndex);
				for (int partnerIndex : bondMap[atomIndex]) {
					if (partnerIndex > atomIndex) {	// avoid duplicate bonds
						IAtom atomJ = molecule.getAtom(partnerIndex);
						IBond bond = builder.newInstance(
								IBond.class, atomI, atomJ, IBond.Order.SINGLE); 
						molecule.addBond(bond);
					}
				}
			}
		}
		
		return molecule;
	}

	@Override
	public void setReader(Reader reader) throws CDKException {
		if (reader instanceof BufferedReader) {
            this.input = (BufferedReader)reader;
        } else {
            this.input = new BufferedReader(reader);
        }
	}

	@Override
	public void setReader(InputStream reader) throws CDKException {
		setReader(new InputStreamReader(reader));
	}

	@Override
	public IResourceFormat getFormat() {
		return Chem3D_Cartesian_1Format.getInstance();
	}

	@Override
	public boolean accepts(Class<? extends IChemObject> classObject) {
		return IAtomContainer.class.equals(classObject);
	}

	@Override
	@TestMethod("testClose")
	public void close() throws IOException {
		input.close();
	}

}
