package pl.gda.pg.eti.kask.sa.alchemists.ontology.MageOnly;

import jade.content.AgentAction;
import lombok.*;
import pl.gda.pg.eti.kask.sa.alchemists.ProductList.ShopOfferingPrioritedTuples;

import java.util.Random;


@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
@ToString
public class TuplesList implements AgentAction {
    private int ID;
    private String tuples;

    public void setID(){
        Random random = new Random();
        ID=random.nextInt();
    }
}
