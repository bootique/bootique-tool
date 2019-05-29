package example;

import com.google.inject.Module;
import io.bootique.Bootique;

public class Application implements Module {

    public static void main(String[] args) throws Exception {
        Bootique.app(args)
                .module(Application.class)
                .autoLoadModules()
                .exec()
                .exit();
    }

    @Override
    public void configure(Binder binder) {
        // TODO: configure services
    }
}