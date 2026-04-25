package org.example.common.models;

import java.io.Serial;
import java.io.Serializable;
import java.util.Objects;

public class Coordinates implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;
    private double x;
    private long y;

    public Coordinates() {}

    public Coordinates(double x, long y){
        this.x = x;
        this.y = y;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Coordinates that = (Coordinates) o;
        return Double.compare(x, that.x) == 0 && y == that.y;
    }

    @Override
    public int hashCode() {
        return Objects.hash(x, y);
    }

    @Override
    public String toString() {
        return "Coordinates{" +
                "x=" + x +
                ", y=" + y +
                '}';
    }

    public double getX() {
        return x;
    }

    public long getY() {
        return y;
    }

    public void setX(double x) {
        this.x = x;
    }

    public void setY(long y) {
        this.y = y;
    }
}

