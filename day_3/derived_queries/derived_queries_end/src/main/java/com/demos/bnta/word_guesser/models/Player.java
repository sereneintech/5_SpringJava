package com.demos.bnta.word_guesser.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity(name = "players")
public class Player {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(name = "name")
    private String name;

    @OneToMany(mappedBy = "player")
    @JsonIgnoreProperties({"player"})
    private List<Game> games;

    @ManyToMany
    @JoinTable(
            name = "players_words",
            joinColumns = {@JoinColumn(name = "player_id", nullable = false)},
            inverseJoinColumns = {@JoinColumn(name = "word_id", nullable = false)}
    )
    @JsonIgnoreProperties({"players"})
    private List<Word> words;

    public Player(String name) {
        this.name = name;
        this.games = new ArrayList<>();
        this.words = new ArrayList<>();
    }

    public Player() {
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Game> getGames() {
        return games;
    }

    public void setGames(List<Game> games) {
        this.games = games;
    }

    public List<Word> getWords() {
        return words;
    }

    public void setWords(ArrayList<Word> words) {
        this.words = words;
    }

    public void addWord(Word word){
        this.words.add(word);
    }
}
