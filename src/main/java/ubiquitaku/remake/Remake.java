package ubiquitaku.remake;

import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.*;

public final class Remake extends JavaPlugin {
    FileConfiguration config;
    Map<String, String> map;
    List<String> names;

    @Override
    public void onEnable() {
        // Plugin startup logic
        saveDefaultConfig();
        config = getConfig();
        loadConfig();
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (command.getName().equals("remake")) {
            if (args.length == 0) {
                Player p = (Player) sender;
                if (!p.getInventory().getItemInMainHand().hasItemMeta()) {
                    sender.sendMessage("交換対象のアイテムではないため交換されませんでした");
                    return true;
                }
                String str = p.getInventory().getItemInMainHand().getItemMeta().getDisplayName();
                if (!map.containsKey(str)) {
                    sender.sendMessage("交換対象のアイテムではないため交換されませんでした");
                    return true;
                }
                change(p,p.getInventory().getItemInMainHand().getAmount(),str);
                sender.sendMessage("交換対象のアイテムだったため交換されました");
                return true;
            }
            if (args[0].equals("help")) {
                sender.sendMessage("/remake : 手に持っているアイテムが保存されているアイテムの情報と一致した場合入れ替える");
                return true;
            }
        }
        return true;
    }

    public void loadConfig() {
        map = new HashMap<>();
        for (String key: config.getConfigurationSection("Items").getKeys(false)){
            map.put(key, config.getString("Items." + key));
        }
        names = getNames();
    }

    public List getNames() {
        names = new ArrayList<>();
        List list = new ArrayList();
        for (Map.Entry<String,String> m : map.entrySet()) {
            list.add(m.getKey());
        }
        return list;
    }

    public void change(Player p,int amo,String itemName) {
//        ItemStack itemStack = new ItemStack(Material.getMaterial("IRON_AXE"));
        ItemStack itemStack = new ItemStack(Material.getMaterial(config.getString("Items."+itemName+".Material")));
        ItemMeta meta = itemStack.getItemMeta();
        meta.setLore((List<String>) config.getList("Items."+itemName+".Lore"));
        itemStack.setItemMeta(meta);
        itemStack.setAmount(amo);
        p.getInventory().setItemInMainHand(itemStack);
    }
}
