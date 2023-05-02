package pl.gda.pg.eti.kask.sa.alchemists.behaviours.mage.MageDiscussions.ShopDiscussions;

import jade.core.AID;
import jade.core.behaviours.SequentialBehaviour;
import jade.core.behaviours.SimpleBehaviour;
import pl.gda.pg.eti.kask.sa.alchemists.ProductList.ShopOfferings;
import pl.gda.pg.eti.kask.sa.alchemists.agents.Mage;
import pl.gda.pg.eti.kask.sa.alchemists.behaviours.mage.MenuBehaviours.RequestMenuBehaviour;
import pl.gda.pg.eti.kask.sa.alchemists.ontology.ShopOntology.SendMenu;

import java.util.List;

public class GetMenusBehaviour extends SimpleBehaviour {

    private boolean done =false;
    private boolean requested =false;
    protected List<AID> shops;
    private ShopOfferings shopOffers;
    private Mage mage;

    private SequentialBehaviour behaviour;

    //                                         IN
    public GetMenusBehaviour(Mage mage, List<AID> shops){
        this.mage = mage;
        this.shops=shops;

        behaviour = new SequentialBehaviour(mage);
    }



    @Override
    public void action() {
        if(!requested){
            requested = true;
            for (AID shop: shops
                 ) {
                SendMenu action = new SendMenu();
                behaviour.addSubBehaviour(new RequestMenuBehaviour(mage,shop,action));
            }

            mage.addBehaviour(behaviour);
        }
    }

    @Override
    public boolean done() {
        return behaviour.done();
    }
}
