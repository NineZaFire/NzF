package starshipshooter;
import java.awt.event.*;
import javax.swing.*;

public class Starshipshooter extends JFrame implements ActionListener {
    MainMenu homeMenu;
    static gameRun stageloop;
    static Starshipshooter gamerun;
    // ========= Main ============
    public static void main(String[] args) {
         gamerun = new Starshipshooter();
    }

    public Starshipshooter() {
        homeMenu = new MainMenu();
        stageloop = new gameRun();
        setTitle("Starship Shooter");
        setSize(1100, 700);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);

        add(homeMenu);
        homeMenu.start.addActionListener(this);
        homeMenu.quit.addActionListener(this);

        setVisible(true);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == homeMenu.start) {
            remove(homeMenu);
            add(stageloop);
            revalidate();
            repaint();
        } else if (e.getSource() == homeMenu.quit) {
            System.exit(0);
        }
    }
}