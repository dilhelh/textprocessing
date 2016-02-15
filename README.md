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

## Thanks to
[Michael McCandless](https://github.com/mikemccand) and his awesome [language detection test](https://github.com/mikemccand/chromium-compact-language-detector/blob/master/test.py) that I've honestly have used.  

## License
Licensed under the Apache License, Version 2.0: [http://www.apache.org/licenses/LICENSE-2.0](http://www.apache.org/licenses/LICENSE-2.0)
