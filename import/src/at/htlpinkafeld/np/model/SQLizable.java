/*
 * SQLizable.java
 *
 * Created on 16. Juni 2005, 07:51
 */

package at.htlpinkafeld.np.model;

/**
 * Das Interface SQLizable beschreibt Funktionen, 
 * die dazu gut sind, um ein Objekt in eine 
 * "INSERT INTO" - Anweisung für die Einlesen-
 * Datenbank zu konvertieren. Jedes Objekt (und 
 * jede Relation), die in eine Datenbank geschrieben 
 * werden soll, sollte dieses Interface implementieren.
 * Damit ist es ein leichtes für die Datenbank-schreib-
 * Funktion, eine große Anzahl von Objekten in die 
 * Datenbank zu schreiben.
 *
 * @author Thomas Perl <thp@perli.net>
 */
public interface SQLizable {
    /**
     * Diese Funktion konvertiert das entsprechende 
     * Objekt in eine SQL "INSERT INTO" Anweisung, 
     * mit der das Objekt in die Datenbank geschrieben
     * werden kann.
     *
     * @return String, der eine "INSERT INTO" Anweisung für die Datenbank ist
     **/
    public String toSqlInsert();
}
