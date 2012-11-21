package test;

import java.util.HashMap;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;

import org.openscience.cdk.group.Partition;

/**
 * Utility class to convert from an array of equivalence classes stored as int
 * to a {@link org.openscience.cdk.group.Partition}. For example, the array
 * [1, 1, 2, 3, 1] would be converted to the partition [0,1,4|2|3] using the
 * indices of the array and the 3 classes. Note that classes can also start 
 * from 0 or any other number, if necessary. 
 * 
 * @author maclean
 *
 */
public class ArrayToPartition {
	
	/**
	 * Convert an array of equivalence classes into a partition.
	 * 
	 * @param equivalences an array of numbers
	 * @return a partition of the elements
	 */
	public static Partition convert(int[] equivalences) {
		return ArrayToPartition.convert(equivalences, 0);
	}
	
	/**
	 * Convert an array of equivalence classes into a partition, starting from
	 * the index <code>start</code>. The indices are re-numbered from start.
	 * 
	 * @param equivalences an array of numbers
	 * @param start the starting point in the array
	 * @return a partition of the elements
	 */
	public static Partition convert(int[] equivalences, int start) {
		Map<Integer, SortedSet<Integer>> cellMap = 
				new HashMap<Integer, SortedSet<Integer>>();
		for (int index = start; index < equivalences.length; index++) {
			int shiftedIndex = index - start;
			int classNum = equivalences[index];
			SortedSet<Integer> cell;
			if (cellMap.containsKey(classNum)) {
				cell = cellMap.get(classNum);
			} else {
				cell = new TreeSet<Integer>();
				cellMap.put(classNum, cell);
			}
			cell.add(shiftedIndex);
		}
		
		
		Partition partition = new Partition();
		for (int classNum : cellMap.keySet()) {
			partition.addCell(cellMap.get(classNum));
		}
		partition.order();
		
		return partition;
	}
}
