/*
 * Databaseable.java
 *
 * Created on 16. Juni 2005, 08:00
 */

package at.htlpinkafeld.np.model;

/**
 * Das Interface Databaseable beschreibt Funktionalitäten, 
 * die für das Schreiben von Daten in eine Datenbank 
 * benötigt werden. Jeder Importer (und jede andere Klasse), 
 * die Daten importiert (oder bereitstellt), die in eine 
 * Datenbank geschrieben werden, sollte dieses Interface 
 * implementieren. Damit muss nur noch eine Funktion 
 * aufgerufen werden, um die kompletten Daten von den 
 * jeweiligen Importern (oder Objekten) in die Datenbank 
 * zu schreiben.
 *
 * @author Thomas Perl <thp@perli.net>
 */
public interface Databaseable {
    /**
     * Schreibt alle Datensätze, die ein Objekt 
     * beinhaltet in die Datenbank.
     *
     * @return true, wenn das Schreiben erfolgreich war, ansonsten false
     **/
    public boolean allToDatabase();
    
    /**
     * Liefert eine kurze Beschreibung, welche 
     * Daten dieses Databaseable beinhaltet. Dies
     * wird für die Fehlerausgabe und für die sonstige
     * Anzeige im Programm verwendet.
     *
     * @return Beschreibung, was mit diesem Databaseable geschrieben wird
     **/
    public String getDescription();
}
