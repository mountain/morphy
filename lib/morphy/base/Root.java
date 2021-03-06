package morphy.base;

import org.morphy.$I;
import org.morphy.$S;

import java.awt.*;

public class Root implements $S {
    @Override
    public $I<Root> make() {
        $I<Root> instance = new $I<Root>() {
            @Override
            public void render(Graphics g) {
                if (!this.ready) return;
                Graphics2D g2 = (Graphics2D) g;
                double x = get("x0");
                double y = get("y0");
                double alpha = get("alpha0");
                double length = get("length0");
                double ratio = get("ratio0");
                g.setColor(Color.CYAN);
                g2.translate(x, y);
                g2.rotate(-alpha);
                g2.fillRect(0, -(int) ((ratio * ratio * 2) + 1), (int) length, (int) ((ratio * ratio * 4) + 2));
                g2.rotate(alpha);
                g2.translate(-x, -y);
            }
        };
        instance.setSymbol(Root.class.getName());
        return instance;
    }
}
