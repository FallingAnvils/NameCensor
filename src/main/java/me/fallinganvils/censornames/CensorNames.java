package me.fallinganvils.censornames;

import me.fallinganvils.censornames.commands.RealNameCommand;
import me.fallinganvils.censornames.events.CensorNameEvents;
import me.fallinganvils.censornames.util.CensorMap;

import net.minecraft.client.Minecraft;
import net.minecraftforge.client.ClientCommandHandler;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import com.google.common.hash.HashCode;

@Mod(modid = CensorNames.MODID, version = CensorNames.VERSION, acceptedMinecraftVersions="[1.8.9]")
public class CensorNames {

    public static final String MODID = "CensorNames";
    public static final String VERSION = "0.1";

    @Mod.Instance
    private static CensorNames instance;
    

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event) {
    }

    @Mod.EventHandler
    public void init(FMLInitializationEvent event) {
        ClientCommandHandler.instance.registerCommand(new RealNameCommand());
        MinecraftForge.EVENT_BUS.register(new CensorNameEvents());
    }

    private CensorMap censorMap = new CensorMap("Player#");
    
    public CensorMap getCensorMap() {
        return censorMap;
    }
    
    public static CensorNames getInstance() {
        return instance;
    }
}
