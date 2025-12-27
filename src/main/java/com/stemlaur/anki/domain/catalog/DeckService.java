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

import com.stemlaur.anki.domain.catalog.api.AddCard;
import com.stemlaur.anki.domain.catalog.api.CreateDeck;
import com.stemlaur.anki.domain.catalog.api.FindDecks;
import com.stemlaur.anki.domain.catalog.api.RemoveDeck;
import com.stemlaur.anki.domain.catalog.spi.DeckIdFactory;
import com.stemlaur.anki.domain.catalog.spi.Decks;
import com.stemlaur.anki.domain.common.spi.DomainEvents;
import com.stemlaur.livingdocumentation.annotation.DomainService;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Service to manage decks.
 */
@DomainService
public class DeckService implements CreateDeck, RemoveDeck, AddCard, FindDecks {
    private final Decks decks;
    private final DeckIdFactory deckIdFactory;
    private final DomainEvents domainEvents;

    public DeckService(final Decks decks,
                       final DeckIdFactory deckIdFactory,
                       final DomainEvents domainEvents) {

        this.decks = decks;
        this.deckIdFactory = deckIdFactory;
        this.domainEvents = domainEvents;
    }

    @Override
    public String create(final String title) {
        final DeckId generatedId = this.deckIdFactory.generate();
        final Deck deck = new Deck(generatedId, new DeckTitle(title));
        this.decks.save(deck);
        this.domainEvents.publish(deck.events());
        return generatedId.getValue();
    }

    @Override
    public void remove(final String deckId) {
        this.decks.delete(
                this.decks.find(deckId)
                        .orElseThrow(() -> new DeckDoesNotExist(deckId))
                        .idString());
    }

    @Override
    public void addCard(final String deckId, final CardDetail cardDetail) {
        final Deck deck = this.decks.find(deckId).orElseThrow(() -> new DeckDoesNotExist(deckId));
        deck.addCard(cardDetail);
        this.decks.save(deck);

        this.domainEvents.publish(deck.events());
    }

    @Override
    public Optional<DeckSnapshot> byId(final String deckId) {
        return this.decks.find(deckId)
                .map(this::mapToSnapshot);
    }

    @Override
    public Collection<DeckSnapshot> all() {
        return this.decks.findAll()
                .stream()
                .map(this::mapToSnapshot)
                .collect(Collectors.toList());
    }

    private DeckSnapshot mapToSnapshot(Deck deck) {
        return new DeckSnapshot(
                deck.idString(),
                deck.titleString(),
                mapToSnapshot(deck.getCards()));
    }

    private List<CardSnapshot> mapToSnapshot(List<Card> cards) {
        return cards.stream()
                .map(c -> new CardSnapshot(c.detail().question(), c.detail().answer()))
                .collect(Collectors.toList());
    }

    //@formatter:on
}
