package com.magmaguy.elitemobs.quests;

import com.magmaguy.elitemobs.playerdata.PlayerData;
import com.magmaguy.elitemobs.utils.WarningMessage;
import org.bukkit.entity.Player;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.UUID;

public class PlayerQuests implements Serializable {

    public Player player;
    public ArrayList<EliteQuest> quests = new ArrayList<>();

    public PlayerQuests(Player player) {
        this.player = player;
    }

    public static PlayerQuests getData(Player player) {
        return PlayerData.getQuestStatus(player.getUniqueId());
    }

    public static void addQuest(Player player, EliteQuest eliteQuest) {
        EliteQuest.addPlayerInQuests(player, eliteQuest);
        if (getData(player) == null) {
            new WarningMessage("Player data not initialized correctly! Restart the server and let the developer know!");
            return;
        }
        if (getData(player).quests != null) {
            getData(player).quests.add(eliteQuest);
        } else {
            ArrayList<EliteQuest> quests = new ArrayList<>();
            quests.add(eliteQuest);
            getData(player).quests = quests;
        }
    }

    public static void removeQuest(Player player, UUID uuid) {
        PlayerData.removeQuest(player.getUniqueId(), uuid);
    }

    public static boolean hasQuest(Player player, EliteQuest eliteQuest) {
        if (player == null) return false;
        if (getData(player) == null) return false;
        return getData(player).hasQuest(eliteQuest.getUuid());
    }

    public static void updateDatabase(Player player, PlayerQuests playerQuests) {
        PlayerData.setQuestStatus(player.getUniqueId(), playerQuests);
    }

    public boolean hasQuest(UUID uuid) {
        if (quests != null)
            for (EliteQuest eliteQuest : quests)
                if (eliteQuest.getUuid().equals(uuid))
                    return true;
        return false;
    }

}
