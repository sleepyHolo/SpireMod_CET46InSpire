/*
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package com.stemlaur.anki.domain.study;

import com.stemlaur.livingdocumentation.annotation.Entity;

/**
 * Represents a card being studied.
 */
@Entity
public final class CardToStudy {
    private final String id;
    private final String question;
    private final String answer;

    public CardToStudy(final String id, final String question, final String answer) {
        this.id = id;
        this.question = question;
        this.answer = answer;
    }

    public String id() {
        return id;
    }

    public String question() {
        return question;
    }

    public String answer() {
        return this.answer;
    }
}
