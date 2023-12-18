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

@JsonTypeName("pong")
public class PongMessage extends AbstractMessage {

    private String content;

    // Constructeur sans argument pour la désérialisation
    public PongMessage() {
    }

    // Constructeur avec arguments
    @JsonCreator
    public PongMessage(@JsonProperty("content") String content) {
        this.content = content;
    }

    public String getContent() {
        return content;
    }

    @Override
    public String getType() {
        return "pong";
    }
}
