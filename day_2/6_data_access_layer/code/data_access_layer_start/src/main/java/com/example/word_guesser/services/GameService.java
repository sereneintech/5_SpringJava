package com.example.word_guesser.services;

import com.example.word_guesser.models.Game;
import com.example.word_guesser.models.Guess;
import com.example.word_guesser.models.Reply;
import org.apache.logging.log4j.util.Strings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class GameService {

    @Autowired
    WordService wordService;

    private String currentWord;
    private ArrayList<String> guessedLetters;

    private ArrayList<Game> games;

    public GameService() {
        this.games = new ArrayList<>();
    }

    public ArrayList<Game> getGames() {
        return games;
    }

    public void setGames(ArrayList<Game> games) {
        this.games = games;
    }

    public String getCurrentWord() {
        return currentWord;
    }

    public void setCurrentWord(String currentWord) {
        this.currentWord = currentWord;
    }

    public ArrayList<String> getGuessedLetters() {
        return guessedLetters;
    }

    public void setGuessedLetters(ArrayList<String> guessedLetters) {
        this.guessedLetters = guessedLetters;
    }

    public Reply startNewGame(){
        String targetWord = wordService.getRandomWord();
        Game game = new Game(targetWord);
        this.currentWord = Strings.repeat("*", targetWord.length());
        this.guessedLetters = new ArrayList<>();
        return new Reply(
                false,
                this.currentWord,
                "Started new game"
        );
    }

    public Reply processGuess(Guess guess, int id){

        Reply reply;

        Game game = this.games.get(id - 1);

        // Check if game is already complete
        if (game.isComplete()){
            reply = new Reply(
                    false,
                    game.getWord(),
                    String.format("Already finished game %d", game.getId())
            );
        }

        // Check if letter has been guessed already
        if (this.guessedLetters.contains(guess.getLetter())){
            reply =  new Reply(
                    false,
                    this.currentWord,
                    String.format("Already guessed %s", guess.getLetter()));
        }

        this.guessedLetters.add(guess.getLetter());
        incrementGuesses(game);

        // Check for incorrect guess
        if (!game.getWord().contains(guess.getLetter())){
            reply =  new Reply(
                    false,
                    this.currentWord,
                    String.format("%s is not in the word", guess.getLetter())
            );
        }

        // Handle correct guess
        String runningResult = game.getWord();

        for (Character letter : game.getWord().toCharArray()) {
            if (!this.guessedLetters.contains(letter.toString())){
                runningResult = runningResult.replace(letter, '*');
            }
        }

        setCurrentWord(runningResult);

        if (checkWinCondition(game)){
            game.setComplete(true);
            reply = new Reply(
                true,
                this.currentWord,
                "You win!"
            );
        } else {
            reply = new Reply(
                    true,
                    this.currentWord,
                    String.format("%s is in the word", guess.getLetter())
            );
        }

        return reply;
    }

    private boolean checkWinCondition(Game game){
        return game.getWord().equals(this.currentWord);
    }

    private void incrementGuesses(Game game){
        game.setGuesses(game.getGuesses() + 1);
    }
}
