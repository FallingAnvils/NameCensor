package me.fallinganvils.censornames.commands;

import me.fallinganvils.censornames.CensorNames;
import net.minecraft.util.ChatComponentText;

import net.minecraft.command.CommandException;
import net.minecraft.command.ICommand;
import net.minecraft.command.ICommandSender;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.entity.player.EntityPlayer;
import java.util.HashMap;
import java.util.List;
import net.minecraft.command.CommandBase;
import net.minecraft.server.MinecraftServer;

import java.util.Arrays;
import java.util.List;
import java.util.ArrayList;

public class RealNameCommand extends CommandBase {

    private CensorNames cn = CensorNames.getInstance();

    @Override
    public String getCommandName() {
        return "realname";
    }

    @Override
    public String getCommandUsage(ICommandSender sender) {
        return EnumChatFormatting.RED + "Usage: /realname";
    }

    @Override
    public List<String> getCommandAliases() {
        return Arrays.asList("rn");
    }

    @Override
    public void processCommand(ICommandSender sender, String[] args) throws CommandException {
    
        if(args.length < 1) return;
        if(!(sender instanceof EntityPlayer)) return;
        
        System.out.println("THE COMMAND RUN, ARG0 " + args[0]);
        
        HashMap<String, Integer> playerMap = cn.getCensorMap();
        for(String key : playerMap.keySet()) {
            String hashStr = cn.getCensorMap().get(key).toString();
            if(hashStr.startsWith(args[0])) {
                ((EntityPlayer)sender).addChatMessage(new ChatComponentText("§2Real name of Player#" + hashStr + ": " + key));
            }
        }
    }


    @Override
    public List<String> addTabCompletionOptions(ICommandSender sender, String[] args, BlockPos pos) {
        
        if(args.length < 1) return null;
        if(!(sender instanceof EntityPlayer)) return null;
        
        ArrayList<String> tabOptions = new ArrayList<String>();
    
        HashMap<String, Integer> playerMap = cn.getCensorMap();
        for(String key : playerMap.keySet()) {
            String hashStr = cn.getCensorMap().get(key).toString();
            if(hashStr.startsWith(args[0])) {
                tabOptions.add(hashStr);
            }
        }
        
        return tabOptions;
    }

    @Override
    public boolean isUsernameIndex(String[] args, int index) {
        return false;
    }

    @Override
    public int getRequiredPermissionLevel() {
        return 0;
    }
    
    @Override
    public int compareTo(ICommand o) {
        return 0;
    }
}
