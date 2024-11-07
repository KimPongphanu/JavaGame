package game.obj;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.geom.Rectangle2D;

public class HpRender {

    private final HP hp;

    public HpRender(HP hp) {
        this.hp = hp;
    }

    // Overloaded version for Player
    protected void hpRender(Graphics2D g2, double x, double y, double size) {
    if (hp.getCurrentHp() != hp.getMAX_HP()) {
        // Draw the health bar directly above the monster using its current coordinates
        double hpY = y - 10; // Fixed position above the monster, adjust as needed
        double hpX = x;

        // Draw background of the health bar
        g2.setColor(new Color(70, 70, 70));
        g2.fill(new Rectangle2D.Double(hpX, hpY, size, 4));

        // Draw current health
        g2.setColor(new Color(253, 91, 91));
        double hpSize = (hp.getCurrentHp() / hp.getMAX_HP()) * size;
        g2.fill(new Rectangle2D.Double(hpX, hpY, hpSize, 4));
    }
}


    // Overloaded version for Monster
    protected void hpRender(Graphics2D g2, Shape shape, double monsterSize) {
        if (hp.getCurrentHp() != hp.getMAX_HP()) {
            double hpY = shape.getBounds().getY() - 15;  // Position above the monster with a spacing of 15
            double hpX = shape.getBounds().getX();

            // Draw background of the health bar
            g2.setColor(new Color(70, 70, 70));
            g2.fill(new Rectangle2D.Double(hpX, hpY, monsterSize, 2));  // Monster health bar is shorter in height

            // Draw current health
            g2.setColor(new Color(253, 91, 91));
            double hpSize = (hp.getCurrentHp() / hp.getMAX_HP()) * monsterSize;
            g2.fill(new Rectangle2D.Double(hpX, hpY, hpSize, 2));
        }
    }

    // Other methods for updating HP, getting HP, etc.
    public boolean updateHP(double cutHP) {
        hp.setCurrentHp(hp.getCurrentHp() - cutHP);
        return hp.getCurrentHp() > 0;
    }

    public double getHP() {
        return hp.getCurrentHp();
    }

    public void resetHP() {
        hp.setCurrentHp(hp.getMAX_HP());
    }
}
