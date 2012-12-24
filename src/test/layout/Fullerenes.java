package test.layout;

import io.Chem3DCartesian1Reader;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.awt.image.RenderedImage;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import javax.imageio.ImageIO;
import javax.vecmath.Point2d;

import layout.GraphLayout;
import layout.ParameterSet;
import layout.Representation;

import org.junit.Test;
import org.openscience.cdk.DefaultChemObjectBuilder;
import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.group.AtomContainerPrinter;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IChemObjectBuilder;
import org.openscience.cdk.renderer.AtomContainerRenderer;
import org.openscience.cdk.renderer.font.AWTFontManager;
import org.openscience.cdk.renderer.generators.BasicAtomGenerator;
import org.openscience.cdk.renderer.generators.BasicBondGenerator;
import org.openscience.cdk.renderer.generators.BasicSceneGenerator;
import org.openscience.cdk.renderer.generators.IGenerator;
import org.openscience.cdk.renderer.visitor.AWTDrawVisitor;
import org.openscience.cdk.silent.AtomContainer;

import planar.AtomContainerEmbedder;
import planar.AtomContainerEmbedding;
import planar.BlockEmbedding;
import planar.Face;
import planar.Vertex;
import refiner.AnnealingRefiner;
import refiner.PlestenjakRefiner;

public class Fullerenes {
    
    public static final String DIR = 
            "/Users/maclean/Documents/molecules/FullereneLib/";
    
    public final static IChemObjectBuilder builder = DefaultChemObjectBuilder.getInstance();
    
    public final int WIDTH = 800;
    
    public final int HEIGHT = 800;
    
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
        int faceIndex = 0;
        for (Face face : blockEmbedding.getFaces()) {
            System.out.println(faceIndex + "\t" + face);
            faceIndex++;
        }
        System.out.println("ExtFace " + blockEmbedding.getExternalFace());
        System.out.println("InnDual " + blockEmbedding.calculateInnerDual());
        GraphLayout layout = new GraphLayout(new ParameterSet());
        return layout.layout(embedding, new Rectangle2D.Double(0, 0, WIDTH, HEIGHT));
    }
    
    public void draw(Representation rep, BlockEmbedding embedding, IAtomContainer ac, int w, int h, String file) throws FileNotFoundException, IOException {
        Rectangle canvas = new Rectangle(0, 0, w, h);
        Rectangle bigCanvas = new Rectangle(0, 0, w * 10, h * 10);
        if (embedding != null) {
//            rep = new PlestenjakRefiner(canvas).refine(rep, embedding);
//            rep = new SpringRefiner(100).refine(rep, embedding);
            rep = new AnnealingRefiner(bigCanvas).refine(rep, embedding);
        }
        for (Vertex v : rep.getVertices()) {
            Point2D point = rep.getPoint(v);
            Point2d p2d = new Point2d(point.getX(), point.getY());
            ac.getAtom(v.getIndex()).setPoint2d(p2d);
        }
        Image image = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
        Graphics2D graphics = (Graphics2D) image.getGraphics();
        graphics.setColor(Color.WHITE);
        graphics.fill(canvas);
        List<IGenerator<IAtomContainer>> generators = new ArrayList<IGenerator<IAtomContainer>>();
        generators.add(new BasicSceneGenerator());
        generators.add(new BasicBondGenerator());
        generators.add(new BasicAtomGenerator());
//        generators.add(new AtomNumberGenerator());
        AWTFontManager fontManager = new AWTFontManager();
        AtomContainerRenderer renderer = new AtomContainerRenderer(generators, fontManager);
        renderer.setup(ac, canvas);
        renderer.getRenderer2DModel().set(BasicAtomGenerator.CompactAtom.class, true);
        renderer.getRenderer2DModel().set(BasicAtomGenerator.AtomRadius.class, 2.0);
        renderer.getRenderer2DModel().set(BasicAtomGenerator.CompactShape.class, BasicAtomGenerator.Shape.OVAL);
        renderer.getRenderer2DModel().set(BasicAtomGenerator.KekuleStructure.class, true);
        fontManager.setFontForZoom(0.5);
        renderer.paint(ac, new AWTDrawVisitor(graphics), canvas, false);
        ImageIO.write((RenderedImage) image, "PNG", new FileOutputStream(file));
    }
    
    public void testFullerene(String path, String name, File outDir) throws CDKException, IOException {
        IAtomContainer atomContainer = readFile(new File(new File(DIR, path), name + ".cc1"));
        AtomContainerEmbedding embedding = AtomContainerEmbedder.embed(atomContainer);
        BlockEmbedding blockEmbedding = embedding.getBlockEmbedding(0);
        int faceIndex = 0;
        for (Face face : blockEmbedding.getFaces()) {
            System.out.println(faceIndex + "\t" + face);
            faceIndex++;
        }
        System.out.println("ExtFace " + blockEmbedding.getExternalFace());
        System.out.println("InnDual " + blockEmbedding.calculateInnerDual());
        GraphLayout layout = new GraphLayout(new ParameterSet());
        Rectangle2D canvas = new Rectangle2D.Double(0, 0, WIDTH, HEIGHT);
        Representation rep = layout.layout(embedding, canvas); 
        draw(rep, blockEmbedding, atomContainer, WIDTH, HEIGHT, new File(outDir, name + ".png").toString());
    }
    
    public void layoutDir(String dirName) throws CDKException, IOException {
        File outDir = new File("output", dirName);
        if (!outDir.exists()) {
            outDir.mkdirs();
        }
        File dirFile = new File(DIR, dirName);
        List<String> filenames = Arrays.asList(dirFile.list());
        Collections.shuffle(filenames);
        for (String filename : filenames) {
            testFullerene(dirName, filename.substring(0, filename.length() - 4), outDir);
        }
    }
    
    @Test
    public void testC34_No1_C2() throws CDKException, IOException {
        testFullerene("C34", "No.1-C2", new File("output", "C34"));
    }
    
    @Test
    public void testC34_No6_C3v() throws CDKException, IOException {
        testFullerene("C34", "No.6-C3v", new File("output", "C34"));
    }
    
    @Test
    public void testSmallest() throws CDKException, IOException {
        testFullerene("C20-30", "c20ih", new File("output", "C20-30"));
    }
    
    @Test
    public void testC20_30() throws CDKException, IOException {
        layoutDir("C20-30");
    }
    
    @Test
    public void testC32() throws CDKException, IOException {
        layoutDir("C32");
    }
    
    @Test
    public void testC34() throws CDKException, IOException {
        layoutDir("C34");
    }
    
    @Test
    public void testC36() throws CDKException, IOException {
        layoutDir("C36");
    }
    
    @Test
    public void testCage() throws FileNotFoundException, IOException {
        String cage = "C0C1C2C3C4C5C6C7C8C9 0:1(1),0:5(1),0:6(1),1:2(1),1:6(1),"
                     + "2:3(1),2:7(1),3:4(1),3:7(1),4:5(1),4:8(1),5:8(1)," 
                     + "6:9(1),7:9(1),8:9(1)";
        IAtomContainer atomContainer = AtomContainerPrinter.fromString(cage, builder);
        Representation rep = layout(atomContainer);
        draw(rep, null, atomContainer, WIDTH, HEIGHT, "cage.png");
    }
}
