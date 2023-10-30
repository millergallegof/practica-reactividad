package com.example.demo;

public class NationalityPlayerDTO {
    private String nationality;
    private Player player;

    public NationalityPlayerDTO(String nationality, Player player) {
        this.nationality = nationality;
        this.player = player;
    }

    public String getNationality() {
        return nationality;
    }

    public void setNationality(String nationality) {
        this.nationality = nationality;
    }

    public Player getPlayer() {
        return player;
    }

    public void setPlayer(Player player) {
        this.player = player;
    }
}
