package pl.gda.pg.eti.kask.sa.alchemists.ontology.MageOnly;

import jade.content.AgentAction;
import jade.core.AID;
import lombok.*;

import java.util.List;


@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
@ToString
public class CheckShops implements AgentAction {
    private int priority;
    private String serviceName;
}
