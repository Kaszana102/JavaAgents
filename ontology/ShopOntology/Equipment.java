package pl.gda.pg.eti.kask.sa.alchemists.ontology.ShopOntology;

import jade.content.Concept;
import lombok.*;

@Getter
@Setter
@EqualsAndHashCode(callSuper=true)

public class Equipment extends Product implements Concept {
    public static final int TYPE = 3;
    public Equipment(){
        this.type=TYPE;
    }

    public Equipment(String name)
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
