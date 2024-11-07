package game.obj;

import java.awt.*;
import java.awt.geom.AffineTransform;
import javax.swing.*;
import java.awt.geom.*;

public class Player extends HpRender {

    public static final double PLAYER_SIZE = 64;
    private double x;
    private double y;
    private final float MAX_SPEED = 1.5f;
    private float angle = 0f;
    private final Area playerShap;
    private final Image image;
    private final Image image_speed;
    // Alive
    private boolean alive = true;
    // Speed
    public boolean speedUp;
    private float normalSpeed;
    private float currentSpeed;
    private float speedReductionRate = 0.1f;
    public boolean isSlowed;

    private long slowStartTime;
    private static final long SLOW_DURATION = 3000;
    private static final int WINDOW_WIDTH = 1366;
    private static final int WINDOW_HEIGHT = 750;
    private static final int WIDTH = 64;
    private static final int HEIGHT = 40;

    public Player() {
        super(new HP(150, 150));
        this.normalSpeed = 0.5f;
        this.currentSpeed = normalSpeed;
        speedUp = false;
        this.image = new ImageIcon(getClass().getResource("/game/image/Aircraft.png")).getImage();
        this.image_speed = new ImageIcon(getClass().getResource("/game/image/Aircraft-speed.png")).getImage();
        Rectangle rect = new Rectangle(20, 5, (int) PLAYER_SIZE, (int) PLAYER_SIZE);
        playerShap = new Area(rect);
    }

    public void changeLocation(double x, double y) {
        this.x = x;
        this.y = y;
    }

    public void update() {

        double newX = x + Math.cos(Math.toRadians(angle)) * currentSpeed;
        double newY = y + Math.sin(Math.toRadians(angle)) * currentSpeed;

        boolean hitBoundary = false;

        if (newX < 10 || newX + PLAYER_SIZE > WINDOW_WIDTH) {
            hitBoundary = true;
            currentSpeed = Math.max(currentSpeed - 0.1f, 0);
        } else {
            x = newX;
        }

        if (newY < 10 || newY + PLAYER_SIZE > WINDOW_HEIGHT) {
            hitBoundary = true;
            currentSpeed = Math.max(currentSpeed - 0.1f, 0);
        } else {
            y = newY;
        }

        if (!hitBoundary) {
            currentSpeed = Math.min(currentSpeed + 0.2f, normalSpeed);
        }

        if (isSlowed) {
            this.currentSpeed = 0.2f;
            long currentTime = System.currentTimeMillis();
            if (currentTime - slowStartTime >= SLOW_DURATION) {
                currentSpeed = normalSpeed;
                isSlowed = false;
            }
        }
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

        // Translate to the player's position
        g2.translate(x, y);
        g2.rotate(Math.toRadians(angle - 35), PLAYER_SIZE / 2, PLAYER_SIZE / 2);

        // Draw the player image
        g2.drawImage(speedUp ? image_speed : image, 0, 0, null);

        // Restore the original transform
        g2.setTransform(oldTransform);

        // Draw the health bar above the player
        hpRender(g2, x, y - 20, PLAYER_SIZE);
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

    public void speedUp() {
        if (!isSlowed) {
            speedUp = true;
            currentSpeed += 0.1f;
            if (currentSpeed > MAX_SPEED) {
                currentSpeed = MAX_SPEED;
            }
        }
    }

    public void reduceSpeed() {
        speedUp = false;
        isSlowed = true;
        slowStartTime = System.currentTimeMillis();
    }

    public Area getShape() {
        AffineTransform afx = new AffineTransform();
        afx.translate(x, y);
        afx.rotate(Math.toRadians(angle), PLAYER_SIZE / 2, PLAYER_SIZE / 2);
        return new Area(afx.createTransformedShape(playerShap));
    }

    public boolean isAlive() {
        return alive;
    }

    public void setAlive(boolean alive) {
        this.alive = alive;
    }

    public void reset() {
        alive = true;
        resetHP();
        angle = 0;
        currentSpeed = normalSpeed;
    }

    public double getNormalSpeed() {
        return normalSpeed;
    }

    public double getCurrentSpeed() {
        return currentSpeed;
    }

    public void speedDown() {
        if (currentSpeed > 0.2f) {
            currentSpeed -= 0.1f;
            speedUp = false;
        }
    }
}
