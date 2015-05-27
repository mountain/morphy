package org.morphy;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.jkee.gtree.*;
import org.morphy.anno.Generative;
import org.morphy.anno.Rule;
import org.morphy.anno.Start;
import org.morphy.anno.Terminative;

public class $L {

    protected Set<$S> _generatives = new HashSet<$S>();
    protected Set<$S> _terminatives = new HashSet<$S>();
    protected Map<$S, $R> _rules = new HashMap<$S, $R>();

    protected $I<?>[] _start;
    protected Tree<? extends $I> _tree;

    public <T extends $S> T $(Class<T> clz) {
        try {
            return clz.newInstance();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return null;
    }

    public $I<?>[] $i($S... syms) {
        int cnt = syms.length;
        $I<?>[] instances = new $I<?>[cnt];
        for(int i = 0; i < cnt; i++) {
            instances[i] = syms[i].make();
            i++;
        }
        return instances;
    }

    protected void initialize() {
        try {
            for (Field field : getClass().getFields()) {
                if (field.isAnnotationPresent(Generative.class)) {
                    this._generatives.add(($S)field.get(this));
                } else if (field.isAnnotationPresent(Terminative.class)) {
                    this._terminatives.add(($S)field.get(this));
                } else if (field.isAnnotationPresent(Start.class)) {
                    this._start = ($I<?>[])field.get(this);
                } else if (field.isAnnotationPresent(Rule.class)) {
                    Type type = field.getGenericType();
                }
            }
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }

}
