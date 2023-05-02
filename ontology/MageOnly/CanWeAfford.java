package pl.gda.pg.eti.kask.sa.alchemists.ontology.MageOnly;

import jade.content.AgentAction;
import lombok.*;


@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
@ToString
public class CanWeAfford implements AgentAction {
    private boolean weCanAfford;
}
