package gui.moonlanding;

/**
 *
 * @author Pavel
 */
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Point;
import java.awt.Polygon;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.geom.Rectangle2D;
import java.util.Random;
import javax.swing.ImageIcon;
import javax.swing.JPanel;

public class MoonLandingGame extends JPanel implements KeyListener {

    double startTime = System.currentTimeMillis();
    int fuel = 600;
    int x = 0;
    int y = 0;
    double gravityShift = 1;
    boolean touchDown;
    boolean first;
    boolean timerSet;
    boolean restart;
    boolean jets;
    
    int randomNum = randInt(0, 580);
    
    private Image shipImg;
    public void loadShip() {
        shipImg = new ImageIcon("/Users/drofa/NetBeansProjects/GUI-MoonLanding/src/obr/ship.png").getImage();
    }
    
    public void loadJetShip() {
        shipImg = new ImageIcon("/Users/drofa/NetBeansProjects/GUI-MoonLanding/src/obr/shipjets.png").getImage();
    }    

    public void loadCrash() {
        shipImg = new ImageIcon("/Users/drofa/NetBeansProjects/GUI-MoonLanding/src/obr/crash.png").getImage();
    }    

    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        //pozadi
        g.setColor(Color.BLACK);
        g.fillRect(0, 0, getWidth(), getHeight());

        //instrukce
        g.setColor(Color.LIGHT_GRAY);
        g.drawString("Ovládání: |←| |→| mezerník trysky", 374, 20);

        //teren
        Polygon ground = groundPolygon();
        g.fillPolygon(ground);

        //palivo
        drawFuel(g);

        //docasne promenne pro detekci kolize a prekreslovani grafiky
        if (!touchDown) {
            y += (int) gravityShift;
        }
        int tx = x;
        int ty = y;

        //raketa
        //g.setColor(Color.CYAN);
        Polygon ship = shipPolygon();
        tx = x + randomNum;
        String collision = detectCollision(ship, ground, tx, ty);
        //podminky prekreslovani rakety
        if (jets == true && !collision.equals("landed")) {
            loadJetShip();
            jets = false;
        } else if (collision.equals("crashed")) {
            loadCrash();
            tx = tx - 35;
            ty = ty - 40;
        } else {
            loadShip();
        }
        g.drawImage(shipImg, tx, ty, null);
        g.translate(tx, ty);
        //g.fillPolygon(ship);  //vybarveni polygonu lodi
        g.translate(0 - tx, 0 - ty);

        //detekce kolize/pristani
        if (collision.equals("crashed")) {
            crashed(g, tx);
        } else if (collision.equals("landed")) {
            landed(g);
        }

        //novy thread
        if (!timerSet) {
            timerSet = true;
            restart = false;
            new Timer().start();
        }
    }

    private void drawFuel(Graphics g) {
        if (fuel > 300) {
            g.setColor(Color.GREEN);
        } else if (fuel > 240) {
            g.setColor(Color.YELLOW);
        } else if (fuel > 150) {
            g.setColor(Color.ORANGE);
        } else {
            g.setColor(Color.RED);
        }
        g.fillRect(580, 600 - (int) (fuel / 2), 20, (int) fuel / 2);
    }

    private void landed(Graphics g) {
        double estimatedTime = System.currentTimeMillis() - startTime;
        g.setColor(Color.GREEN);
        g.drawString("Úspěšné přistání, stiskněte 'R' pro nový let", 162, 210);
        g.drawString("Váš let trval: " + estimatedTime/1000 + "s", 162, 225);
    }

    private void crashed(Graphics g, int tx) {
        g.setColor(Color.RED);
        g.drawString("Havárie, stiskněte 'R' po nový let", 192, 210);
        //g.fillArc(tx - 35 , y + 50 , 110, 50, 0, 360); //exploze polygon
    }

    /**
     * Detects if and how the ship hits the ground. A bounding box is made to
 represent the ship. The bottom left and bottom right points of the box
 are used to test for collisions. If both corners touchdown at the same
 time and the craft is not travelling too fast then it is a successful
 landing.
     *
     * @param lander
     * @param ground
     * @param x
     * @param y
     * @return either "", "crash", "landed"
     */
    private String detectCollision(Polygon lander, Polygon ground, int x, int y) {
        Rectangle2D landerBoundingBox = lander.getBounds2D();

        Point lp = new Point(x, y + (int) landerBoundingBox.getMaxY() + 2); //- (int)gravityShift);
        Point rp = new Point(x + (int) landerBoundingBox.getMaxX(), y + (int) landerBoundingBox.getMaxY() + 2); //- (int) gravityShift);

        //if both feet don't land at the same time then it is counted as a crash
        if ((ground.contains(lp) && !ground.contains(rp))
                || (ground.contains(rp) && !ground.contains(lp))) {
            touchDown = true;
            return "crashed";
        }   //if both feet land at the same time
        else if (ground.contains(lp) && ground.contains(rp)) {
            touchDown = true;
            if (gravityShift > 17) {
                return "crashed";   //check if going too fast
            }
            return "landed";
        }
        return "";
    }

    public Polygon groundPolygon() {
        int[] groundXS = {0, 30, 40, 100, 140, 160, 180, 200, 220, 230, 300,
            310, 330, 350, 360, 400, 410, 435, 460, 465, 500, 545, 560,
            575, 580, 600, 600, 0};
        int[] groundYS = {500, 450, 480, 510, 350, 400, 395, 480, 490, 480,
            480, 520, 515, 520, 515, 550, 400, 350, 360, 400, 410, 480,
            455, 465, 480, 500, 600, 600};
        return new Polygon(groundXS, groundYS, groundXS.length);
    }

    public Polygon shipPolygon() {
        int[] shipXS = {0, 48, 48, 0};
        int[] shipYS = {0, 0, 68, 68};
        return new Polygon(shipXS, shipYS, shipXS.length);
    }

    @Override
    public Dimension getPreferredSize() {
        Dimension dimension = new Dimension(600, 600);
        return dimension;
    }
    
    public static int randInt(int min, int max) {
    Random rand = new Random();
    int randomNum = rand.nextInt((max - min) + 1) + min;
    return randomNum;
    }    
    
    public class Timer extends Thread {

        public void run() {
            try {
                while (true) {
                    if (touchDown || restart) {
                        this.suspend();
                    }
                    Thread.sleep(100); //0.1s zpozdeni

                    //zvyseni rychlosti kdyz se lod pohybuje
                    gravityShift = gravityShift * 1.08;
                    GUIMoonLanding.screen.repaint(); //preklesleni sceny
                }
            } catch (InterruptedException e) {
                System.out.println(e);
            }
        }
    }

    @Override
    public void keyPressed(KeyEvent e) {
        int code = e.getKeyCode();
        if (code == KeyEvent.VK_RIGHT || code == KeyEvent.VK_KP_RIGHT) {
            x = x + 7; //pohyb lodi doprava
            fuel -= 2;
        } else if (code == KeyEvent.VK_LEFT || code == KeyEvent.VK_KP_LEFT) {
            x = x - 7; //pohyb lodi doleva
            fuel -= 2;
        } else if (code == KeyEvent.VK_UP || code == KeyEvent.VK_KP_UP || code == KeyEvent.VK_SPACE) {	//thruster
            fuel -= 10;
            if (fuel > 0) {
                jets = true;
            }
            if (gravityShift > 5 && fuel > 0) {
                gravityShift = gravityShift * 0.85;
            }
        } else if (code == KeyEvent.VK_R) {   //restart
            restart = true;
            startTime = System.currentTimeMillis(); //restart casovace
            System.out.println("Nová hra");
            x = 0;
            y = 0;
            randomNum = randInt(0, 580);
            gravityShift = 1;
            touchDown = false;
            first = false;
            timerSet = false;
            GUIMoonLanding.screen.repaint();
            fuel = 600;
        } else if (code == KeyEvent.VK_ESCAPE) { //konec
            System.exit(0);
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {
    }

    @Override
    public void keyTyped(KeyEvent e) {
    }
}