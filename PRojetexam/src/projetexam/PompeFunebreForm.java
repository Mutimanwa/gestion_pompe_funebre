package projetexam;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class PompeFunebreForm extends JFrame {

    private JPanel mainPanel;
    private CardLayout cardLayout;
    private JButton btnSuivant, btnPrecedent;
    private int currentStep = 0; // 0: Famille, 1: Défunt, 2: Cérémonie

    public PompeFunebreForm() {
        setTitle("Gestion Pompe Funèbre - Ajout Guidé");
        setSize(500, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        cardLayout = new CardLayout();
        mainPanel = new JPanel(cardLayout);

        // Étape 1: Famille
        JPanel panelFamille = new JPanel();
        panelFamille.setLayout(new GridLayout(4, 2));
        panelFamille.add(new JLabel("Nom Famille :"));
        panelFamille.add(new JTextField());
        panelFamille.add(new JLabel("Adresse :"));
        panelFamille.add(new JTextField());
        panelFamille.add(new JLabel("Téléphone :"));
        panelFamille.add(new JTextField());

        // Étape 2: Défunt
        JPanel panelDefunt = new JPanel();
        panelDefunt.setLayout(new GridLayout(5, 2));
        panelDefunt.add(new JLabel("Nom Défunt :"));
        panelDefunt.add(new JTextField());
        panelDefunt.add(new JLabel("Prénom Défunt :"));
        panelDefunt.add(new JTextField());
        panelDefunt.add(new JLabel("Date Naissance :"));
        panelDefunt.add(new JTextField());
        panelDefunt.add(new JLabel("Date Décès :"));
        panelDefunt.add(new JTextField());
        panelDefunt.add(new JLabel("Lieu Décès :"));
        panelDefunt.add(new JTextField());

        // Étape 3: Cérémonie
        JPanel panelCeremonie = new JPanel();
        panelCeremonie.setLayout(new GridLayout(4, 2));
        panelCeremonie.add(new JLabel("Date Cérémonie :"));
        panelCeremonie.add(new JTextField());
        panelCeremonie.add(new JLabel("Heure Cérémonie :"));
        panelCeremonie.add(new JTextField());
        panelCeremonie.add(new JLabel("Lieu Cérémonie :"));
        panelCeremonie.add(new JTextField());
        panelCeremonie.add(new JLabel("Type Cérémonie :"));
        panelCeremonie.add(new JTextField());

        // Ajouter les panels au CardLayout
        mainPanel.add(panelFamille, "famille");
        mainPanel.add(panelDefunt, "defunt");
        mainPanel.add(panelCeremonie, "ceremonie");

        // Boutons de navigation
        btnPrecedent = new JButton("Précédent");
        btnSuivant = new JButton("Suivant");

        JPanel navigationPanel = new JPanel();
        navigationPanel.add(btnPrecedent);
        navigationPanel.add(btnSuivant);

        // Ajouter tout dans la fenêtre
        add(mainPanel, BorderLayout.CENTER);
        add(navigationPanel, BorderLayout.SOUTH);

        // Actions des boutons
        btnPrecedent.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if (currentStep > 0) {
                    currentStep--;
                    updateStep();
                }
            }
        });

        btnSuivant.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if (currentStep < 2) {
                    currentStep++;
                    updateStep();
                } else {
                    JOptionPane.showMessageDialog(null, "Formulaire terminé !");
                    // Ici tu pourrais déclencher l’enregistrement en base !
                }
            }
        });

        updateStep();
    }

    private void updateStep() {
        switch (currentStep) {
            case 0:
                cardLayout.show(mainPanel, "famille");
                btnPrecedent.setEnabled(false);
                break;
            case 1:
                cardLayout.show(mainPanel, "defunt");
                btnPrecedent.setEnabled(true);
                btnSuivant.setText("Suivant");
                break;
            case 2:
                cardLayout.show(mainPanel, "ceremonie");
                btnSuivant.setText("Terminer");
                break;
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new PompeFunebreForm().setVisible(true);
        });
    }
}
