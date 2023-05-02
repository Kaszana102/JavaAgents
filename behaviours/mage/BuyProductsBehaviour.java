package pl.gda.pg.eti.kask.sa.alchemists.behaviours.mage;

import jade.core.AID;
import jade.core.behaviours.SequentialBehaviour;
import jade.core.behaviours.SimpleBehaviour;
import pl.gda.pg.eti.kask.sa.alchemists.behaviours.mage.MageDiscussions.MoneyDiscussions.DistributeProductToBuyBehaviour;
import pl.gda.pg.eti.kask.sa.alchemists.behaviours.mage.MageDiscussions.SendProductsToFellowMagesBehaviour;
import pl.gda.pg.eti.kask.sa.alchemists.behaviours.mage.RequestReceiveProduct.RequestProductBehaviour;
import pl.gda.pg.eti.kask.sa.alchemists.ontology.ShopOntology.*;
import pl.gda.pg.eti.kask.sa.alchemists.ProductList.ContactList;
import pl.gda.pg.eti.kask.sa.alchemists.ProductList.PriceCounterTuple;
import pl.gda.pg.eti.kask.sa.alchemists.ProductList.ShopOfferings;
import pl.gda.pg.eti.kask.sa.alchemists.agents.Mage;


public class BuyProductsBehaviour extends SimpleBehaviour {

    private SequentialBehaviour behaviour;
    private final Mage myAgent;

    public BuyProductsBehaviour(Mage agent) {
        super(agent);
        this.myAgent = agent;
        behaviour = new SequentialBehaviour(myAgent);
        requestProducts(behaviour);
    }

    protected void requestProducts(SequentialBehaviour behaviour) {

        ShopOfferings myOffers = myAgent.getMyTuples().converToShopOfferings();

        for(Product product : myOffers.getProducts().keySet()){
            ContactList contactList = myOffers.getContactList(product);
            for (AID shop: contactList
                 ) {
                for (PriceCounterTuple tuple:  contactList.getPricesCounter(shop)
                     ) {
                    for(int i=0;i <tuple.units; i++){

                        SellProduct action = new SellProduct(product, tuple.price);
                        RequestProductBehaviour request = new RequestProductBehaviour(myAgent, shop, action);
                        behaviour.addSubBehaviour(request);
                        myAgent.pay(tuple.price);
                    }
                }
            }
        }

        myAgent.addBehaviour(behaviour);

    }

    @Override
    public void action() {

    }

    @Override
    public boolean done() {
        if(behaviour.done()){
            //sent to others!
            ((SequentialBehaviour) this.getParent()).addSubBehaviour(new SendProductsToFellowMagesBehaviour(myAgent));
        }

        return behaviour.done();
    }
}
