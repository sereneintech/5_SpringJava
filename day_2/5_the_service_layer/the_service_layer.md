# The Service Layer

### Learning Objectives

- Understand the benefits of maintaining a separation of concerns in a program
- Be familiar with a common architecture pattern for Spring applications
- Know what a Bean is and how to use them
- Be familiar with the `@Service` and `@Autowired` annotations and know how they interact.

We can finally have some fun with our game! It's not perfect - it won't tell us when the game is won and the mystery word is always going to be "hello" - but we can at least make a guess and get some feedback on it. Most importantly we have built it in a format which will enable us to interact with it in a number of different ways in the future. We could play using a client like Postman, build a web app for it or even use this API as a microservice for another Spring app.

The problem is that we haven't built our game *well*. We're violating at least two of the SOLID principles (single responsibility and open-closed) but at the moment there isn't a clear solution to the problem. Careful consideration of the application's architecture will help.

## Why Separate Our Code?

In previous projects the need for the Single Responsibility Principle has been clear. Indeed it's still obvious in some parts of our current project: a `Game` POJO shouldn't have anything to do with a `Reply`, so each needs its own class. The distinctions start to break down when we move away from POJOs though. Where exactly do we draw the line with what a controller should do? How much should the models be taking care of?

The Open-Closed Principle should act as a guide for us as well. Recall that a class should be **open for extension** but **closed for modification**. Currently our `GameController` is failing this test as any new functionality will require significant changes to the codebase. Imagine a scenario where we wanted to play a different game, maybe a number guessing game instead of a word game. The controller would still need to be able to process the request, but at the moment it can only work with our word game.

Adding new rules will therefore require heavy edits. The `handleGuess` method is already long and would need another `if`-statement to determine which rule set it should follow. Our controller already breaks single responsibility, making one of its methods break the principle as well won't help the situation. 

Things will get even worse when we start keeping track of previous game results. Not only will we be adding more responsibilities to the controller, each method will get longer and more convoluted depending on what we're storing and how we store it. We will quickly hit a tipping point where not only do we have to contend with a bloated, brittle controller, we won't even know where to start looking when something breaks. Wouldn't it be great if instead of that mess we could abstract some of the code away into a different class, leaving our controller to call a method when it needed to do something?

```java

@RestController
public class MyController{

	@PostMapping(value="/route")
	public ResponseEntity createResource(Payload payload){
		someMethod(payload)
		return new ResponseEntity()
	}

}
```

This is much easier said than done, though. Without an understanding of how to abstract the method and where to abstract it to all we are doing is moving the problem somewhere else.

## An Example of Spring Boot Architecture

We have already seen many examples of the request-response cycle, where a client sends a request to a server to process before receiving a response. This separation is often necessary since our application's users are unlikely to be in the same place as the server but even when everything *is* running on the same system it has benefits. The client only has to make a request and deal with the response, it doesn't need to worry about how one is turned into the other. Likewise the server doesn't care about what's going to happen with the response, it just needs to package it up and send it off.

We may not formalise it in the same way as request-response, but it's entirely possible to take a similar approach internally in a Spring application. By separating our code into "layers" we can determine which classes should be responsible for which actions according to the layer the sit in. Our controllers will only need to send information to a service and deal with whatever is returned, they don't need to worry about the logic. The service classes don't care what happens with the information they send back, they just process an input and send off the results.

There is no single "best" way to structure a Spring application, but as always some patterns will be better than others in certain situations. The structure we will use for our game is laid out at a high level in the diagram below:

![An example Spring application architecture](../../../assets/spring/5_the_service_layer/spring_architecture.png)

Within the server there are three distinct layers:

#### The Controller Layer 

Our controllers sit at the top level of the server and act as gatekeepers, dictating how the clients (in whatever form) can interact with the API. They define the routes which are available to the client and the methods each route will accept, along with any required parameters. They also create the response objects and set their status codes.

When a request is received the controller should **not** be "hands-on" in the processing of it. Instead it should deserialise whatever payload is sent in the request's body and send it onward to a different part of the application for it to deal with, typically by passing it as an argument to a method. The return value from that method can be sent to the client as the body of a response, or incorporated into a payload which is sent. 

Controllers can also be used to determine what the status of a response should be. For example, if a request is received searching for a specific resource the controller would not do the search itself but would have access to the result. If a resource is found the controller may return the value with a `200` status, or if nothing is retrieved it could return an error and `404`. If we are using a templating engine such as [Thymeleaf](https://www.thymeleaf.org/) the controller will also be responsible for requesting the view to be sent to the client.

#### The Service Layer

Typically when a controller passes off a payload to another class it will send it to a **service class**. These are responsible for the "heavy lifting" of an application and are where most of the business logic will be defined. That logic could be a couple of lines long, it could be a long, complex process. It could even involve acting as a client and making requests to other APIs or microservices. 

In our application the service layer will have a couple of jobs. For now its main purpose will be to handle the game logic, moving it away from the controller. This will be the first step towards making our controller capable of playing any game, although that will likely still require a refactoring of how we handle guesses and format replies. It doesn't entirely solve our single responsibility problem though as there is a lot to do in order to play a game.

At the moment all we are doing is creating and updating a `Game` object but as our application grows we will also need to access player details and keep track of which words have been used in games. Those will each require their own service to maintain single responsibility. It's beyond the scope of this week, but we could hypothetically have a player log in before playing a game, or compete against another player. Any supporting logic would be defined in a class sitting in the service layer.

The other job our service layer will have is to form a link between the controller and any data storage a client needs to access. Regardless of the type of data storage, directly accessing it is the responsibility of another layer of our application.

#### The Data Access Layer

The Data Access layer is responsible for exactly what it sounds like: managing the storage and retrieval of data needed by our application. By keeping this independent from the controller and the service layer we enable our application to work with data stored in many different ways. Ultimately our service layer won't care how player records are stored, it will rely on a class in the data access layer to expose methods enabling it to access them.

As we build out our application we will see examples of two ways in whcih we can store data: maintaining collections of the relevant objects internally and connecting to an external database.

#### The Database

The database in the diagram above is still part of the server but isn't part of the Spring application. We can use both relational and non-relational databases, provided Spring Boot has an appropriate **driver** for our chosen language. There are a number of ways in which Spring can interact with a database, ranging from entirely auto-generated queries to executing user-defined queries when particular methods are called. Depending on the configuration we may even be able to swap one database for another, update the driver and our app will continue working as if nothing happened.

#### The Models

Sitting next to our three layers in the diagram are the **models**. As we have already seen they are the classes which define the data moving around our application and as such are needed at every step of the process. We have already seen how we can define DTOs to facilitate communication between client and server and this structure can be used for communication between the layers of the server as well. In a future lesson we will see how to annotate models in such a way that we can automatically generate a database schema for the data access layer to use.

## Creating Beans

Before we can start splitting our application up we need to establish how the different layers will be able to communicate with each other. When we move the logic from `GameController` to `GameService` the logical next step is to instantiate a `GameService` object somewhere in the controller to enable access to the methods. When we do this, though, we start to push against another SOLID principle - this time it's dependency inversion.

If a controller is responsible for instantiating a required service then the two classes have become **tightly coupled**. In general we try to avoid this, ensuring that the existance of one object doesn't depend on another. Spring gives us a way to keep our classes **loosely coupled** by handling the instantiation of all the needed objects elsewhere. Using a process known as **dependency injection** we can then use those objects in the appropriate places in our application.

This has been happening already, we just haven't realised we're doing it. If we stop and think, though, there's a big question that pops into our heads: how have we been calling the methods in `GameController` when we haven't created one with `new GameController()` anywhere in our app?

The object is created and managed for us by Spring, making it an example of a **bean**. Beans are used in many types of Java application, not just Spring, and have similar requirements to POJOs. In addition to those requirements they must implement the `Serializable` interface, which is taken care of for us when working with Spring beans. Not everything becomes a bean when working with Spring, though - we need to use annotations to identify them.

Spring includes the `@Bean` annotation which identifies a class as one which can be instantiated as a Spring bean. Annotations are like regular classes in that they can be extended, and Spring leverages this to give more specificity to some of the annotations we can use. When we created our controller we annotated it using `@RestController` which is a sub-class of `@Bean` but adds some controller-specific logic. There are variations (such as `@Controller`) which are implemented in slightly different ways. 

When our application starts, every class with an annotation which extends `@Bean` is instantiated by Spring and ready to be injected wherever it is required. In the case of our controllers we don't need to explicitly handle the injection, which is why we can immediately access our routes` but this generally won't be the case. We will need to not only identify the beans, but also explicitly state where they should be injected.

## Using a `@Service` Bean

Now we know how to manage the objects we will be creating we can start to dismantle our monster controller. The first step is to create a `services` package to hold the files, followed by a `GameService` class.

```java title="services/GameService.java"
// services/GameService.java

@Service
public class GameService {
    
    
}

```

This time we use the `@Service` annotation for the class declaration which also extends `@Bean`. Our logic will be moving from `GameController` across to `GameService` which means the properties it depends on need to move too. We will move `game`, `currentWord` and `guessedLetters` into our new service class and set up a constructor along with getters and setters.

```java title="services/GameService.java"
// services/GameService.java

@Service
public class GameService {
    
   	private Game game;
   	private String currentWord;
   	private List<String> guessedLetters;

   	public GameService() {
   
   	}
   
   // getters & setters 
}
```

There are two tasks being carried out by the controller which should really be handled by the service. We'll start by moving the logic to start a game into a method in the service:


```java title="services/GameService.java"
// services/GameService.java

@Service
public class GameService {
    
   	// ...
   	
   	public Reply startNewGame(){
     	Game game = new Game("hello");
      	this.currentWord = "*****";
      	this.guessedLetters = new ArrayList<>();
      	return new Reply(
                false,
                this.currentWord,
                "Started new game"
      	);
   	}
}
```

We also need to transfer the method to check a guess.

```java title="services/GameService.java"
// services/GameService.java

@Service
public class GameService {
    
   	// ...
   	
   	public Reply processGuess(Guess guess){

        Reply reply;
        
        if (this.guessedLetters.contains(guess.getLetter())){
            reply =  new Reply(
                    false, 
                    this.currentWord, 
                    String.format("Already guessed %s", guess.getLetter()));
        }

        this.guessedLetters.add(guess.getLetter());
        
        if (!game.getWord().contains(guess.getLetter())){
            reply =  new Reply(
                    false,
                    this.currentWord,
                    String.format("%s is not in the word", guess.getLetter())
            );
        }
        
        String runningResult = game.getWord();

        for (Character letter : game.getWord().toCharArray()) {
            if (!this.guessedLetters.contains(letter.toString())){
                runningResult = runningResult.replace(letter, '*');
            }
        }

        setCurrentWord(runningResult);

        reply = new Reply(
                true,
                this.currentWord,
                String.format("%s is in the word", guess.getLetter())
        );
        
        return reply;
    }
}
```

Now instead of defining it itself our controller can call upon the methods defind in `GameService`. The methods to start a game and process a guess are much shorter:

```java title="controllers/GameController.java"
// controllers/GameController.java

 	@PostMapping
   	public ResponseEntity<Reply> newGame(){
      	Reply reply = gameService.startNewGame();
      	return new ResponseEntity<>(reply, HttpStatus.CREATED);
   	}
   	
   	@PatchMapping
   	public ResponseEntity<Reply> handleGuess(@RequestBody Guess guess) {
      	Reply reply = gameService.processGuess(guess);
      	return new ResponseEntity<>(reply, HttpStatus.OK);
  	}
```

Our two `GET` requests can also use methods from teh service to get what they need.

```java title="controllers/GameController.java"
// controllers/GameController.java

 	@GetMapping
   	public ResponseEntity<Reply> getGameStatus(){
      	Reply reply = new Reply(
                false,
                gameService.getCurrentWord(),
                "Game in progress.");
      	return new ResponseEntity<>(reply, HttpStatus.OK);
   	}
   	
   @GetMapping(value = "/guessed")
   public ResponseEntity<LetterList> checkGuesses(){
      	ArrayList<String> guesses = gameService.getGuessedLetters();
      	LetterList letters = new LetterList(guesses);
       return new ResponseEntity<>(letters, HttpStatus.OK);
   }
```

We now have a problem though. If we start our application we can still make requests to the same routes but we get `500` errors every time. Something has broken in our server.

The problem lies with our beans. Although `@Service` and `@RestController` both extend `@Bean` they are managed by Spring in different ways. Although Spring still instantiates a `@Service` it doesn't handle the injection in the same way s it does for a `@RestController`, which means that although our `GameService` has been instantiated we can't access it from anywhere. We need to explicitly inject it into `GameController` which can be done using another annotation.

```java title="controllers/GameController.java"
// controllers/GameController.java

 	@RestController
 	public class GameController{
 	
 		@Autowired
 		GameService gameService
 		
 		// ...
 	
 	}
```

The `@Autowired` annotation indicates that a bean of the given type should be injected into this class. With this in place we can now run our application as before with no noticeable difference to the client, but our architecture is much cleaner. Any time we want to inject a bean into one of our classes we will use `@Autowired` to do so.

## Extending the Game Logic

A major advantage of our new separation of concerns is that it removes some of the brittleness from our application: if we modify one of the methods in the service and break something it won't affect anything else. With that in mind we can think about improving the game play.

An obvious flaw is that we have the same mystery word every time we play. We will add the ability to pick a random word from a list but we need to think carefully about how we approach this. Remember that single responsibility hasn't been satisfied just by moving our business logic out of the controller, we also need to consider the division of labour within each layer. Instead of having `GameService` handle the logic for choosing a word we will create a separate `WordService` to take care of it.

```java title="services/WordService.java"
// services/WordServicce.java

@Service
public class WordService {

   	private List<String> words;

   	public WordService(){
      	this.words = this.words = Arrays.asList(
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

Our `WordService` bean will have a list of words to choose from and a method available to pick one at random. Since it is a `@Service` we can inject it into another class, including another `@Service`. As we have already seen with `GameController` and `GameService` it is quite common to inject one bean into another like this. Our secret word is being set by `GameService` so we will inject `WordService` there.

```java title="services/GameService.java"
// services/GameService.java

@Service
public class GameService {
    
   	@Autowired
   	WordService wordService
   	
   	// ...
}
```

The process of starting a new game can be refactored to use the `getRandomWord()` method. We also need to dynamically build the hidden `currentWord` property.

```java title="services/GameService.java"
// services/GameService.java

@Service
public class GameService {
    
   	// ...
   	
   	public Reply startNewGame(){
   		String targetWord = wordService.getRandomWord();
     	Game game = new Game(targetWord);
      	this.currentWord = Strings.repeat("*", targetWord.length());
      	this.guessedLetters = new ArrayList<>();
      	return new Reply(
                false,
                this.currentWord,
                "Started new game"
      	);
   	}
}
```

The controller should be able to play any instance of the game, or even start a new round mid-game, which we currently don't support. We can expand `GameService` to store a record of played games, check a guess against specific games and check if a game has been won. We start by replacing the `game` property with a list of `Game` objects.

```java title="services/GameService.java"
// services/GameService.java

@Service
public class GameService {

   	// ...
   	
   	private ArrayList<Game> games;
   	
   	public GameService(){
   		this.games = new ArrayList<>();
   	}
}
```

Starting a new game will add the game to the list:

```java title="services/GameService.java"
// services/GameService.java

@Service
public class GameService {
    
   	// ...
   	
   	public Reply startNewGame(){
   		String targetWord = wordService.getRandomWord();
     	Game game = new Game(targetWord);
      	this.currentWord = Strings.repeat("*", targetWord.length());
      	this.guessedLetters = new ArrayList<>();
      	this.games.add(game);			// NEW
      	return new Reply(
                false,
                this.currentWord,
                "Started new game"
      	);
   	}
}
```

Once we can have multiple games in existence at once we need a way to tell them apart. We will revist the `Game` model and add an `id` property. This is a good use case for a `static` property which can be incremented each time a new `Game` is instantiated to track what the next value of `id` should be.

```java title="models/Game.java"
// models/Game.java

public class Game {

   	private static int nextId = 1;		// NEW

   	private int id;				// NEW
   	private String word;
   	private int guesses;
   	private boolean complete;

   	public Game(String word) {		// MODIFIED
      	this.id = nextId;
      	nextId += 1;
      	this.guesses = 0;
      	this.complete = false;
      	this.word = word;
   	}

	// ...
}
```

We aren't limited to defining methods which are going to be used by the controller. As with many of the classes we have worked with previously we need to add extra helper methods for reusable functionality. In this case we need to be able to increment the number of guesses a player has made for a given game and also check if the game has been won.

```java title="services/GameService.java"
// services/GameService.java

private boolean checkWinCondition(Game game){
	return game.getWord().equals(this.currentWord);
}

private void incrementGuesses(Game game){
   	game.setGuesses(game.getGuesses() + 1);
}
```

Note that these methods can be private, since they are only needed within `GameService`. 

Now that we can have multiple games in existence we need to know which game we are making a guess against. We need to add an extra parameter to `processGuess`, but we can also include our helper methods to update the game as we go. The method should now look like this:

```java title="services/GameService.java"
// services/GameService.java

public Reply processGuess(Guess guess, int id){

	Reply reply;

   	Game game = this.games.get(id - 1);

   	// Check if game is already complete
    	if (game.isComplete()){
       	reply = new Reply(
          	false,
             	game.getWord(),
             	String.format("Already finished game %d", game.getId())
         	);
      	}

      	// Check if letter has been guessed already
      	if (this.guessedLetters.contains(guess.getLetter())){
        	reply =  new Reply(
          	false,
             	this.currentWord,
             	String.format("Already guessed %s", guess.getLetter()));
      	}

	 	this.guessedLetters.add(guess.getLetter());
      	incrementGuesses(game);

      	// Check for incorrect guess
      	if (!game.getWord().contains(guess.getLetter())){
         	reply =  new Reply(
          	false,
             	this.currentWord,
             	String.format("%s is not in the word", guess.getLetter())
         	);
      	}

      	// Handle correct guess
      	String runningResult = game.getWord();

      	for (Character letter : game.getWord().toCharArray()) {
         	if (!this.guessedLetters.contains(letter.toString())){
          	runningResult = runningResult.replace(letter, '*');
         	}
      	}

      	setCurrentWord(runningResult);

      	if (checkWinCondition(game)){
       	game.setComplete(true);
         	reply = new Reply(
          	true,
             	this.currentWord,
             	"You win!"    
         	);
      	} else {
       	reply = new Reply(
          	true,
            	this.currentWord,
            	String.format("%s is in the word", guess.getLetter())
         	);
      	}
        
      	return reply;
}
```

Our game now has more features and is (hopefully) even more fun to play! We have, however, managed to break something along the way. By updating our `processGuess` method to take a second parameter we have introduced a compiler error, since the controller only passes it one argument. It's easy to fix the compiler error by passing any old number to it, but we need to ensure we are processing a guess for the correct game and the only way to do that is to get the information from the user.

## Making a Dynamic Request

Our controller could, in theory, handle as many requests as we can think of. The problem with doing that is that we would have to define methods for each of them and remember what they all are. Imagine a news website with routes like this:

```
www.mynewssite.com/stories/awful-local-crime
www.mynewssite.com/stories/politician-in-trouble-again
www.mynewssite.com/stories/panda-escapes-from-zoo
```

It's long-winded, tough to remember and needs three different methods which will all be doing *very* similar things. What if we structured our routes in a more consistent style? Every story will have an ID associated with it, so why not use that?

```
www.mynewssite.com/stories/1
www.mynewssite.com/stories/2
www.mynewssite.com/stories/3
```

We still have three different routes but now they are more consistent - they all include a number after `/stories`. There isn't much difference to the client in that they still need to make a request to the correct route, but we will see when we move in to front end development that dynamically generating these will be much easier. The big difference will come when our server has to handle the request. Instead of several different methods we could write a route which will be able to take a dynamic number and use that to find the correct story.

In the context of our game, we can adapt the `PATCH` route in our controller to work in this dynamic way. We can't simply add a number to our existing route though, if we try that we will get a `404` error. We need to configure our route to accept this extra parameter. There are many ways to achieve this, but if we want to format our routes in a RESTful way (as in the example above) we need to introduce the `@PathVariable` annotation. 

The annotation isn't quite enough though, we need to modify the route as well. We will add a `value` argument to `@PatchMapping` to show what the route should look like.

```java title="controllers/GameController.java"
// controllers/GameController.java

@PatchMapping(value = "/{id}")
public ResponseEntity<Reply> handleGuess(@RequestBody Guess guess, @PathVariable int id) {
  	Reply reply = gameService.processGuess(guess, id);
   	return new ResponseEntity<>(reply, HttpStatus.OK);
}
```

Note that we don't need the fully-qualified route here - we already have `/games` from the `@RequestMapping` annotation so by adding `/{id}` we arrive at `localhost:8080/games/{id}`. The braces `{}` around the parameter indicate that it is not a fixed value and is acting as a placeholder. The value inside the braces can be passed into the method by annotating it as a `@PathVariable` - the name inside the braces and the parameter name **must** match. Although the request will be made as a string, the value should match the specified data type. If we request something that can't be an `int` here, for example `localhost:8080/games/play`, our controller won't be able to convert it into the correct type and will return a `500` error.

Once the value is accessible and passed to the game service we can have multiple games running at once and make sure we are playing the right one when we make a guess. Now that we have added logic to check win conditions we can even celebrate when we guess the correct word! Our celebrations will be fairly short-lived though as the games are lost as soon as we stop the app. We're also struggling with single responsibility again, which we will address in the next lesson.