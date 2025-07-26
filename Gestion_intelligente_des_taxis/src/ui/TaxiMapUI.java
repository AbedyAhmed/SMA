package ui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.HashMap;
import java.util.Map;

public class TaxiMapUI extends JFrame {
    private static TaxiMapUI instance;

    // Collections pour stocker les données
    private Map<String, Point> taxiPositions = new HashMap<>();
    private Map<String, Boolean> taxiAvailability = new HashMap<>();
    private Map<String, Integer> taxiCourses = new HashMap<>();
    private Map<String, Integer> taxiDistances = new HashMap<>();
    
    private Map<String, Point> clientPositions = new HashMap<>();
    private Map<String, Color> clientColors = new HashMap<>();

    // Composants UI
    private JPanel canvas;
    private JLabel statusLabel;

    public static TaxiMapUI getInstance() {
        if (instance == null) {
            instance = new TaxiMapUI();
        }
        return instance;
    }

    public TaxiMapUI() {
        initializeUI();
    }

    private void initializeUI() {
        setTitle("Visualisation Taxis & Clients");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // Panel de dessin
        canvas = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                drawGrid(g);
                drawClients(g);
                drawTaxis(g);
            }

            private void drawGrid(Graphics g) {
                g.setColor(Color.LIGHT_GRAY);
                for (int x = 0; x < getWidth(); x += 20) {
                    g.drawLine(x, 0, x, getHeight());
                }
                for (int y = 0; y < getHeight(); y += 20) {
                    g.drawLine(0, y, getWidth(), y);
                }
            }
        };
        canvas.setBackground(Color.WHITE);

        // Panel de contrôle
        JPanel controlPanel = new JPanel(new FlowLayout());
        statusLabel = new JLabel("Statut: Prêt | Taxis: 0 | Clients: 0");
        JButton statsButton = new JButton("Statistiques");
        statsButton.addActionListener(this::showStats);

        controlPanel.add(statusLabel);
        controlPanel.add(statsButton);

        add(canvas, BorderLayout.CENTER);
        add(controlPanel, BorderLayout.SOUTH);

        setVisible(true);
    }

    private void drawClients(Graphics g) {
        g.setFont(new Font("Arial", Font.PLAIN, 12));
        for (Map.Entry<String, Point> entry : clientPositions.entrySet()) {
            String name = entry.getKey();
            Point p = entry.getValue();
            Color color = clientColors.getOrDefault(name, Color.BLUE);
            
            // Dessin du client
            g.setColor(color);
            g.fillOval(p.x - 10, p.y - 10, 20, 20);
            g.setColor(Color.BLACK);
            g.drawOval(p.x - 10, p.y - 10, 20, 20);
            g.drawString("C:" + name, p.x - 15, p.y - 15);
        }
    }

    private void drawTaxis(Graphics g) {
        g.setFont(new Font("Arial", Font.BOLD, 12));
        for (Map.Entry<String, Point> entry : taxiPositions.entrySet()) {
            String name = entry.getKey();
            Point p = entry.getValue();
            boolean disponible = taxiAvailability.getOrDefault(name, false);
            
            // Dessin du taxi
            g.setColor(disponible ? new Color(0, 180, 0) : new Color(180, 0, 0));
            g.fillRect(p.x - 10, p.y - 10, 20, 20);
            g.setColor(Color.BLACK);
            g.drawRect(p.x - 10, p.y - 10, 20, 20);
            g.drawString("T:" + name, p.x - 15, p.y - 15);
            
            // Indicateur de disponibilité
            g.setColor(Color.WHITE);
            g.fillOval(p.x - 5, p.y - 5, 10, 10);
            g.setColor(disponible ? Color.GREEN : Color.RED);
            g.fillOval(p.x - 4, p.y - 4, 8, 8);
        }
    }

    // Méthodes publiques pour mettre à jour l'état
    public void addClient(String name, Point position, Color color) {
        clientPositions.put(name, position);
        clientColors.put(name, color);
        updateStatus();
        repaint();
    }

    public void updateTaxi(String name, Point position, boolean disponible) {
        taxiPositions.put(name, position);
        taxiAvailability.put(name, disponible);
        updateStatus();
        repaint();
    }

    public void updateStats(String name, int courses, int distance) {
        taxiCourses.put(name, courses);
        taxiDistances.put(name, distance);
    }

    public void setClientPosition(String name, Point p) {
        clientPositions.put(name, p);
        if (!clientColors.containsKey(name)) {
            clientColors.put(name, new Color((int)(Math.random() * 0x1000000)));
        }
        updateStatus();
        repaint();
    }

    private void updateStatus() {
        statusLabel.setText(String.format("Statut: Actif | Taxis: %d (%d dispo) | Clients: %d", 
            taxiPositions.size(), 
            taxiAvailability.values().stream().filter(b -> b).count(),
            clientPositions.size()));
    }

    private void showStats(ActionEvent e) {
        StringBuilder sb = new StringBuilder("<html><h2>Statistiques</h2>");
        
        sb.append("<h3>Taxis:</h3>");
        taxiCourses.forEach((name, courses) -> {
            int distance = taxiDistances.getOrDefault(name, 0);
            sb.append(String.format("<br/><b>%s</b>: %d courses, %d km, %s", 
                name, courses, distance, 
                taxiAvailability.getOrDefault(name, false) ? "disponible" : "occupé"));
        });
        
        sb.append("<h3>Clients:</h3>");
        sb.append(String.format("Total: %d clients</html>", clientPositions.size()));
        
        JOptionPane.showMessageDialog(this, sb.toString(), "Statistiques", JOptionPane.INFORMATION_MESSAGE);
    }
}