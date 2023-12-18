/**
 * Classe PulsarProducer pour gérer la production et la consommation de messages avec Apache Pulsar.
 * Cette classe crée un producteur pour interagir avec des topics spécifiques.
 * Elle gère également l'écoute des cycles de vie des agents et le traitement des acquittements.
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
import org.example.messages.*;

import java.util.HashSet;

public class PulsarProducer {

    private final HashSet<String> agents = new HashSet<>();
    private final Communication lifecycleListener;
    private final Communication pongtopic;
    private final Communication pingtopic;

    public PulsarProducer() throws PulsarClientException {
        // Utilisation de la classe Communication pour gérer les messages
        lifecycleListener = new Communication("lifecycle", Communication.Mode.LISTEN, this::handleLifecycleMessage);
        pongtopic = new Communication("pong", Communication.Mode.LISTEN, this::handlePongMessage);
        pingtopic = new Communication("ping", Communication.Mode.SEND);
    }

    public void startLifecycleListener() {
        new Thread(lifecycleListener).start();
    }

    public void startTickAckProcessor() {
        new Thread(pongtopic).start();
    }

    private void handleLifecycleMessage(AbstractMessage message) throws PulsarClientException, JsonProcessingException {
        if (message instanceof CreateMessage createMessage) {
            synchronized (agents) {
                agents.add(createMessage.getAgentId());
                agents.notifyAll();
                System.out.println("Nouvel agent créé avec ID: " + createMessage.getAgentId());
                sendPingMessages();
            }
        } else if (message instanceof DieMessage dieMessage) {
            synchronized (agents) {
                agents.remove(dieMessage.getAgentId());
                if (agents.isEmpty()) agents.notifyAll();

                System.out.println("Agent mort avec ID: " + dieMessage.getAgentId());
            }
        }
    }

    private void handlePongMessage(AbstractMessage message) throws PulsarClientException, JsonProcessingException, InterruptedException {
        if (message instanceof PongMessage PongMessage) {

            // Affichage du message reçu
            System.out.println("PongMessage reçu: " + PongMessage.getContent());

            // Envoi d'un PingMessage
            sendPingMessages();

            // Attente de 1 seconde entre chaque envoi de PingMessage
            Thread.sleep(1000);
        }
    }

    private void sendPingMessages() throws PulsarClientException, JsonProcessingException {
        if (!agents.isEmpty()) {

            // Envoi d'un PingMessage à chaque agent
            pingtopic.sendMessage(new PingMessage(String.valueOf(System.currentTimeMillis())));

            // Affichage du nombre d'agents
            System.out.println("PingMessage envoyé à " + agents.size() + " agents.");

        }
    }
}
