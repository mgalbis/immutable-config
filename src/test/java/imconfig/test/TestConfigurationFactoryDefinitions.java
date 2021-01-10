/**
 * @author Luis Iñesta Gelabert - luiinge@gmail.com
 */
package imconfig.test;


import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import java.nio.file.Path;
import org.junit.Test;
import imconfig.Configuration;
import imconfig.ConfigurationException;
import imconfig.ConfigurationFactory;


public class TestConfigurationFactoryDefinitions {


    private final ConfigurationFactory factory = ConfigurationFactory.instance();
    private final Path definitionPath = Path.of("src", "test", "resources", "definition.yaml");


    @Test
    public void testBuildEmptyConfigurationWithDefinitionFromURI() {
        var conf = factory.accordingDefinitionsFromURI(definitionPath.toUri());
        assertConfiguration(conf);
    }

    @Test
    public void testBuildEmptyConfigurationWithDefinitionFromPath() {
        var conf = factory.accordingDefinitionsFromPath(definitionPath);
        assertConfiguration(conf);
    }


    @Test
    public void testAttachDefinitionFromURI() {
        var conf = factory.empty().accordingDefinitionsFromURI(definitionPath.toUri());
        assertConfiguration(conf);
    }


    @Test
    public void testAttachDefinitionFromPath() {
        var conf = factory.empty().accordingDefinitionsFromPath(definitionPath);
        assertConfiguration(conf);
    }


    @Test
    public void testConfigurationValidation() {
        var conf = factory
            .fromPairs("defined.property.min-max-number", "6")
            .accordingDefinitionsFromPath(definitionPath);
        assertThat(conf.validation("defined.property.min-max-number"))
        .contains("Integer number between 2 and 3");
    }


    private void assertConfiguration(Configuration conf) {
        assertThat(conf.getDefinitions()).hasSize(6);
        assertThat(conf.getDefinition("defined.property.required")).isNotEmpty();
        assertThat(conf.getDefinition("defined.property.with-default-value")).isNotEmpty();
        assertThat(conf.getDefinition("defined.property.regex-text")).isNotEmpty();
        assertThat(conf.getDefinition("defined.property.min-max-number")).isNotEmpty();
        assertThat(conf.getDefinition("defined.property.enumeration")).isNotEmpty();
        assertThat(conf.getDefinition("defined.property.boolean")).isNotEmpty();

        assertThat(conf.getDefinition("undefined.property")).isEmpty();

        assertThat(conf.get("defined.property.regex-text", String.class)).isEmpty();

        assertThatCode(()->conf.get("defined.property.required", Integer.class))
            .isExactlyInstanceOf(ConfigurationException.class)
            .hasMessage("Property 'defined.property.required' is required but has no value");

        assertThat(conf.get("defined.property.with-default-value", Integer.class)).hasValue(5);
    }

}
