package com.testcorridor.jopendocument;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.UUID;
import java.util.regex.Pattern;

import org.jdom.Attribute;
import org.jdom.Element;
import org.jdom.Namespace;
import org.jopendocument.dom.ODSingleXMLDocument;
import org.jopendocument.dom.OOXML;
import org.jopendocument.dom.XMLVersion;
import org.jopendocument.dom.template.JavaScriptFileTemplate;
import org.jopendocument.util.JDOMUtils;
import org.junit.Test;

import com.testcorridor.jopendocument.rich.JDom;

public class JOpenDocumentTest {

	static Namespace ns = Namespace.getNamespace("xlink", "http://www.w3.org/1999/xlink");

	static Pattern p = Pattern.compile("<(/?[buix])>");

	static String in = "sample_in.odt";

	static String out = "sample_out.odt";

	@Test
	public void test() throws Exception {
		File templateFile = new File(in);
		File outFile = new File(out);
		// load
		JavaScriptFileTemplate template = new JavaScriptFileTemplate(templateFile);

		// field (standard)
		template.setField("simpleField", "Hallo World!");

		// set command
		template.setField("male", false);

		// field (enc)
		template.setField("encField", "Hallo World!\nWith linebreak! And \t tab.");

		// field (ooxml)
		String ooxmlIn = "Test [b]bold![/b]\nTest [i]italic![/i]\nTest with\ttab [u]underlined![/u]";
		// 1.3 use XMLFormatVersion.getDefault()
		String ooxmlRs = JDOMUtils.output(OOXML.get(XMLVersion.OD).encodeRT(ooxmlIn,
				OoUtil.styles()));
		ooxmlRs = ooxmlRs.replaceAll(Pattern.quote("\n"), "<text:line-break />");
		ooxmlRs = ooxmlRs.replaceAll(Pattern.quote("\t"), "<text:tab />");
		template.setField("ooxmlField", ooxmlRs);

		// field (vaadinContent)
		String vaadinIn = "<b><i><u>Hallo</u></i></b><ul><li>My list.</li></ul><b><i><u>Hallo</u></i></b>";
		vaadinIn = p.matcher(vaadinIn).replaceAll("[$1]");
		String vaadinOut = "Hallo<text:line-break />";
		vaadinOut += "<text:list>";
		vaadinOut += "<text:list-item>";
		vaadinOut += "<text:p>Hardcoded list.</text:p>";
		vaadinOut += "</text:list-item>";
		vaadinOut += "</text:list>";
		// vaadinOut =
		// JDOMUtils.output(OOXML.get(XMLVersion.OD).encodeRT(vaadinOut,
		// styles));

		template.setField("content", vaadinOut);

		// table
		List<Map<String, String>> months = new ArrayList<Map<String, String>>();
		months.add(createTableRow("January", "-12", "3"));
		months.add(createTableRow("February", "-8", "5"));
		months.add(createTableRow("March", "-5", "12"));
		months.add(createTableRow("April", "-1", "15"));
		months.add(createTableRow("May", "3", "21"));
		template.setField("months", months);

		// hiding
		template.hideParagraph("p1");

		// checkbox
		setCheckbox(template, "isGreen", false);
		setCheckbox(template, "isYellow", true);

		// bullet points
		List<Map<String, String>> items = new ArrayList<Map<String, String>>();
		items.add(createBulletParagraph("The world is a globe."));
		items.add(createBulletParagraph("The world is a globe."));
		template.setField("items", items);

		// images
		File image = new File("avatar.png");

		File tmp = copyFileToTmp(image);
		// System.out.println(tmp.toURI().toURL().toString());

		final ODSingleXMLDocument ddoc = template.createDocument();

		ddoc.getDescendantByName("draw:frame", "worldImage").setAttribute("href",
				tmp.toURI().toURL().toString(), ns);
		Element rootElement = ddoc.getDocument().getRootElement();

		System.out.println(JDom.toString(rootElement));

		List<Element> pdxmls = new ArrayList<Element>();
		List<Attribute> findAllAttribute = JDom
				.findAllAttribute("span", "description", rootElement);
		for (Attribute attribute : findAllAttribute) {
			if (attribute.getValue().startsWith("PDXML")) {
				pdxmls.add(attribute.getParent());
			}
		}

		for (Element e : pdxmls) {
			System.out.println(JDom.toString(e));
		}

		// save
		ddoc.saveAs(outFile);
		System.out.println("written to: " + out);
		// template.saveAs(outFile);
	}

	static Map<String, String> createTableRow(String n, String min, String max) {
		final Map<String, String> res = new HashMap<String, String>();
		res.put("name", n);
		res.put("min", min);
		res.put("max", max);
		return res;
	}

	static Map<String, String> createBulletParagraph(String n) {
		final Map<String, String> res = new HashMap<String, String>();
		res.put("value", n);
		return res;
	}

	static void setCheckbox(JavaScriptFileTemplate template, String key, boolean value) {
		template.setField(key, value ? "\u2612" : "\u2610");
	}

	static File copyFileToTmp(File in) throws IOException {
		File out = File.createTempFile(UUID.randomUUID().toString(), null);
		try (FileInputStream inStream = new FileInputStream(in);
				FileOutputStream outStream = new FileOutputStream(out);
				FileChannel inChannel = inStream.getChannel();
				FileChannel outChannel = outStream.getChannel();) {
			inChannel.transferTo(0, inChannel.size(), outChannel);
		} catch (IOException e) {
			throw e;
		}
		return out;
	}

	static String replaceAll(Map<String, String> map, String in) {
		String out = in;
		Set<Entry<String, String>> entrySet = map.entrySet();
		for (Entry<String, String> entry : entrySet) {
			out = out.replaceAll(entry.getKey(), entry.getValue());
		}
		return out;
	}

}
