package morphy.app;


import org.morphy.$L;

import javax.swing.*;
import java.awt.*;
import java.awt.font.FontRenderContext;
import java.awt.geom.Rectangle2D;

public class Plant2DViewer extends JPanel {

    private $L plant;

    public void seed($L plant) {
        this.plant = plant;
    }

    public void grow() {
        this.plant.grow();
        this.plant.grow();
        this.plant.grow();
        this.plant.grow();
        this.plant.grow();
        this.plant.grow();
        this.plant.grow();
        this.plant.grow();
    }

    public void paint(Graphics g) {
        this.plant.render(g);
    }

}
