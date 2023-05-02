package pl.gda.pg.eti.kask.sa.alchemists.agents;

import lombok.Getter;
import pl.gda.pg.eti.kask.sa.alchemists.ProductList.PriceCounterTuple;
import pl.gda.pg.eti.kask.sa.alchemists.ProductList.ShopOfferings;
import pl.gda.pg.eti.kask.sa.alchemists.behaviours.RegisterServiceBehaviour;
import pl.gda.pg.eti.kask.sa.alchemists.ontology.ShopOntology.Herb;

/**
 *
 * @author psysiu
 */
public class Herbalist extends Shopper {

    public Herbalist() {
    }

    @Override
    protected void registerService(){
        addBehaviour(new RegisterServiceBehaviour(this, "herbalist"));
    }

}
