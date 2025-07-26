import jade.core.Profile;
import jade.core.ProfileImpl;
import jade.wrapper.AgentContainer;
import jade.wrapper.AgentController;
import jade.core.Runtime;
import ui.TaxiMapUI;

import java.awt.Point;
import java.util.Random;

@SuppressWarnings("unused")
public class MainContainer {
    public static void main(String[] args) {
        Runtime rt = Runtime.instance();
        Profile p = new ProfileImpl();
        AgentContainer container = rt.createMainContainer(p);

        // Créer UNE SEULE instance de l'interface
        TaxiMapUI map = TaxiMapUI.getInstance();  // Utilisation du singleton
        agents.TaxiAgent.mapUI = map;

        try {
            // Création des clients
            for (int i = 1; i <= 2; i++) {
                String clientName = "client" + i;
                container.createNewAgent(clientName, "agents.ClientAgent", null).start();
            }

            // Création du dispatcher
            container.createNewAgent("dispatcher", "agents.DispatcherAgent", null).start();

            // Création des taxis avec positions aléatoires
            Random rand = new Random();
            for (int i = 1; i <= 3; i++) {
                String taxiName = "taxi" + i;
                Point pos = new Point(rand.nextInt(700), rand.nextInt(500)); // Adapté à la taille de la fenêtre
                boolean disponible = rand.nextBoolean();
                map.updateTaxi(taxiName, pos, disponible);
                container.createNewAgent(taxiName, "agents.TaxiAgent", null).start();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}