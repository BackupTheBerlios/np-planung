/*
 * JConfigTextField.java
 *
 * Created on 24. Juni 2005, 21:23
 *
 * To change this template, choose Tools | Options and locate the template under
 * the Source Creation and Management node. Right-click the template and choose
 * Open. You can then make changes to the template in the Source Editor.
 */

package at.htlpinkafeld.np.frontend.widgets;

import javax.swing.*;

import at.htlpinkafeld.np.util.*;
import at.htlpinkafeld.np.devel.*;

/**
 * Das JConfigTextField ist ein einfaches JTextField, welches 
 * zusätzlich den Konfigurations-Key (Pfad) für einen Wert 
 * speichern kann. Damit kann das JConfigTextField automatisch 
 * den Wert in einen ConfigManager schreiben. Wird für den 
 * ConfigurationEditor benötigt.
 *
 * @author Thomas Perl <thp@perli.net>
 */
public class JConfigTextField extends JTextField {
    private String path = null; // Konfigurations-Pfad für dieses ConfigTextField
    
    /**
     * Erstellt ein neues JConfigTextField, mit dem man
     * den Pfad zur Konfiguration speichern kann und später
     * dieses auch per ConfigManager speichern kann.
     *
     * @param path Der Konfigurations-Pfad (siehe ConfigManager) für die Eigenschaft
     * @param value Der Anfangswert, auf den dieses JConfigTextField gesetzt wird
     **/
    public JConfigTextField( String path, String value) {
        super( value);
        this.path = path;
    }

    /**
     * Speichert den Wert dieses JConfigTextField in einen 
     * ConfigManager, wobei der Konfigurations-Pfad, der beim 
     * Konstruktor angegeben wurde als Key benutzt wird.
     *
     * @param cm Ein ConfigManager, in dem die Daten abgelegt werden
     **/
    public void saveTo( ConfigManager cm) {
        Logger.debug( this, "Wert wird im ConfigManager abgelegt: [" + path + "]=\"" + getText() + "\"");
        cm.setProperty( path, getText());
    }
}
