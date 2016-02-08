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

import com.cybozu.labs.langdetect.util.LangProfile;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Maps;
import com.sun.codemodel.*;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.text.WordUtils;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.kgusarov.textprocessing.annotations.LanguageProfile;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * This mojo generates java classes from language detection json files.
 *
 * @author Konstantin Gusarov
 */
@Mojo(name = "generate", defaultPhase = LifecyclePhase.GENERATE_SOURCES)
public class GenerateLanguageDetectionSourcesMojo extends AbstractMojo {
    private static final String FAILED_TO_WRITE_OUT_GENERATED_CODE_FILES = "Failed to write out generated code files";
    private static final String PROFILE_NAME_FIELD = "NAME";
    private static final String PROFILE_INITIAL_FREQS_FIELD = "FREQUENCIES";
    private static final String PROFILE_NGRAM_COUNT_INFO_FIELD = "NGRAM_COUNT";

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

    /**
     * Additional comment to include in each generated file
     */
    @Parameter(
            name = "additionalComment",
            required = false
    )
    private String additionalComment;

    /**
     * This parameter defines if set of generated classes is meant for short messages
     */
    @Parameter(
            name = "shortMessages",
            required = true
    )
    private boolean shortMessages;

    @Override
    public void execute() throws MojoExecutionException, MojoFailureException {
        final JCodeModel codeModel = new JCodeModel();

        if (!inputDir.isDirectory()) {
            throw new MojoFailureException("Invalid input directory specified");
        }

        final ObjectMapper objectMapper = new ObjectMapper();
        final File[] files = inputDir.listFiles();
        if (files == null) {
            throw new MojoFailureException("Failed to get file list from directory");
        }

        for (final File file : files) {
            processLangProfileFile(objectMapper, file, codeModel);
        }

        try {
            if (!outputDir.exists()) {
                FileUtils.forceMkdir(outputDir);
            }

            codeModel.build(outputDir);
        } catch (final IOException e) {
            getLog().error(FAILED_TO_WRITE_OUT_GENERATED_CODE_FILES, e);
            throw new MojoFailureException(FAILED_TO_WRITE_OUT_GENERATED_CODE_FILES, e);
            // https://maven.apache.org/plugin-developers/plugin-testing.html
        }
    }

    private void processLangProfileFile(final ObjectMapper objectMapper, final File file, final JCodeModel codeModel) throws MojoFailureException {
        try {
            final LangProfileDocument langProfileDocument = objectMapper.readValue(file, LangProfileDocument.class);
            final JDefinedClass clazz = generateClass(file, codeModel);

            generateSimpleConstants(codeModel, langProfileDocument, clazz);
            generateFreqConstant(codeModel, langProfileDocument, clazz);

            clazz.constructor(JMod.PUBLIC)
                    .body()
                    .directStatement("super(" +
                            PROFILE_NAME_FIELD + ", " +
                            PROFILE_INITIAL_FREQS_FIELD + ", " +
                            PROFILE_NGRAM_COUNT_INFO_FIELD +
                            ");");
        } catch (final IOException e) {
            getLog().error(e);
            throw new MojoFailureException("Incorrect input file: " + file, e);
        } catch (final JClassAlreadyExistsException e) {
            getLog().error(e);
            throw new MojoFailureException("Failed to generate class file: " + file, e);
        }
    }

    private void generateFreqConstant(final JCodeModel codeModel, final LangProfileDocument langProfileDocument,
                                      final JDefinedClass clazz) {
        final JClass stringClass = codeModel.ref(String.class);
        final JClass integerClass = codeModel.ref(Integer.class);
        final JClass mapClass = codeModel.ref(Map.class);
        final JClass mapsClass = codeModel.ref(Maps.class);

        final JClass freqClass = mapClass.narrow(stringClass, integerClass);
        final JInvocation freqFieldInit = mapsClass.staticInvoke("newHashMap");

        final JFieldVar freqConstField = clazz.field(JMod.FINAL | JMod.STATIC | JMod.PRIVATE, freqClass,
                PROFILE_INITIAL_FREQS_FIELD, freqFieldInit);

        final Map<String, Integer> frequencies = langProfileDocument.getFrequencies();
        final Set<Map.Entry<String, Integer>> entries = frequencies.entrySet();

        final List<Map.Entry<String, Integer>> sorted = entries.stream()
                .sorted((a, b) -> Integer.compare(a.getValue(), b.getValue()))
                .collect(Collectors.toList());

        for (final Map.Entry<String, Integer> entry : sorted) {
            final String k = entry.getKey();
            final Integer v = entry.getValue();

            final JExpression key = JExpr.lit(k);
            final JExpression value = JExpr.lit(v);

            final JInvocation invocation = freqConstField.invoke("put").arg(key).arg(value);
            clazz.init().add(invocation);
        }
    }

    private void generateSimpleConstants(final JCodeModel codeModel, final LangProfileDocument langProfileDocument,
                                         final JDefinedClass clazz) {
        final JExpression profileName = JExpr.lit(langProfileDocument.getName());
        final JType intClass = codeModel.INT;
        final JArray nGramCount = JExpr.newArray(intClass);

        Arrays.stream(langProfileDocument.getnGramCount())
                .mapToObj(JExpr::lit)
                .forEach(nGramCount::add);

        clazz.field(JMod.FINAL | JMod.STATIC | JMod.PRIVATE, String.class, PROFILE_NAME_FIELD, profileName);
        clazz.field(JMod.FINAL | JMod.STATIC | JMod.PRIVATE, int[].class, PROFILE_NGRAM_COUNT_INFO_FIELD, nGramCount);
    }

    private JDefinedClass generateClass(final File file, final JCodeModel codeModel) throws JClassAlreadyExistsException {
        final String name = file.getName();
        final String classNamePostfix = WordUtils.capitalizeFully(name, '-')
                .replace("-", "");
        final String className = packagePrefix + ".LangProfile" + classNamePostfix;
        final JDefinedClass clazz = codeModel._class(className);

        clazz._extends(LangProfile.class);
        clazz.annotate(LanguageProfile.class)
                .param("forShortMessages", JExpr.lit(shortMessages));

        final String comment = String.format("This is a generated class.%n");
        clazz.javadoc().append(comment);

        if (!StringUtils.isEmpty(additionalComment)) {
            clazz.javadoc().append(additionalComment);
        }

        return clazz;
    }
}
