package org.graphicsEditor.transformer;

import org.graphicsEditor.shapes.GShape;

public class GTranslator extends GTransformer{
    private int x0;
    private int y0;

    public GTranslator(GShape shape) {
        super(shape);
    }
    @Override
    public void start(int x, int y) {
        this.x0 = x;
        this.y0 = y;
    }

    @Override
    public void keep(int x, int y) {
        int dx = x-x0;
        int dy = y-y0;
        shape.translate(dx, dy);
        this.x0 = x;
        this.y0 = y;
    }

    @Override
    public void finish(int x, int y) {

    }

    public void cont(int x, int y) {

    }
}
