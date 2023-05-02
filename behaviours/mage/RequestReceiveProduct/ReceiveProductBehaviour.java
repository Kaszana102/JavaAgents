package pl.gda.pg.eti.kask.sa.alchemists.behaviours.mage.RequestReceiveProduct;

import jade.content.Predicate;
import jade.content.onto.basic.Result;
import jade.core.AID;
import pl.gda.pg.eti.kask.sa.alchemists.agents.Mage;
import pl.gda.pg.eti.kask.sa.alchemists.behaviours.ReceiveResultBehaviour;
import pl.gda.pg.eti.kask.sa.alchemists.ontology.ShopOntology.Product;
import pl.gda.pg.eti.kask.sa.alchemists.ontology.ShopOntology.SellProduct;

public class ReceiveProductBehaviour extends ReceiveResultBehaviour<Mage> {

    public ReceiveProductBehaviour(Mage agent, String conversationId) {
        super(agent, conversationId);
    }

    @Override
    protected void handleResult(Predicate predicate, AID participant) {
        if (predicate instanceof Result) {
            myAgent.addProduct( ((SellProduct)  ((Result) predicate).getAction()).getProduct()   );
        } else {
            System.out.println("No result");
        }
    }

}
