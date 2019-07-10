package io.bootique.tools.shell.template.processor;

import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import io.bootique.tools.shell.template.Properties;
import io.bootique.tools.shell.template.TemplateException;
import org.w3c.dom.Document;
import org.w3c.dom.Node;

public class MavenModuleProcessor extends XMLTemplateProcessor {

    @Override
    protected Document processDocument(Document document, Properties properties) {
        XPath xpath = XPathFactory.newInstance().newXPath();

        try {
            Node artifactId = (Node)xpath.evaluate("/project/artifactId", document, XPathConstants.NODE);
            artifactId.setTextContent(properties.get("project.name"));

            Node version = (Node)xpath.evaluate("/project/version", document, XPathConstants.NODE);
            version.setTextContent(properties.get("project.version"));

            Node parentGroup = (Node)xpath.evaluate("/project/parent/groupId", document, XPathConstants.NODE);
            parentGroup.setTextContent(properties.get("parent.group"));

            Node parentArtifactId = (Node)xpath.evaluate("/project/parent/artifactId", document, XPathConstants.NODE);
            parentArtifactId.setTextContent(properties.get("parent.name"));

            Node parentVersion = (Node)xpath.evaluate("/project/parent/version", document, XPathConstants.NODE);
            parentVersion.setTextContent(properties.get("parent.version"));
        } catch (XPathExpressionException ex) {
            throw new TemplateException("Unable to modify xml, is template a proper maven xml?", ex);
        }

        return document;
    }
}
