/**
 * Classe PulsarConsumer pour consommer des messages avec Apache Pulsar.
 * Implémente l'interface MessageHandler pour gérer les messages reçus.
 * Gère la communication sur plusieurs topics, y compris l'envoi des messages de cycle de vie des agents.
 *
 * @author Simon Stephan
 * @email simon.stephan@u-bourgogne.fr
 * @date 18/12/2023
 * @license All rights reserved.
 */
package org.example;

import org.apache.pulsar.client.api.PulsarClientException;
import org.apache.pulsar.shade.com.fasterxml.jackson.core.JsonProcessingException;
import org.example.lib.Communication;
import org.example.lib.MessageHandler;
import org.example.messages.*;

import java.util.UUID;

public class PulsarConsumer implements MessageHandler {

    // Canaux de communication
    static Communication pingtopic;
    static Communication pongtopic;
    static Communication lifecycle;

    private final String id = UUID.randomUUID().toString();

    /**
     * Constructeur par défaut de PulsarConsumer.
     * Initialise les canaux de communication et configure le traitement des messages de cycle de vie.
     * @throws RuntimeException si une erreur PulsarClientException se produit.
     */
    public PulsarConsumer() {
        try {
            pingtopic = new Communication("ping", Communication.Mode.LISTEN, this);
            pongtopic = new Communication("pong", Communication.Mode.SEND, null);
            lifecycle = new Communication("lifecycle", Communication.Mode.SEND);

            sendAgentCreationMessage();

            Runtime.getRuntime().addShutdownHook(new Thread(this::sendDieMessage));
        } catch (PulsarClientException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Envoie un message de mort de l'agent.
     * Doit être appelée lors de l'arrêt du programme pour signaler la fin de l'agent.
     */
    private void sendDieMessage() {
        try {
            lifecycle.sendMessage(new DieMessage(this.getId()));
            System.out.println("DieMessage envoyé par l'agent: " + this.getId());
        } catch (PulsarClientException | JsonProcessingException e) {
            e.printStackTrace();
        }
    }

    /**
     * Envoie un message de création de l'agent.
     * Doit être appelée lors de la création de l'agent.
     */
    private void sendAgentCreationMessage() {
        try {
            lifecycle.sendMessage(new CreateMessage(getId()));
        } catch (PulsarClientException | JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Démarre l'écoute des messages sur le canal de communication.
     * Lance un thread pour écouter les messages entrants.
     */
    public void startListening() {
        Thread listenerThread = new Thread(pingtopic);
        listenerThread.start();
    }

    /**
     * Envoie un PongMessage en réponse à un message reçu.
     */
    private void sendPongMessage() {
        PongMessage PongMessage = new PongMessage(UUID.randomUUID().toString());
        try {
            pongtopic.sendMessage(PongMessage);
            System.out.println("PongMessage envoyé.");
        } catch (PulsarClientException | JsonProcessingException e) {
            e.printStackTrace();
        }
    }

    /**
     * Méthode implémentée de l'interface MessageHandler pour gérer les messages reçus.
     * Traite les PingMessages en envoyant un PongMessage en réponse.
     *
     * @param message Le message reçu à traiter.
     */
    @Override
    public void handle(AbstractMessage message) {
        if (message instanceof PingMessage) {
            System.out.println("PingMessage reçu par l'agent : " + this.getId());
            sendPongMessage();
        }
    }

    /**
     * Obtient l'ID de l'agent.
     *
     * @return String représentant l'ID de l'agent.
     */
    private String getId() {
        return id;
    }
}
