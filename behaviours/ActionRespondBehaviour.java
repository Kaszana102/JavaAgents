package pl.gda.pg.eti.kask.sa.alchemists.behaviours;

import jade.content.AgentAction;
import jade.content.lang.Codec;
import jade.content.lang.sl.SLCodec;
import jade.content.onto.OntologyException;
import jade.content.onto.basic.Action;
import jade.core.AID;
import jade.core.behaviours.OneShotBehaviour;
import jade.lang.acl.ACLMessage;
import lombok.extern.java.Log;
import pl.gda.pg.eti.kask.sa.alchemists.agents.BaseAgent;
import pl.gda.pg.eti.kask.sa.alchemists.ontology.AlchemyOntology;

import java.util.logging.Level;

@Log
/**
 * RETURN ACTION INSTEAD OF PREDICATE(RESULT)
 */
public abstract class ActionRespondBehaviour<T extends AgentAction, E extends BaseAgent> extends OneShotBehaviour {

    protected final E myAgent;

    protected final String conversationId;

    protected final AID participant;

    protected final T action;

    protected int aclMessagePerformative = 0;

    public ActionRespondBehaviour(E agent, T action, String conversationId, AID participant) {
        super(agent);
        this.myAgent = agent;
        this.action = action;
        this.conversationId = conversationId;
        this.participant = participant;
    }

    @Override
    public void action() {
        Action action = performAction();
        ACLMessage msg;


        msg = new ACLMessage(aclMessagePerformative);

        msg.setLanguage(new SLCodec().getName());
        msg.setOntology(AlchemyOntology.getInstance().getName());
        msg.setConversationId(conversationId);
        msg.addReceiver(participant);
        try {
            if (action != null) {
                myAgent.getContentManager().fillContent(msg, action);
            }

            myAgent.send(msg);
        } catch (Codec.CodecException | OntologyException ex) {
            log.log(Level.SEVERE, null, ex);
        }
    }


    /**
     * IT MUST SET aclMessagePerformative!!
     * @return
     */
    protected abstract Action performAction();
}
