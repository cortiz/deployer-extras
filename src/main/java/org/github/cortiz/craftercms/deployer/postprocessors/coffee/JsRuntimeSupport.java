package org.github.cortiz.craftercms.deployer.postprocessors.coffee;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.mozilla.javascript.Context;
import org.mozilla.javascript.Function;
import org.mozilla.javascript.Scriptable;
import org.mozilla.javascript.ScriptableObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ClassPathResource;

/**
 * Created by cortiz on 3/31/14.
 */
public class JsRuntimeSupport extends ScriptableObject {
    private static final long serialVersionUID = 1L;
    private static Logger logger = LoggerFactory.getLogger(JsRuntimeSupport.class);

    private static final boolean silent = false;

    @Override
    public String getClassName() {
        return "test";
    }

    public static void print(Context cx, Scriptable thisObj, Object[] args, Function funObj) {
        if (silent) {
            return;
        }
        for (int i = 0; i < args.length; i++) {
            logger.info(Context.toString(args[i]));
        }
    }

    public static void load(Context cx, Scriptable thisObj, Object[] args,
                            Function funObj) throws FileNotFoundException, IOException {
        JsRuntimeSupport shell = (JsRuntimeSupport)getTopLevelScope(thisObj);
        for (int i = 0; i < args.length; i++) {
            logger.info("Loading file " + Context.toString(args[i]));
            shell.processSource(cx, Context.toString(args[i]));
        }
    }

    private void processSource(Context cx, String filename) throws FileNotFoundException, IOException {
        cx.evaluateReader(this, new InputStreamReader(getInputStream(filename)), filename, 1, null);
    }

    private InputStream getInputStream(String file) throws IOException {
        return new ClassPathResource(file).getInputStream();
    }
}
