package com.demos.bnta.word_guesser.repositories;

import com.demos.bnta.word_guesser.models.Game;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface GameRepository extends JpaRepository<Game, Integer> {


    @Query("SELECT g FROM games g "
            + "WHERE g.complete = TRUE "
            + "AND (:word IS NULL OR g.word = :word) "
            + "AND (:guesses IS NULL OR g.guesses < :guesses)"
    )
    List<Game> findByCompleteTrueAndOptionalWordAndOptionalGuessesLessThan(
            @Param("word") String word,
            @Param("guesses") Integer guesses
    );

    List<Game> findByPlayerId(long id);

    List<Game> findByWord(String word);

    List<Game> findByCompleteTrue();

}
