package pl.gda.pg.eti.kask.sa.alchemists.behaviours.mage.RequestReceiveProduct;

import jade.content.Predicate;
import jade.content.onto.basic.Result;
import jade.core.AID;
import pl.gda.pg.eti.kask.sa.alchemists.agents.Mage;
import pl.gda.pg.eti.kask.sa.alchemists.behaviours.ReceiveResultBehaviour;
import pl.gda.pg.eti.kask.sa.alchemists.ontology.ShopOntology.Product;

/**
 *
 * @author psysiu
 */
public class ReceiveHerbBehaviour extends ReceiveResultBehaviour<Mage> {

    public ReceiveHerbBehaviour(Mage agent, String conversationId) {
        super(agent, conversationId);
    }

    @Override
    protected void handleResult(Predicate predicate, AID participant) {
        if (predicate instanceof Result) {
            myAgent.addProduct( ((Product)  ((Result) predicate).getValue())   );
        } else {
            System.out.println("No result");
        }
    }

}
