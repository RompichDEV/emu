package com.eu.habbo.messages.rcon;

import com.eu.habbo.Emulator;
import com.eu.habbo.habbohotel.users.Habbo;
import com.eu.habbo.messages.ServerMessage;
import com.eu.habbo.messages.outgoing.generic.alerts.GenericAlertComposer;
import com.eu.habbo.messages.outgoing.generic.alerts.StaffAlertWithLinkComposer;
import com.eu.habbo.messages.outgoing.rooms.ForwardToRoomComposer;
import com.eu.habbo.messages.outgoing.rooms.users.RoomUserDataComposer;
import com.google.gson.Gson;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Map;

public class FollowRoom extends RCONMessage<FollowRoom.JSONFollowRoom> {

    public FollowRoom() {
        super(JSONFollowRoom.class);
    }

    @Override
    public void handle(Gson gson, JSONFollowRoom json) {
        Habbo habbo = Emulator.getGameEnvironment().getHabboManager().getHabbo(json.user_id);

        if (habbo != null) {
            habbo.getClient().sendResponse(new ForwardToRoomComposer(json.room_id));
        }
    }

    static class JSONFollowRoom {

        public int user_id = -1;
        public int room_id = 0;
    }
}
