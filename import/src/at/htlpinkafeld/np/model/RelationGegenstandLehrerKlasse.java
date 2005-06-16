/*
 * RelationGegenstandLehrerKlasse.java
 *
 * Created on 13. Juni 2005, 17:25
 */

package at.htlpinkafeld.np.model;

import java.util.*;

/**
 * Die Klasse RelationGegenstandLehrerKlasse speichert 
 * eine Relation von je einem Gegenstand mit dem 
 * passenden Lehrer in der passenden Klasse. Diese 
 * Relation wird benötigt, um Lehrern Gegenständen 
 * und Klassen zuzuordnen.
 *
 * @author Thomas Perl <thp@perli.net>
 */
public class RelationGegenstandLehrerKlasse {
    private Gegenstand gegenstand;
    private Lehrer lehrer;
    private Klasse klasse;
    
    /**
     * Erstellt eine neue Gegenstand-Lehrer-Klasse Relation, 
     * mit dem Gegenstände zu Lehrern und Klassen zugeteilt 
     * werden können.
     *
     * @param gegenstand Der Gegenstand, mit dem diese Relation verknüpft werden soll
     * @param lehrer Der Lehrer, mit dem diese Relation verknüpft werden soll 
     * @param klasse Die Klasse, mit der diese Relation verknüpft werden soll
     **/
    public RelationGegenstandLehrerKlasse( Gegenstand gegenstand, Lehrer lehrer, Klasse klasse) {
        this.gegenstand = gegenstand;
        this.lehrer = lehrer;
        this.klasse = klasse;
    }
    
    /**
     * Prüft, ob in einem Vektor von Relationen bereits 
     * eine Relation von einer Klasse zu einem Gegenstand 
     * besteht. Diese Funktion ist dann hilfreich, wenn 
     * man herausfinden will, ob für eine Klasse zusätzlich 
     * eine Relation mit einem anderen Lehrer angelegt 
     * werden soll (Wichtig bei getrennten Gegenständen, 
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
     * Sucht nach der niedrigsten Gruppen ID für eine 
     * Relation zwischen Gegenstand und Klasse.
     *
     * @param relationen Ein Vektor mit den Relationen, in denen zu suchen ist
     * @param gegenstand Ein Gegenstand, für den eine Gruppen ID zu suchen ist
     * @param klasse Eine Klasse, in der der Gegenstand gruppiert ist
     * @return Niedrigster Wert für die nächste Gruppen ID oder 1, wenn keine Gruppen existieren
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
     * Prüft, ob ein Vektor von Relationen bereits eine 
     * Relation von 3 Elementen besitzt.
     *
     * @param relationen Ein Vektor von Relationen, der geprüft wird
     * @param gegenstand Der Gegenstand, der geprüft werden soll
     * @param lehrer Der Lehrer, der geprüft werden soll
     * @param klasse Die Klasse, die geprüft werden soll
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
     * diese Funktion einen leeren Lehrer-Vektor zurück. Wenn 
     * der Gegenstand nur von EINEM Lehrer unterrichtet wird, 
     * liefert diese Funktion einen Vektor mit dem einzelnen 
     * Lehrer zurück. Wenn der Gegenstand von mehreren Lehrern 
     * (zB Gruppenteilung) unterrichtet wird, dann liefert
     * diese Funktion einen Vektor mit allen Lehrern zurück, 
     * die diesen Gegenstand unterrichten.
     *
     * @param relationen Die Relationen, die zu durchsuchen sind (als Vektor)
     * @param gegenstand Der Gegenstand, für den die Lehrer zu suchen sind
     * @param klasse Die Klasse, für die die Lehrer zu suchen sind
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
    
}
