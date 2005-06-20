/*
 * Main.java
 *
 * Created on 10. März 2005, 16:45
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

package npimport;

import java.util.*;
import java.io.*;

import at.htlpinkafeld.np.frontend.*;
import at.htlpinkafeld.np.importers.*;
import at.htlpinkafeld.np.util.*;
import at.htlpinkafeld.np.devel.*;

/**
 * Die Main Klasse ist das Hauptprogramm des 
 * Einlesen-Programms für die Nachprüfungsplanung.
 *
 * @author Thomas Perl <thp@perli.net>
 */
public class Main {
    private static String CONFIGFILE = "c:\\np-import.xml";
    
    // Soll ein Import stattfinden oder testen wir etwas anderes?
    private static final boolean doImport = false;

    /**
     * Dies ist die Hauptklasse von der aus das 
     * Import-Programm gestartet wird.
     *
     * @param args Die Kommandozeilen-Parameter
     */
    public static void main(String[] args) {
        DBWriter dbw = new DBWriter();
        ConfigManager cm = ConfigManager.getInstance();
        
        /**
         * Wenn als Kommandozeilenargument etwas angegeben wurde, 
         * dann ist der erste Parameter der Name der Konfigurations-
         * Datei, deshalb die Variable anpassen.
         **/
        if( args.length > 0)
        {
            CONFIGFILE = args[0];
        }
        
        try
        {
            /********* KONFIGURATION EINLESEN **********/
            try
            {
                cm.readFromFile( CONFIGFILE);
            }
            catch( IOException ioe)
            {
                Main error_object = new Main();
                
                Logger.message( error_object, "Konnte Default-Konfiguration nicht laden. Benutze Standardwerte.");
                
                Logger.debug( error_object, "Fehler beim Lesen der Konfiguration: " + CONFIGFILE);
                Logger.debug( error_object, "Obiger Fehler ist folgender: " + ioe.toString());
            }
            
            if( doImport)
            {
                /******************* KLASSEN ***************/
                KlasseImporter ki = new KlasseImporter( "C:\\gpu002.txt");
                ki.readKlassen();

                // Diesen Importer vormerken für Datenbank-Schreiben
                dbw.addDatabaseable( ki);

                /******************** RÄUME *****************/
                RaumImporter ri = new RaumImporter( "C:\\gpu005.txt");
                ri.readRooms();
            
                // Diesen Importer vormerken für Datenbank-Schreiben
                dbw.addDatabaseable( ri);

                /*********** LEHRER UND GEGENSTÄNDE *********/
                LehrerGegenstandImporter lgi = new LehrerGegenstandImporter( "C:\\gpu008.txt");
                lgi.readLehrerGegenstaende();
            
                RelationGegenstandLehrerImporter rlgi = new RelationGegenstandLehrerImporter( "C:\\gpu002.txt", ki, lgi);
                rlgi.readLehrerGegenstaende();
            
                // Diesen Importer vormerken für Datenbank-Schreiben
                dbw.addDatabaseable( rlgi);

                /********* GRUPPENTEINUNGEN (K-L-G) *********/
                GruppenteilungFinder gtf = new GruppenteilungFinder( ki.getKlassen(), rlgi.getGegenstaende(), rlgi.getRelationen());
                gtf.calculateRelationen();
            
                // Gruppenteilung in die HTML Datei schreiben (Position wird in der Konfiguration angegeben)
                gtf.printToHtmlFile();

                /****************** SCHÜLER ****************/
                SchuelerImporter si = new SchuelerImporter( "C:\\SchuelermitNoten.csv", ki, rlgi);
                si.readSchueler();
            
                // Diesen Importer vormerken für Datenbank-Schreiben
                dbw.addDatabaseable( si);
            
                /*********** Hier passiert die Magie **********/
                dbw.writeAll();
            }
            
            /********** KONFIGURATION SPEICHERN ***********/
            cm.saveToFile( CONFIGFILE);
        }
        catch( Exception e)
        {
            e.printStackTrace();
        }
    }
    
}
