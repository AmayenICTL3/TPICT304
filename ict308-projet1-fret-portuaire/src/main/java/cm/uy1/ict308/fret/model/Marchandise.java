package cm.uy1.ict308.fret.model;

import java.io.Serial;
import java.io.Serializable;
import java.util.Objects;

public abstract class Marchandise implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    private final String id;
    private double poids;
    private String description;

    protected Marchandise(String id, double poids, String description) {
        if (id == null || id.isBlank()) {
            throw new IllegalArgumentException("L'identifiant est obligatoire.");
        }
        if (poids <= 0) {
            throw new IllegalArgumentException("Le poids doit etre strictement positif.");
        }
        this.id = id.trim();
        this.poids = poids;
        this.description = description == null ? "" : description.trim();
    }

    public String getId() {
        return id;
    }

    public double getPoids() {
        return poids;
    }

    public void setPoids(double poids) {
        if (poids <= 0) {
            throw new IllegalArgumentException("Le poids doit etre strictement positif.");
        }
        this.poids = poids;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description == null ? "" : description.trim();
    }

    public abstract double calculerTaxePortuaire();

    public abstract String getType();

    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof Marchandise that)) {
            return false;
        }
        return id.equals(that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
