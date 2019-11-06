package example;

import com.google.inject.Module;
import io.bootique.BQModuleProvider;

public class MyModuleProvider implements BQModuleProvider {

    @Override
    public Module module() {
        return new MyModule();
    }

}
