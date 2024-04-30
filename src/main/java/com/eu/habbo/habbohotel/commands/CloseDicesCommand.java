package com.eu.habbo.habbohotel.commands;

import com.eu.habbo.Emulator;
import com.eu.habbo.habbohotel.gameclients.GameClient;
import com.eu.habbo.habbohotel.items.interactions.InteractionDice;
import com.eu.habbo.habbohotel.rooms.Room;
import com.eu.habbo.habbohotel.rooms.RoomChatMessageBubbles;
import com.eu.habbo.habbohotel.rooms.RoomLayout;
import com.eu.habbo.habbohotel.users.Habbo;
import com.eu.habbo.habbohotel.users.HabboItem;

public class CloseDicesCommand extends Command {

    public CloseDicesCommand() {
        super(null, Emulator.getTexts().getValue("commands.keys.cmd_closedices").split(";"));
    }

    @Override
    public boolean handle(GameClient gameClient, String[] params) {
        Habbo self = gameClient.getHabbo();

        if (self.getRoomUnit() == null)
            return false;

        Room room = self.getRoomUnit().getRoom();

        if (room == null)
            return false;

        if (params.length == 1) {
            for (HabboItem item : room.getFloorItems())
                if (item instanceof InteractionDice && self.getRoomUnit().get2DPosition().distance(item.get2DPosition()) < 1.5 && !item.getExtradata().equalsIgnoreCase("0"))
                    this.closeDice(item, room);

            room.resetUserDicesRolls(self.getHabboInfo().getId());

            self.shout(Emulator.getTexts().getValue("commands.success.cmd_closedices"), RoomChatMessageBubbles.FRANK);
        } else {
            self.whisper(Emulator.getTexts().getValue("commands.error.invalid_arguments"), RoomChatMessageBubbles.ALERT);
        }

        return true;
    }

    private void closeDice(HabboItem item, Room room) {
        item.setExtradata("0");
        item.needsUpdate(true);
        room.updateItem(item);
    }

}