package pl.gda.pg.eti.kask.sa.alchemists.behaviours.Shopper;

import jade.content.onto.basic.Action;
import jade.core.AID;
import pl.gda.pg.eti.kask.sa.alchemists.agents.Herbalist;
import pl.gda.pg.eti.kask.sa.alchemists.agents.Shopper;
import pl.gda.pg.eti.kask.sa.alchemists.behaviours.WaitingBehaviour;
import pl.gda.pg.eti.kask.sa.alchemists.ontology.ShopOntology.SellHerb;
import pl.gda.pg.eti.kask.sa.alchemists.ontology.ShopOntology.SellProduct;
import pl.gda.pg.eti.kask.sa.alchemists.ontology.ShopOntology.SendMenu;

public class ShopperBehaviour extends WaitingBehaviour<Shopper> {

    public ShopperBehaviour(Shopper agent) {
        super(agent);
    }

    @Override
    protected void action(Action action, String conversationId, AID participant) {
        if (action.getAction() instanceof SellProduct) {
            myAgent.addBehaviour(new SellProductBehaviour(myAgent, (SellProduct) action.getAction(), conversationId, participant));
        }
        else if(action.getAction() instanceof SendMenu){
            myAgent.addBehaviour(new SendMenuBehaviour(myAgent, (SendMenu) action.getAction(), conversationId, participant));
        }
    }

}
