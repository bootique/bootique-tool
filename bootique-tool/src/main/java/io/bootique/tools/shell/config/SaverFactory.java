package io.bootique.tools.shell.config;

import io.bootique.tools.shell.template.*;
import io.bootique.tools.shell.util.PermissionsUtils;

public class SaverFactory {
    private Integer permissions;

    public SaverFactory() {
    }

    public void setPermissions(Integer permissions) {
        this.permissions = permissions;
    }

    public TemplateSaver getSaverWithType(SaverType saverType) {
        switch (saverType) {
            case FILE:
                return new TemplateFileSaver();
            case BINARY: {
                if (permissions != null)
                    return new BinaryContentSaver(PermissionsUtils.parsePermissions(permissions));
                return new BinaryContentSaver();
            }
            case SAFE_BINARY:
                return new SafeBinaryContentSaver();
            case DIR_ONLY:
                return new TemplateDirOnlySaver();
            default:
                throw new IllegalArgumentException("Unrecognizable saver type: " + saverType);
        }
    }
}
