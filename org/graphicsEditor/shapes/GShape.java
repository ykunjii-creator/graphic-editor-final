package org.graphicsEditor.shapes;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.geom.Ellipse2D;
import java.awt.geom.NoninvertibleTransformException;
import java.awt.geom.RectangularShape;
import java.lang.reflect.InvocationTargetException;

// 모든 도형 클래스의 공통 부모 클래스
// GRectangle, GOval, GPolygon이 이 클래스를 상속한다.
// abstract라서 GShape 자체를 직접 객체로 만들지는 않고,
// 공통 기능을 자식 도형들에게 제공하는 역할이다.
// Cloneable은 툴바에 있는 기본 도형 객체를 복제해서 새 도형을 만들기 위해 사용한다.
abstract public class GShape implements Cloneable {
    public enum EAnchor {
        eNW, // North West = 왼쪽 위
        eNN, // North = 위쪽 중앙
        eNE, // North East = 오른쪽 위
        eEE, // East = 오른쪽 중앙
        eSE, // South East = 오른쪽 아래
        eSS, // South = 아래쪽 중앙
        eSW, // South West = 왼쪽 아래
        eWW, // West = 왼쪽 중앙
        eRR,
        eRotate,
        eMove,
        eResize
    }

    // 도형을 선택했을 때 나타나는 조작 위치를 의미하는 enum
    // eNW, eNN, eNE, eEE, eSE, eSS, eSW, eWW는 크기 조절용 8개 앵커
    // eRotate는 회전용 앵커
    // eMove는 도형 내부를 클릭했을 때 이동 기능을 의미
    // eResize는 크기 조절 상태를 의미하는 값

    // 현재 도형이 선택된 상태인지 저장한다.
    // 선택되면 앵커를 화면에 표시한다.
    private boolean isSelected;

    // 실제 도형 모양을 저장하는 Java AWT Shape 객체
    // Rectangle, Ellipse2D, Polygon 등이 여기에 들어간다.
    // protected라서 자식 클래스에서 접근할 수 있다.
    protected Shape shape;

    // 선택된 도형 주변에 표시되는 크기 조절 / 회전 앵커들을 관리한다.
    private Anchors anchors;

    // 도형의 이동, 크기 조절, 회전 정보를 저장하는 AffineTransform 객체
    // 실제 도형 좌표를 계속 바꾸는 대신 변환 정보를 누적해서 관리한다.
    private AffineTransform affineTransform;

    // 현재 도형이 가지고 있는 AffineTransform 객체를 반환한다.
    // 외부에서 도형의 변환 정보를 확인하거나 사용할 수 있게 한다.
    public AffineTransform getAffineTransform() {
        return this.affineTransform;
    };

    // GShape 객체가 만들어질 때 실행되는 생성자
    // 처음에는 선택되지 않은 상태로 시작한다.
    // 앵커 객체를 생성해서 선택 시 조작점을 표시할 준비를 한다.
    // AffineTransform을 새로 만들어 이동/크기조절/회전 정보를 저장할 준비를 한다.
    public GShape() {
        this.isSelected = false;
        this.anchors = new Anchors();
        this.affineTransform = new AffineTransform();
    }

    // 툴바에 저장된 기본 도형 객체를 복제해서
    // 실제 드로잉 패널에 추가할 새 도형 객체를 만드는 메서드
    public GShape clone() {
        try {
            // Object 클래스의 clone()을 이용해 현재 객체를 얕은 복사한다.
            GShape cloned = (GShape) super.clone();

            // shape가 RectangularShape 계열이면 별도로 clone해서 복사한다.
            // Rectangle, Ellipse2D 같은 사각형/타원 계열 도형을 독립된 객체로 만들기 위함이다.
            if (this.shape instanceof RectangularShape) {
                cloned.shape = (Shape) ((RectangularShape) this.shape).clone();
            } else {
                // RectangularShape가 아닌 경우에는 기존 shape 참조를 그대로 사용한다.
                // 현재 Polygon은 여기로 들어갈 수 있다.
                cloned.shape = this.shape;
            }

            // AffineTransform도 새 객체로 복사한다.
            // 원본 도형과 복제된 도형의 변환 정보가 서로 영향을 주지 않게 하기 위함이다.
            cloned.affineTransform = new AffineTransform(this.affineTransform);

            // 앵커도 새로 생성한다.
            // 선택 표시와 조작점은 복제 도형마다 따로 가져야 하기 때문이다.
            cloned.anchors = new Anchors();

            // 새로 만들어진 도형은 기본적으로 선택되지 않은 상태로 둔다.
            cloned.isSelected = false;

            return cloned;
        } catch (CloneNotSupportedException e) {
            throw new RuntimeException(e);
        }
    }

    public boolean isSelected() {
        return isSelected;
    }
    // 현재 도형이 선택되었는지 여부를 반환한다.

    // 도형의 선택 상태를 변경한다.
    public void setSelected(boolean isSelected) {
        // 전달받은 값으로 선택 여부를 저장한다.
        this.isSelected = isSelected;

        // 도형이 선택된 경우, 도형의 bounds를 기준으로 앵커 위치를 설정한다.
        if (isSelected) {
            this.anchors.setPosition(this.shape.getBounds());
        }
    }

    // 마우스 좌표가 도형 위에 있는지,
    // 또는 앵커 위에 있는지 판단하는 메서드
    public EAnchor onShape(int x, int y) {
        // 화면에서 클릭한 좌표를 Point 객체로 만든다.
        Point p = new Point(x, y);
        try {
            // 도형에는 AffineTransform이 적용되어 있을 수 있다.
            // 화면 좌표를 원래 도형 좌표계로 되돌려서 hit-test를 하기 위해
            // inverseTransform을 사용한다.
            this.affineTransform.inverseTransform(p,p);
        } catch (NoninvertibleTransformException e){
            e.printStackTrace();
        }

        // 클릭된 위치가 어떤 앵커인지 저장할 변수
        EAnchor eAnchor = null;

        // 도형이 선택된 상태라면 먼저 앵커를 클릭했는지 확인한다.
        if (isSelected) {
            eAnchor = this.anchors.onShape(p.x, p.y);
        }

        // 앵커가 아니라면 도형 내부를 클릭했는지 확인한다.
        if (eAnchor == null) {
            if (this.shape.contains(p)) {
                // 도형 내부를 클릭했으면 이동 모드로 판단한다.
                eAnchor = EAnchor.eMove;
            }
        }

        // 클릭 위치에 해당하는 앵커/이동 상태를 반환한다.
        return eAnchor;
    }

    // 도형을 화면에 그리는 메서드
    public void draw(Graphics2D g) {
        // 현재 Graphics2D의 transform 상태를 저장한다.
        // 다른 도형에 변환이 영향을 주지 않도록 하기 위함이다.
        AffineTransform oldTransform = g.getTransform();

        // 현재 도형이 가진 AffineTransform을 Graphics2D에 적용한다.
        // 이동, 확대/축소, 회전 정보가 여기서 반영된다.
        g.transform(this.affineTransform);

        // 변환이 적용된 상태에서 실제 도형을 그린다.
        g.draw(shape);

        // 도형이 선택된 상태라면 앵커도 함께 그린다.
        if (isSelected) {
            // 도형의 bounds를 기준으로 앵커 위치를 다시 계산한다.
            this.anchors.setPosition(this.shape.getBounds());

            // 앵커를 화면에 그린다.
            this.anchors.draw(g);
        }

        // Graphics2D의 transform 상태를 원래대로 복원한다.
        // 복원하지 않으면 다음 도형에도 현재 도형의 변환이 적용될 수 있다.
        g.setTransform(oldTransform);
    }

    // 도형을 처음 그릴 때 시작 위치를 설정하는 메서드
    // 도형 종류마다 구현 방식이 다르기 때문에 부모 클래스에서는 비워두고,
    // GRectangle, GOval, GPolygon에서 오버라이딩해서 사용한다.
    public void setLocation0(int x, int y) {}

    // 도형을 그리는 중 마우스 위치에 따라 끝 위치를 설정하는 메서드
    // 자식 클래스에서 도형별로 다르게 구현한다.
    public void setLocation1(int x, int y) {}

    // 도형을 dx, dy만큼 이동시키는 메서드
    // 실제 shape 좌표를 직접 바꾸는 대신 AffineTransform에 이동 정보를 누적한다.
    public void translate(int dx, int dy) {
        this.affineTransform.translate(dx, dy);
    }

    // 도형을 특정 중심점 기준으로 angle만큼 회전시키는 메서드
    // angle은 라디안 단위이며, AffineTransform에 회전 정보를 누적한다.
    public void rotate(double angle, double centerX, double centerY) {
        this.affineTransform.rotate(angle, centerX, centerY);
    }

    // AffineTransform이 적용된 후의 도형 경계 박스를 반환한다.
    public Rectangle getBounds() {
        // 현재 shape에 affineTransform을 적용한 새로운 Shape을 만든다.
        Shape transformedShape = this.affineTransform.createTransformedShape(this.shape);

        // 변환된 도형의 bounds를 반환한다.
        // 회전/이동/크기 조절된 이후의 실제 화면상 경계 영역을 얻기 위해 사용한다.
        return transformedShape.getBounds();
    }

    // 도형을 특정 기준점(anchorX, anchorY)을 기준으로 확대/축소하는 메서드
    public void scale(double sx, double sy, int anchorX, int anchorY) {

        // 기준점을 원점처럼 사용하기 위해 기준점 위치로 좌표계를 이동한다.
        this.affineTransform.translate(anchorX, anchorY);

        // x방향은 sx배, y방향은 sy배 확대/축소한다.
        this.affineTransform.scale(sx, sy);

        // 다시 원래 좌표계로 되돌린다.
        this.affineTransform.translate(-anchorX, -anchorY);
    }

    // 폴리곤처럼 여러 점을 찍는 도형에서 점을 추가하기 위한 메서드
    // 일반 도형에서는 필요 없기 때문에 부모 클래스에서는 비워둔다.
    public void addPoint(int x, int y) {}

    // 도형 그리기를 완료할 때 호출되는 메서드
    // 특히 폴리곤은 더블클릭 시 마지막 임시 점을 정리하기 위해 사용한다.
    public void finish() {}

    // 선택된 도형 주변에 표시되는 조작점들을 관리하는 내부 클래스
    // 크기 조절 앵커 8개와 회전 앵커를 생성하고, 클릭 여부를 판단하며, 화면에 그린다.
    private static class Anchors {
        // 앵커 하나의 너비와 높이
        public int w = 10;
        public int h = 10;

        // 각 앵커를 Ellipse2D 객체 배열로 저장한다.
        // EAnchor enum의 순서와 같은 인덱스를 사용한다.
        private final Ellipse2D[] anchors;

        // Anchors 객체가 만들어질 때 실행되는 생성자
        public Anchors() {
            // EAnchor enum 개수만큼 Ellipse2D 배열을 만든다.
            anchors = new Ellipse2D[EAnchor.values().length];

            // 각 배열 칸에 실제 Ellipse2D 객체를 생성해서 넣는다.
            for (int i = 0; i < anchors.length; i++) {
                this.anchors[i] = new Ellipse2D.Float();
            }
        }

        // 마우스 좌표가 어떤 앵커 위에 있는지 검사한다.
        public EAnchor onShape(int x, int y) {
            // 모든 앵커를 순회하면서
            for (int i = 0; i < anchors.length; i++) {
                // 해당 좌표가 앵커 내부에 포함되면
                if (this.anchors[i].contains(x, y)) {
                    // 그 앵커에 해당하는 EAnchor 값을 반환한다.
                    return EAnchor.values()[i];
                }
            }
            // 어떤 앵커에도 포함되지 않으면 null 반환
            return null;
        }

        // 도형의 경계 사각형 br을 기준으로 앵커들의 위치를 설정한다.
        public void setPosition(Rectangle br) {
            // 도형 bounds의 왼쪽 위 좌표
            int x1 = br.x;
            int y1 = br.y;

            // 도형 bounds의 오른쪽 아래 좌표
            int x2 = br.x + br.width;
            int y2 = br.y + br.height;

            // 도형 bounds의 중앙 좌표
            int mx = br.x + br.width / 2;
            int my = br.y + br.height / 2;

            // 앵커를 중심 기준으로 배치하기 위해 앵커 크기의 절반을 계산한다.
            int hw = w / 2;
            int hh = h / 2;


            // 왼쪽 위 앵커
            this.anchors[EAnchor.eNW.ordinal()].setFrame(x1 - hw, y1 -hh, w, h);
            // 위쪽 중앙 앵커
            this.anchors[EAnchor.eNN.ordinal()].setFrame(mx - hw, y1 -hh, w, h);
            // 오른쪽 위 앵커
            this.anchors[EAnchor.eNE.ordinal()].setFrame(x2 - hw, y1 -hh, w, h);
            // 오른쪽 중앙 앵커
            this.anchors[EAnchor.eEE.ordinal()].setFrame(x2 - hw, my -hh, w, h);
            // 오른쪽 아래 앵커
            this.anchors[EAnchor.eSE.ordinal()].setFrame(x2 - hw, y2 -hh, w, h);
            // 아래쪽 중앙 앵커
            this.anchors[EAnchor.eSS.ordinal()].setFrame(mx - hw, y2 -hh, w, h);
            // 왼쪽 아래 앵커
            this.anchors[EAnchor.eSW.ordinal()].setFrame(x1 - hw, y2 -hh, w, h);
            // 왼쪽 중앙 앵커
            this.anchors[EAnchor.eWW.ordinal()].setFrame(x1 - hw, my -hh, w, h);
            // 회전 앵커는 도형 위쪽 중앙보다 30픽셀 위에 배치한다.
            this.anchors[EAnchor.eRotate.ordinal()].setFrame(mx - hw, y1 -30, w, h);
        }

        // 앵커들을 화면에 그리는 메서드
        public void draw(Graphics2D g) {
            // 원본 Graphics2D에 영향을 주지 않기 위해 복사본을 만든다.
            Graphics2D g2 = (Graphics2D) g.create();

            // 크기 조절용 앵커 8개를 그린다.
            // eNW부터 eWW까지가 resize anchor이다.
            for (int i = EAnchor.eNW.ordinal(); i <= EAnchor.eWW.ordinal(); i++) {
                if (anchors[i] != null) {
                    g2.draw(anchors[i]);
                }
            }

            // 회전 앵커와 위쪽 중앙 앵커를 가져온다.
            // 연결선 그리기: 위쪽 중앙 anchor(eNN) -> rotate.anchor
            Ellipse2D rotateAnchor = anchors[EAnchor.eRotate.ordinal()];
            Ellipse2D topAnchor = anchors[EAnchor.eNN.ordinal()];

            // 위쪽 중앙 앵커의 중심 좌표
            int x1 = (int) topAnchor.getCenterX();
            int y1 = (int) topAnchor.getCenterY();

            // 회전 앵커의 중심 좌표
            int x2 = (int) rotateAnchor.getCenterX();
            int y2 = (int) rotateAnchor.getCenterY();

            // 위쪽 중앙 앵커와 회전 앵커를 연결하는 선을 그린다.
            g2.drawLine(x1, y1, x2, y2);

            // 회전 아이콘을 표시하기 위해 기존 폰트를 저장한다.
            Font oldFont = g2.getFont();
            // 회전 아이콘용 폰트 설정
            g2.setFont(new Font("Dialog", Font.PLAIN, 20));

            // 회전 아이콘을 회전 앵커 위치 근처에 그린다.
            g2.drawString("↻", x2 - 8, y2 + 5);

            // 폰트를 원래대로 복원한다.
            g2.setFont(oldFont);
            // 복사한 Graphics2D 자원을 해제한다.
            g2.dispose();
        }

    }
}
