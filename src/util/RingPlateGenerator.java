package util;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.vecmath.Point2d;
import javax.vecmath.Vector2d;

import org.openscience.cdk.geometry.GeometryTools;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IRing;
import org.openscience.cdk.interfaces.IRingSet;
import org.openscience.cdk.renderer.RendererModel;
import org.openscience.cdk.renderer.elements.ElementGroup;
import org.openscience.cdk.renderer.elements.GeneralPath;
import org.openscience.cdk.renderer.elements.IRenderingElement;
import org.openscience.cdk.renderer.elements.TextElement;
import org.openscience.cdk.renderer.elements.path.Close;
import org.openscience.cdk.renderer.elements.path.LineTo;
import org.openscience.cdk.renderer.elements.path.MoveTo;
import org.openscience.cdk.renderer.elements.path.PathElement;
import org.openscience.cdk.renderer.generators.IGenerator;
import org.openscience.cdk.renderer.generators.IGeneratorParameter;
import org.openscience.cdk.ringsearch.SSSRFinder;
import org.openscience.cdk.signature.MoleculeSignature;
import org.openscience.cdk.signature.Orbit;
import org.openscience.cdk.silent.Ring;
import org.openscience.cdk.silent.RingSet;

import planar.BlockEmbedding;
import planar.Face;
import planar.Vertex;

public class RingPlateGenerator implements IGenerator<IAtomContainer> {
    
    public BlockEmbedding embedding;    // UGH

    @Override
    public List<IGeneratorParameter<?>> getParameters() {
        return new ArrayList<IGeneratorParameter<?>>();
    }

    @Override
    public IRenderingElement generate(IAtomContainer atomContainer, RendererModel model) {
        ElementGroup ringPaths = new ElementGroup();
//        List<IRingSet> ringEqCl = getRingEqClFromSSSR(atomContainer);
        List<IRingSet> ringEqCl = getRingEqClFromSigs(atomContainer);
        int n = ringEqCl.size();
        Color[] colors = new Color[n];
        for (int i = 0; i < n; i++) {
            colors[i] = colourRamp(i, 0, n);
        }
        int colorIndex = 0; 
        for (IRingSet ringSet : ringEqCl) {
            for (IAtomContainer ring : ringSet.atomContainers()) {
                Point2d center = GeometryTools.get2DCenter(ring);
                ringPaths.add(generateRing(ring, center, colors[colorIndex]));
//                ringPaths.add(new TextElement(
//                        center.x, center.y, String.valueOf(colorIndex), Color.BLACK));
            }
            
            colorIndex++;
        }
        return ringPaths;
    }
    
    private List<IRingSet> getRingEqClFromSSSR(IAtomContainer atomContainer) {
        SSSRFinder ringFinder = new SSSRFinder(atomContainer);
        return ringFinder.findEquivalenceClasses();
    }
    
    private class FaceCode {
        
        private List<Integer> code;
        
        boolean isMinimalForm;
        
        private String minimalForm;
        
        public FaceCode() {
            code = new ArrayList<Integer>();
            isMinimalForm = true;
            minimalForm = "";
        }
        
        public void add(Integer i) {
            code.add(i);
            isMinimalForm = false;
        }
        
        private String calcMinimalForm() {
            int n = code.size();
            String[] forwardForms = new String[n];
            for (int i = 0; i < n; i++) {
                String form = new String();
                int j = i;
                for (int counter = 0; counter < n; counter++) {
                    form += code.get(j);
                    if (j == n - 1) {
                        j = 0;
                    } else {
                        j++;
                    }
                }
                forwardForms[i] = form;
            }
            
            String[] backwardForms = new String[n];
            for (int i = 0; i < n; i++) {
                String form = new String();
                int j = i;
                for (int counter = 0; counter < n; counter++) {
                    form += code.get(j);
                    if (j == 0) {
                        j = n - 1;
                    } else {
                        j--;
                    }
                }
                backwardForms[i] = form;
            }
            
            Map<String, List<Integer>> posMap = new HashMap<String, List<Integer>>();
            for (int i = 0; i < n; i++) {
                String fForm = forwardForms[i];
                if (posMap.containsKey(fForm)) {
                    posMap.get(fForm).add(i);
                } else {
                    List<Integer> pos = new ArrayList<Integer>();
                    pos.add(i);
                    posMap.put(fForm, pos);
                }
                String bForm = backwardForms[i];
                if (posMap.containsKey(bForm)) {
                    posMap.get(bForm).add(i + n);
                } else {
                    List<Integer> pos = new ArrayList<Integer>();
                    pos.add(i + n);
                    posMap.put(bForm, pos);
                }
            }
            
            List<String> keys = new ArrayList<String>(posMap.keySet());
            Collections.sort(keys);
            // doesn't matter which we pick, so long as it's consistent...
            String min = keys.get(0);
            
            return min;
        }
        
        public String getMinimalForm() {
            if (!isMinimalForm) {
                minimalForm = calcMinimalForm();
                isMinimalForm = true;
            }
            return minimalForm;
        }
        
        public boolean equals(Object other) {
            if (other instanceof FaceCode) {
                FaceCode oF = (FaceCode) other;
                return getMinimalForm().equals(oF.getMinimalForm());
            }
            return false;
        }
        
        public int hashCode() {
            return getMinimalForm().hashCode();
        }
        
        public String toString() {
            return code + "\t" + getMinimalForm();
        }
    }
    
    private List<IRingSet> getRingEqClFromSigs(IAtomContainer atomContainer) {
        MoleculeSignature molSig = new MoleculeSignature(atomContainer);
        int[] orbitMap = new int[atomContainer.getAtomCount()];
        List<Orbit> orbits = molSig.calculateOrbits();
        int oIndex = 0;
        for (Orbit o : orbits) {
            for (int i : o) {
                orbitMap[i] = oIndex;
            }
            oIndex++;
        }
        
        Map<FaceCode, List<Face>> faceEqCl = new HashMap<FaceCode, List<Face>>();
        for (Face face : embedding.getFaces()) {
            FaceCode code = new FaceCode();
            for (Vertex v : face) {
                code.add(orbitMap[v.getIndex()]);
            }
            List<Face> eqCl;
            if (faceEqCl.containsKey(code)) {
                eqCl = faceEqCl.get(code);
            } else {
                eqCl= new ArrayList<Face>();
                faceEqCl.put(code, eqCl);
            }
            eqCl.add(face);
        }
        List<IRingSet> ringEqCl = new ArrayList<IRingSet>();
        for (FaceCode code : faceEqCl.keySet()) {
//            System.out.println(code);
            List<Face> faces = faceEqCl.get(code);
            IRingSet ringSet = new RingSet();
            for (Face face : faces) {
                IRing ring = new Ring();
                for (Vertex v : face) {
                    ring.addAtom(atomContainer.getAtom(v.getIndex()));
                }
                ringSet.addAtomContainer(ring);
            }
            ringEqCl.add(ringSet);
        }
        return ringEqCl;
    }
    
    private GeneralPath generateRing(IAtomContainer atomContainer, Point2d center, Color color) {
        List<PathElement> pathElements = new ArrayList<PathElement>();
        for (int index = 0; index < atomContainer.getAtomCount(); index++) {
            IAtom atom = atomContainer.getAtom(index);
            Point2d p = atom.getPoint2d();
            Vector2d v = new Vector2d(center);
            v.sub(p);
            v.scaleAdd(0.2, v, p);
            Point2d np = new Point2d(v);
            if (index == 0) {
                pathElements.add(new MoveTo(np));
            } else {
                pathElements.add(new LineTo(np));
            }
        }
        pathElements.add(new Close());
        GeneralPath genPath = new GeneralPath(pathElements, color);
        return genPath;
    }
    
    private Color colourRamp(int v, int vmin, int vmax) {
        double r = 1.0;
        double g = 1.0;
        double b = 1.0;
        if (v < vmin) { v = vmin; }
        if (v > vmax) { v = vmax; }
        int dv = vmax - vmin;

        try  {
            if (v < (vmin + 0.25 * dv)) {
                r = 0.0;
                g = 4.0 * (v - vmin) / dv;
            } else if (v < (vmin + 0.5 * dv)) {
                r = 0.0;
                b = 1.0 + 4.0 * (vmin + 0.25 * dv - v) / dv;
            } else if (v < (vmin + 0.75 * dv)) {
                r = 4.0 * (v - vmin - 0.5  * dv) / dv;
                b = 0.0;
            } else {
                g = 1.0 + 4.0 * (vmin + 0.75 * dv - v) / dv;
                b = 0.0;
            }
            float[] hsb = Color.RGBtoHSB(
                    (int)(r * 255), (int)(g * 255), (int)(b * 255), null);
            return Color.getHSBColor(hsb[0], hsb[1], hsb[2]);
        } catch (ArithmeticException zde) {
            float[] hsb = Color.RGBtoHSB(0, 0, 0, null);
            return Color.getHSBColor(hsb[0], hsb[1], hsb[2]);
        }

    }

}
