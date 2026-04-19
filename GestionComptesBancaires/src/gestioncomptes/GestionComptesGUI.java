/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package gestioncomptes;

import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.*;
import java.util.List;

public class GestionComptesGUI extends JFrame {

    // ── Liste des comptes ──────────────────────────────────────────────────────
    private final List<Compte> comptes = new ArrayList<>();

    // ── Composants ─────────────────────────────────────────────────────────────
    private JTextField txtNumero, txtTitulaire, txtSolde, txtTaux, txtRecherche, txtMontant;
    private JCheckBox  chkEpargne;
    private JTable     table;
    private DefaultTableModel tableModel;
    private JLabel     lblStatus;

    private static final String FICHIER = "comptes.dat";
    private static final String FICHIER_TEXTE = "comptes.txt";

    // ── Couleurs ────────────────────────────────────────────────────────────────
    private static final Color BLEU_FONCE  = new Color(13, 71, 161);
    private static final Color BLEU_CLAIR  = new Color(21, 101, 192);
    private static final Color VERT        = new Color(27, 94, 32);
    private static final Color ROUGE       = new Color(183, 28, 28);
    private static final Color ORANGE      = new Color(230, 81, 0);
    private static final Color GRIS_FOND   = new Color(236, 239, 241);

    // ══════════════════════════════════════════════════════════════════════════
    public GestionComptesGUI() {
        setTitle("Gestion des Comptes Bancaires");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1000, 680);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(10, 10));
        getContentPane().setBackground(GRIS_FOND);

        add(creerPanneauTitre(),    BorderLayout.NORTH);
        add(creerPanneauGauche(),   BorderLayout.WEST);
        add(creerPanneauCentre(),   BorderLayout.CENTER);
        add(creerPanneauStatus(),   BorderLayout.SOUTH);

        ajouterValidationsTempsReel();
        
        chargerDepuisFichier();
        rafraichirTable(comptes);
        setVisible(true);
    }

    // ── Titre ──────────────────────────────────────────────────────────────────
    private JPanel creerPanneauTitre() {
        JPanel p = new JPanel(new BorderLayout());
        p.setBackground(BLEU_FONCE);
        p.setBorder(BorderFactory.createEmptyBorder(12, 20, 12, 20));

        JLabel titre = new JLabel("Gestion des Comptes Bancaires", JLabel.LEFT);
        titre.setFont(new Font("Segoe UI", Font.BOLD, 22));
        titre.setForeground(Color.WHITE);

        JLabel sous = new JLabel("Application CRUD – Java Swing", JLabel.RIGHT);
        sous.setFont(new Font("Segoe UI", Font.ITALIC, 13));
        sous.setForeground(new Color(187, 222, 251));

        p.add(titre, BorderLayout.WEST);
        p.add(sous,  BorderLayout.EAST);
        return p;
    }

    // ── Panneau gauche : formulaire ────────────────────────────────────────────
    private JPanel creerPanneauGauche() {
        JPanel outer = new JPanel(new BorderLayout());
        outer.setBackground(GRIS_FOND);
        outer.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 5));

        JPanel form = new JPanel(new GridBagLayout());
        form.setBackground(Color.WHITE);
        form.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(BLEU_CLAIR, 1),
                "  Informations du compte  ",
                javax.swing.border.TitledBorder.LEFT,
                javax.swing.border.TitledBorder.TOP,
                new Font("Segoe UI", Font.BOLD, 13), BLEU_FONCE));

        GridBagConstraints gc = new GridBagConstraints();
        gc.insets = new Insets(6, 10, 6, 10);
        gc.fill   = GridBagConstraints.HORIZONTAL;
        gc.anchor = GridBagConstraints.WEST;

        txtNumero    = new JTextField(14);
        txtTitulaire = new JTextField(14);
        txtSolde     = new JTextField(14);
        txtTaux      = new JTextField(14);
        txtTaux.setEnabled(false);
        chkEpargne   = new JCheckBox("Compte Épargne");
        chkEpargne.setBackground(Color.WHITE);
        chkEpargne.addActionListener(e -> txtTaux.setEnabled(chkEpargne.isSelected()));

        ajouterChamp(form, gc, "Numéro :",    txtNumero,    0);
        ajouterChamp(form, gc, "Titulaire :", txtTitulaire, 1);
        ajouterChamp(form, gc, "Solde (MAD):",txtSolde,     2);

        gc.gridx = 0; gc.gridy = 3; gc.gridwidth = 2;
        form.add(chkEpargne, gc);
        gc.gridwidth = 1;

        ajouterChamp(form, gc, "Taux (%) :",  txtTaux,      4);

        // Séparateur opérations
        gc.gridx = 0; gc.gridy = 5; gc.gridwidth = 2;
        JSeparator sep = new JSeparator();
        sep.setForeground(BLEU_CLAIR);
        form.add(sep, gc);
        gc.gridwidth = 1;

        ajouterChamp(form, gc, "Montant :", txtMontant = new JTextField(14), 6);

        // Boutons CRUD
        JPanel btnCRUD = new JPanel(new GridLayout(2, 2, 6, 6));
        btnCRUD.setBackground(Color.WHITE);
        btnCRUD.setBorder(BorderFactory.createEmptyBorder(8, 10, 4, 10));

        btnCRUD.add(creerBouton("Ajouter",     VERT,       e -> ajouterCompte()));
        btnCRUD.add(creerBouton("Modifier",    BLEU_CLAIR, e -> modifierCompte()));
        btnCRUD.add(creerBouton("Supprimer",  ROUGE,      e -> supprimerCompte()));
        btnCRUD.add(creerBouton("Réinitialiser", Color.GRAY, e -> reinitialiser()));

        gc.gridx = 0; gc.gridy = 7; gc.gridwidth = 2;
        form.add(btnCRUD, gc);

        // Boutons opérations bancaires
        JPanel btnOps = new JPanel(new GridLayout(1, 2, 6, 6));
        btnOps.setBackground(Color.WHITE);
        btnOps.setBorder(BorderFactory.createEmptyBorder(4, 10, 10, 10));

        btnOps.add(creerBouton("Déposer",  new Color(0, 121, 107), e -> deposer()));
        btnOps.add(creerBouton("Retirer",  ORANGE,                  e -> retirer()));

        gc.gridy = 8;
        form.add(btnOps, gc);

        outer.add(form, BorderLayout.CENTER);
        return outer;
    }

    private void ajouterChamp(JPanel p, GridBagConstraints gc, String label, JTextField txt, int row) {
        gc.gridx = 0; gc.gridy = row; gc.gridwidth = 1;
        JLabel lbl = new JLabel(label);
        lbl.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        p.add(lbl, gc);
        gc.gridx = 1;
        txt.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        p.add(txt, gc);
    }

    private JButton creerBouton(String texte, Color couleur, ActionListener action) {
        JButton btn = new JButton(texte);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btn.setBackground(couleur);
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.addActionListener(action);
        btn.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) { btn.setBackground(couleur.darker()); }
            public void mouseExited (MouseEvent e) { btn.setBackground(couleur); }
        });
        return btn;
    }

    // ── Panneau centre : recherche + tableau ───────────────────────────────────
    private JPanel creerPanneauCentre() {
        JPanel panel = new JPanel(new BorderLayout(0, 8));
        panel.setBackground(GRIS_FOND);
        panel.setBorder(BorderFactory.createEmptyBorder(10, 5, 10, 10));

        // Barre de recherche + tri + sauvegarde
        JPanel barreTop = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        barreTop.setBackground(GRIS_FOND);

        txtRecherche = new JTextField(16);
        txtRecherche.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        txtRecherche.putClientProperty("JTextField.placeholderText", "Rechercher…");

        JButton btnRecherche = creerBouton("Rechercher", BLEU_CLAIR, e -> rechercher());
        JButton btnTout      = creerBouton("Tous",       Color.DARK_GRAY, e -> rafraichirTable(comptes));
        JButton btnTri       = creerBouton("Trier (solde)", new Color(74, 20, 140), e -> trierParSolde());
        //JButton btnSauv      = creerBouton("Sauvegarder (BIN)", new Color(1, 87, 155), e -> sauvegarder());
        JButton btnSauvTexte = creerBouton("Sauvegarder (TXT)", new Color(46, 125, 50), e -> sauvegarderTexte());
        JButton btnLoadTexte = creerBouton("Charger (TXT)", new Color(245, 124, 0), e -> chargerDepuisFichierTexte());

        barreTop.add(new JLabel("Recherche : "));
        barreTop.add(txtRecherche);
        barreTop.add(btnRecherche);
        barreTop.add(btnTout);
        barreTop.add(Box.createHorizontalStrut(20));
        barreTop.add(btnTri);
        barreTop.add(Box.createHorizontalStrut(10));
//        barreTop.add(btnSauv);
        barreTop.add(btnSauvTexte);
        barreTop.add(btnLoadTexte);

        // Tableau
        String[] colonnes = {"Numéro", "Titulaire", "Solde", "Type", "Taux (%)"};
        tableModel = new DefaultTableModel(colonnes, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };

        table = new JTable(tableModel);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        table.setRowHeight(26);
        table.setGridColor(new Color(207, 216, 220));
        table.setSelectionBackground(new Color(187, 222, 251));
        table.setSelectionForeground(Color.BLACK);

        // En-tête coloré
        JTableHeader header = table.getTableHeader();
        header.setFont(new Font("Segoe UI", Font.BOLD, 13));
        header.setBackground(BLEU_FONCE);
        header.setForeground(Color.WHITE);

        // Alternance lignes
        table.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            public Component getTableCellRendererComponent(JTable t, Object v,
                    boolean sel, boolean foc, int row, int col) {
                super.getTableCellRendererComponent(t, v, sel, foc, row, col);
                if (!sel) setBackground(row % 2 == 0 ? Color.WHITE : new Color(227, 242, 253));
                setHorizontalAlignment(col >= 2 ? JLabel.CENTER : JLabel.LEFT);
                return this;
            }
        });

        // Sélection → remplir formulaire
        table.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) remplirFormulaire();
        });

        JScrollPane scroll = new JScrollPane(table);
        scroll.setBorder(BorderFactory.createLineBorder(new Color(176, 190, 197)));

        panel.add(barreTop, BorderLayout.NORTH);
        panel.add(scroll,   BorderLayout.CENTER);
        return panel;
    }

    // ── Barre de statut ────────────────────────────────────────────────────────
    private JPanel creerPanneauStatus() {
        JPanel p = new JPanel(new BorderLayout());
        p.setBackground(BLEU_FONCE);
        p.setBorder(BorderFactory.createEmptyBorder(5, 15, 5, 15));
        lblStatus = new JLabel("Prêt.");
        lblStatus.setForeground(Color.WHITE);
        lblStatus.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        p.add(lblStatus, BorderLayout.WEST);
        return p;
    }

    // ══════════════════════════════════════════════════════════════════════════
    //  LOGIQUE CRUD
    // ══════════════════════════════════════════════════════════════════════════

    private void ajouterCompte() {
        if (!validerChamps()) return;
        if (numeroExiste(txtNumero.getText().trim())) {
            erreur("Le numéro de compte existe déjà !");
            return;
        }
        Compte c = creerDepuisFormulaire();
        comptes.add(c);
        rafraichirTable(comptes);
        reinitialiser();
        statut("Compte " + c.getNumero() + " ajouté avec succès.");
    }

    private void modifierCompte() {
        int row = table.getSelectedRow();
        if (row < 0) { erreur("Sélectionnez un compte à modifier."); return; }
        if (!validerChamps()) return;

        String num = (String) tableModel.getValueAt(row, 0);
        Compte c = trouverParNumero(num);
        if (c == null) return;

        c.setTitulaire(txtTitulaire.getText().trim());
        c.setSolde(Double.parseDouble(txtSolde.getText().trim()));
        if (c instanceof CompteEpargne && !txtTaux.getText().trim().isEmpty())
            ((CompteEpargne) c).setTauxInteret(Double.parseDouble(txtTaux.getText().trim()));

        rafraichirTable(comptes);
        reinitialiser();
        statut("Compte " + num + " modifié.");
    }

    private void supprimerCompte() {
        int row = table.getSelectedRow();
        if (row < 0) { erreur("Sélectionnez un compte à supprimer."); return; }
        String num = (String) tableModel.getValueAt(row, 0);
        int ok = JOptionPane.showConfirmDialog(this,
                "Supprimer le compte " + num + " ?", "Confirmation",
                JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
        if (ok == JOptionPane.YES_OPTION) {
            comptes.removeIf(c -> c.getNumero().equals(num));
            rafraichirTable(comptes);
            reinitialiser();
            statut("Compte " + num + " supprimé.");
        }
    }

    private void deposer() {
        int row = table.getSelectedRow();
        if (row < 0) { erreur("Sélectionnez un compte."); return; }
        double montant = lireMontant();
        if (montant <= 0) return;
        String num = (String) tableModel.getValueAt(row, 0);
        Compte c = trouverParNumero(num);
        if (c == null) return;
        c.deposer(montant);
        rafraichirTable(comptes);
        statut(String.format("Dépôt de %.2f MAD effectué sur le compte %s. Nouveau solde : %.2f MAD",
                montant, num, c.getSolde()));
    }

    private void retirer() {
        int row = table.getSelectedRow();
        if (row < 0) { erreur("Sélectionnez un compte."); return; }
        double montant = lireMontant();
        if (montant <= 0) return;
        String num = (String) tableModel.getValueAt(row, 0);
        Compte c = trouverParNumero(num);
        if (c == null) return;
        if (!c.retirer(montant)) {
            erreur("Solde insuffisant ! Solde actuel : " + String.format("%.2f", c.getSolde()) + " MAD");
            return;
        }
        rafraichirTable(comptes);
        statut(String.format("Retrait de %.2f MAD effectué. Nouveau solde : %.2f MAD", montant, c.getSolde()));
    }

    private void rechercher() {
        String terme = txtRecherche.getText().trim().toLowerCase();
        if (terme.isEmpty()) { rafraichirTable(comptes); return; }
        List<Compte> resultats = new ArrayList<>();
        for (Compte c : comptes) {
            if (c.getNumero().toLowerCase().contains(terme) ||
                c.getTitulaire().toLowerCase().contains(terme))
                resultats.add(c);
        }
        rafraichirTable(resultats);
        statut(resultats.size() + " résultat(s) trouvé(s).");
    }

    private void trierParSolde() {
        comptes.sort(Comparator.comparingDouble(Compte::getSolde));
        rafraichirTable(comptes);
        statut("Comptes triés par solde croissant.");
    }

    // ── Sauvegarde / Chargement ────────────────────────────────────────────────
    private void sauvegarder() {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(FICHIER))) {
            oos.writeObject(new ArrayList<>(comptes));
            statut("Données sauvegardées dans " + FICHIER);
        } catch (IOException ex) {
            erreur("Erreur de sauvegarde : " + ex.getMessage());
        }
    }
    
    // Nouvelle méthode pour sauvegarder en format texte
    private void sauvegarderTexte() {
        try (PrintWriter writer = new PrintWriter(new FileWriter(FICHIER_TEXTE))) {
            // Écrire l'en-tête
            writer.println("=== LISTE DES COMPTES BANCAIRES ===");
            writer.println("Date d'export: " + new java.util.Date());
            writer.println();
            
            double totalGeneral = 0;
            
            for (Compte c : comptes) {
                writer.println("----------------------------------------");
                if (c instanceof CompteEpargne) {
                    CompteEpargne ce = (CompteEpargne) c;
                    writer.printf("Type: Compte Épargne%n");
                    writer.printf("Numéro: %s%n", ce.getNumero());
                    writer.printf("Titulaire: %s%n", ce.getTitulaire());
                    writer.printf("Solde: %.2f MAD%n", ce.getSolde());
                    writer.printf("Taux d'intérêt: %.2f%%%n", ce.getTauxInteret());
                } else {
                    writer.printf("Type: Compte Courant%n");
                    writer.printf("Numéro: %s%n", c.getNumero());
                    writer.printf("Titulaire: %s%n", c.getTitulaire());
                    writer.printf("Solde: %.2f MAD%n", c.getSolde());
                }
                totalGeneral += c.getSolde();
            }
            
            writer.println("----------------------------------------");
            writer.printf("TOTAL: %d compte(s)%n", comptes.size());
            writer.printf("SOLDE TOTAL: %.2f MAD%n", totalGeneral);
            writer.println("========================================");
            
            statut("Données sauvegardées dans " + FICHIER_TEXTE);
            JOptionPane.showMessageDialog(this, 
                "Sauvegarde réussie !\n\n" +
                "Fichier: " + FICHIER_TEXTE + "\n" +
                "Nombre de comptes: " + comptes.size() + "\n" +
                "Solde total: " + String.format("%.2f", totalGeneral) + " MAD",
                "Sauvegarde terminée", 
                JOptionPane.INFORMATION_MESSAGE);
                
        } catch (IOException ex) {
            erreur("Erreur de sauvegarde texte : " + ex.getMessage());
        }
    }
    
    // Nouvelle méthode pour charger depuis le fichier texte
    private void chargerDepuisFichierTexte() {
        File f = new File(FICHIER_TEXTE);
        if (!f.exists()) {
            JOptionPane.showMessageDialog(this,
                "Fichier non trouvé : " + FICHIER_TEXTE + "\n\n" +
                "Veuillez d'abord sauvegarder des comptes en format texte.",
                "Fichier introuvable",
                JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        int option = JOptionPane.showConfirmDialog(this,
            "Attention !\n\n" +
            "Charger les comptes depuis le fichier texte écrasera\n" +
            "tous les comptes actuellement en mémoire.\n\n" +
            "Voulez-vous continuer ?",
            "Chargement fichier texte",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.QUESTION_MESSAGE);
        
        if (option != JOptionPane.YES_OPTION) return;
        
        try (BufferedReader reader = new BufferedReader(new FileReader(FICHIER_TEXTE))) {
            List<Compte> nouveauxComptes = new ArrayList<>();
            String line;
            String currentNumero = null;
            String currentTitulaire = null;
            double currentSolde = 0;
            double currentTaux = 0;
            boolean isEpargne = false;
            boolean inCompte = false;
            
            while ((line = reader.readLine()) != null) {
                if (line.startsWith("Numéro: ")) {
                    currentNumero = line.substring(8).trim();
                    inCompte = true;
                } else if (line.startsWith("Titulaire: ")) {
                    currentTitulaire = line.substring(11).trim();
                } else if (line.startsWith("Solde: ")) {
                    String soldeStr = line.substring(7, line.indexOf(" MAD")).trim();
                    currentSolde = Double.parseDouble(soldeStr);
                } else if (line.startsWith("Type: ")) {
                    isEpargne = line.contains("Épargne");
                } else if (line.startsWith("Taux d'intérêt: ")) {
                    String tauxStr = line.substring(16, line.indexOf("%")).trim();
                    currentTaux = Double.parseDouble(tauxStr);
                } else if (line.equals("----------------------------------------") && inCompte && currentNumero != null) {
                    // Créer le compte
                    if (isEpargne) {
                        nouveauxComptes.add(new CompteEpargne(currentNumero, currentTitulaire, currentSolde, currentTaux));
                    } else {
                        nouveauxComptes.add(new Compte(currentNumero, currentTitulaire, currentSolde));
                    }
                    // Réinitialiser
                    currentNumero = null;
                    currentTitulaire = null;
                    isEpargne = false;
                    currentTaux = 0;
                    inCompte = false;
                }
            }
            
            if (!nouveauxComptes.isEmpty()) {
                comptes.clear();
                comptes.addAll(nouveauxComptes);
                rafraichirTable(comptes);
                statut(comptes.size() + " compte(s) chargé(s) depuis le fichier texte.");
                JOptionPane.showMessageDialog(this,
                    "Chargement réussi !\n\n" +
                    "Fichier: " + FICHIER_TEXTE + "\n" +
                    "Nombre de comptes chargés: " + comptes.size(),
                    "Chargement terminé",
                    JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this,
                    "Aucun compte trouvé dans le fichier texte.",
                    "Chargement terminé",
                    JOptionPane.WARNING_MESSAGE);
            }
            
        } catch (Exception ex) {
            erreur("Erreur de chargement du fichier texte : " + ex.getMessage());
        }
    }

    @SuppressWarnings("unchecked")
    private void chargerDepuisFichier() {
        File f = new File(FICHIER);
        if (!f.exists()) return;
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(f))) {
            List<Compte> liste = (List<Compte>) ois.readObject();
            comptes.addAll(liste);
            statut("📂 " + comptes.size() + " compte(s) chargé(s) depuis le fichier.");
        } catch (Exception ex) {
            statut("ℹ️ Aucune donnée précédente trouvée.");
        }
    }

    // ── Validations ────────────────────────────────────────────────────────────
    private void ajouterValidationsTempsReel() {
        // Validation du numéro en temps réel (chiffres uniquement)
        txtNumero.addKeyListener(new KeyAdapter() {
            public void keyTyped(KeyEvent e) {
                char c = e.getKeyChar();
                if (!Character.isDigit(c) && c != KeyEvent.VK_BACK_SPACE && c != KeyEvent.VK_DELETE) {
                    e.consume();
                    Toolkit.getDefaultToolkit().beep();
                }
            }
        });
        
        // Validation du solde en temps réel (chiffres et point décimal)
        txtSolde.addKeyListener(new KeyAdapter() {
            public void keyTyped(KeyEvent e) {
                char c = e.getKeyChar();
                if (!Character.isDigit(c) && c != '.' && c != KeyEvent.VK_BACK_SPACE && c != KeyEvent.VK_DELETE) {
                    e.consume();
                    Toolkit.getDefaultToolkit().beep();
                }
                // Empêcher plusieurs points
                if (c == '.' && txtSolde.getText().contains(".")) {
                    e.consume();
                }
            }
        });
        
        // Validation du taux en temps réel
        txtTaux.addKeyListener(new KeyAdapter() {
            public void keyTyped(KeyEvent e) {
                char c = e.getKeyChar();
                if (!Character.isDigit(c) && c != '.' && c != KeyEvent.VK_BACK_SPACE && c != KeyEvent.VK_DELETE) {
                    e.consume();
                    Toolkit.getDefaultToolkit().beep();
                }
                if (c == '.' && txtTaux.getText().contains(".")) {
                    e.consume();
                }
            }
        });
        
        // Validation du montant en temps réel
        txtMontant.addKeyListener(new KeyAdapter() {
            public void keyTyped(KeyEvent e) {
                char c = e.getKeyChar();
                if (!Character.isDigit(c) && c != '.' && c != KeyEvent.VK_BACK_SPACE && c != KeyEvent.VK_DELETE) {
                    e.consume();
                    Toolkit.getDefaultToolkit().beep();
                }
                if (c == '.' && txtMontant.getText().contains(".")) {
                    e.consume();
                }
            }
        });
    }
    
    private boolean validerChamps() {
        // Vérification des champs obligatoires
        if (txtNumero.getText().trim().isEmpty() ||
            txtTitulaire.getText().trim().isEmpty() ||
            txtSolde.getText().trim().isEmpty()) {
            erreur("Veuillez remplir tous les champs obligatoires (Numéro, Titulaire, Solde).");
            return false;
        }
        
        // Validation du numéro (doit être un nombre)
        String numeroStr = txtNumero.getText().trim();
        try {
            long num = Long.parseLong(numeroStr);
            if (num <= 0) {
                erreur("Le numéro de compte doit être un nombre positif !");
                return false;
            }
        } catch (NumberFormatException e) {
            erreur("Le numéro de compte doit être un nombre valide !");
            return false;
        }
        
        // Validation du solde
        try {
            double sol = Double.parseDouble(txtSolde.getText().trim());
            if (sol < 0) { 
                erreur("Le solde ne peut pas être négatif."); 
                return false; 
            }
            if (Double.isInfinite(sol) || Double.isNaN(sol)) {
                erreur("Le solde n'est pas valide.");
                return false;
            }
        } catch (NumberFormatException e) {
            erreur("Le solde doit être un nombre valide (exemple: 1000.50)."); 
            return false;
        }
        
        // Validation du taux pour compte épargne
        if (chkEpargne.isSelected() && !txtTaux.getText().trim().isEmpty()) {
            try { 
                double taux = Double.parseDouble(txtTaux.getText().trim());
                if (taux < 0) {
                    erreur("Le taux d'intérêt ne peut pas être négatif.");
                    return false;
                }
                if (taux > 100) {
                    erreur("Le taux d'intérêt ne peut pas dépasser 100%.");
                    return false;
                }
            }
            catch (NumberFormatException e) { 
                erreur("Le taux doit être un nombre valide."); 
                return false; 
            }
        }
        return true;
    }

    private double lireMontant() {
        if (txtMontant.getText().trim().isEmpty()) {
            erreur("Veuillez entrer un montant dans le champ Montant.");
            return -1;
        }
        try {
            double m = Double.parseDouble(txtMontant.getText().trim());
            if (m <= 0) {
                erreur("Le montant doit être positif.");
                return -1;
            }
            return m;
        } catch (NumberFormatException e) {
            erreur("Entrez un montant valide (nombre positif) dans le champ Montant.");
            return -1;
        }
    }

    private boolean numeroExiste(String num) {
        return comptes.stream().anyMatch(c -> c.getNumero().equals(num));
    }

    private Compte trouverParNumero(String num) {
        return comptes.stream().filter(c -> c.getNumero().equals(num)).findFirst().orElse(null);
    }

    private void reinitialiser() {
        txtNumero.setText(""); txtTitulaire.setText("");
        txtSolde.setText("");  txtTaux.setText("");
        txtMontant.setText(""); txtRecherche.setText("");
        chkEpargne.setSelected(false); txtTaux.setEnabled(false);
        table.clearSelection();
    }

    private void rafraichirTable(List<Compte> liste) {
        tableModel.setRowCount(0);
        for (Compte c : liste) {
            boolean ep = c instanceof CompteEpargne;
            tableModel.addRow(new Object[]{
                c.getNumero(),
                c.getTitulaire(),
                String.format(java.util.Locale.FRANCE, "%,.2f MAD", c.getSolde()),
                ep ? "Épargne" : "Courant",
                ep ? String.format("%.2f", ((CompteEpargne) c).getTauxInteret()) : "-"
            });
        }
    }

    private void remplirFormulaire() {
        int row = table.getSelectedRow();
        if (row < 0) return;
        txtNumero.setText((String) tableModel.getValueAt(row, 0));
        txtTitulaire.setText((String) tableModel.getValueAt(row, 1));
        
        String soldeAffiche = (String) tableModel.getValueAt(row, 2);
        txtSolde.setText(soldeAffiche.replace(" MAD", "").replaceAll("[\\s\\u00A0]", "").replace(",", "."));
        
        boolean ep = "Épargne".equals(tableModel.getValueAt(row, 3));
        chkEpargne.setSelected(ep);
        txtTaux.setEnabled(ep);
        txtTaux.setText(ep ? (String) tableModel.getValueAt(row, 4) : "");
    }

    private Compte creerDepuisFormulaire() {
        String num  = txtNumero.getText().trim();
        String tit  = txtTitulaire.getText().trim();
        double sol  = Double.parseDouble(txtSolde.getText().trim());
        if (chkEpargne.isSelected()) {
            double taux = txtTaux.getText().trim().isEmpty() ? 0 :
                          Double.parseDouble(txtTaux.getText().trim());
            return new CompteEpargne(num, tit, sol, taux);
        }
        return new Compte(num, tit, sol);
    }

    private void statut(String msg) { lblStatus.setText(msg); }
    private void erreur(String msg) {
        JOptionPane.showMessageDialog(this, msg, "Erreur", JOptionPane.ERROR_MESSAGE);
    }

    // ── Point d'entrée ─────────────────────────────────────────────────────────
    public static void main(String[] args) {
        try { UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName()); }
        catch (Exception ignored) {}
        SwingUtilities.invokeLater(GestionComptesGUI::new);
    }
}