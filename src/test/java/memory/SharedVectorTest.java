package memory;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

public class SharedVectorTest {
  double[] baseVector = { 1, 1, 1 };
  double[][] uniformMatrix = { { 1, 1, 1 }, { 1, 1, 1 }, { 1, 1, 1 } };
  VectorOrientation rowOrientation = VectorOrientation.ROW_MAJOR;

  @Test
  public void testVectorConstructorStoresValues() {
    SharedVector vector = new SharedVector(baseVector, rowOrientation);
    assertEquals(vector.getOrientation(), VectorOrientation.ROW_MAJOR);
    assertEquals(vector.length(), baseVector.length);
    for (int i = 0; i < baseVector.length; i++) {
      assertEquals(baseVector[i], vector.get(i));
    }
  }

  @Test
  public void testGetReturnsCorrectEntries(){
    double[] pairVector = {1,2};
    SharedVector vector = new SharedVector(pairVector, rowOrientation);
    assertEquals(vector.get(0), 1);
    assertEquals(vector.get(1), 2);
  }

  @Test
  public void testGetRejectsTooLargeIndex(){
    SharedVector v = new SharedVector(baseVector, rowOrientation);
    Exception e = assertThrows(IllegalArgumentException.class, () -> {
      v.get(10);
    });
    String expectedMsg = "Index out of bounds";
    String actualMsg = e.getMessage();
    assertTrue(actualMsg.equals(expectedMsg));
  }

    @Test
  public void testGetRejectsNegativeIndex(){
    SharedVector v = new SharedVector(baseVector, rowOrientation);
    Exception e = assertThrows(IllegalArgumentException.class, () -> {
      v.get(-1);
    });
    String expectedMsg = "Index out of bounds";
    String actualMsg = e.getMessage();
    assertTrue(actualMsg.equals(expectedMsg));
  }

  @Test
  public void testTransposeTogglesOrientation() {
    SharedVector v = new SharedVector(baseVector, rowOrientation);
    v.transpose();
    assertEquals(v.getOrientation(), VectorOrientation.COLUMN_MAJOR);
  }

  @Test
  public void testAddAccumulatesComponents() {
    SharedVector lhs = new SharedVector(baseVector, rowOrientation);
    double[] addVector = { 2, 3, 4 };
    SharedVector rhs = new SharedVector(addVector, rowOrientation);
    lhs.add(rhs);
    assertEquals(3, lhs.get(0));
    assertEquals(4, lhs.get(1));
    assertEquals(5, lhs.get(2));
  }

  @Test
  public void testAddRejectsDifferentLength() {
    SharedVector vecA = new SharedVector(baseVector, rowOrientation);
    double[] arr = { 2, 2, 2, 2 };
    SharedVector vecB = new SharedVector(arr, rowOrientation);
    Exception e = assertThrows(IllegalArgumentException.class, () -> {
      vecA.add(vecB);
    });

    String expectedMsg = "ERROR - Vectors in different length";
    String actualMsg = e.getMessage();
    assertTrue(actualMsg.equals(expectedMsg));
  }

  @Test
  public void testAddRejectsDifferentOrientation() {
    SharedVector vecA = new SharedVector(baseVector, rowOrientation);
    SharedVector vecB = new SharedVector(baseVector, VectorOrientation.COLUMN_MAJOR);
    Exception e = assertThrows(IllegalArgumentException.class, () -> {
      vecA.add(vecB);
    });

    String expectedMsg = "ERROR -  cannot add, Orientetion is different";
    String actualMsg = e.getMessage();
    assertTrue(actualMsg.equals(expectedMsg));
  }

  @Test
  public void testAddRejectsNullOperand() {
    SharedVector vecA = new SharedVector(baseVector, rowOrientation);
    Exception e = assertThrows(IllegalArgumentException.class, () -> {
      vecA.add(null);
    });

    String expectedMsg = "other vector is null";
    String actualMsg = e.getMessage();
    assertTrue(actualMsg.equals(expectedMsg));
  }

  @Test
  public void testNegateFlipsPositiveToNegative() {
    SharedVector v = new SharedVector(baseVector, rowOrientation);
    v.negate();
    assertEquals(v.length(), baseVector.length);
    for (int i = 0; i < v.length(); i++) {
      assertEquals(v.get(i), -1);
    }
  }

  @Test
  public void testNegateFlipsNegativeToPositive() {
    double[] negs = { -1, -2 };
    double[] pos = { 1, 2 };
    SharedVector v = new SharedVector(negs, rowOrientation);
    v.negate();
    assertEquals(v.length(), negs.length);
    for (int i = 0; i < v.length(); i++) {
      assertEquals(v.get(i), pos[i]);
    }
  }

  @Test
  public void testDotComputesProduct() {
    SharedVector vecA = new SharedVector(baseVector, rowOrientation);
    double[] baseVector2 = { 1, 2, 3 };
    SharedVector vecB = new SharedVector(baseVector2, VectorOrientation.COLUMN_MAJOR);
    assertEquals(vecA.dot(vecB), 6);
  }

  @Test
  public void testDotWithNegativesRespectsOrientation() {
    double[] baseVectorWithNegs = { -10, -8, 4 };
    SharedVector vecA = new SharedVector(baseVectorWithNegs, rowOrientation);
    double[] baseVectorWithNegs2 = { 1, -2, 3 };
    SharedVector vecB = new SharedVector(baseVectorWithNegs2, VectorOrientation.COLUMN_MAJOR);
    assertEquals(vecB.dot(vecA), 18);
  }

  @Test
  public void testDotRejectsSelfOperand() {
    SharedVector v = new SharedVector(baseVector, rowOrientation);
    Exception e = assertThrows(IllegalArgumentException.class, () -> {
      v.dot(v);
    });
    String expectedMsg = "ERROR - same product";
    String actualMsg = e.getMessage();

    assertTrue(actualMsg.equals(expectedMsg));
  }

  @Test
  public void testDotRejectsNullOperand() {
    SharedVector v = new SharedVector(baseVector, rowOrientation);
    Exception e = assertThrows(IllegalArgumentException.class, () -> {
      v.dot(null);
    });
    String expectedMsg = "other is null";
    String actualMsg = e.getMessage();

    assertTrue(actualMsg.equals(expectedMsg));
  }

  @Test
  public void testDotRejectsSameOrientation() {
    SharedVector vecA = new SharedVector(baseVector, VectorOrientation.COLUMN_MAJOR);
    double[] baseVector2 = { 1, 2, 3 };
    SharedVector vecB = new SharedVector(baseVector2, VectorOrientation.COLUMN_MAJOR);
    Exception e = assertThrows(IllegalArgumentException.class, () -> {
      vecA.dot(vecB);
    });
    String expectedMsg = "same orientation";
    String actualMsg = e.getMessage();

    assertTrue(actualMsg.equals(expectedMsg));
  }

  @Test
  public void testDotRejectsDifferentLength() {
    SharedVector vecA = new SharedVector(baseVector, VectorOrientation.ROW_MAJOR);
    double[] baseVector2 = { 1, 2, 3, 4 };
    SharedVector vecB = new SharedVector(baseVector2, VectorOrientation.COLUMN_MAJOR);
    Exception e = assertThrows(IllegalArgumentException.class, () -> {
      vecA.dot(vecB);
    });
    String expectedMsg = "length is different";
    String actualMsg = e.getMessage();

    assertTrue(actualMsg.equals(expectedMsg));
  }

  @Test
  public void testVecMatMulWithRowMajorMatrix() {
    SharedVector v = new SharedVector(baseVector, rowOrientation); // 1x3
    double[][] uniformMatrix = { { 1, 1, 1, 1 }, { 1, -1, 1, -1 }, { -1, 1, -1, 1 } }; // 3x4
    SharedMatrix m = new SharedMatrix(uniformMatrix);
    v.vecMatMul(m); // should be 1x4 -> {1,1,1,1}
    assertEquals(v.length(), 4);
    for (int i = 0; i < v.length(); i++) {
      assertEquals(v.get(i), 1);
    }
  }

  @Test
  public void testVecMatMulRowMajorWithMixedValues() {
    double[] baseVectorRand = { 4, -5, 15 };
    SharedVector v = new SharedVector(baseVectorRand, rowOrientation); // 1x3
    double[][] uniformMatrix = {
        { 1, 8, -7, 7 },
        { -8, 11, 13, -2 },
        { 1, 9, 5, 0 }
    }; // 3x4
    SharedMatrix m = new SharedMatrix(uniformMatrix);
    v.vecMatMul(m); // should be 1x4
    double[] expectedResult = { 59, 112, -18, 38 };
    assertEquals(v.length(), 4);
    for (int i = 0; i < v.length(); i++) {
      assertEquals(v.get(i), expectedResult[i]);
    }
  }

  @Test
  public void testVecMatMulWithColumnMajorMatrix() {
    SharedVector v = new SharedVector(baseVector, rowOrientation); // 1x3
    double[][] uniformMatrix = { { 1, 1, 1 }, { 1, -1, 1 }, { -1, 1, -1 }, { 1, -1, 1 } }; // 3x4
    SharedMatrix m = new SharedMatrix();
    m.loadColumnMajor(uniformMatrix);
    v.vecMatMul(m); // should be 1x4
    double[] expectedResult = { 3, 1, -1, 1 };
    assertEquals(v.length(), 4);
    for (int i = 0; i < v.length(); i++) {
      assertEquals(v.get(i), expectedResult[i]);
    }
  }

  @Test
  public void testVecMatMulColumnMajorWithMixedValues() {
    double[] baseVectorRand = { -7, 12, 5 };

    double[][] uniformMatrixRand = {
        { 3, -2, 10 }, 
        { -15, 4, -6 }, 
        { 8, 11, -1 }, 
        { 0, -13, 7 } 
    };

    SharedVector v = new SharedVector(baseVectorRand, rowOrientation); // 1x3
    SharedMatrix m = new SharedMatrix();
    m.loadColumnMajor(uniformMatrixRand);
    v.vecMatMul(m); // should be 1x4
    double[] expectedResult = { 5, 123, 71, -121 };
    assertEquals(v.length(), 4);
    for (int i = 0; i < v.length(); i++) {
      assertEquals(v.get(i), expectedResult[i]);
    }
  }

  @Test
  public void testVecMatMulWithEmptyOperands() {
    double[] empty = {};
    SharedVector emptyVec = new SharedVector(empty, rowOrientation);
    SharedMatrix emptyMat = new SharedMatrix();
    emptyVec.vecMatMul(emptyMat);
    assertEquals(emptyVec.length(), 0);
  }

  @Test
  public void testVecMatMulRejectsNullMatrix() {
    SharedVector v = new SharedVector(baseVector, rowOrientation);
    Exception e = assertThrows(IllegalArgumentException.class, () -> {
      v.vecMatMul(null);
    });
    String expectedMsg = "matrix is null";
    String actualMsg = e.getMessage();
    assertTrue(actualMsg.equals(expectedMsg));
  }

  @Test
  public void testVecMatMulRejectsColumnVector() {
    SharedVector v = new SharedVector(baseVector, VectorOrientation.COLUMN_MAJOR);
    SharedMatrix m = new SharedMatrix(uniformMatrix);
    Exception e = assertThrows(IllegalArgumentException.class, () -> {
      v.vecMatMul(m);
    });
    String expectedMsg = "vector orientation is not row";
    String actualMsg = e.getMessage();
    assertTrue(actualMsg.equals(expectedMsg));
  }

  @Test
  public void testVecMatMulRejectsRowMajorLengthMismatch() {
    SharedVector v = new SharedVector(baseVector, rowOrientation);
    double[][] uniformMatrix2 = { { 1, 1, 1 }, { 1, 1, 1 } };
    SharedMatrix m = new SharedMatrix();
    m.loadRowMajor(uniformMatrix2);
    Exception e = assertThrows(IllegalArgumentException.class, () -> {
      v.vecMatMul(m);
    });
    String expectedMsg = "amount of rows in matrix and vector length are unequal";
    String actualMsg = e.getMessage();
    assertTrue(actualMsg.equals(expectedMsg));
  }

  @Test
  public void testVecMatMulRejectsColumnMajorLengthMismatch() {
    SharedVector v = new SharedVector(baseVector, rowOrientation);
    double[][] uniformMatrix2 = { { 1, 1 }, { 1, 1 }, { 1, 1 } };
    SharedMatrix m = new SharedMatrix();
    m.loadColumnMajor(uniformMatrix2);
    Exception e = assertThrows(IllegalArgumentException.class, () -> {
      v.vecMatMul(m);
    });
    String expectedMsg = "amount of rows in matrix and vector length are unequal";
    String actualMsg = e.getMessage();
    assertTrue(actualMsg.equals(expectedMsg));
  }
}
