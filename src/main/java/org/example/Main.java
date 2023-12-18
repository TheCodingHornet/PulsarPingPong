package org.example;

import org.apache.pulsar.client.api.PulsarClientException;

public class Main {
    public static void main(String[] args) {
        try {

            // Création d'un producteur de messages
            PulsarProducer pulsarProducer = new PulsarProducer();
            pulsarProducer.startLifecycleListener();
            pulsarProducer.startTickAckProcessor();

            // Création d'un premier consommateur de messages
            PulsarConsumer pulsarConsumer1 = new PulsarConsumer();
            pulsarConsumer1.startListening();

            // Création d'un second consommateur de messages
            PulsarConsumer pulsarConsumer2 = new PulsarConsumer();
            pulsarConsumer2.startListening();

        } catch (PulsarClientException e) {
            throw new RuntimeException(e);
        }
    }
}