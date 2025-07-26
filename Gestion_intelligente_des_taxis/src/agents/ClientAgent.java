
package agents;

import jade.core.Agent;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import ui.TaxiMapUI;

import java.awt.*;

public class ClientAgent extends Agent {
    @Override
    protected void setup() {
        System.out.println("ClientAgent " + getLocalName() + " démarré");

        // Position aléatoire pour le client
        int x = 50 + (int)(Math.random() * 400);
        int y = 50 + (int)(Math.random() * 400);
        Color color = new Color((float)Math.random(), (float)Math.random(), (float)Math.random());

        TaxiMapUI.getInstance().addClient(getLocalName(), new Point(x, y), color);

        // Envoi de la requête au dispatcher
        ACLMessage msg = new ACLMessage(ACLMessage.REQUEST);
        msg.setContent("Demande de taxi: PositionA -> PositionB");
        msg.addReceiver(new jade.core.AID("dispatcher", jade.core.AID.ISLOCALNAME));
        send(msg);

        addBehaviour(new jade.core.behaviours.CyclicBehaviour() {
            @Override
            public void action() {
                ACLMessage response = receive(MessageTemplate.MatchPerformative(ACLMessage.INFORM));
                if (response != null) {
                    System.out.println(getLocalName() + " a reçu une confirmation : " + response.getContent());
                } else block();
            }
        });
    }
}
