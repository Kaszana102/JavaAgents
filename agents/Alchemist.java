package pl.gda.pg.eti.kask.sa.alchemists.agents;

import lombok.Getter;
import pl.gda.pg.eti.kask.sa.alchemists.ProductList.PriceCounterTuple;
import pl.gda.pg.eti.kask.sa.alchemists.ProductList.ShopOfferings;
import pl.gda.pg.eti.kask.sa.alchemists.behaviours.RegisterServiceBehaviour;
import pl.gda.pg.eti.kask.sa.alchemists.ontology.ShopOntology.Potion;

/**
 *
 * @author psysiu
 */
public class Alchemist extends Shopper {

    @Getter
    private final ShopOfferings potions = new ShopOfferings();
    
    public Alchemist() {
    }

    @Override
    protected void registerService(){
        addBehaviour(new RegisterServiceBehaviour(this, "alchemist"));
    }


}
