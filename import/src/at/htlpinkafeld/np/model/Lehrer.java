/*
 * Lehrer.java
 *
 * Created on 11. Juni 2005, 13:03
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
 */
public class Lehrer implements SQLizable {
    private int uid = 0; // UID des Lehrers
    private String name = ""; // 4-stelliges Kürzel des Lehrers (zB ANTA)
    private boolean montag = true; // Kann der Lehrer am ersten Tag prüfen?
    private boolean dienstag = true; // Kann der Lehrer am zweiten Tag prüfen?
    
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
     * @param montag true, wenn der Lehrer am Montag prüfen kann
     * @param dienstag true, wenn der Lehrer am Dienstag prüfen kann
     **/
    public Lehrer( int uid, String name, boolean montag, boolean dienstag) {
        this.uid = uid;
        this.name = name;
        this.montag = montag;
        this.dienstag = dienstag;
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
     * Dieser String hat die Form "UID Name Montag Dienstag", 
     * wobei Montag und Dienstag nur jeweils dann eingeblendet 
     * werden, wenn der Lehrer an diesem Tag frei ist.
     *
     * @return String der Form "UID Name Montag Dienstag"
     **/
    public String toString() {
        return uid + " " + name +
               (montag ? " Montag" : "") +
               (dienstag ? " Dienstag" : "");
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

    public boolean isMontag() {
        return montag;
    }

    public void setMontag(boolean montag) {
        this.montag = montag;
    }

    public boolean isDienstag() {
        return dienstag;
    }

    public void setDienstag(boolean dienstag) {
        this.dienstag = dienstag;
    }

    /**
     * Wandelt dieses Lehrer-Objekt in einen SQL 
     * "INSERT INTO" Befehl um.
     *
     * @return SQL "INSERT INTO" Befehl für diesen Lehrer
     **/
    public String toSqlInsert() {
        String table = DatabaseMetadata.T_LEHRER;
        
        /**
         * Für MS Access scheint zu gelten, dass ein "true"
         * Wert bei einem "INSERT INTO" ein "-1" darstellt, 
         * und ein "false" Wert ein "0" darstellt.
         *
         * Bei der Verwendung einer moderneren Datenbank 
         * (MySQL, PostgreSQL) sollte dies dann je nach 
         * verwendeter Datenbank angepasst werden.
         **/
        int montag_int = montag ? -1 : 0;
        int dienstag_int = dienstag ? -1 : 0;
        
        return "INSERT INTO " + table + " (Lehrernummer, Kürzel, Montag, Dienstag) " +
                    "VALUES (" + uid + ", '" + name + "', " + montag_int + ", " + dienstag_int + ")";
    }
    
}
