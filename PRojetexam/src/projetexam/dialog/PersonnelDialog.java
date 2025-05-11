package projetexam.dialog;

import projetexam.constructor.Personnel;
import projetexam.manager.PersonnelManager;
import javax.swing.*;
import java.awt.*;

public class PersonnelDialog extends JDialog {
    private JTextField nomField;
    private JTextField posteField;
    private JTextField telephoneField;
    private JComboBox<String> statutCombo;
    private JButton saveButton;
    private JButton deleteButton;
    private JButton cancelButton;
    
    private Personnel personnel;
    private boolean confirmed = false;
    private boolean isModification = false;
    
    public PersonnelDialog(Frame parent, boolean modal) {
        super(parent, modal);
        initComponents();
    }
    
    private void initComponents() {
        setTitle("Personnel");
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
        mainPanel.add(new JLabel("Poste:"), gbc);
        gbc.gridx = 1;
        posteField = new JTextField(20);
        mainPanel.add(posteField, gbc);
        
        gbc.gridx = 0; gbc.gridy = 2;
        mainPanel.add(new JLabel("Téléphone:"), gbc);
        gbc.gridx = 1;
        telephoneField = new JTextField(20);
        mainPanel.add(telephoneField, gbc);
        
        gbc.gridx = 0; gbc.gridy = 3;
        mainPanel.add(new JLabel("Statut:"), gbc);
        gbc.gridx = 1;
        statutCombo = new JComboBox<>(new String[]{"actif", "inactif"});
        mainPanel.add(statutCombo, gbc);
        
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
        saveButton.addActionListener(e -> savePersonnel());
        deleteButton.addActionListener(e -> deletePersonnel());
        cancelButton.addActionListener(e -> dispose());
        
        // Configuration initiale
        deleteButton.setVisible(false);
    }
    
    public void setPersonnel(Personnel personnel) {
        this.personnel = personnel;
        if (personnel != null) {
            nomField.setText(personnel.getNom());
            posteField.setText(personnel.getPoste());
            telephoneField.setText(personnel.getTelephone());
            statutCombo.setSelectedItem(personnel.getStatut());
        }
    }
    
    public void setModificationMode(boolean isModification) {
        this.isModification = isModification;
        deleteButton.setVisible(isModification);
        setTitle(isModification ? "Modifier le personnel" : "Nouveau personnel");
    }
    
    private void savePersonnel() {
        try {
            String nom = nomField.getText();
            String poste = posteField.getText();
            String telephone = telephoneField.getText();
            String statut = (String) statutCombo.getSelectedItem();
            
            if (nom.isEmpty() || poste.isEmpty() || telephone.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Tous les champs sont obligatoires", "Erreur", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            if (isModification && personnel != null) {
                personnel.setNom(nom);
                personnel.setPoste(poste);
                personnel.setTelephone(telephone);
                personnel.setStatut(statut);
                PersonnelManager.updatePersonnel(personnel);
            } else {
                Personnel newPersonnel = new Personnel(0, nom, poste, telephone, statut);
                PersonnelManager.AddPersonnel(newPersonnel);
            }
            
            confirmed = true;
            dispose();
            
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Erreur lors de l'enregistrement: " + ex.getMessage(), 
                "Erreur", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void deletePersonnel() {
        if (personnel != null) {
            int confirm = JOptionPane.showConfirmDialog(this, 
                "Êtes-vous sûr de vouloir supprimer ce personnel ?", 
                "Confirmation", 
                JOptionPane.YES_NO_OPTION);
                
            if (confirm == JOptionPane.YES_OPTION) {
                try {
                    PersonnelManager.deletePersonnel(personnel.getId());
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