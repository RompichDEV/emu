package com.eu.habbo.undo;

import com.eu.habbo.Emulator;
import com.eu.habbo.habbohotel.commands.Command;
import com.eu.habbo.habbohotel.commands.CommandHandler;
import com.eu.habbo.habbohotel.rooms.Room;
import com.eu.habbo.habbohotel.users.Habbo;
import com.eu.habbo.plugin.EventHandler;
import com.eu.habbo.plugin.EventListener;
import com.eu.habbo.plugin.HabboPlugin;
import com.eu.habbo.plugin.events.emulator.EmulatorLoadedEvent;
import com.eu.habbo.plugin.events.furniture.FurnitureMovedEvent;
import com.eu.habbo.plugin.events.furniture.FurniturePickedUpEvent;
import com.eu.habbo.plugin.events.furniture.FurniturePlacedEvent;
import com.eu.habbo.plugin.events.furniture.FurnitureRotatedEvent;
import com.eu.habbo.plugin.events.users.UserLoginEvent;
import com.eu.habbo.undo.actions.*;
import com.eu.habbo.undo.commands.UndoCommand;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Undo extends HabboPlugin implements EventListener {
    public static Undo INSTANCE = null;

    public static String LAST_FURNI_ACTIONS = "last.furni.actions.key";

    public void onEnable() {
        INSTANCE = this;
        Emulator.getPluginManager().registerEvents(this, this);
        if (Emulator.isReady)
            checkDatabase();
        Emulator.getLogging().logStart("[Undo] Started Undo Command Plugin!");
    }

    public void onDisable() {
        Emulator.getLogging().logShutdownLine("[Undo] Stopped Undo Command Plugin!");
    }

    @EventHandler
    public static void onEmulatorLoaded(EmulatorLoadedEvent event) {
        INSTANCE.checkDatabase();
    }

    public boolean hasPermission(Habbo habbo, String s) {
        return false;
    }

    private void checkDatabase() {
        boolean reloadPermissions = false;
        try(Connection connection = Emulator.getDatabase().getDataSource().getConnection(); Statement statement = connection.createStatement()) {
            statement.execute("ALTER TABLE  `emulator_texts` CHANGE  `value`  `value` VARCHAR( 4096 ) CHARACTER SET latin1 COLLATE latin1_swedish_ci NOT NULL");
        } catch (SQLException sQLException) {}
        Emulator.getTexts().register("commands.description.cmd_undo", ":undo");
        Emulator.getTexts().register("undo.cmd_undo.keys", "undo");
        Emulator.getTexts().register("undo.cmd_undo.success", "Successfully undone last action");
        Emulator.getTexts().register("undo.cmd_undo.error", "You have no actions done in this room");
        reloadPermissions = registerPermission("cmd_undo", "'0', '1', '2'", "2", reloadPermissions);
        if (reloadPermissions)
            Emulator.getGameEnvironment().getPermissionsManager().reload();
        CommandHandler.addCommand((Command)new UndoCommand("cmd_undo", Emulator.getTexts().getValue("undo.cmd_undo.keys").split(";")));
    }

    private boolean registerPermission(String name, String options, String defaultValue, boolean defaultReturn) {
        try(Connection connection = Emulator.getDatabase().getDataSource().getConnection();
            PreparedStatement statement = connection.prepareStatement("ALTER TABLE  `permissions` ADD  `" + name + "` ENUM(  " + options + " ) NOT NULL DEFAULT  '" + defaultValue + "'")) {
            statement.execute();
            return true;
        } catch (SQLException sQLException) {
            return defaultReturn;
        }
    }

    @EventHandler
    public static void onUserLoginEvent(UserLoginEvent event) {
        if (event.habbo == null)
            return;
        Map<Integer, List<FurniAction>> furniActionList = new HashMap<>();
        (event.habbo.getHabboStats()).cache.put(LAST_FURNI_ACTIONS, furniActionList);
    }

    @EventHandler
    public static void onFurnitureRotatedEvent(FurnitureRotatedEvent event) {
        if (event.habbo == null || event.furniture == null)
            return;
        Room room = event.habbo.getHabboInfo().getCurrentRoom();
        if (room == null)
            return;
        FurniRotateAction furniRotateAction = new FurniRotateAction(room, event.furniture, event.furniture.getZ(), event.oldRotation);
        List<FurniAction> furniActions = new ArrayList<>();
        Map<Integer, List<FurniAction>> furniActionList = new HashMap<>();
        if ((event.habbo.getHabboStats()).cache.containsKey(LAST_FURNI_ACTIONS)) {
            furniActionList = (HashMap)(event.habbo.getHabboStats()).cache.get(LAST_FURNI_ACTIONS);
            if (furniActionList.containsKey(Integer.valueOf(room.getId())))
                furniActions = furniActionList.get(Integer.valueOf(room.getId()));
        }
        furniActions.remove(furniActions.size() - 1);
        furniActions.add(furniRotateAction);
        furniActionList.put(Integer.valueOf(room.getId()), furniActions);
        (event.habbo.getHabboStats()).cache.put(LAST_FURNI_ACTIONS, furniActionList);
    }

    @EventHandler
    public static void onFurniturePlacedEvent(FurniturePlacedEvent event) {
        if (event.habbo == null || event.furniture == null)
            return;
        Room room = event.habbo.getHabboInfo().getCurrentRoom();
        if (room == null)
            return;
        FurniPlaceAction furniPlaceAction = new FurniPlaceAction(room, event.furniture);
        List<FurniAction> furniActions = new ArrayList<>();
        Map<Integer, List<FurniAction>> furniActionList = new HashMap<>();
        if ((event.habbo.getHabboStats()).cache.containsKey(LAST_FURNI_ACTIONS)) {
            furniActionList = (HashMap)(event.habbo.getHabboStats()).cache.get(LAST_FURNI_ACTIONS);
            if (furniActionList.containsKey(Integer.valueOf(room.getId())))
                furniActions = furniActionList.get(Integer.valueOf(room.getId()));
        }
        furniActions.add(furniPlaceAction);
        furniActionList.put(Integer.valueOf(room.getId()), furniActions);
        (event.habbo.getHabboStats()).cache.put(LAST_FURNI_ACTIONS, furniActionList);
    }

    @EventHandler
    public static void onFurnitureMovedEvent(FurnitureMovedEvent event) {
        if (event.habbo == null || event.furniture == null)
            return;
        Room room = event.habbo.getHabboInfo().getCurrentRoom();
        if (room == null)
            return;
        FurniMoveAction furniMoveAction = new FurniMoveAction(room, event.furniture, event.oldPosition.x, event.oldPosition.y, event.furniture.getZ());
        List<FurniAction> furniActions = new ArrayList<>();
        Map<Integer, List<FurniAction>> furniActionList = new HashMap<>();
        if ((event.habbo.getHabboStats()).cache.containsKey(LAST_FURNI_ACTIONS)) {
            furniActionList = (HashMap)(event.habbo.getHabboStats()).cache.get(LAST_FURNI_ACTIONS);
            if (furniActionList.containsKey(Integer.valueOf(room.getId())))
                furniActions = furniActionList.get(Integer.valueOf(room.getId()));
        }
        furniActions.add(furniMoveAction);
        furniActionList.put(Integer.valueOf(room.getId()), furniActions);
        (event.habbo.getHabboStats()).cache.put(LAST_FURNI_ACTIONS, furniActionList);
    }

    @EventHandler
    public static void onFurniturePickedUpEvent(FurniturePickedUpEvent event) {
        if (event.habbo == null || event.furniture == null)
            return;
        if (event.habbo.getHabboInfo().getId() != event.furniture.getUserId())
            return;
        Room room = event.habbo.getHabboInfo().getCurrentRoom();
        if (room == null)
            return;
        FurniPickupAction furniPickupAction = new FurniPickupAction(room, event.furniture, event.furniture.getX(), event.furniture.getY(), event.furniture.getZ(), event.furniture.getRotation(), event.furniture.getExtradata());
        List<FurniAction> furniActions = new ArrayList<>();
        Map<Integer, List<FurniAction>> furniActionList = new HashMap<>();
        if ((event.habbo.getHabboStats()).cache.containsKey(LAST_FURNI_ACTIONS)) {
            furniActionList = (HashMap)(event.habbo.getHabboStats()).cache.get(LAST_FURNI_ACTIONS);
            if (furniActionList.containsKey(Integer.valueOf(room.getId())))
                furniActions = furniActionList.get(Integer.valueOf(room.getId()));
        }
        furniActions.add(furniPickupAction);
        furniActionList.put(Integer.valueOf(room.getId()), furniActions);
        (event.habbo.getHabboStats()).cache.put(LAST_FURNI_ACTIONS, furniActionList);
    }

    public static void main(String[] args) {
        System.out.println("Don't run this seperately");
    }
}