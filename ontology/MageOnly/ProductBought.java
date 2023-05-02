package pl.gda.pg.eti.kask.sa.alchemists.ontology.MageOnly;

import jade.content.AgentAction;
import lombok.*;
import pl.gda.pg.eti.kask.sa.alchemists.ProductList.ProductList;
import pl.gda.pg.eti.kask.sa.alchemists.ontology.ShopOntology.Product;


@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
@ToString
public class ProductBought implements AgentAction {
    String productsList;
}
