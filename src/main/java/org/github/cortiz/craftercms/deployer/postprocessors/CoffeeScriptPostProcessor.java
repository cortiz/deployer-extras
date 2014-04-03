package org.github.cortiz.craftercms.deployer.postprocessors;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.craftercms.cstudio.publishing.PublishedChangeSet;
import org.craftercms.cstudio.publishing.exception.PublishingException;
import org.craftercms.cstudio.publishing.processor.PublishingProcessor;
import org.craftercms.cstudio.publishing.target.PublishingTarget;
import org.github.cortiz.craftercms.deployer.postprocessors.coffee.CoffeeScriptCompiler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Required;

/**
 * <p>Detects any coffee script and compiles it. Only change extension of the file (*.coffee to *.js)</p>
 * <p><b>Retains original coffeescript file</b></p>
 *
 * @author Carlos Ortiz
 * @version 1.0
 * @since 1.0
 */
public class CoffeeScriptPostProcessor implements PublishingProcessor {
    private static final String FILE_OUTPOSTFIX = ".js";
    private Logger log = LoggerFactory.getLogger(CoffeeScriptPostProcessor.class);
    private static final String FILE_POSTFIX = ".coffee";
    private String siteName;
    private boolean deleteOriginal;
    private CoffeeScriptCompiler compiler;

    public void init() throws Exception {
        compiler = new CoffeeScriptCompiler();
    }

    @Override
    public void doProcess(final PublishedChangeSet publishedChangeSet, final Map<String, String> parameters,
                          final PublishingTarget publishingTarget) throws PublishingException {

        try {
            compiler.createContext();
        } catch (URISyntaxException e) {
            log.error("Unable to load context", e);
            throw new PublishingException("Unable to create Coffee Compiler Context", e);
        }
        List<String> files = collectCoffeeFiles(publishedChangeSet);
        String root = Utils.getFilesRoot(publishingTarget, parameters, siteName);
        for (String file : files) {
            compileCoffee(root + File.separator + file);
        }
        if (deleteOriginal) {
            for (String file : files) {
                try {
                    Files.delete(Paths.get(file));
                } catch (IOException e) {
                    throw new PublishingException("Unable to delete files ", e);
                }
            }
        }
        if(publishedChangeSet.getDeletedFiles()!=null) {
            for (String delete : publishedChangeSet.getDeletedFiles()) {
                deleteCompile(root + File.separator + delete);
            }
        }
    }


    private void compileCoffee(final String file) {
        String outputFile = file.replaceAll(FILE_POSTFIX, FILE_OUTPOSTFIX);
        log.info("Processing {} to {}", file, outputFile);
        try {
            compiler.compile(file, outputFile);
        } catch (Exception e) {
            log.error("Unable to write " + file + " due DefaultConfigurableProvider error", e);
        }
        log.info("File {} was written", outputFile);
    }

    private void deleteCompile(final String file) {
        String outputFile = file.replaceAll(FILE_POSTFIX, FILE_OUTPOSTFIX);
        log.info("Processing {} to {}", file, outputFile);
        try {
            new File(outputFile).delete();
        } catch (Exception e) {
            log.error("Unable to delete " + file, e);
        }
        log.info("File {} was deleted", outputFile);
    }

    @Override
    public String getName() {
        return "Coffee Script Processor";
    }

    /**
     * <p>Gets all coffee scripts from the change set.</p>
     * <p>Files are filter by extension</p>
     *
     * @param publishedChangeSet change set to filter.
     * @return All coffee script files
     */
    private List<String> collectCoffeeFiles(final PublishedChangeSet publishedChangeSet) {
        log.debug("Getting Coffee files from {} and {}", publishedChangeSet.getCreatedFiles(),
            publishedChangeSet.getUpdatedFiles());
        List<String> files = new ArrayList<>();
        log.debug("Getting created files");
        for (String file : publishedChangeSet.getCreatedFiles()) {
            if (file.endsWith(FILE_POSTFIX)) {
                files.add(file);

            }
        }
        log.debug("Created Coffee files {}", files.size());
        log.debug("Getting updated  Coffee files");
        for (String file : publishedChangeSet.getUpdatedFiles()) {
            if (file.endsWith(FILE_POSTFIX)) {
                files.add(file);

            }
        }
        log.debug("Updated Coffee files {}", files.size());
        log.info("Found {} Coffee files to be process ", files.size());
        return files;
    }


    @Required
    public void setSiteName(String siteName) {
        this.siteName = siteName;
    }

    @Required
    public void setDeleteOriginal(final boolean deleteOriginal) {
        this.deleteOriginal = deleteOriginal;
    }


}
