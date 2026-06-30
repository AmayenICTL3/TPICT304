package cm.uy1.ict308.fret.model;

import java.io.Serial;

public class ConteneurStandard extends Marchandise {
    @Serial
    private static final long serialVersionUID = 1L;

    public static final double TARIF_PAR_TONNE = 1200.0;

    public ConteneurStandard(String id, double poids, String description) {
        super(id, poids, description);
    }

    @Override
    public double calculerTaxePortuaire() {
        return getPoids() * TARIF_PAR_TONNE;
    }

    @Override
    public String getType() {
        return "Standard";
    }
}
