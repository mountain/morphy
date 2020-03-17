package plantae.asterales.asteraceae.cosmos;

import morphy.base.*;
import org.morphy.$I;
import org.morphy.$L;
import org.morphy.$R;
import org.morphy.anno.Generative;
import org.morphy.anno.Rule;
import org.morphy.anno.Start;
import org.morphy.anno.Terminative;

import morphy.base.leaves.Subulate;

public class Cosmos_bipinnatus extends $L {

    @Terminative
    public Leaf leaf = $(Subulate.class);

    @Generative
    public Root root = $(Root.class);

    @Generative
    public Stem stem = $(Stem.class);

    @Generative
    public Bud bud = $(Bud.class);

    @Generative
    public Tip tip = $(Tip.class);

    @Rule(value = {43, 21, 2, -23, -43})
    public $R<Bud> rule1 = ($I<Bud> one) -> $i(leaf, stem, stem, stem, leaf);

    @Rule({31, 37, -3, -31})
    public $R<Bud> rule2 = ($I<Bud> one) -> $i(leaf, stem, stem, leaf);

    @Rule({31, -3, -27})
    public $R<Bud> rule3 = ($I<Bud> one) -> $i(leaf, stem, leaf);

    @Rule({67, 0, -67})
    public $R<Tip> rule4 = ($I<Tip> one) -> $i(root, root, root);

    @Rule({0, 0})
    public $R<Stem> rule5 = ($I<Stem> one) -> $i(stem, bud);

    @Rule({0, 0})
    public $R<Root> rule6 = ($I<Root> one) -> $i(root, tip);

    @Start({90, -90})
    public $I<?>[] start = $i(bud, tip);

}
