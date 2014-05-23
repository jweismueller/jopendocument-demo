package com.testcorridor.jopendocument.rich;

import java.util.HashMap;
import java.util.Map;

/**
 * Ziel ist die automatische Transformation von bestimmten HTML-Auszeichnungen
 * in das OpenOffice Template.
 * <p>
 * Unterst�tzt werden sollen:
 * <ul>
 * <li>Abs�tze &lt;p&gt;
 * <li>Listen &lt;ul&gt;
 * <li>Schriftformatierungen &lt;b&gt;, &lt;i&gt;, &lt;u&gt;
 * </ul>
 * Mit geringerer Priorit�t:
 * <ul>
 * <li>Numerierte Listen &lt;ol&gt;
 * </ul>
 * 
 * Zu tun ist also:
 * <ul>
 * <li>Bessere Analyse der HTML-Struktur
 * <li>Ersetzen von &lt;b&gt;, &lt;i&gt;, &lt;u&gt; nach [b], [i], [u]
 * <li>Setzen des PDXML-Inhalts in die generierten Platzhalter
 * <li>Sanitizing des restlichen HTMLs
 * </ul>
 * 
 */
public class OpenOfficeRichTest {

	public static void main(String[] args) throws Exception {
		Map<String, String> dict = new HashMap<String, String>();
		dict.put("content",
				"<b><i><u>Hallo</u></i></b><ul><li>My list.</li></ul><b><i><u>Hallo</u></i></b>");
		PreprocessorRunner runner = new PreprocessorRunner(dict);
		runner.evaluate();
	}

}
