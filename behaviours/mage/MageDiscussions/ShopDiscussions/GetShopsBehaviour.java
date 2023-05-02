package pl.gda.pg.eti.kask.sa.alchemists.behaviours.mage.MageDiscussions.ShopDiscussions;

import jade.core.AID;
import jade.core.Agent;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import pl.gda.pg.eti.kask.sa.alchemists.behaviours.FindServiceBehaviour;

import java.util.ArrayList;
import java.util.List;

public class GetShopsBehaviour extends FindServiceBehaviour {

    protected List<AID> shops;

    public GetShopsBehaviour(Agent agent, String serviceType,List<AID> shops) {
        super(agent, serviceType);
        this.shops=shops;
    }

    @Override
    protected void onResult(DFAgentDescription[] services) {
        for (DFAgentDescription service: services
        ) {
            shops.add(service.getName());
        }
    }
}
