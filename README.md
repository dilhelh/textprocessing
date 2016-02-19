# Text Processing Utilities

## Build Status in Travis CI
[![Build Status](https://travis-ci.org/kgusarov/text-processing-utils.svg?branch=master)](https://travis-ci.org/kgusarov/text-processing-utils)

## Language Detection
###### Forked from [https://github.com/shuyo/language-detection](https://github.com/shuyo/language-detection)
One of the major changes here (except for some code style change and saying bye-bye to Mr. ```StringBuffer```) 
is the "unstatic'ing" of the ```DetectorFactory``` class. Now it has to be created via consructor that accepts 
a single parameter ```shortMessages``` that defines which corpus should be used:
* If parameter is set to ```true``` then language profiles that are generated from Twitter Corpus are used. These language profiles should prove better on short messages
* If parameter is set to ```false``` then language profiles that are generated from Wikipedia Corpus are used. These language profiles should prove better on long messages

Corpuses are now part of the language detection JAR file. Also please note that ```Detector``` instances obtained from 
the ```DetectorFactory``` are stateful - they keep both evaluated text as well as some additional information inside.

###### Example usage
```java
// Use short message corpus
final DetectorFactory detectorFactory = new DetectorFactory(true);
final Detector detector = detectorFactory.create();

detector.append("Some text to detect language for");
final String detectedLang = detector.detect();
```

## Text Analysis
Set of various tools for performing text analysis

###### TermExtractionService
Utilizes Language detector and [Apache Lucene](https://lucene.apache.org/) for term extraction from an arbitrary string.
Since ```TermExtractionService``` uses the ```DetectorFactory``` it also asks for a ```shortMessages``` parameter when constructing
an instance. Example usage can be found below:
```java
// Use short message corpus
final TermExtractionService service = new TermExtractionService(true);
final List<String> terms = service.getTerms("Some text to extract terms from");
```

###### TextCleanupService
Can be used both to remove some of the unwanted chars from the given text and extract Twitter-like entities: hashtags, cashtags,
mentions and urls. Example usage:
```java
// Remove characters that are responsible for the text direction or
// are invisible spaces that may interfere
final TextCleanupService service = new TextCleanupService();
final String cleanedUp = service.removeDirectionAndInvisibleChars("Some text with garbage here");

// Remove Twitter-like entities from text. If second parameter is set to true
// removeDirectionAndInvisibleChars() will be invoked before the entity cleanup
final String withoutEntities = service.removeTwitterEntities("@kgusarov Click me! #coolbuttons", true);

// It is also possible to get all the extracted entities with the cleaned up text
final Pair<String, List<Extractor.Entity>> withAndWithoutEntities = service.extractTwitterEntities("@kgusarov Click me! #coolbuttons", true);
```

###### TransliterationService
Utilizes [ICU4J](http://site.icu-project.org/home) for performing text transliteration. The service itself uses
[Commons Pool](https://commons.apache.org/proper/commons-pool/) for pooling ```Transliterator``` instances. Example usage:
```java
// Create service instance with given pool and transliterator configuration
final GenericObjectPoolConfig poolConfig = new GenericObjectPoolConfig();
poolConfig.setMaxIdle(4);
poolConfig.setMaxTotal(4);
poolConfig.setMinIdle(4);

final TransliterationService transliterationService =
    new TransliterationService("Any-Lower; SomeExampleReplacement; Any-Latin; NFD; [^\\p{Alnum}] Remove", poolConfig);

transliterationService.addTransliteratorConfiguration("SomeExampleReplacement", "ы > i;");

final String transliterated = transliterate("Мама мыла раму");
```

###### TextAnalysisService
Performs text analysis by utilizing other service found in this module. It accepts those services as a constructor
arguments and uses them to perform all the neccessary actions.
```java
final TransliterationService ts = TransliterationServiceFactory.create();
final TermExtractionService tes = new TermExtractionService(true);
final TextCleanupService tcs = new TextCleanupService();

final TextAnalysisService service = new TextAnalysisService(tes, tcs, ts);
final AnalysedText analysedText = service.analyse("Text to analyse", true);
```

## Thanks to
[Michael McCandless](https://github.com/mikemccand) and his awesome [language detection test](https://github.com/mikemccand/chromium-compact-language-detector/blob/master/test.py) that I've honestly have used.  

## License
Licensed under the Apache License, Version 2.0: [http://www.apache.org/licenses/LICENSE-2.0](http://www.apache.org/licenses/LICENSE-2.0)
