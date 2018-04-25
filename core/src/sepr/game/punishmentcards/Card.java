package sepr.game.punishmentcards;


public class Card {

    CardType type;

    public Card(CardType type) {
        this.type = type;
    }

    public void act() {

    }

    public CardType getType() {
        return this.type;
    }

}
