package com.demos.bnta.word_guesser.services;

import com.demos.bnta.word_guesser.models.Word;
import com.demos.bnta.word_guesser.repositories.WordRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Random;

@Service
public class WordService {

    @Autowired
    WordRepository wordRepository;

    public Word getRandomWord(){
        Random random = new Random();
        long randomIndex = random.nextLong(50);
        return wordRepository.findById(randomIndex).get();
    }

    public void updateWord(Word word){
        wordRepository.save(word);
    }

}
