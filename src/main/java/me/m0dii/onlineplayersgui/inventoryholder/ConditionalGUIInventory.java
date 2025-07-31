package me.m0dii.onlineplayersgui.inventoryholder;

import me.m0dii.onlineplayersgui.CustomItem;
import me.m0dii.onlineplayersgui.OnlineGUI;
import me.m0dii.onlineplayersgui.utils.*;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.IntStream;

public class ConditionalGUIInventory implements InventoryHolder, CustomGUI {
    private Inventory inv;
    private final Component name;
    private final int size, page;
    private final OnlineGUI plugin;

    private final String condition;

    private final ConditionalConfig cfg;

    public ConditionalGUIInventory(@NotNull OnlineGUI plugin, @NotNull Component name, int page, @NotNull ConditionalConfig cfg) {
        this.size = adjustSize();

        this.cfg = cfg;

        this.name = name;
        this.page = page;

        this.plugin = plugin;

        this.condition = this.cfg.getCondition();

        this.inv = Bukkit.createInventory(this, this.size, this.cfg.getGuiTitle());

        initByPage(page);
    }

    @Override
    public void execute(Player clickee, ItemStack clicked, ClickType clickType, int slot) {
        if (clicked == null) {
            return;
        }

        Material type = clicked.getType();

        if (type.equals(cfg.getPlayerHeadDisplay().getType())) {
            Player skullOwner = VersionUtils.getSkullOwner(clicked);

            if (skullOwner == null) {
                return;
            }

            List<String> cmds = new ArrayList<>();

            if (clickType.equals(ClickType.LEFT)) {
                cmds = this.cfg.getLeftClickCommands();
            }

            if (clickType.equals(ClickType.MIDDLE)) {
                cmds = this.cfg.getMiddleClickCommands();
            }

            if (clickType.equals(ClickType.RIGHT)) {
                cmds = this.cfg.getRightClickCommands();
            }

            CommandActionParser.parse(clickee, skullOwner, cmds);
        }

        if (type.equals(this.cfg.getPrevPageMat()) || type.equals(this.cfg.getNextPageMat())) {
            int nextPage = page;

            if (cfg.getNextPageSlot() == slot) {
                nextPage = page + 1;
            } else if (cfg.getPrevPageSlot() == slot) {
                nextPage = page - 1;
            }

            try {
                ConditionalGUIInventory newinv = new ConditionalGUIInventory(this.plugin, this.name, nextPage, cfg);

                if (newinv.hasPlayers()) {
                    clickee.openInventory(newinv.getInventory());
                }
            } catch (IndexOutOfBoundsException ex) {
                Messenger.debug("IndexOutOfBoundsException: " + ex.getMessage());
            }
        }

        CustomItem c = this.getCustomItemBySlot(slot);

        if (c == null) {
            return;
        }

        List<String> cicmds = new ArrayList<>();

        if (clickType.equals(ClickType.LEFT)) {
            cicmds = c.getLcc();
        }

        if (clickType.equals(ClickType.MIDDLE)) {
            cicmds = c.getMcc();
        }

        if (clickType.equals(ClickType.RIGHT)) {
            cicmds = c.getRcc();
        }

        CommandActionParser.parse(clickee, clickee, cicmds);
    }

    @Override
    public void refresh(Player p) {
        this.inv = Bukkit.createInventory(this, this.size, this.name);

        initByPage(this.page);
        setCustomItems(p);

        p.openInventory(this.inv);
    }

    private CustomItem getCustomItemBySlot(int slot) {
        return cfg.getCustomItems().getOrDefault(slot, null);
    }

    public void setCustomItems(Player p) {
        plugin.getGuiUtils().setCustomItems(inv, p, cfg.getCustomItems());
    }

    private int adjustSize() {
        int guiSize = cfg.getGuiSize();

        if (guiSize < 18) {
            return 18;
        } else if (guiSize > 54) {
            return 54;
        } else if (guiSize % 9 == 0) {
            return guiSize;
        }

        return 54;
    }

    @Override
    public @NotNull Inventory getInventory() {
        return inv;
    }

    private List<Player> getByPage(int page) {
        String permission = cfg.isPermissionRequired() ? cfg.getRequiredPermission() : null;
        String cond = condition.isEmpty() ? null : condition;

        List<Player> online = plugin.getGuiUtils().getOnline(permission, cond);

        List<Player> byPage = new ArrayList<>();

        int availableSlots = this.size - 9;

        for (Map.Entry<Integer, CustomItem> entry : cfg.getCustomItems().entrySet()) {
            if (entry.getKey() >= this.size - 9) {
                continue;
            }

            availableSlots--;
        }

        if (cfg.getNextPageSlot() < this.size - 9) {
            availableSlots--;
        }

        if (cfg.getPrevPageSlot() < this.size - 9) {
            availableSlots--;
        }

        int lowBound = availableSlots * page;
        int highBound = availableSlots * (page == 0 ? 1 : page + 1);

        for (int i = lowBound; i < highBound; i++) {
            if (lowBound < online.size() && i < online.size()) {
                byPage.add(online.get(i));
            }
        }

        return byPage;
    }

    private void initByPage(int page) {
        List<Player> byPage = getByPage(page);

        for (Player player : byPage) {
            ItemStack head = VersionUtils.getSkull(player, cfg.getHeadLore(), cfg.getHeadName());

            IntStream.range(0, inv.getSize())
                    .filter(i -> inv.getItem(i) == null)
                    .filter(i -> cfg.getNextPageSlot() != i && cfg.getPrevPageSlot() != i)
                    .findFirst()
                    .ifPresent(i -> inv.setItem(i, head));
        }

        setButtons();
    }

    public boolean hasPlayers() {
        return !getByPage(page).isEmpty();
    }

    public boolean hasPlayers(int offset) {
        return !getByPage(page + offset).isEmpty();
    }

    private void setButtons() {
        boolean show = plugin.getCfg().isButtonsAlwaysVisible();

        if (show || hasPlayers(1)) {
            setNextButton();
        }

        if (show || page != 0) {
            setPreviousButton();
        }
    }

    private void setNextButton() {
        ItemStack nextButton = new ItemStack(cfg.getNextPageMat());
        ItemMeta nextButtonMeta = nextButton.getItemMeta();

        List<Component> nextLore = cfg.getNextPageLore().stream()
                .map(str -> Utils.setPlaceholders(str, null))
                .map(TextUtils::kyorify)
                .toList();

        nextButtonMeta.lore(nextLore);
        nextButtonMeta.displayName(cfg.getNextPageName());
        nextButton.setItemMeta(nextButtonMeta);

        inv.setItem(cfg.getNextPageSlot(), nextButton);
    }

    private void setPreviousButton() {
        ItemStack prevButton = new ItemStack(cfg.getPrevPageMat());
        ItemMeta prevButtonMeta = prevButton.getItemMeta();

        List<Component> prevLore = cfg.getPrevPageLore().stream()
                .map(str -> Utils.setPlaceholders(str, null))
                .map(TextUtils::kyorify)
                .toList();

        prevButtonMeta.lore(prevLore);
        prevButtonMeta.displayName(cfg.getPrevPageName());
        prevButton.setItemMeta(prevButtonMeta);

        inv.setItem(cfg.getPrevPageSlot(), prevButton);
    }
}
