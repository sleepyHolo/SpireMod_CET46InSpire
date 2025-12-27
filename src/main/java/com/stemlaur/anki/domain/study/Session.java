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

import java.util.Optional;
import java.util.Set;

/**
 * Represents a play session of a user.
 */
@Entity
public final class Session {
    private final String id;
    private final Set<CardToStudy> cardsToStudy;

    public Session(final String id,
                   final Set<CardToStudy> cardsToStudy) {
        this.id = id;
        this.cardsToStudy = cardsToStudy;
    }

    public String id() {
        return this.id;
    }

    public Set<CardToStudy> cardsToStudy() {
        return this.cardsToStudy;
    }

    public Optional<CardToStudy> findCard(final String cardId) {
        return this.cardsToStudy.stream()
                .filter(cardToStudy -> cardToStudy.id().equals(cardId))
                .findFirst();
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        final Session session = (Session) o;

        return id.equals(session.id);
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }
}
