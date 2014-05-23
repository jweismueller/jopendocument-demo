package com.testcorridor.jopendocument.rich;

import java.util.ArrayList;
import java.util.List;

import org.jdom.Attribute;
import org.jdom.Element;

public class JDom {

	public static boolean isParent(Element elem, String name) {
		return name.equals(elem.getParentElement().getName());
	}

	public static void putOneLevelUp(Element elem) {
		Element parent = elem.getParentElement(); // span
		Element parentParent = parent.getParentElement(); // p
		int foundIndex = getParentIndex(parent);
		if (foundIndex != -1) {
			parent.removeContent(elem);
			parentParent.removeContent(parent);
			parentParent.addContent(foundIndex, elem);
		}
	}

	public static int getParentIndex(Element e) {
		Element parent = e.getParentElement();
		List<?> children = parent.getChildren();
		int foundIndex = -1;
		for (int i = 0; i < children.size(); i++) {
			if (children.get(i).equals(e)) {
				foundIndex = i;
			}
		}
		return foundIndex;
	}

	public static Element getParent(String name, Element e) {
		if (e.getParentElement() == null) {
			return null;
		} else if (name.equals(e.getName())) {
			return e;
		} else {
			return getParent(name, e.getParentElement());
		}
	}

	public static Element getElementBelowParent(String name, Element e) {
		return getElementBelowParent(name, e, e);
	}

	private static Element getElementBelowParent(String name, Element e, Element last) {
		if (e.getParentElement() == null) {
			return last;
		} else if (name.equals(e.getName())) {
			return last;
		} else {
			return getElementBelowParent(name, e.getParentElement(), e);
		}
	}

	/** Returns all elements which contain the given text. */
	public static List<Element> findAllContaining(String text, Element elem) {
		List<Element> out = new ArrayList<Element>();
		findAllContaining(text, elem, out);
		return out;
	}

	private static void findAllContaining(String text, Element elem, List<Element> found) {
		List<?> children = elem.getChildren();
		for (Object object : children) {
			if (object instanceof Element) {
				if (elem.getText().contains(text)) {
					found.add((Element) object);
				}
				findAllContaining(text, (Element) object, found);
			}
		}
	}

	public static List<Attribute> findAllAttribute(String name, String attribute, Element elem) {
		List<Attribute> out = new ArrayList<Attribute>();
		List<Element> all = allChildren(elem);
		for (Element e : all) {
			if (e.getName().startsWith(name)) {
				Attribute a = findAttribute(attribute, e);
				if (a != null) {
					out.add(a);
				}
			}
		}
		return out;
	}

	/** Returns all children with given name. */
	public static List<Element> getAllChildren(String name, Element elem) {
		List<Element> out = new ArrayList<Element>();
		getAllChildren(name, elem, out);
		return out;
	}

	private static void getAllChildren(String name, Element elem, List<Element> found) {
		List<?> children = elem.getChildren();
		for (Object object : children) {
			if (object instanceof Element) {
				if (name.equals(((Element) object).getName())) {
					found.add((Element) object);
				}
				getAllChildren(name, (Element) object, found);
			}
		}
	}

	/** Prints the tree. */
	public static String toString(Element e) {
		StringBuilder out = new StringBuilder();
		toString(e, out, 0);
		return out.toString();
	}

	private static void toString(Element e, StringBuilder s, int indent) {
		for (int i = 0; i < indent; i++) {
			s.append(' ');
		}
		s.append(e.getName());
		s.append(":");
		s.append(e.getText());
		s.append('\n');
		for (Object object : e.getChildren()) {
			if (object instanceof Element) {
				toString((Element) object, s, indent + 1);
			}
		}
	}

	/** Not memory friendly linearization. */
	public static List<Element> allChildren(Element e) {
		List<Element> out = new ArrayList<Element>();
		allChildren(e, out);
		return out;
	}

	private static void allChildren(Element e, List<Element> found) {
		List<?> children = e.getChildren();
		for (Object object : children) {
			if (object instanceof Element) {
				found.add((Element) object);
				allChildren((Element) object, found);
			}
		}
	}

	public static Attribute findAttribute(String attribute, Element e) {
		List<?> attributes = e.getAttributes();
		for (Object object : attributes) {
			if (attribute.equals(((Attribute) object).getName())) {
				return (Attribute) object;
			}
		}
		return null;
	}
}
