package spl.lae;

import parser.*;
import memory.*;
import scheduling.*;

import java.util.List;

public class LinearAlgebraEngine {

    private SharedMatrix leftMatrix = new SharedMatrix();
    private SharedMatrix rightMatrix = new SharedMatrix();
    private TiredExecutor executor;

    public LinearAlgebraEngine(int numThreads) {
        // TODO: create executor with given thread count
        executor = new TiredExecutor(numThreads);
    }

    public ComputationNode run(ComputationNode computationRoot) {
        // TODO: resolve computation tree step by step until final matrix is produced
        try {
            computationRoot.associativeNesting();
            while(computationRoot.getNodeType() != ComputationNodeType.MATRIX) {
                ComputationNode toResolve = computationRoot.findResolvable();
                loadAndCompute(toResolve);
            }
            return computationRoot;
        }
        finally {
            try {
                executor.shutdown();
            }
            catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }

    public void loadAndCompute(ComputationNode node) {
        // TODO: load operand matrices
        // TODO: create compute tasks & submit tasks to executor
        ComputationNodeType check = node.getNodeType();
        List<ComputationNode> children = node.getChildren();
        validateAmountOfVars(check, children);
        switch (check) {
            case MULTIPLY:
                double[][] left = children.get(0).getMatrix();
                double[][] right = children.get(1).getMatrix();
                validateMultiply(left, right);
                leftMatrix.loadRowMajor(left);
                double[][] rightAsColumns = transpose(right);
                rightMatrix.loadColumnMajor(rightAsColumns);
                executor.submitAll(createMultiplyTasks());
                node.resolve(leftMatrix.readRowMajor());
                break;
            case ADD:
                double[][] addLeft = children.get(0).getMatrix();
                double[][] addRight = children.get(1).getMatrix();
                validateAdd(addLeft, addRight);
                leftMatrix.loadRowMajor(addLeft);
                rightMatrix.loadRowMajor(addRight);
                executor.submitAll(createAddTasks());
                node.resolve(leftMatrix.readRowMajor());
                break;
            case NEGATE:
                leftMatrix.loadRowMajor(children.get(0).getMatrix());
                executor.submitAll(createNegateTasks());
                node.resolve(leftMatrix.readRowMajor());
                break;
            case TRANSPOSE:
                leftMatrix.loadRowMajor(children.get(0).getMatrix());
                executor.submitAll(createTransposeTasks());
                node.resolve(leftMatrix.readRowMajor());
                break;
            default:
                break;
        }
    }

    public List<Runnable> createAddTasks() {
        // TODO: return tasks that perform row-wise addition
        int matrixLength = leftMatrix.length();
        List<Runnable> addTasks = new java.util.LinkedList<>();
        //check later if is okay to import linkedlist. - OKAY
        for (int i = 0; i < matrixLength; i++) {
            int rowNum = i;
            Runnable task = () -> {
                SharedVector row1 = leftMatrix.get(rowNum);
                SharedVector row2 = rightMatrix.get(rowNum);
                row1.add(row2); 
            };
            addTasks.add(task);
        }
        return addTasks;
    }

    public List<Runnable> createMultiplyTasks() {
        // TODO: return tasks that perform row × matrix multiplication
        int matrixLength = leftMatrix.length();
        List<Runnable> multTasks = new java.util.LinkedList<>();
        for (int i = 0; i < matrixLength; i++) {
            int rowNum = i;
            Runnable task = () -> {
                SharedVector row1 = leftMatrix.get(rowNum);
                row1.vecMatMul(rightMatrix); 
            };
            multTasks.add(task);
        }
        return multTasks;
    }

    public List<Runnable> createNegateTasks() {
        // TODO: return tasks that negate rows
        int matrixLength = leftMatrix.length();
        List<Runnable> negateTasks = new java.util.LinkedList<>();
        for (int i = 0; i < matrixLength; i++) {
            int rowNum = i;
            Runnable task = () -> {
                SharedVector row = leftMatrix.get(rowNum);
                row.negate();
            };
            negateTasks.add(task);
        }
        return negateTasks;
    }

    public List<Runnable> createTransposeTasks() {
        // TODO: return tasks that transpose rows
        int matrixLength = leftMatrix.length();
        List<Runnable> transposeTasks = new java.util.LinkedList<>();
        for (int i = 0; i < matrixLength; i++) {
            int rowNum = i;
            Runnable task = () -> {
                SharedVector row = leftMatrix.get(rowNum);
                row.transpose();
            };
            transposeTasks.add(task);
        }
        return transposeTasks;
    }

    public String getWorkerReport() {
        // TODO: return summary of worker activity
        return executor.getWorkerReport();
    }

    //Aid function - we added this.
    private double[][] transpose(double[][] mat) {
        if (mat == null || mat.length == 0) {
            return new double[0][0];
        }
        int rows = mat.length;
        int cols = mat[0].length;
        double[][] res = new double[cols][rows];
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                res[j][i] = mat[i][j];
            }
        }
        return res;
    }

    //Aid function - we added this.
    private void validateAmountOfVars(ComputationNodeType type, List<ComputationNode> children) {
        int arity = children == null ? 0 : children.size();
        switch (type) {
            case NEGATE:
            case TRANSPOSE:
                if (arity != 1) {
                    throw new IllegalArgumentException("wrong amount of variables");
                }
                break;
            case ADD:
            case MULTIPLY:
                if (arity < 2) {
                    throw new IllegalArgumentException("wrong amount of variables");
                }
                break;
            default:
                break;
        }
    }

    //Aid function - we added this.
    private void validateAdd(double[][] left, double[][] right) {
        if (left == null || right == null) {
            throw new IllegalArgumentException("dimensions mismatch");
        }
        if (left.length != right.length) {
            throw new IllegalArgumentException("dimensions mismatch");
        }
        if (left.length == 0) {
            return;
        }
        if (left[0].length != right[0].length) {
            throw new IllegalArgumentException("dimensions mismatch");
        }
    }

    //Aid function - we added this.
    private void validateMultiply(double[][] left, double[][] right) {
        if (left == null || right == null) {
            throw new IllegalArgumentException("dimensions mismatch");
        }
        if (left.length == 0 || right.length == 0) {
            throw new IllegalArgumentException("dimensions mismatch");
        }
        int leftCols = left[0].length;
        int rightRows = right.length;
        if (leftCols != rightRows) {
            throw new IllegalArgumentException("dimensions mismatch");
        }
    }
}
