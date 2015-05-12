package org.morphy.anno;

public @interface Rule {
    double weight() default 1.0;

    double scale() default 0.9;

    double[] value();
}
