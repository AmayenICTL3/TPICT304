package cm.uy1.ict308.fret;

import cm.uy1.ict308.fret.service.FretRepository;
import cm.uy1.ict308.fret.service.GestionStock;
import cm.uy1.ict308.fret.ui.FenetrePrincipale;

import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import java.nio.file.Path;

public final class Main {
    private Main() {
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception ignored) {
                // The default Swing theme remains usable if the platform theme is unavailable.
            }

            FretRepository repository = new FretRepository(Path.of("data", "fret.ser"));
            GestionStock stock = new GestionStock();
            stock.remplacerTout(repository.charger());

            FenetrePrincipale fenetre = new FenetrePrincipale(stock, repository);
            fenetre.setVisible(true);
        });
    }
}
