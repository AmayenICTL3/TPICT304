package cm.uy1.ict308.fret.ui;

import cm.uy1.ict308.fret.model.ConteneurRefrigere;
import cm.uy1.ict308.fret.model.ConteneurStandard;
import cm.uy1.ict308.fret.model.Marchandise;
import cm.uy1.ict308.fret.service.FretRepository;
import cm.uy1.ict308.fret.service.GestionStock;
import cm.uy1.ict308.fret.service.TemperatureSimulation;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.WindowConstants;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.util.List;

public class FenetrePrincipale extends JFrame {
    private final GestionStock stock;
    private final FretRepository repository;
    private final MarchandiseTableModel tableModel = new MarchandiseTableModel();
    private final TemperatureSimulation simulation;

    private final JTextField idField = new JTextField();
    private final JTextField poidsField = new JTextField();
    private final JTextField descriptionField = new JTextField();
    private final JTextField temperatureField = new JTextField("-5");
    private final JComboBox<String> typeCombo = new JComboBox<>(new String[]{"Standard", "Refrigere"});
    private boolean modeAlertes = false;

    public FenetrePrincipale(GestionStock stock, FretRepository repository) {
        super("Gestion du Fret Portuaire - Port Autonome de Douala");
        this.stock = stock;
        this.repository = repository;
        this.simulation = new TemperatureSimulation(stock, this::rafraichirTableau);

        configurerFenetre();
        ajouterDonneesDemoSiNecessaire();
        rafraichirTableau();

        Thread thread = new Thread(simulation, "simulation-temperature");
        thread.setDaemon(true);
        thread.start();
    }

    private void configurerFenetre() {
        setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
        setSize(1080, 640);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(10, 10));

        JTable table = new JTable(tableModel);
        table.setRowHeight(28);
        add(new JScrollPane(table), BorderLayout.CENTER);
        add(creerFormulaire(), BorderLayout.NORTH);
        add(creerActions(), BorderLayout.SOUTH);

        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent event) {
                fermerProprement();
            }
        });
    }

    private JPanel creerFormulaire() {
        JPanel panneau = new JPanel(new GridLayout(2, 5, 8, 6));
        panneau.setBorder(BorderFactory.createTitledBorder("Ajouter un conteneur"));

        panneau.add(new JLabel("ID"));
        panneau.add(new JLabel("Type"));
        panneau.add(new JLabel("Poids (tonnes)"));
        panneau.add(new JLabel("Description"));
        panneau.add(new JLabel("Temperature"));

        panneau.add(idField);
        panneau.add(typeCombo);
        panneau.add(poidsField);
        panneau.add(descriptionField);
        panneau.add(temperatureField);

        return panneau;
    }

    private JPanel creerActions() {
        JButton ajouter = new JButton("Ajouter");
        JButton afficherTout = new JButton("Afficher tout");
        JButton afficherAlertes = new JButton("Afficher les alertes");
        JButton sauvegarder = new JButton("Sauvegarder");

        ajouter.addActionListener(event -> ajouterMarchandise());
        afficherTout.addActionListener(event -> {
            modeAlertes = false;
            rafraichirTableau();
        });
        afficherAlertes.addActionListener(event -> {
            modeAlertes = true;
            rafraichirTableau();
        });
        sauvegarder.addActionListener(event -> sauvegarder());

        JPanel panneau = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        panneau.add(ajouter);
        panneau.add(afficherTout);
        panneau.add(afficherAlertes);
        panneau.add(sauvegarder);
        return panneau;
    }

    private void ajouterMarchandise() {
        try {
            String id = idField.getText();
            double poids = Double.parseDouble(poidsField.getText().trim());
            String description = descriptionField.getText();
            Marchandise marchandise;

            if ("Refrigere".equals(typeCombo.getSelectedItem())) {
                double temperature = Double.parseDouble(temperatureField.getText().trim());
                marchandise = new ConteneurRefrigere(id, poids, description, temperature);
            } else {
                marchandise = new ConteneurStandard(id, poids, description);
            }

            stock.ajouter(marchandise);
            viderFormulaire();
            modeAlertes = false;
            rafraichirTableau();
        } catch (NumberFormatException erreur) {
            JOptionPane.showMessageDialog(this, "Poids ou temperature invalide.", "Saisie invalide", JOptionPane.WARNING_MESSAGE);
        } catch (IllegalArgumentException erreur) {
            JOptionPane.showMessageDialog(this, erreur.getMessage(), "Saisie invalide", JOptionPane.WARNING_MESSAGE);
        }
    }

    private void viderFormulaire() {
        idField.setText("");
        poidsField.setText("");
        descriptionField.setText("");
        temperatureField.setText("-5");
        idField.requestFocusInWindow();
    }

    public void rafraichirTableau() {
        List<Marchandise> lignes = modeAlertes ? stock.listerAlertes() : stock.listerTout();
        tableModel.remplacerLignes(lignes);
    }

    private void sauvegarder() {
        try {
            repository.sauvegarder(stock.copieSerializable());
            JOptionPane.showMessageDialog(this, "Donnees sauvegardees dans data/fret.ser.");
        } catch (IOException erreur) {
            JOptionPane.showMessageDialog(this, "Sauvegarde impossible : " + erreur.getMessage(), "Erreur", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void fermerProprement() {
        simulation.arreter();
        try {
            repository.sauvegarder(stock.copieSerializable());
        } catch (IOException erreur) {
            JOptionPane.showMessageDialog(this, "Sauvegarde impossible : " + erreur.getMessage(), "Erreur", JOptionPane.ERROR_MESSAGE);
        }
        dispose();
        System.exit(0);
    }

    private void ajouterDonneesDemoSiNecessaire() {
        if (stock.taille() > 0) {
            return;
        }
        stock.ajouter(new ConteneurStandard("STD-001", 12.5, "Materiel de construction"));
        stock.ajouter(new ConteneurRefrigere("REF-001", 8.0, "Produits halieutiques", -4.0));
        stock.ajouter(new ConteneurRefrigere("REF-002", 9.2, "Produits pharmaceutiques", 2.5));
        SwingUtilities.invokeLater(this::rafraichirTableau);
    }
}
