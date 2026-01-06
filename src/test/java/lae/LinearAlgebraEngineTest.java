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
  public void testAddOperationProducesSum() {
    LinearAlgebraEngine engine = new LinearAlgebraEngine(6);
    double[][] addLeft = {
        { 2, -1 },
        { 0, 5 }
    };
    double[][] addRight = {
        { 17, 4 },
        { 7, -2 }
    };
    ComputationNode addNode = new ComputationNode("+", List.of(
        new ComputationNode(addLeft),
        new ComputationNode(addRight)));
    ComputationNode addResultNode = engine.run(addNode);
    double[][] expectedAdd = {
        { 19, 3 },
        { 7, 3 }
    };
    assertMatrixEquals(expectedAdd, addResultNode.getMatrix());
  }

  @Test
  public void testLeafMatrixReturnsItself() {
    LinearAlgebraEngine engine = new LinearAlgebraEngine(4);
    double[][] singleMatrix = {
        { 7, 8 },
        { 9, 10 }
    };
    ComputationNode matrixNode = new ComputationNode(singleMatrix);
    ComputationNode resultNode = engine.run(matrixNode);
    assertMatrixEquals(singleMatrix, resultNode.getMatrix());
  }

  @Test
  public void testMultiplyOperationProducesProduct() {
    LinearAlgebraEngine engine = new LinearAlgebraEngine(6);
    double[][] multiplicandMatrix = {
        { 1, 3 },
        { -2, 4 }
    };
    double[][] multiplierMatrix = {
        { 0, 5 },
        { 2, 1 }
    };

    ComputationNode multiplyNode = new ComputationNode("*", List.of(
        new ComputationNode(multiplicandMatrix),
        new ComputationNode(multiplierMatrix)));
    ComputationNode multiplyResultNode = engine.run(multiplyNode);
    double[][] expectedMultiply = {
        { 6, 8 },
        { 8, -6 }
    };
    assertMatrixEquals(expectedMultiply, multiplyResultNode.getMatrix());
  }

  @Test
  public void testTransposeOperationProducesTranspose() {
    LinearAlgebraEngine engine = new LinearAlgebraEngine(6);
    double[][] matrixToTranspose = {
        { 9, 8, 7 },
        { 0, -1, 2 }
    };

    ComputationNode transposeNode = new ComputationNode("T", List.of(new ComputationNode(matrixToTranspose)));
    ComputationNode transposeResultNode = engine.run(transposeNode);
    double[][] expectedTranspose = {
        { 9, 0 },
        { 8, -1 },
        { 7, 2 }
    };
    assertMatrixEquals(expectedTranspose, transposeResultNode.getMatrix());
  }

  @Test
  public void testNegateOperationFlipsSigns() {
    LinearAlgebraEngine engine = new LinearAlgebraEngine(6);
    double[][] matrixToNegate = {
        { 0, -3 },
        { 4, 0 }
    };

    ComputationNode negateNode = new ComputationNode("-", List.of(new ComputationNode(matrixToNegate)));
    ComputationNode negateResultNode = engine.run(negateNode);
    double[][] expectedNegated = {
        { 0, 3 },
        { -4, 0 }
    };
    assertMatrixEquals(expectedNegated, negateResultNode.getMatrix());
  }

  @Test
  public void testComplexExpressionEvaluatesCorrectly() {
    LinearAlgebraEngine engine = new LinearAlgebraEngine(8);
    double[][] leftOperand = {
        { 2, 1, 0 },
        { -1, 3, 4 }
    };

    double[][] rightOperand = {
        { 1, 0, 5 },
        { 2, -2, 1 }
    };

    double[][] transformOperand = {
        { 0, 2, 1 },
        { 3, -1, 4 }
    };

    double[][] subtractOperand = {
        { 1, 2 },
        { -4, 5 }
    };

    ComputationNode complexNode = new ComputationNode("+", List.of(
        new ComputationNode("*", List.of(
            new ComputationNode("+", List.of(
                new ComputationNode(leftOperand),
                new ComputationNode("-", List.of(new ComputationNode(rightOperand))) 
            )),
            new ComputationNode("T", List.of(new ComputationNode(transformOperand)))
        )),
        new ComputationNode("-", List.of(new ComputationNode(subtractOperand))) 
    ));
    ComputationNode complexResultNode = engine.run(complexNode);
    double[][] expectedComplex = {
        { -4, -20 },
        { 17, -7 }
    };
    assertMatrixEquals(expectedComplex, complexResultNode.getMatrix());
  }

  @Test
  public void testAddRejectsMismatchedShapes() {
    LinearAlgebraEngine engine = new LinearAlgebraEngine(2);
    double[][] shortMatrix = { { 1, 2 } };
    double[][] tallMatrix = { { 3, 4 }, { 5, 6 } };
    ComputationNode badAdd = new ComputationNode("+", List.of(
        new ComputationNode(shortMatrix),
        new ComputationNode(tallMatrix)));
    assertThrows(IllegalArgumentException.class, () -> engine.run(badAdd));
  }

  @Test
  private void assertMatrixEquals(double[][] expected, double[][] actual) {
    assertEquals(expected.length, actual.length);
    for (int i = 0; i < expected.length; i++) {
      assertEquals(expected[i].length, actual[i].length, "Row " + i + " length mismatch");
      for (int j = 0; j < expected[i].length; j++) {
        assertEquals(expected[i][j], actual[i][j], "Mismatch at [" + i + "," + j + "]");
      }
    }
  }

  @Test
  public void testMultiplyRejectsMismatchedShapes() {
    LinearAlgebraEngine engine = new LinearAlgebraEngine(2);
    double[][] matLeft = { { 1, 2, 3 }, { 4, 5, 6 } }; // 2x3
    double[][] matRight = { { 1, 2 }, { 3, 4 } };       // 2x2 -> cols of left (3) != rows of right (2)
    ComputationNode badMultiply = new ComputationNode("*", List.of(
        new ComputationNode(matLeft),
        new ComputationNode(matRight)));
    assertThrows(IllegalArgumentException.class, () -> engine.run(badMultiply));
  }

  @Test
  public void testUnaryOpRejectsExtraOperands() {
    LinearAlgebraEngine engine = new LinearAlgebraEngine(2);
    double[][] matrixForNegate = { { 1, 2 }, { 3, 4 } };
    ComputationNode badNegate = new ComputationNode("-", List.of(
        new ComputationNode(matrixForNegate),
        new ComputationNode(matrixForNegate)));
    assertThrows(IllegalArgumentException.class, () -> engine.run(badNegate));
  }

}
