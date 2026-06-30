package cm.uy1.ict308.fret.service;

import cm.uy1.ict308.fret.model.Marchandise;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class FretRepository {
    private final Path fichier;

    public FretRepository(Path fichier) {
        this.fichier = fichier;
    }

    public void sauvegarder(List<Marchandise> marchandises) throws IOException {
        Path parent = fichier.getParent();
        if (parent != null) {
            Files.createDirectories(parent);
        }
        try (ObjectOutputStream sortie = new ObjectOutputStream(new FileOutputStream(fichier.toFile()))) {
            sortie.writeObject(new ArrayList<>(marchandises));
        }
    }

    @SuppressWarnings("unchecked")
    public List<Marchandise> charger() {
        try (ObjectInputStream entree = new ObjectInputStream(new FileInputStream(fichier.toFile()))) {
            Object data = entree.readObject();
            if (data instanceof ArrayList<?>) {
                return (ArrayList<Marchandise>) data;
            }
            return List.of();
        } catch (FileNotFoundException erreur) {
            return List.of();
        } catch (IOException | ClassNotFoundException erreur) {
            System.err.println("Chargement impossible : " + erreur.getMessage());
            return List.of();
        }
    }
}
