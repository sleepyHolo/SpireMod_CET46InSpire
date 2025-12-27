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
package com.stemlaur.anki.domain.catalog;

import com.stemlaur.livingdocumentation.annotation.ValueObject;
import lombok.Getter;

import java.util.Objects;

/**
 * This value object represents the question and its answer from a card.
 */
@ValueObject
@Getter
public final class CardDetail {
    private final String question;
    private final String answer;

    public CardDetail(final String question, final String answer) {
        this.question = question;
        this.answer = answer;
    }

    public String question() {
        return this.question;
    }

    public String answer() {
        return this.answer;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        final CardDetail that = (CardDetail) o;

        if (!Objects.equals(question, that.question)) return false;
        return Objects.equals(answer, that.answer);
    }

    @Override
    public int hashCode() {
        int result = question != null ? question.hashCode() : 0;
        result = 31 * result + (answer != null ? answer.hashCode() : 0);
        return result;
    }
}
