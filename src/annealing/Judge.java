package annealing;

import org.openscience.cdk.interfaces.IAtomContainer;


public interface Judge {

	public double score(IAtomContainer other);

}