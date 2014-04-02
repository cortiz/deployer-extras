# Crafter Cms deployer Extras
This project will extend the default features found in [CrafterCMS](https://github.com/craftercms/studio) deployer.
## Added Features
### Coffee Script Compiler
This will compile your coffee script and deployed it as a .js file.
#### Usage
* Create the postprocessor bean.
```xml
  <bean id="CoffeePostProcessor"
         class="org.github.cortiz.craftercms.deployer.postprocessors.CoffeeScriptPostProcessor" init-method="init">
             <property name="deleteOriginal" value="false"/>
            <property name="siteName"><value>preview</value></property>
  </bean>
```
* Added to the wanted target in the postprocessor list.

#### Properties
* deleteOriginal
Deletes the original .coffee script from the deployed target
