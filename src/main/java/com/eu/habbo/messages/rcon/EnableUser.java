package com.eu.habbo.messages.rcon;

import com.eu.habbo.Emulator;
import com.eu.habbo.habbohotel.users.Habbo;
import com.eu.habbo.messages.ServerMessage;
import com.eu.habbo.messages.outgoing.generic.alerts.GenericAlertComposer;
import com.eu.habbo.messages.outgoing.generic.alerts.StaffAlertWithLinkComposer;
import com.eu.habbo.messages.outgoing.rooms.users.RoomUserDataComposer;
import com.google.gson.Gson;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Map;

public class EnableUser extends RCONMessage<EnableUser.JSONEnableUser> {

    public EnableUser() {
        super(JSONEnableUser.class);
    }

    @Override
    public void handle(Gson gson, JSONEnableUser json) {
        Habbo habbo = Emulator.getGameEnvironment().getHabboManager().getHabbo(json.user_id);

        if (habbo != null) {
            habbo.getHabboInfo().getCurrentRoom().giveEffect(habbo, json.effect_id, -1);
        }
    }

    static class JSONEnableUser {

        public int user_id = -1;
        public int effect_id = 0;
    }
}
