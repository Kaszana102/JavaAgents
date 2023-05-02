package pl.gda.pg.eti.kask.sa.alchemists.agents;

import lombok.Getter;
import pl.gda.pg.eti.kask.sa.alchemists.ProductList.PriceCounterTuple;
import pl.gda.pg.eti.kask.sa.alchemists.ProductList.ShopOfferings;
import pl.gda.pg.eti.kask.sa.alchemists.behaviours.RegisterServiceBehaviour;
import pl.gda.pg.eti.kask.sa.alchemists.ontology.ShopOntology.Equipment;

public class Blacksmith extends Shopper{
    @Getter
    private final ShopOfferings equipments = new ShopOfferings();

    public Blacksmith() {
    }

    @Override
    protected void registerService(){
        addBehaviour(new RegisterServiceBehaviour(this, "blacksmith"));
    }


}

