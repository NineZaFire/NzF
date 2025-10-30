package starshipshooter;
import javax.swing.*;
import java.awt.*;
import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;

public class Enemy implements Runnable {
    
    protected static int baseSpeed = 3;
    protected int x, y, width, height, speed, dx = 0, dy = 0;
    protected int health = 1;
    protected Image img;
    protected Image explosionImg;
    protected boolean alive = true;
    protected boolean exploding = false;
    protected int minX, maxX;
    protected Random rand = new Random();
    private long lastShotTime = 0;
    private Thread explosionThread;

    protected CopyOnWriteArrayList<enemyLaser> enemyLasers;
    protected Player player;
    protected gameRun gamePanel;

    public Enemy(int panelWidth, int panelHeight) {
        width = 80;
        height = 80;
        speed = baseSpeed;
        x = panelWidth - width;
        y = rand.nextInt(Math.max(50, panelHeight - height - 50));
        img = new ImageIcon(getClass().getResource("image/normalEnemy.gif")).getImage();
        explosionImg = new ImageIcon(getClass().getResource("image/explosion.gif")).getImage();
        minX = panelWidth / 2;
        maxX = panelWidth - width;
    }

    public void setRefs(Player player, gameRun gamePanel, CopyOnWriteArrayList<enemyLaser> enemyLasers) {
        this.player = player;
        this.gamePanel = gamePanel;
        this.enemyLasers = enemyLasers;
    }

    public void update() {
        if (!alive && !exploding) return;
        if (exploding) return;
        move();
        Shoot();
    }

    protected void move() {
        if (rand.nextInt(20) == 0) {
            dx = rand.nextInt(3) - 1;
            dy = rand.nextInt(3) - 1;
        }
        x += dx * speed;
        y += dy * speed;
        if (x < minX) { x = minX; dx = Math.abs(dx); }
        if (x > maxX) { x = maxX; dx = -Math.abs(dx); }
        if (y < 0 || y > gamePanel.getHeight() - height) dy = -dy;
    }

    protected void Shoot() {
        long now = System.currentTimeMillis();
        if (now - lastShotTime > 1000 && rand.nextDouble() < 0.02) {
            enemyLasers.add(new enemyLaser(x, y + height / 2));
            lastShotTime = now;
        }
    }

    public void takeDamage(int dmg) {
        if (!alive || exploding) return;
        health -= dmg;
        if (health <= 0) {
            alive = false;
            exploding = true;
            explosionThread = new Thread(this);
            explosionThread.start();
        }
    }

    public boolean isAlive() { return alive || exploding; }

    public Rectangle getBounds() { return new Rectangle(x, y, width, height); }

    public void draw(Graphics g) {
        if (exploding) g.drawImage(explosionImg, x, y, width, height, null);
        else g.drawImage(img, x, y, width, height, null);
    }

    @Override
    public void run() {
        try { Thread.sleep(400); } catch (InterruptedException ignored) {}
        exploding = false;
    }
}


//  BossEnemy Class

class BossEnemy extends Enemy {
    private long lastShotTimeBoss = 0;
    private CopyOnWriteArrayList<bossLaser> bossLasers;
    protected static int healthBuff = 1;
    public BossEnemy(int panelWidth, int panelHeight) {
        super(panelWidth, panelHeight);
        width = 160;
        height = 160;
        speed = 2;
        health = 38 + healthBuff;
        x = panelWidth - width - 50;
        y = rand.nextInt(Math.max(100, panelHeight - height - 100));
        img = new ImageIcon(getClass().getResource("image/bossEnemy.gif")).getImage();
    }
    public void setbossRef(Player player, gameRun gamePanel, CopyOnWriteArrayList<bossLaser> bossLasers) {
        this.player = player;
        this.gamePanel = gamePanel;
        this.bossLasers = bossLasers;
    }
    
    @Override
    protected void Shoot() {
        long now = System.currentTimeMillis();
        if (now - lastShotTimeBoss > 700) {
            bossLasers.add(new bossLaser(x, y + height / 2 - 10));
            bossLasers.add(new bossLaser(x, y + height / 2 + 10));
            lastShotTimeBoss = now;
        }
    }
    @Override
    public void takeDamage(int dmg) {
        if (!alive || exploding) return;
        health -= dmg;
        if (health <= 0) {
            alive = false;
            exploding = true;
            new Thread(this).start();
        }
    }
}

// enemyLaser Class
class enemyLaser {
    int x, y, width = 10, height = 3;
    int speed = 15;
    boolean active = true;

    public enemyLaser(int startX, int startY) {
        this.x = startX;
        this.y = startY;
    }

    public void move() {
        x -= speed;
        if (x < 0) active = false;
    }

    public void draw(Graphics g) {
        g.setColor(Color.RED);
        g.fillRect(x, y, width, height);
    }

    public Rectangle getBounds() {
        return new Rectangle(x, y, width, height);
    }
}


// bossLaser Class
class bossLaser {
    int x, y, width = 60, height = 10;
    int speed = 20;
    boolean active = true;

    public bossLaser(int startX, int startY) {
        this.x = startX;
        this.y = startY;
    }

    public void move() {
        x -= speed;
        if (x < 0) active = false;
    }

    public void draw(Graphics g) {
        g.setColor(Color.MAGENTA);
        g.fillRect(x, y, width, height);
    }

    public Rectangle getBounds() {
        return new Rectangle(x, y, width, height);
    }
}