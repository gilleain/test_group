package util;

import java.awt.Color;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.renderer.color.IAtomColorer;
import org.openscience.cdk.signature.MoleculeSignature;
import org.openscience.cdk.signature.Orbit;

public class SignatureAtomColorer implements IAtomColorer {
    
    private Map<IAtom, Color> colorMap;
    
    public SignatureAtomColorer(IAtomContainer atomContainer) {
        colorMap = new HashMap<IAtom, Color>();
        MoleculeSignature molSig = new MoleculeSignature(atomContainer);
        List<Orbit> orbits = molSig.calculateOrbits();
        int orbitIndex = 0;
        for (Orbit o : orbits) {
            Color color = colourRamp(orbitIndex, 0, orbits.size());
            System.out.println("orbit " + o.getAtomIndices() + " = " + color);
            for (int atomIndex : o) {
                colorMap.put(atomContainer.getAtom(atomIndex), color);
            }
            orbitIndex++;
        }
    }

    @Override
    public Color getAtomColor(IAtom atom) {
        return getAtomColor(atom, Color.BLACK);
    }

    @Override
    public Color getAtomColor(IAtom atom, Color defaultColor) {
        if (colorMap.containsKey(atom)) {
            return colorMap.get(atom);
        } else {
            return defaultColor;
        }
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
