# Crafter Cms deployer Extras
This project will extend the default features found in [CrafterCMS](https://github.com/craftercms/studio) deployer.
* [CoffeeScript]
## Added Features

### Coffee Script Compiler

This will compile your coffee script and deployed it as a .js file.
#### Usage

1. Create the postprocessor bean.
2. Added to the postprocessors list of the deployer target.

#### Example

```xml
  <bean id="CoffeePostProcessor"
         class="org.github.cortiz.craftercms.deployer.postprocessors.CoffeeScriptPostProcessor" init-method="init">
             <property name="deleteOriginal" value="false"/>
            <property name="siteName"><value>preview</value></property>
  </bean>
```

#### Properties
* deleteOriginal: Deletes the original .coffee script from the deployed target
* siteName: Name of the site.
