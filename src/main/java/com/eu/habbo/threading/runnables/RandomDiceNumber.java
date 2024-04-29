package com.eu.habbo.threading.runnables;

import com.eu.habbo.Emulator;
import com.eu.habbo.habbohotel.items.interactions.InteractionColorWheel;
import com.eu.habbo.habbohotel.rooms.Room;
import com.eu.habbo.habbohotel.rooms.RoomChatMessageBubbles;
import com.eu.habbo.habbohotel.rooms.RoomUnit;
import com.eu.habbo.habbohotel.users.Habbo;
import com.eu.habbo.habbohotel.users.HabboItem;

import java.util.List;

public class RandomDiceNumber implements Runnable {
    private final HabboItem item;
    private final Room room;
    private final RoomUnit roomUnit;
    private final int maxNumber;
    private int result;

    public RandomDiceNumber(HabboItem item, Room room, int maxNumber, RoomUnit roomUnit) {
        this.item = item;
        this.room = room;
        this.roomUnit = roomUnit;
        this.maxNumber = maxNumber;
        this.result = -1;
    }

    public RandomDiceNumber(Room room, HabboItem item, int result, RoomUnit roomUnit) {
        this.item = item;
        this.room = room;
        this.roomUnit = roomUnit;
        this.maxNumber = -1;
        this.result = result;
    }

    @Override
    public void run() {
        if (this.result <= 0) {
            this.result = Emulator.getRandom().nextInt(this.maxNumber) + 1;
            if (this.roomUnit != null) {
                Habbo habbo = this.room.getHabbo(this.roomUnit);
                this.room.addUserDiceRoll(habbo.getHabboInfo().getId(), this.result);
                final List<Integer> userDicesRolls = this.room.getUserDicesRolls(habbo.getHabboInfo().getId());
                int sum = userDicesRolls.stream().mapToInt(Integer::intValue).sum();
                habbo.talk("Dé " + userDicesRolls.size() + ": +" + this.result + " = " + sum + ".", RoomChatMessageBubbles.FRANK);
            }
        }

        this.item.setExtradata(this.result + "");
        this.item.needsUpdate(true);
        Emulator.getThreading().run(this.item);

        this.room.updateItem(this.item);
        if (this.item instanceof InteractionColorWheel) {
            ((InteractionColorWheel) this.item).clearRunnable();
        }
    }
}
