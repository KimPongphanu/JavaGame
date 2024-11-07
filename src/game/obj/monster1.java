package game.obj;

import java.awt.*;
import java.awt.geom.AffineTransform;
import javax.swing.*;
import java.awt.geom.*;

public class monster1 extends HpRender {

    private boolean facingLeft;
    public static final double MONSTER1_SIZE = 50;
    private double x;
    private double y;
    private final float speed = 0.3f;
    private float angle = 0;
    private final Image image;
    private final Area monsterShap;

    //tune rect shape
    public double plusW;
    public double plusX;
    public double plusY;

    // Constructor with facing direction flag
    public monster1(boolean facingLeft, double plusX, double plusY, double plusW) {
        super(new HP(
                100, 100));
        this.plusX = plusX;
        this.plusY = plusY;
        this.plusW = plusW;

        this.facingLeft = facingLeft;

        this.image = new ImageIcon(getClass().getResource("/game/image/evilMonster.png")).getImage();

        // Define the shape for collision purposes
        int imageWidth = image.getWidth(null);
        int imageHeight = image.getHeight(null);
        int rectWidth = imageWidth;
        int rectHeight = imageHeight;
        Rectangle rect = new Rectangle(30, 10, rectWidth, rectHeight);
        monsterShap = new Area(rect);
    }

    public Rectangle getBounds() {
        return new Rectangle((int) x, (int) y, (int) MONSTER1_SIZE, (int) MONSTER1_SIZE);
    }

    public void changeLocation(double x, double y) {
        this.x = x;
        this.y = y;
    }

    public void update() {
        if (facingLeft) {
            x -= speed; // Move left
        } else {
            x += speed; // Move right
        }
    }

    public void changeDirection() {
        facingLeft = !facingLeft; // Change direction when needed
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
        // Save the original transform
        AffineTransform oldTransform = g2.getTransform();

        // Translate to the monster's position
        g2.translate(x, y);

        // Rotate if the monster is facing left
        if (facingLeft) {
            g2.rotate(Math.toRadians(180), MONSTER1_SIZE / 2, MONSTER1_SIZE / 2);
        }

        // Draw the monster image
        g2.drawImage(image, 0, 0, null);

        // Restore the original transform
        g2.setTransform(oldTransform);

        // Draw the health bar above the monster without rotating it
        hpRender(g2, x + plusX, y + plusY, MONSTER1_SIZE);

//        g2.setColor(Color.RED);
//        g2.draw(getShape());
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

    public Area getShape() {
        Rectangle2D rect = new Rectangle2D.Double(0, 0, image.getWidth(null), image.getHeight(null));

        AffineTransform afx = new AffineTransform();
        afx.translate(x, y);
        afx.rotate(Math.toRadians(angle), image.getWidth(null) / 2.0, image.getHeight(null) / 2.0);

        return new Area(afx.createTransformedShape(rect));
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
