package projetexam.dialog;

import projetexam.constructor.Materiel;
import projetexam.manager.MaterielManager;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class MaterielDialog extends JDialog {
    private JTextField nomField;
    private JTextField descriptionField;
    private JTextField prixField;
    private JTextField stockField;
    private JButton saveButton;
    private JButton deleteButton;
    private JButton cancelButton;
    
    private Materiel materiel;
    private boolean confirmed = false;
    private boolean isModification = false;
    
    public MaterielDialog(Frame parent, boolean modal) {
        super(parent, modal);
        initComponents();
    }
    
    private void initComponents() {
        setTitle("Matériel");
        setSize(400, 300);
        setLocationRelativeTo(getParent());
        setLayout(new BorderLayout());
        
        // Panel principal
        JPanel mainPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        
        // Champs
        gbc.gridx = 0; gbc.gridy = 0;
        mainPanel.add(new JLabel("Nom:"), gbc);
        gbc.gridx = 1;
        nomField = new JTextField(20);
        mainPanel.add(nomField, gbc);
        
        gbc.gridx = 0; gbc.gridy = 1;
        mainPanel.add(new JLabel("Description:"), gbc);
        gbc.gridx = 1;
        descriptionField = new JTextField(20);
        mainPanel.add(descriptionField, gbc);
        
        gbc.gridx = 0; gbc.gridy = 2;
        mainPanel.add(new JLabel("Prix:"), gbc);
        gbc.gridx = 1;
        prixField = new JTextField(20);
        mainPanel.add(prixField, gbc);
        
        gbc.gridx = 0; gbc.gridy = 3;
        mainPanel.add(new JLabel("Stock:"), gbc);
        gbc.gridx = 1;
        stockField = new JTextField(20);
        mainPanel.add(stockField, gbc);
        
        // Panel des boutons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        saveButton = new JButton("Enregistrer");
        deleteButton = new JButton("Supprimer");
        cancelButton = new JButton("Annuler");
        
        buttonPanel.add(saveButton);
        buttonPanel.add(deleteButton);
        buttonPanel.add(cancelButton);
        
        // Ajout des panels
        add(mainPanel, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);
        
        // Écouteurs d'événements
        saveButton.addActionListener(e -> saveMateriel());
        deleteButton.addActionListener(e -> deleteMateriel());
        cancelButton.addActionListener(e -> dispose());
        
        // Configuration initiale
        deleteButton.setVisible(false);
    }
    
    public void setMateriel(Materiel materiel) {
        this.materiel = materiel;
        if (materiel != null) {
            nomField.setText(materiel.getNom());
            descriptionField.setText(materiel.getDescription());
            prixField.setText(String.valueOf(materiel.getPrix()));
            stockField.setText(String.valueOf(materiel.getStock()));
        }
    }
    
    public void setModificationMode(boolean isModification) {
        this.isModification = isModification;
        deleteButton.setVisible(isModification);
        setTitle(isModification ? "Modifier le matériel" : "Nouveau matériel");
    }
    
    private void saveMateriel() {
        try {
            String nom = nomField.getText();
            String description = descriptionField.getText();
            double prix = Double.parseDouble(prixField.getText());
            int stock = Integer.parseInt(stockField.getText());
            
            if (nom.isEmpty() || description.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Le nom et la description ne peuvent pas être vides", "Erreur", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            if (isModification && materiel != null) {
                materiel.setNom(nom);
                materiel.setDescription(description);
                materiel.setPrix(prix);
                materiel.setStock(stock);
                MaterielManager.updateMateriel(materiel);
            } else {
                Materiel newMateriel = new Materiel(0, nom, description, prix, stock);
                MaterielManager.addMateriel(newMateriel);
            }
            
            confirmed = true;
            dispose();
            
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Veuillez entrer des valeurs numériques valides pour le prix et le stock", 
                "Erreur", JOptionPane.ERROR_MESSAGE);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Erreur lors de l'enregistrement: " + ex.getMessage(), 
                "Erreur", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void deleteMateriel() {
        if (materiel != null) {
            int confirm = JOptionPane.showConfirmDialog(this, 
                "Êtes-vous sûr de vouloir supprimer ce matériel ?", 
                "Confirmation", 
                JOptionPane.YES_NO_OPTION);
                
            if (confirm == JOptionPane.YES_OPTION) {
                try {
                    MaterielManager.deleteMateriel(materiel.getId());
                    confirmed = true;
                    dispose();
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(this, 
                        "Erreur lors de la suppression: " + ex.getMessage(), 
                        "Erreur", 
                        JOptionPane.ERROR_MESSAGE);
                }
            }
        }
    }
    
    public boolean isConfirmed() {
        return confirmed;
    }
} 