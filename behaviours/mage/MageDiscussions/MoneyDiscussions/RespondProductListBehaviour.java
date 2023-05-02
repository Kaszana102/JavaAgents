package pl.gda.pg.eti.kask.sa.alchemists.behaviours.mage.MageDiscussions.MoneyDiscussions;

import jade.content.onto.basic.Action;
import jade.core.AID;
import pl.gda.pg.eti.kask.sa.alchemists.agents.Mage;
import pl.gda.pg.eti.kask.sa.alchemists.behaviours.ActionRespondBehaviour;
import pl.gda.pg.eti.kask.sa.alchemists.ontology.MageOnly.CheckShops;
import pl.gda.pg.eti.kask.sa.alchemists.ontology.MageOnly.TuplesList;

public class RespondProductListBehaviour  extends ActionRespondBehaviour<TuplesList, Mage> {
    public RespondProductListBehaviour(Mage agent, TuplesList action, int performative   , String conversationId, AID participant) {
        super(agent, action, conversationId, participant);
        this.aclMessagePerformative = performative;
    }

    @Override
    protected Action performAction() {

        return new Action(participant, action);
    }

}
