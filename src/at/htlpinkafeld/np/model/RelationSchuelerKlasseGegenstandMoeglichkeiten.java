/*
 * RelationSchuelerKlasseGegenstandMoeglichkeiten.java
 *
 * Created on 24. Juni 2005, 17:08
 *
 * To change this template, choose Tools | Options and locate the template under
 * the Source Creation and Management node. Right-click the template and choose
 * Open. You can then make changes to the template in the Source Editor.
 */

package at.htlpinkafeld.np.model;

import java.util.*;

import at.htlpinkafeld.np.model.*;

/**
 * Diese Klasse beinhaltet alle Daten, die für die Speicherung 
 * einer Relation zwischen Schüler, Klasse, Gegenstand und 
 * Gegenstand-Lehrer Möglichkeiten (bei Gruppenteilung) benötigt werden.
 *
 * @author Thomas Perl <thp@perli.net>
 */
public class RelationSchuelerKlasseGegenstandMoeglichkeiten {
    private Schueler schueler = null;
    private Klasse klasse = null;
    private Gegenstand gegenstand = null;
    private Vector<RelationGegenstandLehrerKlasse> moeglichkeiten = null;
    
    /**
     * Erstellt eine neue Relation zwischen Schüler, Klasse, Gegenstand 
     * und Möglichkeiten (=Relation Gegenstand, Lehrer, Klasse).
     *
     * @param schueler Der betroffene Schüler
     * @param klasse Die betroffene Klasse
     * @param gegenstand Der betroffene Gegenstand
     * @param moeglichkeiten Ein Vektor von Relationen zwischen Gegenstand, Lehrer, Klasse
     **/
    public RelationSchuelerKlasseGegenstandMoeglichkeiten( Schueler schueler, Klasse klasse, Gegenstand gegenstand, Vector<RelationGegenstandLehrerKlasse> moeglichkeiten) {
        this.schueler = schueler;
        this.klasse = klasse;
        this.gegenstand = gegenstand;
        this.moeglichkeiten = moeglichkeiten;
    }

    /**
     * Bereinigt eine Liste von Relationen (für Schüler, die nicht 
     * mehr Prüfung machen dürfen, oder schueler.isValid() == false.
     *
     * @param relationen Ein Vektor von Relationen, der zu bereinigen ist
     **/
    public static void cleanupRelationen( Vector<RelationSchuelerKlasseGegenstandMoeglichkeiten> relationen) {
        // Von hinten durchgehen, weil wir auch etwas herauslöschen
        for( int i=relationen.size()-1; i>=0; i--)
        {
            RelationSchuelerKlasseGegenstandMoeglichkeiten r = relationen.get(i);
            
            // Wenn der Schüler nicht mehr gültig ist, dann löschen
            if( !r.getSchueler().isValid())
                relationen.remove( r);
        }
    }

    // Setter und Getter Methoden
    
    public Schueler getSchueler() {
        return schueler;
    }

    public void setSchueler(Schueler schueler) {
        this.schueler = schueler;
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

    public Vector<RelationGegenstandLehrerKlasse> getMoeglichkeiten() {
        return moeglichkeiten;
    }

    public void setMoeglichkeiten(Vector<RelationGegenstandLehrerKlasse> moeglichkeiten) {
        this.moeglichkeiten = moeglichkeiten;
    }
    
    
    
}
