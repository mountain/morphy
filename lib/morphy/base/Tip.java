package morphy.base;

import org.morphy.$I;
import org.morphy.$S;

import java.awt.*;

public class Tip implements $S {
    @Override
    public $I<Tip> make() {
        $I<Tip> instance = new $I<Tip>() {
            @Override
            public void render(Graphics g) {
            }
        };
        instance.setSymbol(Tip.class.getName());
        return instance;
    }
}
