/*
 * MainDialog.java
 *
 * Created on 24. Juni 2005, 17:51
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
import java.lang.*;
import java.io.*;
import javax.swing.*;

import at.htlpinkafeld.np.devel.*;
import at.htlpinkafeld.np.frontend.util.*;
import at.htlpinkafeld.np.importers.*;
import at.htlpinkafeld.np.model.*;
import at.htlpinkafeld.np.util.*;

/**
 * Dies ist der Hauptdialog des Einlesen-Programms.
 *
 * @author  Thomas Perl <thp@perli.net>
 */
public class MainDialog extends javax.swing.JFrame implements Runnable {
    // Instance f�r Singleton Ding
    private static MainDialog instance = null;
    
    private String CONFIGFILE = "c:\\np-import.xml"; // Konfigurationsdatei, die beim Start geladen wird
    private DBWriter dbw = new DBWriter(); // Datenbank-Writer
    private ConfigManager cm = ConfigManager.getInstance(); // Konfigurations-Manager

    private KlasseImporter ki = null; // Klasse-Importer
    private RaumImporter ri = null; // Raum-Importer
    private LehrerGegenstandImporter lgi = null; // Lehrer-Gegenstand Importer
    private RelationGegenstandLehrerImporter rlgi = null; // Relation G-L-K Importer
    private SchuelerImporter si = null; // Schueler Importer
    private GruppenteilungFinder gtf = null; // Gruppenteilung-Finder f�r Formulare

    private DefaultListModel dlm = new DefaultListModel(); // List-Model f�r Logger
    
    private boolean doImport = true; // Soll Importiert werden?
    private boolean doFormulare = false; // Sollen Gruppenteilung-Formulare erzeugt werden?
    private boolean doDatenbank = false; // Soll in die Datenbank geschrieben werden?
    
    public static final int HOEHE_MIN = 500; // minimale H�he des Fensters beim Start
    public static final int BREITE_MIN = 700; // minimale Breite des Fensters beim Start
    
    /** Creates new form MainDialog */
    private MainDialog( String [] args) {
        /**
         * Wenn als Kommandozeilenargument etwas angegeben wurde, 
         * dann ist der erste Parameter der Name der Konfigurations-
         * Datei, deshalb die Variable anpassen.
         **/
        if( args.length > 0)
        {
            CONFIGFILE = args[0];
        }
        
        // Konfiguration auslesen
        readConfig();
        
        // Fenster "erstellen"
        initComponents();
        
        // Fenstergr��e anpassen
        // <editor-fold defaultstate="collapsed" desc=" Fenstergr��e anpassen ">
        java.awt.Dimension size = getSize();
        
        if( size.height < HOEHE_MIN)
            size.height = HOEHE_MIN;
        
        if( size.width < BREITE_MIN)
            size.width = BREITE_MIN;
        
        setSize( size);
        // </editor-fold>
        
        // Fenster am Bildschirm zentrieren
        FrontendUtil.centerForm( this);
        
        // Das Fenster anzeigen
        setVisible( true);
        
        // Logger-View auf das Model zeigen lassen
        LoggerList.setModel( dlm);
    }
    
    private void readConfig() {
        try
        {
            cm.readFromFile( CONFIGFILE);
        }
        catch( IOException ioe)
        {
            Logger.message( this, "Konnte Default-Konfiguration nicht laden. Benutze Standardwerte.");
            
            Logger.debug( this, "Fehler beim Lesen der Konfiguration: " + CONFIGFILE);
            Logger.debug( this, "Obiger Fehler ist folgender: " + ioe.toString());
        }
    }
    
    /**
     * Liest Daten aus den GPU bzw SASII Dateien ein.
     **/
    private void readFromGPUAndSASII() throws Exception {
        ki = new KlasseImporter( ImportFiles.getFilename( ImportFiles.GPU002));
        ki.readKlassen();
        dbw.addDatabaseable( ki);
        
        ri = new RaumImporter( ImportFiles.getFilename( ImportFiles.GPU005));
        ri.readRooms();
        dbw.addDatabaseable( ri);
        
        lgi = new LehrerGegenstandImporter( ImportFiles.getFilename( ImportFiles.GPU008));
        lgi.readLehrerGegenstaende();
        
        rlgi = new RelationGegenstandLehrerImporter( ImportFiles.getFilename( ImportFiles.GPU002), ki, lgi);
        rlgi.readLehrerGegenstaende();
        dbw.addDatabaseable( rlgi);
        
        si = new SchuelerImporter( ImportFiles.getFilename( ImportFiles.SASII_SCHUELER_MIT_NOTEN), ki, rlgi);
        si.readSchueler();
        dbw.addDatabaseable( si);        
    }
    
    /**
     * Loggt eine Nachricht auf die Log-Ausgabe. Dies kann vom Logger
     * dazu verwendet werden, um Nachrichten auszugeben.
     *
     * @param message Die anzuzeigende Nachricht
     **/
    public void logMessage( String message) {
        dlm.addElement( message);
        LoggerList.setSelectedIndex( dlm.size()-1);
        
        // Ganz runter scrollen (2x)
        LoggerScrollPane.getVerticalScrollBar().setValue( LoggerScrollPane.getVerticalScrollBar().getMaximum() + 100);
    }

    /**
     * Errechnet Gruppenteilungen und erzeugt Gruppenteilung-Formulare.
     * Diese Funktion muss NACH dem Einlesen von GPU bzw SASII Dateien 
     * aufgerufen werden.
     **/
    private void generateGruppenteilungFormulare() {
        gtf = new GruppenteilungFinder( ki.getKlassen(), rlgi.getGegenstaende(), rlgi.getRelationen());
        gtf.calculateRelationen();
        
        gtf.printToHtmlFile();
    }
    
    /**
     * Schreibt Daten, die eingelesen wurden in die Datenbank. Diese 
     * Funktion muss NACH dem Einlesen von GPU bzw SASII Dateien aufgerufen 
     * werden.
     **/
    private void writeToDatabase() {
        dbw.writeAll();
    }
    
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc=" Generated Code ">//GEN-BEGIN:initComponents
    private void initComponents() {
        ConfigPanel = new javax.swing.JPanel();
        CheckBoxEinlesen = new javax.swing.JCheckBox();
        CheckBoxGruppenteilung = new javax.swing.JCheckBox();
        CheckBoxDatenbank = new javax.swing.JCheckBox();
        CheckBoxDebugging = new javax.swing.JCheckBox();
        LoggerPanel = new javax.swing.JPanel();
        LoggerScrollPane = new javax.swing.JScrollPane();
        LoggerList = new javax.swing.JList();
        ActionPanel = new javax.swing.JPanel();
        BtnExecute = new javax.swing.JButton();
        Menu = new javax.swing.JMenuBar();
        MenuDatei = new javax.swing.JMenu();
        MenuDateiBeenden = new javax.swing.JMenuItem();
        MenuKonfiguration = new javax.swing.JMenu();
        MenuKonfigurationLaden = new javax.swing.JMenuItem();
        MenuKonfiguationSpeichern = new javax.swing.JMenuItem();
        jSeparator1 = new javax.swing.JSeparator();
        MenuKonfigurationBearbeiten = new javax.swing.JMenuItem();
        MenuLogging = new javax.swing.JMenu();
        MenuLoggingLeeren = new javax.swing.JMenuItem();
        jSeparator2 = new javax.swing.JSeparator();
        MenuLoggingSpeichern = new javax.swing.JMenuItem();
        MenuHilfe = new javax.swing.JMenu();
        MenuHilfeUeber = new javax.swing.JMenuItem();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("npImport | http://np-planung.berlios.de/");
        ConfigPanel.setLayout(new java.awt.GridLayout(0, 2, 10, 10));

        ConfigPanel.setBorder(new javax.swing.border.EmptyBorder(new java.awt.Insets(10, 10, 10, 10)));
        CheckBoxEinlesen.setSelected(true);
        CheckBoxEinlesen.setText("Einlesen von GPU/SASII Dateien");
        CheckBoxEinlesen.setEnabled(false);
        ConfigPanel.add(CheckBoxEinlesen);

        CheckBoxGruppenteilung.setText("Gruppenteilung-Formulare erstellen");
        ConfigPanel.add(CheckBoxGruppenteilung);

        CheckBoxDatenbank.setText("In die Datenbank schreiben");
        ConfigPanel.add(CheckBoxDatenbank);

        CheckBoxDebugging.setText("Debug-Ausgabe aktivieren");
        CheckBoxDebugging.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                CheckBoxDebuggingActionPerformed(evt);
            }
        });

        ConfigPanel.add(CheckBoxDebugging);

        getContentPane().add(ConfigPanel, java.awt.BorderLayout.NORTH);

        LoggerPanel.setLayout(new java.awt.BorderLayout());

        LoggerScrollPane.setViewportView(LoggerList);

        LoggerPanel.add(LoggerScrollPane, java.awt.BorderLayout.CENTER);

        getContentPane().add(LoggerPanel, java.awt.BorderLayout.CENTER);

        ActionPanel.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.RIGHT));

        BtnExecute.setText("Gew\u00e4hlte Aktionen ausf\u00fchren");
        BtnExecute.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                BtnExecuteActionPerformed(evt);
            }
        });

        ActionPanel.add(BtnExecute);

        getContentPane().add(ActionPanel, java.awt.BorderLayout.SOUTH);

        MenuDatei.setText("Datei");
        MenuDateiBeenden.setText("Beenden");
        MenuDateiBeenden.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                MenuDateiBeendenActionPerformed(evt);
                jMenuItem1ActionPerformed(evt);
            }
        });

        MenuDatei.add(MenuDateiBeenden);

        Menu.add(MenuDatei);

        MenuKonfiguration.setText("Konfiguration");
        MenuKonfigurationLaden.setText("\u00d6ffnen...");
        MenuKonfigurationLaden.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                MenuKonfigurationLadenActionPerformed(evt);
            }
        });

        MenuKonfiguration.add(MenuKonfigurationLaden);

        MenuKonfiguationSpeichern.setText("Speichern unter...");
        MenuKonfiguationSpeichern.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                MenuKonfiguationSpeichernActionPerformed(evt);
                jMenuItem2ActionPerformed(evt);
            }
        });

        MenuKonfiguration.add(MenuKonfiguationSpeichern);

        MenuKonfiguration.add(jSeparator1);

        MenuKonfigurationBearbeiten.setText("Bearbeiten...");
        MenuKonfigurationBearbeiten.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                MenuKonfigurationBearbeitenActionPerformed(evt);
            }
        });

        MenuKonfiguration.add(MenuKonfigurationBearbeiten);

        Menu.add(MenuKonfiguration);

        MenuLogging.setText("Logging");
        MenuLoggingLeeren.setText("Log leeren");
        MenuLoggingLeeren.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                MenuLoggingLeerenActionPerformed(evt);
            }
        });

        MenuLogging.add(MenuLoggingLeeren);

        MenuLogging.add(jSeparator2);

        MenuLoggingSpeichern.setText("In Datei speichern...");
        MenuLoggingSpeichern.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                MenuLoggingSpeichernActionPerformed(evt);
            }
        });

        MenuLogging.add(MenuLoggingSpeichern);

        Menu.add(MenuLogging);

        MenuHilfe.setText("Hilfe");
        MenuHilfeUeber.setText("\u00dcber...");
        MenuHilfeUeber.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                MenuHilfeUeberActionPerformed(evt);
            }
        });

        MenuHilfe.add(MenuHilfeUeber);

        Menu.add(MenuHilfe);

        setJMenuBar(Menu);

        pack();
    }
    // </editor-fold>//GEN-END:initComponents

    private void MenuLoggingLeerenActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_MenuLoggingLeerenActionPerformed
        // Einfach das Log leeren... :)
        dlm.clear();
    }//GEN-LAST:event_MenuLoggingLeerenActionPerformed

    private void CheckBoxDebuggingActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_CheckBoxDebuggingActionPerformed
        // Live-Update f�r Logger Debug-Modus
        Logger.setDebugging( CheckBoxDebugging.isSelected());
    }//GEN-LAST:event_CheckBoxDebuggingActionPerformed

    private void MenuHilfeUeberActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_MenuHilfeUeberActionPerformed
        JDialog dialog = new JDialog( this, true);
        
        dialog.setTitle( "�ber npImport (Ein etwas weniger aufregender Dialog)");
        dialog.setLayout( new java.awt.BorderLayout());
        dialog.add( new JLabel( "npImport | http://np-import.berlios.de/"), java.awt.BorderLayout.NORTH);
        dialog.add( new JLabel( "Nachpr�fungsplanung HTBL Pinkafeld"), java.awt.BorderLayout.CENTER);
        dialog.add( new JLabel( "Copyright (c) 2005 Thomas Perl <thp@perli.net>"), java.awt.BorderLayout.SOUTH);
        dialog.pack();
        
        FrontendUtil.centerForm( dialog);
        
        dialog.setVisible( true);
    }//GEN-LAST:event_MenuHilfeUeberActionPerformed

    private void jMenuItem2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem2ActionPerformed
    }//GEN-LAST:event_jMenuItem2ActionPerformed

    private void MenuKonfigurationLadenActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_MenuKonfigurationLadenActionPerformed
        JFileChooser chooser = new JFileChooser();
        chooser.setDialogType( JFileChooser.OPEN_DIALOG);
        chooser.setDialogTitle( "Konfigurationsdatei laden");
        
        if( chooser.showOpenDialog( this) == JFileChooser.APPROVE_OPTION)
        {
            String filename = chooser.getSelectedFile().getAbsolutePath();
            try
            {
                cm.readFromFile( filename);
                Logger.progress( this, "Konfiguration geladen von: " + filename);
            }
            catch( Exception e)
            {
                Logger.warning( this, "Fehler beim Laden von Konfiguation: " + e.toString());
            }
        }
    }//GEN-LAST:event_MenuKonfigurationLadenActionPerformed

    private void MenuLoggingSpeichernActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_MenuLoggingSpeichernActionPerformed
        JFileChooser chooser = new JFileChooser();
        chooser.setDialogType( JFileChooser.SAVE_DIALOG);
        chooser.setDialogTitle( "Log-Datei speichern");
        
        if( chooser.showSaveDialog( this) == JFileChooser.APPROVE_OPTION)
        {
            String filename = chooser.getSelectedFile().getAbsolutePath();
            try
            {
                PrintWriter out = new PrintWriter( new FileOutputStream( new File( filename)));
                
                // Zeile f�r Zeile schreiben in die Log-Datei
                for( int i=0; i<dlm.size(); i++)
                    out.println( (String)(dlm.get(i)));
                
                out.close();
                
                Logger.progress( this, "Logging-Datei erfolgreich gespeichert nach: " + filename);
            }
            catch( Exception e)
            {
                Logger.warning( this, "Fehler beim Speichern von Log-Datei: " + e.toString());
            }
        }
    }//GEN-LAST:event_MenuLoggingSpeichernActionPerformed

    private void MenuKonfigurationBearbeitenActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_MenuKonfigurationBearbeitenActionPerformed
        new ConfigurationEditor( this, true);
    }//GEN-LAST:event_MenuKonfigurationBearbeitenActionPerformed

    private void jMenuItem1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem1ActionPerformed
        dispose();
    }//GEN-LAST:event_jMenuItem1ActionPerformed

    private void MenuKonfiguationSpeichernActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_MenuKonfiguationSpeichernActionPerformed
        JFileChooser chooser = new JFileChooser();
        chooser.setDialogType( JFileChooser.SAVE_DIALOG);
        chooser.setDialogTitle( "Konfigurationsdatei speichern");
        
        if( chooser.showSaveDialog( this) == JFileChooser.APPROVE_OPTION)
        {
            String filename = chooser.getSelectedFile().getAbsolutePath();
            try
            {
                cm.saveToFile( filename);
                Logger.progress( this, "Konfiguration gespeichert: " + filename);
            }
            catch( Exception e)
            {
                Logger.warning( this, "Fehler beim Speichern von Konfiguation: " + e.toString());
            }
        }
    }//GEN-LAST:event_MenuKonfiguationSpeichernActionPerformed

    private void BtnExecuteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BtnExecuteActionPerformed
        /**
         * Woo! Los gehts.. Hier passiert die ganze Magie :)
         **/
        
        doImport = CheckBoxEinlesen.isSelected();
        doFormulare = CheckBoxGruppenteilung.isSelected();
        doDatenbank = CheckBoxDatenbank.isSelected();
        
        Logger.setDebugging( CheckBoxDebugging.isSelected());
        
        // Logger-Ausgabe l�schen
        dlm.clear();
        
        // Klicken verboten.. bis der Thread fertig ist.. :)
        BtnExecute.setEnabled( false);
        
        new Thread( this).start();
    }//GEN-LAST:event_BtnExecuteActionPerformed

    private void MenuDateiBeendenActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_MenuDateiBeendenActionPerformed
        dispose();
    }//GEN-LAST:event_MenuDateiBeendenActionPerformed
    
    public static MainDialog getInstance()
    {
        return instance;
    }
    
    public static MainDialog getInstance( String [] args)
    {
        if( instance == null)
            instance = new MainDialog( args);
        
        return instance;
    }

    /**
     * F�hrt alle anstehenden Operationen aus. Wird f�r einen 
     * Thread verwendet.
     **/
    public void run() {
        Logger.progress( this, "Ausf�hrungs-Thread gestartet.");
        if( doImport)
        {
            try
            {
                readFromGPUAndSASII();
            }
            catch( Exception e)
            {
                Logger.warning( this, "Fehler bei Read-Thread(Einlesen): " + e.toString());
            }
        }
        
        if( doFormulare)
            generateGruppenteilungFormulare();
        
        if( doDatenbank)
            writeToDatabase();
        
        Logger.progress( this, "Ausf�hrungs-Thread beendet.");
        
        // Man darf wieder klicken!
        BtnExecute.setEnabled( true);
    }
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel ActionPanel;
    private javax.swing.JButton BtnExecute;
    private javax.swing.JCheckBox CheckBoxDatenbank;
    private javax.swing.JCheckBox CheckBoxDebugging;
    private javax.swing.JCheckBox CheckBoxEinlesen;
    private javax.swing.JCheckBox CheckBoxGruppenteilung;
    private javax.swing.JPanel ConfigPanel;
    private javax.swing.JList LoggerList;
    private javax.swing.JPanel LoggerPanel;
    private javax.swing.JScrollPane LoggerScrollPane;
    private javax.swing.JMenuBar Menu;
    private javax.swing.JMenu MenuDatei;
    private javax.swing.JMenuItem MenuDateiBeenden;
    private javax.swing.JMenu MenuHilfe;
    private javax.swing.JMenuItem MenuHilfeUeber;
    private javax.swing.JMenuItem MenuKonfiguationSpeichern;
    private javax.swing.JMenu MenuKonfiguration;
    private javax.swing.JMenuItem MenuKonfigurationBearbeiten;
    private javax.swing.JMenuItem MenuKonfigurationLaden;
    private javax.swing.JMenu MenuLogging;
    private javax.swing.JMenuItem MenuLoggingLeeren;
    private javax.swing.JMenuItem MenuLoggingSpeichern;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JSeparator jSeparator2;
    // End of variables declaration//GEN-END:variables
    
}
