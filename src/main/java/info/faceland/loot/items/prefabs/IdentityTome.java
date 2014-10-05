package info.faceland.loot.items.prefabs;

import info.faceland.hilt.HiltBook;
import org.bukkit.ChatColor;

import java.util.Arrays;

public final class IdentityTome extends HiltBook {

    public IdentityTome() {
        super(TomeType.WRITTEN_BOOK);
        setName(ChatColor.DARK_PURPLE + "Identity Tome");
        setTitle(ChatColor.DARK_PURPLE + "Identity Tome");
        setLore(Arrays.asList(ChatColor.WHITE + "Drop this item onto an Unidentified Item",
                              ChatColor.WHITE + "to identify it!"));
        setPages(Arrays.asList("Much identify", "Very magic", "So book", "Wow"));
    }

}