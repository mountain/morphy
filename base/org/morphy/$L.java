package org.morphy;

import java.awt.*;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.util.*;
import java.util.List;

import com.google.common.base.Function;
import com.google.common.base.Predicate;
import org.jkee.gtree.*;
import org.jkee.gtree.builder.ChildrenLinkTreeBuilder;
import org.morphy.anno.Generative;
import org.morphy.anno.Rule;
import org.morphy.anno.Start;
import org.morphy.anno.Terminative;

public class $L {

    protected Set<$S> _generatives = new HashSet<$S>();
    protected Set<$S> _terminatives = new HashSet<$S>();
    protected Map<String, List<$R>> _rules = new HashMap<String, List<$R>>();
    protected Map<$R, Rule> _params = new HashMap<$R, Rule>();

    protected ChildrenLinkTreeBuilder _builder;

    protected $I<?>[] _start;
    protected Tree<$I> _tree;

    public synchronized <T extends $S> T $(Class<T> clz) {
        try {
            return clz.newInstance();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return null;
    }

    public synchronized $I<?>[] $i($S... syms) {
        int cnt = syms.length;
        System.out.println(cnt);
        System.out.println(Arrays.asList(syms));
        $I<?>[] instances = new $I<?>[cnt];
        for (int i = 0; i < cnt; i++) {
            instances[i] = syms[i].make();
            System.out.println("++++++++++++++++++++++++++++++++++");
            System.out.println(syms[i]);
            System.out.println(instances[i]);
            System.out.println("++++++++++++++++++++++++++++++++++");
        }
        return instances;
    }

    protected synchronized void initialize() {
        try {
            _builder = new ChildrenLinkTreeBuilder<$I>(new Funnel());
            _tree = _builder.build($I.root);

            for (Field field : getClass().getDeclaredFields()) {
                System.out.println(String.format("process: %s", field.getName()));
                System.out.println(Arrays.asList(field.getAnnotations()));
                if (field.isAnnotationPresent(Generative.class)) {
                    this._generatives.add(($S) field.get(this));
                    System.out.println(String.format("process generative: %s", field.getName()));
                    System.out.println(this._generatives);
                } else if (field.isAnnotationPresent(Terminative.class)) {
                    this._terminatives.add(($S) field.get(this));
                    System.out.println(String.format("process terminative: %s", field.getName()));
                    System.out.println(this._terminatives);
                } else if (field.isAnnotationPresent(Start.class)) {
                    this._start = ($I<?>[]) field.get(this);
                    double[] val = field.getAnnotation(Start.class).value();
                    System.out.println(this._start);

                    $I root = $I.root;
                    double alpha = root.get("alpha");
                    double ratio = root.get("ratio");
                    double length = root.get("length");
                    double x = root.get("x");
                    double y = root.get("y");

                    int len = _start.length;
                    for (int i = 0; i < len; i++) {
                        $I input = _start[i];
                        System.out.println(input);

                        double theta = val[i] / 360.0 * 2 * Math.PI;
                        input.set("alpha",  alpha + theta);
                        input.set("theta", theta);
                        input.set("ratio", ratio);
                        input.set("length", length);
                        input.set("x", x);
                        input.set("y", y);
                        input.ready = true;

                        Tree<$I> ndChild = _builder.build(input);
                        _tree.addChild(ndChild);
                    }
                } else if (field.isAnnotationPresent(Rule.class)) {
                    Type type = field.getGenericType();
                    String name = type.getTypeName();
                    name = name.substring(name.indexOf('<') + 1, name.lastIndexOf('>'));
                    if (!_rules.containsKey(name)) {
                        _rules.put(name, new ArrayList<$R>());
                    }

                    $R key = ($R) field.get(this);
                    Rule val = field.getAnnotation(Rule.class);

                    System.out.println(String.format("process rule: %s %s", field.getName(), name));

                    _rules.get(name).add(key);
                    _params.put(key, val);
                }
            }

        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    public void grow() {
        if (this._start == null) {
            this.initialize();
        }

        Tree<$I> clone = _tree.transform(constant);
        Iterator<Tree<$I>> iterSelf = _tree.treeIterator();
        Iterator<Tree<$I>> iterClone = clone.treeIterator();
        while (iterSelf.hasNext()) {
            Tree<$I> ndSelf = iterSelf.next();
            Tree<$I> ndClone = iterClone.next();

            if (ndSelf.getChildren() == null || ndSelf.getChildren().size() == 0) {
                $I instance = ndClone.getValue();
                String symbol = instance.symbol();
                $R rule = choiceBy(symbol);

                if (rule != null) {
                    double alpha = instance.get("alpha");
                    double ratio = instance.get("ratio");
                    double length = instance.get("length");
                    double x = instance.get("x");
                    double y = instance.get("y");

                    Rule param = _params.get(rule);
                    double[] thetas = param.value();
                    double scale = param.scale();

                    $I[] results = rule.to(instance);
                    int len = results.length;
                    for (int i = 0; i < len; i++) {
                        $I result = results[i];

                        double theta = thetas[i] / 360.0 * 2 * Math.PI;
                        result.set("alpha0", alpha);
                        result.set("ratio0", ratio);
                        result.set("length0", length);
                        result.set("x0", x);
                        result.set("y0", y);

                        result.set("alpha", alpha + theta);
                        result.set("theta", theta);
                        result.set("ratio", ratio * scale);
                        result.set("length", length * scale);
                        result.set("x", x + Math.cos(alpha) * length);
                        result.set("y", y - Math.sin(alpha) * length);

                        result.ready = true;

                        Tree<$I> ndChild = _builder.build(result);
                        ndClone.addChild(ndChild);
                    }
                }
            }
        }

        _tree = clone;
    }

    private $R choiceBy(String symbol) {
        System.out.println(symbol);
        List<$R> rules = _rules.get(symbol);
        if (rules != null && rules.size() > 0) {
            Collections.shuffle(rules);
            return rules.get(0);
        } else {
            return null;
        }
    }

    public void render(Graphics g) {
        if (this._start == null) {
            this.initialize();
            this.grow();
        }
        Iterator<$I> iter = _tree.iterator();
        while (iter.hasNext()) {
            iter.next().render(g);
        }
    }

    private static class Funnel implements ChildrenLinkTreeBuilder.Funnel<$I> {

        private Map<$I, List<$I>> registry;

        public Funnel() {
            this.registry = new HashMap<$I, List<$I>>();
        }

        @Override
        public List getChildren($I key) {
            return registry.get(key);
        }

    }

    private static class Const implements Function<$I, $I> {
        @Override
        public $I apply($I i) {
            return i;
        }

        @Override
        public boolean equals(Object o) {
            return false;
        }
    }

    private static final Const constant = new Const();
}
