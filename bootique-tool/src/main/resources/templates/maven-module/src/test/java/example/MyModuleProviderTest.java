package {{java.package}};

import io.bootique.test.junit.BQModuleProviderChecker;
import org.junit.Test;

public class {{module.name}}ProviderTest {

    @Test
    public void testAutoLoading() {
        BQModuleProviderChecker.testAutoLoadable({{module.name}}Provider.class);
    }
}
