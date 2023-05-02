package pl.gda.pg.eti.kask.sa.alchemists.behaviours.mage.MenuBehaviours;

import jade.core.AID;
import pl.gda.pg.eti.kask.sa.alchemists.agents.Mage;
import pl.gda.pg.eti.kask.sa.alchemists.behaviours.ReceiveResultBehaviour;
import pl.gda.pg.eti.kask.sa.alchemists.behaviours.RequestActionBehaviour;
import pl.gda.pg.eti.kask.sa.alchemists.ontology.ShopOntology.SendMenu;

public class RequestMenuBehaviour extends RequestActionBehaviour<SendMenu, Mage> {

    public RequestMenuBehaviour(Mage agent, AID participant, SendMenu action) {
        super(agent, participant, action);
    }


    @Override
    protected ReceiveResultBehaviour createResultBehaviour(String conversationId) {
        return new ReceiveMenuBehaviour(myAgent, conversationId);
    }
}
