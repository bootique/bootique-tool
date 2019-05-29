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
            Node artefactId = (Node)xpath.evaluate("/project/artifactId", document, XPathConstants.NODE);
            artefactId.setTextContent(properties.get("maven.artifactId"));

            Node groupId = (Node)xpath.evaluate("/project/groupId", document, XPathConstants.NODE);
            groupId.setTextContent(properties.get("maven.groupId"));

            Node version = (Node)xpath.evaluate("/project/version", document, XPathConstants.NODE);
            version.setTextContent(properties.get("maven.version"));
        } catch (XPathExpressionException ex) {
            throw new TemplateException("Unable to modify xml, is template a proper maven xml?", ex);
        }

        return document;
    }
}
