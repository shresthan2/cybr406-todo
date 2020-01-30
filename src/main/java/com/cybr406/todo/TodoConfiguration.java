package com.cybr406.todo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Configuration
public class TodoConfiguration {

    @Autowired
    TodoRepository todoRepository;

    @Profile("!test")
    @Autowired
    public void configureDummyData(TodoRepository todoRepository) {
        addDemoTodo("What do you call a belt made out of watches? A waist of time.");
        addDemoTodo("What does the roof say to the house? I got you covered.");
        addDemoTodo("What did one ocean say to another ocean? Nothing they just waved.");
        addDemoTodo("Never trust an atom...They make up everything!");
        addDemoTodo("I'm reading a book about anti-gravity. I can't put it down.");
        addDemoTodo("To the guy who invented zero: Thanks for nothing!");
        addDemoTodo("I was up all night wondering where the sun went, but then it dawned on me.");
        addDemoTodo("I was wondering why the baseball was getting bigger. Then it hit me.");
        addDemoTodo("A backwards poet writes inverse.");
        addDemoTodo("Orion's Belt is a huge waist of space.");
        addDemoTodo("What did the grape say when it was stepped on? Nothing, it just let out a little wine.");
        addDemoTodo("Why can't a bicycle stand on its own? Because it's two tired.");
        addDemoTodo("What do you get from a pampered cow? Spoiled milk.");
        addDemoTodo("What did the monkey say when he caught his tail in the revolving door? It won't be long now!");
        addDemoTodo("Why shouldn't you write with a broken pencil? BECAUSE IT'S POINTLESS!");
        addDemoTodo("A neutron walks into a bar and asks, \"how much for a beer?\" The bartender says, \"for you? no charge.\"");
        addDemoTodo("How does an octopus go to war? WELL-ARMED!");
        addDemoTodo("What kind of horses go out after dusk? Nightmares!");
        addDemoTodo("A magician was driving down the road...then he turned into a drive way.");
        addDemoTodo("What washes up on tiny beaches? MICROWAVES!");
        addDemoTodo("Why do crabs never give to charity? Because they're shellfish.");
        addDemoTodo("Last night I dreamed that I was a muffler. I woke up exhausted.");
        addDemoTodo("Why did the skeleton hit the party solo? He had no body to go with him.");
        addDemoTodo("Why don't skeletons watch scary movies? They just don't have the guts.");
        addDemoTodo("A man walks into a zoo, the only animal was a dog. It was a shitzu.");
    }

    private void addDemoTodo(String details) {
        Todo todo = new Todo();
        todo.setAuthor("Anonymous");
        todo.setDetails(details);
        todoRepository.create(todo);
    }


}
