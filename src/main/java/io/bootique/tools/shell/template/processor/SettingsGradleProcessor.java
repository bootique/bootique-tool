package io.bootique.tools.shell.template.processor;

import java.nio.ByteBuffer;
import java.nio.charset.Charset;

import io.bootique.tools.shell.Shell;
import io.bootique.tools.shell.template.BinaryTemplate;
import io.bootique.tools.shell.template.Properties;

public class SettingsGradleProcessor extends ParentFileProcessor {

    public SettingsGradleProcessor(Shell shell) {
        super(shell);
    }

    @Override
    protected Charset detectCharset(byte[] content) {
        Charset detectedCharset = tryToDetectCharset(content);
        return detectedCharset != null ? detectedCharset : Charset.defaultCharset();
    }

    @Override
    protected byte[] processParentFile(byte[] content, Charset charset, Properties properties) throws Exception {
        String moduleDefinition = "\ninclude '" + properties.get("project.name") + "'";
        ByteBuffer byteBuffer = charset.encode(moduleDefinition);
        byte[] moduleDefinitionBinary = new byte[byteBuffer.limit()];
        byteBuffer.get(moduleDefinitionBinary);

        int offset = content.length;

        byte[] modifiedContent = new byte[content.length + moduleDefinitionBinary.length];
        System.arraycopy(content, 0, modifiedContent, 0, offset);
        System.arraycopy(moduleDefinitionBinary, 0, modifiedContent, offset, moduleDefinitionBinary.length);

        return modifiedContent;
    }

    @Override
    protected void validateContent(BinaryTemplate template) {
        // do nothing
    }
}
