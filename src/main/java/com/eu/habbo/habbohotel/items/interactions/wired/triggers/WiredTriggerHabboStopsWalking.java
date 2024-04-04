package com.eu.habbo.habbohotel.items.interactions.wired.triggers;

import com.eu.habbo.Emulator;
import com.eu.habbo.habbohotel.items.Item;
import com.eu.habbo.habbohotel.items.interactions.InteractionWiredTrigger;
import com.eu.habbo.habbohotel.items.interactions.wired.WiredSettings;
import com.eu.habbo.habbohotel.rooms.Room;
import com.eu.habbo.habbohotel.rooms.RoomUnit;
import com.eu.habbo.habbohotel.users.Habbo;
import com.eu.habbo.habbohotel.wired.WiredHandler;
import com.eu.habbo.habbohotel.wired.WiredTriggerType;
import com.eu.habbo.habbohotel.wired.highscores.WiredHighscoreMidnightUpdater;
import com.eu.habbo.messages.ServerMessage;
import com.eu.habbo.plugin.EventHandler;
import com.eu.habbo.plugin.events.emulator.EmulatorLoadedEvent;
import com.eu.habbo.plugin.events.roomunit.RoomUnitStopsWalkingEvent;
import gnu.trove.set.hash.THashSet;

import java.sql.ResultSet;
import java.sql.SQLException;

public class WiredTriggerHabboStopsWalking extends InteractionWiredTrigger {

    public static final WiredTriggerType type = WiredTriggerType.HABBO_STOPS_WALKING;

    private String username = "";

    public WiredTriggerHabboStopsWalking(ResultSet set, Item baseItem) throws SQLException {
        super(set, baseItem);
    }

    public WiredTriggerHabboStopsWalking(int id, int userId, Item item, String extradata, int limitedStack, int limitedSells) {
        super(id, userId, item, extradata, limitedStack, limitedSells);
    }

    @Override
    public boolean execute(RoomUnit roomUnit, Room room, Object[] stuff) {
        if (room == null)
            return false;

        Habbo habbo = room.getHabbo(roomUnit);
        if (habbo == null)
            return false;

        return this.username.isEmpty() || habbo.getHabboInfo().getUsername().equalsIgnoreCase(this.username);
    }

    @Override
    public String getWiredData() {
        return WiredHandler.getGsonBuilder().create().toJson(new JsonData(
                this.username
        ));
    }

    @Override
    public void loadWiredData(ResultSet set, Room room) throws SQLException {
        String wiredData = set.getString("wired_data");

        if (Emulator.isValidJSON(wiredData)) {
            JsonData data = WiredHandler.getGsonBuilder().create().fromJson(wiredData, JsonData.class);
            this.username = data.username;
        } else {
            this.username = wiredData;
        }
    }

    @Override
    public void onPickUp() {
        this.username = "";
    }

    @Override
    public WiredTriggerType getType() {
        return type;
    }

    @Override
    public void serializeWiredData(ServerMessage message, Room room) {
        message.appendBoolean(false);
        message.appendInt(5);
        message.appendInt(0);
        message.appendInt(this.getBaseItem().getSpriteId());
        message.appendInt(this.getId());
        message.appendString(this.username);
        message.appendInt(0);
        message.appendInt(0);
        message.appendInt(this.getType().code);
        message.appendInt(0);
        message.appendInt(0);
    }

    @Override
    public boolean saveData(WiredSettings settings) {
        this.username = settings.getStringParam();

        return true;
    }

    @Override
    public boolean isTriggeredByRoomUnit() {
        return true;
    }

    static class JsonData {
        String username;

        public JsonData(String username) {
            this.username = username;
        }
    }

    @EventHandler
    public static void onRoomUnitStopsWalking(RoomUnitStopsWalkingEvent event) {
        WiredHandler.handle(WiredTriggerType.HABBO_STOPS_WALKING, event.roomUnit, event.room, null);
    }

}
