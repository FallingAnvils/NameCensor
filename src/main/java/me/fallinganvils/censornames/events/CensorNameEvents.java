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
        
        for(String tryhard : cn.getCensorMap().keySet()) {
            if(tryhard.isEmpty()) System.err.println("THE TRYHARD WAS EMPTY!!!!!!!");
            if(goodFormat.contains(tryhard) /*&& !tryhard.isEmpty()*/) {
                
                
                // if their name has discrete color (ex. joining game messsages)
                goodFormat = goodFormat.replaceAll(
                    "(?:(?!.))*(§[6,7,a,b]){1}" + tryhard, 
                    "§7Player#" + cn.getCensorMap().get(tryhard)
                );
                
                // if their name carries over the color from before (ex. chat)
                goodFormat = goodFormat.replaceAll(
                    tryhard,
                    "Player#" + cn.getCensorMap().get(tryhard)
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
        Collection<NetworkPlayerInfo> tryhards_and_also_me = Minecraft.getMinecraft().getNetHandler().getPlayerInfoMap();
        Iterator<NetworkPlayerInfo> stupidJavaConstruct = tryhards_and_also_me.iterator();
        while(stupidJavaConstruct.hasNext()) {
        
            NetworkPlayerInfo a_single_tryhard_or_also_me = stupidJavaConstruct.next();

            GameProfile tryhards_profile = a_single_tryhard_or_also_me.getGameProfile();
            String tryhards_name = tryhards_profile.getName();
            
            if(!cn.getCensorMap().containsKey(tryhards_name)) {
                if(tryhards_name != null && !tryhards_name.isEmpty() && tryhards_name.hashCode() != 0) { // stuff we dont want
                    cn.getCensorMap().put(tryhards_name, tryhards_name.hashCode());
                } else {
                    System.out.println("EMPTY NAME, DISPLAY: " + a_single_tryhard_or_also_me.getDisplayName());
                }
            }
            
            a_single_tryhard_or_also_me.setDisplayName(new ChatComponentText("§7Player#" + cn.getCensorMap().get(tryhards_name)));
            
        }
    }
    
}
