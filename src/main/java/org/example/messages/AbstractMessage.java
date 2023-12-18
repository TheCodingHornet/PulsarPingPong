/**
 * @author Simon Stephan
 * @email simon.stephan@u-bourgogne.fr
 * @date 17/12/2023
 * @license All rights reserved.
 */
package org.example.messages;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

import java.io.Serializable;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "type")
@JsonSubTypes({
        @JsonSubTypes.Type(value = PingMessage.class, name = "ping"),
        @JsonSubTypes.Type(value = PongMessage.class, name = "pong"),
        @JsonSubTypes.Type(value = CreateMessage.class, name = "create"),
        @JsonSubTypes.Type(value = DieMessage.class, name = "die"),
})
public abstract class AbstractMessage implements Serializable {

    public String type;
    public String content;

    public abstract String getType();

    public String getContent() {
        return content;
    }

    public AbstractMessage setContent(String content) {
        this.content = content;
        return this;
    }
}
