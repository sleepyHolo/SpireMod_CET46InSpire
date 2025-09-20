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
package com.stemlaur.anki.domain.study.spi.fake;

import com.stemlaur.anki.domain.study.CardProgress;
import com.stemlaur.anki.domain.study.spi.CardProgresses;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public final class InMemoryCardProgresses implements CardProgresses {
    private final List<CardProgress> cardProgresses = new ArrayList<>();

    @Override
    public Optional<CardProgress> findCardProgressById(final String id) {
        return this.cardProgresses.stream().filter(cardProgress -> cardProgress.id().equals(id))
                .findFirst();
    }

    @Override
    public void save(final CardProgress cardProgress) {
        this.cardProgresses.add(cardProgress);
    }
}
