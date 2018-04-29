package sepr.game.punishmentcards;

public enum CardType {
    EXCEPTIONAL_CIRCUMSTANCES("EXCEPTIONAL CIRCUMSTANCES"),
    PLAGUE_OF_GEESE("PLAGUE OF GEESE"),
    FRESHERS_FLU("FRESHERS FLU"),
    GOLDEN_GOOSE("GOLDEN GOOSE"),
    STRIKE("STRIKE"),
    CRIPPLING_HANGOVER("CRIPPLING_HANGOVER");

    private final String shortCode;

    CardType(String code){
        this.shortCode = code;
    }

    public String getCardType(){
        return this.shortCode;
    }

    /**
     * converts the string representation of the enum to the enum value
     * @throws IllegalArgumentException if the text does not match any of the enum's string values
     * @param text string representation of the enum
     * @return the enum value of the provided text
     */
    public static CardType fromString(String text) throws IllegalArgumentException {
        for (CardType cardType : CardType.values()) {
            if (cardType.getCardType().equals(text)) return cardType;
        }
        throw new IllegalArgumentException("Text parameter must match one of the enums");
    }

    @Override
    public String toString() {
        return this.shortCode;
    }
}
