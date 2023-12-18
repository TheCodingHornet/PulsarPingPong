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

@JsonTypeName("ping")
public class PingMessage extends AbstractMessage {

    private String content;

    // Constructeur sans argument pour la désérialisation
    public PingMessage() {
    }

    // Constructeur avec arguments
    @JsonCreator
    public PingMessage(@JsonProperty("content") String content) {
        this.content = content;
    }

    public String getContent() {
        return content;
    }

    @Override
    public String getType() {
        return "ping";
    }
}