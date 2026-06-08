package org.graphicsEditor.transformer;

import org.graphicsEditor.shapes.GShape;

import java.awt.Point;
import java.awt.Rectangle;

public class GRotator extends GTransformer {
    private double previousAngle;
    private Point center;

    public GRotator(GShape shape) {
        super(shape);
    }

    @Override
    public void start(int x, int y) {
        Rectangle bounds = shape.getBounds();

        center = new Point(
                bounds.x + bounds.width / 2,
                bounds.y + bounds.height / 2
        );

        previousAngle = Math.atan2(y - center.y, x - center.x);
    }

    @Override
    public void keep(int x, int y) {
        double currentAngle = Math.atan2(y - center.y, x - center.x);
        double dAngle = currentAngle - previousAngle;

        shape.rotate(dAngle, center.x, center.y);

        previousAngle = currentAngle;
    }

    @Override
    public void finish(int x, int y) {
    }
}