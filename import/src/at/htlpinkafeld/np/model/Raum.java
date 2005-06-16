/*
 * Raum.java
 *
 * Created on 16. Juni 2005, 11:01
 */

package at.htlpinkafeld.np.model;

import at.htlpinkafeld.np.util.*;

/**
 * Die Datenmodell-Klasse "Raum" beinhaltet alle Daten, 
 * die für die Erfassung eines Raums (in dem später 
 * geprüft wird) wichtig sind.
 *
 * @author Thomas Perl <thp@perli.net>
 */
public class Raum implements SQLizable {
    private int uid = 0; // UID des Raums
    private String bezeichnung = ""; // Bezeichung des Raums (zB W-105)
    private String typ = "Klasse"; // Welcher Typ ist der Raum?
    private boolean computerraum = false; // Ist es ein Computerraum?
    
    /**
     * Erstellt ein neues Raum-Objekt.
     *
     * @param uid Die UID des Raums
     * @param bezeichnung Die Bezeichnung des Raums, zB "W-105"
     * @param typ Der Typ des Raums, zB "Klasse"
     * @param computerraum true, wenn der Raum ein Computerraum ist, sonst false
     **/
    public Raum( int uid, String bezeichnung, String typ, boolean computerraum) {
        this.uid = uid;
        this.bezeichnung = bezeichnung;
        this.typ = typ;
        this.computerraum = computerraum;
    }
    
    /**
     * Erstellt ein neues Raum-Objekt. Die Variable 
     * "computerraum" wird hierbei automagisch bestimmt.
     *
     * @param uid Die UID des Raums
     * @param bezeichnung Die Bezeichnung des Raums, zB "W-105"
     * @param typ Der Typ des Raums, zB "Klasse"
     **/
    public Raum( int uid, String bezeichnung, String typ) {
        this.uid = uid;
        this.bezeichnung = bezeichnung;
        this.typ = typ;
    }

    
    // Es folgen die automatischen getter/setter Methoden
    
    
    public int getUid() {
        return uid;
    }

    public void setUid(int uid) {
        this.uid = uid;
    }

    public String getBezeichnung() {
        return bezeichnung;
    }

    public void setBezeichnung(String bezeichnung) {
        this.bezeichnung = bezeichnung;
    }

    public String getTyp() {
        return typ;
    }

    public void setTyp(String typ) {
        this.typ = typ;
    }

    public boolean isComputerraum() {
        return computerraum;
    }

    public void setComputerraum(boolean computerraum) {
        this.computerraum = computerraum;
    }

    /**
     * Wandelt diesen Raum in eine SQL 
     * "INSERT INTO" Anweisung um, mit der dieser 
     * Raum in die Datenbank geschrieben werden kann.
     *
     * @return SQL "INSERT INTO" Statement für dieses Raum
     **/
    public String toSqlInsert() {
        String table = DatabaseMetadata.T_RAUM;
        
        return "INSERT INTO " + table + " (Klassennummer, Bezeichnung, Typ, Größe, Computerraum) " + 
                    "VALUES (" + uid + ", '" + bezeichnung + "', '" + typ + "', 0, " + DatabaseTool.toDatabaseInt( computerraum) + ")";
    }
    
}
