package memory;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

public class SharedMatrixTest {

  double[][] dummyMat = { { 1, 2, 3 }, { 4, 5, 6 } };

  @Test
  public void testEmptyMatrixConstructor() {
    SharedMatrix mat = new SharedMatrix();
    assertEquals(mat.length(), 0);
    assertEquals(mat.getOrientation(), VectorOrientation.ROW_MAJOR);
  }

  @Test
  public void testMatrixConstructor() {
    SharedMatrix mat = new SharedMatrix(dummyMat);
    assertEquals(mat.length(), 2);
    assertEquals(mat.get(0).length(), 3);
    assertEquals(mat.get(1).length(), 3);
    assertMatrixEquals(dummyMat, mat.readRowMajor());
  }

  @Test
  public void testMatrixConstructorExceptionNullInput() {
    Exception e = assertThrows(IllegalArgumentException.class, () -> {
      SharedMatrix m = new SharedMatrix(null);
    });
    String expectedMsg = "input matrix is null";
    String actualMsg = e.getMessage();
    assertTrue(actualMsg.equals(expectedMsg));
  }

  @Test
  public void testLoadRowMajor() {
    SharedMatrix m = new SharedMatrix();
    m.loadRowMajor(dummyMat);
    assertEquals(m.length(), 2);
    assertEquals(m.get(0).length(), 3);
    assertEquals(m.get(1).length(), 3);
    assertEquals(m.getOrientation(), VectorOrientation.ROW_MAJOR);
    assertMatrixEquals(dummyMat, m.readRowMajor());
  }

  @Test
  public void testLoadRowMajorOverridingExistingInfo() {
    SharedMatrix m = new SharedMatrix(new double[][] { { 1 } });
    m.loadRowMajor(dummyMat);
    assertEquals(m.length(), 2);
    assertEquals(m.get(0).length(), 3);
    assertEquals(m.get(1).length(), 3);
    assertEquals(m.getOrientation(), VectorOrientation.ROW_MAJOR);
    assertMatrixEquals(dummyMat, m.readRowMajor());
  }

  @Test
  public void testLoadRowMajorExceptionNullInput() {
    Exception e = assertThrows(IllegalArgumentException.class, () -> {
      SharedMatrix m = new SharedMatrix();
      m.loadRowMajor(null);
    });
    String expectedMsg = "input matrix is null";
    String actualMsg = e.getMessage();
    assertTrue(actualMsg.equals(expectedMsg));
  }

  @Test
  public void testLoadColumnMajor() {
    SharedMatrix m = new SharedMatrix();
    m.loadColumnMajor(dummyMat);
    assertEquals(m.length(), 2);
    assertEquals(m.get(0).length(), 3);
    assertEquals(m.get(0).getOrientation(), VectorOrientation.COLUMN_MAJOR);
    assertEquals(m.get(1).length(), 3);
    assertEquals(m.get(1).getOrientation(), VectorOrientation.COLUMN_MAJOR);
    double[][] expected = {
        {1,4},
        {2,5},
        {3,6}
    };
    assertMatrixEquals(expected, m.readRowMajor());
  }

  @Test
  public void testLoadColumnMajorOverridingExistingInfo() {
    SharedMatrix m = new SharedMatrix(new double[][] { { 1 } });
    m.loadColumnMajor(dummyMat);
    assertEquals(m.length(), 2);
    assertEquals(m.get(0).length(), 3);
    assertEquals(m.get(0).getOrientation(), VectorOrientation.COLUMN_MAJOR);
    assertEquals(m.get(1).length(), 3);
    assertEquals(m.get(1).getOrientation(), VectorOrientation.COLUMN_MAJOR);
    double[][] expected = {
        {1,4},
        {2,5},
        {3,6}
    };
    assertMatrixEquals(expected, m.readRowMajor());
  }

  @Test
  public void testLoadColumnMajorExceptionNullInput() {
    Exception e = assertThrows(IllegalArgumentException.class, () -> {
      SharedMatrix m = new SharedMatrix();
      m.loadColumnMajor(null);
    });
    String expectedMsg = "input matrix is null";
    String actualMsg = e.getMessage();
    assertTrue(actualMsg.equals(expectedMsg));
  }

  @Test
  public void testReadRowMajorFromRowMajor() {
    SharedMatrix m = new SharedMatrix();
    m.loadRowMajor(dummyMat);
    double[][] read = m.readRowMajor();
    assertEquals(read.length, 2);
    assertEquals(read[0].length, 3);
    assertEquals(read[1].length, 3);
    assertMatrixEquals(dummyMat, read);
  }

  @Test
  public void testReadRowMajorFromColumnMajor() {
    SharedMatrix m = new SharedMatrix();
    m.loadColumnMajor(dummyMat);
    double[][] read = m.readRowMajor();
    assertEquals(read.length, 3);
    assertEquals(read[0].length, 2);
    assertEquals(read[1].length, 2);
    assertEquals(read[2].length, 2);
    double[][] expected = {
        {1,4},
        {2,5},
        {3,6}
    };
    assertMatrixEquals(expected, read);
  }

  @Test
  public void testReadRowMajorFromEmptyMatrix() {
    SharedMatrix m = new SharedMatrix();
    double[][] read = m.readRowMajor();
    assertEquals(read.length, 0);
  }

  @Test
  public void testGetExceptionNegativeIndex() {
    SharedMatrix matrix = new SharedMatrix(dummyMat);
    Exception e = assertThrows(IllegalArgumentException.class, () -> {
      SharedVector v = matrix.get(-1);
    });
    String expectedMsg = "index out of bounds";
    String actualMsg = e.getMessage();
    assertTrue(actualMsg.equals(expectedMsg));
  }

  @Test
  public void testGetExceptionTooBigIndex() {
    SharedMatrix matrix = new SharedMatrix(dummyMat);
    Exception e = assertThrows(IllegalArgumentException.class, () -> {
      SharedVector v = matrix.get(10);
    });
    String expectedMsg = "index out of bounds";
    String actualMsg = e.getMessage();
    assertTrue(actualMsg.equals(expectedMsg));
  }

  @Test
  public void testGetOrientationRow() {
    SharedMatrix m = new SharedMatrix();
    m.loadRowMajor(dummyMat);
    assertEquals(m.getOrientation(), VectorOrientation.ROW_MAJOR);
  }

  @Test
  public void testGetOrientationRowForEmptyMatrix() {
    SharedMatrix m = new SharedMatrix();
    assertEquals(m.getOrientation(), VectorOrientation.ROW_MAJOR);
  }

  @Test
  public void testGetOrientationColumn() {
    SharedMatrix m = new SharedMatrix();
    m.loadColumnMajor(dummyMat);
    assertEquals(m.getOrientation(), VectorOrientation.COLUMN_MAJOR);
  }

  @Test
  public void testLoadRowMajorEmptyMatrix() {
    SharedMatrix m = new SharedMatrix();
    m.loadRowMajor(new double[0][0]);
    assertEquals(0, m.length());
    assertEquals(VectorOrientation.ROW_MAJOR, m.getOrientation());
    assertEquals(0, m.readRowMajor().length);
  }

  @Test
  public void testLoadColumnThenRowResetsOrientation() {
    SharedMatrix m = new SharedMatrix();
    m.loadColumnMajor(dummyMat);
    m.loadRowMajor(dummyMat);
    assertEquals(VectorOrientation.ROW_MAJOR, m.getOrientation());
    assertMatrixEquals(dummyMat, m.readRowMajor());
  }

  @Test
  public void testReadRowMajorReturnsCopy() {
    SharedMatrix m = new SharedMatrix(dummyMat);
    double[][] read = m.readRowMajor();
    read[0][0] = 999;
    assertEquals(1, m.get(0).get(0)); // internal data unchanged
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
