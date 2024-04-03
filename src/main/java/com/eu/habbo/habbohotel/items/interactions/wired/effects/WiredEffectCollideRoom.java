package com.eu.habbo.habbohotel.items.interactions.wired.effects;

import com.eu.habbo.Emulator;
import com.eu.habbo.habbohotel.gameclients.GameClient;
import com.eu.habbo.habbohotel.items.Item;
import com.eu.habbo.habbohotel.items.interactions.InteractionWiredEffect;
import com.eu.habbo.habbohotel.items.interactions.wired.WiredSettings;
import com.eu.habbo.habbohotel.rooms.Room;
import com.eu.habbo.habbohotel.rooms.RoomUnit;
import com.eu.habbo.habbohotel.users.HabboItem;
import com.eu.habbo.habbohotel.wired.WiredEffectType;
import com.eu.habbo.habbohotel.wired.WiredHandler;
import com.eu.habbo.messages.ServerMessage;
import com.eu.habbo.messages.incoming.wired.WiredSaveException;
import com.eu.habbo.threading.runnables.WiredCollissionRunnable;
import gnu.trove.set.hash.THashSet;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class WiredEffectCollideRoom extends InteractionWiredEffect {

    public static final WiredEffectType type = WiredEffectType.COLLIDE_ROOM;

    private boolean collideEveryone = true;

    public WiredEffectCollideRoom(ResultSet set, Item baseItem) throws SQLException {
        super(set, baseItem);
    }

    public WiredEffectCollideRoom(int id, int userId, Item item, String extradata, int limitedStack, int limitedSells) {
        super(id, userId, item, extradata, limitedStack, limitedSells);
    }

    @Override
    public boolean execute(RoomUnit roomUnit, Room room, Object[] stuff) {
        if (room == null)
            return false;

        final THashSet<RoomUnit> roomUnits = room.getRoomUnits();
        if (this.collideEveryone) {
            for (RoomUnit ru : roomUnits)
                Emulator.getThreading().run(new WiredCollissionRunnable(ru, room, new Object[]{this}));
        } else {
            RoomUnit randomRoomUnit = new ArrayList<>(roomUnits).get(Emulator.getRandom().nextInt(roomUnits.size()));
            Emulator.getThreading().run(new WiredCollissionRunnable(randomRoomUnit, room, new Object[]{this}));
        }

        return true;
    }

    @Override
    public String getWiredData() {
        return WiredHandler.getGsonBuilder().create().toJson(new JsonData(this.getDelay(), this.collideEveryone));
    }

    @Override
    public void loadWiredData(ResultSet set, Room room) throws SQLException {
        String wiredData = set.getString("wired_data");

        if (Emulator.isValidJSON(wiredData)) {
            JsonData data = WiredHandler.getGsonBuilder().create().fromJson(wiredData, JsonData.class);
            this.setDelay(data.delay);
            this.collideEveryone = data.collideEveryone;
        }
    }

    @Override
    public void onPickUp() {
        this.collideEveryone = false;
        this.setDelay(0);
    }

    @Override
    public void serializeWiredData(ServerMessage message, Room room) {
        message.appendBoolean(false);
        message.appendInt(0);
        message.appendInt(0);
        message.appendInt(this.getBaseItem().getSpriteId());
        message.appendInt(this.getId());
        message.appendString("");
        message.appendInt(1);
        message.appendInt(this.collideEveryone);
        message.appendInt(0);
        message.appendInt(this.getType().code);
        message.appendInt(this.getDelay());
        message.appendInt(0);
    }

    @Override
    public boolean saveData(WiredSettings settings, GameClient gameClient) throws WiredSaveException {
        if(settings.getIntParams().length != 1)
            throw new WiredSaveException("Invalid data");

        int collideEveryone = settings.getIntParams()[0];
        if (collideEveryone != 0 && collideEveryone != 1)
            throw new WiredSaveException("Collide everyone value is invalid");

        int delay = settings.getDelay();
        if (delay > Emulator.getConfig().getInt("hotel.wired.max_delay", 20))
            throw new WiredSaveException("Delay too long");

        this.collideEveryone = collideEveryone == 1;
        this.setDelay(delay);

        return true;
    }

    @Override
    public WiredEffectType getType() {
        return type;
    }

    static class JsonData {
        int delay;
        boolean collideEveryone;

        public JsonData(int delay, boolean collideEveryone) {
            this.delay = delay;
            this.collideEveryone = collideEveryone;
        }
    }

}
