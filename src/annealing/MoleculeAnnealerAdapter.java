package annealing;

import java.util.ArrayList;

import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.structgen.RandomGenerator;

public class MoleculeAnnealerAdapter implements AnnealerAdapterI {
	
	private Judge judge; 
	
	private final ArrayList<StateListener> stateListeners;
	private RandomGenerator randomGenerator;
	
	private double bestCost;
	private double currentCost;
	private double nextCost;
	
	private IAtomContainer best;
	private IAtomContainer current;
	private IAtomContainer next;
	
	private int stepIndex;

	private int bestStepIndex;
	
	public MoleculeAnnealerAdapter(IAtomContainer startingMolecule, Judge judge) {
		this.judge = judge;
		this.stateListeners = new ArrayList<StateListener>();
		
		this.randomGenerator = new RandomGenerator(startingMolecule);
		
		this.current = startingMolecule;
		this.next = null;
		this.best = current;
		this.bestCost = this.currentCost = this.nextCost = 0.0;
		
		this.stepIndex = 0;
		this.bestStepIndex = 0;
	}

	public IAtomContainer getBest() {
		return this.best;
	}
	
	public int getBestStepIndex() {
		return this.bestStepIndex;
	}
	
	public IAtomContainer getCurrent() {
		return this.current;
	}

	public void addStateListener(StateListener listener) {
		this.stateListeners.add(listener);
	}

	public boolean costDecreasing() {
//		System.out.println("current cost: "+ this.currentCost);
//		System.out.println("previous cost: "+ this.nextCost);
		return this.nextCost < this.currentCost;
//	    return this.nextCost > this.currentCost;
	}

	public double costDifference() {
		return this.currentCost - this.nextCost;
//	    return this.nextCost - this.currentCost;
	}
	
	private double cost(IAtomContainer mol) {
		// the score is in the range [0-1], so the cost must be 1-score.
//		return 1 - (this.judge.score(mol) / 100);
	    return 100 - (this.judge.score(mol));
//	    return this.judge.score(mol);
	}
	
	private boolean currentIsBetterThanBest() {
	    return this.currentCost < this.bestCost;
//	    return this.currentCost > this.bestCost;
	}

	public void initialState() {
		// bit pointless.
		this.current = this.randomGenerator.getMolecule();
		this.currentCost = cost(this.current);
		this.bestCost = this.currentCost;
	}

	public void nextState() {
		this.next = this.randomGenerator.proposeStructure();
		this.nextCost = cost(this.next);
		this.stepIndex++;
	}

	public void accept() {
		this.current = this.next;
		this.currentCost = this.nextCost;
		if (this.currentIsBetterThanBest()) {
//			System.out.println("best > current, storing best=" + this.bestCost + " current=" + this.currentCost);
			this.best = this.current;
			this.bestCost = currentCost;
			this.bestStepIndex = this.stepIndex;
		} else {
//			System.out.println("best !> current, NOT storing best=" + this.bestCost + " current=" + this.currentCost);
		}
		this.randomGenerator.acceptStructure(); 
		
		fireStateEvent(
				new MoleculeState(this.current, MoleculeState.Acceptance.ACCEPT, this.stepIndex));
	}

	public void reject() {
		fireStateEvent(
				new MoleculeState(this.next, MoleculeState.Acceptance.REJECT, this.stepIndex));
	}
	
	private void fireStateEvent(State state) {
		for (StateListener listener : this.stateListeners) {
			listener.stateChanged(state);
		}
	}

}
