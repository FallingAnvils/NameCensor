package me.fallinganvils.censornames.commands;

import me.fallinganvils.censornames.CensorNames;
import me.fallinganvils.censornames.util.CensorMap;
import me.fallinganvils.censornames.util.ColorCode;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommand;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.BlockPos;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.EnumChatFormatting;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class RealNameCommand extends CommandBase {

    private CensorNames cn = CensorNames.getInstance();
    private CensorMap censorMap = cn.getCensorMap();

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
        
        boolean hasResult = false;
        
        CensorMap playerMap = censorMap;
        for(String key : playerMap.keySet()) {
            String hashStr = censorMap.getShort(key);
            if(hashStr.startsWith(args[0])) {
                hasResult = true;
                ((EntityPlayer)sender).addChatMessage(new ChatComponentText(ColorCode.GREEN+"Real name of " + playerMap.getCensoredName(key) + "...: " + key));
            }
        }
        if(!hasResult) ((EntityPlayer)sender).addChatMessage(new ChatComponentText(ColorCode.LIGHTRED+"No matches found for " + args[0]));
    }


    @Override
    public List<String> addTabCompletionOptions(ICommandSender sender, String[] args, BlockPos pos) {
        
        if(args.length < 1) return null;
        if(!(sender instanceof EntityPlayer)) return null;
        
        ArrayList<String> tabOptions = new ArrayList<String>();
    
        CensorMap playerMap = censorMap;
        for(String key : playerMap.keySet()) {
            String hashStr = censorMap.getShort(key);
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
