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
            Color color = ColorRamp.getColor(orbitIndex, 0, orbits.size());
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
}
