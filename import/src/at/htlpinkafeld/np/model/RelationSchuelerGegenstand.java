/*
 * RelationSchuelerGegenstand.java
 *
 * Created on 01. Juni 2005, 17:05
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


package at.htlpinkafeld.np.model;

import java.util.*;

import at.htlpinkafeld.np.util.*;

/**
 * Die Klasse RelationSchuelerGegenstand speichert je einen 
 * Sch�ler und einen Gegenstand, die miteinander verkn�pft 
 * sind. Wenn nun ein Sch�ler einen Gegenstand hat, der 
 * gepr�ft werden soll, wird eine Relation erstellt.
 *
 * @author Thomas Perl <thp@perli.net>
 */
public class RelationSchuelerGegenstand implements SQLizable {
    private Schueler schueler;
    private Gegenstand gegenstand;
    
    /**
     * Erstellt eine neue Sch�ler-Gegenstand Relation, mit der 
     * einem Sch�ler die Pr�fungen per Gegenstand zugeteilt werden.
     *
     * @param schueler Der Sch�ler, mit dem diese Relation verkn�pft werden soll
     * @param gegenstand Der Gegenstand, mit dem diese Relation verkn�pft werden soll
     **/
    public RelationSchuelerGegenstand( Schueler schueler, Gegenstand gegenstand) {
        this.schueler = schueler;
        this.gegenstand = gegenstand;
    }
    
    /**
     * Pr�ft, ob eine Relation zwischen Sch�ler und Gegenstand 
     * bereits in einem Vektor von Relationen existiert oder nicht.
     *
     * @param relationen Der Vektor, der zu durchsuchen ist
     * @param schueler Der Sch�ler der Relation, die gesucht werden soll
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
        // Von hinten durchgehen, weil wir auch etwas herausl�schen
        for( int i=relationen.size()-1; i>=0; i--)
        {
            RelationSchuelerGegenstand r = relationen.get(i);
            
            // Wenn der Sch�ler nicht mehr g�ltig ist, dann l�schen
            if( !r.getSchueler().isValid())
                relationen.remove( r);
        }
    }
    
    /**
     * Wandelt eine Relation in einen String um, der f�r
     * Debugging-Zwecke oder f�r eine sonstige Ausgabe 
     * verwendet werden kann, um.
     *
     * @return String im Format (Sch�ler)-(Gegenstand)
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
     * Sch�lers abgerufen werden kann.
     *
     * @return UID-Nummer des Sch�lers
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
     * @return SQL "INSERT INTO" Statement f�r diese Relation
     **/
    public String toSqlInsert() {
        String table = DatabaseMetadata.getTableName( DatabaseMetadata.SCHUELER_GEGENSTAND);
        
        int s_num = schueler.getUid();
        int g_num = gegenstand.getUid();
        
        return "INSERT INTO " + table + " (Schuelernummer, Gegenstandsnummer) " +
                    "VALUES (" + s_num + ", " + g_num + ")";
    }

}
