package pl.gda.pg.eti.kask.sa.alchemists.behaviours.mage.MenuBehaviours;

import jade.content.Predicate;
import jade.content.onto.basic.Result;
import jade.core.AID;
import pl.gda.pg.eti.kask.sa.alchemists.ProductList.ShopOfferings;
import pl.gda.pg.eti.kask.sa.alchemists.agents.Mage;
import pl.gda.pg.eti.kask.sa.alchemists.behaviours.ReceiveResultBehaviour;
import pl.gda.pg.eti.kask.sa.alchemists.ontology.ShopOntology.Equipment;

public class ReceiveMenuBehaviour extends ReceiveResultBehaviour<Mage> {

    public ReceiveMenuBehaviour(Mage agent, String conversationId) {
        super(agent, conversationId);
    }

    @Override
    protected void handleResult(Predicate predicate, AID participant) {
        if (predicate instanceof Result) {
            ShopOfferings offer = new ShopOfferings();
            offer.unpackFromJson(((Result) predicate).getValue().toString());
            myAgent.addShopOffers(offer);

        }
    }
}
