package org.github.cortiz.craftercms.deployer.postprocessors;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.craftercms.cstudio.publishing.PublishedChangeSet;
import org.craftercms.cstudio.publishing.exception.PublishingException;
import org.craftercms.cstudio.publishing.processor.PublishingProcessor;
import org.craftercms.cstudio.publishing.target.PublishingTarget;
import org.lesscss.LessCompiler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Required;

/**
 * <p>Detects any Less script and compiles it. Only change extension of the file (*.Less to *.css)</p>
 * <p><b>Retains original Less script file</b></p>
 *
 * @author Carlos Ortiz
 * @version 1.0
 * @since 1.0
 */
public class LessPostProcessor implements PublishingProcessor {

    private static final String FILE_POSTFIX = ".less";
    private static final String FILE_OUTPOSTFIX = ".css";
    private boolean deleteOriginal;
    private LessCompiler lessCompiler;
    private Logger log = LoggerFactory.getLogger(LessPostProcessor.class);
    private boolean compress;
    private String siteName;

    @Required
    public void setDeleteOriginal(final boolean deleteOriginal) {
        this.deleteOriginal = deleteOriginal;
    }

    @Override
    public void doProcess(final PublishedChangeSet changeSet, final Map<String, String> parameters,
                          final PublishingTarget target) throws PublishingException {
        List<String> files = collectLessFiles(changeSet);
        String root = Utils.getFilesRoot(target, parameters, siteName);
        for (String file : files) {
            compileLess(root + File.separator + file);
        }
        if (deleteOriginal) {
            for (String file : files) {
                try {
                    Files.delete(Paths.get(root, file));
                } catch (IOException e) {
                    throw new PublishingException("Unable to delete original File", e);
                }
            }
        }

        if (changeSet.getDeletedFiles() != null) {
            for (String delete : changeSet.getDeletedFiles()) {
                deleteCompile(root + File.separator + delete);
            }
        }
    }

    @Override
    public String getName() {
        return "Less compiler Postprocessor";
    }

    public void init() {
        this.lessCompiler = new LessCompiler();
        lessCompiler.setCompress(compress);
        lessCompiler.setEncoding("UTF-8");
    }

    /**
     * <p>Gets all coffee scripts from the change set.</p>
     * <p>Files are filter by extension</p>
     *
     * @param publishedChangeSet change set to filter.
     * @return All coffee script files
     */
    private List<String> collectLessFiles(final PublishedChangeSet publishedChangeSet) {
        log.debug("Getting Less files from {} and {}", publishedChangeSet.getCreatedFiles(),
            publishedChangeSet.getUpdatedFiles());
        List<String> files = new ArrayList<>();
        log.debug("Getting created files");
        for (String file : publishedChangeSet.getCreatedFiles()) {
            if (file.endsWith(FILE_POSTFIX)) {
                files.add(file);
            }
        }
        log.debug("Created Less files {}", files.size());
        log.debug("Getting updated  Less files");
        for (String file : publishedChangeSet.getUpdatedFiles()) {
            if (file.endsWith(FILE_POSTFIX)) {
                files.add(file);
            }
        }
        log.debug("Updated Less files {}", files.size());
        log.info("Found {} Less files to be process ", files.size());
        return files;
    }

    private void compileLess(final String file) {
        String outputFile = file.replaceAll(FILE_POSTFIX, FILE_OUTPOSTFIX);
        log.info("Processing {} to {}", file, outputFile);
        try {
            lessCompiler.compile(new File(file), new File(outputFile));
        } catch (Exception e) {
            log.error("Unable to write " + file, e);
        }
        log.info("File {} was written", outputFile);
    }

    @Required
    public void setCompress(final boolean compress) {
        this.compress = compress;
    }

    @Required
    public void setSiteName(final String siteName) {
        this.siteName = siteName;
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
}
