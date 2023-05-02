package pl.gda.pg.eti.kask.sa.alchemists.behaviours.mage.RequestReceiveProduct;

import jade.core.AID;
import pl.gda.pg.eti.kask.sa.alchemists.agents.Mage;
import pl.gda.pg.eti.kask.sa.alchemists.behaviours.ReceiveResultBehaviour;
import pl.gda.pg.eti.kask.sa.alchemists.behaviours.RequestActionBehaviour;
import pl.gda.pg.eti.kask.sa.alchemists.ontology.ShopOntology.SellHerb;
import pl.gda.pg.eti.kask.sa.alchemists.ontology.ShopOntology.SellProduct;

public class RequestProductBehaviour extends RequestActionBehaviour<SellProduct, Mage> {

    public RequestProductBehaviour(Mage agent, AID participant, SellProduct action) {
        super(agent, participant, action);
    }

    @Override
    protected ReceiveResultBehaviour createResultBehaviour(String conversationId) {
        return new ReceiveProductBehaviour(myAgent, conversationId);
    }

}
