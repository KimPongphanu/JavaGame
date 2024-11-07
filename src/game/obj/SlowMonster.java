package game.obj;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.Area;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Path2D;
import java.awt.geom.Rectangle2D;
import javax.swing.ImageIcon;
import static javax.swing.Spring.height;

public class SlowMonster extends HpRender {

    public static final double SLOWMONSTER_SIZE = 50;
    private double x;
    private double y;
    private final float speed = 0.4f;
    private float angle = 0;
    private final Image image;
    private double shapeXOffset = 0;
    private double shapeYOffset = 0;
    private double shapeWidth = SLOWMONSTER_SIZE;
    private double shapeHeight = SLOWMONSTER_SIZE;

public SlowMonster() {
    super(new HP(100, 100));
    this.image = new ImageIcon(getClass().getResource("/game/image/blueGem.png")).getImage();

    shapeXOffset = 22;
    shapeYOffset = 22;
    shapeWidth = SLOWMONSTER_SIZE-8;
    shapeHeight = SLOWMONSTER_SIZE- 8;
}

public SlowMonster(int i) {
    super(new HP(100, 100));
    this.image = new ImageIcon(getClass().getResource("/game/image/blueGem.png")).getImage();

    shapeXOffset = -13;
    shapeYOffset = -16;
    shapeWidth = SLOWMONSTER_SIZE-8;
    shapeHeight = SLOWMONSTER_SIZE- 8;
}


public void setShapePosition(double offsetX, double offsetY) {
    this.shapeXOffset = offsetX;
    this.shapeYOffset = offsetY;
}

public void setShapeSize(double width, double height) {
    this.shapeWidth = width;
    this.shapeHeight = height;
}


    public void ChangeLocation(double x, double y) {
        this.x = x;
        this.y = y;
    }

    public void update() {
        x += Math.cos(Math.toRadians(angle)) * speed;
        y +=Math.sin(Math.toRadians(angle)) * speed;
    }

    public void changeAngle(float angle) {
        if (angle < 0) {
            angle = 359;
        } else if (angle > 359) {
            angle = 0;
        }
        this.angle = angle;
    }

    public void draw(Graphics2D g2) {
        AffineTransform oldTransform = g2.getTransform();
        g2.translate(x, y);
        AffineTransform tran = new AffineTransform();
        tran.rotate(Math.toRadians(angle), SLOWMONSTER_SIZE / 2, SLOWMONSTER_SIZE / 2);
        g2.drawImage(image, tran, null);
        Shape shap = getShape();
//        hpRender(g2, shap,y - 10 , x + 10);
        g2.setTransform(oldTransform);

        //Test
//        g2.setColor(Color.BLUE);
//        g2.draw(shap);
    }
    
public Rectangle getBounds() {
    return new Rectangle((int) x, (int) y, (int) SLOWMONSTER_SIZE, (int) SLOWMONSTER_SIZE);
}



    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public float getAngle() {
        return angle;
    }

 
//public Area getShape() {
//    AffineTransform afx = new AffineTransform();
//    afx.translate(x, y + 30);
//
//    afx.rotate(Math.toRadians(angle), SLOWMONSTER_SIZE / 2, SLOWMONSTER_SIZE / 2);
//
//    Area transformedShape = new Area(afx.createTransformedShape(monsterShap)); 
//
//    return transformedShape; 
//}

public Area getShape() {
    return new Area(new Ellipse2D.Double(x + shapeXOffset, y + shapeYOffset, shapeWidth, shapeHeight));
}



    public boolean check(int width, int height) {
        Rectangle size = getShape().getBounds();
        if (x <= -size.getWidth() || y < -size.getHeight() || x > width || y > height) {
            return false;
        } else {
            return true;
        }
    }
}
