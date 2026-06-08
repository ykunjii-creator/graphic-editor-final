package org.graphicsEditor.transformer;

import org.graphicsEditor.shapes.GShape;

import java.awt.Rectangle;

public class GScaler extends GTransformer {
    private GShape.EAnchor anchor;

    private int x0, y0;           // 이전 마우스 위치
    private int fixedX, fixedY;   // 고정점

    public GScaler(GShape shape, GShape.EAnchor anchor) {
        super(shape);
        this.anchor = anchor;
    }

    @Override
    public void start(int x, int y) {
        this.x0 = x;
        this.y0 = y;

        Rectangle bounds = shape.getBounds();

        int x1 = bounds.x;
        int y1 = bounds.y;
        int x2 = bounds.x + bounds.width;
        int y2 = bounds.y + bounds.height;
        int mx = bounds.x + bounds.width / 2;
        int my = bounds.y + bounds.height / 2;

        switch (anchor) {
            case eNW:
                fixedX = x2;
                fixedY = y2;
                break;
            case eNN:
                fixedX = mx;
                fixedY = y2;
                break;
            case eNE:
                fixedX = x1;
                fixedY = y2;
                break;
            case eEE:
                fixedX = x1;
                fixedY = my;
                break;
            case eSE:
                fixedX = x1;
                fixedY = y1;
                break;
            case eSS:
                fixedX = mx;
                fixedY = y1;
                break;
            case eSW:
                fixedX = x2;
                fixedY = y1;
                break;
            case eWW:
                fixedX = x2;
                fixedY = my;
                break;
            default:
                fixedX = x1;
                fixedY = y1;
                break;
        }
    }

    @Override
    public void keep(int x, int y) {
        double oldDX = x0 - fixedX;
        double oldDY = y0 - fixedY;

        double newDX = x - fixedX;
        double newDY = y - fixedY;

        double sx = 1.0;
        double sy = 1.0;

        // 좌우 방향으로 크기 조절하는 앵커
        if (anchor == GShape.EAnchor.eNW ||
                anchor == GShape.EAnchor.eNE ||
                anchor == GShape.EAnchor.eEE ||
                anchor == GShape.EAnchor.eSE ||
                anchor == GShape.EAnchor.eSW ||
                anchor == GShape.EAnchor.eWW) {

            if (Math.abs(oldDX) > 1) {
                sx = newDX / oldDX;
            }
        }

        // 상하 방향으로 크기 조절하는 앵커
        if (anchor == GShape.EAnchor.eNW ||
                anchor == GShape.EAnchor.eNN ||
                anchor == GShape.EAnchor.eNE ||
                anchor == GShape.EAnchor.eSE ||
                anchor == GShape.EAnchor.eSS ||
                anchor == GShape.EAnchor.eSW) {

            if (Math.abs(oldDY) > 1) {
                sy = newDY / oldDY;
            }
        }

        shape.scale(sx, sy, fixedX, fixedY);

        this.x0 = x;
        this.y0 = y;
    }

    @Override
    public void finish(int x, int y) {
    }
}