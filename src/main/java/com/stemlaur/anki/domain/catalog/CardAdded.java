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

import com.stemlaur.anki.domain.common.DomainEvent;
import org.apache.commons.lang3.builder.ToStringBuilder;

import java.util.Objects;

/**
 * Event representing the fact that a card is added to a deck.
 */
@com.stemlaur.livingdocumentation.annotation.DomainEvent
public class CardAdded implements DomainEvent {
    private final String deckId;
    private final int cardId;
    private final String question;
    private final String answer;

    public CardAdded(final DeckId deckId,
                     final int cardId,
                     final String question,
                     final String answer) {
        this.deckId = deckId.getValue();
        this.cardId = cardId;
        this.question = question;
        this.answer = answer;
    }

    @Override
    public String getAggregateId() {
        return this.deckId;
    }

    public String getQuestion() {
        return question;
    }

    public String getAnswer() {
        return answer;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        final CardAdded cardAdded = (CardAdded) o;
        return deckId.equals(cardAdded.deckId) && cardId == cardAdded.cardId && question.equals(cardAdded.question) && answer.equals(cardAdded.answer);
    }

    @Override
    public int hashCode() {
        return Objects.hash(deckId, cardId, question, answer);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .append("deckId", deckId)
                .append("cardId", cardId)
                .append("question", question)
                .append("answer", answer)
                .toString();
    }
}
