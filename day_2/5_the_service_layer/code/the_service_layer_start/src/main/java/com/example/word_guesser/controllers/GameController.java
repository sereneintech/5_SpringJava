package com.example.word_guesser.controllers;

import com.example.word_guesser.models.Game;
import com.example.word_guesser.models.Guess;
import com.example.word_guesser.models.LetterList;
import com.example.word_guesser.models.Reply;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;

@RestController
@RequestMapping(value = "/games")
public class GameController {

    private Game game;
    private String currentWord;
    private ArrayList<String> guessedLetters;

    @PostMapping
    public ResponseEntity<Reply> newGame(){
        this.game = new Game("hello");
        this.currentWord = "*****";
        this.guessedLetters = new ArrayList<>();
        Reply reply = new Reply(false, currentWord, "New game started");
        return new ResponseEntity<>(reply, HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<Reply> getGameStatus(){
        Reply reply = new Reply(false, currentWord, "Game in progress.");
        return new ResponseEntity<>(reply, HttpStatus.OK);
    }

    @PatchMapping
    public ResponseEntity<Reply> handleGuess(@RequestBody Guess guess) {
        // create new Reply object
        Reply reply;

        // check if letter has already been guessed
        if (this.guessedLetters.contains(guess.getLetter())) {
            reply = new Reply(
                    false,
                    this.currentWord,
                    String.format("Already guessed %s", guess.getLetter()
                    ));
        }
        // add letter to previous guesses
        this.guessedLetters.add(guess.getLetter());
        // check for incorrect guess
        if (!game.getWord().contains(guess.getLetter())) {
            reply = new Reply(
                    false,
                    this.currentWord,
                    String.format("%s is not in the word", guess.getLetter())
            );
        }
        // process correct guess
        String runningResult = game.getWord();

        for (Character letter : game.getWord().toCharArray()) {
            if (!this.guessedLetters.contains(letter.toString())) {
                runningResult = runningResult.replace(letter, '*');
            }
        }

        this.currentWord = runningResult;

        reply = new Reply(
                true,
                this.currentWord,
                String.format("%s is in the word", guess.getLetter())
        );

        // return result
        return new ResponseEntity<>(reply, HttpStatus.OK);
    }

    @GetMapping(value = "/guessed")
    public ResponseEntity<LetterList> checkGuesses(){
        LetterList letters = new LetterList(this.guessedLetters);
        return new ResponseEntity<>(letters, HttpStatus.OK);
    }
}
