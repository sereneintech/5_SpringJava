package com.demos.bnta.word_guesser.components;

import com.demos.bnta.word_guesser.models.Game;
import com.demos.bnta.word_guesser.models.Player;
import com.demos.bnta.word_guesser.models.Word;
import com.demos.bnta.word_guesser.repositories.GameRepository;
import com.demos.bnta.word_guesser.repositories.PlayerRepository;
import com.demos.bnta.word_guesser.repositories.WordRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

@Component
public class DataLoader implements ApplicationRunner {

    @Autowired
    WordRepository wordRepository;

    @Autowired
    PlayerRepository playerRepository;
    @Autowired
    GameRepository gameRepository;

    @Override
    public void run(ApplicationArguments args) throws Exception {

        List<String> rawWords = Arrays.asList(
                "safari",
                "volcano",
                "throne",
                "constraint",
                "copper",
                "fence",
                "porter",
                "grandfather",
                "oral",
                "enemy",
                "cry",
                "gate",
                "different",
                "harmony",
                "dream",
                "management",
                "finance",
                "set",
                "display",
                "burn",
                "frank",
                "overcharge",
                "heroin",
                "inappropriate",
                "dose",
                "hover",
                "literacy",
                "campaign",
                "twin",
                "revenge",
                "swallow",
                "ignore",
                "rack",
                "treatment",
                "government",
                "weight",
                "beard",
                "insurance",
                "sum",
                "sail",
                "creation",
                "soldier",
                "develop",
                "parking",
                "permanent",
                "surprise",
                "virgin",
                "lie",
                "valley",
                "hell"
        );

        for (String rawWord : rawWords) {
            Word word = new Word(rawWord);
            wordRepository.save(word);
        }

        Player player1 = new Player("Iain");
        Player player2 = new Player("Colin");

        player1.addWord(wordRepository.findById(1l).get());
        player1.addWord(wordRepository.findById(2l).get());
        player1.addWord(wordRepository.findById(3l).get());

        player2.addWord(wordRepository.findById(1l).get());
        player2.addWord(wordRepository.findById(2l).get());
        player2.addWord(wordRepository.findById(6l).get());

        playerRepository.save(player1);
        playerRepository.save(player2);

        Game game1 = new Game(wordRepository.findById(1l).get().getWord(), player1);
        Game game2 = new Game(wordRepository.findById(2l).get().getWord(), player1);
        Game game3 = new Game(wordRepository.findById(3l).get().getWord(), player1);

        Game game4 = new Game(wordRepository.findById(1l).get().getWord(), player2);
        Game game5 = new Game(wordRepository.findById(2l).get().getWord(), player2);
        Game game6 = new Game(wordRepository.findById(4l).get().getWord(), player2);
        game6.setComplete(true);

        gameRepository.saveAll(Arrays.asList(game1, game2, game3, game4, game5, game6));

    }
}
