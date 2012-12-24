package refiner;

import java.util.List;
import java.util.Map;

public class PathLengthFinder {
    
    public static int[][] paths(Map<Integer, List<Integer>> adjMatrix) {
        int nrow = adjMatrix.size();
        int[][] distMatrix = new int[nrow][nrow];
        for (int i = 0; i < nrow; i++) {
            List<Integer> neighbours = adjMatrix.get(i); 
            for (int j = 0; j < nrow; j++) {
                if (i == j) {
                    distMatrix[i][i] = 0; // no self cycles
                } else {
                    if (neighbours.contains(j)) {
                        distMatrix[i][j] = 1;
                    } else {
                        distMatrix[i][j] = nrow + 1;
                    }
                }
            }
        }
        
        for (int k = 0; k < nrow; k++) {
            for (int i = 0; i < nrow; i++) {
                for (int j = 0; j < nrow; j++) {
                    if (distMatrix[i][k] + distMatrix[k][j] < distMatrix[i][j]) {
                        distMatrix[i][j] = distMatrix[i][k] + distMatrix[k][j];
                    }
                }
            }
        }
        return distMatrix;
    }

}
