/*
 * Klasse.java
 *
 * Created on 27. Mai 2005, 10:18
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
 * Die Klasse "Klasse" beinhaltet alle Daten zu einer Klasse. 
 * Zusätzlich gibt es Funktionen, um mit Klassendaten umzugehen.
 *
 * @author Thomas Perl <thp@perli.net>
 */
public class Klasse implements SQLizable {
    private int uid = 0; // Die UID der Klasse
    private String name = ""; // Die Klassenbezeichnung, zB 4ADV
    
    /**
     * Erstellt eine neue Klasse.
     *
     * @param name Der Name der Klasse, zB 4ADV
     **/
    public Klasse( String name) {
        this.name = name;
    }
    
    /**
     * Erstellt eine neue Klasse.
     *
     * @param uid Die UID der Klasse
     * @param name Der Name der Klasse, zB 4ADV
     **/
    public Klasse( int uid, String name) {
        this.uid = uid;
        this.name = name;
    }

    public int getUid() {
        return uid;
    }

    public void setUid(int uid) {
        this.uid = uid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
    
    /**
     * Vergleicht diese Klasse mit einer anderen Klasse.
     * Das Resultat ist ein Wert ähnlich dem Rückgabewert 
     * der Funktion String.compareTo(). Diese Funktion 
     * kann von Sortieralgorithmen benutzt werden.
     *
     * @param k Die Klasse, mit der verglichen werden soll
     * @return int-Wert wie bei String.compareTo()
     **/
    public int compareTo( Klasse k) {
        return name.compareTo( k.name);
    }
    
    /**
     * Vergleicht diese Klasse mit einer anderen auf Gleichheit.
     *
     * @return true, wenn die Klasse die selbe ist, false anderenfalls
     **/
    public boolean equals( Klasse k) {
        return name.equals( k.name);
    }
    
    /**
     * Überprüft, ob die Klasse gültig ist, um zur Datenbank 
     * hinzugefügt zu werden.
     *
     * @return true, wenn die Klasse gültig ist, false anderenfalls
     **/
    public boolean isValid() {
        return !name.equals( "") &&
                name.length() > 2 &&
                Character.isDigit( name.charAt( 0)) && 
                isAbteilung( name.substring( 2));
    }
    
    /**
     * Diese Funktion wird von Klasse.isValid() 
     * benutzt, um festzustellen ob eine 
     * Bezeichnung (zB das "DV" in "4ADV") eine
     * Abteilung ist, die existiert.
     *
     * @param abt Teil des Namen, der für die Abteilung steht (zB name.substring(2))
     * @return true, wenn es eine Abteilung ist, die in der Schule existiert
     **/
    private boolean isAbteilung( String abt) {
        // EDV und Organisation
        if( abt.equals( "DV"))
            return true;
        
        // Bautechnik (Hoch- und Tiefbau)
        if( abt.equals( "B") || abt.equals( "H") || abt.equals( "T"))
            return true;
        
        // Computer- und Leittechnik
        if( abt.equals( "E"))
            return true;
        
        // Maschineningenieurwesen
        if( abt.equals( "M"))
            return true;
        
        return false;
    }

    /**
     * Sortiert einen Vektor von Klassen alphabetisch nach Namen.
     *
     * @param v Der Vektor, der zu sortieren ist
     * @param doIndex Wenn true, werden die UIDs nach dem Sortiervorgang neu gesetzt
     **/
    public static void sort( Vector<Klasse> v, boolean doIndex) {
        boolean isSorted = false;
        
        while( !isSorted)
        {
            isSorted = true;
            for( int i=0; i<v.size()-1; i++)
            {
                Klasse a = v.get(i);
                Klasse b = v.get(i+1);
                if( a.compareTo( b) > 0)
                {
                    v.setElementAt( b, i);
                    v.setElementAt( a, i+1);
                    isSorted = false;
                }
            }
        }
        
        if( doIndex)
        {
            int uid = 1;
            
            for( int i=0; i<v.size(); i++)
                v.get(i).setUid( uid++);
        }
    }
    
    /**
     * Sucht in einem Klassenvektor nach einem 
     * Klassennamen. Liefert die UID der Klasse 
     * zurück, die gefunden wurde.
     *
     * @param klassen Vektor, in dem die Klasse zu suchen ist
     * @param name Name der Klasse, die zu finden ist
     * @return UID der Klasse, wenn gefunden; wenn nicht gefunden -1
     **/
    public static int findKlasse( Vector<Klasse> klassen, String name) {
        for( int i=0; i<klassen.size(); i++)
        {
            Klasse k = klassen.get(i);
            if( name.equals( k.getName()))
                return k.getUid();
        }
        
        return -1;
    }
    
    /**
     * Prüft, ob eine Klasse bereits in einem Vektor von 
     * Klassen vorhanden ist oder nicht.
     *
     * @return true, wenn die Klasse vorhanden ist, ansonsten false
     **/
    public static boolean klasseExists( Vector<Klasse> klassen, Klasse k) {
        for( int i=0; i<klassen.size(); i++)
        {
            if( klassen.get(i).equals( k))
                return true;
        }
        
        return false;
    }
    
    /**
     * Wandelt eine Klasse in einen String um.
     *
     * @return String, der die Klasse repräsentiert (zB "23 4ADV")
     **/
    public String toString() {
        return uid + " " + name;
    }

    /**
     * Wandelt das Klasse-Objekt in ein SQL 
     * "INSERT INTO" Statement um, mit dem das 
     * Objekt in die Datenbank geschrieben werden 
     * kann.
     *
     * @return SQL "INSERT INTO" Statement für diese Klasse
     **/
    public String toSqlInsert() {
        String table = DatabaseMetadata.T_KLASSE;
        
        return "INSERT INTO " + table + " (Klassennummer, Bezeichnung) " + 
                    "VALUES " + "(" + uid + ", '" + name + "')";
    }

}
