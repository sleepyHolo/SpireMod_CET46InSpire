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
package com.stemlaur.anki.domain.stats;

import com.stemlaur.livingdocumentation.annotation.ValueObject;

import java.util.Objects;

/**
 * Represent the total number of decks created in the system.
 */
@ValueObject
public class TotalNumberOfCreatedDecks {
    private final int value;

    public TotalNumberOfCreatedDecks(final int value) {
        if (value < 0) throw new NumberOfDecksNotPositive(value);
        this.value = value;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        final TotalNumberOfCreatedDecks that = (TotalNumberOfCreatedDecks) o;
        return value == that.value;
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }
}
