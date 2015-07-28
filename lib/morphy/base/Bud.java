package morphy.base;

import org.morphy.$I;
import org.morphy.$S;

import java.awt.*;

public class Bud implements $S {
    @Override
    public $I<Bud> make() {
        $I<Bud> instance = new $I<Bud>() {
            @Override
            public void render(Graphics g) {
            }
        };
        instance.setSymbol(Bud.class.getName());
        return instance;
    }
}
