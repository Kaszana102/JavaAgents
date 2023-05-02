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
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.java.Log;
import pl.gda.pg.eti.kask.sa.alchemists.ProductList.ShopOfferingPrioritedTuples;
import pl.gda.pg.eti.kask.sa.alchemists.agents.Mage;
import pl.gda.pg.eti.kask.sa.alchemists.behaviours.mage.BuyProductsBehaviour;
import pl.gda.pg.eti.kask.sa.alchemists.behaviours.mage.MageDiscussions.BroadCast;
import pl.gda.pg.eti.kask.sa.alchemists.ontology.MageOnly.Hello;
import pl.gda.pg.eti.kask.sa.alchemists.ontology.MageOnly.SendMoney;
import pl.gda.pg.eti.kask.sa.alchemists.ontology.MageOnly.TuplesList;

import java.util.List;
import java.util.Vector;
import java.util.logging.Level;


@AllArgsConstructor
@Getter
@Setter
class Tuple{
    public SendMoney sendMoney;
    public AID fellowMage;
}


@Log
//TODO
//na razie pominąc tę fazę!
public class TransferMoneyBehaviour extends SimpleBehaviour {

    private final Mage mage;
    private boolean done=false;

    private int hellos = 0;
    private boolean once = true;

    private int totalBudget;

    private int moneyToSpare;
    private int moneyINeedToBorrow=-1;

    private int confirms =0;
    private boolean confirmSent=false;

    List<Tuple> previousRequests = new Vector<Tuple>();

    public TransferMoneyBehaviour(Mage mage){
        this.mage=mage;
    }

    @Override
    public void action() {
        if(once){
            once =false;

            Hello hello = new Hello();

            myAgent.addBehaviour(
                    new BroadCast(
                            mage,
                            hello,
                            mage.getFellowMages(),
                            ACLMessage.PROXY
                    )
            );

            if(mage.getFellowMages().size() == 0){
                calculateNeededMoney();
            }

        }

        checkMessages();



        if(moneyToSpare>0){
            if(previousRequests.size()>0) {
                SendMoney sendMoney = new SendMoney(); //which will be sent
                SendMoney sendMoneyRequest=previousRequests.get(0).sendMoney; //from previous request. they are different!
                AID fellowMage=previousRequests.get(0).fellowMage;
                if(moneyToSpare<sendMoneyRequest.getMoney()){
                    sendMoney.setMoney(moneyToSpare);
                    moneyToSpare=0;
                    sendMoneyRequest.setMoney(sendMoneyRequest.getMoney()-moneyToSpare);
                    previousRequests.remove(0);
                    previousRequests.add( new Tuple(sendMoneyRequest,fellowMage)); //move it to the end
                }
                else{
                    moneyToSpare-=sendMoney.getMoney();
                    previousRequests.remove(0);
                    sendMoney.setMoney(sendMoneyRequest.getMoney());
                }

                mage.addBehaviour(new SendMoneyBehaviour(mage,sendMoney,ACLMessage.PROPOSE, mage.getFellowMages().get(fellowMage),fellowMage));
            }
        }



        if(moneyINeedToBorrow == 0) {
            if (!confirmSent) {
                System.out.println(mage.getName() + " FINALLY GOT MONEY");
                myAgent.addBehaviour(
                        new BroadCast(
                                mage,
                                new Hello(),
                                mage.getFellowMages(),
                                ACLMessage.INFORM));
                confirmSent=true;
            }
            if(confirms == mage.getFellowMages().size()){
                done = true;
            }
        }

    }

    private MessageTemplate createMessageTemplate(){
        MessageTemplate mtMages = MessageTemplate.not(MessageTemplate.MatchAll());
        for(AID id: mage.getFellowMages().keySet()){
            mtMages = MessageTemplate.or(mtMages,MessageTemplate.MatchSender(id));
        }

        MessageTemplate mtPerformatives = MessageTemplate.not(MessageTemplate.MatchAll());
        mtPerformatives = MessageTemplate.or(mtPerformatives,MessageTemplate.MatchPerformative(ACLMessage.REQUEST));
        mtPerformatives = MessageTemplate.or(mtPerformatives,MessageTemplate.MatchPerformative(ACLMessage.PROPOSE));
        mtPerformatives = MessageTemplate.or(mtPerformatives,MessageTemplate.MatchPerformative(ACLMessage.PROXY));
        mtPerformatives = MessageTemplate.or(mtPerformatives,MessageTemplate.MatchPerformative(ACLMessage.REFUSE));
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
                        case ACLMessage.REQUEST:
                            SendMoney sendMoney = (SendMoney) ((Action) ce).getAction();

                            if(moneyToSpare>0){
                                if(sendMoney.getMoney()>=moneyToSpare) {
                                    previousRequests.add(new Tuple(new SendMoney(sendMoney.getMoney() - moneyToSpare),message.getSender()));
                                    sendMoney.setMoney(moneyToSpare);
                                    moneyToSpare = 0;

                                }
                                else{
                                    //money to send < moneyToSpare
                                    moneyToSpare-=sendMoney.getMoney();
                                }

                                //send ACCEPT
                                mage.addBehaviour(new SendMoneyBehaviour(mage,sendMoney,ACLMessage.PROPOSE, message.getConversationId(),message.getSender()));
                            }
                            else{
                                previousRequests.add(new Tuple(sendMoney,message.getSender()));
                                mage.addBehaviour(new SendMoneyBehaviour(mage,new SendMoney(0),ACLMessage.REFUSE, message.getConversationId(),message.getSender()));
                            }


                            break;

                        //you receive some money
                        case ACLMessage.PROPOSE:
                            sendMoney = (SendMoney) ((Action) ce).getAction();
                            if(moneyINeedToBorrow>0){
                                if( moneyINeedToBorrow > sendMoney.getMoney()){
                                    System.out.println(mage.getName() + " RECEIVED MONEY: " + sendMoney.getMoney()+ " FROM " + message.getSender().getName());
                                    //take all
                                    moneyINeedToBorrow-=sendMoney.getMoney();
                                    sendMoney.setMoney(0);
                                }
                                else{
                                    System.out.println(mage.getName() + " RECEIVED MONEY: " + moneyINeedToBorrow+ " FROM " + message.getSender().getName());
                                    moneyINeedToBorrow=0;
                                    sendMoney.setMoney(sendMoney.getMoney()-moneyINeedToBorrow);
                                }
                                System.out.println(mage.getName() + " I NEED TO BORROW: " + moneyINeedToBorrow);
                            }

                            //send response
                            mage.addBehaviour(new SendMoneyBehaviour(mage,sendMoney,ACLMessage.REFUSE, message.getConversationId(),message.getSender())); //XDDDD

                            break;


                        //agent ready to talk
                        case ACLMessage.PROXY:
                            hellos++;
                            if(hellos == mage.getFellowMages().size()){
                                System.out.println("GOT ALL FOR MONEY TRANSFER");
                                calculateNeededMoney();
                            }
                            break;

                        //get you money back
                        case ACLMessage.REFUSE:
                            sendMoney = (SendMoney) ((Action) ce).getAction();
                            if(sendMoney.getMoney()>0) {
                                System.out.println(mage.getName() + " MONEY returned: " + sendMoney.getMoney());
                            }
                            moneyToSpare+=sendMoney.getMoney();


                            break;


                        //get all satisfactions
                        case ACLMessage.INFORM:
                            confirms++;
                            //System.out.println(mage.getName() + " CONFIRM RECEIVED");

                            break;
                    }
                }

            } catch (Codec.CodecException | OntologyException ex) {
                log.log(Level.SEVERE, null, ex);
            }
        }
    }


    void calculateNeededMoney(){
        totalBudget = mage.getMyBudget() + mage.getFellowBudgets().values().stream().mapToInt(Integer::intValue).sum();
        int totalCost = mage.getBestOffers().getTotalPrice();
        int budgetINeedToPay = mage.getMyTuples().converToShopOfferings().getTotalPrice();

        double a = totalCost;
        double b = totalBudget;

        double percentage=a/b;

        if(Math.ceil( mage.getMyBudget() * percentage) >= budgetINeedToPay){
            System.out.println(mage.getName() +  " SATISFIED WITH MONEY ");
            moneyToSpare = (int) Math.ceil( mage.getMyBudget() * percentage) - budgetINeedToPay; //TODO SPRAWDŹ CZY DOBRZE

            System.out.println(mage.getName() + " MONEY TO SPARE: " + moneyToSpare);
            moneyINeedToBorrow=0;
        }
        else{
            //broadcast need
            moneyINeedToBorrow=budgetINeedToPay- (int) Math.ceil( mage.getMyBudget() * percentage);

            System.out.println(mage.getName() + " NEED MONEY: " + moneyINeedToBorrow);

            SendMoney sendMoney = new SendMoney();
            sendMoney.setMoney(moneyINeedToBorrow);

            myAgent.addBehaviour(
                    new BroadCast(
                            mage,
                            sendMoney,
                            mage.getFellowMages(),
                            ACLMessage.REQUEST));
        }

    }


    @Override
    public boolean done() {
        if(done) {
            System.out.println(mage.getName() + " FINISHED TRANSFERRING ");
            ((SequentialBehaviour) this.getParent()).addSubBehaviour(new BuyProductsBehaviour(mage));
        }
        return done;
    }
}
