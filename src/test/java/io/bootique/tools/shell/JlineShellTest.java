package io.bootique.tools.shell;

import org.junit.Test;

import static org.junit.Assert.*;

public class JlineShellTest {

    @Test
    public void compactPackageName() {
        String name1 = JlineShell.compactPackageName("io.bootique.Name");
        assertEquals("io.bootique.Name", name1);

        String name2 = JlineShell.compactPackageName("org.apache.cayenne.demo.test.long.Name");
        assertEquals("o.a.cayenne.demo.test.long.Name", name2);

        String name3 = JlineShell
                .compactPackageName("org.apache.cayenne.demo.test.long.long.long.long.NameOfSomeComplexComponent");
        assertEquals("o.a.c.d.t.l.l.l.l.NameOfSomeComplexComponent", name3);
    }
}