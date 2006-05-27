/*
 * ConfigurationEditor.java
 *
 * Created on 20. Juni 2005, 21:13
 */

/*

npImport - Einlesen-Programm für Nachprüfungsplanung
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

import at.htlpinkafeld.np.util.*;
import at.htlpinkafeld.np.frontend.widgets.*;

/**
 *
 * @author  Thomas Perl <thp@perli.net>
 */
public class ConfigurationEditor extends javax.swing.JDialog {
    Vector<String> properties = null;
    Vector<JConfigTextField> textfields = new Vector<JConfigTextField>();
    
    /** Creates new form ConfigurationEditor */
    public ConfigurationEditor(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        initComponents();
        
        ConfigManager cm = ConfigManager.getInstance();
        
        properties = cm.getPropertyNames();

        if(properties.size()==0)
        {
            properties.add("at.htlpinkafeld.np.util.ImportFiles.gpu002");
            cm.setProperty("at.htlpinkafeld.np.util.ImportFiles.gpu002", "D:\\Schule\\Projekt\\Backups\\03_Implementierung\\Testdaten AKTUELL\\gpu002.txt");
            properties.add("at.htlpinkafeld.np.util.ImportFiles.gpu005");
            cm.setProperty("at.htlpinkafeld.np.util.ImportFiles.gpu005", "D:\\Schule\\Projekt\\Backups\\03_Implementierung\\Testdaten AKTUELL\\gpu005.txt");
            properties.add("at.htlpinkafeld.np.util.ImportFiles.gpu006");
            cm.setProperty("at.htlpinkafeld.np.util.ImportFiles.gpu006", "D:\\Schule\\Projekt\\Backups\\03_Implementierung\\Testdaten AKTUELL\\gpu006.txt");
            properties.add("at.htlpinkafeld.np.util.ImportFiles.gpu008");
            cm.setProperty("at.htlpinkafeld.np.util.ImportFiles.gpu008", "D:\\Schule\\Projekt\\Backups\\03_Implementierung\\Testdaten AKTUELL\\gpu008.txt");
            properties.add("at.htlpinkafeld.np.util.ImportFiles.sasii-schuelermitnoten");
            cm.setProperty("at.htlpinkafeld.np.util.ImportFiles.sasii-schuelermitnoten", "D:\\Schule\\Projekt\\Backups\\03_Implementierung\\Testdaten AKTUELL\\SchuelermitNoten.csv");
        }

        sortStringVector( properties);
        
        String oldsection = "";
        
        for( int i=0; i<properties.size(); i++)
        {
            String name = properties.get(i);
            
            JConfigTextField tf = new JConfigTextField( name, cm.getProperty( name, null));           
            
            String section = name.substring( 19, name.lastIndexOf( "."));
            
            if( oldsection.equals( section) == false)
            {
                BodyPanel.add( new JLabel( ""));
                BodyPanel.add( new JLabel( getDescription( section)));
                oldsection = section;
            }
            
            JLabel namelbl = new JLabel( name.substring( name.lastIndexOf( ".")+1) + ":");
            namelbl.setHorizontalAlignment( namelbl.RIGHT);
            
            BodyPanel.add( namelbl);
            BodyPanel.add( tf);
            
            textfields.add( tf);                      
        }
        
        pack();
        
        FrontendUtil.setMaxSize( this, 600, 0);
        FrontendUtil.setMinSize( this, 0, 600);
        
        FrontendUtil.centerForm( this);       
        
        // Infomeldung ausgeben
        JOptionPane.showMessageDialog( parent,
                "Möglicherweise werden erst nach einem kompletten Durchlauf\n" + 
                "alle benötigen Konfigurationseinstellungen sichtbar.\n\n");
        
        setVisible( true);
    }
    
    /**
     * Einfache Hilfsfunktion, um einen Stringvektor alphabetisch 
     * zu sortieren.
     *
     * @param v Ein String-Vektor, der alphabetisch sortiert wird
     **/
    private void sortStringVector( Vector<String> v) {
        boolean isSorted = false;
        
        while( !isSorted)
        {
            isSorted = true;
            for( int i=0; i<v.size()-1; i++)
            {
                String a = v.get(i);
                String b = v.get(i+1);
                if( a.compareTo( b) > 0)
                {
                    v.setElementAt( b, i);
                    v.setElementAt( a, i+1);
                    isSorted = false;
                }
            }           
        }
    }
    
    /**
     * Liefert eine Beschreibung für einen Konfigurationspfad.
     *
     * @param path Konfigurationspfad, zB "util.DatabaseMetadata"
     * @return Beschreibung falls vorhanden, ansonsten path
     **/
    private String getDescription( String path) {
        // Definieren von Beschreibungen
        String [][] descriptions = {
            { "util.DatabaseMetadata", "Datenbank-Verbindung" },
            { "util.DatabaseMetadata.tabellen", "Datenbank-Tabellen Namen" },
            { "util.GruppenteilungFinder", "Gruppenteilung-Formulare" },
            { "util.ImportFiles", "Dateipfade für Import" },
            { "model.Gegenstand.dauer.schriftlich", "Schriftliche Prüfungsdauer" },
            { "model.Gegenstand.dauer.muendlich", "Mündliche Prüfungsdauer" },
            { "model.Gegenstand.schriftlich", "Schriftliche Prüfungen" },
            { "model.Gegenstand.muendlich", "Mündliche Prüfungen" },
            { "importers.KlasseImporter", "Spalten (nullbasiert) für GPU002 (nur Klassen)" },
            { "importers.LehrerGegenstandImporter", "Spalten (nullbasiert) für GPU008" },
            { "importers.RelationGegenstandLehrerImporter", "Spalten (nullbasiert) für GPU002" },
            { "importers.SchuelerImporter", "Spalten (nullbasiert) für SchulerMitNoten" },
            { "importers.RaumImporter", "Spalten (nullbasiert) für GPU005" }
        };

        // Suche nach einer Beschreibung
        for( int i=0; i<descriptions.length; i++)
        {
            if( descriptions[i][0].equals( path))
                return descriptions[i][1];
        }

        // Keine Beschreibung gefunden :(
        return path;
    }
    
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc=" Generated Code ">//GEN-BEGIN:initComponents
    private void initComponents() {
        HeaderPanel = new javax.swing.JPanel();
        SpeichernButton = new javax.swing.JButton();
        AbbrechenButton = new javax.swing.JButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        BodyPanel = new javax.swing.JPanel();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Konfigurations-Editor");
        HeaderPanel.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.RIGHT));

        SpeichernButton.setText("Speichern");
        SpeichernButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                SpeichernButtonActionPerformed(evt);
            }
        });

        HeaderPanel.add(SpeichernButton);

        AbbrechenButton.setText("Abbrechen");
        AbbrechenButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                AbbrechenButtonActionPerformed(evt);
            }
        });

        HeaderPanel.add(AbbrechenButton);

        getContentPane().add(HeaderPanel, java.awt.BorderLayout.SOUTH);

        BodyPanel.setLayout(new java.awt.GridLayout(0, 2, 5, 5));

        BodyPanel.setBorder(javax.swing.BorderFactory.createEmptyBorder(10, 10, 10, 10));
        jScrollPane1.setViewportView(BodyPanel);

        getContentPane().add(jScrollPane1, java.awt.BorderLayout.CENTER);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void SpeichernButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_SpeichernButtonActionPerformed
        for( int i=0; i<textfields.size(); i++)
        {
            JConfigTextField tf = textfields.get(i);
            
            // Daten vom TextField in den ConfigManager speichern
            tf.saveTo( ConfigManager.getInstance());
        }
        dispose();
    }//GEN-LAST:event_SpeichernButtonActionPerformed

    private void AbbrechenButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_AbbrechenButtonActionPerformed
        dispose();
    }//GEN-LAST:event_AbbrechenButtonActionPerformed
   
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton AbbrechenButton;
    private javax.swing.JPanel BodyPanel;
    private javax.swing.JPanel HeaderPanel;
    private javax.swing.JButton SpeichernButton;
    private javax.swing.JScrollPane jScrollPane1;
    // End of variables declaration//GEN-END:variables
    
}
