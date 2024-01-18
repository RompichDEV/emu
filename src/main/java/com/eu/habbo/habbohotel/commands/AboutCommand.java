package com.eu.habbo.habbohotel.commands;

import com.eu.habbo.Emulator;
import com.eu.habbo.habbohotel.catalog.CatalogManager;
import com.eu.habbo.habbohotel.gameclients.GameClient;
import com.eu.habbo.habbohotel.users.HabboManager;
import com.eu.habbo.messages.ServerMessage;
import com.eu.habbo.messages.outgoing.generic.alerts.BubbleAlertComposer;
import com.eu.habbo.messages.outgoing.generic.alerts.MessagesForYouComposer;
import gnu.trove.map.hash.THashMap;

import java.util.Collections;
import java.util.concurrent.TimeUnit;


public class AboutCommand extends Command {
    public AboutCommand() {
        super(null, new String[]{"about"});
    }
    public static String credits = "Arcturus Morningstar is an opensource project based on Arcturus By TheGeneral \n" +
            "The Following people have all contributed to this emulator:\n" +
            " TheGeneral\n Beny\n Alejandro\n Capheus\n Skeletor\n Harmonic\n Mike\n Remco\n zGrav \n Quadral \n Harmony\n Swirny\n ArpyAge\n Mikkel\n Rodolfo\n Rasmus\n Kitt Mustang\n Snaiker\n nttzx\n necmi\n Dome\n Jose Flores\n Cam\n Oliver\n Narzo\n Tenshie\n MartenM\n Ridge\n SenpaiDipper\n Snaiker\n Thijmen";
    @Override
    public boolean handle(GameClient gameClient, String[] params) {

        Emulator.getRuntime().gc();

        int seconds = Emulator.getIntUnixTimestamp() - Emulator.getTimeStarted();
        int day = (int) TimeUnit.SECONDS.toDays(seconds);
        long hours = TimeUnit.SECONDS.toHours(seconds) - (day * 24);
        long minute = TimeUnit.SECONDS.toMinutes(seconds) - (TimeUnit.SECONDS.toHours(seconds) * 60);
        long second = TimeUnit.SECONDS.toSeconds(seconds) - (TimeUnit.SECONDS.toMinutes(seconds) * 60);

        //String message = "<b>" + Emulator.version + "</b>\r\n";
        String message = "<b><h5>Comet Server</h5></b>\r\r\n";

        if (Emulator.getConfig().getBoolean("infos", true)) {
            message += "<b>Server Status </b>\r" +
                    "- Users online: " + Emulator.getGameEnvironment().getHabboManager().getOnlineCount() + "\r" +
                    "- Active rooms: " + Emulator.getGameEnvironment().getRoomManager().getActiveRooms().size() + "\r" +
                    "- Uptime: " + day + (day > 1 ? " days " : " day ") + hours + (hours > 1 ? " hours " : " hour ") + minute + (minute > 1 ? " minutes " : " minute ") + second + (second > 1 ? " seconds" : " second") + "\r\r" +
                    //"- Shop:  " + Emulator.getGameEnvironment().getCatalogManager().catalogPages.size() + " pages and " + CatalogManager.catalogItemAmount + " items. \r" +
                    //"- Furni: " + Emulator.getGameEnvironment().getItemManager().getItems().size() + " item definitions" + "\r" +
                    "\n" +
                    "<b>Server Infos</b>\r" +
                    "- Allocated memory: " + Emulator.getRuntime().maxMemory() / (1024 * 1024) + "MB" + "\r\n" +
                    "- Used memory: " + (Emulator.getRuntime().totalMemory() - Emulator.getRuntime().freeMemory()) / (1024 * 1024) + "/" + (Emulator.getRuntime().freeMemory()) / (1024 * 1024) + "MB\r" +
                    "- CPU cores: " + Emulator.getRuntime().availableProcessors() + "\r\r" +
                    "\n" +
                    "<b>Credits</b>\r" +
                    "Comet Developers\r";
        }
        ServerMessage message2 = new BubbleAlertComposer("cannon.png", message).compose();
        //gameClient.sendResponse(message2); //kicked composer
        //gameClient.sendResponse(new MessagesForYouComposer(Collections.singletonList(credits)));

        THashMap<String, String> dater = new THashMap<>();
        dater.put("title", "About HabboZone Server");
        dater.put("image", "${image.library.url}/notification/server.png");
        dater.put("message", "<b>Server Status</b>\r" +
                "- Users online: " + Emulator.getGameEnvironment().getHabboManager().getOnlineCount() + "\r" +
                "- Active rooms: " + Emulator.getGameEnvironment().getRoomManager().getActiveRooms().size() + "\r" +
                "- Uptime: " + day + (day > 1 ? " days " : " day ") + hours + (hours > 1 ? " hours " : " hour ") + minute + (minute > 1 ? " minutes " : " minute ") + second + (second > 1 ? " seconds" : " second") + "\r\r"
                //+"\n" +
                //"<b>Server Infos</b>\r" +
                //"- Allocated memory: " + Emulator.getRuntime().maxMemory() / (1024 * 1024) + "MB" + "\r\n" +
                //"- Used memory: " + (Emulator.getRuntime().totalMemory() - Emulator.getRuntime().freeMemory()) / (1024 * 1024) + "/" + (Emulator.getRuntime().freeMemory()) / (1024 * 1024) + "MB\r" +
                //"- CPU cores: " + Emulator.getRuntime().availableProcessors()
                 );
        ServerMessage mymessage = new BubbleAlertComposer("about.png", dater).compose();
        gameClient.sendResponse(mymessage);
        //gameClient.getHabbo().getHabboInfo().setBanner(61);
        return true;
    }
}