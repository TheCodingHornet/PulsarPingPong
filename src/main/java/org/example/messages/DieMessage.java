/**
 * @author Simon Stephan
 * @email simon.stephan@u-bourgogne.fr
 * @date 17/12/2023
 * @license All rights reserved.
 */
package org.example.messages;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;

@JsonTypeName("die")
public class DieMessage extends AbstractMessage {

    private final String agentId; // Ajout d'un champ pour l'ID de l'agent

    // Constructeur sans argument pour la désérialisation
    public DieMessage() {
        this.agentId = null; // Initialise avec une valeur par défaut ou null
    }

    // Constructeur avec arguments
    @JsonCreator
    public DieMessage(@JsonProperty("agentId") String agentId) {
        this.agentId = agentId;
    }

    public String getAgentId() {
        return agentId;
    }

    @Override
    public String getType() {
        return "die";
    }
}