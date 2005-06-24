/*
 * RelationGegenstandLehrerKlasse.java
 *
 * Created on 13. Juni 2005, 17:25
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
 * Die Klasse RelationGegenstandLehrerKlasse speichert 
 * eine Relation von je einem Gegenstand mit dem 
 * passenden Lehrer in der passenden Klasse. Diese 
 * Relation wird ben�tigt, um Lehrern Gegenst�nden 
 * und Klassen zuzuordnen.
 *
 * @author Thomas Perl <thp@perli.net>
 */
public class RelationGegenstandLehrerKlasse implements SQLizable {
    private Gegenstand gegenstand;
    private Lehrer lehrer;
    private Klasse klasse;
    
    /**
     * Erstellt eine neue Gegenstand-Lehrer-Klasse Relation, 
     * mit dem Gegenst�nde zu Lehrern und Klassen zugeteilt 
     * werden k�nnen.
     *
     * @param gegenstand Der Gegenstand, mit dem diese Relation verkn�pft werden soll
     * @param lehrer Der Lehrer, mit dem diese Relation verkn�pft werden soll 
     * @param klasse Die Klasse, mit der diese Relation verkn�pft werden soll
     **/
    public RelationGegenstandLehrerKlasse( Gegenstand gegenstand, Lehrer lehrer, Klasse klasse) {
        this.gegenstand = gegenstand;
        this.lehrer = lehrer;
        this.klasse = klasse;
    }
    
    /**
     * Pr�ft, ob in einem Vektor von Relationen bereits 
     * eine Relation von einer Klasse zu einem Gegenstand 
     * besteht. Diese Funktion ist dann hilfreich, wenn 
     * man herausfinden will, ob f�r eine Klasse zus�tzlich 
     * eine Relation mit einem anderen Lehrer angelegt 
     * werden soll (Wichtig bei getrennten Gegenst�nden, 
     * zum Beispiel PRRU, Programmieren oder Englisch.
     *
     * @param relationen Vektor mit Relationen, die durchsucht werden
     * @param gegenstand Der Gegenstand der Relation
     * @param klasse Die Klasse der Relation
     * @return true, wenn der gegebene Gegenstand mit der Klasse in Relation steht, false wenn nicht
     **/
    public static boolean relationExists( Vector<RelationGegenstandLehrerKlasse> relationen, Gegenstand gegenstand, Klasse klasse)
    {
        for( int i=0; i<relationen.size(); i++)
        {
            RelationGegenstandLehrerKlasse r = relationen.get(i);
            
            if( gegenstand.equalsIgnoreGruppe( r.getGegenstand()) && klasse.equals( r.getKlasse()))
                return true;
        }
        
        return false;
    }

    /**
     * Sucht nach der niedrigsten Gruppen ID f�r eine 
     * Relation zwischen Gegenstand und Klasse.
     *
     * @param relationen Ein Vektor mit den Relationen, in denen zu suchen ist
     * @param gegenstand Ein Gegenstand, f�r den eine Gruppen ID zu suchen ist
     * @param klasse Eine Klasse, in der der Gegenstand gruppiert ist
     * @return Niedrigster Wert f�r die n�chste Gruppen ID oder 1, wenn keine Gruppen existieren
     **/
    public static int findNextGruppeByKlasse( Vector<RelationGegenstandLehrerKlasse> relationen, Gegenstand gegenstand, Klasse klasse)
    {
        int next_gruppe = 1;
        for( int i=0; i<relationen.size(); i++)
        {
            RelationGegenstandLehrerKlasse r = relationen.get(i);
            
            if( gegenstand.equalsIgnoreGruppe( r.getGegenstand()) && klasse.equals( r.getKlasse()) && r.getGegenstand().getGruppe() >= next_gruppe)
            {
                next_gruppe = r.getGegenstand().getGruppe()+1;
            }
        }
        
        return next_gruppe;
    }
    
    /**
     * Pr�ft, ob ein Vektor von Relationen bereits eine 
     * Relation von 3 Elementen besitzt.
     *
     * @param relationen Ein Vektor von Relationen, der gepr�ft wird
     * @param gegenstand Der Gegenstand, der gepr�ft werden soll
     * @param lehrer Der Lehrer, der gepr�ft werden soll
     * @param klasse Die Klasse, die gepr�ft werden soll
     * @return true, wenn die Relation bereits existiert, ansonsten false
     **/
    public static boolean relationExists( Vector <RelationGegenstandLehrerKlasse> relationen, Gegenstand gegenstand, Lehrer lehrer, Klasse klasse)
    {
        for( int i=0; i<relationen.size(); i++)
        {
            RelationGegenstandLehrerKlasse r = relationen.get(i);
            
            if( gegenstand.equalsIgnoreGruppe( r.getGegenstand()) && lehrer.equals( r.getLehrer()) && klasse.equals( r.getKlasse()))
                return true;
        }
        
        return false;
    }
    
    /**
     * Liefert eine Liste von Lehrern, die zu einer bestimmten 
     * Klasse in einem bestimmten Gegenstand passen. Wenn dieser 
     * Gegenstand NICHT in dieser Klasse gelehrt wird, liefert 
     * diese Funktion einen leeren Lehrer-Vektor zur�ck. Wenn 
     * der Gegenstand nur von EINEM Lehrer unterrichtet wird, 
     * liefert diese Funktion einen Vektor mit dem einzelnen 
     * Lehrer zur�ck. Wenn der Gegenstand von mehreren Lehrern 
     * (zB Gruppenteilung) unterrichtet wird, dann liefert
     * diese Funktion einen Vektor mit allen Lehrern zur�ck, 
     * die diesen Gegenstand unterrichten.
     *
     * @param relationen Die Relationen, die zu durchsuchen sind (als Vektor)
     * @param gegenstand Der Gegenstand, f�r den die Lehrer zu suchen sind
     * @param klasse Die Klasse, f�r die die Lehrer zu suchen sind
     * @return Vektor von Lehrern, die f�r diesen Gegenstand + Klasse zutreffen
     **/
    public static Vector<Lehrer> getLehrerByGegenstandKlasse( Vector <RelationGegenstandLehrerKlasse> relationen, Gegenstand gegenstand, Klasse klasse)
    {
        Vector<Lehrer> lehrer = new Vector<Lehrer>();
        
        for( int i=0; i<relationen.size(); i++)
        {
            RelationGegenstandLehrerKlasse r = relationen.get(i);
            
            if( gegenstand.equalsIgnoreGruppe( r.getGegenstand()) && klasse.equals( r.getKlasse()))
            {
                lehrer.add( r.getLehrer());
            }
        }
        
        return lehrer;
    }
    
    /**
     * Liefert eine Liste von Relationen, die auf eine 
     * Klasse und den passenden Gegenstand passen. Aus 
     * dieser Liste kann man den Lehrer ausw�hlen 
     * lassen, der f�r einen Sch�ler unterrichten wird.
     *
     * @param relationen Die Relationen, die zu durchsuchen sind (als Vektor)
     * @param gegenstand Der Gegenstand, f�r den die Relationen zu suchen sind
     * @param klasse Die Klasse, f�r die die Relationen zu suchen sind
     * @return Vektor von Lehrern, auf die dieser Gegenstand + Klasse zutreffen
     **/
    public static Vector<RelationGegenstandLehrerKlasse> getRelationenByGegenstandKlasse( Vector<RelationGegenstandLehrerKlasse> relationen, Gegenstand gegenstand, Klasse klasse)
    {
        Vector<RelationGegenstandLehrerKlasse> rel = new Vector<RelationGegenstandLehrerKlasse>();
        
        for( int i=0; i<relationen.size(); i++)
        {
            RelationGegenstandLehrerKlasse r = relationen.get(i);
            
            // Relation hinzuf�gen, wenn beide Dinge gleich sind
            if( gegenstand.equalsIgnoreGruppe( r.getGegenstand()) && klasse.equals( r.getKlasse()))
                rel.add( r);
        }
        
        return rel;
    }
    
    /**
     * Hilfsfunktion, mit der die UID des gespeicherten Gegenstands 
     * geholt werden kann.
     *
     * @return UID des Gegenstands dieser Relation
     **/
    public int getGegenstandUid() {
        return gegenstand.getUid();
    }
    
    /**
     * Hilfsfunktion, mit der die UID des gespeicherten Lehrers 
     * geholt werden kann.
     *
     * @return UID des Lehrers dieser Relation
     **/
    public int getLehrerUid() {
        return lehrer.getUid();
    }
    
    /**
     * Hilfsfunktion, mit der die UID der gespeicherten Klasse 
     * geholt werden kann.
     *
     * @return UID der Klasse dieser Relation
     **/
    public int getKlasseUid() {
        return klasse.getUid();
    }
    
    /**
     * Wandelt diese Relation in einen String um. Dieser String 
     * ist einfach eine Umwandlung der Objekte (Gegenstand, Lehrer, 
     * Klasse) in einen String, verkn�pft mit "-".
     *
     * @return String im Format "(Gegenstand)-(Lehrer)-(Klasse)"
     **/
    public String toString() {
        return "(" + gegenstand + ")-(" + lehrer + ")-(" + klasse + ")";
    }
    
    // Es folgen die Methoden, die automatisch erzeugt wurden (getter und setter)

    public Gegenstand getGegenstand() {
        return gegenstand;
    }

    public void setGegenstand(Gegenstand gegenstand) {
        this.gegenstand = gegenstand;
    }

    public Lehrer getLehrer() {
        return lehrer;
    }

    public void setLehrer(Lehrer lehrer) {
        this.lehrer = lehrer;
    }

    public Klasse getKlasse() {
        return klasse;
    }

    public void setKlasse(Klasse klasse) {
        this.klasse = klasse;
    }

    /**
     * Wandelt dieses Relationen-Objekt um in einen SQL 
     * "INSERT INTO" - Befehl, mit dem diese Relation 
     * in die Datenbank geschrieben werden kann.
     *
     * @return SQL "INSERT INTO" Befehl, mit dem dieses Objekt in die Datenbank geschrieben werden kann
     **/
    public String toSqlInsert() {
        String table = DatabaseMetadata.getTableName( DatabaseMetadata.GEGENSTAND_LEHRER_KLASSE);

        int g_num = gegenstand.getUid();
        int l_num = lehrer.getUid();
        int k_num = klasse.getUid();
        int dauer = 0;
        
        // Wenn der Gegenstand schriftlich ist, dauert es eine 3/4 Stunde mehr
        if( gegenstand.isSchriftlich())
            dauer += 45;
        
        // Wenn der Gegenstand m�ndlich ist, dauert es 1/4 Stunde mehr
        if( gegenstand.isMuendlich())
            dauer += 15;
        
        return "INSERT INTO " + table + " (Gegenstandsnummer, Lehrernummer, Klassennummer, Dauer) " +
                    "VALUES (" + g_num + ", " + l_num + ", + " + k_num + ", " + dauer + ")";
    }
    
}
