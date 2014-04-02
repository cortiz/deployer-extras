package org.github.cortiz.craftercms.deployer.postprocessors;

import java.io.File;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.craftercms.cstudio.publishing.servlet.FileUploadServlet;
import org.craftercms.cstudio.publishing.target.PublishingTarget;

/**
 * Collection of reusable util functions to be use across multiple processors.
 * @author Carlos Ortiz.
 * @version 1.0
 * @since  1.0
 */
public final class Utils {

    /**
     * Prevents instances of this class.
     */
    private Utils(){}

    /**
     * Gets the root path where files are been deployed.
     * @param publishingTarget PublishingTarget.
     * @param parameters Parameters.
     * @param siteName Site name.
     * @return Returns DefaultConfigurableProvider string that represents the path where the files are been deploy (aka root crafter path).
     */
    public static String getFilesRoot(final PublishingTarget publishingTarget,final Map<String,String> parameters,
                                      final String siteName){
        String root = publishingTarget.getParameter(FileUploadServlet.CONFIG_ROOT);
        String contentFolder = publishingTarget.getParameter(FileUploadServlet.CONFIG_CONTENT_FOLDER);
        String siteId = parameters.get(FileUploadServlet.PARAM_SITE);
        if (StringUtils.isEmpty(siteId)) {
            siteId = siteName;
        }

        root += File.separator + contentFolder;
        if (org.springframework.util.StringUtils.hasText(siteId)) {
            root = root.replaceAll(FileUploadServlet.CONFIG_MULTI_TENANCY_VARIABLE, siteId);
        }
        return root;
    }
}
