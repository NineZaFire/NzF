package starshipshooter;
import javax.swing.*;
import java.awt.*;

class MainMenu extends JPanel {
    private final Image bg = new ImageIcon(getClass().getResource("image/blinking_stars.gif")).getImage();
    private final Image logoImg = new ImageIcon(getClass().getResource("image/Starshipshooter_Icon.png")).getImage();
    private final Image startImg = new ImageIcon(getClass().getResource("image/Start.png")).getImage();
    private final Image quitImg = new ImageIcon(getClass().getResource("image/Quit.png")).getImage();

    JButton start;
    JButton quit;

    MainMenu() {
        setLayout(new GridBagLayout());
        setOpaque(true);

        // ---------- edit size button-logo ----------
        Image scaledLogo = logoImg.getScaledInstance(500, -1, 100);
        Image scaledStart = startImg.getScaledInstance(180, -1, Image.SCALE_SMOOTH);
        Image scaledQuit = quitImg.getScaledInstance(180, -1, Image.SCALE_SMOOTH);

        JLabel logoLabel = new JLabel(new ImageIcon(scaledLogo));

        start = new JButton(new ImageIcon(scaledStart));
        quit = new JButton(new ImageIcon(scaledQuit));

        // ---------- set button tranparant ----------
        makeButtonTransparent(start);
        makeButtonTransparent(quit);

        // ---------- Panel button ----------
        JPanel buttonPanel = new JPanel(new GridLayout(2, 1, 0, 20));
        buttonPanel.setOpaque(false);
        buttonPanel.add(start);
        buttonPanel.add(quit);

        // ---------- Layout ----------
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.CENTER;
        add(logoLabel, gbc);

        gbc.gridy = 1;
        gbc.insets = new Insets(0, 0, 0, 0);
        add(buttonPanel, gbc);
    }

    private void makeButtonTransparent(JButton button) {
        button.setBorderPainted(false);
        button.setContentAreaFilled(false);
        button.setFocusPainted(false);
        button.setOpaque(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.drawImage(bg, 0, 0, getWidth(), getHeight(), this);
    }
}