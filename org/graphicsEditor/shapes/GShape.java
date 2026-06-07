package org.graphicsEditor.shapes;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.geom.Ellipse2D;
import java.awt.geom.NoninvertibleTransformException;
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
    private Anchors anchors;
    private AffineTransform affineTransform;

    public AffineTransform getAffineTransform() {
        return this.affineTransform;
    };

    public GShape() {
        this.isSelected = false;
        this.anchors = new Anchors();
        this.affineTransform = new AffineTransform();
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

    public boolean isSelected() {
        return isSelected;
    }
    public void setSelected(boolean isSelected) {
        this.isSelected = isSelected;
        if (isSelected) {
            this.anchors.setPosition(this.shape.getBounds());
        }
    }

    public EAnchor onShape(int x, int y) {
        Point p = new Point(x, y);
        try {
            this.affineTransform.inverseTransform(p,p);
        } catch (NoninvertibleTransformException e){
            e.printStackTrace();
        }

        EAnchor eAnchor = null;

        if (isSelected) {
            eAnchor = this.anchors.onShape(p.x, p.y);
        }

        if (eAnchor == null) {
            if (this.shape.contains(p)) {
                eAnchor = EAnchor.eMove;
            }
        }

        return eAnchor;
    }

    public void draw(Graphics2D g) {
        AffineTransform oldTransform = g.getTransform();

        g.transform(this.affineTransform);

        g.draw(shape);

        if (isSelected) {
            this.anchors.setPosition(this.shape.getBounds());
            this.anchors.draw(g);
        }

        g.setTransform(oldTransform);
    }

    public void setLocation0(int x, int y) {}
    public void setLocation1(int x, int y) {}
    public void translate(int dx, int dy) {}
    public void scale(double sx, double sy, int cx, int cy) {}

    public void addPoint(int x, int y) {}
    public void finish() {}

    private static class Anchors {
        public int w = 10;
        public int h = 10;

        private final Ellipse2D[] anchors;

        public Anchors() {
            anchors = new Ellipse2D[EAnchor.values().length];

            for (int i = 0; i < anchors.length; i++) {
                this.anchors[i] = new Ellipse2D.Float();
            }
        }

        public EAnchor onShape(int x, int y) {
            for (int i = 0; i < anchors.length; i++) {
                if (this.anchors[i].contains(x, y)) {
                    return EAnchor.values()[i];
                }
            }
            return null;
        }

        public void setPosition(Rectangle br) {
            int x1 = br.x;
            int y1 = br.y;

            int x2 = br.x + br.width;
            int y2 = br.y + br.height;

            int mx = br.x + br.width / 2;
            int my = br.y + br.height / 2;

            int hw = w / 2;
            int hh = h / 2;

            // 8개 resize anchor
            this.anchors[EAnchor.eNW.ordinal()].setFrame(x1 - hw, y1 -hh, w, h);
            this.anchors[EAnchor.eNN.ordinal()].setFrame(mx - hw, y1 -hh, w, h);
            this.anchors[EAnchor.eNE.ordinal()].setFrame(x2 - hw, y1 -hh, w, h);

            this.anchors[EAnchor.eEE.ordinal()].setFrame(x2 - hw, my -hh, w, h);

            this.anchors[EAnchor.eSE.ordinal()].setFrame(x2 - hw, y2 -hh, w, h);
            this.anchors[EAnchor.eSS.ordinal()].setFrame(mx - hw, y2 -hh, w, h);
            this.anchors[EAnchor.eSW.ordinal()].setFrame(x1 - hw, y2 -hh, w, h);

            this.anchors[EAnchor.eWW.ordinal()].setFrame(x1 - hw, my -hh, w, h);
           // this.anchors[EAnchor.eRotate.ordinal()].setFrame(br.x, br.y -h, w, h);
        }

        public void draw(Graphics2D g) {
            for (int i = 0; i < 8; i++) {
                if (anchors[i] != null) {
                    g.draw(anchors[i]);
                }
            }
        }

    }

}
