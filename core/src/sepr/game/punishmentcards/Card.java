package sepr.game.punishmentcards;


import sepr.game.GameScreen;
import sepr.game.Player;

public class Card {

    CardType type;
    boolean playerRequired;
    boolean affectsNeutral;

    public Card(CardType type, boolean playerRequired, boolean affectsNeutral) {
        this.type = type;
        this.playerRequired = playerRequired;
        this.affectsNeutral = affectsNeutral;
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

    public boolean getAffectsNeutral() {
        return affectsNeutral;
    }

    public static Card initiateCard(CardType cardType) {
        switch (cardType) {
            case STRIKE: return new Strike();
            case FRESHERS_FLU: return new FreshersFlu();
            case GOLDEN_GOOSE: return new GoldenGoose();
            case PLAGUE_OF_GEESE: return new PlagueOfGeese();
            case CRIPPLING_HANGOVER: return new CripplingHangover();
            case EXCEPTIONAL_CIRCUMSTANCES: return new ExceptionalCircumstances();
            default: throw new IllegalArgumentException("That card doesn't exist");
        }
    }

    @Override
    public String toString() {
        return this.type.toString();
    }

}
