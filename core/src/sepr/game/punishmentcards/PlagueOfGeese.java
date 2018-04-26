package sepr.game.punishmentcards;


import sepr.game.Player;

public class PlagueOfGeese extends Card {
    public PlagueOfGeese() {
        super(CardType.PLAGUE_OF_GEESE);
    }

    /**
     * Effect: Remove 3 units from all sectors of affected player
     */
    @Override
    public void act(Player player) {
        System.out.println(player.getPlayerName() + ": " + type.getCardType());
    }
}
