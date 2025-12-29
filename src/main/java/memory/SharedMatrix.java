package memory;

public class SharedMatrix {

    private volatile SharedVector[] vectors = {}; // underlying vectors

    public SharedMatrix() {
        // TODO: initialize empty matrix
        this.vectors = new SharedVector[0];
    }

    public SharedMatrix(double[][] matrix) {
        // TODO: construct matrix as row-major SharedVectors
        if (matrix == null || matrix.length == 0) 
            return;
        this.vectors = new SharedVector[matrix.length];
        for(int i = 0; i < matrix.length; i++){
            SharedVector rowVector = new SharedVector(matrix[i], VectorOrientation.ROW_MAJOR);
            this.vectors[i] = rowVector;
        }
    }

    public void loadRowMajor(double[][] matrix) {
        // TODO: replace internal data with new row-major matrix
        if (matrix == null || matrix.length == 0) 
            return;
        this.vectors = new SharedVector[matrix.length];
        for(int i = 0; i < matrix.length; i++){
            SharedVector rowVector = new SharedVector(matrix[i], VectorOrientation.ROW_MAJOR);
            this.vectors[i] = rowVector;
        }
        //Note for later to check - why didnt we lock here? - because vectors is volatile.
    }

    public void loadColumnMajor(double[][] matrix) {
        // TODO: replace internal data with new column-major matrix
        if (matrix == null || matrix.length == 0) 
            return;
        this.vectors = new SharedVector[matrix[0].length];
        for(int i = 0; i < matrix[0].length; i++){
            double[] column = new double[matrix.length];
            for(int j = 0; j < column.length; j++){
                column[j] = matrix[j][i];
            }
            SharedVector columnVector = new SharedVector(column, VectorOrientation.COLUMN_MAJOR);
            this.vectors[i] = columnVector;
        }
        //Note for later to check - why didnt we lock here? - because vectors is volatile.

    }

    public double[][] readRowMajor() {
        // TODO: return matrix contents as a row-major double[][]
        if (vectors == null || vectors.length == 0) {
            return new double[0][0];
        }
        acquireAllVectorReadLocks(vectors);
        try {
            VectorOrientation orientation = getOrientation();
            int numVectors = vectors.length;
            int vectorSize = vectors[0].length();
            if (orientation == VectorOrientation.ROW_MAJOR) {
                double[][] result = new double[numVectors][vectorSize];
                for (int i = 0; i < numVectors; i++) 
                    result[i] = vectors[i].clone();
                return result;
            } 
            else {
                double[][] result = new double[vectorSize][numVectors];
                for (int j = 0; j < numVectors; j++) {
                    for (int i = 0; i < vectorSize; i++) { 
                        result[i][j] = vectors[j].get(i);
                    }
                }
                return result;
            }
        } 
        finally {
            releaseAllVectorReadLocks(vectors);
        }
        //I need to check if I really need to clone a vector or not.
        // Can I add getData for quicker Implemenation?
    }

    public SharedVector get(int index) {
        // TODO: return vector at index
        return this.vectors[index];
    }

    public int length() {
        // TODO: return number of stored vectors
        return this.vectors.length;
    }

    public VectorOrientation getOrientation() {
        // TODO: return orientation
        if (this.vectors.length == 0)
            return null;
        return this.vectors[0].getOrientation();
    
    }

    private void acquireAllVectorReadLocks(SharedVector[] vecs) {
        // TODO: acquire read lock for each vector
        for (int i = 0; i < vecs.length; i++) {
            vecs[i].readLock();
        }
    }

    private void releaseAllVectorReadLocks(SharedVector[] vecs) {
        // TODO: release read locks
        for (int i = vecs.length - 1; i >= 0; i--) {
            vecs[i].readUnlock();
        }
    }

    private void acquireAllVectorWriteLocks(SharedVector[] vecs) {
        // TODO: acquire write lock for each vector
        for (int i = 0; i < vecs.length; i++) {
            vecs[i].writeLock();
        }
    }

    private void releaseAllVectorWriteLocks(SharedVector[] vecs) {
        // TODO: release write locks
        for (int i = 0; i < vecs.length; i++) {
            vecs[i].writeUnlock();
        }
    }
}
