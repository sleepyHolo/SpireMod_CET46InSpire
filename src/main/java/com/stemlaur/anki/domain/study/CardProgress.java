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

import java.time.LocalDateTime;

/**
 * Represents the progress of a card in a session.
 */
@Entity
public final class CardProgress {
    private static final Score MINIMUM_SCORE = new Score(1);
    private static final int GREEN_MULTIPLICAND = 10;
    private static final int ORANGE_DIVISOR = 2;
    private static final int RED_DIVISOR = 5;

    private final String cardId;
    private Score score;
    private LocalDateTime lastEvaluationAt;

    public CardProgress(final String cardId, final LocalDateTime lastEvaluationAt, final Score score) {
        this.cardId = cardId;
        this.lastEvaluationAt = lastEvaluationAt;
        this.score = score;
    }

    public static CardProgress init(String cardId) {
        return new CardProgress(cardId, null, MINIMUM_SCORE);
    }

    public CardProgress updateProgress(final Opinion opinion,
                                       final LocalDateTime now) {
        this.lastEvaluationAt = now;
        switch (opinion) {
            case GREEN:
                this.score = this.score.multipliedBy(GREEN_MULTIPLICAND);
                break;
            case ORANGE:
                this.score = this.score.dividedBy(ORANGE_DIVISOR);
                break;
            default:
                this.score = this.score.dividedBy(RED_DIVISOR);
                break;
        }
        if (score.value() < MINIMUM_SCORE.value()) {
            this.score = MINIMUM_SCORE;
        }
        return this;
    }

    LocalDateTime lastEvaluationAt() {
        return this.lastEvaluationAt;
    }

    public String id() {
        return this.cardId;
    }

    public Score score() {
        return this.score;
    }
}
