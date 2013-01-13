package jmol;

import java.awt.Color;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IRing;
import org.openscience.cdk.renderer.color.IAtomColorer;

import util.SignatureRingColorer;

public class ScriptMaker {
    
    public static void printAtomColorScript(
            IAtomContainer atomContainer, IAtomColorer atomColorer, PrintStream printStream) {
        for (int atomIndex = 0; atomIndex < atomContainer.getAtomCount(); atomIndex++) {
            IAtom atom = atomContainer.getAtom(atomIndex);
            Color color = atomColorer.getAtomColor(atom);
            String selectLine = String.format("select atomno=%s;%n", atomIndex + 1);
            String colorLine = 
                    String.format("color [%s, %s, %s];%n", 
                            color.getRed(), color.getGreen(), color.getBlue());
            printStream.append(selectLine);
            printStream.append(colorLine);
        }
        
    }
    
    public static void printRingColorScript(
            IAtomContainer atomContainer, SignatureRingColorer ringColorer, PrintStream printStream) {
        List<IRing> rings = ringColorer.getRings();
        int ringIndex = 0;
        double radius = 0.9;
        String centerID = "cPoint";
        String centerLine = String.format("draw %s CIRCLE (all) RADIUS 1;%n", centerID);
        printStream.append(centerLine);
        for (IRing ring : rings) {
            Color color = ringColorer.getRingColor(ring);
            List<Integer> ids = new ArrayList<Integer>();
            for (IAtom atom : ring.atoms()) {
                ids.add(atomContainer.getAtomNumber(atom));
            }
            Collections.sort(ids);
            StringBuilder selection = new StringBuilder("({");
            for (int i = 0; i < ids.size(); i++) {
                selection.append(ids.get(i));
                if (i < ids.size() - 1) {
                    selection.append(" ");
                } else {
                    selection.append("})");
                }
            }
            String drawLine = 
                    String.format("draw ring%s CIRCLE %s $cPoint RADIUS %s COLOR [%s, %s, %s];%n",
                            ringIndex, selection, radius, 
                            color.getRed(), color.getGreen(), color.getBlue());
            printStream.append(drawLine);
            ringIndex++;
        }
        printStream.append(String.format("draw %s OFF;%n", centerID));
        printStream.append(String.format("center $%s;%n", centerID));
        
    }

}
