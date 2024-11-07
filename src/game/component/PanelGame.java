package game.component;

import game.obj.BossPurple;
import game.obj.Bullet;
import game.obj.BulletMonster;
import game.obj.Effect;
import game.obj.Player;
import game.obj.SlowMonster;
import game.obj.monster1;
import game.obj.sound.Sound;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.geom.Area;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.*;
import javax.imageio.ImageIO;
import javax.swing.*;
import java.util.List;
import java.util.ArrayList;
import java.util.Collections;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.ResultSet;

public class PanelGame extends JComponent {

    private Graphics2D g2;
    private BufferedImage image;
    private int width;
    private int height;
    private ScheduledExecutorService gameExecutor;
    private boolean start = true;
    private Key key;
    private int shotTime;
    private BufferedImage backgroundImage;
    private BossPurple bossPurple;

    // Game FPS
    private final int FPS = 60;
    private final int TARGET_TIME = 1000000000 / FPS;

    // Game Object
    private Sound sound;
    private Player player;
    private int maxScore = 5;
    private int score;
    private int gameState = 0;
    private boolean goNext = false;
    private boolean gameActive = true;
    private final List<Bullet> bullets = Collections.synchronizedList(new ArrayList<>());
    private final List<monster1> monsters = Collections.synchronizedList(new ArrayList<>());
    private final List<SlowMonster> slowmonsters = Collections.synchronizedList(new ArrayList<>());
    private final List<Effect> boomEffects = Collections.synchronizedList(new ArrayList<>());
    private final List<BulletMonster> bulletMonsters = Collections.synchronizedList(new ArrayList<>());
    private boolean checkBoss = false;
    private long startTime;
    private int bossScore = 0;
    private ScheduledExecutorService bossShootExecutor;

    private boolean panelTransitionTriggered = false;

    public PanelGame() {
        width = 1366;
        height = 768;
        score = 0;
        gameActive = true;
        setFont(new Font("Arial", Font.PLAIN, 20));
    }

    public void stop() {
        start = false;
        if (gameExecutor != null && !gameExecutor.isShutdown()) {
            gameExecutor.shutdownNow();
        }
    }

    public void start() {

        start = true;  // Set start to true to indicate the game has started
        gameActive = true;  // Mark game as active
        score = 0;  // Reset score
        goNext = false;

//        width = 1366;
//        height = 768;
        image = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        g2 = image.createGraphics();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);

        gameState = 0;

        // Initialize gameExecutor if it has not already been initialized
        if (gameExecutor == null || gameExecutor.isShutdown()) {
            gameExecutor = Executors.newScheduledThreadPool(1);
        }

        // Initialize game components
        initObjectGame();
        initKeyboard();
        initBullets();

        // Schedule the game loop
        gameExecutor.scheduleAtFixedRate(this::gameLoop, 0, 1000 / FPS, TimeUnit.MILLISECONDS);

        if (bossShootExecutor == null || bossShootExecutor.isShutdown()) {
            bossShootExecutor = Executors.newScheduledThreadPool(1);
            bossShootExecutor.scheduleAtFixedRate(() -> {
                if (bossPurple != null) {
                    shootBullets(bossPurple);
                }
            }, 0, 6, TimeUnit.SECONDS);
        }
    }

    private void shootBullets(BossPurple boss) {
        for (int i = 0; i < 6; i++) {
            float angle = i * 60.0f;
            float speed = 1.0f;
            BulletMonster bullet = new BulletMonster(boss.getX() + 200, boss.getY() + 100, angle, 5, speed, 1);
            bulletMonsters.add(bullet);
        }
    }

    private void gameLoop() {
        long startTime = System.nanoTime();
        updateGame();
        drawBackground();
        drawGame();
        render();
        long time = System.nanoTime() - startTime;
        if (time < TARGET_TIME) {
            sleep((TARGET_TIME - time) / 1000000);
        }
    }

    private void addMonster() {
        if (monsters.size() < 3 && !checkBoss) {
            Random ran = new Random();
            int locationY = ran.nextInt(height - 100);
            monster1 monster = new monster1(false, 10, 0, 40); //Encapsulation
            monster.changeLocation(0, locationY);
            monsters.add(monster);
            int locationY2 = ran.nextInt(height + 100);
            monster1 monster2 = new monster1(true, 0, -30, 0);//Encapsulation
            monster2.changeLocation(1300, locationY2);
            monsters.add(monster2);
        }
    }

    private void addSlowMonster() {
        if (slowmonsters.size() < 2 && !checkBoss) {
            Random ran = new Random();
            int locationY = ran.nextInt(height + 150);
            SlowMonster slowmonster = new SlowMonster();
            slowmonster.ChangeLocation(0, locationY);
            slowmonsters.add(slowmonster);

            int locationY2 = ran.nextInt(height - 150);
            SlowMonster slowMonster2 = new SlowMonster(1);
            slowMonster2.ChangeLocation(width, locationY2);
            slowMonster2.changeAngle(180);
            slowmonsters.add(slowMonster2);
        }
    }

    private void initObjectGame() {
        player = new Player(); //Composition
        player.changeLocation(150, 150);
        loadBackgroundImage();

        gameExecutor.scheduleAtFixedRate(() -> {
            if (start) {
                addMonster();
                addSlowMonster();
            }
        }, 0, 6, TimeUnit.SECONDS);
    }

    private void resetGame() {
        gameState = 0;
        score = 0;
        monsters.clear();
        slowmonsters.clear();
        bullets.clear();
        player.changeLocation(150, 150);
        player.reset();
        player.isSlowed = false;
    }

    private void initKeyboard() {
        key = new Key();
        requestFocus();
        addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                switch (e.getKeyCode()) {
                    case KeyEvent.VK_A ->
                        key.setKey_left(true);
                    case KeyEvent.VK_D ->
                        key.setKey_right(true);
                    case KeyEvent.VK_SPACE ->
                        key.setKey_space(true);
                    case KeyEvent.VK_J ->
                        key.setKey_j(true);
                    case KeyEvent.VK_K ->
                        key.setKey_k(true);
                    case KeyEvent.VK_ENTER -> {
                        key.setKey_enter(true);
                    }
                }
            }

            @Override
            public void keyReleased(KeyEvent e) {
                switch (e.getKeyCode()) {
                    case KeyEvent.VK_A ->
                        key.setKey_left(false);
                    case KeyEvent.VK_D ->
                        key.setKey_right(false);
                    case KeyEvent.VK_SPACE ->
                        key.setKey_space(false);
                    case KeyEvent.VK_J ->
                        key.setKey_j(false);
                    case KeyEvent.VK_K ->
                        key.setKey_k(false);
                    case KeyEvent.VK_ENTER ->
                        key.setKey_enter(false);
                }
            }
        });

        gameExecutor.scheduleAtFixedRate(this::updateGame, 0, 4, TimeUnit.MILLISECONDS);
    }

    private void updateGame() {
        if (player.isAlive()) {
            float angle = player.getAngle();
            if (key.isKey_left()) {
                angle -= 0.5f;
            }
            if (key.isKey_right()) {
                angle += 0.5f;
            }
            if (key.isKey_j() || key.isKey_k()) {
                if (shotTime == 0) {
                    bullets.add(0, new Bullet(
                            player.getX(),
                            player.getY(),
                            player.getAngle(),
                            key.isKey_j() ? 5 : 20,
                            key.isKey_j() ? 2f : 1f,
                            key.isKey_j() ? 2 : 1));
                    shotTime = 15;
                }
            } else {
                if (shotTime > 0) {
                    shotTime--;
                }
            }

            if (key.isKey_space()) {
                player.speedUp = true;
                player.speedUp();
            } else {
                player.speedUp = false;
                if (player.getCurrentSpeed() > player.getNormalSpeed()) {
                    player.speedDown();
                }
            }

            player.update();
            player.changeAngle(angle);
        } else {
            if (key.isKey_enter() && gameState == 1) {
                resetGame();
            }
        }

        // ลบกระสุนที่ออกนอกหน้าจอ
        List<Bullet> bulletsToRemove = new ArrayList<>();
        for (Bullet bullet : bullets) {
            bullet.update();
            if (!bullet.check(width, height)) {  // ตรวจสอบว่ากระสุนออกนอกหน้าจอ
                bulletsToRemove.add(bullet);
            }
        }
        bullets.removeAll(bulletsToRemove);
//        System.out.println("Player Bullet Remove");

// ลบกระสุนของมอนสเตอร์ที่ยิงออกไปนอกหน้าจอ
        List<BulletMonster> bulletsToRemoveMonsters = new ArrayList<>();
        for (BulletMonster bullet : bulletMonsters) {
            bullet.update();
            if (!bullet.check(width, height)) {
                bulletsToRemoveMonsters.add(bullet);
            }
        }
        bulletMonsters.removeAll(bulletsToRemoveMonsters);
//        System.out.println("Bullet Monster Remove");

        // อัปเดตมอนสเตอร์และบอส
        for (monster1 monster : monsters) {
            monster.update();
        }

        for (SlowMonster slowMonster : slowmonsters) {
            slowMonster.update();
        }

        // อัปเดตตำแหน่งของ BossPurple ถ้ามี
        if (bossPurple != null) {
            bossPurple.update(width, height);
        }

        if (player.getHP() <= 0) {
            gameState = 1;
            displayGameOver(g2);
        }

        // อัปเดตการชนกัน
        handleCollisions();

        // อัปเดตเอฟเฟกต์ต่างๆ
        for (Effect effect : boomEffects) {
            effect.update();
        }
    }

    private void handleCollisions() {
        List<Bullet> bulletsToRemove = new ArrayList<>();
        List<monster1> monstersToRemove = new ArrayList<>();
        List<SlowMonster> slowMonstersToRemove = new ArrayList<>();
        List<BulletMonster> bulletMonstersToRemove = new ArrayList<>();

        for (Bullet bullet : bullets) {
            Rectangle bulletBounds = bullet.getBounds();

            //Bullet shoot Monster
            for (monster1 monster : monsters) {
                if (bulletBounds.intersects(monster.getBounds())) {
                    monster.updateHP(20);
                    bulletsToRemove.add(bullet);

                    if (monster.getHP() <= 0) {
                        monstersToRemove.add(monster);
                        score += 1;
                    }
                    break;
                }
            }

            //Bullet shoot SlowMonster
            for (SlowMonster slowMonster : slowmonsters) {
                if (bulletBounds.intersects(slowMonster.getBounds())) {
                    slowMonster.updateHP(20);
                    bulletsToRemove.add(bullet);

                    if (slowMonster.getHP() <= 0) {
                        slowMonstersToRemove.add(slowMonster);
                    }
                    break;
                }
            }

            //Bullet shoot Boss
            if (bossPurple != null && bossPurple.getShape().getBounds().intersects(bulletBounds)) {
                bossPurple.updateHP(bullet.getDamageMonster());
                bulletsToRemove.add(bullet);

                if (bossPurple.getHP() <= 0) {
                    bossPurple = null;
                    bossScore++;

                    if (bossScore >= 1) {
                        SwingUtilities.invokeLater(this::goToEndPanel);
                    }
                }
            }
        }

        for (BulletMonster bulletMonster : bulletMonsters) {
            Area bulletArea = new Area(bulletMonster.getShape());
            Area playerArea = new Area(player.getShape());
            bulletArea.intersect(playerArea);
            if (!bulletArea.isEmpty()) {
                player.updateHP(50);
                bulletMonstersToRemove.add(bulletMonster);
                if (player.getHP() <= 0) {
                    player.setAlive(false);
                    SwingUtilities.invokeLater(this::goToDefeatPanel);
                }
            }
        }

        for (monster1 monster : monsters) {
            if (checkPlayer(monster)) {
                player.updateHP(10);
                if (monster.getHP() <= 0) {
                    monstersToRemove.add(monster);
                }
                if (player.getHP() <= 0) {
                    player.setAlive(false);
                }
            }
        }

        for (SlowMonster slowMonster : slowmonsters) {
            if (checkPlayer(slowMonster)) {
                slowMonstersToRemove.add(slowMonster);
                if (player.getHP() <= 0) {
                    player.setAlive(false);
                    gameState = 1;  
                }
            }
        }

        if (bossPurple != null && checkPlayer(bossPurple)) {
            player.updateHP(200); 
            if (bossPurple.getHP() <= 0) {
                bossPurple = null; 
                SwingUtilities.invokeLater(this::goToEndPanel); 
            } else if (player.getHP() <= 0) {
                SwingUtilities.invokeLater(this::goToDefeatPanel);
            }
        }

        bullets.removeAll(bulletsToRemove);
        monsters.removeAll(monstersToRemove);
        slowmonsters.removeAll(slowMonstersToRemove);
        bulletMonsters.removeAll(bulletMonstersToRemove);
    }

    private boolean checkPlayer(monster1 monster) { // Overloading
        Area area = new Area(player.getShape());
        area.intersect(monster.getShape());

        if (!area.isEmpty()) {
            player.reduceSpeed();
            return true;
        }
        return false;
    }

    private boolean checkPlayer(SlowMonster slowmonster) { // Overloading
        Area area = new Area(player.getShape());
        area.intersect(slowmonster.getShape());

        if (!area.isEmpty()) {
            player.reduceSpeed();
            return true;
        }
        return false;
    }

    private boolean checkPlayer(BossPurple bossPurple) {
        Area area = new Area(player.getShape());
        area.intersect(bossPurple.getShape());

        if (!area.isEmpty()) {
            player.reduceSpeed();
            return true;
        }
        return false;
    }

    private void loadBackgroundImage() {
        try {
            if (checkBoss) {
                backgroundImage = ImageIO.read(getClass().getResource("/game/image/background2.png"));
            } else {
                backgroundImage = ImageIO.read(getClass().getResource("/game/image/background1.png"));
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void initBullets() {
        gameExecutor.scheduleAtFixedRate(() -> {
            List<Bullet> bulletsToRemove = new ArrayList<>();

            for (Bullet bullet : bullets) {
                bullet.update();
                if (!bullet.check(width, height)) {
                    bulletsToRemove.add(bullet);
                }
            }

            bullets.removeAll(bulletsToRemove);

            List<Effect> effectsToRemove = new ArrayList<>();
            for (Effect effect : boomEffects) {
                effect.update();
                if (!effect.check()) {
                    effectsToRemove.add(effect);
                }
            }

            boomEffects.removeAll(effectsToRemove);
        }, 0, 5, TimeUnit.MILLISECONDS);
    }

    private void drawBackground() {
        g2.drawImage(backgroundImage, 0, 0, width, height, null);
    }

    private void drawBossHP() {
        int hpWidth = (int) (bossPurple.getHP() / 2);
        int hpHeight = 40;
        int x = 800;
        int y = 30;

        g2.setColor(Color.RED);
        g2.fillRect(x, y, hpWidth, hpHeight);
        g2.setColor(Color.BLACK);
        g2.drawRect(x, y, hpWidth, hpHeight);

        g2.setFont(getFont().deriveFont(Font.BOLD, 20f));
        g2.setColor(Color.WHITE);
        g2.drawString("Boss HP: " + bossPurple.getHP() + "/ 1000", x, y - 10);
    }

    private void drawGame() {
        synchronized (this) {
            if (player.isAlive()) {
                player.draw(g2);
            }

            for (Bullet bullet : bullets) {
                bullet.draw(g2);
            }

            for (monster1 monster : monsters) {
                monster.draw(g2);
            }

            for (SlowMonster slowMonster : slowmonsters) {
                slowMonster.draw(g2);
            }

            // วาดกระสุนของบอส
            for (BulletMonster bullet : bulletMonsters) {
                bullet.draw(g2);
            }

            // วาด BossPurple ถ้ามี
            if (bossPurple != null) {
                bossPurple.draw(g2);
                drawBossHP();  // เพิ่มการแสดงผล HP ของบอส
            }

            for (Effect boomEffect : boomEffects) {
                boomEffect.draw(g2);
            }
            if (!checkBoss) {
                g2.setColor(Color.WHITE);
                g2.setFont(getFont().deriveFont(Font.BOLD, 30f));
                g2.drawString("Score : " + score, 50, 50);
            }

            // เพิ่มการแสดงผลเวลาเฉพาะเฟรม Boss Level
            if (checkBoss && bossPurple != null) {
                long elapsedMillis = (System.nanoTime() - startTime) / 1_000_000;  // แปลงเป็นมิลลิวินาที
                long minutes = (elapsedMillis / 1000) / 60;
                long seconds = (elapsedMillis / 1000) % 60;
                long millis = elapsedMillis % 1000;

                String formattedTime = String.format("Time: %02d:%02d:%03d", minutes, seconds, millis);
                g2.drawString(formattedTime, 20, 50);
            }

            if (!player.isAlive()) {
                gameState = 1;
                displayGameOver(g2);

            }

            if (score >= maxScore) {
                if (!panelTransitionTriggered) {
                    panelTransitionTriggered = true; // ป้องกันการเปลี่ยนหน้าซ้ำ
                    gameState = 2;

                    // หยุดเกมก่อนเปลี่ยนหน้า
                    stop();

                    // เปลี่ยน Panel
                    SwingUtilities.invokeLater(this::goToNextPanel);
                }
            }
        }
    }

    private void goToNextPanel() {
        JFrame currentFrame = (JFrame) SwingUtilities.getWindowAncestor(this);
        if (currentFrame != null) {
            SwingUtilities.invokeLater(() -> {
                System.out.println("Starting to remove current panel.");
                currentFrame.getContentPane().removeAll();
                System.out.println("Removed current panel.");

                // สร้าง PanelGame ใหม่และกำหนดค่า
                PanelGame nextLevelPanel = new PanelGame();
                nextLevelPanel.initializeForBossBattle(); // กำหนดค่าให้เรียบร้อยก่อนเพิ่มเข้า JFrame

                // เพิ่ม Panel ใหม่ใน JFrame
                currentFrame.getContentPane().add(nextLevelPanel, BorderLayout.CENTER);

                // เรียก revalidate และ repaint เพื่ออัพเดต JFrame
                currentFrame.revalidate();
                currentFrame.repaint();
                System.out.println("Added new panel and refreshed UI.");

                // เริ่มเกมใน Panel ใหม่
                nextLevelPanel.start();
                System.out.println("Started next level.");
            });
        }
    }

    public void initializeForBossBattle() {
        checkBoss = true;  // ระบุว่าเป็นเฟรมบอสเลเวล

        player = new Player();
        player.changeLocation(150, 150);

        monsters.clear();
        slowmonsters.clear();
        bullets.clear();
        boomEffects.clear();

        bossPurple = new BossPurple();
        bossPurple.ChangeLocation(400, 200);

        startTime = System.nanoTime();
    }

// insert database 
    private void goToEndPanel() {
        long endTime = System.nanoTime();
        long elapsedTime = endTime - startTime;
        long elapsedSeconds = elapsedTime / 1_000_000_000;
        long minutes = elapsedSeconds / 60;
        long seconds = elapsedSeconds % 60;
        long millis = (elapsedTime / 1_000_000) % 1000;

        insertGameTime((int) minutes, (int) seconds, (int) millis);

        JFrame currentFrame = (JFrame) SwingUtilities.getWindowAncestor(this);
        if (currentFrame != null) {
            currentFrame.remove(this);

            Image tempImage = new ImageIcon(getClass().getResource("/game/image/winner.png")).getImage();
            BufferedImage backgroundImage = new BufferedImage(tempImage.getWidth(null), tempImage.getHeight(null), BufferedImage.TYPE_INT_ARGB);
            Graphics2D g2d = backgroundImage.createGraphics();
            g2d.drawImage(tempImage, 0, 0, null);
            g2d.dispose();

            JPanel endPanel = new JPanel(null) {
                @Override
                protected void paintComponent(Graphics g) {
                    super.paintComponent(g);
                    if (backgroundImage != null) {
                        g.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), this);
                    }

                    // Draw rectangle for statistics
                    g.setColor(new Color(255, 255, 0, 150)); // Yellow with transparency
                    g.fillRoundRect(20, 50, 240, 300, 10, 10); // Smaller rounded rectangle
                    g.setColor(Color.RED);
                    g.drawRoundRect(20, 50, 240, 300, 10, 10);
                }
            };

            // Displaying statistics (Top 5 times)
            List<String> topTimes = getTopTimes();
            JLabel statsLabel = new JLabel("<html>Top 5 Times:<br/>" + String.join("<br/>", topTimes) + "</html>");
            statsLabel.setBounds(40, 70, 200, 260);
            statsLabel.setFont(new Font("Arial", Font.BOLD, 24));
            statsLabel.setForeground(Color.BLACK);
            statsLabel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
            endPanel.add(statsLabel);

            // Congratulatory message
            JLabel endLabel = new JLabel("You Win the Boss!", SwingConstants.CENTER);
            endLabel.setFont(new Font("Arial", Font.BOLD, 42));
            endLabel.setForeground(Color.BLUE);
            endLabel.setBounds(400, 100, 600, 100);
            endPanel.add(endLabel);

            // Time taken display with red background
            JPanel timePanel = new JPanel();
            timePanel.setBackground(new Color(255, 0, 0, 150)); // Semi-transparent red
            timePanel.setBounds(500, 40, 400, 50);
            timePanel.setLayout(new BorderLayout());
            timePanel.setBorder(BorderFactory.createLineBorder(Color.RED, 2, true)); // Rounded border

            JLabel timeLabel = new JLabel(String.format("Time Taken: %02d:%02d:%03d", minutes, seconds, millis), SwingConstants.CENTER);
            timeLabel.setFont(new Font("Arial", Font.BOLD, 28));
            timeLabel.setForeground(Color.WHITE);
            timePanel.add(timeLabel);

            endPanel.add(timePanel);

            // Exit Game button with rounded borders
            JButton exitButton = new JButton("Exit Game") {
                @Override
                protected void paintComponent(Graphics g) {
                    super.paintComponent(g);
                    Graphics2D g2 = (Graphics2D) g;
                    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                    g2.setColor(getBackground());
                    g2.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
                    super.paintComponent(g2);
                }

                @Override
                protected void paintBorder(Graphics g) {
                    Graphics2D g2 = (Graphics2D) g;
                    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                    g2.setColor(Color.BLACK);
                    g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 10, 10);
                }
            };
            exitButton.setFont(new Font("Arial", Font.BOLD, 24));
            exitButton.setBackground(Color.RED);
            exitButton.setForeground(Color.WHITE);
            exitButton.setFocusPainted(false);
            exitButton.setBounds(currentFrame.getWidth() / 2 - 200, currentFrame.getHeight() - 200, 400, 80);
            exitButton.addActionListener(e -> System.exit(0));//exit game
            endPanel.add(exitButton);

            currentFrame.add(endPanel, BorderLayout.CENTER);
            currentFrame.revalidate();
            currentFrame.repaint();
        }
    }

    private List<String> getTopTimes() {
        List<String> topTimes = new ArrayList<>();
        String url = "jdbc:mysql://localhost:3306/oop";
        String user = "root";
        String password = "";
        String query = "SELECT minutes, seconds,millisecs FROM gametime ORDER BY (minutes * 60 + seconds) ASC LIMIT 5";

        try (Connection conn = DriverManager.getConnection(url, user, password); PreparedStatement stmt = conn.prepareStatement(query); ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                int minutes = rs.getInt("minutes");
                int seconds = rs.getInt("seconds");
                int millisecs = rs.getInt("millisecs");
                topTimes.add(String.format("%02d:%02d:%03d", minutes, seconds, millisecs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return topTimes;
    }

    private void insertGameTime(int minutes, int seconds, int millisecs) {
        String url = "jdbc:mysql://localhost:3306/oop";
        String user = "root";
        String password = "";

        String query = "INSERT INTO gametime (minutes, seconds,millisecs) VALUES (?, ?,?)";

        try (Connection conn = DriverManager.getConnection(url, user, password); PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setInt(1, minutes);
            stmt.setInt(2, seconds);
            stmt.setInt(3, millisecs);

            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void goToDefeatPanel() {
        JFrame currentFrame = (JFrame) SwingUtilities.getWindowAncestor(this);
        if (currentFrame != null) {
            currentFrame.remove(this);

            // โหลดรูปภาพพื้นหลัง
            Image tempImage = new ImageIcon(getClass().getResource("/game/image/lossPic.png")).getImage();
            BufferedImage backgroundImage = new BufferedImage(tempImage.getWidth(null), tempImage.getHeight(null), BufferedImage.TYPE_INT_ARGB);
            Graphics2D g2d = backgroundImage.createGraphics();
            g2d.drawImage(tempImage, 0, 0, null);
            g2d.dispose();

            // สร้าง Panel สุดท้าย
            JPanel endPanel = new JPanel(null) {
                @Override
                protected void paintComponent(Graphics g) {
                    super.paintComponent(g);
                    if (backgroundImage != null) {
                        g.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), this);
                    }
                }
            };

            // ปุ่มออกจากเกม
            JButton exitButton = new JButton("Exit Game") {
                @Override
                protected void paintComponent(Graphics g) {
                    super.paintComponent(g);
                    Graphics2D g2 = (Graphics2D) g;
                    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                    g2.setColor(getBackground());
                    g2.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
                    super.paintComponent(g2);
                }

                @Override
                protected void paintBorder(Graphics g) {
                    Graphics2D g2 = (Graphics2D) g;
                    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                    g2.setColor(Color.BLACK);
                    g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 10, 10);
                }
            };
            exitButton.setFont(new Font("Arial", Font.BOLD, 24));
            exitButton.setBackground(Color.RED);
            exitButton.setForeground(Color.WHITE);
            exitButton.setFocusPainted(false);
            exitButton.setBounds(currentFrame.getWidth() / 2 - 200, currentFrame.getHeight() - 200, 400, 80);
            exitButton.addActionListener(e -> System.exit(0)); // ปิดเกมทันที
            endPanel.add(exitButton);

            // เพิ่ม Panel สุดท้ายใน JFrame
            currentFrame.add(endPanel, BorderLayout.CENTER);
            currentFrame.revalidate();
            currentFrame.repaint();
        }
    }

    private void displayGameOver(Graphics2D g2) {
        String text = "Game OVER";
        String textKey = "Press key enter to Continue ...";

        g2.setFont(getFont().deriveFont(Font.BOLD, 50f));
        FontMetrics fm = g2.getFontMetrics();
        Rectangle2D r2 = fm.getStringBounds(text, g2);
        double textWidth = r2.getWidth();
        double textHeight = r2.getHeight();
        double x = (width - textWidth) / 2;
        double y = (height - textHeight) / 2;
        g2.drawString(text, (int) x, (int) y + fm.getAscent());
        g2.setFont(getFont().deriveFont(Font.BOLD, 15f));
        fm = g2.getFontMetrics();
        r2 = fm.getStringBounds(textKey, g2);
        textWidth = r2.getWidth();
        textHeight = r2.getHeight();
        x = (width - textWidth) / 2;
        y = (height - textHeight) / 2;
        g2.drawString(textKey, (int) x, (int) y + fm.getAscent() + 50);
    }

    private void render() {
        Graphics g = getGraphics();
        if (g != null) {
            g.drawImage(image, 0, 0, null);
            g.dispose();
        }
    }

    private void sleep(long speed) {
        try {
            Thread.sleep(speed);
        } catch (InterruptedException ex) {
            System.err.println(ex);
        }
    }

}
