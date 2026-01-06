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
  public void testTransposeTogglesOrientation() {
    SharedVector v = new SharedVector(baseVector, rowOrientation);
    v.transpose();
    assertEquals(v.getOrientation(), VectorOrientation.COLUMN_MAJOR);
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
