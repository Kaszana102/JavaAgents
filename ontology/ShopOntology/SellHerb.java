package pl.gda.pg.eti.kask.sa.alchemists.ontology.ShopOntology;

import jade.content.AgentAction;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 *
 * @author psysiu
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
@ToString
public class SellHerb implements AgentAction {
    
    private Herb herb;
    private int price;
    
}
