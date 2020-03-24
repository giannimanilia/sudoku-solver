package com.gmaniliapp.sudokusolver.models;

public class Cell {
    private int value;
    private boolean isHighlighted;

    public Cell(int value, boolean isHighlighted) {
        this.value = value;
        this.isHighlighted = isHighlighted;
    }

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }

    public boolean isHighlighted() {
        return isHighlighted;
    }

    public void setHighlighted(boolean highlighted) {
        isHighlighted = highlighted;
    }
}
