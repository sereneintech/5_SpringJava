# The Data Access Layer

As our app's complexity increases, so does the need for further separation of concerns. In the next section, we will introduce you to a way to separate out the logic of persisting data in our apps using a data access layer.

The need for separating concerns will only increase, as this allows us to evolve different parts of our app in different directions, while strictly adhering to SOLID principles.

Just like how we showed you how to use a DTO to communicate between components, our new data access layer will handle communication when access to data is needed within and between business logic and the controllers. We will use the repository pattern in our app.

> Note that we will not actually implement a full repository yet, as we will just emulate it in memory. In the next lesson we will add PSQL for persistance, and we will use the JPARepository interface to reap the full benefits of the JPA.

## Introducting GameList and WordList

The first thing we are going to extract from our business logic is the handling of games. If the `GameService` is responsible for accessing and storing games, we are breaking quite a few SOLID principles. Let's fix that!

First, let's create a `GameList` class, which will be responsible for accessing data. Create a folder called `repositories`, then create a new classfile called `GameService` in it.

We will extract the following functionality to this class from `GameService`:

* Keep track of games
* Be able to add a new game to track
* Get a game by an ID

```java
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

```

> Note that we are adding the `@Repository` annotation to our class, this will enable Spring to track this class and we can autowire it later

Now that we have the functionality we need separated, we can modify `GameService` to rely on how to add a new game or retrieve a game on the `GameList` class

Let's move onto the `GameService` class - quite a few things to modify here! First, we need to remove the `games` property and instead autowire the `GameList`!


```java
public class GameService {

    @Autowired
    WordService wordService;
    
    @Autowired                            // UPDATED
    GameList gameList;                    // UPDATED

    private String currentWord;
    private ArrayList<String> guessedLetters; 

    //private ArrayList<Game> games;      // REMOVE

    public GameService() {
        //this.games = new ArrayList<>(); // REMOVE
    }
    
    //...
```

Next, modify the `processGuess()` method, so the responsibility of getting the game to guess for lies with `GameList`, not the Service!

```java
public Reply processGuess(Guess guess, int id){

        // Find the correct game
        Game game = gameList.getGameById(id); // UPDATED

        // Check if game is already complete
        if (game.isComplete()){
            return new Reply(
        //...
``` 

Finally, whenever a new game is started, we will ask the `GameList` to handle this in the `startNewGame` method!

```java
public Reply startNewGame(){
    String targetWord = wordService.getRandomWord();
    Game game = new Game(targetWord);
    this.currentWord = Strings.repeat("*", targetWord.length());
    this.guessedLetters = new ArrayList<>();
    gameList.addGame(game); 		                 // UPDATED
    return new Reply(
            false,
            this.currentWord,
            String.format("Started new game with id %d", game.getId())
    );
}
```

Fantastic, now whenever the method of accessing games is changing, we do not need to modify the GameService class, only the GameList class - thus following the Single Responsbility principle and the Open/Closed principle!

However, there's another part of our app that's responsible and could use a little update - namely the `WordService`! In the future if we want to store words in our database, we are much better off handling this in a separate file too! Let's create a `WordList` class to handle it!

Again, we start by moving all of our data logic to a new file called `WordList` in the `repositories` package.

```java
package com.demos.bnta.word_guesser.repositories;

import org.springframework.stereotype.Repository;

import java.util.Arrays;
import java.util.List;
import java.util.Random;

@Repository
public class WordList {

    private List<String> words;

    public WordList(){
        this.words = Arrays.asList(
                "hello",
                "goodbye",
                "testing",
                "mystery",
                "games",
                "spring",
                "controller",
                "repository"
        );
    }

    public String getRandomWord(){
        Random random = new Random();
        int randomIndex = random.nextInt(this.words.size());
        return this.words.get(randomIndex);
    }

}
```

And amend the same logic from `WordService`:

```java
@Service
public class WordService {

    @Autowired
    WordList wordList;

    public String getRandomWord(){
        return wordList.getRandomWord();
    }
}

```
Luckily no other modifications are needed, as the `WordService` method name is unchanged, so we can leave the `GameService` that uses `WordService` alone!


## Recap

Separating the concerns by creating a `WordList` and `GameList` class helps us follow the SOLID principles better, and enables us to make extensions to our code without the need to modify classes whose responsibility lies elsewhere.