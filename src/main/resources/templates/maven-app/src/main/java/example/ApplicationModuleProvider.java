package example;

import com.google.inject.Module;
import io.bootique.BQModuleProvider;

public class ApplicationModuleProvider implements BQModuleProvider {

    @Override
    public Module module() {
        return new Application();
    }

}
