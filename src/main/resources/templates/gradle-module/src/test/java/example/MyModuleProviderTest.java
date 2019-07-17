package example;

import io.bootique.test.junit.BQModuleProviderChecker;
import org.junit.Test;

public class MyModuleProviderTest {

    @Test
    public void testAutoLoading() {
        BQModuleProviderChecker.testAutoLoadable(MyModuleProvider.class);
    }
}
