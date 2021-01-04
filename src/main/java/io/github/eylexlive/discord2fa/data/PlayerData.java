package io.github.eylexlive.discord2fa.data;

import net.dv8tion.jda.api.entities.User;

import java.util.UUID;

/*
 *	Created by EylexLive on Feb 23, 2020.
 *	Currently version: 3.4
 */

public class PlayerData {

    private final UUID uuid;

    private String checkCode, confirmCode;

    private int playerTaskID, leftRights;

    private User confirmUser;

    public PlayerData(UUID uuid) {
        this.uuid = uuid;
    }

    public UUID getUuid() {
        return uuid;
    }

    public String getCheckCode() {
        return checkCode;
    }

    public void setCheckCode(String checkCode) {
        this.checkCode = checkCode;
    }

    public String getConfirmCode() {
        return confirmCode;
    }

    public void setConfirmCode(String confirmCode) {
        this.confirmCode = confirmCode;
    }

    public int getPlayerTaskID() {
        return playerTaskID;
    }

    public void setPlayerTaskID(int playerTaskID) {
        this.playerTaskID = playerTaskID;
    }

    public int getLeftRights() {
        return leftRights;
    }

    public void setLeftRights(int leftRights) {
        this.leftRights = leftRights;
    }

    public User getConfirmUser() {
        return confirmUser;
    }

    public void setConfirmUser(User confirmUser) {
        this.confirmUser = confirmUser;
    }

    @Override
    public boolean equals(Object obj) {
        return super.equals(obj);
    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }
}
