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
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

/**
 * This value object represents the unique identified or a deck.
 */
public class DeckId {
    private final String value;

    private DeckId(String value) {
        this.value = value;
    }

    public static DeckId from(final String id) {
        return new DeckId(id);
    }

    public String getValue() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        DeckId deckId = (DeckId) o;

        return new EqualsBuilder().append(value, deckId.value).isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37).append(value).toHashCode();
    }

    @Override
    public String toString() {
        return value;
    }
}
