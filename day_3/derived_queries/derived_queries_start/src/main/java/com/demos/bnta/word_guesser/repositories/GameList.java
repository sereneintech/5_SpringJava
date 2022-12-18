package com.demos.bnta.word_guesser.repositories;

import com.demos.bnta.word_guesser.models.Game;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

@Repository
public class GameList {

    private List<Game> games;

    public GameList(){
        this.games = new ArrayList<>();
    }

    public void addGame(Game game){
        this.games.add(game);
    }

    public Game getGameById(int id){
        return this.games.get(id - 1);
    }

}
