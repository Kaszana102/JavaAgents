package pl.gda.pg.eti.kask.sa.alchemists.behaviours.mage.RequestReceiveProduct;

import jade.core.AID;
import pl.gda.pg.eti.kask.sa.alchemists.agents.Mage;
import pl.gda.pg.eti.kask.sa.alchemists.behaviours.ReceiveResultBehaviour;
import pl.gda.pg.eti.kask.sa.alchemists.behaviours.RequestActionBehaviour;
import pl.gda.pg.eti.kask.sa.alchemists.ontology.ShopOntology.SellHerb;

/**
 *
 * @author psysiu
 */
public class RequestHerbBehaviour extends RequestActionBehaviour<SellHerb, Mage> {

    public RequestHerbBehaviour(Mage agent, AID participant, SellHerb action) {
        super(agent, participant, action);
    }

    @Override
    protected ReceiveResultBehaviour createResultBehaviour(String conversationId) {
        return new ReceiveHerbBehaviour(myAgent, conversationId);
    }
    
}
