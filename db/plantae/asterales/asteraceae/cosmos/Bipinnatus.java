package plantae.asterales.asteraceae.cosmos;

import morphy.base.*;
import org.morphy.$I;
import org.morphy.$L;
import org.morphy.$R;
import org.morphy.anno.Generative;
import org.morphy.anno.Rule;
import org.morphy.anno.Start;
import org.morphy.anno.Terminative;

public class Bipinnatus extends $L {

    @Terminative
    public Root root = $(Root.class);

    @Terminative
    public Stem stem = $(Stem.class);

    @Terminative
    public Leaf leaf = $(Leaf.class);

    @Generative
    public Bud bud = $(Bud.class);

    @Generative
    public Tip tip = $(Tip.class);

    @Rule({1, 0, 0, 0, 1, 0, 0, 0, 1})
    public $R<Bud> rule1 = ($I<Bud> one) -> $i(bud, stem, stem, stem);

    @Rule({1, 0, 0, 0, 1, 0, 0, 0, 1})
    public $R<Bud> rule2 = ($I<Bud> one) -> $i(bud, leaf, leaf, stem);

    @Rule({1, 0, 0, 0, 1, 0, 0, 0, 1})
    public $R<Tip> rule3 = ($I<Tip> one) -> $i(tip, tip, root, root);

    @Start
    public $I<?>[] start = $i(bud, tip);

}
