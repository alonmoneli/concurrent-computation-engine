package memory;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

public class SharedVectorTest {

    @Test
    public void verifyVectorStateTransformations() {
        double[] initialData = { 2, 4, 6 };
        SharedVector vector = new SharedVector(initialData, VectorOrientation.ROW_MAJOR);

        assertEquals(VectorOrientation.ROW_MAJOR, vector.getOrientation());
        assertEquals(3, vector.length());
        assertEquals(2, vector.get(0));
        assertEquals(6, vector.get(2));

        vector.transpose();
        assertEquals(VectorOrientation.COLUMN_MAJOR, vector.getOrientation());

        vector.negate();
        assertEquals(-2, vector.get(0));
        assertEquals(-4, vector.get(1));
        assertEquals(-6, vector.get(2));

        vector.negate();
        assertEquals(2, vector.get(0));
    }

    @Test
    public void verifyDotProductLogicAndValidation() {
        double[] dataA = { 1, 2, 3 };
        SharedVector v1 = new SharedVector(dataA, VectorOrientation.ROW_MAJOR);
        
        double[] dataB = { 4, 5, 6 };
        SharedVector v2 = new SharedVector(dataB, VectorOrientation.COLUMN_MAJOR);

        double result = v1.dot(v2);
        assertEquals(32, result); 

        assertThrows(IllegalArgumentException.class, () -> v1.dot(null));
    }

    @Test
    public void verifyMatrixMultiplicationFlow() {
        double[] vecData = { 3, -1 }; 
        SharedVector vector = new SharedVector(vecData, VectorOrientation.ROW_MAJOR);

        double[][] matData = { 
            { 10, 20, 30 }, 
            { 5, 5, 5 } 
        };
        SharedMatrix matrix = new SharedMatrix(matData);

        vector.vecMatMul(matrix);

        assertEquals(3, vector.length());
        assertEquals(25, vector.get(0)); 
        assertEquals(55, vector.get(1));
        assertEquals(85, vector.get(2));

        assertThrows(IllegalArgumentException.class, () -> vector.vecMatMul(null));

        SharedMatrix invalidDimMatrix = new SharedMatrix(new double[][] { { 1 }, { 1 } }); // only 2 rows, vector length is 3
        assertThrows(IllegalArgumentException.class, () -> vector.vecMatMul(invalidDimMatrix));
    }
}
