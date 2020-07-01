package me.fallinganvils.censornames.events;

import me.fallinganvils.censornames.CensorNames;

import net.minecraft.client.Minecraft;
import net.minecraftforge.client.event.ClientChatReceivedEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraft.util.ChatComponentText;
import net.minecraft.client.network.NetworkPlayerInfo;
import net.minecraft.util.IChatComponent;
import java.util.Collection;
import java.util.Iterator;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import com.mojang.authlib.GameProfile;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.world.WorldEvent;


import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;
import java.util.HashMap;
import java.util.Random;

public class CensorNameEvents {


    private CensorNames cn = CensorNames.getInstance();

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
    
        if(!cn.getCensorMap().containsKey(event.username) && event.username != null && !event.username.isEmpty() && event.username.hashCode() != 0) {
            cn.getCensorMap().put(event.username, event.username.hashCode());
        }
 
        event.displayname = "§7Player#" + cn.getCensorMap().get(event.username);
    }
    
    @SubscribeEvent
    public void worldUnload(WorldEvent.Unload event) {
        cn.getCensorMap().clear();
    }
    

    @SubscribeEvent(priority = EventPriority.LOW)
    public void chatEvent(ClientChatReceivedEvent event) {
    
        updatePlayerList();
    
    
        String goodFormat = event.message.getFormattedText();
        String lessGoodFormat = event.message.getUnformattedText();
        
        System.out.println("THE ORIGINAL GOOD MESSAGE: " + goodFormat);
        
        boolean changedAthing = false;
        
        for(String playerName : cn.getCensorMap().keySet()) {
            if(playerName.isEmpty()) System.err.println("THE TRYHARD WAS EMPTY!!!!!!!");
            if(goodFormat.contains(playerName)) {
                
                
                // if their name has discrete color (ex. joining game messsages)
                goodFormat = goodFormat.replaceAll(
                    "(?:(?!.))*(§[6,7,a,b]){1}" + playerName, 
                    "§7Player#" + cn.getCensorMap().get(playerName)
                );
                
                // if their name carries over the color from before (ex. chat)
                goodFormat = goodFormat.replaceAll(
                    playerName,
                    "Player#" + cn.getCensorMap().get(playerName)
                );
                
                // Make their chat white if they have no rank
                goodFormat = goodFormat.replaceAll(
                    "(?:(?!.))*§7:",
                    "§r:"
                );
                changedAthing = true;
            }
        }
    
        
        if(changedAthing) {
            System.out.println("I changed something!");
            
            // Remove their rank
            goodFormat = goodFormat.replaceAll("(?:(?!.))*§.\\[(MVP|VIP)(§.)*\\+{0,2}(§.)*\\] ", "§7");
            
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
            
            if(!cn.getCensorMap().containsKey(name)) {
                if(name != null && !name.isEmpty() && name.hashCode() != 0) { // stuff we dont want
                    cn.getCensorMap().put(name, name.hashCode());
                } else {
                    System.out.println("EMPTY NAME, DISPLAY: " + player.getDisplayName());
                }
            }
            
            player.setDisplayName(new ChatComponentText("§7Player#" + cn.getCensorMap().get(name)));
            
        }
    }
    
}
