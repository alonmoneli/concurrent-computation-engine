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

    public double[] clone() {
        // TODO: return copy of vector
        double[] clone = new double[this.vector.length];
        for (int i = 0; i < this.vector.length; i++) 
            clone[i] = this.vector[i];
        return clone;
    }

    public void transpose() {
        // TODO: transpose vector
        if (this.orientation == VectorOrientation.ROW_MAJOR)
            this.orientation = VectorOrientation.COLUMN_MAJOR;
        else
            this.orientation = VectorOrientation.ROW_MAJOR;
    }

    public void add(SharedVector other) {
        // TODO: add two vectors
        writeLock();
        other.readLock();
        try {
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
            for (int i = 0; i < this.vector.length; i++) 
                this.vector[i] *= -1;
        }
        finally {
            writeUnlock();
        }
    }

    public double dot(SharedVector other) {
        // TODO: compute dot product (row · column)
        this.readLock();
        other.readLock();
        try {
            if (this.vector.length != other.vector.length) 
                return 0;
            double sum = 0;
            for (int i = 0; i < this.vector.length; i++) 
                sum += this.vector[i] * other.vector[i];
            return sum;
        }
        finally {
            this.readUnlock();
            other.readUnlock();
        }
        //Check later if deadlock is relevant. if not, ignore, else. repair.
    }

    public void vecMatMul(SharedMatrix matrix) {
        // TODO: compute row-vector × matrix
        if (matrix == null) return;
        writeLock();
        try {
            int matLen = matrix.length();
            if (matLen == 0) {
                this.vector = new double[0];
                this.orientation = VectorOrientation.ROW_MAJOR;
                return;
            }
            VectorOrientation matOrient = matrix.getOrientation();
            double[] result;
            if (matOrient == VectorOrientation.COLUMN_MAJOR) {
                result = new double[matLen];
                for (int i = 0; i < matLen; i++) {
                    SharedVector col = matrix.get(i);
                    result[i] = this.dot(col);
                }
            } 
            else {
                if (this.vector.length != matLen) return;

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
                        } finally {
                            row.readUnlock();
                        }
                    }
                }
            }
            this.vector = result;
            this.orientation = VectorOrientation.ROW_MAJOR;
        } 
        finally {
            writeUnlock();
        }
        //You HAVE to go through this again later on.
    }
}
