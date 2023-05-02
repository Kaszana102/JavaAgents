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
import pl.gda.pg.eti.kask.sa.alchemists.ProductList.ShopOfferTuple;
import pl.gda.pg.eti.kask.sa.alchemists.ProductList.ShopOfferingPrioritedTuples;
import pl.gda.pg.eti.kask.sa.alchemists.agents.Mage;
import pl.gda.pg.eti.kask.sa.alchemists.behaviours.mage.BuyProductsBehaviour;
import pl.gda.pg.eti.kask.sa.alchemists.behaviours.mage.MageDiscussions.BroadCast;
import pl.gda.pg.eti.kask.sa.alchemists.ontology.MageOnly.CanWeAfford;
import pl.gda.pg.eti.kask.sa.alchemists.ontology.MageOnly.Hello;
import pl.gda.pg.eti.kask.sa.alchemists.ontology.MageOnly.TuplesList;

import java.util.logging.Level;


@Log
public class DistributeProductToBuyBehaviour extends SimpleBehaviour {

    boolean done=false;
    boolean once=true;



    private final Mage mage;
    private int hellos = 0;

    private ShopOfferingPrioritedTuples shopOfferingPrioritedTuples;
    private ShopOfferingPrioritedTuples myTuples = new ShopOfferingPrioritedTuples();
    private int maxTuples;

    private int agrees=0;
    private int myProposeID;

    private int finishes = 0;
    private boolean broadcastedFinish=false;

    public  DistributeProductToBuyBehaviour(Mage mage){
        this.mage=mage;
    }


    @Override
    public void action() {

        if(once){
            once = false;
            shopOfferingPrioritedTuples = mage.getBestOffers().convertToProritiedTuples();
            double a = (double) shopOfferingPrioritedTuples.size(); //liczba tupli
            double b = (double) mage.getFellowMages().size() + 1; //loczba wszystkich magow
            maxTuples = (int) Math.ceil(a/b);

            Hello hello = new Hello();

            myAgent.addBehaviour(
                    new BroadCast(
                            mage,
                            hello,
                            mage.getFellowMages(),
                            ACLMessage.CFP
                    )
            );


            boolean allAlone = mage.getFellowMages().size() == 0;
            if(allAlone){
                chooseRandomTuples();
            }
        }


        checkMessages();


        if(finishes == mage.getFellowMages().size()){
            System.out.println(mage.getName() + myTuples);
            done=true;
        }

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
        mtPerformatives = MessageTemplate.or(mtPerformatives,MessageTemplate.MatchPerformative(ACLMessage.INFORM));

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
                            //System.out.println(mage.getName() +  " PROOSE");
                            TuplesList tuplesList = (TuplesList)  ((Action) ce).getAction();
                            ShopOfferingPrioritedTuples tuples =  ShopOfferingPrioritedTuples.convertFromJason(tuplesList.getTuples());
                            considerPropose(tuples,tuplesList,message);
                            break;

                        //agent ready to talk
                        case ACLMessage.CFP:
                            if(((Action) ce).getAction() instanceof Hello) {
                                hellos++;
                                if(hellos == mage.getFellowMages().size()){
                                    System.out.println("GOT ALL");
                                    chooseRandomTuples();
                                }
                            }
                            break;

                        //count confirms
                        case ACLMessage.CONFIRM:
                            //System.out.println(mage.getName() + " CONFIRM from " + message.getSender().getName());
                            tuplesList = (TuplesList)  ((Action) ce).getAction();
                            if(tuplesList.getID() == myProposeID) {
                                agrees++;
                                if(agrees == mage.getFellowMages().size()){

                                    System.out.println(mage.getName() + " SATISFIED");

                                    //broadcast you are satisfied
                                    myAgent.addBehaviour(
                                            new BroadCast(
                                                    mage,
                                                    new Hello(),
                                                    mage.getFellowMages(),
                                                    ACLMessage.INFORM
                                            )
                                    );
                                    broadcastedFinish=true;
                                }
                            }
                            break;
                        //count finished agents
                        case ACLMessage.INFORM:


                            finishes++;

                            break;
                    }
                }

            } catch (Codec.CodecException | OntologyException ex) {
                log.log(Level.SEVERE, null, ex);
            }
        }
    }

    void chooseRandomTuples(){
        ShopOfferingPrioritedTuples newChosen = shopOfferingPrioritedTuples.selectFreeTuples(maxTuples - myTuples.size());

        if(newChosen.size() > 0) {

            myTuples.concat(newChosen);

            //System.out.println(myTuples);

            TuplesList tuplesList = new TuplesList();
            tuplesList.setTuples(myTuples.convertToJason());
            tuplesList.setID();

            agrees=0;
            myProposeID =tuplesList.getID();
            myAgent.addBehaviour(
                    new BroadCast(
                            mage,
                            tuplesList,
                            mage.getFellowMages(),
                            ACLMessage.PROPOSE
                    )
            );
        }
    }

    void considerPropose(ShopOfferingPrioritedTuples other, TuplesList tuplesListFromSender, ACLMessage message){

        int tuplesToChange = 0;
        boolean senderNeedToChangeTuples =false;
        boolean tuplesWithSamePriority = false;

        //foreach tuple. if there is one i have to consider
        for (ShopOfferTuple tuple: other
             ) {
            if(myTuples.contains(tuple)){

                ShopOfferTuple myTuple = myTuples.get(tuple);


                if(myTuple.getPriority() > tuple.getPriority()){
                    //i stay with it
                    //respond confirm!
                    senderNeedToChangeTuples = true;
                }
                else if(myTuple.getPriority() == tuple.getPriority()) {
                    //send again!
                    //and modify prioriy
                    myTuple.setNewPriority();

                    tuplesWithSamePriority = true;

                }
                else{
                    //i need to change!
                    myTuples.remove(myTuple);
                    tuplesToChange++;

                }
            }

            //delete taken product from shopOfferingPrioritedTuples!
            shopOfferingPrioritedTuples.remove(tuple);


        }

        if(!senderNeedToChangeTuples){
            mage.addBehaviour(new RespondProductListBehaviour(mage,tuplesListFromSender,ACLMessage.CONFIRM,message.getConversationId(),message.getSender()));
        }



        if(tuplesToChange>0 || tuplesWithSamePriority){
            ShopOfferingPrioritedTuples newChosen = shopOfferingPrioritedTuples.selectFreeTuples(tuplesToChange);

            if(newChosen.size()>0) {
                //send new proposal

                myTuples.concat(newChosen);

            }

            TuplesList tuplesList = new TuplesList();
            tuplesList.setTuples(myTuples.convertToJason());

            agrees=0;
            tuplesList.setID();
            myProposeID =tuplesList.getID();
            myAgent.addBehaviour(
                    new BroadCast(
                            mage,
                            tuplesList,
                            mage.getFellowMages(),
                            ACLMessage.PROPOSE
                    )
            );
        }
    }


    @Override
    public boolean done() {
        if(done){
            if(!broadcastedFinish){
                myAgent.addBehaviour(
                        new BroadCast(
                                mage,
                                new Hello(),
                                mage.getFellowMages(),
                                ACLMessage.INFORM
                        )
                );
            }
            mage.setMyTuples(myTuples);
            ((SequentialBehaviour) this.getParent()).addSubBehaviour(new TransferMoneyBehaviour(mage));
            return true;
        }
        return false;
    }
}
