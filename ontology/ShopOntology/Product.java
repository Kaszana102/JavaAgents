package pl.gda.pg.eti.kask.sa.alchemists.ontology.ShopOntology;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import pl.gda.pg.eti.kask.sa.alchemists.ProductList.ProductList;

@Getter
@EqualsAndHashCode
@ToString
public class Product {
    //I hate my life
    /**
     * type: 1-herb, 2-potion,3-equipment
     */
    protected  int type=0;
    protected  String name;

    public int getType(){
        return type;
    }

    @Override
    public String toString(){
        return name;
    }


    static public Product createProduct(String name, int type){
        switch (type){
            case 1: return new Herb(name);
            case 2: return new Potion(name);
            case 3: return new Equipment(name);
            default: return new Herb(name);
        }
    }

}
