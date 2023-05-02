package pl.gda.pg.eti.kask.sa.alchemists.behaviours.mage;

import jade.core.AID;
import jade.core.behaviours.OneShotBehaviour;
import jade.core.behaviours.SequentialBehaviour;
import pl.gda.pg.eti.kask.sa.alchemists.ProductList.ContactList;
import pl.gda.pg.eti.kask.sa.alchemists.ProductList.PriceCounterTuple;
import pl.gda.pg.eti.kask.sa.alchemists.ProductList.PricesCounter;
import pl.gda.pg.eti.kask.sa.alchemists.ProductList.ShopOfferings;
import pl.gda.pg.eti.kask.sa.alchemists.agents.Mage;
import pl.gda.pg.eti.kask.sa.alchemists.ontology.ShopOntology.Product;

import java.util.Map;

public class CanIAffordBehaviour extends OneShotBehaviour {
    private final Mage myAgent;
    private boolean done =false;


    public CanIAffordBehaviour(Mage agent) {
        super(agent);
        this.myAgent = agent;
    }

    @Override
    public void action() {
        System.out.println("started counting!");
        if(canIAfford()){
            System.out.println("i can afford!");

            ((SequentialBehaviour) getParent()).addSubBehaviour(new BuyProductsBehaviour(myAgent));
        }
        else{
            //abandon shopping
            System.out.println("i can't buy!");
        }

        done=true;
    }

    protected boolean canIAfford(){

        ShopOfferings offersCopy = new ShopOfferings();
        offersCopy.concat(myAgent.getShopOffers());

        int totalCost=0;

        for (Product product: myAgent.getProductsToBuy()
        ) {
            ContactList contact = offersCopy.getContactList(product);
            AID cheapestShopKeeper = null;
            int lowestPrice=-1;


            if(contact != null) {
                Map<AID, PricesCounter> products = contact.getProducts();


                for (AID aid : products.keySet()
                ) {

                    for (PriceCounterTuple tuple : products.get(aid)
                    ) {
                        if (tuple.price < lowestPrice || lowestPrice == -1) {
                            cheapestShopKeeper = aid;
                            lowestPrice = tuple.price;
                        }
                    }
                }

                if (cheapestShopKeeper != null) {
                    offersCopy.deleteProduct(product, cheapestShopKeeper, lowestPrice);
                    totalCost += lowestPrice;
                    myAgent.getBestOffers().addProduct(product, cheapestShopKeeper, new PriceCounterTuple(lowestPrice, 1));
                }
            }
            else{
                System.out.println("Missing shopkeeper for product: " + product + "!");
                return false;
            }
        }

        if(myAgent.getMyBudget() >= totalCost){
            System.out.println("Total cost =" + totalCost);
            System.out.println("I will by everything there:");
            System.out.println(myAgent.getBestOffers());

            return  true;
        }
        else{
            System.out.println("Budget:" +myAgent.getMyBudget() + " to low :" + totalCost + "!");
            return  false;
        }
    }


    protected void createBuyThingsBehaviour(){

    }

}
