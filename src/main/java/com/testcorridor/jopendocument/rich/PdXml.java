package com.testcorridor.jopendocument.rich;

import java.text.MessageFormat;

import org.jdom.Attribute;
import org.jdom.DefaultJDOMFactory;
import org.jdom.Element;
import org.jdom.Namespace;

public class PdXml {

	private final String JOD = "<forEach items=\"{0}\" var=\"i\" element=\"list-item\"/>";

	private final DefaultJDOMFactory factory = new DefaultJDOMFactory();

	private final Attribute attr;

	private final String key;

	private final Element pdXml;

	private final Element pPrototype;

	private final Element p;

	private final int pIndex;

	private final Element text;

	private int lists = 0;

	public PdXml(Attribute a) {
		this.attr = a;
		this.pdXml = a.getParent();
		this.p = JDom.getParent("p", this.pdXml);
		this.text = JDom.getParent("text", this.pdXml);
		this.pPrototype = emptyClone("p", this.p);
		this.pIndex = JDom.getParentIndex(this.p) + 1;
		this.key = attr.getValue().substring("PDXML:".length());
		this.attr.setValue(this.key);
	}

	public Element getElement() {
		return pdXml;
	}

	public void setContent(String content) {
		int i = 0;
		int c = -1;
		while (i != -1) {
			i += 4;
			c++;
			i = content.indexOf("<ul>", i);
		}
		lists = c;
	}

	public String getKey() {
		return key;
	}

	public void evaluate() {
		text.removeContent(p);
		int c = 0;
		for (int i = 0; i < this.lists; i++) {
			Element para = paragraphStructure(key + "_p" + i);
			this.text.addContent(pIndex + c++, para);
			Element list = listStructure(key + "_l" + i);
			this.text.addContent(pIndex + c++, list);
		}
		Element para = paragraphStructure(key + "_p" + this.lists);
		this.text.addContent(pIndex + c++, para);
	}

	//
	// UTIL
	//

	private Element paragraphStructure(String key) {
		Element p = emptyClone("p", this.pPrototype);
		Element input = (Element) pdXml.clone();
		JDom.findAttribute("description", input).setValue("OOXML:" + key);
		p.addContent(input);
		return p;
	}

	private Element listStructure(String key) {
		Element list = emptyClone("list", this.pPrototype);
		Element listItem = emptyClone("list-item", this.pPrototype);
		Element p = emptyClone("p", this.pPrototype);
		Element input = (Element) pdXml.clone();
		String val = "i.get(\"content\")";
		JDom.findAttribute("description", input).setValue(val);
		list.addContent(listItem);
		listItem.addContent(p);
		p.addContent(input);
		p.addContent(jodStructure(key));
		return list;
	}

	static Namespace ns = Namespace.getNamespace("script", "http://openoffice.org/2004/office");

	private Element jodStructure(String key) {
		Element script = emptyClone("script", this.pPrototype);
		Attribute attribute = factory.attribute("language", "JODScript");
		attribute.setNamespace(ns);
		script.setAttribute(attribute);
		String text = MessageFormat.format(JOD, key);
		script.setText(text);
		return script;
	}

	private static Element emptyClone(String name, Element e) {
		Element clone = (Element) e.clone();
		clone.removeContent();
		clone.setName(name);
		return clone;
	}

}
