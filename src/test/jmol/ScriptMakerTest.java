package test.jmol;

import io.Chem3DCartesian1Reader;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintStream;

import jmol.ScriptMaker;

import org.junit.Test;
import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.silent.AtomContainer;

import planar.BlockEmbedding;
import planar.PlanarBlockEmbedder;
import util.SignatureAtomColorer;
import util.SignatureRingColorer;

public class ScriptMakerTest {
    
    public enum AtomOrRing { ATOM, RING };
    
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
    
    public void test(String subDirName, 
                     String name, 
                     AtomOrRing atomOrRing) throws CDKException, IOException {
        File subDir = new File(DIR, subDirName);
        IAtomContainer ac = readFile(new File(subDir, name + ".cc1"));
        File outDir = new File("output/threeDee", subDirName);
        if (!outDir.exists()) {
            outDir.mkdirs();
        }
        File outFile = new File(outDir, name + "_" + atomOrRing + ".spt");
        PrintStream fileOut = new PrintStream(new FileOutputStream(outFile));
        if (atomOrRing == AtomOrRing.ATOM) {
            ScriptMaker.printAtomColorScript(ac, new SignatureAtomColorer(ac), fileOut);
        } else {
            BlockEmbedding em = PlanarBlockEmbedder.embed(ac);
            ScriptMaker.printRingColorScript(ac, new SignatureRingColorer(ac, em), fileOut);
        }
        fileOut.flush();
        fileOut.close();
    }
    
    @Test
    public void test_No3_D3d_atom() throws CDKException, IOException {
        test("C32", "No.3-D3d", AtomOrRing.ATOM);
    }
    
    @Test
    public void test_c24d6d_atom() throws CDKException, IOException {
        test("C20-30", "c24d6d", AtomOrRing.ATOM);
    }
    
    @Test
    public void testC70_atom() throws CDKException, IOException {
        test("C60-76", "C70-D5h", AtomOrRing.ATOM);
    }
    
    @Test
    public void test_No3_D3d_ring() throws CDKException, IOException {
        test("C32", "No.3-D3d", AtomOrRing.RING);
    }
    
    @Test
    public void test_c24d6d_ring() throws CDKException, IOException {
        test("C20-30", "c24d6d", AtomOrRing.RING);
    }
    
    @Test
    public void testC70_ring() throws CDKException, IOException {
        test("C60-76", "C70-D5h", AtomOrRing.RING);
    }


}
