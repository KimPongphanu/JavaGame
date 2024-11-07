package game.obj;

import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.Area;
import java.awt.geom.Ellipse2D;
import java.lang.annotation.Target;
import java.util.ArrayList;
import java.util.List;
import javax.swing.ImageIcon;

public class BulletMonster {
    private double x;
    private double y;
    private final float angle;
    private double size;
    private float speed = 1f;
    private Image bulletImage; 
    private int bulletType;
    private List<Target> targets = new ArrayList<>();

    public BulletMonster(double x, double y, float angle, double size, float speed, int bulletType) {
        x += Player.PLAYER_SIZE / 2 - (size / 2);
        y += Player.PLAYER_SIZE / 2 - (size / 2);
        this.x = x;
        this.y = y;
        this.angle = angle;
        this.size = size;
        this.speed = speed;
        this.bulletType = bulletType; 
        
        if (bulletType == 1) {
            bulletImage = new ImageIcon(getClass().getResource("/game/image/orangeGem.png")).getImage();
//            bulletImage = new ImageIcon(getClass().getResource("/game/image/poop.png")).getImage();
//            bulletImage = new ImageIcon(getClass().getResource("/game/image/bulletMonster.png")).getImage();
        } 
    }

    public void update() {
        x += Math.cos(Math.toRadians(angle)) * speed;
        y += Math.sin(Math.toRadians(angle)) * speed;
    }

    public boolean check(int width, int height) {
        return !(x <= -size || y < -size || x > width || y > height);
    }

    public void draw(Graphics2D g2) {
        AffineTransform oldTransform = g2.getTransform();
        g2.translate(x, y);
        g2.rotate(Math.toRadians(angle)); 
        g2.drawImage(bulletImage, -bulletImage.getWidth(null) / 2, -bulletImage.getHeight(null) / 2, null); 
        g2.setTransform(oldTransform);
    }

    public Shape getShape() {
        return new Area(new Ellipse2D.Double(x, y, size, size));
    }
    
    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public double getSize() {
        return size;
    }

    public double getCenterX() {
        return x + size / 2;
    }

    public double getCenterY() {
        return y + size / 2;
    }
    
    public boolean checkCollision(Player player) {
        Shape bulletShape = getShape();
        Shape playerShape = player.getShape(); // Assume Player class has a getShape() method
        return bulletShape.intersects(playerShape.getBounds());
    }

    public void updatePosition() {
        double radians = Math.toRadians(angle);
        x += speed * Math.cos(radians);
        y += speed * Math.sin(radians);
    }

    public boolean isOutOfBound(int width, int height) {
        return (x < 0 || x > width || y < 0 || y > height);
    }
    


    
}
