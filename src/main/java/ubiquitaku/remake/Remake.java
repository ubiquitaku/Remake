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
            Player p = (Player) sender;
            if (args.length == 0) {
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
                if (sender.hasPermission("remake.control")) {
                    sender.sendMessage("/remake add : オフハンドのアイテム名を交換対象、メインハンドのMaterial、アイテム名、Loreを交換先とします");
                    sender.sendMessage("/remake remove : 手に持っているアイテムへの交換を削除します");
                    sender.sendMessage("/remake reload : ファイルのリロードをします");
                }
                return true;
            }
            if (!sender.hasPermission("remake.control")) {
                return true;
            }
            if (args[0].equals("add")) {
                if (!p.getInventory().getItemInMainHand().hasItemMeta() || !p.getInventory().getItemInOffHand().hasItemMeta()) {
                    sender.sendMessage("当pluginはアイテム名がデフォルトのアイテムは取り扱っておりません");
                    return true;
                }
                Material mat = p.getInventory().getItemInMainHand().getType();
                String name = p.getInventory().getItemInMainHand().getItemMeta().getDisplayName();
                List<String> lore = p.getInventory().getItemInMainHand().getItemMeta().getLore();
                ItemMeta meta = p.getInventory().getItemInOffHand().getItemMeta();
                config.set("Items."+meta.getDisplayName()+"Material",mat.name());
                config.set("Items."+meta.getDisplayName()+"Name",name);
                config.set("Items."+meta.getDisplayName()+"Lore",lore);
                reload();
                sender.sendMessage("設定しました");
            }
            if (args[0].equals("remove")) {
                if (!p.getInventory().getItemInMainHand().hasItemMeta()) {
                    sender.sendMessage("当pluginはアイテム名がデフォルトのアイテムは取り扱っておりません");
                    return true;
                }
                if (!map.containsKey("Items."+p.getInventory().getItemInMainHand().getItemMeta().getDisplayName())) {
                    sender.sendMessage("そのアイテム名は見つかりませんでした");
                    return true;
                }
                config.set("Items."+p.getInventory().getItemInMainHand().getItemMeta().getDisplayName(),"");
                reload();
                sender.sendMessage("削除しました");
            }
            if (args[0].equals("reload")) {
                fileReload();
                sender.sendMessage("リロード完了");
            }
        }
        return true;
    }

    public void loadConfig() {
        map = new HashMap<>();
        for (String key: config.getConfigurationSection("Items").getKeys(false)){
            map.put(key, config.getString("Items." + key));
        }
    }

    public void reload() {
        saveConfig();
        config = getConfig();
        loadConfig();
    }

    public void fileReload() {
        reloadConfig();
        config = getConfig();
        loadConfig();
    }

    public void change(Player p,int amo,String itemName) {
//        ItemStack itemStack = new ItemStack(Material.getMaterial("IRON_AXE"));
        ItemStack itemStack = new ItemStack(Material.getMaterial(config.getString("Items."+itemName+".Material")));
        ItemMeta meta = itemStack.getItemMeta();
        meta.setDisplayName(config.getString("Items."+itemName+".Name"));
        meta.setLore((List<String>) config.getList("Items."+itemName+".Lore"));
        itemStack.setItemMeta(meta);
        itemStack.setAmount(amo);
        p.getInventory().setItemInMainHand(itemStack);
    }
}
