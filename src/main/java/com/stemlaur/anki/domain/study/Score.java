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

import com.stemlaur.livingdocumentation.annotation.ValueObject;
import org.apache.commons.lang3.Validate;

/**
 * This value object represents the score of a card in the session of the player.
 */
@ValueObject
public final class Score {
    private final int score;

    public Score(final int score) {
        this.score = score;
    }

    public Score multipliedBy(final int multiplicand) {
        return new Score(score * multiplicand);
    }

    public Score dividedBy(final int divisor) {
        Validate.isTrue(divisor > 0);
        return new Score(score / divisor);
    }

    public int value() {
        return score;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        final Score score1 = (Score) o;

        return score == score1.score;
    }

    @Override
    public int hashCode() {
        return score;
    }
}
