package com.demos.bnta.word_guesser.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity(name = "words")
public class Word {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(name = "word")
    private String word;

    @ManyToMany
    @JoinTable(
            name = "players_words",
            joinColumns = {@JoinColumn(name = "word_id", nullable = false)},
            inverseJoinColumns = {@JoinColumn(name = "player_id", nullable = false)}
    )
    @JsonIgnoreProperties({"words"})
    private List<Player> players;

    public Word(String word) {
        this.word = word;
        this.players = new ArrayList<>();
    }

    public Word() {
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getWord() {
        return word;
    }

    public void setWord(String word) {
        this.word = word;
    }

    public List<Player> getPlayers() {
        return players;
    }

    public void setPlayers(ArrayList<Player> players) {
        this.players = players;
    }

    public void addPlayer(Player player){
        this.players.add(player);
    }
}
