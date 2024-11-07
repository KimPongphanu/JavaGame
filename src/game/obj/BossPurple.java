package game.obj;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.Area;
import java.awt.geom.Rectangle2D;
import javax.swing.ImageIcon;
import java.util.Random;

public class BossPurple extends HpRender {
    public static final double BOSSPURPLE_SIZE = 400;
    private double x;
    private double y;
    private final float speed = 0.4f;
    private float angle = 0;
    private final Image image;
    private final Area monsterShape;
    private final Random random = new Random(); 
    private int frameCount = 0;
    private long lastShootTime;
    private long shootCooldown;

    public BossPurple() {
        super(new HP(1000, 1000));
        this.image = new ImageIcon(getClass().getResource("/game/image/Boss1.png")).getImage();
        
        this.shootCooldown = 1000; 

        int imageWidth = image.getWidth(null);
        int imageHeight = image.getHeight(null);

        int rectWidth = imageWidth - 260; 
        int rectHeight = imageHeight -180; 

        Rectangle rect = new Rectangle(130, 40, rectWidth, rectHeight);
        monsterShape = new Area(rect);
        this.lastShootTime = System.currentTimeMillis(); 
    }

    public void ChangeLocation(double x, double y) {
        this.x = x;
        this.y = y;
    }

   public void update(int width, int height) {

    int margin = 150;

    x += Math.cos(Math.toRadians(angle)) * speed;
    y += Math.sin(Math.toRadians(angle)) * speed;

    Rectangle size = getShape().getBounds();

    if (x < margin) {
        x++;
        angle = random.nextInt(90);  
    } else if (x + size.getWidth() > width - margin) {
        x--;
        angle = 180 + random.nextInt(90); 
    }


    if (y < margin) {
        y++;
        angle = 90 + random.nextInt(90);  
    } else if (y + size.getHeight() > height - margin) {
        y--;
        angle = 270 + random.nextInt(90);
    }

    if (x < margin && y < margin) {
        angle = 45; 
    } else if (x < margin && y + size.getHeight() > height - margin) {
        angle = 315; 
    } else if (x + size.getWidth() > width - margin && y < margin) {
        angle = 135; 
    } else if (x + size.getWidth() > width - margin && y + size.getHeight() > height - margin) {
        angle = 225;
    }

    x = Math.max(margin, Math.min(x, width - margin - size.getWidth()));
    y = Math.max(margin, Math.min(y, height - margin - size.getHeight()));
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
        tran.rotate(Math.toRadians(angle), BOSSPURPLE_SIZE / 2, BOSSPURPLE_SIZE / 2);
        g2.drawImage(image, tran, null);
        Shape shape = getShape();
        //hpRender(g2, getShape(), BOSSPURPLE_SIZE);
        g2.setTransform(oldTransform);

        // Test
//        g2.setColor(Color.BLUE);
//        g2.draw(shape);
    }
    
    public int getHeight() {
        return image.getHeight(null); 
    }

    public int getWidth() {
        return image.getWidth(null);
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
        AffineTransform afx = new AffineTransform();
        afx.translate(x, y + 30);
        afx.rotate(Math.toRadians(angle), BOSSPURPLE_SIZE / 2, BOSSPURPLE_SIZE / 2);
        Area transformedShape = new Area(afx.createTransformedShape(monsterShape)); 
        return transformedShape; 
    }

    public boolean check(int width, int height) {
        Rectangle size = getShape().getBounds();
        return x + size.getWidth() > 0 && y + size.getHeight() > 0 && x < width && y < height;
    }

    public void setLastShootTime(long time) {
        this.lastShootTime = time;
    }

    public long getLastShootTime() {
        return lastShootTime;
    }


    public long getShootCooldown() {
        return shootCooldown;
    }
}
