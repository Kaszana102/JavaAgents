package pl.gda.pg.eti.kask.sa.alchemists.ontology.MageOnly;

import jade.Boot;
import jade.content.AgentAction;
import lombok.*;

import java.util.Random;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
@ToString
public class ShopList  implements AgentAction {
    private String shopOffers;
    private boolean everythingIsOnTheShelves;
}