package game.main;

import game.component.PanelGame;
import game.component.StartPanel;
import java.awt.BorderLayout;
import javax.swing.JFrame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.SwingUtilities;

public class Main extends JFrame {

    private StartPanel startPanel;
    private PanelGame panelGame;

    public Main() {
        init();
    }

    private void init() {
        setTitle("Java 2D Game");
        setSize(1366, 768);
        setLocationRelativeTo(null);
        setResizable(false);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // Initialize the panels
        startPanel = new StartPanel(new StartButtonListener());
        panelGame = new PanelGame();

        // Add the start panel initially
        add(startPanel, BorderLayout.CENTER);
    }

    public StartPanel getStartPanel() {
        return startPanel;
    }

    private class StartButtonListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            remove(startPanel);
            add(panelGame);
            panelGame.start();
            revalidate();
            repaint();
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            Main mainGame = new Main();
            mainGame.setVisible(true);
        });
    }
}
