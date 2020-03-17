package morphy.base.leaves;

import morphy.base.Leaf;
import org.morphy.$I;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.RescaleOp;

public class Pedate extends Leaf {

    private static String imgname = String.format("%s.png" , Pedate.class.getName().toLowerCase().replace('.', '/'));
    private static BufferedImage img = null;
    private static float[] scalefactor = {-1.0f, 1.0f};
    private static float[] offset = {0.0f, 0.0f};
    private static RescaleOp op = new RescaleOp(scalefactor, offset, null);

    {
        try {
            System.out.println(imgname);
            System.out.println(Pedate.class.getClassLoader().getResource(imgname).getPath());
            img = ImageIO.read(Pedate.class.getClassLoader().getResourceAsStream(imgname));
        } catch (Exception e) {
            System.out.println(e);
            e.printStackTrace();
        }
    }

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
                g2.rotate(alpha);
                g2.drawImage(img, -15, 0, 15, -8, null, null);
                g2.rotate(-alpha );
                g2.rotate(-alpha);
                g2.drawImage(img, -15, 0, 15, -8, null, null);
                g2.rotate(alpha);
                g2.translate(-x, -y);
            }
        };
        instance.setSymbol(Pedate.class.getName());
        return instance;
    }
}
