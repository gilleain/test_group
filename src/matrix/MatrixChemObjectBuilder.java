//package matrix;
//
//import org.openscience.cdk.DynamicFactory;
//import org.openscience.cdk.interfaces.IAdductFormula;
//import org.openscience.cdk.interfaces.IAminoAcid;
//import org.openscience.cdk.interfaces.IAtom;
//import org.openscience.cdk.interfaces.IAtomContainer;
//import org.openscience.cdk.interfaces.IAtomContainerSet;
//import org.openscience.cdk.interfaces.IAtomParity;
//import org.openscience.cdk.interfaces.IAtomType;
//import org.openscience.cdk.interfaces.IBioPolymer;
//import org.openscience.cdk.interfaces.IBond;
//import org.openscience.cdk.interfaces.ICDKObject;
//import org.openscience.cdk.interfaces.IChemFile;
//import org.openscience.cdk.interfaces.IChemModel;
//import org.openscience.cdk.interfaces.IChemObject;
//import org.openscience.cdk.interfaces.IChemObjectBuilder;
//import org.openscience.cdk.interfaces.IChemSequence;
//import org.openscience.cdk.interfaces.ICrystal;
//import org.openscience.cdk.interfaces.IDoubleBondStereochemistry;
//import org.openscience.cdk.interfaces.IElectronContainer;
//import org.openscience.cdk.interfaces.IElement;
//import org.openscience.cdk.interfaces.IFragmentAtom;
//import org.openscience.cdk.interfaces.IIsotope;
//import org.openscience.cdk.interfaces.ILonePair;
//import org.openscience.cdk.interfaces.IMapping;
//import org.openscience.cdk.interfaces.IMolecularFormula;
//import org.openscience.cdk.interfaces.IMolecularFormulaSet;
//import org.openscience.cdk.interfaces.IMonomer;
//import org.openscience.cdk.interfaces.IPDBAtom;
//import org.openscience.cdk.interfaces.IPDBMonomer;
//import org.openscience.cdk.interfaces.IPDBPolymer;
//import org.openscience.cdk.interfaces.IPDBStructure;
//import org.openscience.cdk.interfaces.IPolymer;
//import org.openscience.cdk.interfaces.IPseudoAtom;
//import org.openscience.cdk.interfaces.IReaction;
//import org.openscience.cdk.interfaces.IReactionScheme;
//import org.openscience.cdk.interfaces.IReactionSet;
//import org.openscience.cdk.interfaces.IRing;
//import org.openscience.cdk.interfaces.IRingSet;
//import org.openscience.cdk.interfaces.ISingleElectron;
//import org.openscience.cdk.interfaces.IStrand;
//import org.openscience.cdk.interfaces.ITetrahedralChirality;
//import org.openscience.cdk.silent.AdductFormula;
//import org.openscience.cdk.silent.AminoAcid;
//import org.openscience.cdk.silent.Atom;
//import org.openscience.cdk.silent.AtomContainerSet;
//import org.openscience.cdk.silent.AtomParity;
//import org.openscience.cdk.silent.AtomType;
//import org.openscience.cdk.silent.BioPolymer;
//import org.openscience.cdk.silent.Bond;
//import org.openscience.cdk.silent.ChemFile;
//import org.openscience.cdk.silent.ChemModel;
//import org.openscience.cdk.silent.ChemObject;
//import org.openscience.cdk.silent.ChemSequence;
//import org.openscience.cdk.silent.Crystal;
//import org.openscience.cdk.silent.ElectronContainer;
//import org.openscience.cdk.silent.Element;
//import org.openscience.cdk.silent.FragmentAtom;
//import org.openscience.cdk.silent.Isotope;
//import org.openscience.cdk.silent.LonePair;
//import org.openscience.cdk.silent.Mapping;
//import org.openscience.cdk.silent.MolecularFormula;
//import org.openscience.cdk.silent.MolecularFormulaSet;
//import org.openscience.cdk.silent.Monomer;
//import org.openscience.cdk.silent.PDBAtom;
//import org.openscience.cdk.silent.PDBMonomer;
//import org.openscience.cdk.silent.PDBPolymer;
//import org.openscience.cdk.silent.PDBStructure;
//import org.openscience.cdk.silent.Polymer;
//import org.openscience.cdk.silent.PseudoAtom;
//import org.openscience.cdk.silent.Reaction;
//import org.openscience.cdk.silent.ReactionScheme;
//import org.openscience.cdk.silent.ReactionSet;
//import org.openscience.cdk.silent.Ring;
//import org.openscience.cdk.silent.RingSet;
//import org.openscience.cdk.silent.SingleElectron;
//import org.openscience.cdk.silent.Strand;
//import org.openscience.cdk.stereo.DoubleBondStereochemistry;
//import org.openscience.cdk.stereo.TetrahedralChirality;
//
//public class MatrixChemObjectBuilder implements IChemObjectBuilder {
//	
//	private static IChemObjectBuilder instance = null;
//	private final DynamicFactory factory = new DynamicFactory(200);
//
//	public MatrixChemObjectBuilder() {
//		// self reference required for stereo-elements
//		final IChemObjectBuilder self = this;
//
//		// elements
//		factory.register(IAtom.class,         Atom.class);
//		factory.register(IPseudoAtom.class,   PseudoAtom.class);
//		factory.register(IElement.class,      Element.class);
//		factory.register(IAtomType.class,     AtomType.class);
//		factory.register(IFragmentAtom.class, FragmentAtom.class);
//		factory.register(IPDBAtom.class,      PDBAtom.class);
//		factory.register(IIsotope.class,      Isotope.class);
//
//		// electron containers
//		factory.register(IBond.class,              Bond.class);
//		factory.register(IElectronContainer.class, ElectronContainer.class);
//		factory.register(ISingleElectron.class,    SingleElectron.class);
//		factory.register(ILonePair.class,          LonePair.class);
//
//		// atom containers
//		factory.register(IAtomContainer.class, AtomContainer.class);
//		factory.register(IRing.class,          Ring.class);
//		factory.register(ICrystal.class,       Crystal.class);
//		factory.register(IPolymer.class,       Polymer.class);
//		factory.register(IPDBPolymer.class,    PDBPolymer.class);
//		factory.register(IMonomer.class,       Monomer.class);
//		factory.register(IPDBMonomer.class,    PDBMonomer.class);
//		factory.register(IBioPolymer.class,    BioPolymer.class);
//		factory.register(IPDBStructure.class,  PDBStructure.class);
//		factory.register(IAminoAcid.class,     AminoAcid.class);
//		factory.register(IStrand.class,        Strand.class);
//
//		// reactions
//		factory.register(IReaction.class,       Reaction.class);
//		factory.register(IReactionScheme.class, ReactionScheme.class);
//
//		// formula
//		factory.register(IMolecularFormula.class, MolecularFormula.class);
//		factory.register(IAdductFormula.class,    AdductFormula.class);
//
//		// chem object sets
//		factory.register(IAtomContainerSet.class,    AtomContainerSet.class);
//		factory.register(IMolecularFormulaSet.class, MolecularFormulaSet.class);
//		factory.register(IReactionSet.class,         ReactionSet.class);
//		factory.register(IRingSet.class,             RingSet.class);
//		factory.register(IChemModel.class,           ChemModel.class);
//		factory.register(IChemFile.class,            ChemFile.class);
//		factory.register(IChemSequence.class,        ChemSequence.class);
//
//		// stereo components (requires some modification after instantiation)
//		factory.register(IAtomParity.class, AtomParity.class);
//		factory.register(ITetrahedralChirality.class,
//				TetrahedralChirality.class,
//				new DynamicFactory.CreationModifier<TetrahedralChirality>() {
//			@Override
//			public void modify(TetrahedralChirality instance) {
//				instance.setBuilder(self);
//			}
//		});
//		factory.register(IDoubleBondStereochemistry.class,
//				DoubleBondStereochemistry.class,
//				new DynamicFactory.CreationModifier<DoubleBondStereochemistry>() {
//			@Override
//			public void modify(DoubleBondStereochemistry instance) {
//				instance.setBuilder(self);
//			}
//		});
//
//		// miscellaneous
//		factory.register(IMapping.class,    Mapping.class);
//		factory.register(IChemObject.class, ChemObject.class);
//
//	}
//
//	/**
//	 * Access the singleton instance of this MatrixChemObjectBuilder. <p/>
//	 * <pre>{@code
//	 *
//	 * // get the builder instance
//	 * IChemObjectBuilder builder = MatrixChemObjectBuilder.getInstance();
//	 *
//	 * // using the builder...
//	 * // create an IAtom using the default constructor
//	 * IAtom atom = builder.newInstance(IAtom.class);
//	 *
//	 * // create a carbon atom
//	 * IAtom c1 = builder.newInstance(IAtom.class, "C");
//	 * }</pre>
//	 *
//	 * @return a MatrixChemObjectBuilder instance
//	 */
//	public static IChemObjectBuilder getInstance() {
//		if (instance == null) {
//			instance = new MatrixChemObjectBuilder();
//		}
//		return instance;
//	}
//
//	/**
//	 * @inheritDoc
//	 */
//	@Override
//	public <T extends ICDKObject> T newInstance(Class<T> clazz, Object... params) throws IllegalArgumentException {
//		return factory.ofClass(clazz, params);
//	}
//
//
//}
