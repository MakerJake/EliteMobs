package com.magmaguy.elitemobs.commands.quest;

import org.bukkit.entity.Player;

public class QuestCommand {

    public QuestCommand(Player player) {
        doMainQuestCommand(player);
    }

    public static void doMainQuestCommand(Player player) {
        player.sendMessage("Coming soon!");
        //QuestsMenu.initializeQuestsMenu(player);
    }

}
