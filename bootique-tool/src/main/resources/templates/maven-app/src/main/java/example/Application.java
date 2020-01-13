package {{java.package}};

{{#bq.di}}
import io.bootique.Bootique;
import io.bootique.di.BQModule;
import io.bootique.di.Binder;
{{/bq.di}}
{{^bq.di}}
import com.google.inject.Binder;
import com.google.inject.Module;
import io.bootique.Bootique;
{{/bq.di}}

public class Application implements {{#bq.di}}BQ{{/bq.di}}Module {

    public static void main(String[] args) {
        Bootique.app(args)
                .autoLoadModules()
                .exec()
                .exit();
    }

    @Override
    public void configure(Binder binder) {
        // TODO: configure services
    }
}