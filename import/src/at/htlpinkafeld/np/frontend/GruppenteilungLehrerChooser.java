/*
 * GruppenteilungLehrerChooser.java
 *
 * Created on 24. Juni 2005, 16:29
 */

/*

npImport - Einlesen-Programm f�r Nachpr�fungsplanung
Copyright (c) 2005 Thomas Perl <thp@perli.net>

This program is free software; you can redistribute it and/or
modify it under the terms of the GNU General Public License
as published by the Free Software Foundation; either version 2
of the License, or (at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with this program; if not, write to the Free Software
Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.

*/


package at.htlpinkafeld.np.frontend;

import java.util.*;
import javax.swing.*;

import at.htlpinkafeld.np.model.*;
import at.htlpinkafeld.np.devel.*;

/**
 * Ein Dialog, mit dem man f�r einen Sch�ler in einer bestimmten 
 * Klasse mit einem bestimmten Gegenstand einen Lehrer aussuchen 
 * kann, wenn dies von der Gruppenteilung betroffen ist.
 *
 * @author  Thomas Perl <thp@perli.net>
 */
public class GruppenteilungLehrerChooser extends javax.swing.JDialog {
    private RelationSchuelerKlasseGegenstandMoeglichkeiten sgkm = null;
    private Gegenstand g = null;
    private boolean cancelled = false; // Hat der User abgebrochen?
    
    /** Creates new form GruppenteilungLehrerChooser */
    public GruppenteilungLehrerChooser( java.awt.Frame parent, RelationSchuelerKlasseGegenstandMoeglichkeiten sgkm) {
        // Modal ist auf "true" gesetzt.. somit wird das Programm bislang unterbrochen
        super( parent, true);
        
        this.sgkm = sgkm;
        
        // Dialog aufbauen
        initComponents();
        
        // Dialog mit Daten f�llen
        fillValues();
        
        // Dialog zentrieren
        FrontendUtil.centerForm( this);
        
        // Dialog anzeigen
        setVisible( true);
    }
    
    /**
     * F�llt die Felder auf diesem Dialog mit Werten aus den 
     * Daten, die in diesem Dialog gespeichert sind.
     **/
    private void fillValues()
    {
        VarName.setText( sgkm.getSchueler().getName());
        VarKlasse.setText( sgkm.getKlasse().getName());
        VarGegenstand.setText( sgkm.getGegenstand().getName());
        
        // Liste der Lehrernamen erstellen
        Vector<String> lehrer = new Vector<String>();
        
        Vector<RelationGegenstandLehrerKlasse> relationen = sgkm.getMoeglichkeiten();
        
        for( int i=0; i<relationen.size(); i++)
            lehrer.add( relationen.get(i).getLehrer().getName());
        
        // Datenmodell f�r ComboBox erstellen und in ComboBox darstellen
        DefaultComboBoxModel dcbm = new DefaultComboBoxModel( lehrer);
        ComboBoxLehrer.setModel( dcbm);
    }
    
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc=" Generated Code ">//GEN-BEGIN:initComponents
    private void initComponents() {
        InfoText = new javax.swing.JTextPane();
        MainPanel = new javax.swing.JPanel();
        LblName = new javax.swing.JLabel();
        VarName = new javax.swing.JLabel();
        LblKlasse = new javax.swing.JLabel();
        VarKlasse = new javax.swing.JLabel();
        LblGegenstand = new javax.swing.JLabel();
        VarGegenstand = new javax.swing.JLabel();
        LblLehrer = new javax.swing.JLabel();
        ComboBoxLehrer = new javax.swing.JComboBox();
        ButtonsPanel = new javax.swing.JPanel();
        BtnOK = new javax.swing.JButton();
        btnSkip = new javax.swing.JButton();
        BtnCancel = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Lehrer f\u00fcr Gruppenteilung ausw\u00e4hlen");
        setResizable(false);
        InfoText.setEditable(false);
        InfoText.setText("Ein Sch\u00fcler hat eine Nachpr\u00fcfung in einem Gruppenteilungs-Gegenstand.\nBitte den Lehrer ausw\u00e4hlen, der die Nachpr\u00fcfung durchzuf\u00fchren hat.");
        InfoText.setEnabled(false);
        getContentPane().add(InfoText, java.awt.BorderLayout.NORTH);

        MainPanel.setLayout(new java.awt.GridLayout(0, 2, 10, 10));

        MainPanel.setBorder(new javax.swing.border.EmptyBorder(new java.awt.Insets(10, 10, 10, 10)));
        LblName.setText("Sch\u00fcler:");
        MainPanel.add(LblName);

        VarName.setFont(new java.awt.Font("Microsoft Sans Serif", 1, 11));
        VarName.setText("Auto Matisch");
        MainPanel.add(VarName);

        LblKlasse.setText("Klasse:");
        MainPanel.add(LblKlasse);

        VarKlasse.setFont(new java.awt.Font("Microsoft Sans Serif", 1, 11));
        VarKlasse.setText("4ADV");
        MainPanel.add(VarKlasse);

        LblGegenstand.setText("Gegenstand:");
        MainPanel.add(LblGegenstand);

        VarGegenstand.setFont(new java.awt.Font("Microsoft Sans Serif", 1, 11));
        VarGegenstand.setText("PREU");
        MainPanel.add(VarGegenstand);

        LblLehrer.setText("Lehrer:");
        MainPanel.add(LblLehrer);

        MainPanel.add(ComboBoxLehrer);

        getContentPane().add(MainPanel, java.awt.BorderLayout.CENTER);

        ButtonsPanel.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.RIGHT));

        BtnOK.setText("OK");
        BtnOK.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                BtnOKActionPerformed(evt);
            }
        });

        ButtonsPanel.add(BtnOK);

        btnSkip.setText("\u00dcberspringen");
        btnSkip.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSkipActionPerformed(evt);
            }
        });

        ButtonsPanel.add(btnSkip);

        BtnCancel.setText("Abbrechen");
        BtnCancel.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                BtnCancelActionPerformed(evt);
            }
        });

        ButtonsPanel.add(BtnCancel);

        getContentPane().add(ButtonsPanel, java.awt.BorderLayout.SOUTH);

        pack();
    }
    // </editor-fold>//GEN-END:initComponents

    private void BtnCancelActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BtnCancelActionPerformed
        // Merken, dass wir abbrechen
        cancelled = true;
        
        Logger.warning( this, "User hat abgebrochen!");
        
        // Der Rest verl�uft gleich wie beim "Skip"-Button
        btnSkipActionPerformed( evt);
    }//GEN-LAST:event_BtnCancelActionPerformed

    private void btnSkipActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSkipActionPerformed
        String dbg_geg = sgkm.getGegenstand().getName();
        String dbg_sch = sgkm.getSchueler().getName();
        String dbg_kl = sgkm.getKlasse().getName();
        
        Logger.warning( this, "Kein Lehrer ausgew�hlt f�r Sch�ler " + dbg_sch + " in Klasse " + dbg_kl + " f�r Gegenstand " + dbg_geg + "!");
        
        dispose();
    }//GEN-LAST:event_btnSkipActionPerformed

    private void BtnOKActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BtnOKActionPerformed
        // Gegenstand holen durch Lehrernamen
        String lehrer_name = (String)(ComboBoxLehrer.getModel().getSelectedItem());
        g = getGegenstandByLehrername( lehrer_name);

        if( g != null)
        {
            String dbg_sch = sgkm.getSchueler().getName();
            String dbg_kl = sgkm.getKlasse().getName();
            Logger.debug( this, "Ausgew�hlt: Lehrer " + lehrer_name + " f�r Gegenstand " + g.getName() + " in Klasse " + dbg_kl + " f�r Sch�ler " + dbg_sch + ".");
        }
        
        // Dialog schlie�en
        dispose();
    }//GEN-LAST:event_BtnOKActionPerformed

    /**
     * Holt einen Gegenstand aus den Relationen durch den Namen des 
     * Lehrers. Dies wird von der OK-Button Funktion benutzt, um 
     * den Gegenstand zum Lehrer herauszufinden, der ausgew�hlt wird.
     *
     * @param lehrer_name Der Name des Lehrers (K�rzel)
     * @return Gegenstand-Objekt, welches f�r diesen Lehrer in der Klasse und Gruppe steht
     **/
    private Gegenstand getGegenstandByLehrername( String lehrer_name) {
        for( int i=0; i<sgkm.getMoeglichkeiten().size(); i++)
        {
            RelationGegenstandLehrerKlasse rel = sgkm.getMoeglichkeiten().get(i);
            
            if( rel.getLehrer().equals( new Lehrer( lehrer_name)))
                return rel.getGegenstand();
        }
        
        String dbg_geg = sgkm.getGegenstand().getName();
        String dbg_kl = sgkm.getKlasse().getName();
        
        Logger.warning( this, "Konnte f�r Lehrer" + lehrer_name + " keinen Gegenstand finden (Bei Klasse=" + dbg_kl + " und Gegenstand=" + dbg_geg + ").");
        return null;
    }
    
    /**
     * Liefert den ausgew�hlten Gegenstand, der durch 
     * dieses Auswahlfenster ausgew�hlt wurde.
     *
     * @return Gegenstand, der ausgew�hlt wurde oder null, wenn abgebrochen oder Fehler
     **/
    public Gegenstand getSeletedGegenstand() {
        return g;
    }
    
    /**
     * Pr�ft, ob der User "Abbrechen" geklickt hat oder nicht.
     *
     * @return true, wenn der User auf "Abbrechen" geklickt hat, sonst false
     **/
    public boolean isCancelled() {
        return cancelled;
    }
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton BtnCancel;
    private javax.swing.JButton BtnOK;
    private javax.swing.JPanel ButtonsPanel;
    private javax.swing.JComboBox ComboBoxLehrer;
    private javax.swing.JTextPane InfoText;
    private javax.swing.JLabel LblGegenstand;
    private javax.swing.JLabel LblKlasse;
    private javax.swing.JLabel LblLehrer;
    private javax.swing.JLabel LblName;
    private javax.swing.JPanel MainPanel;
    private javax.swing.JLabel VarGegenstand;
    private javax.swing.JLabel VarKlasse;
    private javax.swing.JLabel VarName;
    private javax.swing.JButton btnSkip;
    // End of variables declaration//GEN-END:variables
    
}