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
public class LangDetectException extends RuntimeException {
    private final ErrorCode code;

    /**
     * @param code          Error code
     * @param message       Detailed error description
     */
    public LangDetectException(final ErrorCode code, final String message) {
        super(message);
        this.code = code;
    }

    /**
     * @param code          Error code
     * @param message       Detailed error description
     * @param cause         Exception that caused this one
     */
    public LangDetectException(final ErrorCode code, final String message, final Throwable cause) {
        super(message, cause);
        this.code = code;
    }

    /**
     * Get the error code
     *
     * @return the error code
     */
    public ErrorCode getCode() {
        return code;
    }
}
