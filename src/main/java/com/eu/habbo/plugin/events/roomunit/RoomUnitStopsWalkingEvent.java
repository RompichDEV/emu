package com.eu.habbo.plugin.events.roomunit;

import com.eu.habbo.habbohotel.rooms.Room;
import com.eu.habbo.habbohotel.rooms.RoomTile;
import com.eu.habbo.habbohotel.rooms.RoomUnit;

public class RoomUnitStopsWalkingEvent extends RoomUnitEvent {

    public final RoomTile tileStoppedOn;

    public RoomUnitStopsWalkingEvent(Room room, RoomUnit roomUnit, RoomTile tileStoppedOn) {
        super(room, roomUnit);
        this.tileStoppedOn = tileStoppedOn;
    }

}