package hr.algebra.Model;

import java.io.Serializable;

public class MoveLocation implements Serializable {
    private Integer X;
    private Integer Y;

    public MoveLocation(Integer x, Integer y) {
        X = x;
        Y = y;
    }

    public Integer getX() {
        return X;
    }

    public void setX(Integer x) {
        X = x;
    }

    public Integer getY() {
        return Y;
    }

    public void setY(Integer y) {
        Y = y;
    }
}
