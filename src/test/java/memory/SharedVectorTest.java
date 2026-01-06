package memory;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

public class SharedVectorTest {
  double[] dummyVector = { 1, 1, 1 };
  double[][] dummyMat = { { 1, 1, 1 }, { 1, 1, 1 }, { 1, 1, 1 } };
  VectorOrientation rowOrientation = VectorOrientation.ROW_MAJOR;

  @Test
  public void testVectorConstructor() {
    SharedVector v = new SharedVector(dummyVector, rowOrientation);
    assertEquals(v.getOrientation(), VectorOrientation.ROW_MAJOR);
    assertEquals(v.length(), dummyVector.length);
    for (int i = 0; i < dummyVector.length; i++) {
      assertEquals(dummyVector[i], v.get(i));
    }
  }

  @Test
  public void testGet(){
    double[] dummyVec2 = {1,2};
    SharedVector v = new SharedVector(dummyVec2, rowOrientation);
    assertEquals(v.get(0), 1);
    assertEquals(v.get(1), 2);
  }

  @Test
  public void testGetOutOfBoundsIndexTooLarge(){
    SharedVector v = new SharedVector(dummyVector, rowOrientation);
    Exception e = assertThrows(IllegalArgumentException.class, () -> {
      v.get(10);
    });
    String expectedMsg = "Index out of bounds";
    String actualMsg = e.getMessage();
    assertTrue(actualMsg.equals(expectedMsg));
  }

    @Test
  public void testGetOutOfBoundsIndexNegative(){
    SharedVector v = new SharedVector(dummyVector, rowOrientation);
    Exception e = assertThrows(IllegalArgumentException.class, () -> {
      v.get(-1);
    });
    String expectedMsg = "Index out of bounds";
    String actualMsg = e.getMessage();
    assertTrue(actualMsg.equals(expectedMsg));
  }

  @Test
  public void testTranspose() {
    SharedVector v = new SharedVector(dummyVector, rowOrientation);
    v.transpose();
    assertEquals(v.getOrientation(), VectorOrientation.COLUMN_MAJOR);
  }

  @Test
  public void testAdd() {
    SharedVector v1 = new SharedVector(dummyVector, rowOrientation);
    double[] arr = { 2, 2, 2 };
    SharedVector v2 = new SharedVector(arr, rowOrientation);
    v1.add(v2);
    for (int i = 0; i < v1.length(); i++) {
      assertEquals(v1.get(i), 3);
    }
  }

  @Test
  public void testAddDifferentLengthException() {
    SharedVector v1 = new SharedVector(dummyVector, rowOrientation);
    double[] arr = { 2, 2, 2, 2 };
    SharedVector v2 = new SharedVector(arr, rowOrientation);
    Exception e = assertThrows(IllegalArgumentException.class, () -> {
      v1.add(v2);
    });

    String expectedMsg = "ERROR - Vectors in different length";
    String actualMsg = e.getMessage();
    assertTrue(actualMsg.equals(expectedMsg));
  }

  @Test
  public void testAddDifferentOrientationException() {
    SharedVector v1 = new SharedVector(dummyVector, rowOrientation);
    SharedVector v2 = new SharedVector(dummyVector, VectorOrientation.COLUMN_MAJOR);
    Exception e = assertThrows(IllegalArgumentException.class, () -> {
      v1.add(v2);
    });

    String expectedMsg = "ERROR -  cannot add, Orientetion is different";
    String actualMsg = e.getMessage();
    assertTrue(actualMsg.equals(expectedMsg));
  }

  @Test
  public void testAddNullException() {
    SharedVector v1 = new SharedVector(dummyVector, rowOrientation);
    Exception e = assertThrows(IllegalArgumentException.class, () -> {
      v1.add(null);
    });

    String expectedMsg = "other vector is null";
    String actualMsg = e.getMessage();
    assertTrue(actualMsg.equals(expectedMsg));
  }

  @Test
  public void testNegatePositiveToNegative() {
    SharedVector v = new SharedVector(dummyVector, rowOrientation);
    v.negate();
    assertEquals(v.length(), dummyVector.length);
    for (int i = 0; i < v.length(); i++) {
      assertEquals(v.get(i), -1);
    }
  }

  @Test
  public void testNegateNegativeToPositive() {
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
  public void testDot() {
    SharedVector v1 = new SharedVector(dummyVector, rowOrientation);
    double[] dummyVector2 = { 1, 2, 3 };
    SharedVector v2 = new SharedVector(dummyVector2, VectorOrientation.COLUMN_MAJOR);
    assertEquals(v1.dot(v2), 6);
  }

  @Test
  public void testDotReverseOrientationOrderWithNegatives() {
    double[] dummyVectorWithNegs = { -10, -8, 4 };
    SharedVector v1 = new SharedVector(dummyVectorWithNegs, rowOrientation);
    double[] dummyVectorWithNegs2 = { 1, -2, 3 };
    SharedVector v2 = new SharedVector(dummyVectorWithNegs2, VectorOrientation.COLUMN_MAJOR);
    assertEquals(v2.dot(v1), 18);
  }

  @Test
  public void testDotWithSelfException() {
    SharedVector v = new SharedVector(dummyVector, rowOrientation);
    Exception e = assertThrows(IllegalArgumentException.class, () -> {
      v.dot(v);
    });
    String expectedMsg = "ERROR - same product";
    String actualMsg = e.getMessage();

    assertTrue(actualMsg.equals(expectedMsg));
  }

  @Test
  public void testDotWithNullException() {
    SharedVector v = new SharedVector(dummyVector, rowOrientation);
    Exception e = assertThrows(IllegalArgumentException.class, () -> {
      v.dot(null);
    });
    String expectedMsg = "other is null";
    String actualMsg = e.getMessage();

    assertTrue(actualMsg.equals(expectedMsg));
  }

  @Test
  public void testDotWithSameOrientationException() {
    SharedVector v1 = new SharedVector(dummyVector, VectorOrientation.COLUMN_MAJOR);
    double[] dummyVector2 = { 1, 2, 3 };
    SharedVector v2 = new SharedVector(dummyVector2, VectorOrientation.COLUMN_MAJOR);
    Exception e = assertThrows(IllegalArgumentException.class, () -> {
      v1.dot(v2);
    });
    String expectedMsg = "same orientation";
    String actualMsg = e.getMessage();

    assertTrue(actualMsg.equals(expectedMsg));
  }

  @Test
  public void testDotWithDifferentLengthException() {
    SharedVector v1 = new SharedVector(dummyVector, VectorOrientation.ROW_MAJOR);
    double[] dummyVector2 = { 1, 2, 3, 4 };
    SharedVector v2 = new SharedVector(dummyVector2, VectorOrientation.COLUMN_MAJOR);
    Exception e = assertThrows(IllegalArgumentException.class, () -> {
      v1.dot(v2);
    });
    String expectedMsg = "length is different";
    String actualMsg = e.getMessage();

    assertTrue(actualMsg.equals(expectedMsg));
  }

  @Test
  public void testVecMatMulMatrixRowMajor() {
    SharedVector v = new SharedVector(dummyVector, rowOrientation); // 1x3
    double[][] dummyMat = { { 1, 1, 1, 1 }, { 1, -1, 1, -1 }, { -1, 1, -1, 1 } }; // 3x4
    SharedMatrix m = new SharedMatrix(dummyMat);
    v.vecMatMul(m); // should be 1x4 -> {1,1,1,1}
    assertEquals(v.length(), 4);
    for (int i = 0; i < v.length(); i++) {
      assertEquals(v.get(i), 1);
    }
  }

  @Test
  public void testVecMatMulMatrixRowMajorDifferentNumbers() {
    double[] dummyVectorRand = { 4, -5, 15 };
    SharedVector v = new SharedVector(dummyVectorRand, rowOrientation); // 1x3
    double[][] dummyMat = {
        { 1, 8, -7, 7 },
        { -8, 11, 13, -2 },
        { 1, 9, 5, 0 }
    }; // 3x4
    SharedMatrix m = new SharedMatrix(dummyMat);
    v.vecMatMul(m); // should be 1x4
    double[] expectedResult = { 59, 112, -18, 38 };
    assertEquals(v.length(), 4);
    for (int i = 0; i < v.length(); i++) {
      assertEquals(v.get(i), expectedResult[i]);
    }
  }

  @Test
  public void testVecMatMulMatrixColumnMajor() {
    SharedVector v = new SharedVector(dummyVector, rowOrientation); // 1x3
    double[][] dummyMat = { { 1, 1, 1 }, { 1, -1, 1 }, { -1, 1, -1 }, { 1, -1, 1 } }; // 3x4
    SharedMatrix m = new SharedMatrix();
    m.loadColumnMajor(dummyMat);
    v.vecMatMul(m); // should be 1x4
    double[] expectedResult = { 3, 1, -1, 1 };
    assertEquals(v.length(), 4);
    for (int i = 0; i < v.length(); i++) {
      assertEquals(v.get(i), expectedResult[i]);
    }
  }

  @Test
  public void testVecMatMulMatrixColumnMajorDifferentNumbers() {
    double[] dummyVectorRand = { -7, 12, 5 };

    double[][] dummyMatRand = {
        { 3, -2, 10 }, 
        { -15, 4, -6 }, 
        { 8, 11, -1 }, 
        { 0, -13, 7 } 
    };

    SharedVector v = new SharedVector(dummyVectorRand, rowOrientation); // 1x3
    SharedMatrix m = new SharedMatrix();
    m.loadColumnMajor(dummyMatRand);
    v.vecMatMul(m); // should be 1x4
    double[] expectedResult = { 5, 123, 71, -121 };
    assertEquals(v.length(), 4);
    for (int i = 0; i < v.length(); i++) {
      assertEquals(v.get(i), expectedResult[i]);
    }
  }

  @Test
  public void testVecMatMulEmptyVectorEmptyMatrix() {
    double[] empty = {};
    SharedVector emptyVec = new SharedVector(empty, rowOrientation);
    SharedMatrix emptyMat = new SharedMatrix();
    emptyVec.vecMatMul(emptyMat);
    assertEquals(emptyVec.length(), 0);
  }

  @Test
  public void testVecMatMulExceptionNullMatrix() {
    SharedVector v = new SharedVector(dummyVector, rowOrientation);
    Exception e = assertThrows(IllegalArgumentException.class, () -> {
      v.vecMatMul(null);
    });
    String expectedMsg = "matrix is null";
    String actualMsg = e.getMessage();
    assertTrue(actualMsg.equals(expectedMsg));
  }

  @Test
  public void testVecMatMulExceptionVectorColumnOrientation() {
    SharedVector v = new SharedVector(dummyVector, VectorOrientation.COLUMN_MAJOR);
    SharedMatrix m = new SharedMatrix(dummyMat);
    Exception e = assertThrows(IllegalArgumentException.class, () -> {
      v.vecMatMul(m);
    });
    String expectedMsg = "vector orientation is not row";
    String actualMsg = e.getMessage();
    assertTrue(actualMsg.equals(expectedMsg));
  }

  @Test
  public void testVecMatMulExceptionNotSameLengthRowMajorMatrix() {
    SharedVector v = new SharedVector(dummyVector, rowOrientation);
    double[][] dummyMat2 = { { 1, 1, 1 }, { 1, 1, 1 } };
    SharedMatrix m = new SharedMatrix();
    m.loadRowMajor(dummyMat2);
    Exception e = assertThrows(IllegalArgumentException.class, () -> {
      v.vecMatMul(m);
    });
    String expectedMsg = "amount of rows in matrix and vector length are unequal";
    String actualMsg = e.getMessage();
    assertTrue(actualMsg.equals(expectedMsg));
  }

  @Test
  public void testVecMatMulExceptionNotSameLengthColumnMajorMatrix() {
    SharedVector v = new SharedVector(dummyVector, rowOrientation);
    double[][] dummyMat2 = { { 1, 1 }, { 1, 1 }, { 1, 1 } };
    SharedMatrix m = new SharedMatrix();
    m.loadColumnMajor(dummyMat2);
    Exception e = assertThrows(IllegalArgumentException.class, () -> {
      v.vecMatMul(m);
    });
    String expectedMsg = "amount of rows in matrix and vector length are unequal";
    String actualMsg = e.getMessage();
    assertTrue(actualMsg.equals(expectedMsg));
  }
}
