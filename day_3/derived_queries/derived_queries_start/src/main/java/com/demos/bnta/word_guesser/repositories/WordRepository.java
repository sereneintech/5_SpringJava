package com.demos.bnta.word_guesser.repositories;

import com.demos.bnta.word_guesser.models.Word;
import org.springframework.data.jpa.repository.JpaRepository;

public interface WordRepository extends JpaRepository<Word, Long> {
}
