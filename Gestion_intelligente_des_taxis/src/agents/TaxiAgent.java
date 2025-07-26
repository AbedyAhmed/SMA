package agents;

import jade.core.Agent;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import ui.TaxiMapUI;

import java.awt.*;
import java.util.Random;

@SuppressWarnings("unused")
public class TaxiAgent extends Agent {
    private int distance;
    private boolean disponible;
    private Point position;
    private Point basePosition;
    public static TaxiMapUI mapUI;
    private final Point clientPosition = new Point(150, 200);

    private int totalCourses = 0;
    private int totalDistance = 0;

    @Override
    protected void setup() {
        System.out.println("TaxiAgent " + getLocalName() + " prêt");

        Random rand = new Random();
        distance = rand.nextInt(10) + 1;
        disponible = rand.nextBoolean();
        basePosition = new Point(rand.nextInt(300), rand.nextInt(300));
        position = new Point(basePosition);

        if (mapUI != null) {
            mapUI.updateTaxi(getLocalName(), position, disponible);
            mapUI.updateStats(getLocalName(), totalCourses, totalDistance);
        }

        addBehaviour(new jade.core.behaviours.CyclicBehaviour() {
            @Override
            public void action() {
                ACLMessage msg = receive();
                if (msg != null) {
                    switch (msg.getPerformative()) {
                        case ACLMessage.CFP:
                            if (disponible) {
                                ACLMessage propose = msg.createReply();
                                propose.setPerformative(ACLMessage.PROPOSE);
                                propose.setContent(String.valueOf(distance));
                                send(propose);
                            }
                            break;

                        case ACLMessage.ACCEPT_PROPOSAL:
                            System.out.println(getLocalName() + " accepte la course");
                            disponible = false;
                            moveTo(clientPosition, () -> {
                                try {
                                    Thread.sleep(1000); // simulate course time
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }
                                totalCourses++;
                                moveTo(basePosition, () -> {
                                    disponible = true;
                                    if (mapUI != null) {
                                        mapUI.updateTaxi(getLocalName(), position, disponible);
                                        mapUI.updateStats(getLocalName(), totalCourses, totalDistance);
                                    }
                                    System.out.println(getLocalName() + " est revenu à sa base.");
                                });
                            });
                            break;

                        case ACLMessage.REJECT_PROPOSAL:
                            System.out.println(getLocalName() + " rejeté pour cette course");
                            break;
                    }
                } else block();
            }
        });
    }

    private void moveTo(Point target, Runnable onArrival) {
        new Thread(() -> {
            int distanceThisTrip = 0;
            while (!position.equals(target)) {
                if (position.x < target.x) position.x++;
                else if (position.x > target.x) position.x--;
                if (position.y < target.y) position.y++;
                else if (position.y > target.y) position.y--;

                distanceThisTrip++;

                if (mapUI != null)
                    mapUI.updateTaxi(getLocalName(), new Point(position), disponible);

                try {
                    Thread.sleep(10);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            totalDistance += distanceThisTrip;
            if (mapUI != null) {
                mapUI.updateStats(getLocalName(), totalCourses, totalDistance);
            }
            onArrival.run();
        }).start();
    }
}
