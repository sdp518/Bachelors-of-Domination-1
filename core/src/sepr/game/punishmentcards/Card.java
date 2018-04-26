package sepr.game.punishmentcards;


import sepr.game.Player;

public class Card {

    CardType type;

    public Card(CardType type) {
        this.type = type;
    }

    public void act(Player player) {

    }

    public CardType getType() {
        return this.type;
    }

}
