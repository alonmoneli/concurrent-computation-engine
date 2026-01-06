package memory;

public class SharedMatrix {

    private volatile SharedVector[] vectors = {}; // underlying vectors

    public SharedMatrix() {
        // TODO: initialize empty matrix
        this.vectors = new SharedVector[0];
    }

    public SharedMatrix(double[][] matrix) {
        // TODO: construct matrix as row-major SharedVectors
        if (matrix == null) {
            throw new IllegalArgumentException("input matrix is null");
        }
        if (matrix.length == 0) {
            this.vectors = new SharedVector[0];
            return;
        }
        this.vectors = new SharedVector[matrix.length];
        for(int i = 0; i < matrix.length; i++){
            SharedVector rowVector = new SharedVector(matrix[i], VectorOrientation.ROW_MAJOR);
            this.vectors[i] = rowVector;
        }
    }

    public void loadRowMajor(double[][] matrix) {
        // TODO: replace internal data with new row-major matrix
        if (matrix == null) {
            throw new IllegalArgumentException("input matrix is null");
        }
        if (matrix.length == 0) {
            this.vectors = new SharedVector[0];
            return;
        }
        this.vectors = new SharedVector[matrix.length];
        for(int i = 0; i < matrix.length; i++){
            SharedVector rowVector = new SharedVector(matrix[i], VectorOrientation.ROW_MAJOR);
            this.vectors[i] = rowVector;
        }
        //Note for later to check - why didnt we lock here? - Answer for self - because vectors is volatile.
    }

    public void loadColumnMajor(double[][] matrix) {
        // TODO: replace internal data with new column-major matrix
        if (matrix == null) {
            throw new IllegalArgumentException("input matrix is null");
        }
        if (matrix.length == 0) {
            this.vectors = new SharedVector[0];
            return;
        }
        this.vectors = new SharedVector[matrix.length];
        for(int i = 0; i < matrix.length; i++){
            SharedVector columnVector = new SharedVector(matrix[i], VectorOrientation.COLUMN_MAJOR);
            this.vectors[i] = columnVector;
        }
        //Note for later to check - why didnt we lock here? - because vectors is volatile, again...
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
                for (int row = 0; row < numVectors; row++) {
                    for (int col = 0; col < vectorSize; col++) {
                        result[row][col] = this.vectors[row].get(col);
                    }
                }
                return result;
            } 
            else {
                double[][] result = new double[vectorSize][numVectors];
                for (int col = 0; col < numVectors; col++) {
                    for (int row = 0; row < vectorSize; row++) { 
                        result[row][col] = vectors[col].get(row);
                    }
                }
                return result;
            }
        } 
        finally {
            releaseAllVectorReadLocks(vectors);
        }
        //I need to check if I really need to clone a vector or not. - Changed in skeleton - no need.
        // Can I add getData for quicker Implemenation? - Not needen eventually
    }

    public SharedVector get(int index) {
        // TODO: return vector at index
        if (index < 0 || index >= this.vectors.length) {
            throw new IllegalArgumentException("index out of bounds");
        }
        return this.vectors[index];
    }

    public int length() {
        // TODO: return number of stored vectors
        return this.vectors.length;
    }

    public VectorOrientation getOrientation() {
        // TODO: return orientation
        if (this.vectors.length == 0)
            return VectorOrientation.ROW_MAJOR;
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
