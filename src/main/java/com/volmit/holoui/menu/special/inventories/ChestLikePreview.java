package com.volmit.holoui.menu.special.inventories;

import com.google.common.collect.Lists;
import com.volmit.holoui.config.MenuComponentData;
import org.bukkit.block.Barrel;
import org.bukkit.block.Chest;
import org.bukkit.block.Container;
import org.bukkit.block.ShulkerBox;
import org.bukkit.inventory.DoubleChestInventory;
import org.bukkit.inventory.Inventory;

import java.util.List;

public class ChestLikePreview implements InventoryPreviewMenu<Inventory> {

    private static final float X_START = -2F;

    @Override
    public void supply(Container b, List<MenuComponentData> components) {
        Inventory inv = getInventory(b);
        if (inv instanceof DoubleChestInventory) {
            if (((org.bukkit.block.data.type.Chest) b.getBlockData()).getType() == org.bukkit.block.data.type.Chest.Type.LEFT)
                inv = ((DoubleChestInventory) inv).getRightSide();
            else
                inv = ((DoubleChestInventory) inv).getLeftSide();
        }
        components.addAll(getLine(inv, 0, .75F));
        components.addAll(getLine(inv, 9, .25F));
        components.addAll(getLine(inv, 18, -.25F));
    }

    @Override
    public boolean isValid(Container b) {
        return b instanceof Chest || b instanceof Barrel || b instanceof ShulkerBox;
    }

    private List<MenuComponentData> getLine(Inventory inv, int startIndex, float yOffset) {
        List<MenuComponentData> line = Lists.newArrayList();
        for (int i = 0; i < 9; i++)
            line.add(component("slot" + (i + startIndex), X_START + (i * .5F), yOffset, 0, new InventorySlotComponent.Data(inv, i + startIndex)));
        return line;
    }
}
