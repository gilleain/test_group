package test.layout;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.awt.image.RenderedImage;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.imageio.ImageIO;
import javax.vecmath.Point2d;

import layout.ConcentricFaceLayout;
import layout.Representation;

import org.junit.Test;
import org.openscience.cdk.DefaultChemObjectBuilder;
import org.openscience.cdk.geometry.GeometryTools;
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

import planar.BlockEmbedding;
import planar.Face;
import planar.Vertex;
import util.MyAtomNumberGenerator;

public class ConcentricFaceLayoutTest {
    
    public final static IChemObjectBuilder builder = DefaultChemObjectBuilder.getInstance();
    
    private void tmpPrintCoords(IAtomContainer ac) {
        for (int i = 0; i < ac.getAtomCount(); i++) {
            Point2d p2d = ac.getAtom(i).getPoint2d();
            if (p2d == null) {
                System.out.println(i + "\t" + null);
            } else {
                System.out.println(i + "\t" + Math.round(p2d.x) + ", " + Math.round(p2d.y));
            }
        }
    }
    
    private Face makeFace(int... vertices) {
        Face face = new Face();
        for (int vertex : vertices) {
            face.add(new Vertex(vertex));
        }
        for (int i = 1; i < vertices.length; i++) {
            face.add(i - 1, i);
        }
        face.add(vertices.length - 1, 0);
        return face;
    }
    
    @Test
    public void testCup() throws FileNotFoundException, IOException {
        IAtomContainer atomContainer = AtomContainerPrinter.fromString(
                "C0C1C2C3C4C5C6C7C8C9C0C1 0:1(1),0:3(1),0:4(1)," +
                "1:2(1),1:5(1),2:3(1),2:6(1),3:7(1),4:8(1),4:11(1)," +
                "5:8(1),5:9(1),6:9(1),6:10(1),7:10(1),7:11(1)", builder);
        BlockEmbedding embedding = new BlockEmbedding(atomContainer);
        List<Face> faces = new ArrayList<Face>();
        faces.add(makeFace(0, 1, 2, 3));
        faces.add(makeFace(0, 1, 5, 8, 4));
        faces.add(makeFace(1, 5, 9, 6, 2));
//        faces.add(makeFace(1, 2, 6, 9, 5));
        faces.add(makeFace(2, 6, 10, 7, 3));
        faces.add(makeFace(3, 7, 11, 4, 0));
        
        embedding.setFaces(faces);
        embedding.setExternalFace(makeFace(8, 5, 9, 6, 10, 7, 11, 4));
        
        ConcentricFaceLayout layout = new ConcentricFaceLayout(50, 40);
        Representation rep = layout.layout(embedding, new Rectangle2D.Double(0, 0, 500, 500));
        draw(rep, true, embedding, atomContainer, 500, 500, "output/test/cycle/cup.png");
    }
    
    @Test
    public void testDoubleCenter() throws FileNotFoundException, IOException {
        IAtomContainer atomContainer = AtomContainerPrinter.fromString(
                "C0C1C2C3C4C5C6C7C8C9 0:1(1),0:2(1),0:4(1),1:2(1),1:3(1),1:6(1),1:7(1)," +
                "2:3(1),2:8(1),2:9(1),3:5(1),4:6(1),4:8(1),5:7(1),5:9(1),6:7(1),8:9(1)", builder);
        BlockEmbedding embedding = new BlockEmbedding(atomContainer);
        List<Face> faces = new ArrayList<Face>();
        faces.add(makeFace(0, 1, 2));
        faces.add(makeFace(1, 3, 2));
        faces.add(makeFace(0, 1, 6, 4));
        faces.add(makeFace(1, 6, 7));
        faces.add(makeFace(1, 7, 5, 3));
        faces.add(makeFace(5, 9, 2, 3));
        faces.add(makeFace(2, 9, 8));
        faces.add(makeFace(0, 2, 8, 4));
        
        embedding.setFaces(faces);
        embedding.setExternalFace(makeFace(4, 6, 7, 5, 9, 8));
        
        ConcentricFaceLayout layout = new ConcentricFaceLayout(50, 50);
        Representation rep = layout.layout(embedding, new Rectangle2D.Double(0, 0, 500, 500));
        draw(rep, true, embedding, atomContainer, 500, 500, "output/test/cycle/dubs.png");
    }
    
    @Test
    public void testCubane() throws FileNotFoundException, IOException {
        IAtomContainer atomContainer = AtomContainerPrinter.fromString(
                "C0C1C2C3C4C5C6C7 0:1(1),0:2(1),0:3(1),1:4(1),1:5(1),2:4(1)," +
                "2:6(1),3:5(1),3:6(1),5:7(1),6:7(1)", builder);
        BlockEmbedding embedding = new BlockEmbedding(atomContainer);
        List<Face> faces = new ArrayList<Face>();
        faces.add(makeFace(0, 1, 4, 2));
        faces.add(makeFace(0, 1, 5, 3));
        faces.add(makeFace(0, 2, 6, 3));
        faces.add(makeFace(1, 5, 7, 4));
        faces.add(makeFace(1, 5, 7, 4));
        faces.add(makeFace(2, 4, 7, 6));
        
        embedding.setFaces(faces);
        embedding.setExternalFace(makeFace(3, 6, 7, 5));
        
        ConcentricFaceLayout layout = new ConcentricFaceLayout(50, 40);
        Representation rep = layout.layout(embedding, new Rectangle2D.Double(0, 0, 500, 500));
        draw(rep, false, embedding, atomContainer, 500, 500, "output/test/cycle/cube.png");
    }
    
    @Test
    public void testIcosahedron() throws FileNotFoundException, IOException {
        IAtomContainer atomContainer = AtomContainerPrinter.fromString(
                // GAH! XXX FIXME in ACP
                "C0C1C2C3C4C5C6C7C8C9C0C1C2C3C4C5C6C7C8C9 " +
                "0:1(1),0:4(1),0:5(1),1:2(1),1:6(1),2:3(1),2:7(1),3:4(1)," +
                "3:8(1),4:9(1),5:10(1),5:14(1),6:10(1),6:11(1),7:11(1)," +
                "7:12(1),8:12(1),8:13(1),9:13(1),9:14(1),10:15(1),11:16(1)," +
                "12:17(1),13:18(1),14:19(1),15:16(1),15:19(1),16:17(1)," +
                "17:18(1),18:19(1)", builder);
        AtomContainerPrinter.print(atomContainer);
        BlockEmbedding embedding = new BlockEmbedding(atomContainer);
        List<Face> faces = new ArrayList<Face>();
        faces.add(makeFace(0, 1, 2, 3, 4));
        faces.add(makeFace(0, 5, 10, 6, 1));
        faces.add(makeFace(1, 6, 11, 7, 2));
        faces.add(makeFace(2, 7, 12, 8, 3));
        faces.add(makeFace(3, 8, 13, 9, 4));
        faces.add(makeFace(4, 9, 14, 5, 0));
        faces.add(makeFace(10, 15, 16, 11, 6));
        faces.add(makeFace(11, 16, 17, 12, 7));
        faces.add(makeFace(12, 17, 18, 13, 8));
        faces.add(makeFace(13, 18, 19, 14, 9));
        faces.add(makeFace(14, 19, 15, 10, 5));
        
        embedding.setFaces(faces);
        embedding.setExternalFace(makeFace(15, 16, 17, 18, 19));
        
        ConcentricFaceLayout layout = new ConcentricFaceLayout(50, 40);
        Representation rep = layout.layout(embedding, new Rectangle2D.Double(0, 0, 500, 500));
        draw(rep, false, embedding, atomContainer, 500, 500, "output/test/cycle/icos.png");
    }
    
    public void draw(Representation rep, boolean drawNum, BlockEmbedding embedding, IAtomContainer ac, int w, int h, String file) throws FileNotFoundException, IOException {
        Rectangle canvas = new Rectangle(0, 0, w, h);
//        Rectangle bigCanvas = new Rectangle(0, 0, w * 10, h * 10);
        if (embedding != null) {
//            rep = new PlestenjakRefiner(canvas).refine(rep, embedding);
//            rep = new SpringRefiner(100).refine(rep, embedding);
//            rep = new AnnealingRefiner(bigCanvas).refine(rep, embedding);
        }
        for (Vertex v : rep.getVertices()) {
            Point2D point = rep.getPoint(v);
            Point2d p2d = new Point2d(point.getX(), point.getY());
            int atomIndex = v.getIndex();
            ac.getAtom(atomIndex).setPoint2d(p2d);
        }
        GeometryTools.center(ac, new Dimension(w, h));
        tmpPrintCoords(ac);
        Image image = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
        Graphics2D graphics = (Graphics2D) image.getGraphics();
        graphics.setColor(Color.WHITE);
        graphics.fill(canvas);
        List<IGenerator<IAtomContainer>> generators = new ArrayList<IGenerator<IAtomContainer>>();
        generators.add(new BasicSceneGenerator());
        generators.add(new BasicBondGenerator());
        generators.add(new BasicAtomGenerator());
        if (drawNum) {
            generators.add(new MyAtomNumberGenerator());
        }
        AWTFontManager fontManager = new AWTFontManager();
        AtomContainerRenderer renderer = new AtomContainerRenderer(generators, fontManager);
        renderer.setup(ac, canvas);
        renderer.getRenderer2DModel().set(BasicAtomGenerator.CompactAtom.class, true);
        renderer.getRenderer2DModel().set(BasicAtomGenerator.AtomRadius.class, 2.0);
        renderer.getRenderer2DModel().set(BasicAtomGenerator.CompactShape.class, BasicAtomGenerator.Shape.OVAL);
        renderer.getRenderer2DModel().set(BasicAtomGenerator.KekuleStructure.class, true);
        renderer.getRenderer2DModel().set(MyAtomNumberGenerator.AtomNumberStartCount.class, 0);
        fontManager.setFontForZoom(0.5);
        renderer.paint(ac, new AWTDrawVisitor(graphics), canvas, false);
        ImageIO.write((RenderedImage) image, "PNG", new FileOutputStream(file));
    }

}
