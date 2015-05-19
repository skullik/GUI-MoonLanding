package gui.moonlanding;

/**
 *
 * @author 
 */
import java.awt.BorderLayout;

import javax.swing.JFrame;

public class GUIMoonLanding extends javax.swing.JFrame {

    public static MoonLandingGame screen;

    public GUIMoonLanding() {
        super("GUI - Moon Landing");

        screen = new MoonLandingGame(); //zavolani hlavni metody
        setLayout(new BorderLayout());
        add(screen, BorderLayout.CENTER);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        pack(); //ukotveni komponent
        setResizable(false);
        setLocationRelativeTo(null); //okno na stred
        setVisible(true);
        this.addKeyListener(screen);
    }

    public void repaint() {
        screen.repaint();
    }

    public static void main(String[] args) {
        new GUIMoonLanding();
    }
}