/*
 * RelationGruppenteilung.java
 *
 * Created on 14. Juni 2005, 12:25
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

import at.htlpinkafeld.np.model.*;

/**
 * Diese Klasse beinhaltet alle Relationen, die 
 * bei einer Gruppenteilung auftreten, sprich:
 * Je eine Klasse und einen Gegenstand und dazu 
 * einen Vektor von Lehrern, die diesen Gegenstand 
 * in einer Klasse unterrichten.
 *
 * @author Thomas Perl <thp@perli.net>
 */
public class RelationGruppenteilung {
    private Klasse klasse;
    private Gegenstand gegenstand;
    private Vector<Lehrer> lehrer;
    
    /**
     * Erstellt eine neue Relation für die Gruppenteilung.
     *
     * @param klasse Die Klasse, die zur Relation gehört
     * @param gegenstand Der Gegenstand, der zur Relation gehört
     * @param lehrer Ein Vektor von Lehrern, die den genannten Gegenstand in der genannten Klasse unterrichten
     **/
    public RelationGruppenteilung( Klasse klasse, Gegenstand gegenstand, Vector<Lehrer> lehrer)
    {
        this.klasse = klasse;
        this.gegenstand = gegenstand;
        this.lehrer = lehrer;
    }
    
    /**
     * Prüft, ob eine Relation zwischen einer Klasse und einem Gegenstand bereits
     * existiert, oder ob so eine Relation noch nicht existiert.
     *
     * @param relationen Ein Vektor von Gruppenteilung-Relationen
     * @param klasse Die Klasse, nach der zu suchen ist
     * @param gegenstand Der Gegenstand, nach dem zu suchen ist
     * @return true, wenn bereits eine Relation existieren, sonst false
     **/
    public static boolean relationExists( Vector<RelationGruppenteilung> relationen, Klasse klasse, Gegenstand gegenstand)
    {
        for( int i=0; i<relationen.size(); i++)
        {
            RelationGruppenteilung relation = relationen.get( i);
            
            if( relation.getKlasse().equals( klasse) && relation.getGegenstand().equalsIgnoreGruppe( gegenstand))
                return true;
        }
        
        return false;
    }

    public Klasse getKlasse() {
        return klasse;
    }

    public void setKlasse(Klasse klasse) {
        this.klasse = klasse;
    }

    public Gegenstand getGegenstand() {
        return gegenstand;
    }

    public void setGegenstand(Gegenstand gegenstand) {
        this.gegenstand = gegenstand;
    }

    public Vector<Lehrer> getLehrer() {
        return lehrer;
    }

    public void setLehrer(Vector<Lehrer> lehrer) {
        this.lehrer = lehrer;
    }
    
}
