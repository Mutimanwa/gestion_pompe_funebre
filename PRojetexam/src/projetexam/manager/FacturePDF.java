/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package projetexam.manager;

import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Document;
import com.itextpdf.text.Element;
import com.itextpdf.text.FontFactory;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.util.List;
import javax.swing.JOptionPane;
import projetexam.constructor.FactureData;
import projetexam.constructor.Materiel;

/**
 *
 * @author calvindev
 */
public class FacturePDF {
    public static void genererFacturePDF(FactureData data, List<Materiel> materiels, double montant) {
        try {
            Document document = new Document();
            String dossier = "src/projetexam/factures";
            new File(dossier).mkdirs(); // Crée le dossier s’il n’existe pas

            String nomFichier = dossier + "/facture_" + data.nomDefunt.replace(" ", "_") + ".pdf";

            PdfWriter.getInstance(document, new FileOutputStream(nomFichier));
            document.open();

            // Titre
            Paragraph titre = new Paragraph("FACTURE - POMPE FUNEBRE",
                    FontFactory.getFont(FontFactory.HELVETICA_BOLD, 18, BaseColor.BLACK));
            titre.setAlignment(Element.ALIGN_CENTER);
            document.add(titre);
            document.add(new Paragraph(" "));

            // Infos générales
            document.add(new Paragraph("Famille       : " + data.nomFamille));
            document.add(new Paragraph("Défunt        : " + data.nomDefunt));
            document.add(new Paragraph("Cérémonie     : " + data.dateCeremonie + " à " + data.heureCeremonie));
            document.add(new Paragraph(" "));

            // Tableau des matériels
            PdfPTable table = new PdfPTable(3); // 3 colonnes
            table.setWidthPercentage(100);
            table.setWidths(new float[]{4, 4, 2});

            table.addCell("Nom du matériel");
            table.addCell("Type");
            table.addCell("Prix (FBu)");

            for (Materiel m : materiels) {
                table.addCell(m.getNom());
                table.addCell(m.getDescription());
                table.addCell(String.format("%,.2f", m.getPrix()));
            }

            document.add(table);

            // Total
            document.add(new Paragraph(" "));
            document.add(new Paragraph("Montant total : " + String.format("%,.2f FBu", montant),
                    FontFactory.getFont(FontFactory.HELVETICA_BOLD, 14)));

            document.close();

            JOptionPane.showMessageDialog(null, "Facture PDF générée : " + nomFichier);
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Erreur génération PDF : " + e.getMessage());
        }
    }
}
