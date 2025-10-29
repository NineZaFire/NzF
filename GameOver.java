package starshipshooter;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;


public class GameOver extends JPanel implements ActionListener {
    private final JButton retryButton;
    private final JButton quitButton;
    private final Image bg = new ImageIcon(getClass().getResource("image/blinking_stars.gif")).getImage();
    private final Image retryImg = new ImageIcon(getClass().getResource("image/retry.png")).getImage();
    private final Image quitImg = new ImageIcon(getClass().getResource("image/Quit.png")).getImage();

    public GameOver() {
        setLayout(new GridBagLayout());
        
        Image scaledRetry = retryImg.getScaledInstance(180, -1, Image.SCALE_SMOOTH);
        Image scaledQuit = quitImg.getScaledInstance(180, -1, Image.SCALE_SMOOTH);

        JLabel gameOverText = new JLabel("GAME OVER");
        gameOverText.setFont(new Font("Arial", Font.BOLD, 80));
        gameOverText.setForeground(Color.RED);

        retryButton = new JButton(new ImageIcon(scaledRetry));
        quitButton = new JButton(new ImageIcon(scaledQuit));

        ButtonTransparent(retryButton);
        ButtonTransparent(quitButton);

        JPanel buttonPanel = new JPanel(new GridLayout(1, 2, 30, 0));
        buttonPanel.setOpaque(false);
        buttonPanel.add(retryButton);
        buttonPanel.add(quitButton);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(20, 0, 20, 0);
        gbc.gridx = 0; gbc.gridy = 0;
        add(gameOverText, gbc);

        gbc.gridy = 1;
        add(buttonPanel, gbc);

        retryButton.addActionListener(this);
        quitButton.addActionListener(this);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.drawImage(bg, 0, 0, getWidth(), getHeight(), this);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        JFrame topFrame = (JFrame) SwingUtilities.getWindowAncestor(this);
        if (e.getSource() == retryButton) {
            topFrame.getContentPane().removeAll();
            topFrame.add(new gameRun());
            topFrame.revalidate();
            topFrame.repaint();
        } else if (e.getSource() == quitButton) {
            System.exit(0);
        }
    }

        private void ButtonTransparent(JButton button) {
        button.setBorderPainted(false);
        button.setContentAreaFilled(false);
        button.setFocusPainted(false);
        button.setOpaque(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
    }
    
}