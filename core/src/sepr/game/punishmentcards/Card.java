package sepr.game.punishmentcards;


import sepr.game.GameScreen;
import sepr.game.Player;

public class Card {

    CardType type;

    public Card(CardType type) {
        this.type = type;
    }

    public void act(Player player, GameScreen gameScreen) {

    }

    public CardType getType() {
        return this.type;
    }

}
