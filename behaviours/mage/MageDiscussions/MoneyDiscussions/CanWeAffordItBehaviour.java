package pl.gda.pg.eti.kask.sa.alchemists.behaviours.mage.MageDiscussions.MoneyDiscussions;

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
import pl.gda.pg.eti.kask.sa.alchemists.ProductList.ShopOfferings;
import pl.gda.pg.eti.kask.sa.alchemists.agents.Mage;
import pl.gda.pg.eti.kask.sa.alchemists.behaviours.mage.MageDiscussions.BroadCast;
import pl.gda.pg.eti.kask.sa.alchemists.ontology.MageOnly.CanWeAfford;
import pl.gda.pg.eti.kask.sa.alchemists.ontology.MageOnly.Hello;
import pl.gda.pg.eti.kask.sa.alchemists.ontology.MageOnly.ShopList;

import java.util.List;
import java.util.Vector;
import java.util.function.BiConsumer;
import java.util.logging.Level;

@Log
public class CanWeAffordItBehaviour extends SimpleBehaviour {


    boolean once = true;
    boolean canWeAffordIt;
    private Mage mage;
    private int hellos = 0;

    private int affordCounter = 0;

    private boolean affordmentSent =false;

    public CanWeAffordItBehaviour(Mage mage){
        this.mage= mage;
    }


    @Override
    public void action() {

        if(once){
            once = false;
            Hello hello = new Hello();

            myAgent.addBehaviour(
                    new BroadCast(
                            mage,
                            hello,
                            mage.getFellowMages(),
                            ACLMessage.PROXY
                    )
            );
        }


        checkMessages();
    }

    private MessageTemplate createMessageTemplate(){
        MessageTemplate mtMages = MessageTemplate.not(MessageTemplate.MatchAll());
        for(AID id: mage.getFellowMages().keySet()){
            mtMages = MessageTemplate.or(mtMages,MessageTemplate.MatchSender(id));
        }

        MessageTemplate mtPerformatives = MessageTemplate.not(MessageTemplate.MatchAll());
        mtPerformatives = MessageTemplate.or(mtPerformatives,MessageTemplate.MatchPerformative(ACLMessage.INFORM));
        mtPerformatives = MessageTemplate.or(mtPerformatives,MessageTemplate.MatchPerformative(ACLMessage.CONFIRM));
        mtPerformatives = MessageTemplate.or(mtPerformatives,MessageTemplate.MatchPerformative(ACLMessage.PROXY));

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
                        case ACLMessage.INFORM:
                            //System.out.println(mage.getName() + " AFFODMENT FROM " + message.getSender().getName());
                            CanWeAfford canWeAfford = (CanWeAfford) ((Action) ce).getAction();
                            if (canWeAfford.isWeCanAfford()){
                                if(canWeAffordIt){

                                }
                            }
                            else{
                                if(!canWeAffordIt){

                                }
                            }
                            affordCounter++;
                            //System.out.println(mage.getName() + " AFFODMENT counter: " + affordCounter);
                            break;

                        //agent ready to talk
                        case ACLMessage.PROXY:
                            if(((Action) ce).getAction() instanceof Hello) {
                                hellos++;
                                if(hellos == mage.getFellowMages().size()){
                                    System.out.println("GOT ALL");
                                    canWeAffordIt();
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


    private void canWeAffordIt(){

        int totalBudget = mage.getMyBudget() + mage.getFellowBudgets().values().stream().mapToInt(Integer::intValue).sum();

        if(totalBudget >= mage.getShopOffers().getTotalPrice()){

            CanWeAfford canWeAfford = new CanWeAfford(true);

            canWeAffordIt =true;
            //send YES WE CAN
            myAgent.addBehaviour(
                    new BroadCast(
                            mage,
                            canWeAfford,
                            mage.getFellowMages(),
                            ACLMessage.INFORM
                    )
            );
        }
        else{

            System.out.println("CAN'T AFFORD  totalbudget: " + totalBudget + " totalprice " + mage.getShopOffers().getTotalPrice());

            CanWeAfford canWeAfford = new CanWeAfford(false);
            canWeAffordIt =false;
            //send NO WE CAN'T
            myAgent.addBehaviour(
                    new BroadCast(
                            mage,
                            canWeAfford,
                            mage.getFellowMages(),
                            ACLMessage.INFORM
                    )
            );
        }
        affordmentSent=true;
        System.out.println(mage.getName() + " AFFORDMENT SENT");
    }


    //TODO
    //NIE ZAWSZE KONCZY SIĘ POPRAWNIE!

    @Override
    public boolean done() {
        if(affordCounter == mage.getFellowMages().size()){

            //just in case!
            if(!affordmentSent){
                canWeAffordIt();
            }
            if(canWeAffordIt) {
                ((SequentialBehaviour) this.getParent()).addSubBehaviour(new DistributeProductToBuyBehaviour(mage));
                System.out.println(mage.getName() + " YES WE CAN");
            }
            return true;
        }
        return false;
    }
}
