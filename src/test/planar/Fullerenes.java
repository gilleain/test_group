package test.planar;

import io.Chem3DCartesian1Reader;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;

import org.junit.Test;
import org.openscience.cdk.DefaultChemObjectBuilder;
import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.group.AtomContainerPrinter;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IChemObjectBuilder;
import org.openscience.cdk.silent.AtomContainer;

import planar.BlockEmbedding;
import planar.Face;
import planar.AtomContainerEmbedder;
import planar.AtomContainerEmbedding;

public class Fullerenes {
    
    public static final String DIR = 
            "/Users/maclean/Documents/molecules/FullereneLib/";
    
    public final static IChemObjectBuilder builder = DefaultChemObjectBuilder.getInstance();
    
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
    
    public void layout(IAtomContainer atomContainer) {
        AtomContainerEmbedding embedding = AtomContainerEmbedder.embed(atomContainer);
        BlockEmbedding blockEmbedding = embedding.getBlockEmbedding(0);
        int faceIndex = 0;
        for (Face face : blockEmbedding.getFaces()) {
            System.out.println(faceIndex + "\t" + face);
            faceIndex++;
        }
        System.out.println("ExtFace " + blockEmbedding.getExternalFace());
        System.out.println("InnDual " + blockEmbedding.calculateInnerDual());
    }
    
    @Test
    public void testSmallest() throws CDKException, IOException {
        IAtomContainer ac = readFile("C20-30/c20ih.cc1");
        layout(ac);
    }
    
    @Test
    public void testCage() {
        String cage = "C0C1C2C3C4C5C6C7C8C9 0:1(1),0:5(1),0:6(1),1:2(1),1:6(1),"
                     + "2:3(1),2:7(1),3:4(1),3:7(1),4:5(1),4:8(1),5:8(1)," 
                     + "6:9(1),7:9(1),8:9(1)";
        IAtomContainer atomContainer = AtomContainerPrinter.fromString(cage, builder);
        layout(atomContainer);
    }
}
