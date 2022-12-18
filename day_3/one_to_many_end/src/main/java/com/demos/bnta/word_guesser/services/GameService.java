package com.demos.bnta.word_guesser.services;

import com.demos.bnta.word_guesser.models.*;
import com.demos.bnta.word_guesser.repositories.GameRepository;
import org.apache.logging.log4j.util.Strings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class GameService {


    @Autowired
    GameRepository gameRepository;

    @Autowired
    PlayerService playerService;

    @Autowired
    WordService wordService;

    private String currentWord;
    private ArrayList<String> guessedLetters;

    public GameService() {
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

    public Reply processGuess(Guess guess, int id){

        // Find the correct game
        Game game = gameRepository.findById(id).get();

        // Check if game is already complete
        if (game.isComplete()){
            return new Reply(
                    false,
                    game.getWord(),
                    String.format("Already finished game %d", game.getId())
            );
        }

        // Check if letter has been guessed already
        if (this.guessedLetters.contains(guess.getLetter())){
            return new Reply(false, this.currentWord, String.format("Already guessed %s", guess.getLetter()));
        }

        // Only increment guess count if a new letter is chosen
        incrementGuesses(game);

        // Check for incorrect guess
        if (!game.getWord().contains(guess.getLetter())){
            this.guessedLetters.add(guess.getLetter());
            return new Reply(
                    false,
                    this.currentWord,
                    String.format("%s is not in the word", guess.getLetter())
            );
        }

        // Add letter to previous guesses
        this.guessedLetters.add(guess.getLetter());


        // Handle correct guess
        String runningResult = game.getWord();

        for (Character letter : game.getWord().toCharArray()) {
            if (!this.guessedLetters.contains(letter.toString())){
                runningResult = runningResult.replace(letter, '*');
            }
        }

        setCurrentWord(runningResult);

        // Check for win
        if (checkWinCondition(game)){
            game.setComplete(true);
            gameRepository.save(game);
            return new Reply(true, this.currentWord, "You win!");
        } else {
            return new Reply(true, this.currentWord,
                    String.format("%s is in the word", guess.getLetter()));
        }
    }

    private boolean checkWinCondition(Game game){
        return game.getWord().equals(this.currentWord);
    }

    private void incrementGuesses(Game game){
        game.setGuesses(game.getGuesses() + 1);
        gameRepository.save(game);
    }

    public Reply startNewGame(long playerId){
        String targetWord = wordService.getRandomWord();
        Player player = playerService.getPlayerById(playerId).get();
        Game game = new Game(targetWord, player);
        this.currentWord = Strings.repeat("*", targetWord.length());
        this.guessedLetters = new ArrayList<>();
        gameRepository.save(game);
        return new Reply(
                false,
                this.currentWord,
                String.format("Started new game with id %d", game.getId())
        );
    }

    public List<Game> getAllGames(){
        return gameRepository.findAll();
    }

    public Optional<Game> getGameById(int id){
        return gameRepository.findById(id);
    }
}
