package pl.gda.pg.eti.kask.sa.alchemists.behaviours.mage.MageDiscussions.ShopListDiscussion;

import jade.content.ContentElement;
import jade.content.lang.Codec;
import jade.content.onto.OntologyException;
import jade.content.onto.basic.Action;
import jade.core.AID;
import jade.core.behaviours.SequentialBehaviour;
import jade.core.behaviours.SimpleBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import lombok.extern.java.Log;
import pl.gda.pg.eti.kask.sa.alchemists.ProductList.ContactList;
import pl.gda.pg.eti.kask.sa.alchemists.ProductList.PriceCounterTuple;
import pl.gda.pg.eti.kask.sa.alchemists.ProductList.PricesCounter;
import pl.gda.pg.eti.kask.sa.alchemists.ProductList.ShopOfferings;
import pl.gda.pg.eti.kask.sa.alchemists.agents.Mage;
import pl.gda.pg.eti.kask.sa.alchemists.behaviours.mage.MageDiscussions.BroadCast;
import pl.gda.pg.eti.kask.sa.alchemists.behaviours.mage.MageDiscussions.MoneyDiscussions.CanWeAffordItBehaviour;
import pl.gda.pg.eti.kask.sa.alchemists.behaviours.mage.MageDiscussions.ShopDiscussions.RespondCheckShops;
import pl.gda.pg.eti.kask.sa.alchemists.ontology.MageOnly.CheckShops;
import pl.gda.pg.eti.kask.sa.alchemists.ontology.MageOnly.Hello;
import pl.gda.pg.eti.kask.sa.alchemists.ontology.MageOnly.ShopList;
import pl.gda.pg.eti.kask.sa.alchemists.ontology.ShopOntology.Product;
import pl.gda.pg.eti.kask.sa.alchemists.ontology.ShopOntology.SendMenu;

import java.util.Map;
import java.util.logging.Level;

@Log
public class DiscussBestShopListBehaviour extends SimpleBehaviour {

    protected Mage mage;

    private boolean once = true;
    private int hellos = 0;

    private boolean everythingIsOnTheShelves;

    private int proposes = 0;


    public  DiscussBestShopListBehaviour(Mage mage){
        this.mage=mage;
    }


    @Override
    public void action() {

        if(once){
            once = false;
            Hello hello = new Hello();

            if(mage.getName().equals("Mag1@GUI")) {
                System.out.println("all offert of mag1 "+mage.getShopOffers());
            }


            myAgent.addBehaviour(
                new BroadCast(
                        mage,
                        hello,
                        mage.getFellowMages(),
                        ACLMessage.CFP
                )
            );
            everythingIsOnTheShelves = calculateBestShopList();
        }
        checkMessages();

    }

    private MessageTemplate createMessageTemplate(){
        MessageTemplate mtMages = MessageTemplate.not(MessageTemplate.MatchAll());
        for(AID id: mage.getFellowMages().keySet()){
            mtMages = MessageTemplate.or(mtMages,MessageTemplate.MatchSender(id));
        }

        MessageTemplate mtPerformatives = MessageTemplate.not(MessageTemplate.MatchAll());
        mtPerformatives = MessageTemplate.or(mtPerformatives,MessageTemplate.MatchPerformative(ACLMessage.PROPOSE));
        mtPerformatives = MessageTemplate.or(mtPerformatives,MessageTemplate.MatchPerformative(ACLMessage.CONFIRM));
        mtPerformatives = MessageTemplate.or(mtPerformatives,MessageTemplate.MatchPerformative(ACLMessage.CFP));

        return MessageTemplate.and(mtMages,mtPerformatives);
    }



    private void checkMessages(){
        MessageTemplate mt = createMessageTemplate();

        ACLMessage message = myAgent.receive(mt);
        if (message != null) {
            //System.out.println(message);
            try {
                ContentElement ce = myAgent.getContentManager().extractContent(message);
                if (ce instanceof Action) {
                    int performative = message.getPerformative();

                    switch(performative){
                        //got proposal from another agent
                        case ACLMessage.PROPOSE:
                            //System.out.println("propose received!");
                            ShopList shopList = (ShopList) ((Action) ce).getAction();

                            ShopOfferings other = new ShopOfferings();
                            other.unpackFromJson(shopList.getShopOffers());

                            compareShopLists(other);

                            break;

                        //agent ready to talk
                        case ACLMessage.CFP:
                            if(((Action) ce).getAction() instanceof Hello) {
                                hellos++;
                                if(hellos == mage.getFellowMages().size()){
                                    //System.out.println(mage.getName()+" got everybody!");
                                    sendBestShopList();
                                }
                            }
                            break;

                            //count confirms
                        case ACLMessage.CONFIRM:

                            break;
                    }
                }

            } catch (Codec.CodecException | OntologyException ex) {
                log.log(Level.SEVERE, null, ex);
            }
        }
    }

    private boolean calculateBestShopList(){
        ShopOfferings offersCopy = new ShopOfferings();
        offersCopy.concat(mage.getShopOffers());

        int totalCost=0;

        for (Product product: mage.getProductsToBuy()
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
                    mage.getBestOffers().addProduct(product, cheapestShopKeeper, new PriceCounterTuple(lowestPrice, 1));
                }
            }
            else{
                System.out.println("Missing shopkeeper for product: " + product + "!");
                return false;
            }
        }

        System.out.println(mage.getName() + " everything is on the shelves!");

        return true;
    }


    private void sendBestShopList(){
        ShopList shopList = new ShopList();
        shopList.setShopOffers(mage.getBestOffers().packToJson());


        myAgent.addBehaviour(
                new BroadCast(
                        mage,
                        shopList,
                        mage.getFellowMages(),
                        ACLMessage.PROPOSE));
    }



    private void compareShopLists(ShopOfferings other){
        if(mage.getBestOffers().equals(other)){
            //send ok
            proposes++;
        }
        else{
            //SHOULD NOT HAPPEN
            //calc which is cheaper.
            if(mage.getBestOffers().getTotalPrice() < other.getTotalPrice()){
                //mine is still better
            }
            else if (mage.getBestOffers().getTotalPrice() == other.getTotalPrice())
            {
                //trouble!
            }
            else{
                //i get his offers
            }
        }

    }

    @Override
    public boolean done() {
        if(proposes == mage.getFellowMages().size()){
            if(everythingIsOnTheShelves){
                /* if(mage.getName().equals("Mag1@GUI")) {
                    System.out.println("best offert of mag1 "+mage.getBestOffers());
                }
                */

                //if can buy add
                ((SequentialBehaviour) this.getParent()).addSubBehaviour(new CanWeAffordItBehaviour(mage));
            }
            return true;
        }
        return false;
    }
}
