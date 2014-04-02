package org.github.cortiz.craftercms.deployer.postprocessors.coffee;

import org.testng.annotations.Test;

/**
 * Created by cortiz on 3/31/14.
 */
public class CoffeeScriptCompilerTest {

    @Test
    public void testCompiler() throws Exception {
        CoffeeScriptCompiler compiler = new CoffeeScriptCompiler();
       // compiler.init();
        String source = getClass().getResource("/hello.coffee").getPath();
        String dest = getClass().getResource("/hello.coffee").getPath().replace(".coffee",".js");
        compiler.compile(source,dest);
    }
}
