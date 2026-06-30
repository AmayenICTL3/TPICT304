package cm.uy1.ict308.fret.service;

import cm.uy1.ict308.fret.model.ConteneurRefrigere;
import cm.uy1.ict308.fret.model.Marchandise;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class GestionStock {
    private final ArrayList<Marchandise> marchandises = new ArrayList<>();

    public synchronized void ajouter(Marchandise marchandise) {
        if (chercherParId(marchandise.getId()).isPresent()) {
            throw new IllegalArgumentException("Un conteneur existe deja avec cet identifiant.");
        }
        marchandises.add(marchandise);
    }

    public synchronized Optional<Marchandise> chercherParId(String id) {
        return marchandises.stream()
            .filter(marchandise -> marchandise.getId().equalsIgnoreCase(id))
            .findFirst();
    }

    public synchronized List<Marchandise> listerTout() {
        return new ArrayList<>(marchandises);
    }

    public synchronized List<Marchandise> listerAlertes() {
        return marchandises.stream()
            .filter(ConteneurRefrigere.class::isInstance)
            .map(ConteneurRefrigere.class::cast)
            .filter(ConteneurRefrigere::estEnAlerte)
            .map(Marchandise.class::cast)
            .toList();
    }

    public synchronized void remplacerTout(List<Marchandise> nouvellesMarchandises) {
        marchandises.clear();
        marchandises.addAll(nouvellesMarchandises);
    }

    public synchronized ArrayList<Marchandise> copieSerializable() {
        return new ArrayList<>(marchandises);
    }

    public synchronized int taille() {
        return marchandises.size();
    }

    public synchronized void modifierTemperaturesRefrigerees(TemperatureUpdater updater) {
        for (Marchandise marchandise : marchandises) {
            if (marchandise instanceof ConteneurRefrigere refrigere) {
                refrigere.setTemperature(updater.nouvelleTemperature(refrigere.getTemperature()));
            }
        }
    }

    @FunctionalInterface
    public interface TemperatureUpdater {
        double nouvelleTemperature(double ancienneTemperature);
    }
}
