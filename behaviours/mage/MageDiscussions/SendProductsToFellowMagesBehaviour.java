package pl.gda.pg.eti.kask.sa.alchemists.behaviours.mage.MageDiscussions;

import jade.content.ContentElement;
import jade.content.lang.Codec;
import jade.content.onto.OntologyException;
import jade.content.onto.basic.Action;
import jade.core.AID;
import jade.core.behaviours.SimpleBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import lombok.extern.java.Log;
import pl.gda.pg.eti.kask.sa.alchemists.ProductList.ProductList;
import pl.gda.pg.eti.kask.sa.alchemists.agents.Mage;
import pl.gda.pg.eti.kask.sa.alchemists.ontology.MageOnly.ProductBought;
import pl.gda.pg.eti.kask.sa.alchemists.ontology.ShopOntology.Product;

import java.util.logging.Level;

@Log
public class SendProductsToFellowMagesBehaviour extends SimpleBehaviour {

    private boolean once=true;
    private final Mage mage;

    private int counter=0;

    public SendProductsToFellowMagesBehaviour(Mage mage){
        this.mage=mage;
    }

    @Override
    public void action() {
        if(once){
            once=false;
            sendProducts();
        }

        checkMessages();
    }


    private MessageTemplate createMessageTemplate(){
        MessageTemplate mtMages = MessageTemplate.not(MessageTemplate.MatchAll());
        for(AID id: mage.getFellowMages().keySet()){
            mtMages = MessageTemplate.or(mtMages,MessageTemplate.MatchSender(id));
        }

        MessageTemplate mtPerformatives = MessageTemplate.not(MessageTemplate.MatchAll());
        mtPerformatives = MessageTemplate.or(mtPerformatives,MessageTemplate.MatchPerformative(ACLMessage.PROPAGATE));

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
                    ProductBought productBought = (ProductBought) ((Action) ce).getAction();

                    mage.getProductsBought().add(ProductList.convertFromJason(productBought.getProductsList()));
                    counter++;
                }

            } catch (Codec.CodecException | OntologyException ex) {
                log.log(Level.SEVERE, null, ex);
            }
        }
    }




    void sendProducts(){

        System.out.println(myAgent.getName() + " bought: " + mage.getProductsBought());
        ProductBought productBought = new ProductBought();
        productBought.setProductsList(mage.getProductsBought().convertToJason());

        System.out.println(mage.getName()+" broadcasted: " + productBought);

        myAgent.addBehaviour(
                new BroadCast(
                        mage,
                        productBought,
                        mage.getFellowMages(),
                        ACLMessage.PROPAGATE
                )
        );

    }

    @Override
    public boolean done() {
        return counter==mage.getFellowMages().size();
    }
}
