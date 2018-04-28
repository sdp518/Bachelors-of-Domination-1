package sepr.game.punishmentcards;


import sepr.game.GameScreen;
import sepr.game.Player;

public class Card {

    CardType type;
    boolean playerRequired;

    public Card(CardType type, boolean playerRequired) {
        this.type = type;
        this.playerRequired = playerRequired;
    }

    public void act(Player player, GameScreen gameScreen) {

    }

    public void act(GameScreen gameScreen) {

    }

    public CardType getType() {
        return this.type;
    }

    public boolean getPlayerRequired() {
        return playerRequired;
    }

}
