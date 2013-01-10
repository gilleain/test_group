package test.planar;

import io.Chem3DCartesian1Reader;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.junit.Test;
import org.openscience.cdk.DefaultChemObjectBuilder;
import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.group.AtomContainerPrinter;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IChemObjectBuilder;
import org.openscience.cdk.silent.AtomContainer;

import planar.AtomContainerEmbedder;
import planar.AtomContainerEmbedding;
import planar.BlockEmbedding;
import planar.Face;

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
    
    public int embed(IAtomContainer atomContainer) {
        AtomContainerEmbedding embedding = AtomContainerEmbedder.embed(atomContainer);
        BlockEmbedding blockEmbedding = embedding.getBlockEmbedding(0);
//        int faceIndex = 0;
//        for (Face face : blockEmbedding.getFaces()) {
//            System.out.println(faceIndex + "\t" + face);
//            faceIndex++;
//        }
//        System.out.println("ExtFace " + blockEmbedding.getExternalFace());
//        System.out.println("InnDual " + blockEmbedding.calculateInnerDual());
        return blockEmbedding.getExternalFace().vsize();
    }
    
    @Test
    public void testSmallest() throws CDKException, IOException {
        IAtomContainer ac = readFile("C20-30/c20ih.cc1");
        embed(ac);
    }
    
    @Test
    public void test_c28d2() throws CDKException, IOException {
        IAtomContainer ac = readFile("C20-30/c28d2.cc1");
        embed(ac);
    }
    
    @Test
    public void testOFBug() throws CDKException, IOException {
        IAtomContainer ac = readFile("C20-30/c30-1.cc1");
        embed(ac);
    }
    
    @Test
    public void testBucky() throws CDKException, IOException {
        IAtomContainer ac = readFile("C60-76/C60-Ih.cc1");
        embed(ac);
    }
    
    @Test
    public void findSmallestOuterFaceBugExample() throws CDKException, IOException {
        int max = 30;
        int counter = 0;
        Map<String, Integer> outerFaceSizeMap = new HashMap<String, Integer>();
        String[] dirNames = new String[] { "C20-30", "C32", "C34", "C36", "C38" };
        for (String dirName : dirNames) {
            File subDir = new File(DIR, dirName);
            if (subDir.isDirectory()) {
                for (File file : subDir.listFiles()) {
                    try {
                        int ofCount = embed(readFile(file));
                        outerFaceSizeMap.put(file.getName(), ofCount);
                    } catch (Exception e) {
                        // todo
                    }
                    if (counter == max) {
                        break;
                    } else {
                        counter++;
                    }
                }
            }
        }
        for (String filename : outerFaceSizeMap.keySet()) {
            System.out.println(outerFaceSizeMap.get(filename) + "\t" + filename);
        }
    }
    
    @Test
    public void testCage() {
        String cage = "C0C1C2C3C4C5C6C7C8C9 0:1(1),0:5(1),0:6(1),1:2(1),1:6(1),"
                     + "2:3(1),2:7(1),3:4(1),3:7(1),4:5(1),4:8(1),5:8(1)," 
                     + "6:9(1),7:9(1),8:9(1)";
        IAtomContainer atomContainer = AtomContainerPrinter.fromString(cage, builder);
        embed(atomContainer);
    }
}
