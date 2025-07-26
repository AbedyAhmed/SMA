package agents;

import jade.core.Agent;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import jade.core.AID;

import java.util.HashMap;
import java.util.Map;

@SuppressWarnings("unused")
public class DispatcherAgent extends Agent {
    private AID clientRequester;
    private Map<AID, Integer> proposals = new HashMap<>();

    @Override
    protected void setup() {
        System.out.println("DispatcherAgent démarré");

        addBehaviour(new jade.core.behaviours.CyclicBehaviour() {
            private int responsesReceived = 0;

            @Override
            public void action() {
                ACLMessage msg = receive();
                if (msg != null) {
                    switch (msg.getPerformative()) {
                        case ACLMessage.REQUEST:
                            clientRequester = msg.getSender();
                            System.out.println("Demande reçue de " + clientRequester.getLocalName());

                            ACLMessage cfp = new ACLMessage(ACLMessage.CFP);
                            cfp.setContent("Client à récupérer en PositionA");
                            cfp.addReceiver(new AID("taxi1", AID.ISLOCALNAME));
                            cfp.addReceiver(new AID("taxi2", AID.ISLOCALNAME));
                            send(cfp);
                            responsesReceived = 0;
                            proposals.clear();
                            break;

                        case ACLMessage.PROPOSE:
                            System.out.println("Proposition reçue de " + msg.getSender().getLocalName());
                            try {
                                int score = Integer.parseInt(msg.getContent());
                                proposals.put(msg.getSender(), score);
                                responsesReceived++;
                            } catch (NumberFormatException e) {
                                System.out.println("Erreur de score dans la proposition");
                            }

                            if (responsesReceived == 2) {
                                AID bestTaxi = proposals.entrySet().stream()
                                        .min(Map.Entry.comparingByValue())
                                        .map(Map.Entry::getKey).orElse(null);

                                for (AID taxi : proposals.keySet()) {
                                    ACLMessage reply = new ACLMessage(
                                            taxi.equals(bestTaxi) ? ACLMessage.ACCEPT_PROPOSAL : ACLMessage.REJECT_PROPOSAL);
                                    reply.addReceiver(taxi);
                                    reply.setContent(taxi.equals(bestTaxi) ? "Course acceptée" : "Course refusée");
                                    send(reply);
                                }
                            }
                            break;
                    }
                } else block();
            }
        });
    }

    public void notifyClient(String message) {
        ACLMessage inform = new ACLMessage(ACLMessage.INFORM);
        inform.addReceiver(clientRequester);
        inform.setContent(message);
        send(inform);
    }
}
