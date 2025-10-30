package starshipshooter;
import java.awt.*;
import java.util.LinkedList;
import java.util.concurrent.CopyOnWriteArrayList;
import javax.swing.ImageIcon;

public class Player extends Thread {
    private final Image ps = new ImageIcon(getClass().getResource("image/playership.gif")).getImage();
    private Image explosionImg = new ImageIcon(getClass().getResource("image/explosion.gif")).getImage(); 
    final int width = 80, height = 80;
    
    private final CopyOnWriteArrayList<Laser> lasers;
    private int x, y, dx, dy;
    private int speed = 6;
    protected int health = 100;
    boolean alive = true;
    boolean exploding = false;
    private boolean running = true;
    private boolean dashing = false;
    final LinkedList<Trail> trails = new LinkedList<>();
    private boolean canDash = true;
    private int lastDx = 0, lastDy = 0;
    private final gameRun parentPanel;

    public Player(int startX, int startY, CopyOnWriteArrayList<Laser> lasers, gameRun parentPanel) {
        this.x = startX;
        this.y = startY;
        this.lasers = lasers;
        this.parentPanel = parentPanel;
    }

    public void shoot() {
        lasers.add(new Laser(x + width, y + height / 2));
    }
    public void resetHealth() { health = 100; }
    
    public int getHealth(){ return health; }
    
    public void setDx(int dx) { this.dx = dx; }
    
    public void setDy(int dy) { this.dy = dy; }
    
    public int getSpeed() { return speed; }
    
    public Rectangle getBounds() { return new Rectangle(x, y, width, height); }
    
    public boolean Isalive() { return alive; }
    
    public void takeDamage(int dmg) {
        if (!alive) return;
        health -= dmg;
        if (health <= 0) {
            health = 0;
            die();
        }
    }
    
    private void die() { alive = false; exploding = true; }
    
    private void updateTrails() {
        synchronized (trails) {
            trails.removeIf(t -> t.life <= 0);
            for (Trail t : trails) t.fade();
        }
    }

    
    private void move() {
        x += dx;
        y += dy;
        if (dx != 0 || dy != 0) { lastDx = dx; lastDy = dy; }

        if (parentPanel != null) {
            x = Math.max(0, Math.min(x, parentPanel.getWidth() - width));
            y = Math.max(0, Math.min(y, parentPanel.getHeight() - height));
        }
    }    
    
    @Override
    public void run() {
        while (running) {
            move();
            updateTrails();
            try { Thread.sleep(20); } catch (InterruptedException e) { running = false; }
        }
    }
    
    

    public void draw(Graphics g) {
        if (exploding) g.drawImage(explosionImg, x, y, width, height, null);
        else g.drawImage(ps, x, y, width, height, null);
    }

    
    //Dash method 
    public void dash() {
        if (!canDash || dashing) return;
        new Thread(() -> {
            dashing = true;
            canDash = false;
            int normalSpeed = speed;
            speed = 15;

            int dirX = (dx == 0 && dy == 0) ? lastDx : Integer.signum(dx);
            int dirY = (dx == 0 && dy == 0) ? lastDy : Integer.signum(dy);
            if (dirX == 0 && dirY == 0) { dirX = 1; dirY = 0; }
            
            // for logic is i < dash range
            for (int i = 0; i < 12; i++) {
                synchronized (trails) { trails.add(new Trail(x, y)); }
                x += dirX * 18; // can change dash speed hear
                y += dirY * 18;
                if (parentPanel != null) {
                    x = Math.max(0, Math.min(x, parentPanel.getWidth() - width));
                    y = Math.max(0, Math.min(y, parentPanel.getHeight() - height));
                }
                parentPanel.repaint();
                try { Thread.sleep(16); } catch (InterruptedException ignored) {}
            }

            speed = normalSpeed;
            dashing = false;
            try { Thread.sleep(800); } catch (InterruptedException ignored) {}
            canDash = true;
        }).start();
    }

    public void stopPlayer() { running = false; }

    

    public class Trail {
        int x, y; float alpha = 0.8f; int life = 20;
        Trail(int x, int y) { this.x = x; this.y = y; }
        void fade() { life--; alpha -= 0.04f; if (alpha < 0) alpha = 0; }
    }
}

class Laser {
    int x, y, width = 13, height = 2;
    int speed = 15;
    boolean active = true;
    public Laser(int startX, int startY){
        this.x = startX; this.y = startY; 
    }
    public void move(){
        x += speed; if (x < 0) active = false; 
    }
    public void draw(Graphics g){
        g.setColor(Color.RED); g.fillRect(x, y, width, height); 
    }
    public Rectangle getBounds(){ 
        return new Rectangle(x, y, width, height); 
    }
}