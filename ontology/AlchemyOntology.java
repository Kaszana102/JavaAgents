package pl.gda.pg.eti.kask.sa.alchemists.ontology;

import jade.content.onto.BeanOntology;
import jade.content.onto.BeanOntologyException;
import java.util.logging.Level;
import lombok.Getter;
import lombok.extern.java.Log;
import pl.gda.pg.eti.kask.sa.alchemists.ontology.MageOnly.*;
import pl.gda.pg.eti.kask.sa.alchemists.ontology.ShopOntology.*;

/**
 *
 * @author psysiu
 */
@Log
public class AlchemyOntology extends BeanOntology {

    public static final String NAME = "alchemy-ontology";

    @Getter
    private static final AlchemyOntology instance = new AlchemyOntology(NAME);

    private AlchemyOntology(String name) {
        super(name);
        try {
            add(Herb.class);
            add(Potion.class);
            add(Equipment.class);
            add(SellProduct.class);
            add(SendMenu.class);

            add(Hello.class);
            add(CheckShops.class);
            add(ShopList.class);
            add(CanWeAfford.class);

            add(TuplesList.class);

            add(ProductBought.class);
            add(SendMoney.class);

        } catch (BeanOntologyException ex) {
            log.log(Level.SEVERE, null, ex);
        }

    }

}
