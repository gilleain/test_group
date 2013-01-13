package util;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IRing;
import org.openscience.cdk.interfaces.IRingSet;
import org.openscience.cdk.signature.MoleculeSignature;
import org.openscience.cdk.signature.Orbit;
import org.openscience.cdk.silent.Ring;
import org.openscience.cdk.silent.RingSet;

import planar.BlockEmbedding;
import planar.Face;
import planar.Vertex;

/**
 * Color the rings of an atom container based on equivalence classes determined from the signatures
 * of the atoms in the ring.
 * 
 * This used an accessory class that gives a 'code' for each ring. For example, if the atoms of the
 * ring are in the signature orbits 1, 2, and 3 then the ring code is "123" - which matches any 
 * other ring with the same code (cyclically). So "1132" matches to "3211" but not "1123".
 * 
 * @author maclean
 *
 */
public class SignatureRingColorer {

    private class RingCode {

        private List<Integer> code;

        boolean isMinimalForm;

        private String minimalForm;

        public RingCode() {
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
            if (other instanceof RingCode) {
                RingCode oF = (RingCode) other;
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
    
    private Map<IRing, Color> ringSetColors;
    
    public SignatureRingColorer(IAtomContainer atomContainer, BlockEmbedding embedding) {
        List<IRingSet> ringEqCl = getRingEqClFromSigs(atomContainer, embedding);
        ringSetColors = new HashMap<IRing, Color>();
        for (int i = 0; i < ringEqCl.size(); i++) {
            Color color = ColorRamp.getColor(i, 0, ringEqCl.size());
            IRingSet ringSet = ringEqCl.get(i); 
            for (int j = 0; j < ringSet.getAtomContainerCount(); j++) {
                IRing ring = (IRing) ringSet.getAtomContainer(j);
                ringSetColors.put(ring, color);
            }
        }
    }
    
    public List<IRing> getRings() {
        return new ArrayList<IRing>(ringSetColors.keySet());
    }
    
    public Color getRingColor(IRing ring) {
        return ringSetColors.get(ring);
    }

    private List<IRingSet> getRingEqClFromSigs(IAtomContainer atomContainer, BlockEmbedding embedding) {
        // get the orbits of the atoms using signatures
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

        // use the faces of the embedding to determine the face/ring classes
        Map<RingCode, List<Face>> faceEqCl = new HashMap<RingCode, List<Face>>();
        for (Face face : embedding.getFaces()) {
            RingCode code = new RingCode();
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
        
        // convert these face equivalence classes directly into ring equivalence classes
        List<IRingSet> ringEqCl = new ArrayList<IRingSet>();
        for (RingCode code : faceEqCl.keySet()) {
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

}
