package org.morphy;

import javax.lang.model.type.NullType;
import java.awt.*;
import java.util.HashMap;
import java.util.Map;

public abstract class $I<T extends $S> {

    public boolean ready;
    protected String symbolClz;
    protected Map<String, Double> properties;

    public $I() {
        this.properties = new HashMap<String, Double>();
    }

    public void set(String key, double val) {
        this.properties.put(key, val);
    }

    public double get(String key) {
        return this.properties.get(key);
    }

    public void setSymbol(String symbol) {
        this.symbolClz = symbol;
    }

    public String symbol() {
        return this.symbolClz;
    }

    public abstract void render(Graphics g);

    public static final class Root extends $I<$S> {

        public Root() {
            super();
            this.setSymbol("Null");
            this.set("alpha", 0.0);
            this.set("ratio", 1.0);
            this.set("length", 72);
        }

        @Override
        public void render(Graphics g) {
            Graphics2D g2 = (Graphics2D) g;
            double width = get("width");
            double height = get("height");
            g.setColor(Color.LIGHT_GRAY);
            g2.drawLine(0, (int)(height * 0.618), (int)width, (int)(height * 0.618));
        }
    }

    public static final Root root = new Root();
}
