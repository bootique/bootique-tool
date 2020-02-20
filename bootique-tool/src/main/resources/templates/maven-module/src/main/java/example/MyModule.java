package {{java.package}};

{{#bq.di}}
import io.bootique.di.BQModule;
import io.bootique.di.Binder;
{{/bq.di}}
{{^bq.di}}
import com.google.inject.Binder;
import com.google.inject.Module;
{{/bq.di}}

public class {{module.name}} implements {{#bq.di}}BQ{{/bq.di}}Module {

    @Override
    public void configure(Binder binder) {
        // TODO: configure services
    }
}