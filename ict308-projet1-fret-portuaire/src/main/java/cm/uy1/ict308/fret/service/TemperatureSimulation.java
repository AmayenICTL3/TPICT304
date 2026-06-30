package cm.uy1.ict308.fret.service;

import javax.swing.SwingUtilities;
import java.util.Random;

public class TemperatureSimulation implements Runnable {
    private final GestionStock stock;
    private final Runnable apresMiseAJour;
    private final Random random = new Random();
    private volatile boolean actif = true;

    public TemperatureSimulation(GestionStock stock, Runnable apresMiseAJour) {
        this.stock = stock;
        this.apresMiseAJour = apresMiseAJour;
    }

    public void arreter() {
        actif = false;
    }

    @Override
    public void run() {
        while (actif) {
            try {
                Thread.sleep(3000);
            } catch (InterruptedException erreur) {
                Thread.currentThread().interrupt();
                return;
            }

            stock.modifierTemperaturesRefrigerees(temperature -> {
                double variation = -1.5 + (random.nextDouble() * 4.0);
                return Math.round((temperature + variation) * 10.0) / 10.0;
            });

            SwingUtilities.invokeLater(apresMiseAJour);
        }
    }
}
