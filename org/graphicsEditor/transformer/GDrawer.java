package org.graphicsEditor.transformer;

import org.graphicsEditor.shapes.GShape;

import java.awt.*;

public class GDrawer extends GTransformer{

    public GDrawer(GShape shape) {
        super(shape);
    }
    @Override
    public void start(int x, int y) {
        shape.setLocation0(x, y);
    }

    @Override
    public void keep(int x, int y) {
        shape.setLocation1(x, y);
    }

    @Override
    public void finish(int x, int y) {

    }

    public void cont(int x, int y) {

    }
}
