/*
 *   Licensed to ObjectStyle LLC under one
 *   or more contributor license agreements.  See the NOTICE file
 *   distributed with this work for additional information
 *   regarding copyright ownership.  The ObjectStyle LLC licenses
 *   this file to you under the Apache License, Version 2.0 (the
 *   "License"); you may not use this file except in compliance
 *   with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *   Unless required by applicable law or agreed to in writing,
 *   software distributed under the License is distributed on an
 *   "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 *   KIND, either express or implied.  See the License for the
 *   specific language governing permissions and limitations
 *   under the License.
 */

package io.bootique.tools.shell.content;

import io.bootique.command.CommandOutcome;
import io.bootique.tools.shell.JlineShell;
import io.bootique.tools.shell.Shell;
import io.bootique.tools.shell.template.TemplatePipeline;
import io.bootique.tools.shell.template.processor.MustacheTemplateProcessor;
import io.bootique.tools.shell.template.processor.TemplateProcessor;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import java.lang.reflect.Field;

import static org.junit.Assert.*;

public class ContentHandlerTest {

    private Shell shell = Mockito.mock(JlineShell.class);

    private ContentHandler contentHandler;

    @Before
    public void setup() {
        contentHandler = Mockito.spy(new ContentHandler() {
            @Override
            public CommandOutcome handle(NameComponents name) {
                return null;
            }
        });

        contentHandler.shell = shell;
    }


    @Test
    public void addPipeline() throws NoSuchFieldException, IllegalAccessException {
        contentHandler
                .addPipeline(TemplatePipeline
                        .builder()
                        .source("testSource.test")
                        .processor(new MustacheTemplateProcessor()));

        TemplatePipeline pipeline = contentHandler.pipelines.get(0);

        Field field = pipeline.getClass().getDeclaredField("processor");
        field.setAccessible(true);
        TemplateProcessor processor = (TemplateProcessor) field.get(pipeline);

        assertEquals(TemplatePipeline.class.getSimpleName(), pipeline.getClass().getSimpleName());
        assertEquals(MustacheTemplateProcessor.class.getSimpleName(), processor.getClass().getSimpleName());
    }

    @Test
    public void log() {
        String message = "testMessage";
        contentHandler.log(message);

        Mockito.verify(shell, Mockito.times(1)).println("@|green   <|@ " + message);
    }

}