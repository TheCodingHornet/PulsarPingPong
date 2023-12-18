/**
 * Classe Communication pour gérer la communication avec Apache Pulsar.
 * Cette classe permet l'envoi et la réception de messages sur un topic donné.
 * Elle peut être configurée pour fonctionner en mode d'écoute, d'envoi, ou les deux.
 *
 * @author Simon Stephan
 * @email simon.stephan@u-bourgogne.fr
 * @date 17/12/2023
 * @license All rights reserved.
 */
package org.example.lib;

import org.apache.pulsar.client.api.*;
import org.apache.pulsar.shade.com.fasterxml.jackson.core.JsonProcessingException;
import org.apache.pulsar.shade.com.fasterxml.jackson.databind.ObjectMapper;
import org.example.messages.AbstractMessage;

public class Communication implements Runnable {

    // TODO: Change this to your Pulsar broker URL
    private static final String SERVICE_URL = "pulsar://127.0.0.1:52661";

    private final MessageHandler messageHandler;
    private final PulsarClient client;
    private final String topic;
    private final Mode mode;

    private Producer<byte[]> producer;
    private Consumer<byte[]> consumer;

    /**
     * Constructeur de Communication en mode d'écoute.
     *
     * @param topic          Le nom du topic.
     * @param messageHandler Le gestionnaire de messages.
     * @throws PulsarClientException Si la création du client Pulsar échoue.
     */
    public Communication(String topic, MessageHandler messageHandler) throws PulsarClientException {
        this(topic, Mode.LISTEN, messageHandler);
    }

    /**
     * Constructeur de Communication en mode spécifié.
     *
     * @param topic Le nom du topic.
     * @param mode  Le mode de la communication (écoute, envoi ou les deux).
     * @throws PulsarClientException Si la création du client Pulsar échoue.
     */
    public Communication(String topic, Mode mode) throws PulsarClientException {
        this(topic, mode, null);
    }

    /**
     * Constructeur principal de Communication.
     * Initialise le client Pulsar, le producteur et/ou le consommateur selon le mode.
     *
     * @param topic          Le nom du topic.
     * @param mode           Le mode de la communication (écoute, envoi ou les deux).
     * @param messageHandler Le gestionnaire de messages, facultatif.
     * @throws PulsarClientException Si la création du client Pulsar échoue.
     */
    public Communication(String topic, Mode mode, MessageHandler messageHandler) throws PulsarClientException {
        this.messageHandler = messageHandler;
        this.client = PulsarClient.builder().serviceUrl(SERVICE_URL).build();
        this.topic = topic;
        this.mode = mode;

        if (mode == Mode.SEND || mode == Mode.FULL)
            this.producer = client.newProducer().topic(topic).create();

        if (mode == Mode.LISTEN || mode == Mode.FULL) {
            this.consumer = client.newConsumer()
                    .topic(this.topic)
                    .subscriptionType(SubscriptionType.Shared)
                    .subscriptionName(java.util.UUID.randomUUID().toString())
                    .subscribe();
        }
    }

    /**
     * Envoie un message sur le topic.
     *
     * @param message Le message à envoyer.
     * @throws PulsarClientException   Si l'envoi du message échoue.
     * @throws JsonProcessingException Si la conversion du message en JSON échoue.
     */
    public void sendMessage(AbstractMessage message) throws PulsarClientException, JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        String jsonMessage = mapper.writeValueAsString(message);
        byte[] messageBytes = jsonMessage.getBytes();
        producer.send(messageBytes);
    }

    /**
     * Reçoit un message du topic.
     *
     * @return Le message reçu.
     * @throws PulsarClientException   Si la réception du message échoue.
     * @throws JsonProcessingException Si la conversion du message en JSON échoue.
     */
    public Message<byte[]> receiveMessage() throws PulsarClientException, JsonProcessingException, InterruptedException {
        Message<byte[]> message = consumer.receive();
        ObjectMapper mapper = new ObjectMapper();
        AbstractMessage messageObject = mapper.readValue(new String(message.getData()), AbstractMessage.class);

        if (messageHandler != null) {
            // Appelle le gestionnaire de messages avec le bon type de message
            messageHandler.handle(messageObject);
        }

        consumer.acknowledge(message);
        return message;
    }

    /**
     * Ferme le producteur, le consommateur et le client Pulsar.
     *
     * @throws PulsarClientException Si la fermeture de l'un des composants échoue.
     */
    public void close() throws PulsarClientException {
        if (producer != null) producer.close();
        if (consumer != null) consumer.close();
        client.close();
    }

    /**
     * Méthode run pour permettre l'exécution de l'objet Communication dans un thread.
     * Écoute les messages si le mode est défini pour écouter.
     */
    @Override
    public void run() {
        if (mode == Mode.LISTEN || mode == Mode.FULL) {
            try {
                while (!Thread.currentThread().isInterrupted()) {
                    receiveMessage();
                }
            } catch (PulsarClientException | JsonProcessingException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }

    /**
     * Énumération des modes de communication.
     */
    public enum Mode {
        LISTEN, SEND, FULL
    }
}
