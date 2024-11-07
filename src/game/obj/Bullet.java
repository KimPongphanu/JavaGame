package game.obj;

import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.Area;
import java.awt.geom.Ellipse2D;
import javax.swing.ImageIcon;
import static javax.swing.Spring.height;
import static javax.swing.Spring.width;

public class Bullet {
    private double x;
    private double y;
    private final float angle;
    private double size;
    private float speed = 1f;
//    private final Shape shape;
    private Image bulletImage; 
    private int bulletType; 
    private int damageMonster = 80;
    
    public int getDamageMonster(){
        return this.damageMonster;
    }

    public Bullet(double x, double y, float angle, double size, float speed, int bulletType) {
        x += Player.PLAYER_SIZE / 2 - (size / 2);
        y += Player.PLAYER_SIZE / 2 - (size / 2);
        this.x = x;
        this.y = y;
        this.angle = angle;
        this.size = size;
        this.speed = speed;
        this.bulletType = bulletType; 
        
        
        if (bulletType == 1) {
           bulletImage = new ImageIcon(getClass().getResource("/game/image/bulletmissileNew.png")).getImage();
//             bulletImage = new ImageIcon(getClass().getResource("/game/image/poop.png")).getImage();
        } else {
            bulletImage = new ImageIcon(getClass().getResource("/game/image/bulletsmall.png")).getImage();
        }
    }

    public void update() {
        x += Math.cos(Math.toRadians(angle)) * speed;
        y += Math.sin(Math.toRadians(angle)) * speed;
    }

   public boolean check(int width, int height) {
    return x >= 0 && x <= width && y >= 0 && y <= height;
}
    
public Rectangle getBounds() {
    return new Rectangle((int) x, (int) y, (int) size, (int) size); 
}


    public void draw(Graphics2D g2) {
    AffineTransform oldTransform = g2.getTransform();

   
    g2.translate(x + bulletImage.getWidth(null) / 2, y + bulletImage.getHeight(null) / 2);


    g2.rotate(Math.toRadians(angle));


    g2.drawImage(bulletImage, -bulletImage.getWidth(null) / 2, -bulletImage.getHeight(null) / 2, null);

    g2.setTransform(oldTransform);
}



    public Shape getShape(){
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
}
