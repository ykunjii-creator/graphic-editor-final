package org.graphicsEditor.shapes;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.geom.Ellipse2D;

public class GOval extends GShape{

    public GOval() {
        this.shape = new Ellipse2D.Double();
    }

    public void setLocation0(int x, int y) {
        Ellipse2D r = (Ellipse2D) shape;
        r.setFrame(x, y, 0, 0);
    }
    public void setLocation1(int x, int y) {
        Ellipse2D r = (Ellipse2D) shape;
        double w = x-r.getX();
        double h = y-r.getY();
        r.setFrame(r.getX(), r.getY(), w, h);
    }
    public void translate(int dx, int dy) {
        Ellipse2D r = (Ellipse2D) shape;
        r.setFrame(r.getX()+dx, r.getY()+dy, r.getWidth(), r.getHeight());
    }

    @Override
    public void scale(double sx, double sy, int cx, int cy) {
        AffineTransform at = new AffineTransform();
        at.translate(cx, cy);
        at.scale(sx, sy);
        at.translate(-cx, -cy);
        this.shape = at.createTransformedShape(this.shape);
    }
}
