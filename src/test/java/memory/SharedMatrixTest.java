package memory;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

public class SharedMatrixTest {

  double[][] primaryMatrix = { { 2, 4, 6 }, { 1, 3, 5 } };

  @Test
  public void testEmptyMatrixHasZeroLength() {
    SharedMatrix mat = new SharedMatrix();
    assertEquals(mat.length(), 0);
    assertEquals(mat.getOrientation(), VectorOrientation.ROW_MAJOR);
  }

  @Test
  public void testMatrixConstructorLoadsRows() {
    SharedMatrix matrix = new SharedMatrix(primaryMatrix);
    assertEquals(matrix.length(), 2);
    assertEquals(matrix.get(0).length(), 3);
    assertEquals(matrix.get(1).length(), 3);
    assertMatrixEquals(primaryMatrix, matrix.readRowMajor());
  }

  @Test
  public void testConstructorRejectsNullInput() {
    Exception e = assertThrows(IllegalArgumentException.class, () -> {
      SharedMatrix m = new SharedMatrix(null);
    });
    String expectedMsg = "input matrix is null";
    String actualMsg = e.getMessage();
    assertTrue(actualMsg.equals(expectedMsg));
  }

  @Test
  public void testLoadRowMajorStoresData() {
    SharedMatrix matrix = new SharedMatrix();
    matrix.loadRowMajor(primaryMatrix);
    assertEquals(matrix.length(), 2);
    assertEquals(matrix.get(0).length(), 3);
    assertEquals(matrix.get(1).length(), 3);
    assertEquals(matrix.getOrientation(), VectorOrientation.ROW_MAJOR);
    assertMatrixEquals(primaryMatrix, matrix.readRowMajor());
  }

  @Test
  public void testLoadRowMajorReplacesOldData() {
    SharedMatrix matrix = new SharedMatrix(new double[][] { { 1 } });
    matrix.loadRowMajor(primaryMatrix);
    assertEquals(matrix.length(), 2);
    assertEquals(matrix.get(0).length(), 3);
    assertEquals(matrix.get(1).length(), 3);
    assertEquals(matrix.getOrientation(), VectorOrientation.ROW_MAJOR);
    assertMatrixEquals(primaryMatrix, matrix.readRowMajor());
  }

  @Test
  public void testLoadRowMajorRejectsNull() {
    Exception e = assertThrows(IllegalArgumentException.class, () -> {
      SharedMatrix m = new SharedMatrix();
      m.loadRowMajor(null);
    });
    String expectedMsg = "input matrix is null";
    String actualMsg = e.getMessage();
    assertTrue(actualMsg.equals(expectedMsg));
  }

  @Test
  public void testLoadColumnMajorStoresAsColumns() {
    SharedMatrix matrix = new SharedMatrix();
    matrix.loadColumnMajor(primaryMatrix);
    assertEquals(matrix.length(), 2);
    assertEquals(matrix.get(0).length(), 3);
    assertEquals(matrix.get(0).getOrientation(), VectorOrientation.COLUMN_MAJOR);
    assertEquals(matrix.get(1).length(), 3);
    assertEquals(matrix.get(1).getOrientation(), VectorOrientation.COLUMN_MAJOR);
    double[][] expected = {
        {2,1},
        {4,3},
        {6,5}
    };
    assertMatrixEquals(expected, matrix.readRowMajor());
  }

  @Test
  public void testLoadColumnMajorReplacesOldData() {
    SharedMatrix matrix = new SharedMatrix(new double[][] { { 1 } });
    matrix.loadColumnMajor(primaryMatrix);
    assertEquals(matrix.length(), 2);
    assertEquals(matrix.get(0).length(), 3);
    assertEquals(matrix.get(0).getOrientation(), VectorOrientation.COLUMN_MAJOR);
    assertEquals(matrix.get(1).length(), 3);
    assertEquals(matrix.get(1).getOrientation(), VectorOrientation.COLUMN_MAJOR);
    double[][] expected = {
        {2,1},
        {4,3},
        {6,5}
    };
    assertMatrixEquals(expected, matrix.readRowMajor());
  }

  @Test
  public void testLoadColumnMajorRejectsNull() {
    Exception e = assertThrows(IllegalArgumentException.class, () -> {
      SharedMatrix m = new SharedMatrix();
      m.loadColumnMajor(null);
    });
    String expectedMsg = "input matrix is null";
    String actualMsg = e.getMessage();
    assertTrue(actualMsg.equals(expectedMsg));
  }

  @Test
  public void testReadRowMajorFromRowLayout() {
    SharedMatrix matrix = new SharedMatrix();
    matrix.loadRowMajor(primaryMatrix);
    double[][] read = matrix.readRowMajor();
    assertEquals(read.length, 2);
    assertEquals(read[0].length, 3);
    assertEquals(read[1].length, 3);
    assertMatrixEquals(primaryMatrix, read);
  }

  @Test
  public void testReadRowMajorFromColumnLayout() {
    SharedMatrix matrix = new SharedMatrix();
    matrix.loadColumnMajor(primaryMatrix);
    double[][] read = matrix.readRowMajor();
    assertEquals(read.length, 3);
    assertEquals(read[0].length, 2);
    assertEquals(read[1].length, 2);
    assertEquals(read[2].length, 2);
    double[][] expected = {
        {2,1},
        {4,3},
        {6,5}
    };
    assertMatrixEquals(expected, read);
  }

  @Test
  public void testReadRowMajorFromEmptyMatrix() {
    SharedMatrix matrix = new SharedMatrix();
    double[][] read = matrix.readRowMajor();
    assertEquals(read.length, 0);
  }

  @Test
  public void testGetRejectsNegativeIndex() {
    SharedMatrix matrix = new SharedMatrix(primaryMatrix);
    Exception e = assertThrows(IllegalArgumentException.class, () -> {
      SharedVector ignored = matrix.get(-1);
    });
    String expectedMsg = "index out of bounds";
    String actualMsg = e.getMessage();
    assertTrue(actualMsg.equals(expectedMsg));
  }

  @Test
  public void testGetRejectsTooLargeIndex() {
    SharedMatrix matrix = new SharedMatrix(primaryMatrix);
    Exception e = assertThrows(IllegalArgumentException.class, () -> {
      SharedVector ignored = matrix.get(10);
    });
    String expectedMsg = "index out of bounds";
    String actualMsg = e.getMessage();
    assertTrue(actualMsg.equals(expectedMsg));
  }

  @Test
  public void testGetOrientationAfterRowLoad() {
    SharedMatrix matrix = new SharedMatrix();
    matrix.loadRowMajor(primaryMatrix);
    assertEquals(matrix.getOrientation(), VectorOrientation.ROW_MAJOR);
  }

  @Test
  public void testGetOrientationForEmptyMatrix() {
    SharedMatrix matrix = new SharedMatrix();
    assertEquals(matrix.getOrientation(), VectorOrientation.ROW_MAJOR);
  }

  @Test
  public void testGetOrientationAfterColumnLoad() {
    SharedMatrix matrix = new SharedMatrix();
    matrix.loadColumnMajor(primaryMatrix);
    assertEquals(matrix.getOrientation(), VectorOrientation.COLUMN_MAJOR);
  }

  @Test
  public void testLoadRowMajorWithEmptyArray() {
    SharedMatrix matrix = new SharedMatrix();
    matrix.loadRowMajor(new double[0][0]);
    assertEquals(0, matrix.length());
    assertEquals(VectorOrientation.ROW_MAJOR, matrix.getOrientation());
    assertEquals(0, matrix.readRowMajor().length);
  }

  @Test
  public void testLoadColumnThenRowResetsOrientation() {
    SharedMatrix matrix = new SharedMatrix();
    matrix.loadColumnMajor(primaryMatrix);
    matrix.loadRowMajor(primaryMatrix);
    assertEquals(VectorOrientation.ROW_MAJOR, matrix.getOrientation());
    assertMatrixEquals(primaryMatrix, matrix.readRowMajor());
  }

  @Test
  public void testReadRowMajorReturnsCopy() {
    SharedMatrix matrix = new SharedMatrix(primaryMatrix);
    double[][] read = matrix.readRowMajor();
    read[0][0] = 999;
    assertEquals(2, matrix.get(0).get(0)); // internal data unchanged
  }

  private void assertMatrixEquals(double[][] expected, double[][] actual) {
    assertEquals(expected.length, actual.length, "row count mismatch");
    for (int i = 0; i < expected.length; i++) {
      assertEquals(expected[i].length, actual[i].length, "col count mismatch at row " + i);
      for (int j = 0; j < expected[i].length; j++) {
        assertEquals(expected[i][j], actual[i][j], "value mismatch at [" + i + "," + j + "]");
      }
    }
  }
}
