package com.eu.habbo.undo.actions;

import com.eu.habbo.Emulator;
import com.eu.habbo.habbohotel.items.FurnitureType;
import com.eu.habbo.habbohotel.rooms.Room;
import com.eu.habbo.habbohotel.rooms.RoomLayout;
import com.eu.habbo.habbohotel.rooms.RoomTile;
import com.eu.habbo.habbohotel.users.Habbo;
import com.eu.habbo.habbohotel.users.HabboItem;
import com.eu.habbo.messages.outgoing.MessageComposer;
import com.eu.habbo.messages.outgoing.inventory.AddHabboItemComposer;
import com.eu.habbo.messages.outgoing.inventory.InventoryRefreshComposer;
import com.eu.habbo.messages.outgoing.rooms.UpdateStackHeightComposer;
import com.eu.habbo.messages.outgoing.rooms.items.RemoveFloorItemComposer;
import com.eu.habbo.messages.outgoing.rooms.items.RemoveWallItemComposer;
import gnu.trove.iterator.hash.TObjectHashIterator;
import gnu.trove.set.hash.THashSet;
import java.awt.Rectangle;

public class FurniPlaceAction extends FurniAction {
    public FurniPlaceAction(Room room, HabboItem habboItem) {
        this.room = room;
        this.habboItem = habboItem;
    }

    public void undo() {
        if (this.habboItem == null || this.room == null)
            return;
        Habbo habbo = Emulator.getGameEnvironment().getHabboManager().getHabbo(this.habboItem.getUserId());
        pickUpItem(this.habboItem, habbo);
    }

    public void pickUpItem(HabboItem item, Habbo picker) {
        if (item == null)
            return;
        this.room.removeHabboItem(item);
        item.onPickUp(this.room);
        item.setRoomId(0);
        item.needsUpdate(true);
        if (item.getBaseItem().getType() == FurnitureType.FLOOR) {
            this.room.sendComposer((new RemoveFloorItemComposer(item)).compose());
            THashSet<RoomTile> updatedTiles = new THashSet();
            Rectangle rectangle = RoomLayout.getRectangle(item.getX(), item.getY(), item.getBaseItem().getWidth(), item.getBaseItem().getLength(), item.getRotation());
            short x;
            for (x = (short)rectangle.x; x < rectangle.x + rectangle.getWidth(); x = (short)(x + 1)) {
                short y;
                for (y = (short)rectangle.y; y < rectangle.y + rectangle.getHeight(); y = (short)(y + 1)) {
                    double stackHeight = this.room.getStackHeight(x, y, false);
                    RoomTile tile = this.room.getLayout().getTile(x, y);
                    if (tile != null) {
                        tile.setStackHeight(stackHeight);
                        updatedTiles.add(tile);
                    }
                }
            }
            this.room.sendComposer((new UpdateStackHeightComposer(this.room, updatedTiles)).compose());
            this.room.updateTiles(updatedTiles);
            for (TObjectHashIterator<RoomTile> tObjectHashIterator = updatedTiles.iterator(); tObjectHashIterator.hasNext(); ) {
                RoomTile tile = tObjectHashIterator.next();
                this.room.updateHabbosAt(tile.x, tile.y);
                this.room.updateBotsAt(tile.x, tile.y);
            }
        } else if (item.getBaseItem().getType() == FurnitureType.WALL) {
            this.room.sendComposer((new RemoveWallItemComposer(item)).compose());
        }
        Habbo habbo = (picker != null && picker.getHabboInfo().getId() == item.getId()) ? picker : Emulator.getGameServer().getGameClientManager().getHabbo(item.getUserId());
        if (habbo != null) {
            habbo.getInventory().getItemsComponent().addItem(item);
            habbo.getClient().sendResponse((MessageComposer)new AddHabboItemComposer(item));
            habbo.getClient().sendResponse((MessageComposer)new InventoryRefreshComposer());
        }
        Emulator.getThreading().run((Runnable)item);
    }
}
