package me.fallinganvils.censornames.events;

import com.mojang.authlib.GameProfile;
import me.fallinganvils.censornames.CensorNames;
import me.fallinganvils.censornames.util.CensorFormat;
import me.fallinganvils.censornames.util.CensorMap;
import me.fallinganvils.censornames.util.ColorCode;
import net.minecraft.client.Minecraft;
import net.minecraft.client.network.NetworkPlayerInfo;
import net.minecraft.util.ChatComponentText;
import net.minecraftforge.client.event.ClientChatReceivedEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.util.Collection;
import java.util.Iterator;

public class CensorNameEvents {


    private CensorNames cn = CensorNames.getInstance();
    private CensorMap censorMap = cn.getCensorMap();

    private long player_list_update_time = 0;
    private int player_list_update_interval = 500;
    
    private Collection<NetworkPlayerInfo> oldInfoMap;
    
    @SubscribeEvent
    public void giveMePlayerListPls(RenderGameOverlayEvent.Pre event) {
        if(event.type == RenderGameOverlayEvent.ElementType.PLAYER_LIST) {
            if(System.currentTimeMillis() - player_list_update_time >= player_list_update_interval) {
                updatePlayerList();
                player_list_update_time = System.currentTimeMillis();
                
            }
            
        }
    }
    
    @SubscribeEvent
    public void nametagRequested(PlayerEvent.NameFormat event) {
    
        if(!censorMap.containsKey(event.username)) {
            censorMap.putName(event.username);
        }
 
        event.displayname = ColorCode.LIGHTGRAY + censorMap.getCensoredName(event.username);
    }
    
    @SubscribeEvent
    public void worldUnload(WorldEvent.Unload event) {
        censorMap.clear();
    }
    

    @SubscribeEvent(priority = EventPriority.LOW)
    public void chatEvent(ClientChatReceivedEvent event) {
    
        updatePlayerList();
    
    
        String goodFormat = event.message.getFormattedText();
        String lessGoodFormat = event.message.getUnformattedText();
        
        System.out.println("THE ORIGINAL GOOD MESSAGE: " + goodFormat);
        
        boolean changedAthing = false;
        
        for(String playerName : censorMap.keySet()) {
            if(playerName.isEmpty()) System.err.println("THE TRYHARD WAS EMPTY!!!!!!!");
            if(goodFormat.contains(playerName)) {
                
                
                // if their name has discrete color (ex. joining game messsages)
                /*goodFormat = goodFormat.replaceAll(
                    "(?:(?!.))*(\u00A7[6,7,a,b]){1}" + playerName, 
                    ColorCode.LIGHTGRAY + censorMap.getCensoredName(playerName)
                );*/
                
                // if their name carries over the color from before (ex. chat)
                goodFormat = goodFormat.replaceAll(
                    playerName,
                    censorMap.getCensoredName(playerName)
                );
                
                // Make their chat white if they have no rank
                goodFormat = goodFormat.replaceAll(
                    "(?:(?!.))*"+ColorCode.LIGHTGRAY+":",
                    ColorCode.RESET + ":"
                );

                goodFormat = goodFormat.replaceAll(
                        "\u00A7r\u00A7e" + censorMap.getCensoredName(playerName) + "\u00A7r\u00A77",
                        ColorCode.YELLOW + censorMap.getCensoredName(playerName) + ColorCode.RESET
                );

                goodFormat = CensorFormat.removeRanks(goodFormat, censorMap.getCensoredName(playerName));

                changedAthing = true;
            }
        }
    
        
        if(changedAthing) {
            System.out.println("I changed something!");
            
            // Remove their rank
            //goodFormat = goodFormat.replaceAll("(?:(?!.))*\u00A7.\\[(MVP|VIP)(\u00A7.)*\\+{0,2}(\u00A7.)*\\] ", ColorCode.LIGHTGRAY);

            System.out.println("THE LESS GOOD MESSAGE: " + lessGoodFormat);
            System.out.println("THE ACTUALLY GOOD MESSAGE: " + goodFormat);
            event.message = new ChatComponentText(goodFormat);
            
        }
    }
    
    private void updatePlayerList() {
        Collection<NetworkPlayerInfo> tabList = Minecraft.getMinecraft().getNetHandler().getPlayerInfoMap();
        Iterator<NetworkPlayerInfo> tabListIter = tabList.iterator();
        while(tabListIter.hasNext()) {
        
            NetworkPlayerInfo player = tabListIter.next();
            GameProfile profile = player.getGameProfile();
            String name = profile.getName();
            
            if(!censorMap.containsKey(name)) {
                censorMap.putName(name);
            }


            /*if(player.getDisplayName() != null) {
                //String newName = player.getDisplayName().getFormattedText();
                //System.out.println("OLD DISPLAY NAME NEW NOW " + newName);
                //player.setDisplayName(new ChatComponentText(newName));
            } else {*/
                String newName = name;
                newName = newName.replaceAll(name, censorMap.getCensoredName(name));




                if(player.getPlayerTeam() != null) {
                    System.out.println(player.getPlayerTeam().formatString("FORMATTED STRING"));
                    newName = player.getPlayerTeam().formatString(newName);
                } //Minecraft.getMinecraft().ingameGUI.getTabList().getPlayerName(player);

                newName = CensorFormat.removeRanks(newName, censorMap.getCensoredName(name));

                System.out.println("NEW DISPLAY NAME " + newName);
                player.setDisplayName(new ChatComponentText(ColorCode.LIGHTGRAY + newName));
            //}
            //System.out.println("THEIR TEAM IS: " + player.getPlayerTeam().formatString("I AM INPUT"));

        }
    }
    
}
