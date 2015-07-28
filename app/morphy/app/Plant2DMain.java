package morphy.app;

import org.morphy.$I;
import org.morphy.$L;
import plantae.asterales.asteraceae.cosmos.Bipinnatus;

import javax.swing.*;
import java.awt.*;


public class Plant2DMain {

    public static Plant2DViewer viewer = new Plant2DViewer();

    private static void createAndShowGUI() {
        //Create and set up the window.
        JFrame frame = new JFrame("HelloWorldSwing");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.getContentPane().add(viewer);

        //Display the window.
        frame.pack();
        centreWindow(frame);
        frame.setVisible(true);
    }

    public static void centreWindow(Window frame) {
        Dimension dimension = Toolkit.getDefaultToolkit().getScreenSize();
        int w = (int) (dimension.getWidth() / 4 * 3);
        int h = (int) (dimension.getHeight() / 4 * 3);
        int x = (int) ((dimension.getWidth() - w) / 2);
        int y = (int) ((dimension.getHeight() - h) / 2);

        $I.root.set("width", w);
        $I.root.set("height", h);
        $I.root.set("x", w * 0.5);
        $I.root.set("y", h * 0.618);

        frame.setBounds(x, y, w, h);
    }

    public static void main(String[] args) {
        $L lsys = new Bipinnatus();
        viewer.seed(lsys);
        javax.swing.SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                createAndShowGUI();
                viewer.grow();
            }
        });
    }

}
