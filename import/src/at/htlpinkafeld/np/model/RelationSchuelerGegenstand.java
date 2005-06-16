/*
 * RelationSchuelerGegenstand.java
 *
 * Created on 01. Juni 2005, 17:05
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

import java.util.*;

import at.htlpinkafeld.np.util.*;

/**
 * Die Klasse RelationSchuelerGegenstand speichert je einen 
 * Schüler und einen Gegenstand, die miteinander verknüpft 
 * sind. Wenn nun ein Schüler einen Gegenstand hat, der 
 * geprüft werden soll, wird eine Relation erstellt.
 *
 * @author Thomas Perl <thp@perli.net>
 */
public class RelationSchuelerGegenstand implements SQLizable {
    private Schueler schueler;
    private Gegenstand gegenstand;
    
    /**
     * Erstellt eine neue Schüler-Gegenstand Relation, mit der 
     * einem Schüler die Prüfungen per Gegenstand zugeteilt werden.
     *
     * @param schueler Der Schüler, mit dem diese Relation verknüpft werden soll
     * @param gegenstand Der Gegenstand, mit dem diese Relation verknüpft werden soll
     **/
    public RelationSchuelerGegenstand( Schueler schueler, Gegenstand gegenstand) {
        this.schueler = schueler;
        this.gegenstand = gegenstand;
    }
    
    /**
     * Prüft, ob eine Relation zwischen Schüler und Gegenstand 
     * bereits in einem Vektor von Relationen existiert oder nicht.
     *
     * @param relationen Der Vektor, der zu durchsuchen ist
     * @param schueler Der Schüler der Relation, die gesucht werden soll
     * @param gegenstand Der Gegenstand der Relation, die gesucht werden soll
     * @return true, wenn die Relation bereits existiert, ansonsten false
     **/
    public static boolean relationExists( Vector<RelationSchuelerGegenstand> relationen, Schueler schueler, Gegenstand gegenstand) {
        for( int i=0; i<relationen.size(); i++)
        {
            RelationSchuelerGegenstand r = relationen.get(i);
            
            if( r.getSchueler().equals( schueler) && r.getGegenstand().equalsIgnoreGruppe( gegenstand))
            {
                return true;
            }
        }
        
        return false;
    }
    
    public static void cleanupRelationen( Vector<RelationSchuelerGegenstand> relationen) {
        // Von hinten durchgehen, weil wir auch etwas herauslöschen
        for( int i=relationen.size()-1; i>=0; i--)
        {
            RelationSchuelerGegenstand r = relationen.get(i);
            
            // Wenn der Schüler nicht mehr gültig ist, dann löschen
            if( !r.getSchueler().isValid())
                relationen.remove( r);
        }
    }
    
    /**
     * Wandelt eine Relation in einen String um, der für
     * Debugging-Zwecke oder für eine sonstige Ausgabe 
     * verwendet werden kann, um.
     *
     * @return String im Format (Schüler)-(Gegenstand)
     **/
    public String toString() {
        return "(" + schueler + ")-(" + gegenstand + ")";
    }

    public Schueler getSchueler() {
        return schueler;
    }

    public void setSchueler(Schueler schueler) {
        this.schueler = schueler;
    }

    public Gegenstand getGegenstand() {
        return gegenstand;
    }

    public void setGegenstand(Gegenstand gegenstand) {
        this.gegenstand = gegenstand;
    }
    
    /**
     * Hilfsfunktion, mit der die UID des gespeicherten 
     * Schülers abgerufen werden kann.
     *
     * @return UID-Nummer des Schülers
     **/
    public int getSchuelerUid() {
        return schueler.getUid();
    }
    
    /**
     * Hilfsfunktion, mit der die UID des gespeicherten 
     * Gegenstands abgerufen werden kann.
     *
     * @return UID-Nummer des Gegenstands
     **/
    public int getGegenstandUid() {
        return gegenstand.getUid();
    }

    /**
     * Wandelt diese Relation in einen SQL "INSERT INTO" 
     * Befehl um, mit dem diese Relation in die Datenbank
     * geschrieben werden kann.
     *
     * @return SQL "INSERT INTO" Statement für diese Relation
     **/
    public String toSqlInsert() {
        String table = DatabaseMetadata.T_SCHUELER_GEGENSTAND;
        
        int s_num = schueler.getUid();
        int g_num = gegenstand.getUid();
        
        return "INSERT INTO " + table + " (Schuelernummer, Gegenstandsnummer) " +
                    "VALUES (" + s_num + ", " + g_num + ")";
    }

}
