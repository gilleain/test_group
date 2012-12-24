package annealing;

import org.openscience.cdk.interfaces.IAtomContainer;


public class Step {

    public final int index;
    public final IAtomContainer mol;
    public final String smiles;
    public final double score;
    public final MoleculeState.Acceptance acceptance;

    public Step(int index, 
            IAtomContainer mol, String smiles, 
            double score, MoleculeState.Acceptance acceptance) {
        this.index = index; 
        this.mol = mol;
        this.smiles = smiles;
        this.score = score;
        this.acceptance = acceptance;
    }

    public String toString() {
        return String.format("%s\t%2.2f\t%s\t%s", 
                this.index, 
                this.score, 
                this.acceptance, 
                this.smiles
        );
    }

}
