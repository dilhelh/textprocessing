package org.kgusarov.textprocessing.maven.mojos;

/*
 * Copyright (C) 2016 Konstantin Gusarov
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;

import java.io.File;

/**
 * This mojo generates java classes from language detection json files.
 *
 * @author Konstantin Gusarov
 */
@Mojo(name = "generate", defaultPhase = LifecyclePhase.GENERATE_SOURCES)
public class GenerateLanguageDetectionSourcesMojo extends AbstractMojo {
    /**
     * Directory wherein generated source will be put; main, test, site, ... will be added implictly.
     */
    @Parameter(
            name = "outputDir",
            defaultValue = "${project.build.directory}/generated-sources",
            required = true
    )
    private File outputDir;

    /**
     * Directory where input files can be found
     */
    @Parameter(
            name = "inputDir",
            defaultValue = "${project.basedir}/src/main/language-json",
            required = true
    )
    private File inputDir;

    /**
     * Package name to contain generated sources
     */
    @Parameter(
            name = "packagePrefix",
            required = true
    )
    private String packagePrefix;

    @Override
    public void execute() throws MojoExecutionException, MojoFailureException {
        getLog().info(packagePrefix);
        getLog().info(inputDir.toString());
        getLog().info(outputDir.toString());
    }
}
