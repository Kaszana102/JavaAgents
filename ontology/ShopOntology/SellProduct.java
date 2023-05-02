package pl.gda.pg.eti.kask.sa.alchemists.ontology.ShopOntology;

import jade.content.AgentAction;
import lombok.*;
import pl.gda.pg.eti.kask.sa.alchemists.behaviours.Shopper.SellProductBehaviour;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
@ToString
public class SellProduct implements AgentAction {

    private String productName;
    private int productType;
    private int price;

    public SellProduct(Product product, int price){
        productName=product.name;
        productType=product.type;
        this.price=price;
    }

    public Product getProduct(){
        Product otp;
        switch (productType){
            case 1:
                return new Herb(productName);
            case 2:
                return new Potion(productName);
            case 3:
                return new Equipment(productName);
            default:
                return new Herb(productName);
        }
    }

}

