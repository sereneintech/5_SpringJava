# POJOs, DTOs & JSON

### Learning Objectives:

- Know what JSON is and understand the benefits of using it in an API
- Know what a POJO is and how they differ from the Java classes we have worked with previously
- Know what a DTO is and when it is appropriate to use one

We have made our first API and returned a message to the user, which is a fantastic start! The building blocks are in place for us to expand and make something more complex. We aren't quite done though - as with everything we've done so far, there are some things we need to consider which can both simplify our development process and hugely increase the quality of our app.

Our first port of call will be the value which we return to the user. For a human reading a message on a screen the simple "Hello World!" message in our last app will suffice but it's not usually going to be a human interacting with the API. Instead it will be another application, potentially a web app, mobile app or even another Spring API. Strings will be much less useful to them and in this lesson we will consider an alternative.

## Responding With JSON

An API sending back a simple data type, such as a string, number or boolean, can have some use. If we want a single value then it could arguably be the best way to transmit data since it minimises the effort required by the client to parse out the information. As soon as the data gets more complicated, however, sending it as a string becomes less useful. If we were to build an API with details of the lessons planned this week we might have an end point which would give a list of all the lessons and who was teaching each. If we send the data as a string it might look something like this:

```
"Zsolt will teach the first lesson - What is an API?,
Anna will teach the second lesson - What is Spring?,
Colin will teach the third lesson - POJOs, DTOs & JSON,
..."
```

For a human this makes sense, but for another program it's a nightmare. While we have the ability to read some context into what we see and parse out the relevant details, our server's client won't be able to do this. It will see a `String` object and be limited to doing whatever its language allows to be done with strings. It may be able to use a regular expression to pattern-match certain details but none of the operations usually associated with data structures will work.

We can address this by presenting our data in **JavaScript Object Notation**, or **JSON**. Instead of a string or a number we can send data organised into key-value pairs. Each language or framework may handle them differently but they can all handle the concept - in Java we have seen key-value pairs in the form of `Map`s. In JSON every key **must** be a string, but the values can be strings, numeric types, booleans or further collections. The syntax means anything written in JSON is also valid JavaScript code, however the inverse is not necessarily true.

If we were to replicate the data above in JSON we might construct it like this:

```json
{
	"lessons": [
		{
			"lessonNumber": 1,
			"trainer": "Zsolt",
			"title": "What is an API?"
		},
		{
			"lessonNumber": 2,
			"trainer": "Anna",
			"title": "What is Spring?"
		},
		{
			"lessonNumber": 3,
			"trainer": "Colin",
			"title": "POJOs, DTOs & JSON"
		},
		<!-- ... -->
	]
}
```

We have an object with a single key-value pair, with the value being a list of objects which each have the same format. Although this is harder for a human to read it is *much* more accessible for another program. We will likely need to do some sort of conversion first, but every language will be able to look up the `lessons` key in the object and every language will be able to iterate over the list it holds.

When working with a Spring app the process of converting our Java objects happens automatically through a process known as **serialisation**. This still requires some configuration from the user though and needs us to write our code in a very specific way to ensure it can happen. A vital part of this is constructing our objects in the correct way.

## Creating Our First POJO

All of the classes we have defined so far have had two things in common: they all have **properties** defining what they *have* and **behaviours** defining what they *do*. Included in those behaviours have been public *getters* and *setters* to access and update the properties. Many of our classes have included additional methods to give extra functionality, for example to add elements to lists or to facilitate interaction between classes.

Objects instantiated from classes without any of these extra methods are known as **Plain Old Java Objects** (**POJOs**). To create a POJO a class must meet some requirements:

- `private` properties
- `public` getters and setters for **every** property
- An overloaded constructor with no parameters and an empty body
- No additional methods

POJOs are used in many programs for many different purposes, but in our Spring applications we will use them to create objects representing data we want to input, store or pass between different parts of the app. We will still need the additional methods to enable our POJOs to interact but instead of defining them in the class we will abstract them away to a separate part of the app (more on this in a later lesson).

### Setting up our Application

This week we will slowly build up a single application, adding new features as we go. We're going to make a guessing game which, by the end of the week, will expose functionality for the user to register a new player, start a new game and guess which letters are in a mystery word. It wil also be possible to see records of previous games and who played each one. First we need to create the app, which we will do using the [Spring Initializr](https://start.spring.io/)

> Click the link above and select the following options:
> 
> - Project: Maven
> - Language: Java
> - Spring Boot Version: 2.7.3
> - Update "artifact" in the meta-data to "word_guesser". Add a description if you wish.
> - Packaging: Jar
> - Java: 17
> 
> Add the dependencies:
> 
> - Web
> - DevTools
> 
> We will add more to `pom.xml` in future lessons
>

We have the skeleton of an application which will receive HTTP requests and send HTTP responses. Thanks to the "Web" dependency we will have access to tools supporting the serialisation and de-serialisation of JSON objects.

Our first step will be to define a class which will be used to instantiate a POJO representing a game. There are differing conventions around organising our code when building an app like this. Some developers advocate gathering all related files in a single package, for example everything relating to games collected together. Others take the approach we will here, where all classes which do a similar job will be kept in packages together. We will create two packages now: one called `models` to hold our POJO classes and another called `controllers` to hold the controller classes.

Inside `models` we need to create a class to define a `Game`.

```java title="models/Game.java"
// models/Game.java

public class Game {

 	private String word;
   	private int guesses;
   	private boolean complete;

   	public Game(String word, int guesses, boolean complete) {
     	this.word = word;
      	this.guesses = guesses;
       this.complete = complete;
   	}

   	public Game() {
   	}

	// Getters & Setters

}
```

Each game will have properties defining the mystery word, how many guesses have been made and if it has been completed or not. Note that we still have a constructor which takes parameters in addition to the empty constructor required. This is because we still need to be able to instantiate `Game` objects ourselves within the application without necessarily de-serialising JSON to do so.

We also need a controller to handle the requests made to our API. Inside the `controllers` package we will define `GameController`.

```java title="controllers/GameController.java"
// controllers/GameController.java

@RestController
@RequestMapping(value = "/games")
public class GameController {

}
```

Our controller is annotated as a `@RestController` which means it is pre-configured to process the HTTP requests we will be making. This annotation will also enable the serialisation process which converts our Java objects into JSON; without it we would need to implement the `Serializable` interface on each of our POJO classes. 

We also add the `@RequestMapping` annotation and pass it the argument `"/games"`. This annotation helps to direct incoming requests to the right place and as a result simplifies the methods we will add to the controller somewhat. With this annotation in place any requests made to a route beginning `http://localhost:8080/games` will be handled by this controller.

We are, however, lacking a method to handle a request to that route. We will add one which will return a new `Game` object.

```java title="controllers/GameController.java"
// controllers/GameController.java

@RestController
@RequestMapping(value = "/games")
public class GameController {

	@GetMapping
   	public Game newGame(){
      	return new Game("hello");
   	}

}
```

We can now handle a `GET` request to `http://localhost:8080/games`. Our return type is specified as `Game` but that doesn't mean we'll be sending a Java object back to the client. Instead our `newGame()` method must return a `Game` object which will be serialised into JSON, then sent to the client. At the moment we don't have any logic around playing or even starting a game so we will create a new object with a placeholder "mystery" word.

So far it doesn't look like anything significant has changed - we're still writing routes and returning something from the associated method, we're just returning a `Game` now instead of a `String`. Our client, however, will see a big difference in the result. Making a request to `http://localhost:8080/games` (either through a browser or using a GUI such as [Postman](https://www.postman.com/)) now gives a very different result:

```json
{
  	"word": "hello",
   	"guesses": 0,
   	"complete": false
}
```

The client is getting JSON, much more useful than a string! The keys in the object correspond to the properties defined for `Game` with the values representing those of the `Game` POJO returned from the method. Wherever the request came from, the client can now easily incorporate this information into their own app.

Note that we need getters and setters for **all** of the properties in the POJO. If any are missing then the serialisation process will still work, but will omit some information. For example, commenting out the getter and setter for the `completed` property results in the `completed` key being missing from the JSON.

```json
{
  	"word": "hello",
   	"guesses": 0
}
```

## Passing Data Around the Application

Our application is much more client-friendly from a programming perspective, but it has a significant flaw as a game. All we can do at the moment is return a `Game` object, which means we need to expose the mystery word to the client. We need some way of confirming that the game has started without immediately giving away the solution.

"POJO" is a pretty broad classification for objects. Not all POJOs will be used for the ame thing, in fact we can augment some of them in such a way as to use them in very specific scenarios. For example, later this week we will add annotations to some of them to enable serialisation into a format compatible with a PostgreSQL database. Not all of them need to represent some real-world concept in the way `Game` does either.

It is very common to use POJOs to facilitate communication between different parts of an application. We call these **Data Transfer Objects** or **DTOs** and while they don't necessarily represent a real-world concept they do provide a standardised structure for transferring data. They aren't restricted to internal use, they can be used to customise responses too. In this application we will create a `Reply` model which we will use to provide a framework for communicating with the client. We won't replace `Game`, but we will no longer need to expose everything about a new game to the user when they start one.

```java title="models/Reply.java"
// models/Reply.java

public class Reply {

   	private String wordState;
   	private String message;

   	public Reply(String wordState, String message) {
      	this.wordState = wordState;
      	this.message = message;
   	}

   	public Reply() {
   	}

	// Getters & Setters

}
```

We can create a `Reply` DTO in our controller to avoid sending the entire game back to the client.

```java title="controllers/GameController.java"
// controllers/GameController.java

@RestController
@RequestMapping(value = "/games")
public class GameController {

	@GetMapping
   	public Reply newGame(){
      	Game game = new Game("hello");
      	return new Reply("*****", "New game started");
   	}

}
```

We still need to create the game, although for now we aren't accessing any of its details. Ultimately everything will be dynamic and the `wordState` argument will be defined according to the mystery word, which will itself be randomly generated. For now we hard-code the value. We also pass a String describing what happened when the request was processed. This won't always be useful or even necessary but it can be a useful tool for a client-side developer trying to interpret the response.

Our JSON response now looks a little different:

```json
{
   	"wordState": "*****",
   	"message": "New game started"
}
```

This is much better from a gameplay perspective! The problem now is that we can't actually *play* the game. To do that we will need to add more routes to our controller, but we need to do so in an appropriate way to ensure our API remains as intuitive and easy to use as possible.