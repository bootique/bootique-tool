package {{java.package}};

import io.bootique.test.junit.BQModuleProviderChecker;
import org.junit.Test;

public class ApplicationModuleProviderTest {

    @Test
    public void testAutoLoading() {
        BQModuleProviderChecker.testAutoLoadable(ApplicationModuleProvider.class);
    }
}
