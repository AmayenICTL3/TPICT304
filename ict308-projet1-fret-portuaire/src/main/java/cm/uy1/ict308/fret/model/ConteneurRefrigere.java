package cm.uy1.ict308.fret.model;

import java.io.Serial;

public class ConteneurRefrigere extends Marchandise {
    @Serial
    private static final long serialVersionUID = 1L;

    public static final double TARIF_PAR_TONNE = 1200.0;
    public static final double SUPPLEMENT_ENERGETIQUE = 7500.0;
    public static final double SEUIL_ALERTE_CELSIUS = 0.0;

    private double temperature;

    public ConteneurRefrigere(String id, double poids, String description, double temperature) {
        super(id, poids, description);
        this.temperature = temperature;
    }

    public double getTemperature() {
        return temperature;
    }

    public void setTemperature(double temperature) {
        this.temperature = temperature;
    }

    public boolean estEnAlerte() {
        return temperature > SEUIL_ALERTE_CELSIUS;
    }

    @Override
    public double calculerTaxePortuaire() {
        return (getPoids() * TARIF_PAR_TONNE) + SUPPLEMENT_ENERGETIQUE;
    }

    @Override
    public String getType() {
        return "Refrigere";
    }
}
