package com.eu.habbo.habbohotel.items.interactions.wired.effects;

import com.eu.habbo.Emulator;
import com.eu.habbo.habbohotel.gameclients.GameClient;
import com.eu.habbo.habbohotel.items.Item;
import com.eu.habbo.habbohotel.items.interactions.InteractionDice;
import com.eu.habbo.habbohotel.items.interactions.InteractionWiredEffect;
import com.eu.habbo.habbohotel.items.interactions.InteractionWiredTrigger;
import com.eu.habbo.habbohotel.items.interactions.wired.WiredSettings;
import com.eu.habbo.habbohotel.permissions.Permission;
import com.eu.habbo.habbohotel.rooms.Room;
import com.eu.habbo.habbohotel.rooms.RoomChatMessageBubbles;
import com.eu.habbo.habbohotel.rooms.RoomUnit;
import com.eu.habbo.habbohotel.users.Habbo;
import com.eu.habbo.habbohotel.users.HabboItem;
import com.eu.habbo.habbohotel.wired.WiredEffectType;
import com.eu.habbo.messages.ClientMessage;
import com.eu.habbo.messages.ServerMessage;
import com.eu.habbo.habbohotel.wired.WiredHandler;
import com.eu.habbo.messages.incoming.wired.WiredSaveException;
import gnu.trove.iterator.hash.TObjectHashIterator;
import gnu.trove.procedure.TObjectProcedure;
import gnu.trove.set.hash.THashSet;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class WiredEffectCloseDices extends InteractionWiredEffect {
    public static final WiredEffectType type = WiredEffectType.TOGGLE_STATE;

    private final THashSet<HabboItem> items;

    public WiredEffectCloseDices(ResultSet set, Item baseItem) throws SQLException {
        super(set, baseItem);
        this.items = new THashSet<>();
    }

    public WiredEffectCloseDices(int id, int userId, Item item, String extradata, int limitedStack, int limitedSells) {
        super(id, userId, item, extradata, limitedStack, limitedSells);
        this.items = new THashSet<>();
    }


    public void serializeWiredData(ServerMessage message, Room room) {
        THashSet<HabboItem> items = new THashSet<>();
        TObjectHashIterator<HabboItem> tObjectHashIterator;
        for (tObjectHashIterator = this.items.iterator(); tObjectHashIterator.hasNext(); ) {
            HabboItem item = tObjectHashIterator.next();
            if (item.getRoomId() != getRoomId() || Emulator.getGameEnvironment().getRoomManager().getRoom(getRoomId()).getHabboItem(item.getId()) == null)
                items.add(item);
        }
        for (tObjectHashIterator = items.iterator(); tObjectHashIterator.hasNext(); ) {
            HabboItem item = tObjectHashIterator.next();
            this.items.remove(item);
        }
        message.appendBoolean(Boolean.FALSE);
        message.appendInt(WiredHandler.MAXIMUM_FURNI_SELECTION);
        message.appendInt(this.items.size());
        for (tObjectHashIterator = this.items.iterator(); tObjectHashIterator.hasNext(); ) {
            HabboItem item = tObjectHashIterator.next();
            message.appendInt(item.getId());
        }
        message.appendInt(getBaseItem().getSpriteId());
        message.appendInt(getId());
        message.appendString("");
        message.appendInt(0);
        message.appendInt(0);
        message.appendInt((getType()).code);
        message.appendInt(getDelay());
        if (requiresTriggeringUser()) {
            final List<Integer> invalidTriggers = new ArrayList<>();
            room.getRoomSpecialTypes().getTriggers(getX(), getY()).forEach(new TObjectProcedure<InteractionWiredTrigger>() {
                public boolean execute(InteractionWiredTrigger object) {
                    if (!object.isTriggeredByRoomUnit())
                        invalidTriggers.add(object.getBaseItem().getSpriteId());
                    return true;
                }
            });
            message.appendInt(invalidTriggers.size());
            for (Integer i : invalidTriggers)
                message.appendInt(i);
        } else {
            message.appendInt(0);
        }
    }

    @Override
    public boolean saveData(WiredSettings settings, GameClient gameClient) throws WiredSaveException {
        int itemsCount = settings.getFurniIds().length;

        if(itemsCount > Emulator.getConfig().getInt("hotel.wired.furni.selection.count")) {
            throw new WiredSaveException("Too many furni selected");
        }

        List<HabboItem> newItems = new ArrayList<>();

        for (int i = 0; i < itemsCount; i++) {
            int itemId = settings.getFurniIds()[i];
            HabboItem it = Emulator.getGameEnvironment().getRoomManager().getRoom(this.getRoomId()).getHabboItem(itemId);

            if(it == null)
                throw new WiredSaveException(String.format("Item %s not found", itemId));

            newItems.add(it);
        }

        int delay = settings.getDelay();

        if(delay > Emulator.getConfig().getInt("hotel.wired.max_delay", 20))
            throw new WiredSaveException("Delay too long");

        this.items.clear();
        this.items.addAll(newItems);
        this.setDelay(delay);

        return true;
    }

    public boolean execute(RoomUnit roomUnit, Room room, Object[] stuff) {
        Habbo habbo = room.getHabbo(roomUnit);
        Integer count = 0;
        for (TObjectHashIterator<HabboItem> tObjectHashIterator = this.items.iterator(); tObjectHashIterator.hasNext(); ) {
            HabboItem item = tObjectHashIterator.next();
            if (item == null || item.getRoomId() == 0)
                continue;
            if (item.getBaseItem().getInteractionType().getType() == InteractionDice.class &&
                    !item.getExtradata().equals("0")) {
                item.setExtradata("0");
                item.needsUpdate(true);
                room.updateItem(item);
                Integer integer = count;
                count = count + 1;
            }
        }
        habbo.whisper(Emulator.getTexts().getValue("wired.effect.closedices.text").replace("%count%", String.valueOf(count)), RoomChatMessageBubbles.ALERT);
        return true;
    }

    public String getWiredData() {
        return WiredHandler.getGsonBuilder().create().toJson(new JsonData(
                getDelay(), (List<Integer>)this.items
                .stream().map(HabboItem::getId).collect(Collectors.toList())));
    }

    public void loadWiredData(ResultSet set, Room room) throws SQLException {
        this.items.clear();
        String wiredData = set.getString("wired_data");
        if (wiredData.startsWith("{")) {
            JsonData data = (JsonData)WiredHandler.getGsonBuilder().create().fromJson(wiredData, JsonData.class);
            setDelay(data.delay);
            for (Integer id : data.itemIds) {
                HabboItem item = room.getHabboItem(id.intValue());
                if (item instanceof com.eu.habbo.habbohotel.items.interactions.games.freeze.InteractionFreezeBlock || item instanceof com.eu.habbo.habbohotel.items.interactions.games.freeze.InteractionFreezeTile || item instanceof com.eu.habbo.habbohotel.items.interactions.InteractionCrackable)
                    continue;
                if (item != null)
                    this.items.add(item);
            }
        } else {
            String[] wiredDataOld = wiredData.split("\t");
            if (wiredDataOld.length >= 1)
                setDelay(Integer.parseInt(wiredDataOld[0]));
            if (wiredDataOld.length == 2 &&
                    wiredDataOld[1].contains(";"))
                for (String s : wiredDataOld[1].split(";")) {
                    HabboItem item = room.getHabboItem(Integer.parseInt(s));
                    if (!(item instanceof com.eu.habbo.habbohotel.items.interactions.games.freeze.InteractionFreezeBlock) && !(item instanceof com.eu.habbo.habbohotel.items.interactions.games.freeze.InteractionFreezeTile) && !(item instanceof com.eu.habbo.habbohotel.items.interactions.InteractionCrackable))
                        if (item != null)
                            this.items.add(item);
                }
        }
    }

    public void onPickUp() {
        this.items.clear();
        setDelay(0);
    }

    public WiredEffectType getType() {
        return type;
    }

    static class JsonData {
        int delay;

        List<Integer> itemIds;

        public JsonData(int delay, List<Integer> itemIds) {
            this.delay = delay;
            this.itemIds = itemIds;
        }
    }
}