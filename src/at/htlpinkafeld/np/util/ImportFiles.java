/*
 * ImportFiles.java
 *
 * Created on 24. Juni 2005, 20:09
 *
 * To change this template, choose Tools | Options and locate the template under
 * the Source Creation and Management node. Right-click the template and choose
 * Open. You can then make changes to the template in the Source Editor.
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

package at.htlpinkafeld.np.util;

/**
 * Diese Klasse beinhaltet Referenzen zu Dateinamen, die in 
 * der Konfigurationsdatei gespeichert werden. Diese 
 * Dateinamen sind Dateinamen und Pfade zu den GPU und 
 * SASII-Dateien.
 *
 * @author Thomas Perl <thp@perli.net>
 */
public class ImportFiles {
    public static final int GPU002 = 2;
    public static final int GPU005 = 5;
    public static final int GPU006 = 6;
    public static final int GPU008 = 8;
    public static final int SASII_SCHUELER_MIT_NOTEN = 9;
    
    /**
     * Dummy-Konstruktor.
     **/
    private ImportFiles() {
        // Nichts!
    }
    
    /**
     * Liefert den Dateinamen einer Import-Datei (GPU bzw SASII).
     * Diese Dateien werden mittels ConfigManager verwaltet und 
     * können dort verändert werden.
     * 
     * @param which Welcher Dateiname wird gesucht (siehe final-int Werte in ImportFiles)
     * @return String, der den Dateinamen der gewünschten Datei angibt oder null bei einem Fehler
     **/
    public static String getFilename( int which) {
        ConfigManager cm = ConfigManager.getInstance();
        
        if( which == GPU002)
            return cm.getProperty( new ImportFiles(), "gpu002", "c:\\gpu002.txt");
        
        if( which == GPU005)
            return cm.getProperty( new ImportFiles(), "gpu005", "c:\\gpu005.txt");
        
        if( which == GPU006)
            return cm.getProperty( new ImportFiles(), "gpu006", "c:\\gpu006.txt");
        
        if( which == GPU008)
            return cm.getProperty( new ImportFiles(), "gpu008", "c:\\gpu008.txt");
        
        if( which == SASII_SCHUELER_MIT_NOTEN)
            return cm.getProperty( new ImportFiles(), "sasii-schuelermitnoten", "c:\\SchuelermitNoten.csv");
            
        return null;
    }
}
