package me.fallinganvils.censornames.util;

import net.minecraft.util.ChatComponentText;
import net.minecraft.util.IChatComponent;

public class CensorFormat {
    public static String removeFullRank(String input) {
        return input.replaceAll("(?:(?!.))*\u00A7.\\[(MVP|VIP)(\u00A7.)*\\+{0,2}(\u00A7.)*\\] ", ColorCode.LIGHTGRAY);
    }
    public static String removeRankColor(String input, String currentName) {
        return input.replaceAll(
                "(?:(?!.))*(\u00A7[6,7,a,b]){1}" + currentName,
                ColorCode.LIGHTGRAY + currentName
        );
    }

    public static String removeGuild(String input) {
        return input.replaceAll(" \u00A7?.?\\[.{1,7}\\]$", "");
    }

    public static String removeRanks(String input, String currentName) {
        input = removeFullRank(input);
        input = removeRankColor(input, currentName);
        input = removeGuild(input);

        return input;
    }
}
