package com.eu.habbo.undo.commands;


import com.eu.habbo.Emulator;
import com.eu.habbo.habbohotel.commands.Command;
import com.eu.habbo.habbohotel.gameclients.GameClient;
import com.eu.habbo.habbohotel.rooms.Room;
import com.eu.habbo.habbohotel.users.Habbo;
import com.eu.habbo.undo.Undo;
import com.eu.habbo.undo.actions.FurniAction;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class UndoCommand extends Command {
    public UndoCommand(String permission, String[] keys) {
        super(permission, keys);
    }

    public boolean handle(GameClient gameClient, String[] strings) throws Exception {
        Habbo habbo = gameClient.getHabbo();
        if (habbo == null)
            return false;
        Room room = gameClient.getHabbo().getRoomUnit().getRoom();
        if (room == null)
            return false;
        Map<Integer, List<FurniAction>> furniActionList = (HashMap)(gameClient.getHabbo().getHabboStats()).cache.get(Undo.LAST_FURNI_ACTIONS);
        if (!furniActionList.containsKey(Integer.valueOf(room.getId()))) {
            gameClient.getHabbo().whisper(Emulator.getTexts().getValue("undo.cmd_undo.error"));
            return true;
        }
        List<FurniAction> furniActions = furniActionList.get(Integer.valueOf(room.getId()));
        if (furniActions.size() == 0) {
            gameClient.getHabbo().whisper(Emulator.getTexts().getValue("undo.cmd_undo.error"));
            return true;
        }
        FurniAction furniAction = furniActions.get(furniActions.size() - 1);
        furniAction.undo();
        furniActions.remove(furniActions.size() - 1);
        furniActionList.put(Integer.valueOf(room.getId()), furniActions);
        (gameClient.getHabbo().getHabboStats()).cache.put(Undo.LAST_FURNI_ACTIONS, furniActionList);
        gameClient.getHabbo().whisper(Emulator.getTexts().getValue("undo.cmd_undo.success"));
        return true;
    }
}
