package com.example.word_guesser.controllers;

import com.example.word_guesser.models.Game;
import com.example.word_guesser.models.Guess;
import com.example.word_guesser.models.LetterList;
import com.example.word_guesser.models.Reply;
import com.example.word_guesser.services.GameService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;

@RestController
@RequestMapping(value = "/games")
public class GameController {

    @Autowired
    GameService gameService;

    @PostMapping
    public ResponseEntity<Reply> newGame(){
        Reply reply = gameService.startNewGame();
        return new ResponseEntity<>(reply, HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<Reply> getGameStatus(){
        Reply reply = new Reply(
                false,
                gameService.getCurrentWord(),
                "Game in progress.");
        return new ResponseEntity<>(reply, HttpStatus.OK);
    }

    @PatchMapping(value = "/{id}")
    public ResponseEntity<Reply> handleGuess(@RequestBody Guess guess, @PathVariable int id) {
        Reply reply = gameService.processGuess(guess, id);
        return new ResponseEntity<>(reply, HttpStatus.OK);
    }

    @GetMapping(value = "/guessed")
    public ResponseEntity<LetterList> checkGuesses(){
        ArrayList<String> guesses = gameService.getGuessedLetters();
        LetterList letters = new LetterList(guesses);
        return new ResponseEntity<>(letters, HttpStatus.OK);
    }
}
