package lae;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.List;

import org.junit.jupiter.api.Test;

import parser.ComputationNode;
import spl.lae.LinearAlgebraEngine;

public class LinearAlgebraEngineTest {
  @Test
  public void testCreateAddTasks() {
    LinearAlgebraEngine lae = new LinearAlgebraEngine(10);
    double[][] mat1 = {
        { 1, 2 },
        { 3, 4 }
    };
    double[][] mat2 = {
        { 5, 6 },
        { 7, 8 }
    };

    ComputationNode addNode = new ComputationNode("+", List.of(
        new ComputationNode(mat1),
        new ComputationNode(mat2)));
    ComputationNode resNode = lae.run(addNode);
    double[][] compRes = resNode.getMatrix();
    double[][] expectedRes = {
        { 6, 8 },
        { 10, 12 }
    };
    assertMatrixEquals(expectedRes, compRes);
  }

  @Test
  public void testCreateMultiplyTasks() {
    LinearAlgebraEngine lae = new LinearAlgebraEngine(10);
    double[][] mat1 = {
        { 1, 2 },
        { 3, 4 }
    };
    double[][] mat2 = {
        { 2, 0 },
        { 1, 2 }
    };

    ComputationNode multiplyNode = new ComputationNode("*", List.of(
        new ComputationNode(mat1),
        new ComputationNode(mat2)));
    ComputationNode resNode = lae.run(multiplyNode);
    double[][] compRes = resNode.getMatrix();
    double[][] expectedRes = {
        { 4, 4 },
        { 10, 8 }
    };
    assertMatrixEquals(expectedRes, compRes);
  }

  @Test
  public void testCreateTransposeTasks() {
    LinearAlgebraEngine lae = new LinearAlgebraEngine(10);
    double[][] mat = {
        { 1, 2 },
        { 3, 4 }
    };

    ComputationNode transposeNode = new ComputationNode("T", List.of(new ComputationNode(mat)));
    ComputationNode resNode = lae.run(transposeNode);
    double[][] compRes = resNode.getMatrix();
    double[][] expectedRes = {
        { 1, 3 },
        { 2, 4 }
    };
    assertMatrixEquals(expectedRes, compRes);
  }

  @Test
  public void testCreateNegateTasks() {
    LinearAlgebraEngine lae = new LinearAlgebraEngine(10);
    double[][] mat = {
        { 3, -5 },
        { 8, 17 }
    };

    ComputationNode negateNode = new ComputationNode("-", List.of(new ComputationNode(mat)));
    ComputationNode resNode = lae.run(negateNode);
    double[][] compRes = resNode.getMatrix();
    double[][] expectedRes = {
        { -3, 5 },
        { -8, -17 }
    };
    assertMatrixEquals(expectedRes, compRes);
  }

  @Test
  public void testRunComplexOperation() {
    LinearAlgebraEngine lae = new LinearAlgebraEngine(10);
    double[][] mat1 = {
        { 1, 2, 3 },
        { 4, 5, 6 }
    };

    double[][] mat2 = {
        { 6, 5, 4 },
        { 3, 2, 1 }
    };

    double[][] mat3 = {
        { 1, 0, 2 },
        { -1, 3, 1 }
    };

    double[][] mat4 = {
        { 2, -1 },
        { 0, 4 }
    };

    ComputationNode complexNode = new ComputationNode("+", List.of(
        new ComputationNode("*", List.of(
            new ComputationNode("+", List.of(
                new ComputationNode(mat1),
                new ComputationNode("-", List.of(new ComputationNode(mat2))) 
            )),
            new ComputationNode("T", List.of(new ComputationNode(mat3)))
        )),
        new ComputationNode("-", List.of(new ComputationNode(mat4))) 
    ));
    ComputationNode resNode = lae.run(complexNode);
    double[][] compRes = resNode.getMatrix();
    double[][] expectedRes = {
        { -9, -4 },
        { 11, 9 }
    };
    assertMatrixEquals(expectedRes, compRes);
  }

  @Test
  public void testSingleMatrixPassThrough() {
    LinearAlgebraEngine lae = new LinearAlgebraEngine(4);
    double[][] mat = {
        { 14, 13 },
        { 12, 11 }
    };
    ComputationNode node = new ComputationNode(mat);
    ComputationNode resNode = lae.run(node);
    assertMatrixEquals(mat, resNode.getMatrix());
  }

  @Test
  public void testAddDimensionMismatchThrows() {
    LinearAlgebraEngine lae = new LinearAlgebraEngine(2);
    double[][] mat1 = { { 1, 2 } };
    double[][] mat2 = { { 3, 4 }, { 5, 6 } };
    ComputationNode addNode = new ComputationNode("+", List.of(
        new ComputationNode(mat1),
        new ComputationNode(mat2)));
    assertThrows(IllegalArgumentException.class, () -> lae.run(addNode));
  }

  @Test
  public void testMultiplyDimensionMismatchThrows() {
    LinearAlgebraEngine lae = new LinearAlgebraEngine(2);
    double[][] mat1 = { { 1, 2, 3 }, { 4, 5, 6 } }; // 2x3
    double[][] mat2 = { { 1, 2 }, { 3, 4 } };       // 2x2 -> cols of left (3) != rows of right (2)
    ComputationNode mulNode = new ComputationNode("*", List.of(
        new ComputationNode(mat1),
        new ComputationNode(mat2)));
    assertThrows(IllegalArgumentException.class, () -> lae.run(mulNode));
  }

  @Test
  public void testWrongArityUnaryThrows() {
    LinearAlgebraEngine lae = new LinearAlgebraEngine(2);
    double[][] mat = { { 1, 2 }, { 3, 4 } };
    ComputationNode badNegate = new ComputationNode("-", List.of(
        new ComputationNode(mat),
        new ComputationNode(mat)));
    assertThrows(IllegalArgumentException.class, () -> lae.run(badNegate));
  }

  private void assertMatrixEquals(double[][] expected, double[][] actual) {
    assertEquals(expected.length, actual.length);
    for (int i = 0; i < expected.length; i++) {
      assertEquals(expected[i].length, actual[i].length, "Row " + i + " length mismatch");
      for (int j = 0; j < expected[i].length; j++) {
        assertEquals(expected[i][j], actual[i][j], "Mismatch at [" + i + "," + j + "]");
      }
    }
  }
}
