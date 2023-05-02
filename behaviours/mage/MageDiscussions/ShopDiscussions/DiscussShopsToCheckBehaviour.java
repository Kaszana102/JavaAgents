package pl.gda.pg.eti.kask.sa.alchemists.behaviours.mage.MageDiscussions.ShopDiscussions;

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
import pl.gda.pg.eti.kask.sa.alchemists.ontology.MageOnly.CheckShops;
import pl.gda.pg.eti.kask.sa.alchemists.ontology.ShopOntology.SendMenu;

import java.util.*;
import java.util.logging.Level;

@Log
public class DiscussShopsToCheckBehaviour extends SimpleBehaviour {

    private final Mage mage;

    /** show i willy check by my self and push it forward*/
    //private final Vector<String> serviceIWillCheck= new Vector<String>();
    private final Map<String,Integer> serviceIWillCheck = new HashMap<String, Integer>();



    private final Vector<String> chosenServices= new Vector<String>();
    private final Vector<String> freeServices= new Vector<String>();
    private final Vector<AID> confirms = new Vector<AID>();

    private boolean checkedServices = false;


    //shops i will check
    protected Vector<AID> shops = new Vector<AID>() ;

    protected int menusINeed = 3;

    private SequentialBehaviour behaviour;

    public  DiscussShopsToCheckBehaviour(Mage mage){
        this.myAgent=mage;
        this.mage = mage;

        freeServices.add("herbalist");
        freeServices.add("alchemist");
        freeServices.add("blacksmith");

    }


    @Override
    public void action() {
        checkMessages();

        if(serviceIWillCheck.size()==0){
            sendNewPropose();
        }

        if(!checkedServices){
            if(confirms.size() == mage.getFellowMages().size()){
                checkedServices = true;
                if(serviceIWillCheck.size() != 0) {
                    System.out.println(mage.getName() + " went for service: "+ serviceIWillCheck);
                    goForMenus();
                }
            }
        }

        if(behaviour!= null){
            if(behaviour.done()){
                //send menus to everyone
                System.out.println(mage.getName() + " Came back from shops with menus!: " + serviceIWillCheck);


                SendMenu sendMenu = new SendMenu();
                sendMenu.setShopOfferings(mage.getShopOffersMini().packToJson()); //send what i got
                sendMenu.setShopsNumber(serviceIWillCheck.size());

                mage.getShopOffers().concat(mage.getShopOffersMini());
                menusINeed-=serviceIWillCheck.size();

                myAgent.addBehaviour(
                        new BroadCast(
                                mage,
                                sendMenu,
                                mage.getFellowMages(),
                                ACLMessage.INFORM));

                behaviour = null;
            }
        }

    }

    private MessageTemplate createMessageTemplate(){
        MessageTemplate mtMages = MessageTemplate.not(MessageTemplate.MatchAll());
        for(AID id: mage.getFellowMages().keySet()){
            mtMages = MessageTemplate.or(mtMages,MessageTemplate.MatchSender(id));
        }

        MessageTemplate mtPerformatives = MessageTemplate.not(MessageTemplate.MatchAll());
        mtPerformatives = MessageTemplate.or(mtPerformatives,MessageTemplate.MatchPerformative(ACLMessage.PROPOSE));
        mtPerformatives = MessageTemplate.or(mtPerformatives,MessageTemplate.MatchPerformative(ACLMessage.INFORM));
        mtPerformatives = MessageTemplate.or(mtPerformatives,MessageTemplate.MatchPerformative(ACLMessage.CONFIRM));

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
                        case ACLMessage.PROPOSE:
                            //System.out.println("propose received!");
                            CheckShops checkShops = (CheckShops) ((Action) ce).getAction();

                            //if concflict
                            if( serviceIWillCheck.containsKey( checkShops.getServiceName())){
                                considerPriorities(checkShops,message);
                            }
                            else{
                                if(!chosenServices.contains(checkShops.getServiceName())) {
                                    chosenServices.add(checkShops.getServiceName());
                                }
                                freeServices.remove(checkShops.getServiceName());

                                mage.addBehaviour(new RespondCheckShops(mage, checkShops, ACLMessage.CONFIRM, message.getConversationId(), message.getSender()));
                            }


                            break;

                        //get menus
                        case ACLMessage.INFORM:
                            SendMenu sendMenu = (SendMenu) ((Action) ce).getAction();
                            System.out.println(mage.getName() + " received menu from " + message.getSender().getName());
                            ShopOfferings receivedList = new ShopOfferings();
                            receivedList.unpackFromJson(sendMenu.getShopOfferings());
                            mage.getShopOffers().concat(receivedList);
                            menusINeed-=sendMenu.getShopsNumber();

                            break;
                        //everyone needs to confirm that that person is going for that service
                        case ACLMessage.CONFIRM:
                            checkShops = (CheckShops) ((Action) ce).getAction(); //always used xd
                            if(serviceIWillCheck.containsKey( checkShops.getServiceName())) {
                                if (!confirms.contains(message.getSender())) {
                                    confirms.add(message.getSender());
                                }
                            }

                            break;
                    }
                }

            } catch (Codec.CodecException | OntologyException ex) {
                log.log(Level.SEVERE, null, ex);
            }
        }
    }

    private void considerPriorities(CheckShops checkShops, ACLMessage message){
        String service = checkShops.getServiceName();


        if ( serviceIWillCheck.get(checkShops.getServiceName()) == checkShops.getPriority()){
            //we've got trouble XD
            //send the same thing again xd

            Random random = new Random();
            serviceIWillCheck.replace(service,random.nextInt() % 128);//rand
            checkShops.setPriority(serviceIWillCheck.get(service));
            mage.addBehaviour(new RespondCheckShops(mage, checkShops, ACLMessage.PROPOSE, message.getConversationId(), message.getSender()));
        }
        else if (serviceIWillCheck.get(checkShops.getServiceName()) > checkShops.getPriority()) {
            //i will still check this service
            //do nothing
            if (!confirms.contains(message.getSender())) {
                confirms.add(message.getSender());
            }

        } else {
            // i need to choose new service
            serviceIWillCheck.remove(checkShops.getServiceName());
            confirms.removeAllElements();
            sendNewPropose();
        }

    }



    private void sendNewPropose(){
        Random random = new Random();

        int numberOfServicesToPropose = (int)  Math.ceil(((double)3 )/ (mage.getFellowMages().size() + 1));

        for(int i=0;  i < numberOfServicesToPropose ;i++) {
            String service = randomFreeService();
            if (!service.equals("")) {


                CheckShops checkShops = new CheckShops();
                checkShops.setServiceName(service);

                int myPriority = random.nextInt() % 128;

                checkShops.setPriority(myPriority);
                serviceIWillCheck.put(service, myPriority);

                myAgent.addBehaviour(
                        new BroadCast(
                                mage,
                                checkShops,
                                mage.getFellowMages(),
                                ACLMessage.PROPOSE));
            }
        }
    }

    private String randomFreeService(){
        String service;
        if(freeServices.size() > 0) {
            Random random = new Random();
            int index = Math.abs( random.nextInt()) % freeServices.size();
            service = freeServices.get(index);
            freeServices.remove(index);
            chosenServices.add(service);
        }
        else{
            service = "";
        }
        return service;
    }

    private void goForMenus(){

        behaviour = new SequentialBehaviour();
        for (String service:
             serviceIWillCheck.keySet()) {
            behaviour.addSubBehaviour(new GetShopsBehaviour(mage,service,shops));//add all services' shops
        }

        behaviour.addSubBehaviour(new GetMenusBehaviour(mage,shops));

        mage.addBehaviour(behaviour);

    }


    @Override
    public boolean done() {
        return menusINeed == 0;
    }
}
