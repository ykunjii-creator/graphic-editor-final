package org.graphicsEditor.transformer;

import org.graphicsEditor.shapes.GShape;

public class GScaler extends GTransformer {
    private int x0, y0;

    public GScaler(GShape shape) {
        super(shape);
    }

    @Override
    public void start(int x, int y) {
        x0=x; y0=y;
    }

    @Override
    public void keep(int x, int y) {
        if (x0 == 0 || y0 == 0) return;  // 0 나누기 방지
        double sx = (double) x / x0;
        double sy = (double) y / y0;
        shape.scale(sx, sy, x0, y0);
        x0=x;
        y0=y;
    }

    @Override
    public void finish(int x, int y) {}
}
