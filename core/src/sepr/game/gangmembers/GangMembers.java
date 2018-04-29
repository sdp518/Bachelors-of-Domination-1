package sepr.game.gangmembers;

public class GangMembers {
    private String name;
    private boolean castUsed;

    public GangMembers(String name) {
        this.name = name;
        this.castUsed = false;
    }

    public String getName() {
        return this.name;
    }

    public Boolean getCastUsed() {
        return this.castUsed;
    }

    public void setCastStatus(Boolean status) {
        this.castUsed = status;
    }
}
