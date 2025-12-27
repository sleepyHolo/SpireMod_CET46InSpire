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

import com.stemlaur.anki.domain.study.spi.CardProgresses;
import com.stemlaur.livingdocumentation.annotation.DomainService;

/**
 * Service to retrieve the progress of a specific card.
 */
@DomainService
public class CardProgressService {
    private final CardProgresses cardProgresses;

    public CardProgressService(final CardProgresses cardProgresses) {

        this.cardProgresses = cardProgresses;
    }

    public CardProgress findByCardToStudyId(final String id) {
        return this.cardProgresses.findCardProgressById(id).orElse(CardProgress.init(id));
    }

    public void save(final CardProgress cardProgress) {
        this.cardProgresses.save(cardProgress);
    }
}
