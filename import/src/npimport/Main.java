/*
 * Main.java
 *
 * Created on 10. März 2005, 16:45
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

package npimport;

import at.htlpinkafeld.np.frontend.*;

/**
 * Die Main Klasse ist das Hauptprogramm des 
 * Einlesen-Programms für die Nachprüfungsplanung.
 *
 * @author Thomas Perl <thp@perli.net>
 */
public class Main {
    /**
     * Dies ist die Hauptklasse von der aus das 
     * Import-Programm gestartet wird.
     *
     * @param args Die Kommandozeilen-Parameter
     */
    public static void main(String[] args) {
        // Instanz vom MainDialog erzeugen, mit Kommandozeilenparametern
        MainDialog.getInstance( args);
    }
    
}
