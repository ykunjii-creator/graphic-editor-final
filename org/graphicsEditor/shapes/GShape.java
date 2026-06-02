package org.graphicsEditor.shapes;

import java.awt.*;
import java.awt.geom.Ellipse2D;
import java.awt.geom.RectangularShape;
import java.lang.reflect.InvocationTargetException;

abstract public class GShape implements Cloneable {
    public enum EAnchor {
        eNW,
        eNN,
        eNE,
        eEE,
        eSE,
        eSS,
        eSW,
        eWW,
        eRR,
        eRotate,
        eMove,
        eResize
    }

    private boolean isSelected;

    protected Shape shape;

    public GShape() {
    }
    public GShape clone() {
        try {
            GShape cloned = (GShape) super.clone();
            cloned.shape = (Shape) (((RectangularShape) this.shape).clone());
            return cloned;
        } catch (CloneNotSupportedException e) {
            throw new RuntimeException(e);
        }
    }

    public EAnchor onShape(int x, int y) {
        if (this.shape.contains(x, y)) {
            return EAnchor.eMove;
        } else {
            return null;
        }
    }

    public void draw(Graphics2D g) {
        g.draw(shape);
    }

    public void resize(int x, int y) {

    }
    public void rotate(int x, int y) {
    }
    public void setLocation0(int x, int y) {}
    public void setLocation1(int x, int y) {}
    public void translate(int dx, int dy) {}

    public void scale(double sx, double sy, int cx, int cy) {}
    private class Anchors {
        public int w = 15;
        public int h = 15;

        private Ellipse2D anchors[];

        public Anchors() {
            anchors = new Ellipse2D[EAnchor.values().length-1];
            for (int i = 0; i < anchors.length; i++) {
                this.anchors[i] = new Ellipse2D.Float();
                this.anchors[i].setFrame(0, 0, w, h);
            }
        }

        public void setPosition(Rectangle br) {
            int brw = br.width;
            int brh = br.height;
           this.anchors[EAnchor.eNW.ordinal()].setFrame(br.x, br.y -h, w, h);
           this.anchors[EAnchor.eNN.ordinal()].setFrame(br.x, br.y -h, w, h);
           this.anchors[EAnchor.eNE.ordinal()].setFrame(br.x, br.y -h, w, h);
           this.anchors[EAnchor.eEE.ordinal()].setFrame(br.x, br.y -h, w, h);
           this.anchors[EAnchor.eSE.ordinal()].setFrame(br.x+brw, br.y+brh -h, w, h);
           this.anchors[EAnchor.eSS.ordinal()].setFrame(br.x, br.y -h, w, h);
           this.anchors[EAnchor.eSW.ordinal()].setFrame(br.x, br.y -h, w, h);
           this.anchors[EAnchor.eWW.ordinal()].setFrame(br.x, br.y -h, w, h);
           this.anchors[EAnchor.eRotate.ordinal()].setFrame(br.x, br.y -h, w, h);
        }

        public void draw(Graphics2D g) {
            for (int i = 0; i < EAnchor.values().length; i++) {
                g.draw(anchors[i]);
            }
        }

    }

}
