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
import me.fallinganvils.censornames.util.CensorMap;
import me.fallinganvils.censornames.util.ColorCode;

import com.google.common.hash.Hashing;
import java.nio.charset.Charset;

import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;
import java.util.Random;

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
                goodFormat = goodFormat.replaceAll(
                    "(?:(?!.))*(\u00A7[6,7,a,b]){1}" + playerName, 
                    ColorCode.LIGHTGRAY + censorMap.getCensoredName(playerName)
                );
                
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
                changedAthing = true;
            }
        }
    
        
        if(changedAthing) {
            System.out.println("I changed something!");
            
            // Remove their rank
            goodFormat = goodFormat.replaceAll("(?:(?!.))*\u00A7.\\[(MVP|VIP)(\u00A7.)*\\+{0,2}(\u00A7.)*\\] ", ColorCode.LIGHTGRAY);
            
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
            
            player.setDisplayName(new ChatComponentText(ColorCode.LIGHTGRAY + censorMap.getCensoredName(name)));
            
        }
    }
    
}
