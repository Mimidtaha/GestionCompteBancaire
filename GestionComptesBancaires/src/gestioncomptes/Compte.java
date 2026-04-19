/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package gestioncomptes;

import java.io.Serializable;

public class Compte implements Serializable {
    private static final long serialVersionUID = 1L;

    private String numero;
    private String titulaire;
    private double solde;

    public Compte(String numero, String titulaire, double solde) {
        this.numero = numero;
        this.titulaire = titulaire;
        this.solde = solde;
    }

    // Getters & Setters
    public String getNumero()              { return numero; }
    public void   setNumero(String n)      { this.numero = n; }

    public String getTitulaire()           { return titulaire; }
    public void   setTitulaire(String t)   { this.titulaire = t; }

    public double getSolde()               { return solde; }
    public void   setSolde(double s)       { this.solde = s; }

    // Opérations
    public void deposer(double montant) {
        if (montant > 0) solde += montant;
    }

    public boolean retirer(double montant) {
        if (montant > 0 && solde >= montant) {
            solde -= montant;
            return true;
        }
        return false;
    }

    public String afficherInfos() {
        return String.format("Compte[%s] Titulaire: %s | Solde: %.2f MAD", numero, titulaire, solde);
    }

    @Override
    public String toString() { return afficherInfos(); }
}
