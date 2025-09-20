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

import com.stemlaur.livingdocumentation.annotation.Entity;

/**
 * Represents a card in a deck. It contains a question and an answer.
 */
@Entity
public class Card {
    private final int id;
    private final CardDetail detail;

    Card(final int id, final CardDetail detail) {
        this.id = id;
        this.detail = detail;
    }

    public int id() {
        return this.id;
    }

    public CardDetail detail() {
        return this.detail;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        final Card card = (Card) o;

        return id == card.id;
    }

    @Override
    public int hashCode() {
        return id;
    }
}
