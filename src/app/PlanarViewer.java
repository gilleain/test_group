package app;

import io.Chem3DCartesian1Reader;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.vecmath.Point2d;

import layout.ConcentricFaceLayout;
import layout.Representation;

import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.geometry.GeometryTools;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.renderer.AtomContainerRenderer;
import org.openscience.cdk.renderer.RendererModel;
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
import util.MyAtomNumberGenerator;
import util.RingPlateGenerator;

public class PlanarViewer extends JFrame implements ActionListener, MouseListener {

    private static final int PREF_WIDTH  = 500;
    private static final int PREF_HEIGHT = 500;

    public final int RADIUS = 80;

    public final int EDGE_LEN = 80;

    public class DrawPanel extends JPanel {
        
        public boolean showNumbers;

        private IAtomContainer ac;
        
        private boolean laidOut;
        
        private BlockEmbedding blockEmbedding;
        
        private Map<Point2d, Face> faceCenterMap;
        
        private AWTFontManager fontManager;
        
        private AtomContainerRenderer renderer;
        
        private RingPlateGenerator plateGenerator;

        public DrawPanel() {
            laidOut = false;
            faceCenterMap = new HashMap<Point2d, Face>();
        }

        public void paint(Graphics g) {
            Dimension size = getSize();
            g.setColor(Color.WHITE);
            g.fillRect(0, 0, size.width, size.height);
            if (ac != null) {
                if (!laidOut) {
                   layout(size);
                   setupRenderer();
                }
                draw(size.width, size.height, showNumbers, g);
            }
        }
        
        private void layout(Dimension size) {
            AtomContainerEmbedding embedding = AtomContainerEmbedder.embed(ac);
            layout(size, embedding.getBlockEmbedding(0));
        }

        private void layout(Dimension size, BlockEmbedding embedding) {
            try {
                Representation rep = new ConcentricFaceLayout(RADIUS, EDGE_LEN).layout(
                    embedding, new Rectangle2D.Double(0, 0, WIDTH, HEIGHT));
                for (Vertex v : rep.getVertices()) {
                    Point2D point = rep.getPoint(v);
                    Point2d p2d = new Point2d(point.getX(), point.getY());
                    ac.getAtom(v.getIndex()).setPoint2d(p2d);
                }
                GeometryTools.center(ac, size);
                for (Face face : embedding.getFaces()) {
                    Point2d center = new Point2d();
                    for (Vertex v : face) {
                        Point2d point = ac.getAtom(v.getIndex()).getPoint2d();
                        center.x += point.x;
                        center.y += point.y;
                    }
                    center.x /= face.vsize();
                    center.y /= face.vsize();
                    faceCenterMap.put(center, face);
                }
                laidOut = true;
            } catch (Exception e) {
                System.out.println(e);
                return;
            }
            blockEmbedding = embedding;
        }
        
        private void reEmbed(Face face) {
            Face oldExternalFace = blockEmbedding.getExternalFace();
            List<Face> faces = blockEmbedding.getFaces();
            faces.remove(face);
            faces.add(oldExternalFace);
            
            BlockEmbedding embedding = new BlockEmbedding(ac);
            embedding.setExternalFace(face);
            embedding.setFaces(faces);
            layout(getSize(), embedding);
            plateGenerator.embedding = blockEmbedding;
            repaint();
        }
        
        private void setupRenderer() {
            List<IGenerator<IAtomContainer>> generators = new ArrayList<IGenerator<IAtomContainer>>();
            generators.add(new BasicSceneGenerator());
            generators.add(new BasicBondGenerator());
            generators.add(new BasicAtomGenerator());
            plateGenerator = new RingPlateGenerator();
            plateGenerator.embedding = blockEmbedding;
            generators.add(plateGenerator);
            generators.add(new MyAtomNumberGenerator());
            fontManager = new AWTFontManager();
            renderer = new AtomContainerRenderer(generators, fontManager);
        }
        
        public void setAtomContainer(IAtomContainer atomContainer) {
            ac = atomContainer;
            laidOut = false;
        }

        public void draw(int w, int h, boolean numberAtoms, Graphics g) {
            Rectangle canvas = new Rectangle(0, 0, w, h);

            Graphics2D graphics = (Graphics2D) g;
            graphics.setColor(Color.WHITE);
            graphics.fill(canvas);
            
            renderer.setup(ac, canvas);
            RendererModel model = renderer.getRenderer2DModel(); 
            model.set(BasicAtomGenerator.CompactAtom.class, true);
            model.set(BasicAtomGenerator.AtomRadius.class, 2.0);
            model.set(BasicAtomGenerator.CompactShape.class, BasicAtomGenerator.Shape.OVAL);
            model.set(BasicAtomGenerator.KekuleStructure.class, true);
            if (numberAtoms) {
                model.set(MyAtomNumberGenerator.WillDrawAtomNumbers.class, true);
                model.set(MyAtomNumberGenerator.AtomNumberStartCount.class, 0);
            } else {
                model.set(MyAtomNumberGenerator.WillDrawAtomNumbers.class, false);
            }
            renderer.paint(ac, new AWTDrawVisitor(graphics), canvas, false);
        }
        
        public void click(Point p) {
            double y = getSize().getHeight() - p.y;
            Point2d p2d = new Point2d(p.x, y);
            double bestDist = Double.MAX_VALUE;
            Point2d winner = null;
            for (Point2d center : faceCenterMap.keySet()) {
                double d = center.distance(p2d);
                System.out.println(f(center) + " " + d(d) + " " + faceCenterMap.get(center));
                if (d < bestDist) {
                    bestDist = d;
                    winner = center;
                }
            }
            Face pickedFace = faceCenterMap.get(winner);
            System.out.println(p + "\t" + pickedFace);
            reEmbed(pickedFace);
        }

    }
    
    private String f(Point2d p) {
        if (p == null) {
            return "NULL";
        } else {
            return String.format("(%.1f,  %.1f)", p.x, p.y);
        }
    }
    
    private String d(double d) {
        return String.valueOf(Math.round(d));
    }

    private DrawPanel view;

    private JButton loadButton;
    
    private JButton showNumbersButton;

    public PlanarViewer() {
        setLayout(new BorderLayout());
        view = new DrawPanel();
        Dimension dim = new Dimension(PREF_WIDTH, PREF_HEIGHT);
        view.setPreferredSize(dim);
        view.addMouseListener(this);
        add(view, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel();
        
        loadButton = new JButton("Load");
        loadButton.setActionCommand("LOAD");
        loadButton.addActionListener(this);
        buttonPanel.add(loadButton);
        
        showNumbersButton = new JButton("Num");
        showNumbersButton.setActionCommand("NUM");
        showNumbersButton.addActionListener(this);
        buttonPanel.add(showNumbersButton);
        
        add(buttonPanel, BorderLayout.SOUTH);

        pack();
        setVisible(true);
    }

    public IAtomContainer load(String filePath) throws CDKException, IOException {
        return load(new File(filePath));
    }
    
    public IAtomContainer load(File file) throws CDKException, IOException {
        Chem3DCartesian1Reader reader = 
                new Chem3DCartesian1Reader(new FileReader(file));
        IAtomContainer atomContainer = reader.read(new AtomContainer());
        reader.close();
        return atomContainer;
    }

    @Override
    public void actionPerformed(ActionEvent event) {
        if (event.getActionCommand().equals("LOAD")) {
            JFileChooser fileChooser = 
                    new JFileChooser("/Users/maclean/Documents/molecules/FullereneLib/");
            fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
            try {
                if (fileChooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
                    view.setAtomContainer(load(fileChooser.getSelectedFile()));
                    view.repaint();
                }
            } catch (Exception e) {
                // TODO
            }
        } else {
            view.showNumbers = !view.showNumbers;
            view.repaint();
        }
    }

    public static void main(String[] args) {
        new PlanarViewer();
    }

    @Override
    public void mouseClicked(MouseEvent me) {
        view.click(me.getPoint());
    }

    @Override
    public void mouseEntered(MouseEvent arg0) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void mouseExited(MouseEvent arg0) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void mousePressed(MouseEvent arg0) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void mouseReleased(MouseEvent arg0) {
        // TODO Auto-generated method stub
        
    }

}
