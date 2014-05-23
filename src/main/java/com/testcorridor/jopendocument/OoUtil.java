package com.testcorridor.jopendocument;

import java.util.HashMap;
import java.util.Map;

public class OoUtil {

	public static final Map<String, String> styles() {
		final Map<String, String> styles = new HashMap<String, String>();
		styles.put("b", "Stark betont");
		styles.put("i", "Betont");
		styles.put("t", "Quelltext");
		styles.put("u", "Besuchter Internetlink");
		return styles;
	}

}
