package cm.uy1.ict308.fret;

import cm.uy1.ict308.fret.model.ConteneurRefrigere;
import cm.uy1.ict308.fret.model.ConteneurStandard;
import cm.uy1.ict308.fret.model.Marchandise;
import cm.uy1.ict308.fret.service.FretRepository;
import cm.uy1.ict308.fret.service.GestionStock;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

public final class FretPortuaireTest {
    private FretPortuaireTest() {
    }

    public static void main(String[] args) throws Exception {
        taxeStandard();
        taxeRefrigereeAvecSupplement();
        filtrageAlertes();
        persistance();
        System.out.println("Tous les tests ICT308 Projet 1 sont OK.");
    }

    private static void taxeStandard() {
        Marchandise standard = new ConteneurStandard("STD-T", 10, "test");
        assert standard.calculerTaxePortuaire() == 12000.0;
    }

    private static void taxeRefrigereeAvecSupplement() {
        Marchandise refrigere = new ConteneurRefrigere("REF-T", 10, "test", -5);
        assert refrigere.calculerTaxePortuaire() == 19500.0;
    }

    private static void filtrageAlertes() {
        GestionStock stock = new GestionStock();
        stock.ajouter(new ConteneurStandard("STD-A", 4, "standard"));
        stock.ajouter(new ConteneurRefrigere("REF-A", 4, "normal", -2));
        stock.ajouter(new ConteneurRefrigere("REF-B", 4, "alerte", 3));
        assert stock.listerTout().size() == 3;
        assert stock.listerAlertes().size() == 1;
    }

    private static void persistance() throws Exception {
        Path fichier = Files.createTempFile("fret-test", ".ser");
        FretRepository repository = new FretRepository(fichier);
        repository.sauvegarder(List.of(new ConteneurStandard("STD-P", 5, "persistant")));
        List<Marchandise> chargees = repository.charger();
        assert chargees.size() == 1;
        assert chargees.getFirst().getId().equals("STD-P");
        Files.deleteIfExists(fichier);
    }
}
