package cm.uy1.ict308.fret.ui;

import cm.uy1.ict308.fret.model.ConteneurRefrigere;
import cm.uy1.ict308.fret.model.Marchandise;

import javax.swing.table.AbstractTableModel;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class MarchandiseTableModel extends AbstractTableModel {
    private final String[] colonnes = {"ID", "Type", "Poids", "Description", "Temperature", "Taxe", "Statut"};
    private final NumberFormat monnaie = NumberFormat.getCurrencyInstance(Locale.FRANCE);
    private List<Marchandise> lignes = new ArrayList<>();

    public void remplacerLignes(List<Marchandise> marchandises) {
        this.lignes = new ArrayList<>(marchandises);
        fireTableDataChanged();
    }

    public Marchandise getMarchandiseAt(int rowIndex) {
        return lignes.get(rowIndex);
    }

    @Override
    public int getRowCount() {
        return lignes.size();
    }

    @Override
    public int getColumnCount() {
        return colonnes.length;
    }

    @Override
    public String getColumnName(int column) {
        return colonnes[column];
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        Marchandise marchandise = lignes.get(rowIndex);
        return switch (columnIndex) {
            case 0 -> marchandise.getId();
            case 1 -> marchandise.getType();
            case 2 -> String.format(Locale.FRANCE, "%.2f t", marchandise.getPoids());
            case 3 -> marchandise.getDescription();
            case 4 -> temperature(marchandise);
            case 5 -> monnaie.format(marchandise.calculerTaxePortuaire());
            case 6 -> statut(marchandise);
            default -> "";
        };
    }

    private String temperature(Marchandise marchandise) {
        if (marchandise instanceof ConteneurRefrigere refrigere) {
            return String.format(Locale.FRANCE, "%.1f °C", refrigere.getTemperature());
        }
        return "-";
    }

    private String statut(Marchandise marchandise) {
        if (marchandise instanceof ConteneurRefrigere refrigere && refrigere.estEnAlerte()) {
            return "ALERTE";
        }
        return "OK";
    }
}
