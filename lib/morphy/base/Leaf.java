package morphy.base;

import org.morphy.$I;
import org.morphy.$S;

import java.awt.*;

public class Leaf implements $S {

    @Override
    public $I<Leaf> make() {
        $I<Leaf> instance = new $I<Leaf>() {
            @Override
            public void render(Graphics g) {
                if (!this.ready) return;
                Graphics2D g2 = (Graphics2D) g;
                double x = get("x0");
                double y = get("y0");
                double alpha = get("alpha0");
                g.setColor(Color.GREEN);
                g2.translate(x, y);
                g2.rotate(-alpha);
                g2.fillOval(0, 0, 5, 10);
                g2.rotate(alpha);
                g2.rotate(alpha);
                g2.fillOval(0, 0, 5, 10);
                g2.rotate(-alpha);
                g2.translate(-x, -y);
            }
        };
        instance.setSymbol(Leaf.class.getName());
        return instance;
    }

}
