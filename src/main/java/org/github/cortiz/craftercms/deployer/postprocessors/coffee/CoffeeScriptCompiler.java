package org.github.cortiz.craftercms.deployer.postprocessors.coffee;


import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.mozilla.javascript.Context;
import org.mozilla.javascript.Scriptable;
import org.mozilla.javascript.commonjs.module.Require;
import org.mozilla.javascript.commonjs.module.provider.StrongCachingModuleScriptProvider;
import org.mozilla.javascript.commonjs.module.provider.UrlModuleSourceProvider;

/**
 *
 */
public class CoffeeScriptCompiler {

    private Context context;
    private Scriptable globalScope;
    private Scriptable coffeeScript;

    public CoffeeScriptCompiler() throws URISyntaxException {

    }

    public void createContext() throws URISyntaxException {
        context = Context.enter();
        context.setOptimizationLevel(9);
        globalScope = context.initStandardObjects();//new JsRuntimeSupport());
        List<URI> requireUrls = Collections.unmodifiableList(Arrays.asList(getClass().getResource("/coffee-script/")
            .toURI()));
        final UrlModuleSourceProvider urlModuleSourceProvider = new UrlModuleSourceProvider(requireUrls, null);
        StrongCachingModuleScriptProvider moduleScriptProvider = new StrongCachingModuleScriptProvider
            (urlModuleSourceProvider);
        Require require = new Require(context, context.initStandardObjects(), moduleScriptProvider, null, null, true);
        coffeeScript = require.requireMain(context, "coffee-script");
    }

    public void compile(final String source,final String dest) throws IOException {

        Scriptable compileScope = context.newObject(coffeeScript);
        compileScope.setParentScope(coffeeScript);
        compileScope.put("script", compileScope,readFile(source));
        String out = (String)context.evaluateString(compileScope, "compile(script);", source, 0, null);
        try(BufferedWriter writer = new BufferedWriter(new FileWriter(dest))){
            writer.write(out);
            writer.flush();
        }
    }

    private String readFile(String path) throws IOException {
        byte[] encoded = Files.readAllBytes(Paths.get(path));
        return Charset.forName("UTF-8").decode(ByteBuffer.wrap(encoded)).toString();
    }
}
