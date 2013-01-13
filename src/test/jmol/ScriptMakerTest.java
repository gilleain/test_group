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
import org.openscience.cdk.renderer.color.IAtomColorer;
import org.openscience.cdk.silent.AtomContainer;

import util.SignatureAtomColorer;

public class ScriptMakerTest {
    
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
    
    public void test(String subDirName, String name) throws CDKException, IOException {
        File subDir = new File(DIR, subDirName);
        IAtomContainer ac = readFile(new File(subDir, name + ".cc1"));
        IAtomColorer atomColorer = new SignatureAtomColorer(ac);
        File outDir = new File("output/threeDee", subDirName);
        if (!outDir.exists()) {
            outDir.mkdirs();
        }
        File outFile = new File(outDir, name + ".spt");
        PrintStream fileOut = new PrintStream(new FileOutputStream(outFile));
        ScriptMaker.printAtomColorScript(ac, atomColorer, fileOut);
        fileOut.flush();
        fileOut.close();
    }
    
    @Test
    public void test_No3_D3d() throws CDKException, IOException {
        test("C32", "No.3-D3d");
    }
    
    @Test
    public void test_c24d6d() throws CDKException, IOException {
        test("C20-30", "c24d6d");
    }
    
    @Test
    public void testC70() throws CDKException, IOException {
        test("C60-76", "C70-D5h");
    }

}
