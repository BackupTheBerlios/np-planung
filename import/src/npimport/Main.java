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
import at.htlpinkafeld.np.frontend.*;
import at.htlpinkafeld.np.importers.*;
import at.htlpinkafeld.np.util.*;

/**
 * Die Main Klasse ist das Hauptprogramm des 
 * Einlesen-Programms für die Nachprüfungsplanung.
 *
 * @author Thomas Perl <thp@perli.net>
 */
public class Main {

    /**
     * Dies ist die Hauptklasse von der aus das 
     * Import-Programm gestartet wird.
     *
     * @param args Die Kommandozeilen-Parameter
     */
    public static void main(String[] args) {
        DBWriter dbw = new DBWriter();
        
        try
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
            
            gtf.printToHtmlFile( "c:\\gruppenteilung.html");

            
            
            /****************** SCHÜLER ****************/
            SchuelerImporter si = new SchuelerImporter( "C:\\SchuelermitNoten.csv", ki, rlgi);
            si.readSchueler();
            
            // Diesen Importer vormerken für Datenbank-Schreiben
            dbw.addDatabaseable( si);
            
            
            
            /*********** Hier passiert die Magie **********/
            dbw.writeAll();
        }
        catch( Exception e)
        {
            e.printStackTrace();
        }
    }
    
}
