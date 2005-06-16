/*
 * Databaseable.java
 *
 * Created on 16. Juni 2005, 08:00
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
