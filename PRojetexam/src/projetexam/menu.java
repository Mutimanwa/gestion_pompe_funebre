/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package projetexam;

import java.awt.Component;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import javax.accessibility.AccessibleContext;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import projetexam.constructor.Materiel;
import projetexam.constructor.Personnel;
import projetexam.dialog.MaterielDialog;
import projetexam.dialog.PersonnelDialog;
import projetexam.manager.MaterielManager;
import projetexam.manager.PersonnelManager;
import projetexam.connection.DBconnection;
import projetexam.constructor.FactureData;
import projetexam.manager.FactureDAO;
import projetexam.manager.FacturePDF;


/**
 *
 * @author calvindev
 */
public class menu extends javax.swing.JFrame {
    private int idFamilleActuelle;
    private int idDefuntActuel ;
    private Connection conn;
    private PreparedStatement pst;
    private ResultSet rs;
    private List<Materiel> listeMateriels = new ArrayList<>();
    private List<Personnel> listePersonnel = new ArrayList<>();
    //private AjoutMateriel ajoutMaterielDialog;
    //private Modifier modifierDialog;
    //private Supprimer supprimerDialog;

    /**
     * Creates new form menu
     */
    public menu() {
        initComponents();
        MainPanel.setUI(new javax.swing.plaf.basic.BasicTabbedPaneUI() {
            @Override
            protected int calculateTabAreaHeight(int tabPlacement, int horizRunCount, int maxTabHeight) {
                return 0; // Cache l'espace des onglets
            }
        });
        chargerMateriels();
        chargerPersonnels();
        chargerMaterielFacture();
        updateDashboardStats(); 
    }

    private void chargerMateriels() {
        try {
            // 1. Récupération des données
            listeMateriels = MaterielManager.getAllMateriels(); 

            // 2. Préparation des données pour le modèle
            String[] colonnes = {"Utiliser", "Nom", "Description", "Prix" ,"stock"};
            Object[][] data = new Object[listeMateriels.size()][5];

            for (int i = 0; i < listeMateriels.size(); i++) {
                Materiel m = listeMateriels.get(i);
                data[i][0] = false; // Checkbox initialement décochée
                data[i][1] = m.getNom() != null ? m.getNom() : ""; // Gestion du null
                data[i][2] = m.getDescription() != null ? m.getDescription() : "";
                data[i][3] = m.getPrix();
                data[i][4] = m.getStock();
            }

            // 3. Création du modèle avec rendu des checkboxes
            DefaultTableModel model = new DefaultTableModel(data, colonnes) {
                @Override
                public Class<?> getColumnClass(int columnIndex) {
                    return columnIndex == 0 ? Boolean.class : String.class;
                }

                @Override
                public boolean isCellEditable(int row, int column) {
                    return column == 0; // Seule la colonne 0 (checkbox) est éditable
                }
            };

            // 4. Application du modèle et ajustement des colonnes
            jTableMateriel.setModel(model);

            // Optionnel : Ajuster la largeur des colonnes
            jTableMateriel.getColumnModel().getColumn(0).setPreferredWidth(50); // Checkbox
            jTableMateriel.getColumnModel().getColumn(3).setCellRenderer(new DefaultTableCellRenderer() {
                @Override
                public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                    // Formatage du prix (ex: 1000.0 → "1 000,00 €")
                    if (value instanceof Double) {
                        value = String.format("%.2f €", (Double) value);
                    }
                    return super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                }
            });

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Erreur lors du chargement des matériels : " + e.getMessage(), "Erreur", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
        
        // Ajout de l'écouteur de double-clic pour le tableau de matériel
        jTableMateriel.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                if (evt.getClickCount() == 2) {
                    int row = jTableMateriel.getSelectedRow();
                    if (row != -1) {
                        Materiel materiel = listeMateriels.get(row);
                        showMaterielDialog(materiel, true);
                    }
                }
            }
        });
    }

    private void chargerPersonnels() {
        try {
            // 1. Récupération des données
            listePersonnel = PersonnelManager.getAllPersonnels();

            // 2. Préparation des données pour le modèle
            String[] colonnes = {"Selectionner", "Nom", "Poste", "Numero", "Statut"};
            Object[][] data = new Object[listePersonnel.size()][5];

            for (int i = 0; i < listePersonnel.size(); i++) {
                Personnel p = listePersonnel.get(i);
                data[i][0] = false; // Checkbox initialement décochée
                data[i][1] = p.getNom() != null ? p.getNom() : ""; // Gestion du null
                data[i][2] = p.getPoste()!= null ? p.getPoste(): "";
                data[i][3] = p.getTelephone() != null ? p.getTelephone() : "";
                data[i][4] = p.getStatut() != null ? p.getStatut() : "actif";
            }

            // 3. Création du modèle avec rendu des checkboxes
            DefaultTableModel model = new DefaultTableModel(data, colonnes) {
                @Override
                public Class<?> getColumnClass(int columnIndex) {
                    return columnIndex == 0 ? Boolean.class : String.class;
                }

                @Override
                public boolean isCellEditable(int row, int column) {
                    return column == 0; // Seule la colonne 0 (checkbox) est éditable
                }
            };

            // 4. Application du modèle et ajustement des colonnes
            PersonnelTable.setModel(model);

            // Optionnel : Ajuster la largeur des colonnes
            PersonnelTable.getColumnModel().getColumn(0).setPreferredWidth(50); // Checkbox
            PersonnelTable.getColumnModel().getColumn(3).setCellRenderer(new DefaultTableCellRenderer() {
                @Override
                public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                    // Formatage du prix (ex: 1000.0 → "1 000,00 €")
                    if (value instanceof Double) {
                        value = String.format("%.2f €", (Double) value);
                    }
                    return super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                }
            });

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Erreur lors du chargement des matériels : " + e.getMessage(), "Erreur", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
        
        // Ajout de l'écouteur de double-clic pour le tableau de personnel
        PersonnelTable.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                if (evt.getClickCount() == 2) {
                    int row = PersonnelTable.getSelectedRow();
                    if (row != -1) {
                        Personnel personnel = listePersonnel.get(row);
                        showPersonnelDialog(personnel, true);
                    }
                }
            }
        });
    }

    private void chargerMaterielFacture() {
        try {
            // 1. Récupération des données
            listeMateriels = MaterielManager.getAllMateriels(); 

            // 2. Préparation des données pour le modèle
            String[] colonnes = {"Utiliser", "Nom", "Description", "Prix"};
            Object[][] data = new Object[listeMateriels.size()][4];

            for (int i = 0; i < listeMateriels.size(); i++) {
                Materiel m = listeMateriels.get(i);
                data[i][0] = false; // Checkbox initialement décochée
                data[i][1] = m.getNom() != null ? m.getNom() : ""; // Gestion du null
                data[i][2] = m.getDescription() != null ? m.getDescription() : "";
                data[i][3] = m.getPrix(); // Supposé toujours valide
            }

            // 3. Création du modèle avec rendu des checkboxes
            DefaultTableModel model = new DefaultTableModel(data, colonnes) {
                @Override
                public Class<?> getColumnClass(int columnIndex) {
                    return columnIndex == 0 ? Boolean.class : String.class;
                }

                @Override
                public boolean isCellEditable(int row, int column) {
                    return column == 0; // Seule la colonne 0 (checkbox) est éditable
                }
            };

            // 4. Application du modèle et ajustement des colonnes
            jTableMateriel1.setModel(model);

            // Optionnel : Ajuster la largeur des colonnes
            jTableMateriel1.getColumnModel().getColumn(0).setPreferredWidth(50); // Checkbox
            jTableMateriel1.getColumnModel().getColumn(3).setCellRenderer(new DefaultTableCellRenderer() {
                @Override
                public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                    // Formatage du prix (ex: 1000.0 → "1 000,00 €")
                    if (value instanceof Double) {
                        value = String.format("%.2f €", (Double) value);
                    }
                    return super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                }
            });

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Erreur lors du chargement des matériels : " + e.getMessage(), "Erreur", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }

    private void updateDashboardStats() {
        try {
            // Mise à jour de la date et heure
            LocalDate today = LocalDate.now();
            LocalTime now = LocalTime.now();
            DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
            DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm");
            jLabel37.setText(today.format(dateFormatter));
            jLabel38.setText(now.format(timeFormatter));

            // Mise à jour des statistiques avec une seule connexion
            try (Connection conn = DBconnection.connect()) {
                // Personnel
                int totalPersonnel = PersonnelManager.getAllPersonnels().size();
                jLabel39.setText(String.valueOf(totalPersonnel));
                
                String sqlPersonnel = "SELECT COUNT(*) FROM employe WHERE statut = 'actif'";
                try (Statement st = conn.createStatement();
                     ResultSet rs = st.executeQuery(sqlPersonnel)) {
                    if (rs.next()) {
                        int personnelActif = rs.getInt(1);
                        jLabel25.setText(String.valueOf(personnelActif));
                        int personnelPercentage = totalPersonnel > 0 ? (personnelActif * 100) / totalPersonnel : 0;
                        jProgressBar4.setValue(personnelPercentage);
                        jProgressBar4.setForeground(new java.awt.Color(52, 152, 219));
                        jProgressBar4.setString(personnelPercentage + "%");
                    }
                }

                // Matériel
                int totalMateriel = MaterielManager.getAllMateriels().size();
                jLabel30.setText(String.valueOf(totalMateriel));

                int materielEnStock = MaterielManager.getAvailableMateriels().size();
                jLabel32.setText(String.valueOf(materielEnStock));

                int stockPercentage = totalMateriel > 0 ? (materielEnStock * 100) / totalMateriel : 0;
                jProgressBar2.setValue(stockPercentage);
                jProgressBar2.setString(stockPercentage + "%");

                // Cérémonies
                String sqlCeremonie = "SELECT COUNT(*) FROM ceremonie WHERE DATE(date_ceremonie) = ?";
                try (PreparedStatement pst = conn.prepareStatement(sqlCeremonie)) {
                    pst.setDate(1, java.sql.Date.valueOf(today));
                    try (ResultSet rs = pst.executeQuery()) {
                        if (rs.next()) {
                            int ceremoniesToday = rs.getInt(1);
                            jLabel34.setText(String.valueOf(ceremoniesToday));

                            int ceremoniesPercentage = Math.min(100, (ceremoniesToday * 100) / 10);
                            jProgressBar5.setValue(ceremoniesPercentage);
                            jProgressBar5.setForeground(new java.awt.Color(155, 89, 182));
                            jProgressBar5.setString(ceremoniesPercentage + "%");
                        }
                    }
                }
              String sqlClient = "SELECT COUNT(*) FROM famille ";
                try (PreparedStatement pst = conn.prepareStatement(sqlClient)) {
                    
                    try (ResultSet rs = pst.executeQuery()) {
                        if (rs.next()) {
                            int totalclient = rs.getInt(1);
                            jLabel27.setText(String.valueOf(totalclient));

                         
                        }
                    }
                }   
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, 
                "Erreur lors de la mise à jour du dashboard : " + e.getMessage(),
                "Erreur", 
                JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }

// 
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        AjoutMateriel = new javax.swing.JDialog();
        jPanel22 = new javax.swing.JPanel();
        jLabel70 = new javax.swing.JLabel();
        jLabel71 = new javax.swing.JLabel();
        jLabel72 = new javax.swing.JLabel();
        jLabel73 = new javax.swing.JLabel();
        tfNom1 = new javax.swing.JTextField();
        tfPrix1 = new javax.swing.JTextField();
        jButton15 = new javax.swing.JButton();
        tfType1 = new javax.swing.JComboBox<>();
        jLabel74 = new javax.swing.JLabel();
        jComboBox7 = new javax.swing.JComboBox<>();
        Modifier = new javax.swing.JDialog();
        jPanel23 = new javax.swing.JPanel();
        jLabel75 = new javax.swing.JLabel();
        jLabel76 = new javax.swing.JLabel();
        jLabel77 = new javax.swing.JLabel();
        jLabel78 = new javax.swing.JLabel();
        tfNom2 = new javax.swing.JTextField();
        tfPrix2 = new javax.swing.JTextField();
        jButton16 = new javax.swing.JButton();
        tfType2 = new javax.swing.JComboBox<>();
        jLabel79 = new javax.swing.JLabel();
        jComboBox8 = new javax.swing.JComboBox<>();
        Supprimer = new javax.swing.JDialog();
        AjouterPersonnel = new javax.swing.JDialog();
        jPanel19 = new javax.swing.JPanel();
        jLabel53 = new javax.swing.JLabel();
        jLabel65 = new javax.swing.JLabel();
        jLabel67 = new javax.swing.JLabel();
        jLabel68 = new javax.swing.JLabel();
        jTextField15 = new javax.swing.JTextField();
        jTextField20 = new javax.swing.JTextField();
        jButton14 = new javax.swing.JButton();
        jComboBox5 = new javax.swing.JComboBox<>();
        jLabel14 = new javax.swing.JLabel();
        jButton21 = new javax.swing.JButton();
        MainPanel = new javax.swing.JTabbedPane();
        PanelDashboard = new javax.swing.JPanel();
        jPanel11 = new javax.swing.JPanel();
        jLabel20 = new javax.swing.JLabel();
        jButton20 = new javax.swing.JButton();
        jPanel8 = new javax.swing.JPanel();
        jLabel25 = new javax.swing.JLabel();
        jLabel26 = new javax.swing.JLabel();
        jPanel12 = new javax.swing.JPanel();
        jLabel27 = new javax.swing.JLabel();
        jLabel28 = new javax.swing.JLabel();
        jButton2 = new javax.swing.JButton();
        jLabel21 = new javax.swing.JLabel();
        jSeparator3 = new javax.swing.JSeparator();
        AjouterPanelLouncher = new javax.swing.JButton();
        AjoutPersonnelPanel = new javax.swing.JButton();
        AjoutPersonnelPanel14 = new javax.swing.JButton();
        jPanel15 = new javax.swing.JPanel();
        jLabel30 = new javax.swing.JLabel();
        jLabel31 = new javax.swing.JLabel();
        jPanel16 = new javax.swing.JPanel();
        jLabel32 = new javax.swing.JLabel();
        jLabel33 = new javax.swing.JLabel();
        jProgressBar2 = new javax.swing.JProgressBar();
        jPanel24 = new javax.swing.JPanel();
        jLabel34 = new javax.swing.JLabel();
        jLabel35 = new javax.swing.JLabel();
        jLabel37 = new javax.swing.JLabel();
        jLabel38 = new javax.swing.JLabel();
        jPanel25 = new javax.swing.JPanel();
        jLabel39 = new javax.swing.JLabel();
        jLabel40 = new javax.swing.JLabel();
        PanelFamille = new javax.swing.JPanel();
        jPanel3 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jPanel4 = new javax.swing.JPanel();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        jTextField1 = new javax.swing.JTextField();
        jTextField2 = new javax.swing.JTextField();
        jTextField3 = new javax.swing.JTextField();
        BtnPrecendent = new javax.swing.JButton();
        BntSuivant = new javax.swing.JButton();
        jProgressBar5 = new javax.swing.JProgressBar();
        jSeparator4 = new javax.swing.JSeparator();
        jLabel47 = new javax.swing.JLabel();
        jButton8 = new javax.swing.JButton();
        AjouterPanelLouncher1 = new javax.swing.JButton();
        AjoutPersonnelPanel1 = new javax.swing.JButton();
        AjoutPersonnelPanel13 = new javax.swing.JButton();
        PanelDefunt = new javax.swing.JPanel();
        jPanel6 = new javax.swing.JPanel();
        jLabel6 = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        jLabel8 = new javax.swing.JLabel();
        jLabel9 = new javax.swing.JLabel();
        jTextField4 = new javax.swing.JTextField();
        jTextField5 = new javax.swing.JTextField();
        jTextField6 = new javax.swing.JTextField();
        jTextField7 = new javax.swing.JTextField();
        jLabel11 = new javax.swing.JLabel();
        jTextField8 = new javax.swing.JTextField();
        jLabel12 = new javax.swing.JLabel();
        jPanel7 = new javax.swing.JPanel();
        jLabel10 = new javax.swing.JLabel();
        BntSuivant2 = new javax.swing.JButton();
        BtnPrecendent2 = new javax.swing.JButton();
        jProgressBar4 = new javax.swing.JProgressBar();
        jSeparator5 = new javax.swing.JSeparator();
        jLabel48 = new javax.swing.JLabel();
        jButton9 = new javax.swing.JButton();
        AjouterPanelLouncher10 = new javax.swing.JButton();
        AjoutPersonnelPanel2 = new javax.swing.JButton();
        AjoutPersonnelPanel12 = new javax.swing.JButton();
        PanelCeremonie = new javax.swing.JPanel();
        jPanel9 = new javax.swing.JPanel();
        jLabel13 = new javax.swing.JLabel();
        jLabel15 = new javax.swing.JLabel();
        jLabel16 = new javax.swing.JLabel();
        Date = new javax.swing.JTextField();
        Heure = new javax.swing.JTextField();
        Lieu = new javax.swing.JTextField();
        jLabel17 = new javax.swing.JLabel();
        Type = new javax.swing.JTextField();
        jLabel18 = new javax.swing.JLabel();
        jPanel10 = new javax.swing.JPanel();
        jLabel19 = new javax.swing.JLabel();
        BtnPrecendent1 = new javax.swing.JButton();
        BntSuivant1 = new javax.swing.JButton();
        jProgressBar1 = new javax.swing.JProgressBar();
        jSeparator6 = new javax.swing.JSeparator();
        jLabel49 = new javax.swing.JLabel();
        jButton10 = new javax.swing.JButton();
        AjouterPanelLouncher11 = new javax.swing.JButton();
        AjoutPersonnelPanel3 = new javax.swing.JButton();
        AjoutPersonnelPanel6 = new javax.swing.JButton();
        jPanel1 = new javax.swing.JPanel();
        jPanel13 = new javax.swing.JPanel();
        jLabel29 = new javax.swing.JLabel();
        jPanel14 = new javax.swing.JPanel();
        jSeparator1 = new javax.swing.JSeparator();
        jSeparator2 = new javax.swing.JSeparator();
        jLabel36 = new javax.swing.JLabel();
        jButton7 = new javax.swing.JButton();
        Total = new javax.swing.JLabel();
        jButton22 = new javax.swing.JButton();
        jLabel51 = new javax.swing.JLabel();
        jButton12 = new javax.swing.JButton();
        AjouterPanelLouncher13 = new javax.swing.JButton();
        AjoutPersonnelPanel5 = new javax.swing.JButton();
        jSeparator8 = new javax.swing.JSeparator();
        AjoutPersonnelPanel11 = new javax.swing.JButton();
        jScrollPane3 = new javax.swing.JScrollPane();
        jTableMateriel1 = new javax.swing.JTable();
        jLabel22 = new javax.swing.JLabel();
        jPanel2 = new javax.swing.JPanel();
        jPanel17 = new javax.swing.JPanel();
        jLabel45 = new javax.swing.JLabel();
        jSeparator7 = new javax.swing.JSeparator();
        jLabel50 = new javax.swing.JLabel();
        jButton11 = new javax.swing.JButton();
        AjouterPanelLouncher12 = new javax.swing.JButton();
        AjoutPersonnelPanel4 = new javax.swing.JButton();
        AjoutPersonnelPanel10 = new javax.swing.JButton();
        jScrollPane2 = new javax.swing.JScrollPane();
        PersonnelTable = new javax.swing.JTable();
        jButton17 = new javax.swing.JButton();
        jButton18 = new javax.swing.JButton();
        jButton19 = new javax.swing.JButton();
        jPanel18 = new javax.swing.JPanel();
        jLabel52 = new javax.swing.JLabel();
        jButton13 = new javax.swing.JButton();
        AjouterPanelLouncher14 = new javax.swing.JButton();
        AjoutPersonnelPanel7 = new javax.swing.JButton();
        AjoutPersonnelPanel8 = new javax.swing.JButton();
        jSeparator9 = new javax.swing.JSeparator();
        Materiel = new javax.swing.JPanel();
        jLabel46 = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTableMateriel = new javax.swing.JTable();
        jToggleButton1 = new javax.swing.JToggleButton();
        jToggleButton2 = new javax.swing.JToggleButton();
        jToggleButton3 = new javax.swing.JToggleButton();
        jPanel20 = new javax.swing.JPanel();
        jPasswordField1 = new javax.swing.JPasswordField();
        jButton3 = new javax.swing.JButton();
        jLabel54 = new javax.swing.JLabel();
        jLabel55 = new javax.swing.JLabel();
        jButton4 = new javax.swing.JButton();
        jLabel56 = new javax.swing.JLabel();
        jTextField17 = new javax.swing.JTextField();
        jLabel57 = new javax.swing.JLabel();
        jLabel58 = new javax.swing.JLabel();
        jPanel21 = new javax.swing.JPanel();
        jLabel59 = new javax.swing.JLabel();
        jPasswordField2 = new javax.swing.JPasswordField();
        jPasswordField3 = new javax.swing.JPasswordField();
        jButton5 = new javax.swing.JButton();
        jLabel60 = new javax.swing.JLabel();
        jLabel61 = new javax.swing.JLabel();
        jLabel62 = new javax.swing.JLabel();
        jTextField18 = new javax.swing.JTextField();
        jButton6 = new javax.swing.JButton();
        jLabel63 = new javax.swing.JLabel();
        jTextField19 = new javax.swing.JTextField();
        jLabel64 = new javax.swing.JLabel();
        jLabel66 = new javax.swing.JLabel();

        AjoutMateriel.setBackground(new java.awt.Color(255, 255, 255));

        jPanel22.setBackground(new java.awt.Color(255, 255, 255));
        jPanel22.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(204, 204, 204), 1, true));
        jPanel22.setName(""); // NOI18N

        jLabel70.setFont(new java.awt.Font("Cantarell", 0, 24)); // NOI18N
        jLabel70.setText("JOUTER DES MATERIELS");

        jLabel71.setText("NON DU MATERIEL");

        jLabel72.setText("TYPE");

        jLabel73.setText("PRIX");

        tfNom1.setFont(new java.awt.Font("Cantarell", 0, 20)); // NOI18N

        tfPrix1.setFont(new java.awt.Font("Cantarell", 0, 20)); // NOI18N

        jButton15.setText("AJOUTER");
        jButton15.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton15ActionPerformed(evt);
            }
        });

        tfType1.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Cercueils", "Pierres tombales", "Fleurs", "Plaques", "Urnes" }));

        jLabel74.setText("DISPONIBILITE");

        jComboBox7.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Disponible", "Indisponible" }));

        javax.swing.GroupLayout jPanel22Layout = new javax.swing.GroupLayout(jPanel22);
        jPanel22.setLayout(jPanel22Layout);
        jPanel22Layout.setHorizontalGroup(
            jPanel22Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel22Layout.createSequentialGroup()
                .addGap(27, 27, 27)
                .addGroup(jPanel22Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jLabel71, javax.swing.GroupLayout.PREFERRED_SIZE, 187, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel72, javax.swing.GroupLayout.PREFERRED_SIZE, 187, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel73, javax.swing.GroupLayout.PREFERRED_SIZE, 187, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel74, javax.swing.GroupLayout.PREFERRED_SIZE, 187, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(tfNom1)
                    .addComponent(tfType1, 0, 342, Short.MAX_VALUE)
                    .addComponent(tfPrix1)
                    .addComponent(jComboBox7, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jButton15, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(jPanel22Layout.createSequentialGroup()
                        .addGap(19, 19, 19)
                        .addComponent(jLabel70, javax.swing.GroupLayout.PREFERRED_SIZE, 295, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(29, Short.MAX_VALUE))
        );
        jPanel22Layout.setVerticalGroup(
            jPanel22Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel22Layout.createSequentialGroup()
                .addGap(17, 17, 17)
                .addComponent(jLabel70, javax.swing.GroupLayout.PREFERRED_SIZE, 47, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jLabel71, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(tfNom1, javax.swing.GroupLayout.PREFERRED_SIZE, 46, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(21, 21, 21)
                .addComponent(jLabel72, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(tfType1, javax.swing.GroupLayout.PREFERRED_SIZE, 46, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jLabel73, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(tfPrix1, javax.swing.GroupLayout.PREFERRED_SIZE, 46, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jLabel74, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jComboBox7, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 27, Short.MAX_VALUE)
                .addComponent(jButton15, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(24, 24, 24))
        );

        javax.swing.GroupLayout AjoutMaterielLayout = new javax.swing.GroupLayout(AjoutMateriel.getContentPane());
        AjoutMateriel.getContentPane().setLayout(AjoutMaterielLayout);
        AjoutMaterielLayout.setHorizontalGroup(
            AjoutMaterielLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 400, Short.MAX_VALUE)
            .addGroup(AjoutMaterielLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addComponent(jPanel22, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        AjoutMaterielLayout.setVerticalGroup(
            AjoutMaterielLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 574, Short.MAX_VALUE)
            .addGroup(AjoutMaterielLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addComponent(jPanel22, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanel23.setBackground(new java.awt.Color(255, 255, 255));
        jPanel23.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(204, 204, 204), 1, true));
        jPanel23.setName(""); // NOI18N

        jLabel75.setFont(new java.awt.Font("Cantarell", 0, 24)); // NOI18N
        jLabel75.setText("MODIFER DES MATERIELS");

        jLabel76.setText("NON DU MATERIEL");

        jLabel77.setText("TYPE");

        jLabel78.setText("PRIX");

        tfNom2.setFont(new java.awt.Font("Cantarell", 0, 20)); // NOI18N

        tfPrix2.setFont(new java.awt.Font("Cantarell", 0, 20)); // NOI18N

        jButton16.setText("MODIFIER");
        jButton16.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton16ActionPerformed(evt);
            }
        });

        tfType2.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Cercueils", "Pierres tombales", "Fleurs", "Plaques", "Urnes" }));

        jLabel79.setText("DISPONIBILITE");

        jComboBox8.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Disponible", "Indisponible" }));

        javax.swing.GroupLayout jPanel23Layout = new javax.swing.GroupLayout(jPanel23);
        jPanel23.setLayout(jPanel23Layout);
        jPanel23Layout.setHorizontalGroup(
            jPanel23Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel23Layout.createSequentialGroup()
                .addGap(27, 27, 27)
                .addGroup(jPanel23Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jLabel76, javax.swing.GroupLayout.PREFERRED_SIZE, 187, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel77, javax.swing.GroupLayout.PREFERRED_SIZE, 187, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel78, javax.swing.GroupLayout.PREFERRED_SIZE, 187, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel79, javax.swing.GroupLayout.PREFERRED_SIZE, 187, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(tfNom2)
                    .addComponent(tfType2, 0, 342, Short.MAX_VALUE)
                    .addComponent(tfPrix2)
                    .addComponent(jComboBox8, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jButton16, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(jPanel23Layout.createSequentialGroup()
                        .addGap(19, 19, 19)
                        .addComponent(jLabel75, javax.swing.GroupLayout.PREFERRED_SIZE, 295, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(29, Short.MAX_VALUE))
        );
        jPanel23Layout.setVerticalGroup(
            jPanel23Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel23Layout.createSequentialGroup()
                .addGap(17, 17, 17)
                .addComponent(jLabel75, javax.swing.GroupLayout.PREFERRED_SIZE, 47, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jLabel76, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(tfNom2, javax.swing.GroupLayout.PREFERRED_SIZE, 46, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(21, 21, 21)
                .addComponent(jLabel77, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(tfType2, javax.swing.GroupLayout.PREFERRED_SIZE, 46, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jLabel78, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(tfPrix2, javax.swing.GroupLayout.PREFERRED_SIZE, 46, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jLabel79, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jComboBox8, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jButton16, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(24, 24, 24))
        );

        javax.swing.GroupLayout ModifierLayout = new javax.swing.GroupLayout(Modifier.getContentPane());
        Modifier.getContentPane().setLayout(ModifierLayout);
        ModifierLayout.setHorizontalGroup(
            ModifierLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 400, Short.MAX_VALUE)
            .addGroup(ModifierLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addComponent(jPanel23, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        ModifierLayout.setVerticalGroup(
            ModifierLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 553, Short.MAX_VALUE)
            .addGroup(ModifierLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addComponent(jPanel23, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout SupprimerLayout = new javax.swing.GroupLayout(Supprimer.getContentPane());
        Supprimer.getContentPane().setLayout(SupprimerLayout);
        SupprimerLayout.setHorizontalGroup(
            SupprimerLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 400, Short.MAX_VALUE)
        );
        SupprimerLayout.setVerticalGroup(
            SupprimerLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 300, Short.MAX_VALUE)
        );

        jPanel19.setBackground(new java.awt.Color(255, 255, 255));
        jPanel19.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(204, 204, 204), 1, true));

        jLabel53.setFont(new java.awt.Font("Cantarell", 0, 36)); // NOI18N
        jLabel53.setText("JOUTER UN PERSONNEL");

        jLabel65.setText("NON DU PERSONNEL");

        jLabel67.setText("POSTE");

        jLabel68.setText("NUMERO DE TELEPHONE");

        jTextField15.setFont(new java.awt.Font("Cantarell", 0, 20)); // NOI18N

        jTextField20.setFont(new java.awt.Font("Cantarell", 0, 20)); // NOI18N

        jButton14.setText("ENREGISTRER");
        jButton14.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton14ActionPerformed(evt);
            }
        });

        jComboBox5.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));

        javax.swing.GroupLayout jPanel19Layout = new javax.swing.GroupLayout(jPanel19);
        jPanel19.setLayout(jPanel19Layout);
        jPanel19Layout.setHorizontalGroup(
            jPanel19Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel19Layout.createSequentialGroup()
                .addGap(49, 49, 49)
                .addGroup(jPanel19Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel53, javax.swing.GroupLayout.PREFERRED_SIZE, 411, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanel19Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                        .addComponent(jLabel65, javax.swing.GroupLayout.PREFERRED_SIZE, 187, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel67, javax.swing.GroupLayout.PREFERRED_SIZE, 187, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel68, javax.swing.GroupLayout.PREFERRED_SIZE, 187, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jTextField20)
                        .addComponent(jComboBox5, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jTextField15)
                        .addComponent(jButton14, javax.swing.GroupLayout.DEFAULT_SIZE, 381, Short.MAX_VALUE)))
                .addContainerGap(53, Short.MAX_VALUE))
        );
        jPanel19Layout.setVerticalGroup(
            jPanel19Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel19Layout.createSequentialGroup()
                .addGap(15, 15, 15)
                .addComponent(jLabel53, javax.swing.GroupLayout.PREFERRED_SIZE, 47, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(53, 53, 53)
                .addComponent(jLabel65, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jTextField15, javax.swing.GroupLayout.PREFERRED_SIZE, 46, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jLabel67, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jComboBox5, javax.swing.GroupLayout.PREFERRED_SIZE, 46, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jLabel68, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jTextField20, javax.swing.GroupLayout.PREFERRED_SIZE, 46, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 80, Short.MAX_VALUE)
                .addComponent(jButton14, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(25, 25, 25))
        );

        javax.swing.GroupLayout AjouterPersonnelLayout = new javax.swing.GroupLayout(AjouterPersonnel.getContentPane());
        AjouterPersonnel.getContentPane().setLayout(AjouterPersonnelLayout);
        AjouterPersonnelLayout.setHorizontalGroup(
            AjouterPersonnelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 515, Short.MAX_VALUE)
            .addGroup(AjouterPersonnelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, AjouterPersonnelLayout.createSequentialGroup()
                    .addGap(0, 0, Short.MAX_VALUE)
                    .addComponent(jPanel19, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
        );
        AjouterPersonnelLayout.setVerticalGroup(
            AjouterPersonnelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 559, Short.MAX_VALUE)
            .addGroup(AjouterPersonnelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, AjouterPersonnelLayout.createSequentialGroup()
                    .addGap(0, 0, Short.MAX_VALUE)
                    .addComponent(jPanel19, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
        );

        jLabel14.setText("jLabel14");

        jButton21.setText("jButton21");

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("GOOD LIFE AFTER DEATH");
        setFont(new java.awt.Font("SansSerif", 0, 17)); // NOI18N
        setPreferredSize(new java.awt.Dimension(1300, 760));
        setResizable(false);

        MainPanel.setBackground(new java.awt.Color(255, 255, 255));
        MainPanel.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        MainPanel.setFont(new java.awt.Font("Cantarell", 0, 20)); // NOI18N
        MainPanel.setNextFocusableComponent(jLabel15);

        PanelDashboard.setBackground(new java.awt.Color(255, 255, 255));

        jLabel20.setFont(new java.awt.Font("SansSerif", 0, 20)); // NOI18N
        jLabel20.setText("Dashboard");

        jButton20.setText("ACTUALISER");
        jButton20.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton20ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel11Layout = new javax.swing.GroupLayout(jPanel11);
        jPanel11.setLayout(jPanel11Layout);
        jPanel11Layout.setHorizontalGroup(
            jPanel11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel11Layout.createSequentialGroup()
                .addGap(17, 17, 17)
                .addComponent(jLabel20)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 717, Short.MAX_VALUE)
                .addComponent(jButton20)
                .addGap(164, 164, 164))
        );
        jPanel11Layout.setVerticalGroup(
            jPanel11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel11Layout.createSequentialGroup()
                .addGap(21, 21, 21)
                .addGroup(jPanel11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel20)
                    .addComponent(jButton20))
                .addContainerGap(23, Short.MAX_VALUE))
        );

        jPanel8.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(204, 204, 204), 1, true));

        jLabel25.setFont(new java.awt.Font("SansSerif", 0, 36)); // NOI18N
        jLabel25.setText("200");

        jLabel26.setFont(new java.awt.Font("Cantarell", 0, 16)); // NOI18N
        jLabel26.setText("PERSONNEL ACTIF");

        javax.swing.GroupLayout jPanel8Layout = new javax.swing.GroupLayout(jPanel8);
        jPanel8.setLayout(jPanel8Layout);
        jPanel8Layout.setHorizontalGroup(
            jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel8Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel25, javax.swing.GroupLayout.PREFERRED_SIZE, 186, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel26))
                .addContainerGap(92, Short.MAX_VALUE))
        );
        jPanel8Layout.setVerticalGroup(
            jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel8Layout.createSequentialGroup()
                .addGap(27, 27, 27)
                .addComponent(jLabel26)
                .addGap(18, 18, 18)
                .addComponent(jLabel25, javax.swing.GroupLayout.PREFERRED_SIZE, 41, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanel12.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(204, 204, 204), 1, true));

        jLabel27.setFont(new java.awt.Font("SansSerif", 0, 36)); // NOI18N
        jLabel27.setText("200");

        jLabel28.setFont(new java.awt.Font("Cantarell", 0, 16)); // NOI18N
        jLabel28.setText("CLIENTS  /FAMILLE");

        javax.swing.GroupLayout jPanel12Layout = new javax.swing.GroupLayout(jPanel12);
        jPanel12.setLayout(jPanel12Layout);
        jPanel12Layout.setHorizontalGroup(
            jPanel12Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel12Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel12Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel27, javax.swing.GroupLayout.PREFERRED_SIZE, 186, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel28, javax.swing.GroupLayout.PREFERRED_SIZE, 173, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(92, Short.MAX_VALUE))
        );
        jPanel12Layout.setVerticalGroup(
            jPanel12Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel12Layout.createSequentialGroup()
                .addGap(27, 27, 27)
                .addComponent(jLabel28)
                .addGap(18, 18, 18)
                .addComponent(jLabel27, javax.swing.GroupLayout.PREFERRED_SIZE, 41, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(17, Short.MAX_VALUE))
        );

        jButton2.setText("DASHBOARD");
        jButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton2ActionPerformed(evt);
            }
        });

        jLabel21.setIcon(new javax.swing.ImageIcon(getClass().getResource("/projetexam/Capture du 2025-04-29 22-10-58.png"))); // NOI18N

        jSeparator3.setOrientation(javax.swing.SwingConstants.VERTICAL);

        AjouterPanelLouncher.setText("AJOUTER UN CLIENT");
        AjouterPanelLouncher.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                AjouterPanelLouncherActionPerformed(evt);
            }
        });

        AjoutPersonnelPanel.setText("GERER LES PERSONNELS");
        AjoutPersonnelPanel.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                AjoutPersonnelPanelActionPerformed(evt);
            }
        });

        AjoutPersonnelPanel14.setText("GERER LES MATERIELS");
        AjoutPersonnelPanel14.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                AjoutPersonnelPanel14ActionPerformed(evt);
            }
        });

        jPanel15.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(204, 204, 204), 1, true));

        jLabel30.setFont(new java.awt.Font("SansSerif", 0, 36)); // NOI18N
        jLabel30.setText("200");

        jLabel31.setFont(new java.awt.Font("Cantarell", 0, 16)); // NOI18N
        jLabel31.setText("TOTAL MATERIELES");

        javax.swing.GroupLayout jPanel15Layout = new javax.swing.GroupLayout(jPanel15);
        jPanel15.setLayout(jPanel15Layout);
        jPanel15Layout.setHorizontalGroup(
            jPanel15Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel15Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel15Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel30, javax.swing.GroupLayout.PREFERRED_SIZE, 186, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel31))
                .addContainerGap(92, Short.MAX_VALUE))
        );
        jPanel15Layout.setVerticalGroup(
            jPanel15Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel15Layout.createSequentialGroup()
                .addGap(27, 27, 27)
                .addComponent(jLabel31)
                .addGap(18, 18, 18)
                .addComponent(jLabel30, javax.swing.GroupLayout.PREFERRED_SIZE, 41, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(17, Short.MAX_VALUE))
        );

        jPanel16.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(204, 204, 204), 1, true));

        jLabel32.setFont(new java.awt.Font("SansSerif", 0, 36)); // NOI18N
        jLabel32.setText("200");

        jLabel33.setFont(new java.awt.Font("Cantarell", 0, 16)); // NOI18N
        jLabel33.setText("MATERIELES EN STOCK");

        jProgressBar2.setBackground(new java.awt.Color(255, 255, 255));
        jProgressBar2.setValue(10);

        javax.swing.GroupLayout jPanel16Layout = new javax.swing.GroupLayout(jPanel16);
        jPanel16.setLayout(jPanel16Layout);
        jPanel16Layout.setHorizontalGroup(
            jPanel16Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel16Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel16Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jProgressBar2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(jPanel16Layout.createSequentialGroup()
                        .addGroup(jPanel16Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel32, javax.swing.GroupLayout.PREFERRED_SIZE, 186, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel33))
                        .addGap(0, 86, Short.MAX_VALUE)))
                .addContainerGap())
        );
        jPanel16Layout.setVerticalGroup(
            jPanel16Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel16Layout.createSequentialGroup()
                .addGap(27, 27, 27)
                .addComponent(jLabel33)
                .addGap(18, 18, 18)
                .addComponent(jLabel32, javax.swing.GroupLayout.PREFERRED_SIZE, 41, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jProgressBar2, javax.swing.GroupLayout.PREFERRED_SIZE, 4, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(7, Short.MAX_VALUE))
        );

        jPanel24.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(204, 204, 204), 1, true));

        jLabel34.setFont(new java.awt.Font("SansSerif", 0, 36)); // NOI18N
        jLabel34.setText("200");

        jLabel35.setFont(new java.awt.Font("Cantarell", 0, 16)); // NOI18N
        jLabel35.setText("CEREMONIE ORGANISE AJOURD'HUI");

        javax.swing.GroupLayout jPanel24Layout = new javax.swing.GroupLayout(jPanel24);
        jPanel24.setLayout(jPanel24Layout);
        jPanel24Layout.setHorizontalGroup(
            jPanel24Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel24Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel24Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel35)
                    .addComponent(jLabel34, javax.swing.GroupLayout.PREFERRED_SIZE, 186, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(13, Short.MAX_VALUE))
        );
        jPanel24Layout.setVerticalGroup(
            jPanel24Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel24Layout.createSequentialGroup()
                .addGap(27, 27, 27)
                .addComponent(jLabel35)
                .addGap(18, 18, 18)
                .addComponent(jLabel34, javax.swing.GroupLayout.PREFERRED_SIZE, 41, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jLabel37.setText("jLabel37");

        jLabel38.setText("jLabel37");

        jPanel25.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(204, 204, 204), 1, true));

        jLabel39.setFont(new java.awt.Font("SansSerif", 0, 36)); // NOI18N
        jLabel39.setText("200");

        jLabel40.setFont(new java.awt.Font("Cantarell", 0, 16)); // NOI18N
        jLabel40.setText("TOTAL PERSONNEL");

        javax.swing.GroupLayout jPanel25Layout = new javax.swing.GroupLayout(jPanel25);
        jPanel25.setLayout(jPanel25Layout);
        jPanel25Layout.setHorizontalGroup(
            jPanel25Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel25Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel25Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel39, javax.swing.GroupLayout.PREFERRED_SIZE, 186, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel40))
                .addContainerGap(92, Short.MAX_VALUE))
        );
        jPanel25Layout.setVerticalGroup(
            jPanel25Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel25Layout.createSequentialGroup()
                .addGap(27, 27, 27)
                .addComponent(jLabel40)
                .addGap(18, 18, 18)
                .addComponent(jLabel39, javax.swing.GroupLayout.PREFERRED_SIZE, 41, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(17, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout PanelDashboardLayout = new javax.swing.GroupLayout(PanelDashboard);
        PanelDashboard.setLayout(PanelDashboardLayout);
        PanelDashboardLayout.setHorizontalGroup(
            PanelDashboardLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, PanelDashboardLayout.createSequentialGroup()
                .addGroup(PanelDashboardLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(PanelDashboardLayout.createSequentialGroup()
                        .addGap(17, 17, 17)
                        .addGroup(PanelDashboardLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addGroup(PanelDashboardLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                .addComponent(jButton2, javax.swing.GroupLayout.PREFERRED_SIZE, 215, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(AjouterPanelLouncher, javax.swing.GroupLayout.PREFERRED_SIZE, 215, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(AjoutPersonnelPanel, javax.swing.GroupLayout.PREFERRED_SIZE, 215, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(AjoutPersonnelPanel14, javax.swing.GroupLayout.PREFERRED_SIZE, 215, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addComponent(jLabel21)))
                    .addGroup(PanelDashboardLayout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jLabel37)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jLabel38)))
                .addGap(18, 18, 18)
                .addComponent(jSeparator3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGroup(PanelDashboardLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel11, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(PanelDashboardLayout.createSequentialGroup()
                        .addGap(53, 53, 53)
                        .addGroup(PanelDashboardLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(PanelDashboardLayout.createSequentialGroup()
                                .addComponent(jPanel15, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(37, 37, 37)
                                .addComponent(jPanel16, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(PanelDashboardLayout.createSequentialGroup()
                                .addComponent(jPanel24, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(18, 18, 18)
                                .addComponent(jPanel25, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addGap(18, 18, 18)
                        .addGroup(PanelDashboardLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jPanel12, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jPanel8, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addContainerGap(33, Short.MAX_VALUE))
        );
        PanelDashboardLayout.setVerticalGroup(
            PanelDashboardLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(PanelDashboardLayout.createSequentialGroup()
                .addGroup(PanelDashboardLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(PanelDashboardLayout.createSequentialGroup()
                        .addComponent(jPanel11, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(29, 29, 29)
                        .addGroup(PanelDashboardLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                            .addComponent(jPanel24, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jPanel8, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jPanel25, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(PanelDashboardLayout.createSequentialGroup()
                        .addGap(23, 23, 23)
                        .addComponent(jLabel21)))
                .addGroup(PanelDashboardLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(PanelDashboardLayout.createSequentialGroup()
                        .addGap(18, 18, 18)
                        .addComponent(jButton2, javax.swing.GroupLayout.PREFERRED_SIZE, 46, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(AjouterPanelLouncher, javax.swing.GroupLayout.PREFERRED_SIZE, 46, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(AjoutPersonnelPanel, javax.swing.GroupLayout.PREFERRED_SIZE, 46, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(AjoutPersonnelPanel14, javax.swing.GroupLayout.PREFERRED_SIZE, 46, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(PanelDashboardLayout.createSequentialGroup()
                        .addGap(15, 15, 15)
                        .addComponent(jPanel15, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(PanelDashboardLayout.createSequentialGroup()
                        .addGap(15, 15, 15)
                        .addGroup(PanelDashboardLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jPanel12, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jPanel16, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 214, Short.MAX_VALUE)
                .addGroup(PanelDashboardLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel37)
                    .addComponent(jLabel38))
                .addGap(149, 149, 149))
            .addComponent(jSeparator3, javax.swing.GroupLayout.Alignment.TRAILING)
        );

        MainPanel.addTab("Dashboard", PanelDashboard);

        PanelFamille.setBackground(new java.awt.Color(255, 255, 255));

        jLabel1.setFont(new java.awt.Font("SansSerif", 0, 20)); // NOI18N
        jLabel1.setText("Home/Famille");

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addGap(22, 22, 22)
                .addComponent(jLabel1)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addGap(24, 24, 24)
                .addComponent(jLabel1)
                .addContainerGap(22, Short.MAX_VALUE))
        );

        jPanel4.setBackground(new java.awt.Color(255, 255, 255));
        jPanel4.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(204, 204, 204), 1, true));

        jLabel2.setFont(new java.awt.Font("Cantarell", 0, 36)); // NOI18N
        jLabel2.setText("AJOUTER LA FAMMILE");

        jLabel3.setText("NON DE LA FAMILLE");

        jLabel4.setText("ADRESSE DE LA FAMILLE");

        jLabel5.setText("NUMERO DE LA FAMILLE");

        jTextField1.setFont(new java.awt.Font("Cantarell", 0, 20)); // NOI18N

        jTextField2.setFont(new java.awt.Font("Cantarell", 0, 20)); // NOI18N
        jTextField2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jTextField2ActionPerformed(evt);
            }
        });

        jTextField3.setFont(new java.awt.Font("Cantarell", 0, 20)); // NOI18N

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel4Layout.createSequentialGroup()
                .addContainerGap(138, Short.MAX_VALUE)
                .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 393, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(177, 177, 177))
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addGap(49, 49, 49)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 187, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                        .addComponent(jLabel4, javax.swing.GroupLayout.PREFERRED_SIZE, 187, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel5, javax.swing.GroupLayout.PREFERRED_SIZE, 187, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jTextField1, javax.swing.GroupLayout.DEFAULT_SIZE, 606, Short.MAX_VALUE)
                        .addComponent(jTextField2)
                        .addComponent(jTextField3)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addGap(16, 16, 16)
                .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 47, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(52, 52, 52)
                .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jTextField1, javax.swing.GroupLayout.PREFERRED_SIZE, 46, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jLabel4, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jTextField2, javax.swing.GroupLayout.PREFERRED_SIZE, 46, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jLabel5, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jTextField3, javax.swing.GroupLayout.PREFERRED_SIZE, 46, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(116, Short.MAX_VALUE))
        );

        BtnPrecendent.setText("PRECEDENT");
        BtnPrecendent.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                BtnPrecendentActionPerformed(evt);
            }
        });

        BntSuivant.setText("SIUVANT");
        BntSuivant.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                BntSuivantActionPerformed(evt);
            }
        });

        jProgressBar5.setMaximum(60);
        jProgressBar5.setValue(20);

        jSeparator4.setOrientation(javax.swing.SwingConstants.VERTICAL);

        jLabel47.setIcon(new javax.swing.ImageIcon(getClass().getResource("/projetexam/Capture du 2025-04-29 22-10-58.png"))); // NOI18N

        jButton8.setText("DASHBOARD");
        jButton8.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton8ActionPerformed(evt);
            }
        });

        AjouterPanelLouncher1.setText("AJOUTER UN CLIENT");
        AjouterPanelLouncher1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                AjouterPanelLouncher1ActionPerformed(evt);
            }
        });

        AjoutPersonnelPanel1.setText("GERER LES PERSONNELS");
        AjoutPersonnelPanel1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                AjoutPersonnelPanel1ActionPerformed(evt);
            }
        });

        AjoutPersonnelPanel13.setText("GERER LES MATERIELS");
        AjoutPersonnelPanel13.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                AjoutPersonnelPanel13ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout PanelFamilleLayout = new javax.swing.GroupLayout(PanelFamille);
        PanelFamille.setLayout(PanelFamilleLayout);
        PanelFamilleLayout.setHorizontalGroup(
            PanelFamilleLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, PanelFamilleLayout.createSequentialGroup()
                .addGap(16, 16, 16)
                .addGroup(PanelFamilleLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(PanelFamilleLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addComponent(jButton8, javax.swing.GroupLayout.PREFERRED_SIZE, 215, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(AjouterPanelLouncher1, javax.swing.GroupLayout.PREFERRED_SIZE, 215, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(AjoutPersonnelPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, 215, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(AjoutPersonnelPanel13, javax.swing.GroupLayout.PREFERRED_SIZE, 215, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jLabel47))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(PanelFamilleLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(PanelFamilleLayout.createSequentialGroup()
                        .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addContainerGap())
                    .addGroup(PanelFamilleLayout.createSequentialGroup()
                        .addComponent(jSeparator4, javax.swing.GroupLayout.PREFERRED_SIZE, 3, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGroup(PanelFamilleLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(PanelFamilleLayout.createSequentialGroup()
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addGroup(PanelFamilleLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                    .addGroup(PanelFamilleLayout.createSequentialGroup()
                                        .addComponent(BtnPrecendent)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                        .addComponent(BntSuivant))
                                    .addComponent(jPanel4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addGap(224, 224, 224))
                            .addGroup(PanelFamilleLayout.createSequentialGroup()
                                .addGap(12, 12, 12)
                                .addComponent(jProgressBar5, javax.swing.GroupLayout.PREFERRED_SIZE, 1030, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addContainerGap(116, Short.MAX_VALUE))))))
        );
        PanelFamilleLayout.setVerticalGroup(
            PanelFamilleLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(PanelFamilleLayout.createSequentialGroup()
                .addGroup(PanelFamilleLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(PanelFamilleLayout.createSequentialGroup()
                        .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGroup(PanelFamilleLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addGroup(PanelFamilleLayout.createSequentialGroup()
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jSeparator4, javax.swing.GroupLayout.PREFERRED_SIZE, 678, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(PanelFamilleLayout.createSequentialGroup()
                                .addGap(49, 49, 49)
                                .addComponent(jPanel4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(PanelFamilleLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(BtnPrecendent, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(BntSuivant, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(jProgressBar5, javax.swing.GroupLayout.PREFERRED_SIZE, 10, javax.swing.GroupLayout.PREFERRED_SIZE))))
                    .addGroup(PanelFamilleLayout.createSequentialGroup()
                        .addGap(23, 23, 23)
                        .addComponent(jLabel47)
                        .addGap(18, 18, 18)
                        .addComponent(jButton8, javax.swing.GroupLayout.PREFERRED_SIZE, 46, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(AjouterPanelLouncher1, javax.swing.GroupLayout.PREFERRED_SIZE, 46, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(AjoutPersonnelPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, 46, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(AjoutPersonnelPanel13, javax.swing.GroupLayout.PREFERRED_SIZE, 46, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );

        MainPanel.addTab("Ajout de client", PanelFamille);

        PanelDefunt.setBackground(new java.awt.Color(255, 255, 255));

        jPanel6.setBackground(new java.awt.Color(255, 255, 255));
        jPanel6.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(204, 204, 204), 1, true));

        jLabel6.setFont(new java.awt.Font("Cantarell", 0, 36)); // NOI18N
        jLabel6.setText("JOUTER LE(LA) DEFUNT(E)");

        jLabel7.setText("NON DU DEFUNT");

        jLabel8.setText("PRENOM");

        jLabel9.setText("DATE DE NAISSANCE");

        jTextField4.setFont(new java.awt.Font("Cantarell", 0, 20)); // NOI18N

        jTextField5.setFont(new java.awt.Font("Cantarell", 0, 20)); // NOI18N
        jTextField5.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jTextField5ActionPerformed(evt);
            }
        });

        jTextField6.setFont(new java.awt.Font("Cantarell", 0, 20)); // NOI18N

        jTextField7.setFont(new java.awt.Font("Cantarell", 0, 20)); // NOI18N

        jLabel11.setText("DATE DE DECES");

        jTextField8.setFont(new java.awt.Font("Cantarell", 0, 20)); // NOI18N

        jLabel12.setText("LIEU DE DECES");

        javax.swing.GroupLayout jPanel6Layout = new javax.swing.GroupLayout(jPanel6);
        jPanel6.setLayout(jPanel6Layout);
        jPanel6Layout.setHorizontalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel6Layout.createSequentialGroup()
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel6Layout.createSequentialGroup()
                        .addGap(49, 49, 49)
                        .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(jLabel7, javax.swing.GroupLayout.PREFERRED_SIZE, 187, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel8, javax.swing.GroupLayout.PREFERRED_SIZE, 187, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel9, javax.swing.GroupLayout.PREFERRED_SIZE, 187, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jTextField4, javax.swing.GroupLayout.DEFAULT_SIZE, 606, Short.MAX_VALUE)
                            .addComponent(jTextField5)
                            .addComponent(jTextField6)
                            .addComponent(jLabel11, javax.swing.GroupLayout.PREFERRED_SIZE, 187, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jTextField7)
                            .addComponent(jTextField8)
                            .addComponent(jLabel12, javax.swing.GroupLayout.PREFERRED_SIZE, 187, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(jPanel6Layout.createSequentialGroup()
                        .addGap(115, 115, 115)
                        .addComponent(jLabel6, javax.swing.GroupLayout.PREFERRED_SIZE, 467, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(53, Short.MAX_VALUE))
        );
        jPanel6Layout.setVerticalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel6Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel6, javax.swing.GroupLayout.PREFERRED_SIZE, 47, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jLabel7, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jTextField4, javax.swing.GroupLayout.PREFERRED_SIZE, 46, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jLabel8, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jTextField5, javax.swing.GroupLayout.PREFERRED_SIZE, 46, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel9, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jTextField6, javax.swing.GroupLayout.PREFERRED_SIZE, 46, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel11, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jTextField7, javax.swing.GroupLayout.PREFERRED_SIZE, 46, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel12, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jTextField8, javax.swing.GroupLayout.PREFERRED_SIZE, 46, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(19, Short.MAX_VALUE))
        );

        jLabel10.setFont(new java.awt.Font("SansSerif", 0, 20)); // NOI18N
        jLabel10.setText("Home/Defunt");

        javax.swing.GroupLayout jPanel7Layout = new javax.swing.GroupLayout(jPanel7);
        jPanel7.setLayout(jPanel7Layout);
        jPanel7Layout.setHorizontalGroup(
            jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel7Layout.createSequentialGroup()
                .addGap(22, 22, 22)
                .addComponent(jLabel10)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel7Layout.setVerticalGroup(
            jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel7Layout.createSequentialGroup()
                .addGap(24, 24, 24)
                .addComponent(jLabel10)
                .addContainerGap(22, Short.MAX_VALUE))
        );

        BntSuivant2.setText("SIUVANT");
        BntSuivant2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                BntSuivant2ActionPerformed(evt);
            }
        });

        BtnPrecendent2.setText("PRECEDENT");
        BtnPrecendent2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                BtnPrecendent2ActionPerformed(evt);
            }
        });

        jProgressBar4.setMaximum(60);
        jProgressBar4.setValue(40);

        jSeparator5.setOrientation(javax.swing.SwingConstants.VERTICAL);

        jLabel48.setIcon(new javax.swing.ImageIcon(getClass().getResource("/projetexam/Capture du 2025-04-29 22-10-58.png"))); // NOI18N

        jButton9.setText("DASHBOARD");
        jButton9.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton9ActionPerformed(evt);
            }
        });

        AjouterPanelLouncher10.setText("AJOUTER UN CLIENT");
        AjouterPanelLouncher10.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                AjouterPanelLouncher10ActionPerformed(evt);
            }
        });

        AjoutPersonnelPanel2.setText("GERER LES PERSONNELS");
        AjoutPersonnelPanel2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                AjoutPersonnelPanel2ActionPerformed(evt);
            }
        });

        AjoutPersonnelPanel12.setText("GERER LES MATERIELS");
        AjoutPersonnelPanel12.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                AjoutPersonnelPanel12ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout PanelDefuntLayout = new javax.swing.GroupLayout(PanelDefunt);
        PanelDefunt.setLayout(PanelDefuntLayout);
        PanelDefuntLayout.setHorizontalGroup(
            PanelDefuntLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(PanelDefuntLayout.createSequentialGroup()
                .addGap(16, 16, 16)
                .addGroup(PanelDefuntLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(PanelDefuntLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addComponent(jButton9, javax.swing.GroupLayout.PREFERRED_SIZE, 215, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(AjouterPanelLouncher10, javax.swing.GroupLayout.PREFERRED_SIZE, 215, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(AjoutPersonnelPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, 215, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(AjoutPersonnelPanel12, javax.swing.GroupLayout.PREFERRED_SIZE, 215, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jLabel48))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(PanelDefuntLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(PanelDefuntLayout.createSequentialGroup()
                        .addComponent(jSeparator5, javax.swing.GroupLayout.PREFERRED_SIZE, 3, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGroup(PanelDefuntLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(PanelDefuntLayout.createSequentialGroup()
                                .addGap(190, 190, 190)
                                .addGroup(PanelDefuntLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                    .addGroup(PanelDefuntLayout.createSequentialGroup()
                                        .addComponent(BtnPrecendent2)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                        .addComponent(BntSuivant2))
                                    .addComponent(jPanel6, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                            .addGroup(PanelDefuntLayout.createSequentialGroup()
                                .addGap(18, 18, 18)
                                .addComponent(jProgressBar4, javax.swing.GroupLayout.PREFERRED_SIZE, 1023, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addGap(99, 99, 99))
                    .addComponent(jPanel7, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
        );
        PanelDefuntLayout.setVerticalGroup(
            PanelDefuntLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, PanelDefuntLayout.createSequentialGroup()
                .addGap(0, 0, Short.MAX_VALUE)
                .addComponent(jSeparator5, javax.swing.GroupLayout.PREFERRED_SIZE, 729, javax.swing.GroupLayout.PREFERRED_SIZE))
            .addGroup(PanelDefuntLayout.createSequentialGroup()
                .addGroup(PanelDefuntLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(PanelDefuntLayout.createSequentialGroup()
                        .addComponent(jPanel7, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(49, 49, 49)
                        .addComponent(jPanel6, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(PanelDefuntLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(BtnPrecendent2, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(BntSuivant2, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(PanelDefuntLayout.createSequentialGroup()
                        .addGap(23, 23, 23)
                        .addComponent(jLabel48)
                        .addGap(18, 18, 18)
                        .addComponent(jButton9, javax.swing.GroupLayout.PREFERRED_SIZE, 46, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(AjouterPanelLouncher10, javax.swing.GroupLayout.PREFERRED_SIZE, 46, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(AjoutPersonnelPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, 46, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(AjoutPersonnelPanel12, javax.swing.GroupLayout.PREFERRED_SIZE, 46, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(28, 28, 28)
                .addComponent(jProgressBar4, javax.swing.GroupLayout.PREFERRED_SIZE, 10, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(108, Short.MAX_VALUE))
        );

        MainPanel.addTab("Defunt", PanelDefunt);

        PanelCeremonie.setBackground(new java.awt.Color(255, 255, 255));

        jPanel9.setBackground(new java.awt.Color(255, 255, 255));
        jPanel9.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(204, 204, 204), 1, true));

        jLabel13.setFont(new java.awt.Font("Cantarell", 0, 36)); // NOI18N
        jLabel13.setText("DETAILS DE LA CEREMONIE");

        jLabel15.setText("DATE DU CEREMONIE");

        jLabel16.setText("HEURE DE CEREMONIE");

        Date.setFont(new java.awt.Font("Cantarell", 0, 20)); // NOI18N
        Date.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                DateActionPerformed(evt);
            }
        });

        Heure.setFont(new java.awt.Font("Cantarell", 0, 20)); // NOI18N

        Lieu.setFont(new java.awt.Font("Cantarell", 0, 20)); // NOI18N

        jLabel17.setText("LIEU DU CERMONIE");

        Type.setFont(new java.awt.Font("Cantarell", 0, 20)); // NOI18N

        jLabel18.setText("TYPE DE CEREMOINIE");

        javax.swing.GroupLayout jPanel9Layout = new javax.swing.GroupLayout(jPanel9);
        jPanel9.setLayout(jPanel9Layout);
        jPanel9Layout.setHorizontalGroup(
            jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel9Layout.createSequentialGroup()
                .addGroup(jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel9Layout.createSequentialGroup()
                        .addGap(115, 115, 115)
                        .addComponent(jLabel13, javax.swing.GroupLayout.PREFERRED_SIZE, 467, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel9Layout.createSequentialGroup()
                        .addGap(41, 41, 41)
                        .addGroup(jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(jLabel15, javax.swing.GroupLayout.PREFERRED_SIZE, 187, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel16, javax.swing.GroupLayout.PREFERRED_SIZE, 187, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(Date)
                            .addComponent(Heure)
                            .addComponent(jLabel17, javax.swing.GroupLayout.PREFERRED_SIZE, 187, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(Lieu)
                            .addComponent(Type, javax.swing.GroupLayout.PREFERRED_SIZE, 606, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel18, javax.swing.GroupLayout.PREFERRED_SIZE, 187, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addContainerGap(61, Short.MAX_VALUE))
        );
        jPanel9Layout.setVerticalGroup(
            jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel9Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel13, javax.swing.GroupLayout.PREFERRED_SIZE, 47, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(54, 54, 54)
                .addComponent(jLabel15)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(Date, javax.swing.GroupLayout.PREFERRED_SIZE, 46, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel16)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(Heure, javax.swing.GroupLayout.PREFERRED_SIZE, 46, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel17)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(Lieu, javax.swing.GroupLayout.PREFERRED_SIZE, 46, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel18)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(Type, javax.swing.GroupLayout.PREFERRED_SIZE, 46, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(74, Short.MAX_VALUE))
        );

        jLabel19.setFont(new java.awt.Font("SansSerif", 0, 20)); // NOI18N
        jLabel19.setText("Home/Ceremonie");

        javax.swing.GroupLayout jPanel10Layout = new javax.swing.GroupLayout(jPanel10);
        jPanel10.setLayout(jPanel10Layout);
        jPanel10Layout.setHorizontalGroup(
            jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel10Layout.createSequentialGroup()
                .addGap(22, 22, 22)
                .addComponent(jLabel19)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel10Layout.setVerticalGroup(
            jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel10Layout.createSequentialGroup()
                .addGap(24, 24, 24)
                .addComponent(jLabel19)
                .addContainerGap(22, Short.MAX_VALUE))
        );

        BtnPrecendent1.setText("PRECEDENT");
        BtnPrecendent1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                BtnPrecendent1ActionPerformed(evt);
            }
        });

        BntSuivant1.setText("FACTURATION");
        BntSuivant1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                BntSuivant1ActionPerformed(evt);
            }
        });

        jProgressBar1.setMaximum(60);
        jProgressBar1.setValue(60);

        jSeparator6.setOrientation(javax.swing.SwingConstants.VERTICAL);

        jLabel49.setIcon(new javax.swing.ImageIcon(getClass().getResource("/projetexam/Capture du 2025-04-29 22-10-58.png"))); // NOI18N

        jButton10.setText("DASHBOARD");
        jButton10.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton10ActionPerformed(evt);
            }
        });

        AjouterPanelLouncher11.setText("AJOUTER UN CLIENT");
        AjouterPanelLouncher11.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                AjouterPanelLouncher11ActionPerformed(evt);
            }
        });

        AjoutPersonnelPanel3.setText("GERER LES PERSONNELS");
        AjoutPersonnelPanel3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                AjoutPersonnelPanel3ActionPerformed(evt);
            }
        });

        AjoutPersonnelPanel6.setText("GERER LES MATERIELS");
        AjoutPersonnelPanel6.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                AjoutPersonnelPanel6ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout PanelCeremonieLayout = new javax.swing.GroupLayout(PanelCeremonie);
        PanelCeremonie.setLayout(PanelCeremonieLayout);
        PanelCeremonieLayout.setHorizontalGroup(
            PanelCeremonieLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(PanelCeremonieLayout.createSequentialGroup()
                .addGap(16, 16, 16)
                .addGroup(PanelCeremonieLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(PanelCeremonieLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addComponent(jButton10, javax.swing.GroupLayout.PREFERRED_SIZE, 215, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(AjouterPanelLouncher11, javax.swing.GroupLayout.PREFERRED_SIZE, 215, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(AjoutPersonnelPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, 215, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(AjoutPersonnelPanel6, javax.swing.GroupLayout.PREFERRED_SIZE, 215, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jLabel49))
                .addGap(27, 27, 27)
                .addGroup(PanelCeremonieLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel10, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(PanelCeremonieLayout.createSequentialGroup()
                        .addComponent(jSeparator6, javax.swing.GroupLayout.PREFERRED_SIZE, 3, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGroup(PanelCeremonieLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(PanelCeremonieLayout.createSequentialGroup()
                                .addGap(196, 196, 196)
                                .addGroup(PanelCeremonieLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                    .addGroup(PanelCeremonieLayout.createSequentialGroup()
                                        .addComponent(BtnPrecendent1)
                                        .addGap(470, 470, 470)
                                        .addComponent(BntSuivant1))
                                    .addComponent(jPanel9, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                            .addGroup(PanelCeremonieLayout.createSequentialGroup()
                                .addGap(18, 18, 18)
                                .addComponent(jProgressBar1, javax.swing.GroupLayout.PREFERRED_SIZE, 985, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addContainerGap(140, Short.MAX_VALUE))))
        );
        PanelCeremonieLayout.setVerticalGroup(
            PanelCeremonieLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
            .addGroup(javax.swing.GroupLayout.Alignment.LEADING, PanelCeremonieLayout.createSequentialGroup()
                .addGap(23, 23, 23)
                .addComponent(jLabel49)
                .addGap(18, 18, 18)
                .addComponent(jButton10, javax.swing.GroupLayout.PREFERRED_SIZE, 46, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(AjouterPanelLouncher11, javax.swing.GroupLayout.PREFERRED_SIZE, 46, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(AjoutPersonnelPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, 46, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(AjoutPersonnelPanel6, javax.swing.GroupLayout.PREFERRED_SIZE, 46, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addGroup(PanelCeremonieLayout.createSequentialGroup()
                .addComponent(jPanel10, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGroup(PanelCeremonieLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, PanelCeremonieLayout.createSequentialGroup()
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jSeparator6, javax.swing.GroupLayout.PREFERRED_SIZE, 786, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(PanelCeremonieLayout.createSequentialGroup()
                        .addGap(49, 49, 49)
                        .addComponent(jPanel9, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addGroup(PanelCeremonieLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(BtnPrecendent1, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(BntSuivant1, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(73, 73, 73)
                        .addComponent(jProgressBar1, javax.swing.GroupLayout.PREFERRED_SIZE, 10, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addContainerGap())))
        );

        MainPanel.addTab("Ceremonies", PanelCeremonie);

        jPanel1.setBackground(new java.awt.Color(255, 255, 255));

        jLabel29.setFont(new java.awt.Font("SansSerif", 0, 20)); // NOI18N
        jLabel29.setText("Home/Facturation");

        javax.swing.GroupLayout jPanel13Layout = new javax.swing.GroupLayout(jPanel13);
        jPanel13.setLayout(jPanel13Layout);
        jPanel13Layout.setHorizontalGroup(
            jPanel13Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel13Layout.createSequentialGroup()
                .addGap(22, 22, 22)
                .addComponent(jLabel29)
                .addContainerGap(925, Short.MAX_VALUE))
        );
        jPanel13Layout.setVerticalGroup(
            jPanel13Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel13Layout.createSequentialGroup()
                .addGap(24, 24, 24)
                .addComponent(jLabel29)
                .addContainerGap(22, Short.MAX_VALUE))
        );

        jPanel14.setBackground(new java.awt.Color(255, 255, 255));
        jPanel14.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(204, 204, 204)));

        jLabel36.setFont(new java.awt.Font("Cantarell", 0, 20)); // NOI18N
        jLabel36.setText("Totale a Payer :");

        jButton7.setText("CALCULER");
        jButton7.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton7ActionPerformed(evt);
            }
        });

        Total.setText("Total");

        jButton22.setText("GENERER");
        jButton22.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton22ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel14Layout = new javax.swing.GroupLayout(jPanel14);
        jPanel14.setLayout(jPanel14Layout);
        jPanel14Layout.setHorizontalGroup(
            jPanel14Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel14Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel14Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel14Layout.createSequentialGroup()
                        .addComponent(jLabel36, javax.swing.GroupLayout.PREFERRED_SIZE, 147, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(61, 61, 61)
                        .addComponent(Total)
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel14Layout.createSequentialGroup()
                        .addGroup(jPanel14Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jSeparator1, javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel14Layout.createSequentialGroup()
                                .addComponent(jButton7, javax.swing.GroupLayout.PREFERRED_SIZE, 121, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jSeparator2)
                                .addGap(124, 124, 124)
                                .addComponent(jButton22, javax.swing.GroupLayout.PREFERRED_SIZE, 121, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addGap(204, 204, 204))))
        );
        jPanel14Layout.setVerticalGroup(
            jPanel14Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel14Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel14Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel36, javax.swing.GroupLayout.PREFERRED_SIZE, 36, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(Total))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jSeparator1, javax.swing.GroupLayout.PREFERRED_SIZE, 4, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGroup(jPanel14Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel14Layout.createSequentialGroup()
                        .addGap(18, 18, Short.MAX_VALUE)
                        .addComponent(jSeparator2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(113, 113, 113))
                    .addGroup(jPanel14Layout.createSequentialGroup()
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel14Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jButton7, javax.swing.GroupLayout.PREFERRED_SIZE, 37, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jButton22, javax.swing.GroupLayout.PREFERRED_SIZE, 37, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
        );

        jLabel51.setIcon(new javax.swing.ImageIcon(getClass().getResource("/projetexam/Capture du 2025-04-29 22-10-58.png"))); // NOI18N

        jButton12.setText("DASHBOARD");
        jButton12.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton12ActionPerformed(evt);
            }
        });

        AjouterPanelLouncher13.setText("AJOUTER UN CLIENT");
        AjouterPanelLouncher13.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                AjouterPanelLouncher13ActionPerformed(evt);
            }
        });

        AjoutPersonnelPanel5.setText("GERER LES PERSONNELS");
        AjoutPersonnelPanel5.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                AjoutPersonnelPanel5ActionPerformed(evt);
            }
        });

        jSeparator8.setOrientation(javax.swing.SwingConstants.VERTICAL);

        AjoutPersonnelPanel11.setText("GERER LES MATERIELS");
        AjoutPersonnelPanel11.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                AjoutPersonnelPanel11ActionPerformed(evt);
            }
        });

        jTableMateriel1.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {},
                {},
                {},
                {}
            },
            new String [] {

            }
        ));
        jScrollPane3.setViewportView(jTableMateriel1);

        jLabel22.setText("Ajouter les materiels necessaire pour la ceremonie");

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(17, 17, 17)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addComponent(jButton12, javax.swing.GroupLayout.PREFERRED_SIZE, 215, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(AjouterPanelLouncher13, javax.swing.GroupLayout.PREFERRED_SIZE, 215, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(AjoutPersonnelPanel5, javax.swing.GroupLayout.PREFERRED_SIZE, 215, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(AjoutPersonnelPanel11, javax.swing.GroupLayout.PREFERRED_SIZE, 215, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jLabel51))
                .addGap(27, 27, 27)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel13, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                        .addComponent(jPanel14, javax.swing.GroupLayout.PREFERRED_SIZE, 393, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGroup(jPanel1Layout.createSequentialGroup()
                            .addComponent(jSeparator8, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                            .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 981, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(jLabel22, javax.swing.GroupLayout.PREFERRED_SIZE, 373, javax.swing.GroupLayout.PREFERRED_SIZE)))))
                .addContainerGap(17, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(23, 23, 23)
                .addComponent(jLabel51)
                .addGap(18, 18, 18)
                .addComponent(jButton12, javax.swing.GroupLayout.PREFERRED_SIZE, 46, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(AjouterPanelLouncher13, javax.swing.GroupLayout.PREFERRED_SIZE, 46, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(AjoutPersonnelPanel5, javax.swing.GroupLayout.PREFERRED_SIZE, 46, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(AjoutPersonnelPanel11, javax.swing.GroupLayout.PREFERRED_SIZE, 46, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addComponent(jPanel13, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jSeparator8, javax.swing.GroupLayout.DEFAULT_SIZE, 780, Short.MAX_VALUE)
                        .addContainerGap())
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(58, 58, 58)
                        .addComponent(jLabel22)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 343, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(jPanel14, javax.swing.GroupLayout.PREFERRED_SIZE, 115, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
        );

        MainPanel.addTab("Facturation", jPanel1);

        jPanel2.setBackground(new java.awt.Color(255, 255, 255));

        jLabel45.setFont(new java.awt.Font("SansSerif", 0, 20)); // NOI18N
        jLabel45.setText("Ajout Personnel");

        javax.swing.GroupLayout jPanel17Layout = new javax.swing.GroupLayout(jPanel17);
        jPanel17.setLayout(jPanel17Layout);
        jPanel17Layout.setHorizontalGroup(
            jPanel17Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel17Layout.createSequentialGroup()
                .addGap(22, 22, 22)
                .addComponent(jLabel45)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel17Layout.setVerticalGroup(
            jPanel17Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel17Layout.createSequentialGroup()
                .addGap(24, 24, 24)
                .addComponent(jLabel45)
                .addContainerGap(22, Short.MAX_VALUE))
        );

        jSeparator7.setOrientation(javax.swing.SwingConstants.VERTICAL);

        jLabel50.setIcon(new javax.swing.ImageIcon(getClass().getResource("/projetexam/Capture du 2025-04-29 22-10-58.png"))); // NOI18N

        jButton11.setText("DASHBOARD");
        jButton11.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton11ActionPerformed(evt);
            }
        });

        AjouterPanelLouncher12.setText("AJOUTER UN CLIENT");
        AjouterPanelLouncher12.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                AjouterPanelLouncher12ActionPerformed(evt);
            }
        });

        AjoutPersonnelPanel4.setText("GERER LES PERSONNELS");
        AjoutPersonnelPanel4.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                AjoutPersonnelPanel4ActionPerformed(evt);
            }
        });

        AjoutPersonnelPanel10.setText("GERER LES MATERIELS");
        AjoutPersonnelPanel10.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                AjoutPersonnelPanel10ActionPerformed(evt);
            }
        });

        PersonnelTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {},
                {},
                {},
                {}
            },
            new String [] {

            }
        ));
        jScrollPane2.setViewportView(PersonnelTable);

        jButton17.setText("Supprimer");

        jButton18.setText("Modifier");
        jButton18.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton18ActionPerformed(evt);
            }
        });

        jButton19.setText("Ajouter");
        jButton19.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton19ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGap(17, 17, 17)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addComponent(jButton11, javax.swing.GroupLayout.PREFERRED_SIZE, 215, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(AjouterPanelLouncher12, javax.swing.GroupLayout.PREFERRED_SIZE, 215, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(AjoutPersonnelPanel4, javax.swing.GroupLayout.PREFERRED_SIZE, 215, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(AjoutPersonnelPanel10, javax.swing.GroupLayout.PREFERRED_SIZE, 215, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jLabel50))
                .addGap(27, 27, 27)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(jSeparator7, javax.swing.GroupLayout.PREFERRED_SIZE, 3, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 993, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addComponent(jButton19, javax.swing.GroupLayout.PREFERRED_SIZE, 117, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jButton18, javax.swing.GroupLayout.PREFERRED_SIZE, 117, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jButton17, javax.swing.GroupLayout.PREFERRED_SIZE, 117, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addGap(129, 129, 129))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(jPanel17, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addContainerGap())))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGap(3, 3, 3)
                        .addComponent(jPanel17, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jSeparator7, javax.swing.GroupLayout.PREFERRED_SIZE, 678, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addGap(67, 67, 67)
                                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jButton17)
                                    .addComponent(jButton18)
                                    .addComponent(jButton19))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 521, javax.swing.GroupLayout.PREFERRED_SIZE))))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGap(23, 23, 23)
                        .addComponent(jLabel50)
                        .addGap(18, 18, 18)
                        .addComponent(jButton11, javax.swing.GroupLayout.PREFERRED_SIZE, 46, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(AjouterPanelLouncher12, javax.swing.GroupLayout.PREFERRED_SIZE, 46, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(AjoutPersonnelPanel4, javax.swing.GroupLayout.PREFERRED_SIZE, 46, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(AjoutPersonnelPanel10, javax.swing.GroupLayout.PREFERRED_SIZE, 46, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(105, Short.MAX_VALUE))
        );

        MainPanel.addTab("Personnel", jPanel2);

        jPanel18.setBackground(new java.awt.Color(255, 255, 255));

        jLabel52.setIcon(new javax.swing.ImageIcon(getClass().getResource("/projetexam/Capture du 2025-04-29 22-10-58.png"))); // NOI18N

        jButton13.setText("DASHBOARD");
        jButton13.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton13ActionPerformed(evt);
            }
        });

        AjouterPanelLouncher14.setText("AJOUTER UN CLIENT");
        AjouterPanelLouncher14.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                AjouterPanelLouncher14ActionPerformed(evt);
            }
        });

        AjoutPersonnelPanel7.setText("GERER LES PERSONNELS");
        AjoutPersonnelPanel7.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                AjoutPersonnelPanel7ActionPerformed(evt);
            }
        });

        AjoutPersonnelPanel8.setText("GERER LES MATERIELS");
        AjoutPersonnelPanel8.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                AjoutPersonnelPanel8ActionPerformed(evt);
            }
        });

        jSeparator9.setOrientation(javax.swing.SwingConstants.VERTICAL);

        jLabel46.setFont(new java.awt.Font("SansSerif", 0, 20)); // NOI18N
        jLabel46.setText("Home/Materiels");

        javax.swing.GroupLayout MaterielLayout = new javax.swing.GroupLayout(Materiel);
        Materiel.setLayout(MaterielLayout);
        MaterielLayout.setHorizontalGroup(
            MaterielLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(MaterielLayout.createSequentialGroup()
                .addGap(22, 22, 22)
                .addComponent(jLabel46)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        MaterielLayout.setVerticalGroup(
            MaterielLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(MaterielLayout.createSequentialGroup()
                .addGap(24, 24, 24)
                .addComponent(jLabel46)
                .addContainerGap(22, Short.MAX_VALUE))
        );

        jTableMateriel.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {},
                {},
                {},
                {}
            },
            new String [] {

            }
        ));
        jScrollPane1.setViewportView(jTableMateriel);

        jToggleButton1.setText("Modifier");

        jToggleButton2.setText("Modifier");
        jToggleButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jToggleButton2ActionPerformed(evt);
            }
        });

        jToggleButton3.setText("Ajouter");
        jToggleButton3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jToggleButton3ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel18Layout = new javax.swing.GroupLayout(jPanel18);
        jPanel18.setLayout(jPanel18Layout);
        jPanel18Layout.setHorizontalGroup(
            jPanel18Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel18Layout.createSequentialGroup()
                .addGap(17, 17, 17)
                .addGroup(jPanel18Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(jPanel18Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addComponent(jButton13, javax.swing.GroupLayout.PREFERRED_SIZE, 215, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(AjouterPanelLouncher14, javax.swing.GroupLayout.PREFERRED_SIZE, 215, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(AjoutPersonnelPanel7, javax.swing.GroupLayout.PREFERRED_SIZE, 215, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(AjoutPersonnelPanel8, javax.swing.GroupLayout.PREFERRED_SIZE, 215, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jLabel52))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel18Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel18Layout.createSequentialGroup()
                        .addComponent(jSeparator9, javax.swing.GroupLayout.PREFERRED_SIZE, 3, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(27, 27, 27)
                        .addGroup(jPanel18Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addGroup(jPanel18Layout.createSequentialGroup()
                                .addComponent(jToggleButton3, javax.swing.GroupLayout.PREFERRED_SIZE, 112, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jToggleButton2, javax.swing.GroupLayout.PREFERRED_SIZE, 112, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jToggleButton1, javax.swing.GroupLayout.PREFERRED_SIZE, 112, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 1000, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addContainerGap(130, Short.MAX_VALUE))
                    .addGroup(jPanel18Layout.createSequentialGroup()
                        .addComponent(Materiel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGap(8, 8, 8))))
        );
        jPanel18Layout.setVerticalGroup(
            jPanel18Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel18Layout.createSequentialGroup()
                .addGroup(jPanel18Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel18Layout.createSequentialGroup()
                        .addComponent(Materiel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGroup(jPanel18Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel18Layout.createSequentialGroup()
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jSeparator9, javax.swing.GroupLayout.PREFERRED_SIZE, 779, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(jPanel18Layout.createSequentialGroup()
                                .addGap(76, 76, 76)
                                .addGroup(jPanel18Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jToggleButton1)
                                    .addComponent(jToggleButton2)
                                    .addComponent(jToggleButton3))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 523, javax.swing.GroupLayout.PREFERRED_SIZE))))
                    .addGroup(jPanel18Layout.createSequentialGroup()
                        .addGap(23, 23, 23)
                        .addComponent(jLabel52)
                        .addGap(18, 18, 18)
                        .addComponent(jButton13, javax.swing.GroupLayout.PREFERRED_SIZE, 46, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(AjouterPanelLouncher14, javax.swing.GroupLayout.PREFERRED_SIZE, 46, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(AjoutPersonnelPanel7, javax.swing.GroupLayout.PREFERRED_SIZE, 46, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(AjoutPersonnelPanel8, javax.swing.GroupLayout.PREFERRED_SIZE, 46, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );

        MainPanel.addTab("Materiel", jPanel18);

        jPanel20.setBackground(new java.awt.Color(255, 255, 255));

        jPasswordField1.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(242, 242, 242), 1, true));

        jButton3.setFont(new java.awt.Font("Arial Rounded MT Bold", 0, 18)); // NOI18N
        jButton3.setText("Submit");
        jButton3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton3ActionPerformed(evt);
            }
        });

        jLabel54.setFont(new java.awt.Font("Comic Sans MS", 1, 36)); // NOI18N
        jLabel54.setForeground(new java.awt.Color(108, 117, 125));
        jLabel54.setText("GoodLife after Death");

        jLabel55.setFont(new java.awt.Font("Arial Rounded MT Bold", 0, 18)); // NOI18N
        jLabel55.setForeground(new java.awt.Color(108, 117, 125));
        jLabel55.setText("already have an account ");

        jButton4.setFont(new java.awt.Font("Arial Rounded MT Bold", 0, 18)); // NOI18N
        jButton4.setForeground(new java.awt.Color(33, 37, 41));
        jButton4.setText("login");
        jButton4.setBorder(null);
        jButton4.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton4ActionPerformed(evt);
            }
        });

        jLabel56.setFont(new java.awt.Font("Arial Rounded MT Bold", 0, 18)); // NOI18N
        jLabel56.setForeground(new java.awt.Color(108, 117, 125));
        jLabel56.setText("Email :");

        jTextField17.setFont(new java.awt.Font("Arial Rounded MT Bold", 0, 18)); // NOI18N
        jTextField17.setForeground(new java.awt.Color(173, 181, 189));
        jTextField17.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(242, 242, 242), 1, true));
        jTextField17.setName("username"); // NOI18N
        jTextField17.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jTextField17ActionPerformed(evt);
            }
        });

        jLabel57.setFont(new java.awt.Font("Arial Rounded MT Bold", 0, 18)); // NOI18N
        jLabel57.setForeground(new java.awt.Color(108, 117, 125));
        jLabel57.setText("Password :");

        jLabel58.setFont(new java.awt.Font("Arial Rounded MT Bold", 0, 24)); // NOI18N
        jLabel58.setForeground(new java.awt.Color(108, 117, 125));
        jLabel58.setText("welcome back");

        javax.swing.GroupLayout jPanel20Layout = new javax.swing.GroupLayout(jPanel20);
        jPanel20.setLayout(jPanel20Layout);
        jPanel20Layout.setHorizontalGroup(
            jPanel20Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel20Layout.createSequentialGroup()
                .addGap(54, 54, 54)
                .addGroup(jPanel20Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel20Layout.createSequentialGroup()
                        .addComponent(jLabel55)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jButton4, javax.swing.GroupLayout.PREFERRED_SIZE, 59, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jLabel54)
                    .addGroup(jPanel20Layout.createSequentialGroup()
                        .addGap(63, 63, 63)
                        .addGroup(jPanel20Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel58, javax.swing.GroupLayout.PREFERRED_SIZE, 224, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(jPanel20Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                .addComponent(jLabel56, javax.swing.GroupLayout.PREFERRED_SIZE, 114, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(jLabel57, javax.swing.GroupLayout.PREFERRED_SIZE, 114, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(jTextField17)
                                .addComponent(jPasswordField1)
                                .addComponent(jButton3, javax.swing.GroupLayout.PREFERRED_SIZE, 254, javax.swing.GroupLayout.PREFERRED_SIZE)))))
                .addContainerGap(930, Short.MAX_VALUE))
        );
        jPanel20Layout.setVerticalGroup(
            jPanel20Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel20Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jLabel54, javax.swing.GroupLayout.PREFERRED_SIZE, 52, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(89, 89, 89)
                .addComponent(jLabel58)
                .addGap(36, 36, 36)
                .addComponent(jLabel56, javax.swing.GroupLayout.PREFERRED_SIZE, 26, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jTextField17, javax.swing.GroupLayout.PREFERRED_SIZE, 38, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel57, javax.swing.GroupLayout.PREFERRED_SIZE, 26, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPasswordField1, javax.swing.GroupLayout.PREFERRED_SIZE, 38, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jButton3, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(204, 204, 204)
                .addGroup(jPanel20Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel55)
                    .addComponent(jButton4))
                .addGap(154, 154, 154))
        );

        MainPanel.addTab("login", jPanel20);

        jPanel21.setBackground(new java.awt.Color(255, 255, 255));
        jPanel21.setPreferredSize(new java.awt.Dimension(888, 460));

        jLabel59.setFont(new java.awt.Font("Arial Rounded MT Bold", 0, 18)); // NOI18N
        jLabel59.setForeground(new java.awt.Color(108, 117, 125));
        jLabel59.setText("confirm password :");

        jPasswordField2.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(242, 242, 242), 1, true));

        jPasswordField3.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(242, 242, 242), 1, true));

        jButton5.setFont(new java.awt.Font("Arial Rounded MT Bold", 0, 18)); // NOI18N
        jButton5.setText("Submit");
        jButton5.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton5ActionPerformed(evt);
            }
        });

        jLabel60.setFont(new java.awt.Font("Comic Sans MS", 1, 36)); // NOI18N
        jLabel60.setForeground(new java.awt.Color(108, 117, 125));
        jLabel60.setText("GoodLife after Death");

        jLabel61.setFont(new java.awt.Font("Arial Rounded MT Bold", 0, 18)); // NOI18N
        jLabel61.setForeground(new java.awt.Color(108, 117, 125));
        jLabel61.setText("already have an account ");

        jLabel62.setFont(new java.awt.Font("Arial Rounded MT Bold", 0, 18)); // NOI18N
        jLabel62.setForeground(new java.awt.Color(108, 117, 125));
        jLabel62.setText("Username :");

        jTextField18.setFont(new java.awt.Font("Arial Rounded MT Bold", 0, 12)); // NOI18N
        jTextField18.setForeground(new java.awt.Color(173, 181, 189));
        jTextField18.setToolTipText("");
        jTextField18.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(242, 242, 242), 1, true));
        jTextField18.setName("username"); // NOI18N

        jButton6.setFont(new java.awt.Font("Arial Rounded MT Bold", 0, 18)); // NOI18N
        jButton6.setForeground(new java.awt.Color(33, 37, 41));
        jButton6.setText("login");
        jButton6.setBorder(null);
        jButton6.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton6ActionPerformed(evt);
            }
        });

        jLabel63.setFont(new java.awt.Font("Arial Rounded MT Bold", 0, 18)); // NOI18N
        jLabel63.setForeground(new java.awt.Color(108, 117, 125));
        jLabel63.setText("Email :");

        jTextField19.setFont(new java.awt.Font("Arial Rounded MT Bold", 0, 12)); // NOI18N
        jTextField19.setForeground(new java.awt.Color(173, 181, 189));
        jTextField19.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(242, 242, 242), 1, true));
        jTextField19.setName("username"); // NOI18N
        jTextField19.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jTextField19ActionPerformed(evt);
            }
        });

        jLabel64.setFont(new java.awt.Font("Arial Rounded MT Bold", 0, 18)); // NOI18N
        jLabel64.setForeground(new java.awt.Color(108, 117, 125));
        jLabel64.setText("Password :");

        jLabel66.setFont(new java.awt.Font("Arial Rounded MT Bold", 0, 24)); // NOI18N
        jLabel66.setForeground(new java.awt.Color(108, 117, 125));
        jLabel66.setText("welcome back");

        javax.swing.GroupLayout jPanel21Layout = new javax.swing.GroupLayout(jPanel21);
        jPanel21.setLayout(jPanel21Layout);
        jPanel21Layout.setHorizontalGroup(
            jPanel21Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel21Layout.createSequentialGroup()
                .addGroup(jPanel21Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel21Layout.createSequentialGroup()
                        .addGap(92, 92, 92)
                        .addGroup(jPanel21Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(jButton5, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jLabel62, javax.swing.GroupLayout.PREFERRED_SIZE, 114, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jTextField18)
                            .addComponent(jLabel63, javax.swing.GroupLayout.PREFERRED_SIZE, 114, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jTextField19, javax.swing.GroupLayout.DEFAULT_SIZE, 303, Short.MAX_VALUE)
                            .addComponent(jLabel64, javax.swing.GroupLayout.PREFERRED_SIZE, 114, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jPasswordField2)
                            .addComponent(jLabel59, javax.swing.GroupLayout.PREFERRED_SIZE, 193, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jPasswordField3)))
                    .addGroup(jPanel21Layout.createSequentialGroup()
                        .addGap(79, 79, 79)
                        .addComponent(jLabel61)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jButton6, javax.swing.GroupLayout.PREFERRED_SIZE, 73, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel21Layout.createSequentialGroup()
                        .addGap(54, 54, 54)
                        .addComponent(jLabel60))
                    .addGroup(jPanel21Layout.createSequentialGroup()
                        .addGap(92, 92, 92)
                        .addComponent(jLabel66, javax.swing.GroupLayout.PREFERRED_SIZE, 224, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(66, 930, Short.MAX_VALUE))
        );
        jPanel21Layout.setVerticalGroup(
            jPanel21Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel21Layout.createSequentialGroup()
                .addGap(14, 14, 14)
                .addComponent(jLabel60, javax.swing.GroupLayout.PREFERRED_SIZE, 52, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(89, 89, 89)
                .addComponent(jLabel66)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel62, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jTextField18, javax.swing.GroupLayout.PREFERRED_SIZE, 38, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel63, javax.swing.GroupLayout.PREFERRED_SIZE, 26, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jTextField19, javax.swing.GroupLayout.PREFERRED_SIZE, 37, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel64, javax.swing.GroupLayout.PREFERRED_SIZE, 26, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPasswordField2, javax.swing.GroupLayout.PREFERRED_SIZE, 44, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel59, javax.swing.GroupLayout.PREFERRED_SIZE, 26, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jPasswordField3, javax.swing.GroupLayout.PREFERRED_SIZE, 39, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jButton5, javax.swing.GroupLayout.PREFERRED_SIZE, 38, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(92, 92, 92)
                .addGroup(jPanel21Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel61)
                    .addComponent(jButton6))
                .addContainerGap(190, Short.MAX_VALUE))
        );

        MainPanel.addTab("signup", jPanel21);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(MainPanel)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(MainPanel)
        );

        getAccessibleContext().setAccessibleDescription("");

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jTextField2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jTextField2ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jTextField2ActionPerformed

    private void BtnPrecendentActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BtnPrecendentActionPerformed
            
    }//GEN-LAST:event_BtnPrecendentActionPerformed
//  ajout de la famille
    
    private void BntSuivantActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BntSuivantActionPerformed
        try {
        String nom = jTextField1.getText();
        String adresse = jTextField2.getText();
        String telephone = jTextField3.getText();
        
        if (nom.isEmpty() || adresse.isEmpty() || telephone.isEmpty()) {
                JOptionPane.showMessageDialog(null, "Tous les champs obligatoires doivent être remplis !", "Erreur", JOptionPane.ERROR_MESSAGE);
                return; // Arrête l'exécution si validation échoue
        }
        conn = DBconnection.connect();
        String sql = "INSERT INTO famille (nom, adresse, telephone) VALUES (?, ?, ?)";
        pst = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
        pst.setString(1, nom);
        pst.setString(2, adresse);
        pst.setString(3, telephone);

        pst.executeUpdate();

        rs = pst.getGeneratedKeys();
        if (rs.next()) {
            idFamilleActuelle = rs.getInt(1); 
            System.out.println("Famille ID : " + idFamilleActuelle);
        }

        conn.close();
        MainPanel.setSelectedIndex(2); // Aller à l'étape suivante : Défunt
    } catch (Exception e) {
        JOptionPane.showMessageDialog(null, "Erreur d'enregistrement Famille : " + e.getMessage());
    }
        
    }//GEN-LAST:event_BntSuivantActionPerformed

    private void jTextField5ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jTextField5ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jTextField5ActionPerformed

    private void DateActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_DateActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_DateActionPerformed

    private void BtnPrecendent1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BtnPrecendent1ActionPerformed
        // TODO add your handling code here:
        MainPanel.setSelectedIndex(2); // Onglet précédent : Famille
    }//GEN-LAST:event_BtnPrecendent1ActionPerformed

    private void BntSuivant1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BntSuivant1ActionPerformed
      try {
        // 1. Récupération des données depuis le formulaire
        String dateStr = Date.getText();    // Format attendu : "dd/MM/yyyy"
        String heureStr = Heure.getText();  // Format attendu : "HH:mm"
        String lieu = Lieu.getText();
        String type = Type.getText();
        
        if (dateStr.isEmpty() || heureStr.isEmpty() || lieu.isEmpty() || type.isEmpty()){
                JOptionPane.showMessageDialog(null, "Tous les champs obligatoires doivent être remplis !", "Erreur", JOptionPane.ERROR_MESSAGE);
                return; // Arrête l'exécution si validation échoue
            }
        
        // 2. Formatage de la date et de l'heure
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        LocalDate dateCeremonie = LocalDate.parse(dateStr, dateFormatter);

        LocalTime heureCeremonie = LocalTime.parse(heureStr); // format HH:mm, ex: "14:30"

        // 3. Connexion à la base
        conn = DBconnection.connect();

        // 4. Préparer l'insertion dans la table ceremonie
        String sql = "INSERT INTO ceremonie (id_defunt, date_ceremonie, heure_ceremonie, lieu_ceremonie, type_ceremonie) VALUES (?, ?, ?, ?, ?)";
        pst = conn.prepareStatement(sql);
        pst.setInt(1, idDefuntActuel); // Doit venir de l'étape précédente
        pst.setDate(2, java.sql.Date.valueOf(dateCeremonie));
        pst.setTime(3, java.sql.Time.valueOf(heureCeremonie));
        pst.setString(4, lieu);
        pst.setString(5, type);

        // 5. Exécuter
        pst.executeUpdate();

        // 6. Fermer connexion
        conn.close();

        // 7. Affichage ou fin de formulaire
        JOptionPane.showMessageDialog(null, "Cérémonie enregistrée avec succès !");
        MainPanel.setSelectedIndex(4); 

    } catch (Exception e) {
        JOptionPane.showMessageDialog(null, "Erreur cérémonie : " + e.getMessage());
    }
    }//GEN-LAST:event_BntSuivant1ActionPerformed

    private void BntSuivant2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BntSuivant2ActionPerformed

        try {
        String nom = jTextField4.getText();
        String prenom = jTextField5.getText();
        String dateNaissanceStr = jTextField6.getText();
        String dateDecesStr = jTextField7.getText();
        String lieu = jTextField8.getText();

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        LocalDate dateNaissance = LocalDate.parse(dateNaissanceStr, formatter);
        LocalDate dateDeces = LocalDate.parse(dateDecesStr, formatter);
        
        if(nom.isEmpty() || prenom.isEmpty() || dateNaissanceStr.isEmpty() || dateDecesStr.isEmpty() || lieu.isEmpty()){
             JOptionPane.showMessageDialog(null, "Tous les champs obligatoires doivent être remplis !", "Erreur", JOptionPane.ERROR_MESSAGE);
             return;
        }

        conn = DBconnection.connect();

        String sql = "INSERT INTO defunt(nom, prenom, date_naissance, date_deces, lieu_deces, id_famille) VALUES (?, ?, ?, ?, ?, ?)";
        pst = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
        pst.setString(1, nom);
        pst.setString(2, prenom);
        pst.setDate(3, java.sql.Date.valueOf(dateNaissance));
        pst.setDate(4, java.sql.Date.valueOf(dateDeces));
        pst.setString(5, lieu);
        pst.setInt(6, idFamilleActuelle);

        pst.executeUpdate();

        rs = pst.getGeneratedKeys();
        if (rs.next()) {
            idDefuntActuel = rs.getInt(1); // À stocker pour la cérémonie
        }

        conn.close();
        
        MainPanel.setSelectedIndex(3); // Aller à la cérémonie
        

    } catch (Exception e) {
        JOptionPane.showMessageDialog(null, "Erreur défunt : " + e.getMessage());
    }
    }//GEN-LAST:event_BntSuivant2ActionPerformed

    private void BtnPrecendent2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BtnPrecendent2ActionPerformed
      MainPanel.setSelectedIndex(1);
    }//GEN-LAST:event_BtnPrecendent2ActionPerformed

    private void AjouterPanelLouncherActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_AjouterPanelLouncherActionPerformed
        // TODO add your handling code here:
        MainPanel.setSelectedIndex(1);
    }//GEN-LAST:event_AjouterPanelLouncherActionPerformed

    private void AjoutPersonnelPanelActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_AjoutPersonnelPanelActionPerformed
        // TODO add your handling code here:
        MainPanel.setSelectedIndex(5);
    }//GEN-LAST:event_AjoutPersonnelPanelActionPerformed

    private void AjouterPanelLouncher1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_AjouterPanelLouncher1ActionPerformed
        // TODO add your handling code here:
        MainPanel.setSelectedIndex(1);
    }//GEN-LAST:event_AjouterPanelLouncher1ActionPerformed

    private void AjoutPersonnelPanel1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_AjoutPersonnelPanel1ActionPerformed
        // TODO add your handling code here:
        MainPanel.setSelectedIndex(5);
    }//GEN-LAST:event_AjoutPersonnelPanel1ActionPerformed

    private void AjouterPanelLouncher10ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_AjouterPanelLouncher10ActionPerformed
        // TODO add your handling code here:
        MainPanel.setSelectedIndex(1);
    }//GEN-LAST:event_AjouterPanelLouncher10ActionPerformed

    private void AjoutPersonnelPanel2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_AjoutPersonnelPanel2ActionPerformed
        // TODO add your handling code here:
        MainPanel.setSelectedIndex(5);
    }//GEN-LAST:event_AjoutPersonnelPanel2ActionPerformed

    private void AjouterPanelLouncher11ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_AjouterPanelLouncher11ActionPerformed
        // TODO add your handling code here:
        MainPanel.setSelectedIndex(1);
    }//GEN-LAST:event_AjouterPanelLouncher11ActionPerformed

    private void AjoutPersonnelPanel3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_AjoutPersonnelPanel3ActionPerformed
        // TODO add your handling code here:
        MainPanel.setSelectedIndex(5);
    }//GEN-LAST:event_AjoutPersonnelPanel3ActionPerformed

    private void AjouterPanelLouncher12ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_AjouterPanelLouncher12ActionPerformed
        // TODO add your handling code here:
        MainPanel.setSelectedIndex(1);
    }//GEN-LAST:event_AjouterPanelLouncher12ActionPerformed

    private void AjoutPersonnelPanel4ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_AjoutPersonnelPanel4ActionPerformed
        // TODO add your handling code here:
        MainPanel.setSelectedIndex(5);
    }//GEN-LAST:event_AjoutPersonnelPanel4ActionPerformed

    private void AjouterPanelLouncher13ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_AjouterPanelLouncher13ActionPerformed
        // TODO add your handling code here:
        MainPanel.setSelectedIndex(1);
    }//GEN-LAST:event_AjouterPanelLouncher13ActionPerformed

    private void AjoutPersonnelPanel5ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_AjoutPersonnelPanel5ActionPerformed
        // TODO add your handling code here:
        MainPanel.setSelectedIndex(5);
    }//GEN-LAST:event_AjoutPersonnelPanel5ActionPerformed

    private void AjoutPersonnelPanel6ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_AjoutPersonnelPanel6ActionPerformed
        // TODO add your handling code here:
        MainPanel.setSelectedIndex(6);
    }//GEN-LAST:event_AjoutPersonnelPanel6ActionPerformed

    private void AjouterPanelLouncher14ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_AjouterPanelLouncher14ActionPerformed
        // TODO add your handling code here:
        MainPanel.setSelectedIndex(1);
    }//GEN-LAST:event_AjouterPanelLouncher14ActionPerformed

    private void AjoutPersonnelPanel7ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_AjoutPersonnelPanel7ActionPerformed
        // TODO add your handling code here:
        MainPanel.setSelectedIndex(5);
    }//GEN-LAST:event_AjoutPersonnelPanel7ActionPerformed

    private void AjoutPersonnelPanel8ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_AjoutPersonnelPanel8ActionPerformed
        // TODO add your handling code here:
        MainPanel.setSelectedIndex(6);
    }//GEN-LAST:event_AjoutPersonnelPanel8ActionPerformed

    private void jButton3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton3ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jButton3ActionPerformed

    private void jButton4ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton4ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jButton4ActionPerformed

    private void jTextField17ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jTextField17ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jTextField17ActionPerformed

    private void jButton5ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton5ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jButton5ActionPerformed

    private void jButton6ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton6ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jButton6ActionPerformed

    private void jTextField19ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jTextField19ActionPerformed
        // TODO add your handling code here:

    }//GEN-LAST:event_jTextField19ActionPerformed

    private void jButton15ActionPerformed(java.awt.event.ActionEvent evt) {
        int selectedRow = jTableMateriel.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Veuillez sélectionner un matériel à modifier", "Information", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        Materiel materiel = listeMateriels.get(selectedRow);
        MaterielDialog dialog = new MaterielDialog(this, true);
        dialog.setMateriel(materiel);
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
        chargerMateriels(); // Rafraîchir la liste après modification
    }

    private void jToggleButton3ActionPerformed(java.awt.event.ActionEvent evt) {
        MaterielDialog dialog = new MaterielDialog(this, true);
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
        chargerMateriels() ;
    }

    private void jButton16ActionPerformed(java.awt.event.ActionEvent evt) {
        int selectedRow = PersonnelTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Veuillez sélectionner un personnel à modifier", "Information", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        Personnel personnel = listePersonnel.get(selectedRow);
        PersonnelDialog dialog = new PersonnelDialog(this, true);
        dialog.setPersonnel(personnel);
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
        chargerPersonnels(); // Rafraîchir la liste après modification
    }

    private void AjoutPersonnelPanel10ActionPerformed(java.awt.event.ActionEvent evt) {
        // TODO add your handling code here:
        MainPanel.setSelectedIndex(6);
    }

    private void AjoutPersonnelPanel11ActionPerformed(java.awt.event.ActionEvent evt) {
        // TODO add your handling code here:
        MainPanel.setSelectedIndex(6);
    }

    private void AjoutPersonnelPanel12ActionPerformed(java.awt.event.ActionEvent evt) {
        // TODO add your handling code here:
        MainPanel.setSelectedIndex(6);
    }

    private void AjoutPersonnelPanel13ActionPerformed(java.awt.event.ActionEvent evt) {
        // TODO add your handling code here:
    }

    private void AjoutPersonnelPanel14ActionPerformed(java.awt.event.ActionEvent evt) {
        MainPanel.setSelectedIndex(6);
    }

    private void jButton14ActionPerformed(java.awt.event.ActionEvent evt) {
        // TODO add your handling code here:
        
    }

    private void jButton19ActionPerformed(java.awt.event.ActionEvent evt) {
        PersonnelDialog dialog = new PersonnelDialog(this, true);
        dialog.setModificationMode(false);
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
        if (dialog.isConfirmed()) {
            chargerPersonnels();
        }
    }

    private void jButton18ActionPerformed(java.awt.event.ActionEvent evt) {
        int selectedRow = PersonnelTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Veuillez sélectionner un personnel à modifier", "Information", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        Personnel personnel = listePersonnel.get(selectedRow);
        PersonnelDialog dialog = new PersonnelDialog(this, true);
        dialog.setPersonnel(personnel);
        dialog.setModificationMode(true);
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
        chargerPersonnels();
    }

    private void jToggleButton2ActionPerformed(java.awt.event.ActionEvent evt) {
        MaterielDialog modifMat = new MaterielDialog(this , true);
        modifMat.setLocationRelativeTo(this);
        modifMat.setVisible(true);
    }

    private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) {
        // TODO add your handling code here:
    }

    private void jButton8ActionPerformed(java.awt.event.ActionEvent evt) {
        MainPanel.setSelectedIndex(0);
    }

    private void jButton9ActionPerformed(java.awt.event.ActionEvent evt) {
        // TODO add your handling code here:
        MainPanel.setSelectedIndex(0);
    }

    private void jButton10ActionPerformed(java.awt.event.ActionEvent evt) {
        // TODO add your handling code here:
        MainPanel.setSelectedIndex(0);
    }

    private void jButton12ActionPerformed(java.awt.event.ActionEvent evt) {
        // TODO add your handling code here:
        MainPanel.setSelectedIndex(0);
    }

    private void jButton11ActionPerformed(java.awt.event.ActionEvent evt) {
        // TODO add your handling code here:
        MainPanel.setSelectedIndex(0);
    }

    private void jButton13ActionPerformed(java.awt.event.ActionEvent evt) {
        // TODO add your handling code here:
        MainPanel.setSelectedIndex(0);
    }

    private void jButton7ActionPerformed(java.awt.event.ActionEvent evt) {
     double total = 0;

    for (int i = 0; i < jTableMateriel1.getRowCount(); i++) {
        boolean isChecked = (Boolean) jTableMateriel1.getValueAt(i, 0);
        if (isChecked) {
            // Il faut retransformer le prix formaté s'il est affiché comme texte, sinon utiliser directement la liste
            double prix = listeMateriels.get(i).getPrix(); 
            total += prix;
        }
    }

    Total.setText(total + "FBu");
    }

    private void jButton20ActionPerformed(java.awt.event.ActionEvent evt) {
        try {
            // Mise à jour du nombre total de personnel
            int totalPersonnel = PersonnelManager.getAllPersonnels().size();
            jLabel45.setText(String.valueOf(totalPersonnel));

            // Mise à jour du nombre total de matériel
            int totalMateriel = MaterielManager.getAllMateriels().size();
            jLabel46.setText(String.valueOf(totalMateriel));

            // Mise à jour du nombre de matériel en stock
            int materielEnStock = MaterielManager.getAvailableMateriels().size();
            jLabel47.setText(String.valueOf(materielEnStock));

            // Mise à jour du nombre de cérémonies du jour
            LocalDate today = LocalDate.now();
            try{
                conn = DBconnection.connect();
                String sql = "SELECT COUNT(*) FROM ceremonie WHERE DATE(date_ceremonie) = ?";
                PreparedStatement pst = conn.prepareStatement(sql);
                pst.setDate(1, java.sql.Date.valueOf(today));
                ResultSet rs = pst.executeQuery();
                if (rs.next()) {
                    jLabel48.setText(String.valueOf(rs.getInt(1)));
                }
            }catch(Exception e){
                e.printStackTrace();
            }

            // Mise à jour des barres de progression
            // Matériel en stock
            int stockPercentage = totalMateriel > 0 ? (materielEnStock * 100) / totalMateriel : 0;
            jProgressBar1.setValue(stockPercentage);

            // Personnel actif
            try{
                conn = DBconnection.connect();
                String sql = "SELECT COUNT(*) FROM employe WHERE statut = 'actif'";
                Statement st = conn.createStatement();
                ResultSet rs = st.executeQuery(sql);
                if (rs.next()) {
                    int personnelActif = rs.getInt(1);
                    int personnelPercentage = totalPersonnel > 0 ? (personnelActif * 100) / totalPersonnel : 0;
                    jProgressBar4.setValue(personnelPercentage);
                }
            }catch(Exception e){
                e.printStackTrace();
            }

            // Cérémonies du jour
            try {
                conn = DBconnection.connect();
                String sql = "SELECT COUNT(*) FROM ceremonie WHERE DATE(date_ceremonie) = ?";
                pst = conn.prepareStatement(sql);
                pst.setDate(1, java.sql.Date.valueOf(today));
                rs = pst.executeQuery();
                if (rs.next()) {
                    int ceremoniesToday = rs.getInt(1);
                    int ceremoniesPercentage = Math.min(100, (ceremoniesToday * 100) / 10);
                    jProgressBar5.setValue(ceremoniesPercentage);
                }
            }catch(Exception e){
                e.printStackTrace();
            }

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Erreur lors de la mise à jour du dashboard : " + e.getMessage(), 
                "Erreur", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }

    private void jButton22ActionPerformed(java.awt.event.ActionEvent evt) {
   int idCeremonie = 1; // récupéré depuis la sélection de l'utilisateur
    FactureData info = FactureDAO.getFactureDataByCeremonieId(idCeremonie);

   List<Materiel> materielsChoisis = new ArrayList<>();

    for (int i = 0; i < jTableMateriel1.getRowCount(); i++) {
        boolean isChecked = (Boolean) jTableMateriel1.getValueAt(i, 0);
        if (isChecked) {
            materielsChoisis.add(listeMateriels.get(i));
        }
    }
    double total = 0;
    for (Materiel m : materielsChoisis) {
        total += m.getPrix();
    }


    FacturePDF.genererFacturePDF(info, materielsChoisis, total);


    }

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {
//        try {
//            // Mise à jour du nombre total de personnel
//            int totalPersonnel = PersonnelManager.getAllPersonnels().size();
//            jLabel45.setText(String.valueOf(totalPersonnel));
//            jLabel45.setFont(new java.awt.Font("Cantarell", 1, 24)); // Police en gras
//            jLabel45.setForeground(new java.awt.Color(51, 51, 51)); // Couleur gris foncé
//
//            // Mise à jour du nombre total de matériel
//            int totalMateriel = MaterielManager.getAllMateriels().size();
//            jLabel46.setText(String.valueOf(totalMateriel));
//            jLabel46.setFont(new java.awt.Font("Cantarell", 1, 24));
//            jLabel46.setForeground(new java.awt.Color(51, 51, 51));
//
//            // Mise à jour du nombre de matériel en stock
//            int materielEnStock = MaterielManager.getAvailableMateriels().size();
//            jLabel47.setText(String.valueOf(materielEnStock));
//            jLabel47.setFont(new java.awt.Font("Cantarell", 1, 24));
//            jLabel47.setForeground(new java.awt.Color(51, 51, 51));
//
//            // Mise à jour du nombre de cérémonies du jour
//            LocalDate today = LocalDate.now();
//            try (Connection conn = DBconnection.connect()) {
//                String sql = "SELECT COUNT(*) FROM ceremonie WHERE DATE(date_ceremonie) = ?";
//                PreparedStatement pst = conn.prepareStatement(sql);
//                pst.setDate(1, java.sql.Date.valueOf(today));
//                ResultSet rs = pst.executeQuery();
//                if (rs.next()) {
//                    jLabel48.setText(String.valueOf(rs.getInt(1)));
//                    jLabel48.setFont(new java.awt.Font("Cantarell", 1, 24));
//                    jLabel48.setForeground(new java.awt.Color(51, 51, 51));
//                }
//            }
//
//            // Mise à jour des barres de progression avec des couleurs
//            // Matériel en stock
//            int stockPercentage = totalMateriel > 0 ? (materielEnStock * 100) / totalMateriel : 0;
//            jProgressBar1.setValue(stockPercentage);
//            jProgressBar1.setForeground(new java.awt.Color(46, 204, 113)); // Vert
//            jProgressBar1.setStringPainted(true);
//            jProgressBar1.setString(stockPercentage + "%");
//
//            // Personnel actif
//            try (Connection conn = DBconnection.connect()) {
//                String sql = "SELECT COUNT(*) FROM employe WHERE statut = 'actif'";
//                Statement st = conn.createStatement();
//                ResultSet rs = st.executeQuery(sql);
//                if (rs.next()) {
//                    int personnelActif = rs.getInt(1);
//                    int personnelPercentage = totalPersonnel > 0 ? (personnelActif * 100) / totalPersonnel : 0;
//                    jProgressBar4.setValue(personnelPercentage);
//                    jProgressBar4.setForeground(new java.awt.Color(52, 152, 219)); // Bleu
//                    jProgressBar4.setStringPainted(true);
//                    jProgressBar4.setString(personnelPercentage + "%");
//                }
//            }
//
//            // Cérémonies du jour
//            try (Connection conn = DBconnection.connect()) {
//                String sql = "SELECT COUNT(*) FROM ceremonie WHERE DATE(date_ceremonie) = ?";
//                PreparedStatement pst = conn.prepareStatement(sql);
//                pst.setDate(1, java.sql.Date.valueOf(today));
//                ResultSet rs = pst.executeQuery();
//                if (rs.next()) {
//                    int ceremoniesToday = rs.getInt(1);
//                    int ceremoniesPercentage = Math.min(100, (ceremoniesToday * 100) / 10);
//                    jProgressBar5.setValue(ceremoniesPercentage);
//                    jProgressBar5.setForeground(new java.awt.Color(155, 89, 182)); // Violet
//                    jProgressBar5.setStringPainted(true);
//                    jProgressBar5.setString(ceremoniesPercentage + "%");
//                }
//            }
//
//            // Mise à jour de la date et heure
//            DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
//            DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm");
//            jLabel49.setText(today.format(dateFormatter));
//            jLabel50.setText(LocalTime.now().format(timeFormatter));
//
//        } catch (Exception e) {
//            JOptionPane.showMessageDialog(this, "Erreur lors de la mise à jour du dashboard : " + e.getMessage(), 
//                "Erreur", JOptionPane.ERROR_MESSAGE);
//            e.printStackTrace();
//        }
    }

    private void showPersonnelDialog(Personnel personnel, boolean isModification) {
        PersonnelDialog dialog = new PersonnelDialog(this, true);
        dialog.setPersonnel(personnel);
        dialog.setModificationMode(isModification);
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
        
        if (dialog.isConfirmed()) {
            chargerPersonnels();
        }
    }

    private void showMaterielDialog(Materiel materiel, boolean isModification) {
        MaterielDialog dialog = new MaterielDialog(this, true);
        dialog.setMateriel(materiel);
        dialog.setModificationMode(isModification);
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
        
        if (dialog.isConfirmed()) {
            chargerMateriels();
        }
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(menu.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(menu.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(menu.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(menu.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new menu().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JDialog AjoutMateriel;
    private javax.swing.JButton AjoutPersonnelPanel;
    private javax.swing.JButton AjoutPersonnelPanel1;
    private javax.swing.JButton AjoutPersonnelPanel10;
    private javax.swing.JButton AjoutPersonnelPanel11;
    private javax.swing.JButton AjoutPersonnelPanel12;
    private javax.swing.JButton AjoutPersonnelPanel13;
    private javax.swing.JButton AjoutPersonnelPanel14;
    private javax.swing.JButton AjoutPersonnelPanel2;
    private javax.swing.JButton AjoutPersonnelPanel3;
    private javax.swing.JButton AjoutPersonnelPanel4;
    private javax.swing.JButton AjoutPersonnelPanel5;
    private javax.swing.JButton AjoutPersonnelPanel6;
    private javax.swing.JButton AjoutPersonnelPanel7;
    private javax.swing.JButton AjoutPersonnelPanel8;
    private javax.swing.JButton AjouterPanelLouncher;
    private javax.swing.JButton AjouterPanelLouncher1;
    private javax.swing.JButton AjouterPanelLouncher10;
    private javax.swing.JButton AjouterPanelLouncher11;
    private javax.swing.JButton AjouterPanelLouncher12;
    private javax.swing.JButton AjouterPanelLouncher13;
    private javax.swing.JButton AjouterPanelLouncher14;
    private javax.swing.JDialog AjouterPersonnel;
    private javax.swing.JButton BntSuivant;
    private javax.swing.JButton BntSuivant1;
    private javax.swing.JButton BntSuivant2;
    private javax.swing.JButton BtnPrecendent;
    private javax.swing.JButton BtnPrecendent1;
    private javax.swing.JButton BtnPrecendent2;
    private javax.swing.JTextField Date;
    private javax.swing.JTextField Heure;
    private javax.swing.JTextField Lieu;
    private javax.swing.JTabbedPane MainPanel;
    private javax.swing.JPanel Materiel;
    private javax.swing.JDialog Modifier;
    private javax.swing.JPanel PanelCeremonie;
    private javax.swing.JPanel PanelDashboard;
    private javax.swing.JPanel PanelDefunt;
    private javax.swing.JPanel PanelFamille;
    private javax.swing.JTable PersonnelTable;
    private javax.swing.JDialog Supprimer;
    private javax.swing.JLabel Total;
    private javax.swing.JTextField Type;
    private javax.swing.JButton jButton10;
    private javax.swing.JButton jButton11;
    private javax.swing.JButton jButton12;
    private javax.swing.JButton jButton13;
    private javax.swing.JButton jButton14;
    private javax.swing.JButton jButton15;
    private javax.swing.JButton jButton16;
    private javax.swing.JButton jButton17;
    private javax.swing.JButton jButton18;
    private javax.swing.JButton jButton19;
    private javax.swing.JButton jButton2;
    private javax.swing.JButton jButton20;
    private javax.swing.JButton jButton21;
    private javax.swing.JButton jButton22;
    private javax.swing.JButton jButton3;
    private javax.swing.JButton jButton4;
    private javax.swing.JButton jButton5;
    private javax.swing.JButton jButton6;
    private javax.swing.JButton jButton7;
    private javax.swing.JButton jButton8;
    private javax.swing.JButton jButton9;
    private javax.swing.JComboBox<String> jComboBox5;
    private javax.swing.JComboBox<String> jComboBox7;
    private javax.swing.JComboBox<String> jComboBox8;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel15;
    private javax.swing.JLabel jLabel16;
    private javax.swing.JLabel jLabel17;
    private javax.swing.JLabel jLabel18;
    private javax.swing.JLabel jLabel19;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel20;
    private javax.swing.JLabel jLabel21;
    private javax.swing.JLabel jLabel22;
    private javax.swing.JLabel jLabel25;
    private javax.swing.JLabel jLabel26;
    private javax.swing.JLabel jLabel27;
    private javax.swing.JLabel jLabel28;
    private javax.swing.JLabel jLabel29;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel30;
    private javax.swing.JLabel jLabel31;
    private javax.swing.JLabel jLabel32;
    private javax.swing.JLabel jLabel33;
    private javax.swing.JLabel jLabel34;
    private javax.swing.JLabel jLabel35;
    private javax.swing.JLabel jLabel36;
    private javax.swing.JLabel jLabel37;
    private javax.swing.JLabel jLabel38;
    private javax.swing.JLabel jLabel39;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel40;
    private javax.swing.JLabel jLabel45;
    private javax.swing.JLabel jLabel46;
    private javax.swing.JLabel jLabel47;
    private javax.swing.JLabel jLabel48;
    private javax.swing.JLabel jLabel49;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel50;
    private javax.swing.JLabel jLabel51;
    private javax.swing.JLabel jLabel52;
    private javax.swing.JLabel jLabel53;
    private javax.swing.JLabel jLabel54;
    private javax.swing.JLabel jLabel55;
    private javax.swing.JLabel jLabel56;
    private javax.swing.JLabel jLabel57;
    private javax.swing.JLabel jLabel58;
    private javax.swing.JLabel jLabel59;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel60;
    private javax.swing.JLabel jLabel61;
    private javax.swing.JLabel jLabel62;
    private javax.swing.JLabel jLabel63;
    private javax.swing.JLabel jLabel64;
    private javax.swing.JLabel jLabel65;
    private javax.swing.JLabel jLabel66;
    private javax.swing.JLabel jLabel67;
    private javax.swing.JLabel jLabel68;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel70;
    private javax.swing.JLabel jLabel71;
    private javax.swing.JLabel jLabel72;
    private javax.swing.JLabel jLabel73;
    private javax.swing.JLabel jLabel74;
    private javax.swing.JLabel jLabel75;
    private javax.swing.JLabel jLabel76;
    private javax.swing.JLabel jLabel77;
    private javax.swing.JLabel jLabel78;
    private javax.swing.JLabel jLabel79;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel10;
    private javax.swing.JPanel jPanel11;
    private javax.swing.JPanel jPanel12;
    private javax.swing.JPanel jPanel13;
    private javax.swing.JPanel jPanel14;
    private javax.swing.JPanel jPanel15;
    private javax.swing.JPanel jPanel16;
    private javax.swing.JPanel jPanel17;
    private javax.swing.JPanel jPanel18;
    private javax.swing.JPanel jPanel19;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel20;
    private javax.swing.JPanel jPanel21;
    private javax.swing.JPanel jPanel22;
    private javax.swing.JPanel jPanel23;
    private javax.swing.JPanel jPanel24;
    private javax.swing.JPanel jPanel25;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel6;
    private javax.swing.JPanel jPanel7;
    private javax.swing.JPanel jPanel8;
    private javax.swing.JPanel jPanel9;
    private javax.swing.JPasswordField jPasswordField1;
    private javax.swing.JPasswordField jPasswordField2;
    private javax.swing.JPasswordField jPasswordField3;
    private javax.swing.JProgressBar jProgressBar1;
    private javax.swing.JProgressBar jProgressBar2;
    private javax.swing.JProgressBar jProgressBar4;
    private javax.swing.JProgressBar jProgressBar5;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JSeparator jSeparator2;
    private javax.swing.JSeparator jSeparator3;
    private javax.swing.JSeparator jSeparator4;
    private javax.swing.JSeparator jSeparator5;
    private javax.swing.JSeparator jSeparator6;
    private javax.swing.JSeparator jSeparator7;
    private javax.swing.JSeparator jSeparator8;
    private javax.swing.JSeparator jSeparator9;
    private javax.swing.JTable jTableMateriel;
    private javax.swing.JTable jTableMateriel1;
    private javax.swing.JTextField jTextField1;
    private javax.swing.JTextField jTextField15;
    private javax.swing.JTextField jTextField17;
    private javax.swing.JTextField jTextField18;
    private javax.swing.JTextField jTextField19;
    private javax.swing.JTextField jTextField2;
    private javax.swing.JTextField jTextField20;
    private javax.swing.JTextField jTextField3;
    private javax.swing.JTextField jTextField4;
    private javax.swing.JTextField jTextField5;
    private javax.swing.JTextField jTextField6;
    private javax.swing.JTextField jTextField7;
    private javax.swing.JTextField jTextField8;
    private javax.swing.JToggleButton jToggleButton1;
    private javax.swing.JToggleButton jToggleButton2;
    private javax.swing.JToggleButton jToggleButton3;
    private javax.swing.JTextField tfNom1;
    private javax.swing.JTextField tfNom2;
    private javax.swing.JTextField tfPrix1;
    private javax.swing.JTextField tfPrix2;
    private javax.swing.JComboBox<String> tfType1;
    private javax.swing.JComboBox<String> tfType2;
    // End of variables declaration//GEN-END:variables
}

