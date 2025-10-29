package starshipshooter;
import java.awt.event.*;
import javax.swing.*;

public class Starshipshooter extends JFrame implements ActionListener {
    MainMenu homeMenu = new MainMenu();
    gameRun stageloop = new gameRun();
    
    // ========= Main ============
    public static void main(String[] args) {
        Starshipshooter gamerun = new Starshipshooter();
    }

    public Starshipshooter() {
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
