package jmol;

import java.awt.Color;
import java.io.PrintStream;

import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.renderer.color.IAtomColorer;

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

}
