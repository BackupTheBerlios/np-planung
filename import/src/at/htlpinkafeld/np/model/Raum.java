/*
 * Raum.java
 *
 * Created on 16. Juni 2005, 11:01
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


package at.htlpinkafeld.np.model;

import at.htlpinkafeld.np.util.*;

/**
 * Die Datenmodell-Klasse "Raum" beinhaltet alle Daten, 
 * die für die Erfassung eines Raums (in dem später 
 * geprüft wird) wichtig sind.
 *
 * @author Thomas Perl <thp@perli.net>
 */
public class Raum implements SQLizable {
    private int uid = 0; // UID des Raums
    private String bezeichnung = ""; // Bezeichung des Raums (zB W-105)
    private String typ = "Klasse"; // Welcher Typ ist der Raum?
    private boolean computerraum = false; // Ist es ein Computerraum?
    
    /**
     * Erstellt ein neues Raum-Objekt.
     *
     * @param uid Die UID des Raums
     * @param bezeichnung Die Bezeichnung des Raums, zB "W-105"
     * @param typ Der Typ des Raums, zB "Klasse"
     * @param computerraum true, wenn der Raum ein Computerraum ist, sonst false
     **/
    public Raum( int uid, String bezeichnung, String typ, boolean computerraum) {
        this.uid = uid;
        this.bezeichnung = bezeichnung;
        
        if( !typ.equals( ""))
            this.typ = typ;
        
        this.computerraum = computerraum;
    }
    
    /**
     * Erstellt ein neues Raum-Objekt. Die Variable 
     * "computerraum" wird hierbei automagisch bestimmt.
     *
     * @param uid Die UID des Raums
     * @param bezeichnung Die Bezeichnung des Raums, zB "W-105"
     * @param typ Der Typ des Raums, zB "Klasse"
     **/
    public Raum( int uid, String bezeichnung, String typ) {
        this.uid = uid;
        this.bezeichnung = bezeichnung;
        
        if( !typ.equals( ""))
            this.typ = typ;
        
        this.computerraum = isComputerraum( typ);
    }
    
    /**
     * Führt eine automagische Ermittlung durch, ob 
     * der entsprechende Raum-Typ (zB "EDV-SAAL Zuse") 
     * ein Computerraum ist oder nicht.
     * 
     * @param typ Der Typ des Raums, zB "EDV-SAAL Zuse"
     * @return true, wenn angenommen wird, dass es sich um einen EDV-Saal handelt, sonst false
     **/
    private boolean isComputerraum( String typ) {
        String s = typ.toLowerCase();

        // Im Schuljahr 2004/05 wurden alle EDV-Säle mit "EDV-SAAL" gekennzeichnet
        if( s.contains( "edv-saal"))
            return true;
        
        // hier könnte weitere Magie erfolgen für "true"
        
        // Im Schuljahr 2004/05 waren alle Klassen fix keine EDV-Säle
        if( s.contains( "klasse"))
            return false;
        
        // hier könnte weitere Magie erfolgen für "false"
            
        return false;
    }
    
    /**
     * Prüft, ob ein Raum gültig ist, um zur 
     * Datenbank hinzugefügt zu werden, oder nicht.
     * Diese Funktion hat automagische Kräfte, um 
     * festzustellen, ob ein Raum gültig ist oder nicht.
     *
     * @return true, wenn der Raum hinzugefügt werden soll, ansonsten false
     **/
    public boolean isValid() {
        String b = bezeichnung.toLowerCase();
        String t = typ.toLowerCase();

        // Wenn die Bezeichung fehlt, wars wohl doch kein passender Raum
        if( b.equals( ""))
            return false;
        
        // Ein Raum, der nur "Pseudo" ist (Grund: klar)
        if( t.contains( "pseudo"))
            return false;
        
        // "KUSTODIAT" kommt in der GPU-Datei vor, macht aber wenig Sinn
        if( t.contains( "kusto"))
            return false;
        
        // Nein, wir wollen nicht am Bauhof geprüft werden
        if( t.contains( "bauhof"))
            return false;
        
        // Räume im Werkstättentrakt nur gültig, wenn es sich um Klassen handelt
        if( b.startsWith( "w-") && !t.contains( "klasse"))
            return false;
        
        // Die Räume im Internat sind zum Großteil Studiersäle, auch ignorieren
        if( t.contains( "internat"))
            return false;
        
        // Der Raum "Sprechstunde" ist ebenfalls nur ein Pseudo-Raum
        if( t.contains( "sprechstunde"))
            return false;
        
        // Auch im sportlichen Räumen kann nicht geprüft werden
        if( t.contains( "hallenbad") || t.contains( "turnsaal"))
            return false;
        
        // Das Heizungslabor (WN-10x) ist ebenfalls nicht geeignet
        if( t.contains( "heizungslabor"))
            return false;
        
        // Im Gästehaus werden auch keine Nachprüfungen abgehalten
        if( t.contains( "gästehaus"))
            return false;
        
        // Es existiert der Raum "Werkst. Labor", diesen ebenfalls ignorieren
        if( t.contains( "werkst"))
            return false;
        
        // Standardmäßig wird alles akzeptiert (außer obige Ausnahmen)
        return true;
    }
    
    /**
     * Wandelt dieses Raum-Objekt in einen String um, 
     * der für Debugging-Zwecke (und sonstige Ausgaben)
     * verwendet werden kann.
     *
     * @return String im der Form "UID Bezeichnung Typ (Computerraum)"
     **/
    public String toString() {
        return uid + " " + bezeichnung + " " + typ + (computerraum ? " (Computerraum)" : "");
    }

    
    // Es folgen die automatischen getter/setter Methoden
    
    
    public int getUid() {
        return uid;
    }

    public void setUid(int uid) {
        this.uid = uid;
    }

    public String getBezeichnung() {
        return bezeichnung;
    }

    public void setBezeichnung(String bezeichnung) {
        this.bezeichnung = bezeichnung;
    }

    public String getTyp() {
        return typ;
    }

    public void setTyp(String typ) {
        this.typ = typ;
    }

    public boolean isComputerraum() {
        return computerraum;
    }

    public void setComputerraum(boolean computerraum) {
        this.computerraum = computerraum;
    }

    /**
     * Wandelt diesen Raum in eine SQL 
     * "INSERT INTO" Anweisung um, mit der dieser 
     * Raum in die Datenbank geschrieben werden kann.
     *
     * @return SQL "INSERT INTO" Statement für dieses Raum
     **/
    public String toSqlInsert() {
        String table = DatabaseMetadata.T_RAUM;
        
        return "INSERT INTO " + table + " (Klassennummer, Bezeichnung, Typ, Größe, Computerraum) " + 
                    "VALUES (" + uid + ", '" + bezeichnung + "', '" + typ + "', 0, " + DatabaseTool.toDatabaseInt( computerraum) + ")";
    }
    
}
