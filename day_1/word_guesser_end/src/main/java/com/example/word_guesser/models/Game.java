package com.example.word_guesser.models;

public class Game {

    private String word;
    private int guesses;
    private boolean complete;

    public String getWord() {
        return word;
    }

    public void setWord(String word) {
        this.word = word;
    }

    public int getGuesses() {
        return guesses;
    }

    public void setGuesses(int guesses) {
        this.guesses = guesses;
    }

    public boolean isComplete() {
        return complete;
    }

    public void setComplete(boolean complete) {
        this.complete = complete;
    }

    public Game(String word, int guesses, boolean complete){
        this.word = word;
        this.guesses = guesses;
        this.complete = complete;
    }

    public Game(){}



}
