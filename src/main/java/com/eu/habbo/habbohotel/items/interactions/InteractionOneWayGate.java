package com.eu.habbo.habbohotel.items.interactions;

import com.eu.habbo.Emulator;
import com.eu.habbo.habbohotel.gameclients.GameClient;
import com.eu.habbo.habbohotel.items.Item;
import com.eu.habbo.habbohotel.rooms.Room;
import com.eu.habbo.habbohotel.rooms.RoomTile;
import com.eu.habbo.habbohotel.rooms.RoomUnit;
import com.eu.habbo.habbohotel.users.HabboItem;
import com.eu.habbo.habbohotel.wired.WiredHandler;
import com.eu.habbo.habbohotel.wired.WiredTriggerType;
import com.eu.habbo.messages.ServerMessage;
import com.eu.habbo.messages.incoming.rooms.users.RoomUserWalkEvent;
import com.eu.habbo.messages.outgoing.rooms.items.ItemIntStateComposer;
import com.eu.habbo.threading.runnables.RoomUnitWalkToLocation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.*;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class InteractionOneWayGate extends HabboItem {
    private static final Logger LOGGER = LoggerFactory.getLogger(InteractionOneWayGate.class);

    private boolean walkable = false;

    public InteractionOneWayGate(ResultSet set, Item baseItem) throws SQLException {
        super(set, baseItem);
        this.setExtradata("0");
    }

    public InteractionOneWayGate(int id, int userId, Item item, String extradata, int limitedStack, int limitedSells) {
        super(id, userId, item, extradata, limitedStack, limitedSells);
        this.setExtradata("0");
    }

    @Override
    public boolean canWalkOn(RoomUnit roomUnit, Room room, Object[] objects) {
        return this.getBaseItem().allowWalk();
    }

    @Override
    public boolean isWalkable() {
        return walkable;
    }

    @Override
    public void onWalk(RoomUnit roomUnit, Room room, Object[] objects) throws Exception {

    }

    @Override
    public void serializeExtradata(ServerMessage serverMessage) {
        if (this.getExtradata().length() == 0) {
            this.setExtradata("0");
            this.needsUpdate(true);
        }

        serverMessage.appendInt((this.isLimited() ? 256 : 0));
        serverMessage.appendString(this.getExtradata());

        super.serializeExtradata(serverMessage);
    }

    @Override
    public void onClick(final GameClient client, final Room room, Object[] objects) throws Exception {
        super.onClick(client, room, objects);

        if (client != null) {
            RoomTile tileInfront = room.getLayout().getTileInFront(room.getLayout().getTile(this.getX(), this.getY()), this.getRotation());
            if (tileInfront == null)
                return;

            RoomUnit roomUnit = client.getHabbo().getRoomUnit();
            RoomTile gateTile = room.getLayout().getTile(this.get2DPosition());

            Point clientGoalLocation = this.get2DPosition();
            int rotation = this.getRotation();
            int dx = (rotation == 2) ? 1 : (rotation == 6) ? -1 : 0;
            int dy = (rotation == 4) ? 1 : (rotation == 0) ? -1 : 0;
            clientGoalLocation.translate(dx, dy);

            List<Runnable> clickFromFarSuccess = new ArrayList<>();
            clickFromFarSuccess.add(() -> {
                try {
                    this.onClick(client, room, objects);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });

            List<Runnable> reset = new ArrayList<>();
            reset.add(() -> {
                walkable = false;
                room.updateTile(gateTile);
                roomUnit.removeOverrideTile(gateTile);
            });

            if (!clientGoalLocation.equals(roomUnit.get2DPosition())) {
                RoomTile goalLocation = room.getLayout().getTile(clientGoalLocation);
                client.getHabbo().getRoomUnit().setGoalLocation(goalLocation);
                Emulator.getThreading().run(new RoomUnitWalkToLocation(client.getHabbo().getRoomUnit(), goalLocation, room, clickFromFarSuccess, reset));
            } else {
                walkable = true;
                roomUnit.addOverrideTile(gateTile);
                room.sendComposer(new ItemIntStateComposer(this.getId(), 1).compose());
                RoomTile goalLocation = room.getLayout().getTileInFront(gateTile, this.getRotation() + 4);
                if (goalLocation.hasUnits() && !room.isAllowWalkthrough())
                    roomUnit.setGoalLocation(gateTile);
                else
                    roomUnit.setGoalLocation(goalLocation);
                Emulator.getThreading().run(new RoomUnitWalkToLocation(roomUnit, goalLocation, room, reset, reset));
                Emulator.getThreading().run(() -> {
                    WiredHandler.handle(WiredTriggerType.WALKS_ON_FURNI, roomUnit, room, new Object[]{this});
                }, 500);
                Emulator.getThreading().run(() -> {
                    room.sendComposer(new ItemIntStateComposer(this.getId(), 0).compose());
                }, 1000);
            }
        }
    }

    private void refresh(Room room) {
        this.setExtradata("0");
        room.sendComposer(new ItemIntStateComposer(this.getId(), 0).compose());
        room.updateTile(room.getLayout().getTile(this.getX(), this.getY()));
    }

    @Override
    public void onPickUp(Room room) {
        this.setExtradata("0");
        this.refresh(room);
    }

    @Override
    public void onWalkOn(RoomUnit roomUnit, Room room, Object[] objects) throws Exception {
        super.onWalkOn(roomUnit, room, objects);
    }

    @Override
    public void onWalkOff(RoomUnit roomUnit, Room room, Object[] objects) throws Exception {
        super.onWalkOff(roomUnit, room, objects);
        this.refresh(room);
    }

    @Override
    public void onPlace(Room room) {
        super.onPlace(room);
        this.refresh(room);
    }

    @Override
    public void onMove(Room room, RoomTile oldLocation, RoomTile newLocation) {
        super.onMove(room, oldLocation, newLocation);
        this.refresh(room);
    }
}
