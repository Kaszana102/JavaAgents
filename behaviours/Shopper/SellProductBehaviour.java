package pl.gda.pg.eti.kask.sa.alchemists.behaviours.Shopper;

import jade.content.Predicate;
import jade.content.onto.basic.Result;
import jade.core.AID;
import jade.lang.acl.ACLMessage;
import pl.gda.pg.eti.kask.sa.alchemists.agents.Shopper;
import pl.gda.pg.eti.kask.sa.alchemists.behaviours.ActionBehaviour;
import pl.gda.pg.eti.kask.sa.alchemists.ontology.ShopOntology.SellProduct;

public class SellProductBehaviour extends ActionBehaviour<SellProduct, Shopper> {

public SellProductBehaviour(Shopper agent, SellProduct action, String conversationId, AID participant) {
        super(agent, action, conversationId, participant);
        }

@Override
    protected Predicate performAction() {
        if (myAgent.getOffers().contains(action.getProduct(),myAgent.getAID(),action.getPrice())) {

            myAgent.getOffers().deleteProduct(action.getProduct(),myAgent.getAID(),action.getPrice());

            this.aclMessagePerformative= ACLMessage.INFORM;
            return new Result(action, action.getProduct());
        } else {
            this.aclMessagePerformative= ACLMessage.REFUSE;
            return null;
        }
    }
}
