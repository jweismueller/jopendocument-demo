package com.testcorridor.jopendocument.rich;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.jdom.Attribute;
import org.jdom.Element;

public class PreprocessorModel {

	private final Element root;

	private List<PdXml> pdXmls = new ArrayList<PdXml>();

	private final Map<String, String> dict;

	public PreprocessorModel(Element e, Map<String, String> dict) {
		this.root = e;
		this.dict = dict;
		this.init();
	}

	private void init() {
		List<Attribute> findAllAttribute = JDom.findAllAttribute("text-input", "description", root);
		for (Attribute attribute : findAllAttribute) {
			if (attribute.getValue().startsWith("PDXML")) {
				PdXml a = new PdXml(attribute);
				pdXmls.add(a);
			}
		}
	}

	public void evaluate() {
		for (PdXml pdXml : pdXmls) {
			pdXml.setContent(dict.get(pdXml.getKey()));
			pdXml.evaluate();
		}
	}

	public boolean hasPdXml() {
		return !pdXmls.isEmpty();
	}

}
