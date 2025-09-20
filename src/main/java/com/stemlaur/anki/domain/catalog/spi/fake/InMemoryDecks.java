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
package com.stemlaur.anki.domain.catalog.spi.fake;

import com.stemlaur.anki.domain.catalog.Deck;
import com.stemlaur.anki.domain.catalog.spi.Decks;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

public final class InMemoryDecks implements Decks {
    private final List<Deck> decks = new ArrayList<>();

    @Override
    public void save(final Deck deck) {
        this.delete(deck.idString());
        this.decks.add(deck);
    }

    @Override
    public void delete(final String id) {
        findDeckImpl(id).ifPresent(this.decks::remove);
    }

    @Override
    public Optional<Deck> find(final String id) {
        return findDeckImpl(id);
    }

    @Override
    public Collection<Deck> findAll() {
        return Collections.unmodifiableCollection(decks);
    }

    private Optional<Deck> findDeckImpl(final String id) {
        return this.decks.stream().filter(deck -> deck.idString().equals(id))
                .findFirst();
    }
}
