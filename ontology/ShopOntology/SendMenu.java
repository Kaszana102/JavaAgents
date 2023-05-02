package pl.gda.pg.eti.kask.sa.alchemists.ontology.ShopOntology;


import jade.content.AgentAction;
import lombok.*;


@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
@ToString
public class SendMenu implements AgentAction {
    private String shopOfferings; //IN JSON FORMAT or something similar
    private int shopsNumber = 1;
}
