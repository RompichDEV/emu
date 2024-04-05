package com.eu.habbo.habbohotel.items.interactions.wired.effects;

import com.eu.habbo.Emulator;
import com.eu.habbo.habbohotel.gameclients.GameClient;
import com.eu.habbo.habbohotel.items.Item;
import com.eu.habbo.habbohotel.items.interactions.InteractionWiredEffect;
import com.eu.habbo.habbohotel.items.interactions.wired.WiredSettings;
import com.eu.habbo.habbohotel.items.interactions.wired.effects.WiredEffectWhisper;
import com.eu.habbo.habbohotel.rooms.*;
import com.eu.habbo.habbohotel.users.DanceType;
import com.eu.habbo.habbohotel.users.Habbo;
import com.eu.habbo.messages.incoming.wired.WiredSaveException;
import com.eu.habbo.messages.outgoing.MessageComposer;
import com.eu.habbo.messages.outgoing.generic.alerts.StaffAlertWithLinkComposer;
import com.eu.habbo.messages.outgoing.habboway.nux.NuxAlertComposer;
import com.eu.habbo.messages.outgoing.modtool.ModToolIssueHandledComposer;
import com.eu.habbo.messages.outgoing.rooms.users.RoomUserDanceComposer;
import com.eu.habbo.messages.outgoing.rooms.users.RoomUserStatusComposer;
import com.eu.habbo.messages.outgoing.wired.WiredEffectDataComposer;
import java.sql.ResultSet;
import java.sql.SQLException;

public class WiredEffectSuperWired extends WiredEffectWhisper {
    public static int numero;

    public WiredEffectSuperWired(ResultSet set, Item baseItem) throws SQLException {
        super(set, baseItem);
    }

    public WiredEffectSuperWired(int id, int userId, Item item, String extradata, int limitedStack, int limitedSells) {
        super(id, userId, item, extradata, limitedStack, limitedSells);
    }

    public void onClick(GameClient client, Room room, Object[] objects) throws Exception {
        if (client != null && room.hasRights(client.getHabbo())) {
            client.sendResponse((MessageComposer)new WiredEffectDataComposer((InteractionWiredEffect)this, room));
            activateBox(room);
            client.sendResponse((new NuxAlertComposer("habbopages/effect.txt")).compose());
        }
    }

    public boolean execute(RoomUnit roomUnit, Room room, Object[] stuff) {
        int effectId;
        StringBuilder message;
        int item;
        String url;
        int danceId;
        StringBuilder alertUrl;
        int i;
        Habbo habbo = room.getHabbo(roomUnit);
        String str = this.message;
        String[] finalText = str.split(":");
        String[] params = str.split(" ");
        switch (finalText[0]) {
            case "enable":
                room.giveEffect(roomUnit, Integer.parseInt(finalText[1]), 2147483647);
                break;
            case "randomenable":
                numero = (int)(Math.random() * 199.0D) + 1;
                room.giveEffect(roomUnit, numero, 2147483647);
                break;
            case "roomenable":
                effectId = Integer.parseInt(finalText[1]);
                if (effectId >= 0) {
                    Room roomusers = habbo.getHabboInfo().getCurrentRoom();
                    for (Habbo habbos : roomusers.getHabbos())
                        roomusers.giveEffect(habbos, effectId, -1);
                    return true;
                }
                habbo.whisper(Emulator.getTexts().getValue("commands.error.cmd_roomeffect.positive"), RoomChatMessageBubbles.ALERT);
                break;
            case "handitem":
                room.giveHandItem(habbo, Integer.parseInt(finalText[1]));
                break;
            case "randomitem":
                numero = (int)(Math.random() * 1999.0D) + 1;
                room.giveHandItem(habbo, numero);
                break;
            case "roomitem":
                item = Integer.parseInt(finalText[1]);
                if (item >= 0) {
                    Room roomusers = habbo.getHabboInfo().getCurrentRoom();
                    for (Habbo habbos : roomusers.getHabbos())
                        roomusers.giveHandItem(habbos, item);
                    return true;
                }
                break;
            case "speed":
                room.setRollerSpeed(Integer.parseInt(finalText[1]));
                break;
            case "dance":
                if (Integer.parseInt(finalText[1]) >= 0 && Integer.parseInt(finalText[1]) <= 4) {
                    habbo.getRoomUnit().setDanceType(DanceType.values()[Integer.parseInt(finalText[1])]);
                    habbo.getHabboInfo().getCurrentRoom().sendComposer((new RoomUserDanceComposer(habbo.getRoomUnit())).compose());
                    break;
                }
                habbo.getRoomUnit().setDanceType(DanceType.values()[0]);
                habbo.getHabboInfo().getCurrentRoom().sendComposer((new RoomUserDanceComposer(habbo.getRoomUnit())).compose());
                break;
            case "roomdance":
                try {
                    danceId = Integer.parseInt(finalText[1]);
                } catch (Exception e) {
                    habbo.whisper(Emulator.getTexts().getValue("commands.error.cmd_danceall.invalid_dance"), RoomChatMessageBubbles.ALERT);
                    return true;
                }
                if (danceId < 0 || danceId > 4) {
                    habbo.whisper(Emulator.getTexts().getValue("commands.error.cmd_danceall.outside_bounds"), RoomChatMessageBubbles.ALERT);
                    return true;
                }
                for (Habbo habbos : habbo.getHabboInfo().getCurrentRoom().getHabbos()) {
                    habbos.getRoomUnit().setDanceType(DanceType.values()[danceId]);
                    habbos.getHabboInfo().getCurrentRoom().sendComposer((new RoomUserDanceComposer(habbos.getRoomUnit())).compose());
                }
                break;
            case "freeze":
                habbo.getRoomUnit().setCanWalk(false);
                break;
            case "unfreeze":
                habbo.getRoomUnit().setCanWalk(true);
                break;
            case "moonwalk":
                room.giveEffect(roomUnit, 136, 2147483647);
                break;
            case "sit":
                habbo.getHabboInfo().getCurrentRoom().makeSit(habbo);
                break;
            case "lay":
                if (habbo != null) {
                    if (habbo.getRoomUnit().isWalking()) {
                        (habbo.getRoomUnit()).cmdLay = false;
                    } else {
                        (habbo.getRoomUnit()).cmdLay = true;
                        habbo.getHabboInfo().getCurrentRoom().updateHabbo(habbo);
                        (habbo.getRoomUnit()).cmdSit = true;
                        habbo.getRoomUnit().setBodyRotation(RoomUserRotation.values()[habbo.getRoomUnit().getBodyRotation().getValue() - habbo.getRoomUnit().getBodyRotation().getValue() % 2]);
                        RoomTile tile = habbo.getRoomUnit().getCurrentLocation();
                        if (tile == null)
                            return true;
                        habbo.getRoomUnit().setStatus(RoomUnitStatus.LAY, "0.5");
                        habbo.getHabboInfo().getCurrentRoom().sendComposer((new RoomUserStatusComposer(habbo.getRoomUnit())).compose());
                        return true;
                    }
                }
                break;
            case "rot":
                if (this.message.length() > 0) {
                    Integer value = Integer.parseInt(finalText[1]);
                    habbo.getRoomUnit().setBodyRotation(RoomUserRotation.values()[value.intValue()]);
                }
                break;
            case "fastwalk":
                habbo.getRoomUnit().setFastWalk(!habbo.getRoomUnit().isFastWalk());
                break;
            case "points":
                if ((habbo.getHabboStats()).cache.containsKey("points_game")) {
                    int point = (habbo.getHabboStats()).cache.get("points_game").hashCode() + Integer.parseInt(finalText[1]);
                    (habbo.getHabboStats()).cache.put("points_game", Integer.valueOf(point));
                    habbo.whisper("Vous avez augmentde " + finalText[1] + " points, maintenant vous avez " + point);
                    break;
                }
                (habbo.getHabboStats()).cache.put("points_game", Integer.valueOf(Integer.parseInt(finalText[1])));
                habbo.talk("Vous avez re" + finalText[1] + "points");
                break;
            case "removepoints":
                if ((habbo.getHabboStats()).cache.containsKey("points_game")) {
                    int point = (habbo.getHabboStats()).cache.get("points_game").hashCode() - Integer.parseInt(finalText[1]);
                    (habbo.getHabboStats()).cache.put("points_game", Integer.valueOf(point));
                    habbo.talk("Il vous reste " + point + " points");
                    if ((habbo.getHabboStats()).cache.get("points_game").hashCode() == 0) {
                        (habbo.getHabboStats()).cache.remove("points_game");
                        habbo.talk("Vos points ont r");
                    }
                }
                break;
            case "resetpoints":
                if ((habbo.getHabboStats()).cache.containsKey("points_game")) {
                    (habbo.getHabboStats()).cache.remove("points_game");
                    habbo.talk("Vos points ont r");
                }
                break;
        }
        switch (params[0]) {
            case "roomalert":
                message = new StringBuilder();
                if (params.length >= 2) {
                    for (int j = 1; j < params.length; j++)
                        message.append(params[j]).append(" ");
                    if (message.length() == 0)
                        return true;
                    room = habbo.getHabboInfo().getCurrentRoom();
                    if (room != null) {
                        room.sendComposer((new ModToolIssueHandledComposer(message.toString())).compose());
                        return true;
                    }
                }
                break;
            case "roomalerturl":
                if (params.length < 3)
                    return true;
                url = params[1];
                alertUrl = new StringBuilder();
                for (i = 2; i < params.length; i++) {
                    alertUrl.append(params[i]);
                    alertUrl.append(" ");
                }
                alertUrl.append("\r\r-<b>").append(habbo.getHabboInfo().getUsername()).append("</b>");
                room = habbo.getHabboInfo().getCurrentRoom();
                if (room != null) {
                    room.sendComposer((new StaffAlertWithLinkComposer(alertUrl.toString(), url)).compose());
                    return true;
                }
                break;
        }
        return true;
    }

    public boolean saveData(WiredSettings settings, GameClient gameClient) throws WiredSaveException {
        String message = settings.getStringParam();
        String[] params = message.split(" ");
        String[] params2 = message.split(":");
        switch (params2[0]) {
            case "roomitem":
                if (!gameClient.getHabbo().hasPermission("cmd_roomitem")) {
                    gameClient.getHabbo().whisper(Emulator.getTexts().getValue("wired.error.superwired"), RoomChatMessageBubbles.ALERT);
                    return false;
                }
                break;
            case "roomdenable":
                if (!gameClient.getHabbo().hasPermission("cmd_roomeffect")) {
                    gameClient.getHabbo().whisper(Emulator.getTexts().getValue("wired.error.superwired"), RoomChatMessageBubbles.ALERT);
                    return false;
                }
                break;
            case "roomdance":
                if (!gameClient.getHabbo().hasPermission("cmd_danceall")) {
                    gameClient.getHabbo().whisper(Emulator.getTexts().getValue("wired.error.superwired"), RoomChatMessageBubbles.ALERT);
                    return false;
                }
                break;
            case "moonwalk":
                if (!gameClient.getHabbo().hasPermission("cmd_moonwalk")) {
                    gameClient.getHabbo().whisper(Emulator.getTexts().getValue("wired.error.superwired"), RoomChatMessageBubbles.ALERT);
                    return false;
                }
                break;
        }
        switch (params[0]) {
            case "roomalert":
                if (!gameClient.getHabbo().hasPermission("cmd_roomalert")) {
                    gameClient.getHabbo().whisper(Emulator.getTexts().getValue("wired.error.superwired"), RoomChatMessageBubbles.ALERT);
                    return false;
                }
                break;
            case "roomalerturl":
                if (!gameClient.getHabbo().hasPermission("cmd_hal")) {
                    gameClient.getHabbo().whisper(Emulator.getTexts().getValue("wired.error.superwired"), RoomChatMessageBubbles.ALERT);
                    return false;
                }
                break;
        }
        int delay = settings.getDelay();
        if (delay > Emulator.getConfig().getInt("hotel.wired.max_delay", Integer.valueOf(20)))
            throw new WiredSaveException("Delay too long");
        this.message = message;
        setDelay(delay);
        return true;
    }
}