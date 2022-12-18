package com.demos.bnta.word_guesser.controllers;

import com.demos.bnta.word_guesser.models.Game;
import com.demos.bnta.word_guesser.models.Guess;
import com.demos.bnta.word_guesser.models.LetterList;
import com.demos.bnta.word_guesser.models.Reply;
import com.demos.bnta.word_guesser.services.GameService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping(value = "/games")
public class GameController {

    @Autowired
    GameService gameService;

//  Alternative Index Route
    @GetMapping
    public ResponseEntity<List<Game>> getAllGames(
            @RequestParam Map<String, String> parameters
    ){
        List<Game> games;
        if(!parameters.isEmpty()){
            games = gameService.getAllGamesMultiParam(parameters);
        } else {
            games = gameService.getAllGames();
        }
        return new ResponseEntity<>(games, HttpStatus.OK);
    }

//    @GetMapping
//    public ResponseEntity<List<Game>> getAllGames(
//            @RequestParam Optional<Boolean> isComplete,
//            @RequestParam Optional<String> word,
//            @RequestParam Optional<Long> playerId,
//            @RequestParam Optional<Integer> guesses
//    ){
//        List<Game> games;
//        if(isComplete.isPresent()){
//            games = gameService.getAllCompletedGames();
//        } else if(word.isPresent()) {
//            games = gameService.getAllGames();
//        } else if(playerId.isPresent()){
//            games = gameService.getAllGamesByPlayerId(playerId.get());
//        } else {
//            games = gameService.getAllGames();
//        }
//        return new ResponseEntity<>(games, HttpStatus.OK);
//    }

    @GetMapping(value = "/{id}")
    public ResponseEntity<Game> getGameById(@PathVariable int id){
        Optional<Game> game = gameService.getGameById(id);
        if (game.isPresent()){
            return new ResponseEntity<>(game.get(), HttpStatus.OK);
        } else {
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        }
    }

    @PatchMapping(value = "/{id}")
    public ResponseEntity<Reply> submitGuess(@RequestBody Guess guess, @PathVariable int id){
        Reply reply = gameService.processGuess(guess, id);
        return new ResponseEntity<>(reply, HttpStatus.OK);
    }


    @GetMapping(value = "/guessed")
    public ResponseEntity<LetterList> checkGuesses(){
        ArrayList<String> guesses = gameService.getGuessedLetters();
        LetterList guessedLetters = new LetterList(guesses);
        return new ResponseEntity<>(guessedLetters, HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<Reply> startNewGame(@RequestParam long playerId){
        Reply reply = gameService.startNewGame(playerId);
        return new ResponseEntity<>(reply, HttpStatus.CREATED);
    }

}
