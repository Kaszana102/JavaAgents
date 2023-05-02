package pl.gda.pg.eti.kask.sa.alchemists.ontology.ShopOntology;

import jade.content.Concept;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 *
 * @author psysiu
 */
@Getter
@Setter
@EqualsAndHashCode(callSuper=true)
public class Potion extends Product implements Concept {

    public static final int TYPE = 2;

    public Potion(){
        this.type=TYPE;
    }

    public Potion(String name)
    {
        this.name=name;
        this.type=TYPE;
    }


    @Override
    public String toString(){
        return name;
    }

    @Override
    public int getType(){
        return type;
    }
}
