package game.component;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.ImageIcon;
import java.awt.BorderLayout;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.event.ActionListener;

public class StartPanel extends JPanel {

    private JButton startButton;
    private Image backgroundImg;

    public StartPanel(ActionListener startListener) {
        setLayout(null);

        // Load the background image using getResource
        backgroundImg = new ImageIcon(getClass().getResource("/game/image/backgroundStart.png")).getImage();

        // Create the Start button with an image
        startButton = new JButton(new ImageIcon(getClass().getResource("/game/image/startBtn.png")));
        startButton.setBorderPainted(false); // To remove the button border
        startButton.setContentAreaFilled(false); // To make the button transparent
        startButton.setFocusPainted(false); // To remove focus border
        startButton.setBounds(400,400,600,200);

        startButton.addActionListener(startListener);

        // Add the button to the panel
        add(startButton);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        // Draw the background image
        g.drawImage(backgroundImg, 0, 0, getWidth(), getHeight(), this);
    }
}
