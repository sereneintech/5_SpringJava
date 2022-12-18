package com.example.word_guesser.controllers;

import com.example.word_guesser.models.Game;
import com.example.word_guesser.models.Reply;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/games")
public class GameController {

    @GetMapping
    public Reply newGame(){
        Game game = new Game("hello", 0, false);
        return new Reply("*****", "New game started");
    }

}
