package test.project;

import io.Chem3DCartesian1Reader;

import java.awt.Dimension;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import javax.vecmath.Point2d;
import javax.vecmath.Point3d;
import javax.vecmath.Vector3d;

import layout.ConcentricFaceLayout;
import layout.Representation;

import org.junit.Test;
import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.geometry.GeometryTools;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.io.PDBWriter;
import org.openscience.cdk.silent.AtomContainer;

import planar.AtomContainerEmbedder;
import planar.AtomContainerEmbedding;
import planar.BlockEmbedding;
import planar.Vertex;
import projection.StereoProjector;

public class Fullerenes {

    public static final String DIR = 
            "/Users/maclean/Documents/molecules/FullereneLib/";

    public final int WIDTH = 800;

    public final int HEIGHT = 800;

    public final int RADIUS = 80;

    public final int EDGE_LEN = 80;

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

    public Representation layout(IAtomContainer atomContainer) {
        AtomContainerEmbedding embedding = AtomContainerEmbedder.embed(atomContainer);
        BlockEmbedding blockEmbedding = embedding.getBlockEmbedding(0);
        return new ConcentricFaceLayout(RADIUS, EDGE_LEN).layout(
                blockEmbedding, new Rectangle2D.Double(0, 0, WIDTH, HEIGHT));
    }
    
    public void apply(Representation rep, IAtomContainer ac) {
        for (Vertex v : rep.getVertices()) {
            Point2D point = rep.getPoint(v);
            Point2d p2d = new Point2d(point.getX(), point.getY());
            ac.getAtom(v.getIndex()).setPoint2d(p2d);
        }
    }
    
    public void centerOnOrigin(IAtomContainer ac) {
        Point2d center = GeometryTools.get2DCenter(ac);
        for (IAtom atom : ac.atoms()) {
            Point2d p = atom.getPoint2d();
            atom.setPoint2d(new Point2d(p.x - center.x, p.y - center.y));
        }
    }
    
    public void radiallyExpand(IAtomContainer ac) {
        for (IAtom atom : ac.atoms()) {
            Point3d p = atom.getPoint3d();
            Vector3d v = new Vector3d(p);
            v.normalize();
            p.scaleAdd(6, v);
            atom.setPoint3d(p);
        }
    }
    
    public void addZ(IAtomContainer ac) {
        for (IAtom atom : ac.atoms()) {
            Point2d p = atom.getPoint2d();
            atom.setPoint3d(new Point3d(p.x, p.y, 1));
        }
    }
    
    public void testFullerene(String path, String name, File outDir) throws CDKException, IOException {
        IAtomContainer atomContainer = readFile(new File(new File(DIR, path), name + ".cc1"));
        apply(layout(atomContainer), atomContainer);
        centerOnOrigin(atomContainer);
        GeometryTools.scaleMolecule(atomContainer, new Dimension(6, 6), 1);
        StereoProjector.projectUpwards(atomContainer);
        System.out.println(GeometryTools.get3DCenter(atomContainer));
        radiallyExpand(atomContainer);
        
        if (!outDir.exists()) {
            outDir.mkdirs();
        }
        File outFile = new File(outDir, name + ".pdb");
        PDBWriter writer = new PDBWriter(new FileWriter(outFile));
        writer.writeMolecule(atomContainer);
        writer.close();
    }
    
    @Test
    public void test_C70_D5h() throws CDKException, IOException {
        testFullerene("C60-76", "C70-D5h", new File("output/threeDee", "C60-76"));
    }
    
    @Test
    public void test_No_9_C2v() throws CDKException, IOException {
        testFullerene("C36", "No.9-C2v", new File("output/threeDee", "C36"));
    }
    
    @Test
    public void test_No_15_D6h() throws CDKException, IOException {
        testFullerene("C36", "No.15-D6h", new File("output/threeDee", "C36"));
    }
    
    @Test
    public void test_No_11_C2() throws CDKException, IOException {
        testFullerene("C36", "No.11-C2", new File("output/threeDee", "C36"));
    }
    
    @Test
    public void test_No_32_C1() throws CDKException, IOException {
        testFullerene("C42", "No.32-C1", new File("output/threeDee", "C42"));
    }
    
    @Test
    public void test_No_36_C1() throws CDKException, IOException {
        testFullerene("C42", "No.36-C1", new File("output/threeDee", "C42"));
    }
    
    @Test
    public void test_No_3_D3d() throws CDKException, IOException {
        testFullerene("C32", "No.3-D3d", new File("output/threeDee", "C32"));
    }
    
    @Test
    public void test_c24d6d() throws CDKException, IOException {
        testFullerene("C20-30", "c24d6d", new File("output/threeDee", "C20-30"));
    }
    
    @Test
    public void testSmallest() throws CDKException, IOException {
        testFullerene("C20-30", "c20ih", new File("output/threeDee", "C20-30"));
    }
    
    @Test
    public void testC70D5h() throws CDKException, IOException {
        testFullerene("C60-76", "C70-D5h", new File("output/threeDee", "C60-76"));
    }
    
    @Test
    public void testBucky() throws CDKException, IOException {
        testFullerene("C60-76", "C60-Ih", new File("output/threeDee", "C60-76"));
    }

}
