import java.io.IOException;
import java.util.*;

/**
 * Created by Colton on 4/1/2017.
 * Modified by Daniel on 4/1/2017 (added playTurn, move, questionTime,
 * decideChoise. Edited member variables as needed.
 */

// Colton:
// I created this class to have a space for writing game functionality.
// Any or all of this can be moved or deleted as you see fit.
public class Game {
    // Daniel:
    // the categories for each space on the gameboard
    // 0 = none
    // 1 = Sports
    // 2 = Science
    // 3 = Places
    // 4 = Events
    // 5 = Entertainment
    // 6 = Arts
    private static final int [] SPACE_CATEGORIES = {0, 5, 5, 0, 4, 3, 0, 2, 2, 2, 0,
    5, 3, 0, 6, 6, 6, 0, 2, 1, 0, 3, 3, 3, 0, 6, 4, 0, 1, 1, 1, 0, 3, 5, 0, 4, 4,
    4, 0, 1, 2, 0, 5, 6, 1, 4, 2, 5, 3, 4, 5, 6, 2, 1, 5, 2, 3, 6, 4, 2, 6, 1, 3,
    5, 6, 3, 4, 1, 2, 3, 1, 5, 4};
    
    // the number of players in the current game
    private final int numPlayers;
    // the array of player. Will be in turn order after game constructor
    private final Player[] players;
    // the deck of avaliable cards
    private final CardDeck cardDeck;
    
    // Constructor for game
    // takes in the array of players
    public Game(Player[] players) throws IOException {

        this.players = players;
        this.numPlayers = players.length;

        this.cardDeck = new CardDeck();
        
        // puts players in the players array based on turn order
        setPlayersTurnOrder(players);
    }
    
    // Colton:
    // This for-loop triple threat monstrosity is one way to solve the
    // "who goes first" RNG requirement without worrying about duplicates
    private void setPlayersTurnOrder(Player[] players) {

        ArrayList<Integer> turns = new ArrayList<>(numPlayers);

        for (int i = 0; i < this.numPlayers; i++)
            turns.add(i);

        // it's random?
        Collections.shuffle(turns);

        // Dish out the shuffled turn numbers
        for (int i = 0; i < numPlayers; i++)
            players[i].setTurnOrder(turns.get(i));

        // rearrange the players array to be in turn order instead
        // of creation order. That way we can easily loop-through
        // turn-based play.
        for (int i = 0; i < numPlayers; i++) {

            if (players[i].getTurnOrder() != i) {

                Player temp = players[i];
                players[i] = players[players[i].getTurnOrder()];
                players[temp.getTurnOrder()] = temp;

            }
        }
    }
    
    // Daniel:
    // runs through a single turn for a single player
    private void playTurn(Player player)
    {
        System.out.println(player.playerName + ", it's your turn!");
        
        //player rolls a die to decide movement spaces
        int roll = Die.rollThatSucker();
        System.out.println("You rolled a " + roll + ".");
        
        //move the player that number of spaces
        for(int i = 0; i < roll; i++)
            move(player);
        System.out.println("You're now on space " + player.position);
        
        //then ask the player their question
        Category temp = questionTime(player);
    }
    
    // Daniel:
    // iterates the player one space foreward. either front end can
    // just update after the entire move is completed, or after each space.
    // Once the player leaves a spoke, they will move around the board in a clockwise fasion.
    private void move(Player player)
    {
        switch (player.position)
        {
            // moves the player from the wheel space. this is random right now, but front end should
            // implement an actionListener to allow the player to choose which spoke they want to go to
            case 0:
                // heres what makes it random
                switch(Die.rollThatSucker())
                {
                    case 1: player.position = 43;
                    break;
                    case 2: player.position = 48;
                    break;
                    case 3: player.position = 53;
                    break;
                    case 4: player.position = 58;
                    break;
                    case 5: player.position = 63;
                    break;
                    case 6: player.position = 68;
                    break;
                }   break;
            //decides which space to move to if the player is at the edge of a spoke
            case 47:
            case 52:
            case 57:
            case 62:
            case 67:
            case 72:
                //fancy math magic
                player.position = (((player.position / 6) - 7) * 7) + 1;
                break;
            default:
                // if the player isn't at the center or on the edge of a spoke,
                // just iterate their position
                player.position++;
                break;
        }
    }
    
    // Daniel: (IN PROGRESS)
    // goes through the process of answering a question from start to finish
    // returns the category of a successfully completed question, null otherwise
    private Category questionTime(Player player)
    {
        Card card;
        // the user input was just for testing, front end should replace text input
        // with screen prompts and buttons
        Scanner userInput = new Scanner(System.in);
        //draw a card for the appropriate category. return null if on a white space
        switch(player.position)
        {
            case 1:
                card = cardDeck.drawRandomCard(Category.SPORTS);
                break;
            case 2:
                card = cardDeck.drawRandomCard(Category.SCIENCE);
                break;
            case 3:
                card = cardDeck.drawRandomCard(Category.PLACES);
                break;
            case 4:
                card = cardDeck.drawRandomCard(Category.EVENTS);
                break;
            case 5:
                card = cardDeck.drawRandomCard(Category.ENTERTAINMENT);
                break;
            case 6:
                card = cardDeck.drawRandomCard(Category.ARTS);
                break;
            default:
                return null;
        }
        //give the player the option to answer or stump
        System.out.println("Your question is:");
        System.out.println(card.question);
        System.out.println("Do you want to answer the question, or try and stump your opponents? (Enter 1 to answer or 2 to stump)");
        
        //if the player chooses to answer
        if(userInput.nextInt() == 1)
        {
            System.out.println("Here are your choices. Enter the number for your selection:");
            int i = 0;
            //give them their choices
            for(String s: card.choices)
                System.out.println(i++ + ". " + s);
            
            // get their response. return the card category if answered correctly,
            // null if incorrectly
            if(userInput.nextInt() == card.correctAnsIndex)
                return card.category;
            else
                return null;
        }
        //if the player chooses to stump (still commenting this)
        else
        {
            System.out.println(player.playerName + " has chosen to stump their opponents! Get ready everyone...");
            int [] temp = new int[card.choices.length];
            for(Player p: players)
            {
                if(p != player)
                {
                    int i = 0;
                    System.out.println(p.playerName + " enter the number of your selected choice.");
                    System.out.println(card.question);
                    for(String s: card.choices)
                    {
                        System.out.println(i++ + ". " + s);
                    }
                    temp[userInput.nextInt()]++;
                }
            }
            if(decideChoice(temp) == card.correctAnsIndex)
            {
                System.out.println("Uh-oh, your oppenents managed to stump you! They all moved forewar one space.");
                for(Player p: players)
                {
                    if(player != p)
                        move(p);
                }
            }
            else
            {
                System.out.println("Hooray! Your opponents couldn't answer the question so the " + card.category + "wedge goes to you.");
                // TODO: Move player to respective home space. This also needs
                //to be done if the player answers their own question correctly
            }
        }
        
    }
    
    private int decideChoice(int [] choiceFrequency)
    {
        int highestFrequency = 0;
        int tieBreaker = 0;
        boolean [] choices = new boolean[choiceFrequency.length];
        
        //run through the selected choices to find the highest observed choice frequency
        for(Integer i: choiceFrequency)
        {
            if(highestFrequency < i)
                highestFrequency = i;
        }
        
        //run though it again, flipping the boolean associated with choices with the highest frequency
        int j = 0;
        for(Integer i: choiceFrequency)
        {
            if(highestFrequency == i)
            {
                choices[j] = true;
                tieBreaker++;
            }
            j++; 
        }
        
        j = (int)(Math.random() * tieBreaker) + 1;
        int i = 0;
        for(Boolean b: choices)
        {
            if(b)
            {
                if(--j == 0)
                    return i;
            }
            
            i++;
        }
        // It should never get down to here. If this ever returns -1, theres a problem.
        return -1;
    }

    // this is just a test drive. I'm assuming we'll get player array from
    // the GUI class
    public static void main(String[] args) throws IOException {
        Player[] testPlayers = new Player[3];
        for (int i = 0; i < testPlayers.length; i++) {
            testPlayers[i] = new Player("Shia LaBeouf" + Integer.toString(i));
        }
        Game game = new Game(testPlayers);
        for (Player player : testPlayers) {
            System.out.println(player.getPlayerName() + " " + player.getTurnOrder());
        }
        
        System.out.println(game.players[0].position);
        for(int i = 0; i < 6; i++)
            game.move(game.players[0]);
        
        System.out.println(game.players[0].position);
    }
}