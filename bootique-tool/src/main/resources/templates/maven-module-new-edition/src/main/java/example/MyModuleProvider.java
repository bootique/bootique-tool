package {{java.package}};

{{#bq.di}}
import io.bootique.BQModuleProvider;
import io.bootique.di.BQModule;
{{/bq.di}}
{{^bq.di}}
import com.google.inject.Module;
import io.bootique.BQModuleProvider;
{{/bq.di}}

public class {{module.name}}Provider implements BQModuleProvider {

    @Override
    public {{#bq.di}}BQ{{/bq.di}}Module module() {
        return new {{module.name}}();
    }

}
