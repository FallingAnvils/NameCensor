package me.fallinganvils.censornames.util;

import java.util.HashMap;
import com.google.common.hash.HashCode;
import com.google.common.hash.HashFunction;
import com.google.common.hash.Hashing;
import java.nio.charset.Charset;

public class CensorMap extends HashMap<String, HashCode> {
    
    private HashFunction hasher = Hashing.sha256();
    private String prefix;
    
    public CensorMap(String prefix) {
        this.prefix = prefix;
    }
    
    public String getPrefix() {
        return prefix;
    }
    
    public void setPrefix(String prefix) {
        this.prefix = prefix;
    }
    
    public String getCensoredName(String key) {
        return prefix + getShort(key);
    }
    
    public String getSubstring(String key, int amount) {
        HashCode code = this.get(key);
        if(code != null) return code.toString().substring(0, amount);
        else return "";
    }
    
    public String getShort(String key) {
        return this.getSubstring(key, 7);
    }
    
    public HashCode putName(String name) {
        if(name != null && !name.isEmpty()) return this.put(name, hasher.hashString(name, Charset.defaultCharset()));
        else return null;
    }
    

}
