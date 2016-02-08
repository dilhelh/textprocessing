package com.cybozu.labs.langdetect;

import com.cybozu.labs.langdetect.util.LangProfile;
import com.cybozu.labs.langdetect.util.NGram;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.kgusarov.textprocessing.annotations.LanguageProfile;
import org.reflections.Reflections;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.*;

/**
 * Language Detector Factory Class
 * <p/>
 * This class manages an initialization and constructions of {@link Detector}.
 * <p/>
 * When the language detection,
 * construct Detector instance via {@link DetectorFactory#create()}.
 * See also {@link Detector}'s sample code.
 * <p/>
 * <ul>
 * <li>4x faster improvement based on Elmer Garduno's code. Thanks!</li>
 * </ul>
 *
 * @author Nakatani Shuyo
 * @author Konstantin Gusarov
 * @see Detector
 */
@SuppressWarnings("unchecked")
public class DetectorFactory {
    private static final Logger LOGGER = LoggerFactory.getLogger(DetectorFactory.class);

    final Map<String, double[]> languageProbabilityMap = Maps.newHashMap();
    final List<String> languages = Lists.newArrayList();

    /**
     * Create new {@code DetectorFactory}
     *
     * @param shortMessages             Should this detector factory use short message profiles
     * @throws LangDetectException      In case ini
     */
    public DetectorFactory(final boolean shortMessages) {
        final Reflections reflections = Reflections.collect();
        final Set<Class<?>> languageProfiles = reflections.getTypesAnnotatedWith(LanguageProfile.class);

        final List<Class<? extends LangProfile>> profileClasses = Lists.newArrayList();
        for (final Class<?> profile : languageProfiles) {
            if (!LangProfile.class.isAssignableFrom(profile)) {
                throw new LangDetectException(ErrorCode.FAILED_TO_INITIALIZE,
                        profile + " is annotated as language profile while it isn't");
            }

            final LanguageProfile annotation = profile.getAnnotation(LanguageProfile.class);
            if (shortMessages != annotation.forShortMessages()) {
                LOGGER.trace("Skipping {} profile - it is meant for different length messages", profile);
                continue;
            }

            profileClasses.add((Class<? extends LangProfile>) profile);
        }

        final int languageCount = profileClasses.size();
        for (int i = 0; i < languageCount; i++) {
            final Class<? extends LangProfile> profile = profileClasses.get(i);
            addProfile(profile, i, languageCount);
        }
    }

    private void addProfile(final Class<? extends LangProfile> clazz, final int index, final int languageCount) {
        try {
            final Constructor<? extends LangProfile> ctr = clazz.getConstructor();
            final LangProfile profileInstance = ctr.newInstance();

            addProfile(profileInstance, index, languageCount);
        } catch (final InvocationTargetException | NoSuchMethodException | IllegalAccessException | InstantiationException e) {
            throw new LangDetectException(ErrorCode.FAILED_TO_INITIALIZE,
                    "Failed to instantiate language profile class " + clazz, e);
        }
    }

    /**
     * Merge information from language profile instance into this factory
     *
     * @param profile                   Language profile to be merged
     * @param languageCount             Total amount of language profiles
     * @param index                     Index of language profile being added
     * @throws LangDetectException
     */
    private void addProfile(final LangProfile profile, final int index, final int languageCount) {
        final String language = profile.getName();
        if (languages.contains(language)) {
            throw new LangDetectException(ErrorCode.DUPLICATE_LANGUAGE, language + " language profile is already defined");
        }

        languages.add(language);
        final Map<String, Integer> frequencies = profile.getFrequencies();

        for (final Map.Entry<String, Integer> entry : frequencies.entrySet()) {
            final String ngram = entry.getKey();

            if (!languageProbabilityMap.containsKey(ngram)) {
                languageProbabilityMap.put(ngram, new double[languageCount]);
            }

            final int length = ngram.length();
            final int[] nGramCount = profile.getNGramCount();
            if ((length >= 1) && (length <= NGram.MAX_NGRAM_LENGTH)) {
                final Double count = entry.getValue().doubleValue();
                final double probability = count / nGramCount[length - 1];

                languageProbabilityMap.get(ngram)[index] = probability;
            } else {
                LOGGER.warn("Invalid n-gram in language profile: {}", ngram);
            }
        }
    }

    /**
     * Construct Detector instance
     *
     * @return          Detector instance
     * @throws          LangDetectException
     */
    public Detector create() {
        return createDetector();
    }

    /**
     * Construct Detector instance with smoothing parameter
     *
     * @param alpha         smoothing parameter (default value = 0.5)
     * @return              Detector instance
     * @throws              LangDetectException
     */
    public Detector create(final double alpha) {
        final Detector detector = createDetector();
        detector.setAlpha(alpha);
        return detector;
    }

    private Detector createDetector() {
        if (languages.isEmpty()) {
            throw new LangDetectException(ErrorCode.PROFILE_NOT_LOADED, "No language profile classes found");
        }

        return new Detector(this);
    }
}
