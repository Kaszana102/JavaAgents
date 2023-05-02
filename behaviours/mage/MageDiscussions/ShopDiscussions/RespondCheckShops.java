package pl.gda.pg.eti.kask.sa.alchemists.behaviours.mage.MageDiscussions.ShopDiscussions;

import jade.content.onto.basic.Action;
import jade.core.AID;
import pl.gda.pg.eti.kask.sa.alchemists.agents.Mage;
import pl.gda.pg.eti.kask.sa.alchemists.behaviours.ActionRespondBehaviour;
import pl.gda.pg.eti.kask.sa.alchemists.ontology.MageOnly.CheckShops;

public class RespondCheckShops extends ActionRespondBehaviour<CheckShops, Mage> {



    public RespondCheckShops(Mage agent, CheckShops action, int performative   , String conversationId, AID participant) {
        super(agent, action, conversationId, participant);
        this.aclMessagePerformative = performative;
    }

    @Override
    protected Action performAction() {

        return new Action(participant, action);
    }

}

