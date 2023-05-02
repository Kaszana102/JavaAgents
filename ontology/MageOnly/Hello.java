package pl.gda.pg.eti.kask.sa.alchemists.ontology.MageOnly;


import jade.content.AgentAction;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
@ToString
public class Hello implements AgentAction {
    private int priority;
    private int budget;
}
