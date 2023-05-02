package pl.gda.pg.eti.kask.sa.alchemists.behaviours.mage.MageDiscussions;

import jade.content.AgentAction;
import jade.content.lang.Codec;
import jade.content.lang.sl.SLCodec;
import jade.content.onto.OntologyException;
import jade.content.onto.basic.Action;
import jade.core.AID;
import jade.core.behaviours.OneShotBehaviour;
import jade.lang.acl.ACLMessage;
import lombok.extern.java.Log;
import pl.gda.pg.eti.kask.sa.alchemists.agents.Mage;
import pl.gda.pg.eti.kask.sa.alchemists.ontology.AlchemyOntology;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.logging.Level;


@Log
public class BroadCast extends OneShotBehaviour {

    protected final Map<AID,String> addresses;
    protected final int aclMessagePerformative;
    protected final AgentAction messageContent;
    protected final Mage mage;

    /**
     *  sends messageContent as action
     * @param agent
     * @param messageContent
     * @param addresses
     * @param aclMessagePerformative
     */
    public BroadCast(Mage agent, AgentAction messageContent, Map<AID,String> addresses, int aclMessagePerformative){
        this.myAgent=agent;
        this.mage=agent;

        this.addresses=addresses;
        this.aclMessagePerformative=aclMessagePerformative;
        this.messageContent = messageContent;
    }

    @Override
    public void action() {
        for (AID target: addresses.keySet()
             ) {

            Action action = new Action(target, this.messageContent);

            ACLMessage msg;
            msg = new ACLMessage(aclMessagePerformative);
            String conversationId = addresses.get(target);


            msg.setLanguage(new SLCodec().getName());
            msg.setOntology(AlchemyOntology.getInstance().getName());
            msg.setConversationId(conversationId);
            msg.addReceiver(target);

            try {
                myAgent.getContentManager().fillContent(msg,action);
                mage.getActiveConversationIds().add(conversationId);
                myAgent.send(msg);
            } catch (Codec.CodecException | OntologyException ex) {
                log.log(Level.SEVERE, null, ex);
            }
        }

    }
}
