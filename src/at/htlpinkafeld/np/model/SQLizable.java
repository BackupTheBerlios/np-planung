/*
 * SQLizable.java
 *
 * Created on 16. Juni 2005, 07:51
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
