# What is Visualization Driven Development?

**TODO reread all of this and correct as necessary.**

Visualization Driven Development (VDD) is the practice of using interactive visualizations to understand the algorithms of our applications. When you were first learning software development you might have learned to develop with something like [LOGO](http://en.wikipedia.org/wiki/Logo_(programming_language)) or [Scratch](http://scratch.mit.edu). Both of those programming languages and others like them operate in a visual environment. The execution of your code is shown visually as it executes. This has profound implications on our ability to quickly understand how code is executed and find and correct problems. 

It's not only useful but also _fun_. Part of the joy of programming is the joy of creating something. It's extremely satisfying to create something that works, is useful, and solves peoples' problems. Modern software development stunts that. The concepts of classes, objects, functions, and data are often invisible inside the computer. Visualizations can help make them more tangible and alive. Visualization Driven Development is extremely satisfying that way. 

When you can _see_ what your code is doing you _know_ what your code is doing. This is a great way to teach the ideas of programming and computing but why does it have to stop there? We can bring the this idea to modern software development. 

## Steps

These are the steps to follow Visualization Driven Development.

### Step 1. Think

There's no excuse not to consider the long view when developing software. Don't let some _letter_ DD (as in TDD, BDD, VDD etc.) replace thinking about the main goal and the path to get there. The first step in developing software (or solving any problem for that matter) is thinking about the problem. 

Think about what you're trying to solve. What's the actual problem? What's the likely solution? How can you visualize the solution? **(TODO link to How can you Visualize Code)**

Once you've got answers or possible answers to those questions write them down. 

### Step 2. Build a (Minimal) Visualization

Build a minimal version of the visualization that should show your code executing. You can mock out that data that you expect to produce from your executing code. 

We need to have a visualization in place to see something while we are developing. It's useful to know how you want to visualize the code execution before you write the code so that you know what data to capture during code execution. See Designing a Visualization (TODO link) later in this document for guidelines on creating a visualization. There's no hard and fast rule that this has to come before you write your code.

### Step 3. Write Some Code

Start writing the main code of your application. Write just enough code to make the visualization you built in step 2 show something.

### Step 4. Iterate

This step is similar to Test Driven Development with the added ability to visualize your code. You should have some code that you can execute and visualize at this point. You should be able to see some problems with your code using your visualization. The first thing to do is to add a test that will capture the problem that the visualization shows. Then correct the problem in the code. Next go to the visualization and look for another problem with the execution. If you've built an interactive visualization that can execute your code you can use that to test various scenarios. Or you can exercise your code from the REPL and send the data to the visualization that way.

## Designing a Visualization

The details indicated here are just one set of ways to think about designing a visualization. There are many other resources available online and in book form to help design visualizations. 

We should design a visualization knowing the audience and the purpose. The audience is you and your fellow developers. This is much easier than building a normal visualization for the web. You know what information needs to be conveyed to explain the idea. You know the metaphors that will make sense. You know technical details like what browsers the audience will be using. The purpose of the visualization is to communicate what's happening inside the computer when you're code is running. It should be communicated at a level that makes sense for the audience. IE. we don't need to worry about the behavior of electrons in piece of copper.

The trick is that you're not visualizing code. You're visualizing code _execution_. You want to understand the computer's interpretation of your code. We know that [code can be thought of as data](http://en.wikipedia.org/wiki/Homoiconicity). Code execution can also be captured as data. (See the 2013 Clojure/West talk by Zach Tellman [Code Execution as Data](http://www.infoq.com/presentations/analyze-running-system).) VDD Core is one way that this data can be captured and sent to the browser for visualization. Once the data in a browser we can visualize it in numerous ways using the data visualization tools that already exist.

### Visualizing Time

#### Simple

Consider how you want to view code execution over time. It could be all shown in a single visualization that captures data changing across time. Traditional charts like the line graph, scatter plot, and bar charts are good for showing simplistic data over time. These are also easy to implement in many different libraries. 

#### Timelines

Timelines are another way to visualize change over time. The developer tools built into your web browser usually provide several timelines to show network requests, rendering, and JavaScript events. The timeline can show all of the overall events and allow drilling down into events by selecting them. Selecting an event would provide a mini visualization of the data in the event. 

#### Animations

Code execution can also be shown by visualizing each discrete chunk of data from a sequence as a separate "frame" in an animation. The [player component](player.md) (TODO fix link to player component.) combined with a visualization showing each step can be used to show this. 

Or you may not care about visualizing execution over time. You may only care about viewing the final state.

### Visualizing Nouns

Once you've thought about how to visualize the 
TODO finish that thought

#### 1. Identify Entities (AKA Nouns)

Identify the _things_ in your code that you want to visualize. If you're using Object Oriented Development these would most likely be similar to the classes that you've defined. If you're using Functional Programming these would be the names you would use for the data flowing through your functions. You should pick the entities you think are important to communicate what's happening.

#### 2. Choose a Visual Representation for Each Entity.

The next step is to choose a visual representation for the entities you picked in step 1. Step 2 often has to be done in concert with Step 3, Choose a Layout. Depending on the layout chosen the visual representation of each noun may be different. It's useful to treat this as a separate step in which you consider all of the possible visual representations for each entity. 

If the entity is simple like a string name of something it can just be the text. Numbers can also be represented as text. Depending on what the number represents it can be more useful to include another representation of the number that's visually distinct. This could be the size of a drawn object like the length of a bar or the radius of a circle. Or it could be represented by a color or the opacity of an object. The number as text should always be visible or accessible via a tooltip alongside it's visual representation.

More complicated entities can be represented by a combination of visual entities. Each attribute of the entity becomes a characteristic of the visual representation (where that makes sense).

#### 3. Choose a Layout

After creating a list of the possibly entity visual representations in Step 2 you need to choose how to layout the entities. 

TODO later indicate you can use a D3 layout https://github.com/mbostock/d3/wiki/Layouts  Things can be horizontal with bars to create a bar chart. There are many different ways

#### Keep it Simple

TODO we don't need to over do things. Sometimes the simplest thing is to pretty print data as an HTML list

### Representing Sequential Execution

TODO talk about sequential execution should be captured in a sequence type like a list or vector. Link to the player docs. May want to just move this section there.

### Interactivity

TODO indicate that visualizations should ideally have an interactive component. This will allow the code to be tested from the visualization. Alternatively you can interact using the REPL. Exercise your code from the REPL. This should capture some data which you will send to the visualization. 


## FAQ

TODO potentially move this to it's own page.
TODO answer these questions

### What is Visualization Driven Development?
TODO provide a short summary here
TODO link to what is visualization driven development

### How can I visualize my code?

TODO answer

### What are the benefits of visualizing my code?

TODO answer

### Isn't this just a big yak shave?

TODO answer

### What about visually impaired software developers?

Software develoment should be tangible and concrete. Expressing information aurally through [sonification](http://en.wikipedia.org/wiki/Sonification) is an area that could be explored more. vdd-core's goal is to make it easy to deliver information to the browser for visualization. An "auditory display" of data could be used in the browser in place of or alongside a traditional visualization. 