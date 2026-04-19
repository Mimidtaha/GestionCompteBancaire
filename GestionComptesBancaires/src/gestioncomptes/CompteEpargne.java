/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package gestioncomptes;


import java.io.Serializable;

public class CompteEpargne extends Compte implements Serializable {
    private static final long serialVersionUID = 2L;

    private double tauxInteret;

    public CompteEpargne(String numero, String titulaire, double solde, double tauxInteret) {
        super(numero, titulaire, solde);
        this.tauxInteret = tauxInteret;
    }

    public double getTauxInteret()           { return tauxInteret; }
    public void   setTauxInteret(double t)   { this.tauxInteret = t; }

    public void appliquerInteret() {
        double interet = getSolde() * (tauxInteret / 100);
        deposer(interet);
    }

    @Override
    public String afficherInfos() {
        return String.format("Epargne[%s] Titulaire: %s | Solde: %.2f MAD | Taux: %.2f%%",
                getNumero(), getTitulaire(), getSolde(), tauxInteret);
    }
}
