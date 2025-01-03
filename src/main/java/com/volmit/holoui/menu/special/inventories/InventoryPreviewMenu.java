package com.volmit.holoui.menu.special.inventories;

import com.google.common.collect.Lists;
import com.volmit.holoui.config.HuiSettings;
import com.volmit.holoui.config.MenuComponentData;
import com.volmit.holoui.config.MenuDefinitionData;
import com.volmit.holoui.config.components.ComponentData;
import com.volmit.holoui.menu.special.BlockMenuSession;
import org.bukkit.block.Block;
import org.bukkit.block.Container;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.util.Vector;

import java.util.List;
import java.util.Optional;

public interface InventoryPreviewMenu<T extends Inventory> {

    List<InventoryPreviewMenu<?>> PREVIEWS = Lists.newArrayList(new FurnacePreview(), new ChestLikePreview(), new DispenserMenu(), new HopperPreview());

    static BlockMenuSession create(Block block, Player p) {
        Optional<InventoryPreviewMenu<?>> optional = PREVIEWS.stream().filter(m -> m.isValid((Container) block.getState())).findFirst();
        if (optional.isPresent()) {
            InventoryPreviewMenu<?> menu = optional.get();
            Vector offset;
            if (HuiSettings.PREVIEW_FOLLOW_PLAYER.value())
                offset = new Vector(0, 0, 0);
            else
                offset = new Vector(0, .5, -1);
            List<MenuComponentData> data = Lists.newArrayList();
            menu.supply((Container) block.getState(), data);
            return new BlockMenuSession(new MenuDefinitionData(offset, false, false, data), p, block);
        }
        return null;
    }

    void supply(Container b, List<MenuComponentData> components);

    boolean isValid(Container container);

    @SuppressWarnings("unchecked")
    default T getInventory(Container b) {
        return (T) b.getInventory();
    }


    default MenuComponentData component(String id, float x, float y, float z, ComponentData d) {
        return new MenuComponentData(id, new Vector(x, y, z), d);
    }
}
