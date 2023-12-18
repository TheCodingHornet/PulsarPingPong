/**
 * @author Simon Stephan
 * @email simon.stephan@u-bourgogne.fr
 * @date 17/12/2023
 * @license All rights reserved.
 */
package org.example.lib;

import org.apache.pulsar.client.api.PulsarClientException;
import org.apache.pulsar.shade.com.fasterxml.jackson.core.JsonProcessingException;
import org.example.messages.AbstractMessage;

public interface MessageHandler {
    void handle(AbstractMessage message) throws PulsarClientException, JsonProcessingException, InterruptedException;
}
