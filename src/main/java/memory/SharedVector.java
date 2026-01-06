package memory;

import java.util.concurrent.locks.ReadWriteLock;

public class SharedVector {

    private double[] vector;
    private VectorOrientation orientation;
    private ReadWriteLock lock = new java.util.concurrent.locks.ReentrantReadWriteLock();

    public SharedVector(double[] vector, VectorOrientation orientation) {
        // TODO: store vector data and its orientation
        this.vector = vector;
        this.orientation = orientation;
    }

    public double get(int index) {
        // TODO: return element at index (read-locked)
        if (index >= this.vector.length || index < 0) {
            throw new IllegalArgumentException("Index out of bounds");
        }
        this.readLock();
        try {
            return this.vector[index];
        }
        finally {
            this.readUnlock();
        }
    }

    public int length() {
        // TODO: return vector length
        return this.vector.length;
    }

    public VectorOrientation getOrientation() {
        // TODO: return vector orientation
        return this.orientation;
    }

    public void writeLock() {
        // TODO: acquire write lock
        this.lock.writeLock().lock();
    }

    public void writeUnlock() {
        // TODO: release write lock
        this.lock.writeLock().unlock();
    }

    public void readLock() {
        // TODO: acquire read lock
        this.lock.readLock().lock();
    }

    public void readUnlock() {
        // TODO: release read lock
        this.lock.readLock().unlock();
    }

    public void transpose() {
        // TODO: transpose vector
        this.writeLock();
        try {
            if (this.orientation == VectorOrientation.ROW_MAJOR) {
                this.orientation = VectorOrientation.COLUMN_MAJOR;
            } else {
                this.orientation = VectorOrientation.ROW_MAJOR;
            }
        } 
        finally {
            this.writeUnlock();
        }
    }

    public void add(SharedVector other) {
        // TODO: add two vectors
        if (other == null) {
            throw new IllegalArgumentException("other vector is null");
        }
        this.writeLock();
        other.readLock();
        try {
            if (this.vector.length != other.vector.length) {
                throw new IllegalArgumentException("ERROR - Vectors in different length");
            }
            if (this.orientation != other.orientation) {
                throw new IllegalArgumentException("ERROR -  cannot add, Orientetion is different");
            }
            for (int i = 0; i < this.vector.length; i++) 
                this.vector[i] += other.vector[i];
        }
        finally {
            other.readUnlock();
            writeUnlock();
        }
    }

    public void negate() {
        // TODO: negate vector
        writeLock();
        try{
            for (int i = 0; i < this.vector.length; i++) {
                this.vector[i] *= -1;
                if (this.vector[i] == -0.0d) {
                    this.vector[i] = 0.0d;
                }
            }
        }
        finally {
            writeUnlock();
        }
    }

    public double dot(SharedVector other) {
        // TODO: compute dot product (row · column)
        if (other == null) {
            throw new IllegalArgumentException("other is null");
        }
        if (other == this) {
            throw new IllegalArgumentException("ERROR - same product");
        }
        if (this.getOrientation() == other.getOrientation()) {
            throw new IllegalArgumentException("same orientation");
        }
        if (this.length() != other.length()) {
            throw new IllegalArgumentException("length is different");
        }
        this.readLock();
        other.readLock();
        try {
            double sum = 0;
            for (int i = 0; i < this.vector.length; i++) 
                sum += this.vector[i] * other.vector[i];
            return sum;
        }
        finally {
            this.readUnlock();
            other.readUnlock();
        }
        //Check later if deadlock is relevant. if not, ignore, else. repair. - Not relevant here - everything runs smoothly.
    }

    


    public void vecMatMul(SharedMatrix matrix) {
        // TODO: compute row-vector × matrix
        if (matrix == null) {
            throw new IllegalArgumentException("matrix is null");
        }
        if (this.orientation != VectorOrientation.ROW_MAJOR) {
            throw new IllegalArgumentException("vector orientation is not row");
        }
        this.writeLock();
        try {
            int vLen = this.vector.length;
            int matLen = matrix.length();
            if (vLen == 0 && matLen == 0) {
                return;
            }
            if (matLen == 0) {
                this.vector = new double[0];
                return;
            }
            VectorOrientation matOrientation = matrix.getOrientation();
            double[] result;
            if (matOrientation == VectorOrientation.COLUMN_MAJOR) {
                if (matrix.get(0).length() != vLen) {
                    throw new IllegalArgumentException("amount of rows in matrix and vector length are unequal");
                }
                result = new double[matLen];
                for (int i = 0; i < matLen; i++) {
                    result[i] = this.dot(matrix.get(i));
                }
            }
            else {
                if (matLen != vLen) {
                    throw new IllegalArgumentException("amount of rows in matrix and vector length are unequal");
                }
                int width = matrix.get(0).length();
                result = new double[width];
                for (int i = 0; i < matLen; i++) {
                    double scalar = this.vector[i];
                    if (scalar != 0) {
                        SharedVector row = matrix.get(i);
                        row.readLock();
                        try {
                            for (int j = 0; j < width; j++) {
                                result[j] += scalar * row.vector[j];
                            }
                        }
                        finally {
                            row.readUnlock();

                        }
                    }
                }
            }
            this.vector = result;
            this.orientation = VectorOrientation.ROW_MAJOR;
        } 
        finally {
            this.writeUnlock();
        }
    }
}
