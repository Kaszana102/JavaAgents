package pl.gda.pg.eti.kask.sa.alchemists.behaviours.mage.MageDiscussions;

import jade.content.AgentAction;
import jade.content.ContentElement;
import jade.content.lang.Codec;
import jade.content.lang.sl.SLCodec;
import jade.content.onto.OntologyException;
import jade.content.onto.basic.Action;
import jade.core.AID;
import jade.core.behaviours.OneShotBehaviour;
import jade.core.behaviours.SimpleBehaviour;
import jade.domain.DFService;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.domain.FIPAException;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import pl.gda.pg.eti.kask.sa.alchemists.agents.BaseAgent;
import pl.gda.pg.eti.kask.sa.alchemists.agents.Mage;

import java.util.*;
import java.util.logging.Level;
import lombok.extern.java.Log;
import pl.gda.pg.eti.kask.sa.alchemists.ontology.AlchemyOntology;
import pl.gda.pg.eti.kask.sa.alchemists.ontology.MageOnly.CheckShops;
import pl.gda.pg.eti.kask.sa.alchemists.ontology.MageOnly.Hello;

@Log

/** fill the fellow mages hashmap */
public class GetFellowMagesBehaviour extends SimpleBehaviour {


    private boolean requested = false;
    private final Map<AID,String> fellowMages;

    protected Map<AID,Integer> magesPriority = new HashMap<AID,Integer>();
    protected Map<AID,Integer> fellowBudgets =new HashMap<AID,Integer>();
    int counter = 0;

    private Mage mage;

    private boolean helloSent = false;


    public GetFellowMagesBehaviour(Mage agent) {
        super(agent);
        this.myAgent=agent;
        this.mage = agent;

        fellowMages = ((Mage)this.myAgent).getFellowMages();
    }


    @Override
    public void action() {
        if(!requested) {
            requested=true;
            DFAgentDescription dfad = new DFAgentDescription();
            ServiceDescription sd = new ServiceDescription();
            sd.setType("mage");
            dfad.addServices(sd);
            try {
                DFAgentDescription[] services = DFService.search(myAgent, dfad);
                addFellowMages(services);
            } catch (FIPAException ex) {
                log.log(Level.SEVERE, null, ex);
            }
        }
        else{
            MessageTemplate mt = MessageTemplate.MatchPerformative(ACLMessage.PROXY);


            ACLMessage message = myAgent.receive(mt);
            if (message != null) {
                try {
                    ContentElement ce = myAgent.getContentManager().extractContent(message);
                    if (ce instanceof Action) {
                        Hello hello = (Hello) ((Action) ce).getAction();

                        fellowBudgets.put(message.getSender(),hello.getBudget());

                        if(magesPriority.containsKey(message.getSender())) {
                            if (magesPriority.get(message.getSender()) > hello.getPriority()) {
                                //mine has higher priority so it stays
                                ((BaseAgent) myAgent).getActiveConversationIds().add(fellowMages.get(message.getSender()));

                                counter++;

                                System.out.println(myAgent.getName() + " " + fellowMages.get(message.getSender()) + " " + message.getSender().getName());
                            } else if (magesPriority.get(message.getSender()) == hello.getPriority()) {
                                //trouble need to send again!
                                sendHello(message.getSender());
                            } else {
                                //i update conversation ID
                                //add listener to the agent
                                ((BaseAgent) myAgent).getActiveConversationIds().add(message.getConversationId());

                                //update to send
                                fellowMages.remove(message.getSender());
                                fellowMages.put(message.getSender(), message.getConversationId());

                                counter++;
                                System.out.println(myAgent.getName() + " " + message.getConversationId() + " " + message.getSender().getName());
                            }
                        }
                        else{
                            //i update conversation ID
                            //add listener to the agent
                            ((BaseAgent) myAgent).getActiveConversationIds().add(message.getConversationId());

                            //update to send
                            fellowMages.put(message.getSender(), message.getConversationId());

                            counter++;
                            System.out.println(myAgent.getName() + " " + message.getConversationId() + " " + message.getSender().getName());
                        }


                    }
                }catch (Codec.CodecException | OntologyException ex) {
                log.log(Level.SEVERE, null, ex);
                }
            }
        }
    }


    private void addFellowMages(DFAgentDescription[] services){
        for (DFAgentDescription service: services
        ) {
            AID fellowMage = service.getName();
            if(!myAgent.getAID().equals(fellowMage)) { //avoid adding self
                sendHello(fellowMage);
            }
        }
    }


    private void  sendHello(AID target){
        ACLMessage msg;
        msg = new ACLMessage(ACLMessage.PROXY);
        String conversationId = UUID.randomUUID().toString();

        msg.setLanguage(new SLCodec().getName());
        msg.setOntology(AlchemyOntology.getInstance().getName());
        msg.setConversationId(conversationId);
        msg.addReceiver(target);

        Random random = new Random();

        int priority = random.nextInt();
        if(!fellowMages.containsKey(target)) {
            fellowMages.put(target, conversationId); //should actualize if called previously
            magesPriority.put(target, priority);
        }
        else{
            priority = magesPriority.get(target)-1;
        }

        AgentAction agentAction = new Hello(priority,mage.getMyBudget());

        Action action = new Action(target, agentAction);


        try {
            myAgent.getContentManager().fillContent(msg,action);
            myAgent.send(msg);
        } catch (Codec.CodecException | OntologyException ex) {
            log.log(Level.SEVERE, null, ex);
        }

        helloSent = true;


    }


    @Override
    public boolean done() {
        if(counter == fellowMages.size()){
            mage.setFellowBudgets(fellowBudgets);
        }
        return counter == fellowMages.size();
    }
}
