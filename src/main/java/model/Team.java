package model;

public enum Team {
    RED("Red"),
    BLUE("Blue");

    private String teamName;

    Team(String teamName) {
        this.teamName = teamName;
    }

    public String getTeamName() {
        return teamName;
    }

    public void setTeamName(String teamName) {
        this.teamName = teamName;
    }
}
