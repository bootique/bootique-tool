package io.bootique.tools.shell.template.processor;

import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import io.bootique.tools.shell.template.Properties;
import io.bootique.tools.shell.template.TemplateException;
import org.w3c.dom.Document;
import org.w3c.dom.Node;

public class MavenProcessor extends XMLTemplateProcessor {

    @Override
    protected Document processDocument(Document document, Properties properties) {
        XPath xpath = XPathFactory.newInstance().newXPath();

        try {
            Node artifactId = (Node)xpath.evaluate("/project/artifactId", document, XPathConstants.NODE);
            artifactId.setTextContent(properties.get("project.name"));

            Node groupId = (Node)xpath.evaluate("/project/groupId", document, XPathConstants.NODE);
            String groupIdText = properties.get("java.package");
            if(groupIdText == null || groupIdText.trim().isEmpty()) {
                groupId.setTextContent("example");
            } else {
                groupId.setTextContent(groupIdText);
            }

            Node version = (Node)xpath.evaluate("/project/version", document, XPathConstants.NODE);
            version.setTextContent(properties.get("project.version"));

            Node mainClass = (Node)xpath.evaluate("/project/properties/main.class", document, XPathConstants.NODE);
            String javaPackage = properties.get("java.package");
            if(javaPackage != null && !javaPackage.isEmpty()) {
                javaPackage += '.';
            }
            mainClass.setTextContent(javaPackage + "Application");
        } catch (XPathExpressionException ex) {
            throw new TemplateException("Unable to modify xml, is template a proper maven xml?", ex);
        }

        return document;
    }
}
