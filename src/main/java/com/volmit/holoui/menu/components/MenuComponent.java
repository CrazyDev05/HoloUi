package com.volmit.holoui.menu.components;

import com.volmit.holoui.config.MenuComponentData;
import com.volmit.holoui.config.components.ButtonComponentData;
import com.volmit.holoui.config.components.ComponentData;
import com.volmit.holoui.config.components.DecoComponentData;
import com.volmit.holoui.config.components.ToggleComponentData;
import com.volmit.holoui.menu.MenuSession;
import com.volmit.holoui.menu.icon.MenuIcon;
import com.volmit.holoui.menu.special.BlockMenuSession;
import com.volmit.holoui.menu.special.inventories.InventoryProgressComponent;
import com.volmit.holoui.menu.special.inventories.InventorySlotComponent;
import com.volmit.holoui.utils.math.MathHelper;
import lombok.Getter;
import org.bukkit.Location;
import org.bukkit.util.Vector;

public abstract class MenuComponent<T extends ComponentData> {

    protected final MenuSession session;
    protected final Vector offset;
    protected final T data;

    @Getter
    protected final String id;

    @Getter
    protected Location location;
    protected MenuIcon<?> currentIcon;

    @Getter
    protected boolean open = false;

    @SuppressWarnings("unchecked")
    public MenuComponent(MenuSession session, MenuComponentData data) {
        this.session = session;
        this.id = data.id();
        this.offset = data.offset().clone().multiply(new Vector(-1, 1, 1));
        this.data = (T) data.data();

        this.location = session.getCenterPoint().clone().add(offset);
    }

    public static MenuComponent<?> getComponent(MenuSession session, MenuComponentData data) {
        return switch (data.data()) {
            case ButtonComponentData ignored -> new ButtonComponent(session, data);
            case DecoComponentData ignored -> new DecoComponent(session, data);
            case ToggleComponentData ignored -> new ToggleComponent(session, data);
            case InventorySlotComponent.Data ignored -> new InventorySlotComponent(session, data);
            case InventoryProgressComponent.Data ignored -> new InventoryProgressComponent(session, data);
            case null, default -> null;
        };
    }

    public void tick() {
        if (!open) return;
        onTick();
        if (currentIcon != null)
            currentIcon.tick();
    }

    protected abstract void onTick();

    protected abstract MenuIcon<?> createIcon();

    protected abstract void onOpen();

    protected abstract void onClose();

    public void open(boolean rotateByPlayer) {
        adjustRotation(rotateByPlayer);
        this.currentIcon = createIcon();
        this.currentIcon.spawn();
        onOpen();
        open = true;
    }

    public void close() {
        open = false;
        if (this.currentIcon != null)
            this.currentIcon.remove();
        onClose();
    }

    public void adjustRotation(boolean byPlayer) {
        if (byPlayer)
            rotateByPlayer();
        else
            rotateByCenter();
        if (this.currentIcon != null)
            this.currentIcon.teleport(location);
    }

    public void move(Location loc) {
        this.location = loc.add(offset);
    }

    public void rotate(float yaw) {
        if (this.currentIcon != null)
            this.currentIcon.rotate(yaw);
    }

    protected void rotateByPlayer() {
        MathHelper.rotateAroundPoint(this.location, session.getPlayer().getEyeLocation(), 0, session.getInitialY());
    }

    protected void rotateByCenter() {
        if (session instanceof BlockMenuSession)
            MathHelper.rotateAroundPoint(this.location, session.getCenterPoint(), 0, session.getInitialY());
    }
}
