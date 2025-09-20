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

import com.stemlaur.anki.domain.catalog.api.DeckCreated;
import com.stemlaur.anki.domain.common.AggregateRoot;
import com.stemlaur.anki.domain.common.DomainEvent;
import com.stemlaur.livingdocumentation.annotation.Entity;
import org.apache.commons.lang3.Validate;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Represents a deck of card.
 */
@Entity
public final class Deck implements AggregateRoot {
    private final DeckId id;
    private final DeckTitle title;
    private final transient List<DomainEvent> events = new ArrayList<>();
    private List<Card> cards = new ArrayList<>();
    private int cardIdCounter = 1;

    public Deck(DeckId deckId, DeckTitle deckTitle) {
        this.id = deckId;
        this.title = deckTitle;
        if (deckId == null) {
            throw new DeckIdIsRequired();
        }
        if (deckTitle == null) {
            throw new DeckTitleIsRequired();
        }
        this.events.add(new DeckCreated(deckId, deckTitle.getValue()));
    }

    public int addCard(final CardDetail cardDetail) {
        Validate.notNull(cardDetail);
        final int cardId = cardIdCounter;
        this.cards.add(new Card(cardId, cardDetail));
        cardIdCounter++;
        this.events.add(new CardAdded(this.id, cardId, cardDetail.getQuestion(), cardDetail.answer()));
        return cardId;
    }

    public void removeCard(final int id) {
        this.cards = this.cards.stream().filter(c -> c.id() != id).collect(Collectors.toList());
    }

    public String idString() {
        return this.id.getValue();
    }

    public String titleString() {
        return this.title.getValue();
    }

    public List<Card> getCards() {
        return Collections.unmodifiableList(this.cards);
    }

    @Override
    public List<DomainEvent> events() {
        List<DomainEvent> domainEvents = new ArrayList<>(this.events);
        this.events.clear();
        return domainEvents;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        final Deck deck = (Deck) o;

        return id.equals(deck.id);
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }
}
