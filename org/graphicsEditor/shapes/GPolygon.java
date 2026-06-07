package org.graphicsEditor.shapes;

import java.awt.*;

public class GPolygon extends GShape {
    private Polygon polygon;

    public GPolygon() {
        this.polygon = new Polygon();
        this.shape = this.polygon;
    }

    @Override
    public void setLocation0(int x, int y) {
        polygon.addPoint(x, y);
        polygon.addPoint(x, y); // 마우스 따라다니는 임시 마지막 점
    }

    @Override
    public void setLocation1(int x, int y) {
        int n = polygon.npoints;

        if (n > 0) {
            polygon.xpoints[n - 1] = x;
            polygon.ypoints[n - 1] = y;
            polygon.invalidate();
        }
    }

    public void addPoint(int x, int y) {
        int n = polygon.npoints;

        if (n > 0) {
            polygon.xpoints[n - 1] = x;
            polygon.ypoints[n - 1] = y;

            polygon.addPoint(x, y); // 다음 임시점
            polygon.invalidate();
        }
    }

    public void finish() {
        if (polygon.npoints > 1) {
            polygon.npoints--;
            polygon.invalidate();
        }
    }

    @Override
    public void translate(int dx, int dy) {
        polygon.translate(dx, dy);
    }

    @Override
    public GShape clone() {
        GPolygon cloned = new GPolygon();
        cloned.polygon = new Polygon(
                this.polygon.xpoints,
                this.polygon.ypoints,
                this.polygon.npoints
        );
        cloned.shape = cloned.polygon;
        return cloned;
    }

}
