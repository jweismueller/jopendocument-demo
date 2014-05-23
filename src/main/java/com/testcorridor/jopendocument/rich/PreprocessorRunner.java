package com.testcorridor.jopendocument.rich;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jdom.Element;
import org.jopendocument.dom.ODPackage;
import org.jopendocument.dom.ODSingleXMLDocument;
import org.jopendocument.dom.ODXMLDocument;
import org.jopendocument.dom.OOXML;
import org.jopendocument.dom.XMLVersion;
import org.jopendocument.dom.template.JavaScriptFileTemplate;
import org.jopendocument.util.JDOMUtils;

import com.testcorridor.jopendocument.OoUtil;

public class PreprocessorRunner {

	static String out = "rich_out.odt";
	private Map<String, String> dict;

	public PreprocessorRunner(Map<String, String> dict) {
		super();
		this.dict = dict;
	}

	public void evaluate() throws Exception {
		File templateFile = new File("rich_in.odt");
		ODPackage odPackage = new ODPackage(templateFile);
		ODXMLDocument content = odPackage.getContent();
		Element root = content.getDocument().getRootElement();

		PreprocessorModel model = new PreprocessorModel(root, dict);
		model.evaluate();

		File targetFile = new File("rich_intermediate.odt");
		odPackage.save(new FileOutputStream(targetFile));

		//

		File outFile = new File(out);
		JavaScriptFileTemplate template = new JavaScriptFileTemplate(targetFile);

		List<Map<String, String>> list = new ArrayList<Map<String, String>>();
		Map<String, String> m1 = new HashMap<String, String>();
		m1.put("content", "Test Test Test");
		list.add(m1);
		Map<String, String> m2 = new HashMap<String, String>();
		m2.put("content", "Test2 Test2 Test2");
		list.add(m2);

		// only for demo
		String p0 = JDOMUtils.output(OOXML.get(XMLVersion.OD).encodeRT("[b]Hallo[/b]",
				OoUtil.styles()));
		String p1 = JDOMUtils.output(OOXML.get(XMLVersion.OD).encodeRT("[u]Hallo[/u]",
				OoUtil.styles()));
		if (model.hasPdXml()) {
			template.setField("content_p0", p0);
			template.setField("content_l0", list);
			template.setField("content_p1", p1);
		}
		final ODSingleXMLDocument ddoc = template.createDocument();
		ddoc.saveAs(outFile);
		System.out.println("written to: " + out);
	}

}