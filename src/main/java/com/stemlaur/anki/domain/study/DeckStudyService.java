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

import com.stemlaur.anki.domain.catalog.api.FindDecks;
import com.stemlaur.anki.domain.catalog.api.FindDecks.DeckSnapshot;
import com.stemlaur.anki.domain.study.api.StudyDeck;
import com.stemlaur.anki.domain.study.spi.Sessions;
import com.stemlaur.livingdocumentation.annotation.DomainService;

import java.time.Clock;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

import static java.util.Optional.empty;

/**
 * Service to study decks.
 */
@DomainService
public class DeckStudyService implements StudyDeck {
    private final FindDecks findDecks;
    private final SessionIdFactory sessionIdFactory;
    private final Sessions sessions;
    private final CardProgressService cardProgressService;
    private final Clock clock;

    public DeckStudyService(final FindDecks findDecks,
                            final CardProgressService cardProgressService,
                            final SessionIdFactory sessionIdFactory,
                            final Sessions sessions,
                            final Clock clock) {
        this.findDecks = findDecks;
        this.sessionIdFactory = sessionIdFactory;
        this.sessions = sessions;
        this.cardProgressService = cardProgressService;
        this.clock = clock;
    }

    public String startStudySession(final String deckId) {
        final String sessionId = this.sessionIdFactory.create();
        final DeckSnapshot deckSnapshot = this.findDeck(deckId);
        this.sessions.save(new Session(sessionId, this.convertCardsToCardsToStudy(deckSnapshot)));
        return sessionId;
    }

    private DeckSnapshot findDeck(final String deckId) {
        final DeckSnapshot deckSnapshot = this.findDecks.byId(deckId).orElseThrow(() -> new DeckDoesNotExist(deckId));
        if (deckSnapshot.getCards().isEmpty()) {
            throw new DeckDoesNotContainAnyCards(deckId);
        }
        return deckSnapshot;
    }

    private Set<CardToStudy> convertCardsToCardsToStudy(final DeckSnapshot deckSnapshot) {
        return deckSnapshot.getCards().stream()
                .map(c -> new CardToStudy(UUID.randomUUID().toString(), c.getQuestion(), c.getAnswer()))
                .collect(Collectors.toSet());
    }

    public Optional<CardToStudy> nextCardToStudy(final String sessionId) {
        final Session session = this.sessions.find(sessionId).orElseThrow(() -> new SessionDoesNotExist(sessionId));
        final Optional<CardProgress> randomCardProgressWithMinimalScore =
                this.findRadomCardProgressWithLowestScore(session.cardsToStudy());
        if (!randomCardProgressWithMinimalScore.isPresent()) {
            return empty();
        }
        return getCardToStudyById(session.cardsToStudy(), randomCardProgressWithMinimalScore.orElseThrow(() -> new NoSuchElementException("No value present")).id());
    }

    private Optional<CardProgress> findRadomCardProgressWithLowestScore(final Set<CardToStudy> cardsToStudy) {
        final List<CardProgress> cardProgressesSortedByScoreAsc = this.findCardProgressesSortedByScoreAsc(cardsToStudy);
        if (cardProgressesSortedByScoreAsc.isEmpty()) {
            return empty();
        }
        return Optional.of(this.findRandomCardProgressWithScore(cardProgressesSortedByScoreAsc,
                cardProgressesSortedByScoreAsc.get(0).score()));
    }

    private Optional<CardToStudy> getCardToStudyById(final Set<CardToStudy> cardsToStudy,
                                                     final String cardId) {
        return cardsToStudy.stream()
                .filter(c -> cardId.equals(c.id()))
                .findFirst();
    }

    private CardProgress findRandomCardProgressWithScore(final List<CardProgress> progresses,
                                                         final Score score) {
        return progresses.stream()
                .filter(cardProgress -> cardProgress.score().equals(score))
                .min((o1, o2) -> ThreadLocalRandom.current().nextInt(-1, 2))
                .orElseThrow(() -> new NoSuchElementException("No value present"));
    }

    private List<CardProgress> findCardProgressesSortedByScoreAsc(final Set<CardToStudy> cardsToStudy) {
        return cardsToStudy.stream()
                .map(c -> this.cardProgressService.findByCardToStudyId(c.id()))
                .sorted(Comparator.comparingInt(o -> o.score().value()))
                .collect(Collectors.toList());
    }

    public void study(final String sessionId, final String cardId, final Opinion opinion) {
        final Session session = this.sessions.find(sessionId).orElseThrow(() -> new SessionDoesNotExist(sessionId));
        final CardToStudy card = session.findCard(cardId).orElseThrow(() -> new CardDoesNotExistInTheSession(sessionId, cardId));
        final CardProgress cardProgress = this.cardProgressService.findByCardToStudyId(card.id());
        cardProgress.updateProgress(opinion, LocalDateTime.now(clock));
        this.cardProgressService.save(cardProgress);
    }

    //@formatter:on
}
