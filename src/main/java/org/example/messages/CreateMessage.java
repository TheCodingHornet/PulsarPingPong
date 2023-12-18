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

@JsonTypeName("create")
public class CreateMessage extends AbstractMessage {

    private final String agentId;

    @JsonCreator
    public CreateMessage(@JsonProperty("agentId") String agentId) {
        this.agentId = agentId;
    }

    public String getAgentId() {
        return agentId;
    }

    @Override
    public String getType() {
        return "create";
    }
}