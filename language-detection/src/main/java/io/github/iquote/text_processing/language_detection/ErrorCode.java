package io.github.iquote.text_processing.language_detection;

/*
 * Copyright (C) 2010-2014 Cybozu Labs, 2016 Konstantin Gusarov
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

/**
 * @author Nakatani Shuyo
 * @author Konstantin Gusarov
 */
public enum ErrorCode {
    NO_TEXT,
    FORMAT,
    DUPLICATE_LANGUAGE,
    PROFILE_NOT_LOADED,
    CANNOT_DETECT,
    CANNOT_OPEN_TRAIN_DATA,
    TRAIN_DATA_FORMAT,
    INIT_PARAM,
    FAILED_TO_INITIALIZE,
    ;
}
