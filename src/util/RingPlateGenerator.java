package util;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

import javax.vecmath.Point2d;
import javax.vecmath.Vector2d;

import org.openscience.cdk.geometry.GeometryTools;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IRingSet;
import org.openscience.cdk.renderer.RendererModel;
import org.openscience.cdk.renderer.elements.ElementGroup;
import org.openscience.cdk.renderer.elements.GeneralPath;
import org.openscience.cdk.renderer.elements.IRenderingElement;
import org.openscience.cdk.renderer.elements.path.Close;
import org.openscience.cdk.renderer.elements.path.LineTo;
import org.openscience.cdk.renderer.elements.path.MoveTo;
import org.openscience.cdk.renderer.elements.path.PathElement;
import org.openscience.cdk.renderer.generators.IGenerator;
import org.openscience.cdk.renderer.generators.IGeneratorParameter;
import org.openscience.cdk.ringsearch.SSSRFinder;

public class RingPlateGenerator implements IGenerator<IAtomContainer> {

    @Override
    public List<IGeneratorParameter<?>> getParameters() {
        return new ArrayList<IGeneratorParameter<?>>();
    }

    @Override
    public IRenderingElement generate(IAtomContainer atomContainer, RendererModel model) {
        ElementGroup ringPaths = new ElementGroup();
        SSSRFinder ringFinder = new SSSRFinder(atomContainer);
        List<IRingSet> ringEqCl = ringFinder.findEquivalenceClasses();
        int n = ringEqCl.size();
        Color[] colors = new Color[n];
        for (int i = 0; i < n; i++) {
            colors[i] = colourRamp(i, 0, n);
        }
        int colorIndex = 0; 
        for (IRingSet ringSet : ringEqCl) {
            for (IAtomContainer ring : ringSet.atomContainers()) {
                ringPaths.add(generateRing(ring, colors[colorIndex]));
            }
            colorIndex++;
        }
        return ringPaths;
    }
    
    private GeneralPath generateRing(IAtomContainer atomContainer, Color color) {
        Point2d center = GeometryTools.get2DCenter(atomContainer);
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
