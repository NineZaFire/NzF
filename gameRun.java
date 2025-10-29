package starshipshooter;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.LinkedList;
import java.util.concurrent.CopyOnWriteArrayList;

class gameRun extends JPanel {
    private final Image bg = new ImageIcon(getClass().getResource("image/blinking_stars.gif")).getImage();
    private final Image ps = new ImageIcon(getClass().getResource("image/playership.gif")).getImage();
    private final Image heart = new ImageIcon(getClass().getResource("image/heart.gif")).getImage();
    private int x1 = 0, speed = 1;
    private Timer timerBG, timerE, timerPL, timerCollision;
    private Player player;
    protected int playerDMG = 1, enemyDMG = 1, bossDMG = 3; 

    private int stage = 1;
    private boolean bossAlive = false;
    private long stageStartTime;
    private int stageDuration = 40000;


    private CopyOnWriteArrayList<Laser> lasers = new CopyOnWriteArrayList<>();
    private CopyOnWriteArrayList<enemyLaser> enemyLasers = new CopyOnWriteArrayList<>();
    private CopyOnWriteArrayList<bossLaser> bossLasers = new CopyOnWriteArrayList<>();
    private LinkedList<Enemy> enemies = new LinkedList<>();
    private long lastSpawnTime = 0;

    public gameRun() {
        setFocusable(true);
        setDoubleBuffered(true);
        setLayout(null);
        player = new Player(200, 290, lasers, this);

        timerBG = new Timer(16, e -> moveBackground()); // print background and move from right to left 
        
        timerE = new Timer(16, e -> {
            spawnEnemies();
            updateEnemies();
            checkStageTimer();
            repaint();
        });
        timerPL = new Timer(16, e -> {
            for (Laser l : lasers) {
                l.move();
                if (!l.active) lasers.remove(l);
            }
            for (enemyLaser el : enemyLasers) {
                el.move();
                if (!el.active) enemyLasers.remove(el);
            }
            for (bossLaser bl : bossLasers) {
                bl.move();
                if (!bl.active) bossLasers.remove(bl);
            }
            repaint();
        });
        timerCollision = new Timer(20, e -> checkCollisions());

        setupKeyBindings();
        requestFocusInWindow();
    }
    
    //spawn normal enemy 
    private void spawnEnemies() {
        int w = getWidth(), h = getHeight();
        if (w <= 10 || h <= 10) return;

        long now = System.currentTimeMillis();
        if (now - lastSpawnTime < 1000) return; //spawn every 1 sec.

        double chance = Math.random(); // spawn change
        if (chance < 0.70) {
            Enemy e = new Enemy(w, h);
            e.setRefs(player, this, enemyLasers);
            enemies.add(e);
        } 
        lastSpawnTime = now;
    }
    
    // spawn boss 
    private void spawnBoss() {
        int w = getWidth(), h = getHeight();
        BossEnemy b = new BossEnemy(w, h); //create new boss entity, input pannel size 
        b.setRef(player, this, bossLasers);
        enemies.add(b);
    }
    
    // enemy alive status update 
    private void updateEnemies() {
        for (Enemy e : enemies) e.update();
        enemies.removeIf(e -> !e.isAlive() && !(e instanceof BossEnemy));
    }

    private void checkCollisions() {
        // laser player shoot enemy
        for (Laser l : lasers) {
            for (Enemy e : enemies) {
                if (l.active && e.isAlive() && l.getBounds().intersects(e.getBounds())) {
                    l.active = false;
                    e.takeDamage(playerDMG);
                }
            }
        }

        // enemyLaser shoot player
        for (enemyLaser el : enemyLasers) {
            if (el.active && player.getBounds().intersects(el.getBounds())) {
                el.active = false;
                player.takeDamage(enemyDMG);
            }
        }

        // bossLaser shoot player
        for (bossLaser bl : bossLasers) {
            if (bl.active && player.getBounds().intersects(bl.getBounds())) {
                bl.active = false;
                player.takeDamage(bossDMG);
            }
        }
        
        // if boss die regen player hp and upgrade atk and boss health
        for (Enemy e : enemies) {
            if (e instanceof BossEnemy boss && !boss.isAlive() && bossAlive) {
                bossAlive = false;
                player.resetHealth();
                playerDMG += 2;
                enemyDMG += 2;
                bossDMG += 3;
                BossEnemy.healthBuff += 8;
                enemies.remove(boss);
                break;
            }
        }
        
        // if player hp = 0, print explosion and lead to Gameover
        if (!player.Isalive()) {
            timerBG.stop();
            timerE.stop();
            timerPL.stop();
            player.stopPlayer();
            player.explode(); 
            new javax.swing.Timer(1500, evt -> { 
                ((Timer)evt.getSource()).stop();
                timerCollision.stop();
                gameOver();
            }).start();
        }
    }

    private void nextStage() {
        stage++;
        if ((stage - 2)% 3 == 0) {
            Enemy.baseSpeed++;
        }

        if (stage % 3 == 0) {
            bossAlive = true;
            spawnBoss();
        }
    }
    
    // when  
    private void checkStageTimer() {
        long now = System.currentTimeMillis();
        if (now - stageStartTime >= stageDuration) {
            stageStartTime = now;
            nextStage();
        }
    }

    private void gameOver() {
        SwingUtilities.invokeLater(() -> {
            JFrame topFrame = (JFrame) SwingUtilities.getWindowAncestor(this);
            topFrame.getContentPane().removeAll();
            topFrame.add(new GameOver());
            topFrame.revalidate();
            topFrame.repaint();
        });
    }
    
    

    @Override
    public void addNotify() {
        super.addNotify();
        x1 = 0;
        timerBG.start();
        timerE.start();
        timerPL.start();
        timerCollision.start();
        player.start();
        stageStartTime = System.currentTimeMillis();
    }

    @Override
    public void removeNotify() {
        super.removeNotify();
        timerBG.stop();
        timerE.stop();
        timerPL.stop();
        timerCollision.stop();
        player.stopPlayer();
    }

    private void moveBackground() {
        int w = getWidth();
        if (w <= 0) return;
        x1 = (x1 - speed) % w;
        if (x1 > 0) x1 -= w; 
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        int w = getWidth(), h = getHeight();
        g.drawImage(bg, x1, 0, w, h, this);
        g.drawImage(bg, x1 + w, 0, w, h, this);

        Graphics2D g2 = (Graphics2D) g;
        synchronized (player.trails) {
            for (Player.Trail t : player.trails) {
                g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, t.alpha));
                g2.drawImage(ps, t.x, t.y, player.width, player.height, null);
            }
        }
        g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1f));

        player.draw(g);
        for (Laser l : lasers) l.draw(g);
        for (enemyLaser el : enemyLasers) el.draw(g);
        for (bossLaser bl : bossLasers) bl.draw(g);
        for (Enemy e : enemies) e.draw(g);

        g2.drawImage(heart, 20, 20, 30, 30, this);
        g2.setFont(new Font("Arial", Font.BOLD, 24));
        g2.setColor(Color.WHITE);
        g2.drawString("x " + player.getHealth(), 60, 45);

        String stageText = "STAGE: " + stage;
        int textWidth = g2.getFontMetrics().stringWidth(stageText);
        g2.drawString(stageText, getWidth() - textWidth - 20, 45);
    }
    
    // detect key used inputmap and react with actionmap 
    private void setupKeyBindings() {
        InputMap im = getInputMap(WHEN_IN_FOCUSED_WINDOW);
        ActionMap am = getActionMap();

        im.put(KeyStroke.getKeyStroke(KeyEvent.VK_A, 0, false), "aPressed");
        im.put(KeyStroke.getKeyStroke(KeyEvent.VK_A, 0, true), "aReleased");
        im.put(KeyStroke.getKeyStroke(KeyEvent.VK_D, 0, false), "dPressed");
        im.put(KeyStroke.getKeyStroke(KeyEvent.VK_D, 0, true), "dReleased");
        im.put(KeyStroke.getKeyStroke(KeyEvent.VK_W, 0, false), "wPressed");
        im.put(KeyStroke.getKeyStroke(KeyEvent.VK_W, 0, true), "wReleased");
        im.put(KeyStroke.getKeyStroke(KeyEvent.VK_S, 0, false), "sPressed");
        im.put(KeyStroke.getKeyStroke(KeyEvent.VK_S, 0, true), "sReleased");
        im.put(KeyStroke.getKeyStroke(KeyEvent.VK_C, 0, false), "cPressed");
        im.put(KeyStroke.getKeyStroke(KeyEvent.VK_SPACE, 0, false), "shoot");
        
        am.put("aPressed", new AbstractAction() { public void actionPerformed(ActionEvent e) { player.setDx(-player.getSpeed()); } });
        am.put("aReleased", new AbstractAction() { public void actionPerformed(ActionEvent e) { player.setDx(0); } });
        am.put("dPressed", new AbstractAction() { public void actionPerformed(ActionEvent e) { player.setDx(player.getSpeed()); } });
        am.put("dReleased", new AbstractAction() { public void actionPerformed(ActionEvent e) { player.setDx(0); } });
        am.put("wPressed", new AbstractAction() { public void actionPerformed(ActionEvent e) { player.setDy(-player.getSpeed()); } });
        am.put("wReleased", new AbstractAction() { public void actionPerformed(ActionEvent e) { player.setDy(0); } });
        am.put("sPressed", new AbstractAction() { public void actionPerformed(ActionEvent e) { player.setDy(player.getSpeed()); } });
        am.put("sReleased", new AbstractAction() { public void actionPerformed(ActionEvent e) { player.setDy(0); } });
        am.put("cPressed", new AbstractAction() { public void actionPerformed(ActionEvent e) { player.dash(); } });
        am.put("shoot", new AbstractAction() { public void actionPerformed(ActionEvent e) { player.shoot(); } });
    }
}
