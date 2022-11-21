package it.unibo.oop.reactivegui03;

import java.awt.Dimension;
import java.awt.Toolkit;
import java.lang.reflect.InvocationTargetException;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

/**
 * Third experiment with reactive gui.
 */
public final class AnotherConcurrentGUI extends JFrame {
    private static final long serialVersionUID = 1L;
    private static final double WIDTH_PERC = 0.2;
    private static final double HEIGHT_PERC = 0.1;
    private final JLabel display = new JLabel();
    private final JButton stop = new JButton("stop");
    private final JButton up = new JButton("up");
    private final JButton down = new JButton("down");
    
    public AnotherConcurrentGUI() {
        super();
        final Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        this.setSize((int) (screenSize.getWidth() * WIDTH_PERC), (int) (screenSize.getHeight() * HEIGHT_PERC));
        this.setDefaultCloseOperation(EXIT_ON_CLOSE);
        final JPanel panel = new JPanel();
        panel.add(display);
        panel.add(up);
        panel.add(down);
        panel.add(stop);
        this.getContentPane().add(panel);
        this.setVisible(true);
        
        final Agent agent = new Agent();
        new Thread(agent).start();
        new Thread(() -> {
            try {
                Thread.sleep(10000);
                SwingUtilities.invokeAndWait(() -> agent.stopCounting());
                SwingUtilities.invokeAndWait(() -> AnotherConcurrentGUI.this.stop.setEnabled(false));
                SwingUtilities.invokeAndWait(() -> AnotherConcurrentGUI.this.up.setEnabled(false));
                SwingUtilities.invokeAndWait(() -> AnotherConcurrentGUI.this.down.setEnabled(false));
            } catch (InterruptedException | InvocationTargetException ex) {
                JOptionPane.showMessageDialog(display, ex, "Error", JOptionPane.ERROR_MESSAGE);
                ex.printStackTrace();
            }
            
        }).start();
        
        up.addActionListener((e) -> agent.upCounting());
        down.addActionListener((e) -> agent.downCounting());
        stop.addActionListener((e) -> {
            agent.stopCounting();
            up.setEnabled(false);
            down.setEnabled(false);
        });
    }

    /*
     * The counter agent is implemented as a nested class. This makes it
     * invisible outside and encapsulated.
     */
    private class Agent implements Runnable {
        private volatile boolean stop;
        private volatile boolean up;
        private int counter;

        @Override
        public void run() {
            while (!this.stop) {
                try {
                    final var nextText = Integer.toString(this.counter);
                    SwingUtilities.invokeAndWait(() -> AnotherConcurrentGUI.this.display.setText(nextText));
                    if (this.up) {
                        this.counter++;
                    } else {
                        this.counter--;
                    }
                    Thread.sleep(100);
                } catch (InvocationTargetException | InterruptedException ex) {
                    JOptionPane.showMessageDialog(display, ex, "Error", JOptionPane.ERROR_MESSAGE);
                    ex.printStackTrace();
                }
            }
        }

        public void stopCounting() {
            this.stop = true;
        }

        public void upCounting() {
            this.up = true;
        }

        public void downCounting() {
            this.up = false;
        }
    }
}
