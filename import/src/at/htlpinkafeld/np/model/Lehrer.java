/*
 * Lehrer.java
 *
 * Created on 11. Juni 2005, 13:03
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
 * Die Klasse Lehrer beinhaltet alle Informationen zu 
 * einem bestimmten Lehrer. Außerdem beinhaltet diese 
 * Klasse die Informationen, an welchen Tagen der Lehrer 
 * Zeit für eine Prüfung hat.
 *
 * @author Thomas Perl <thp@perli.net>
 * @author edited by Marc Schermann
 */
public class Lehrer implements SQLizable {
    private int uid = 0; // UID des Lehrers
    private String name = ""; // 4-stelliges Kürzel des Lehrers (zB ANTA)
    private boolean tag1 = true; // Kann der Lehrer am ersten Tag prüfen?
    private boolean tag2 = true; // Kann der Lehrer am zweiten Tag prüfen?
    
    /**
     * Erstellt einen neuen Lehrer.
     *
     * @param name Das 4-stellige Kürzel des Lehrers (zB ANTA)
     **/
    public Lehrer( String name) {
        this.name = name;
    }
    
    /**
     * Erstellt einen neuen Lehrer. Mit diesem
     * Konstruktor können alle Werte des 
     * Nachprüfungsprogrammes gesetzt werden.
     *
     * @param uid Die UID des Lehrers
     * @param name Das 4-stellige Kürzel des Lehrers (zB ANTA)
     * @param tag1 true, wenn der Lehrer am 1. Tag prüfen kann
     * @param tag2 true, wenn der Lehrer am 2. Tag prüfen kann
     **/
    public Lehrer( int uid, String name, boolean tag1, boolean tag2) {
        this.uid = uid;
        this.name = name;
        this.tag1 = tag1;
        this.tag2 = tag2;
    }
    
    /**
     * Vergleicht diesen Lehrer mit einem anderen. Das 
     * Resultat ist ein int-Wert, der gleich wie der 
     * von String.compareTo() behandelt werden kann. 
     * Diese Funktion kann von Sortieralgorithmen 
     * benutzt werden.
     *
     * @param l Der Lehrer, der mit diesem verglichen werden soll
     * @return int-Wert wie bei String.compareTo()
     **/
    public int compareTo( Lehrer l) {
        return name.compareTo( l.name);
    }
    
    /**
     * Vergleicht diesen Lehrer mit einem anderen auf Gleichheit.
     *
     * @param l Der Lehrer, mit dem verglichen werden soll
     * @return true, wenn der Lehrer der gleiche ist, false wenn nicht
     **/
    public boolean equals( Lehrer l) {
        return name.equals( l.name);
    }
    
    /**
     * Prüft, ob ein Lehrer gültig ist, um in die Datenbank 
     * eingefügt zu werden. Siehe den Sourcecode, um 
     * herauszufinden, wie die Prüfung in Wirklichkeit 
     * erfolgt.
     *
     * @return true, wenn der Lehrer gültig ist, ansonsten false
     **/
    public boolean isValid() {
        return name.length() == 4;
    }
    
    /**
     * Prüft, ob ein Lehrer bereits in einem Vektor von Lehrern 
     * existiert oder nicht.
     *
     * @param lehrer Ein Lehrer-Vektor, in dem der Lehrer zu suchen ist
     * @param l Der zu suchende Lehrer
     * @return true, wenn der Lehrer bereits existiert, sonst false
     **/
    public static boolean lehrerExists( Vector<Lehrer> lehrer, Lehrer l) {
        for( int i=0; i<lehrer.size(); i++)
        {
            if( lehrer.get( i).equals( l))
                return true;
        }
        return false;
    }
    
    /**
     * Sucht in einem Vektor von Lehrern nach einem Lehrer 
     * und liefert dessen UID zurück, wenn er existiert.
     * Wenn der Lehrer nicht existiert, wird -1 zurückgeliefert.
     *
     * @param lehrer Ein Vektor mit Lehrern, in dem zu suchen ist
     * @param name Das 4-stellige Kürzel eines Lehrer, nach dem zu suchen ist
     * @return Die UID des gefundenen Lehrers, oder -1 wenn der Lehrer nicht gefunden wurde
     **/
    public static int findLehrer( Vector<Lehrer> lehrer, String name) {
        for( int i=0; i<lehrer.size(); i++)
        {
            Lehrer l = lehrer.get( i);
            if( name.equals( l.getName()))
                return l.getUid();
        }
        
        return -1;
    }
    
    /**
     * Sortiert einen Vektor von Lehrern alphabetisch nach Namen 
     * und nimmt bei Bedarf ein Re-Indexing vor.
     *
     * @param v Vektor mit Lehrern, die zu sortieren sind
     * @param doIndex true, wenn die UIDs der Lehrer neu gesetzt werden sollen
     **/
    public static void sort( Vector<Lehrer> v, boolean doIndex) {
        boolean isSorted = false;
        
        while( !isSorted)
        {
            isSorted = true;
            for( int i=0; i<v.size()-1; i++)
            {
                Lehrer a = v.get(i);
                Lehrer b = v.get(i+1);
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
     * Liefert einen String zurück, der den Lehrer beschreibt.
     * Dieser String hat die Form "UID Name tag1 tag2", 
     * wobei tag1 und tag2 nur jeweils dann eingeblendet 
     * werden, wenn der Lehrer an diesem Tag frei ist.
     *
     * @return String der Form "UID Name tag1 tag2"
     **/
    public String toString() {
        return uid + " " + name +
               (tag1 ? " tag1" : "") +
               (tag2 ? " tag2" : "");
    }

    // Ab hier nur mehr getter/setter-Methoden (Beschreibung siehe Variablendeklaration):
    
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

    public boolean isTag1() {
        return tag1;
    }

    public void setTag1(boolean tag1) {
        this.tag1 = tag1;
    }

    public boolean isTag2() {
        return tag2;
    }

    public void setTag2(boolean tag2) {
        this.tag2 = tag2;
    }

    /**
     * Wandelt dieses Lehrer-Objekt in einen SQL 
     * "INSERT INTO" Befehl um.
     *
     * @return SQL "INSERT INTO" Befehl für diesen Lehrer
     **/
    public String toSqlInsert() {
        String table = DatabaseMetadata.getTableName( DatabaseMetadata.LEHRER);
        
        /**
         * Für MS Access scheint zu gelten, dass ein "true"
         * Wert bei einem "INSERT INTO" ein "-1" darstellt, 
         * und ein "false" Wert ein "0" darstellt.
         *
         * Bei der Verwendung einer moderneren Datenbank 
         * (MySQL, PostgreSQL) sollte dies dann je nach 
         * verwendeter Datenbank angepasst werden.
         **/
        int tag1_int = tag1 ? -1 : 0;
        int tag2_int = tag2 ? -1 : 0;
        
        return "INSERT INTO " + table + " (Lehrernummer, Kürzel, tag1, tag2) " +
                    "VALUES (" + uid + ", '" + name + "', " + tag1_int + ", " + tag2_int + ")";
    }
    
}
