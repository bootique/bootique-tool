package example;

import io.bootique.test.junit.BQTestFactory;
import org.junit.Rule;
import org.junit.Test;

import static org.junit.Assert.assertTrue;

public class ApplicationTest {

    @Rule
    public BQTestFactory testFactory = new BQTestFactory();

    @Test
    public void testApplication() {
        assertTrue(testFactory.app().autoLoadModules().run().isSuccess());
    }

} 
