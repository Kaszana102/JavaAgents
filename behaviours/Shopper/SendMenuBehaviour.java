package pl.gda.pg.eti.kask.sa.alchemists.behaviours.Shopper;

import jade.content.Predicate;
import jade.content.onto.basic.Result;
import jade.core.AID;
import jade.lang.acl.ACLMessage;
import pl.gda.pg.eti.kask.sa.alchemists.agents.Herbalist;
import pl.gda.pg.eti.kask.sa.alchemists.agents.Shopper;
import pl.gda.pg.eti.kask.sa.alchemists.behaviours.ActionBehaviour;
import pl.gda.pg.eti.kask.sa.alchemists.ontology.ShopOntology.SendMenu;

public class SendMenuBehaviour extends ActionBehaviour<SendMenu, Shopper> {

    public SendMenuBehaviour(Shopper agent, SendMenu action, String conversationId, AID participant) {
        super(agent, action, conversationId, participant);
    }


    @Override
    protected Predicate performAction() {
        this.aclMessagePerformative= ACLMessage.INFORM;
        return new Result(action, myAgent.getOffers().packToJson());
    }
}

