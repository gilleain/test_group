package test.cycle;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.List;

import javax.imageio.ImageIO;

import layout.BaseCircularLayout;
import layout.ParameterSet;
import layout.Representation;

import org.junit.Test;

import planar.Edge;
import planar.Face;
import planar.Vertex;

public class TestArch {

    @Test
    public void testArch() throws IOException {
        Representation rep = new Representation();
        BaseCircularLayout layout = new BaseCircularLayout();  
        
        // setup the base
        Vertex base0 = new Vertex(0);
        Vertex base7 = new Vertex(7);
        Point2D base0P = new Point2D.Double(150, 400);
        Point2D base7P = new Point2D.Double(350, 400);
        rep.addPoint(base0, base0P);
        rep.addPoint(base7, base7P);
        Line2D baseLine = new Line2D.Double(base0P, base7P);
        rep.addLine(new Edge(base0, base7), baseLine);
        
        double edgeLen = 150;
        int faceSize = 8;
        Face face = makeFace(faceSize);
        int archSize = faceSize - 4;
        
        Vertex archStart = face.getVertex(1);
        Point2D archStartP = new Point2D.Double(100, 200);
        rep.addPoint(archStart, archStartP);
        Line2D spokeA = new Line2D.Double(base0P, archStartP);
        rep.addLine(new Edge(base0, archStart), spokeA);
        
        Vertex archEnd = face.getVertex(6);
        Point2D archEndP = new Point2D.Double(400, 200);
        rep.addPoint(archEnd, archEndP);
        Line2D spokeB = new Line2D.Double(base7P, archEndP);
        rep.addLine(new Edge(base7, archEnd), spokeB);
        
        Point2D archCenter = getArchCenter(archStartP, archEndP);
        
        double currentAngle = layout.angle(archCenter, archStartP);
        double addAngle = Math.toRadians(180 / archSize);
//        Vertex prevVert = archStart;
        Point2D prevPoint = archStartP;
        int startIndex = face.indexOf(archStart) + 1;
        Edge edge = face.getEdge(archStart, face.getVertex(startIndex));
        List<Edge> edges = face.getEdges();
        int edgeIndex = edges.indexOf(edge);
        for (int index = startIndex; index < startIndex + archSize; index++) {
            Vertex vertex = face.getVertex(index);
            System.out.println("Vertex " + vertex);
            System.out.println("current = " + deg(currentAngle) + " adding " + deg(addAngle));
            currentAngle += addAngle;
            if (currentAngle >= 2 * Math.PI) {
                currentAngle -= 2 * Math.PI;
            }
            Point2D nextP = layout.makeNextPoint(archCenter, currentAngle, edgeLen);
            rep.addPoint(vertex, nextP);
            Line2D line = new Line2D.Double(prevPoint, nextP);
            rep.addLine(edges.get(edgeIndex), line);
            prevPoint = nextP;
            edgeIndex++;
        }
        Line2D line = new Line2D.Double(prevPoint, archEndP);
        rep.addLine(edges.get(edgeIndex), line);
        draw(rep, "arch.png");
    }
    
    private double deg(double rad) {
        return Math.toDegrees(rad);
    }
    
    public void draw(Representation rep, String filename) throws IOException {
        File dir = new File("output/test/cycle");
        if (!dir.exists()) { dir.mkdirs(); }
        int W = 500;
        int H = 500;
        BufferedImage image = new BufferedImage(W, H, BufferedImage.TYPE_4BYTE_ABGR);
        Graphics2D g = (Graphics2D) image.getGraphics();
        g.setColor(Color.WHITE);
        g.fillRect(0, 0, W, H);
        g.setColor(Color.BLACK);
        rep.draw(g, new ParameterSet());
        ImageIO.write(image, "PNG", new File(dir, filename));
    }
    
    private Face makeFace(int faceSize) {
        Face face = new Face();
        for (int i = 0; i < faceSize; i++) {
            face.add(new Vertex(i));
            if (i > 0) {
                face.add(i - 1, i);
            }
        }
        face.add(0, faceSize - 1);
        return face;
    }
    
    private Point2D getArchCenter(Point2D archStartP, Point2D archEndP) {
        double mx = (archStartP.getX() + archEndP.getX()) / 2;
        double my = (archStartP.getY() + archEndP.getY()) / 2;
        // XXX - for now, just use the midpoint
        return new Point2D.Double(mx, my);
    }
    
}
