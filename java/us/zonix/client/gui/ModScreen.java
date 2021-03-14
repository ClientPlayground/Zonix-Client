package us.zonix.client.gui;

import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.ScaledResolution;
import org.lwjgl.opengl.GL11;
import us.zonix.client.Client;
import us.zonix.client.gui.component.IComponent;
import us.zonix.client.gui.component.impl.menu.MenuComponent;
import us.zonix.client.module.IModule;
import us.zonix.client.module.impl.MiniMap;
import us.zonix.client.util.RenderUtil;

import java.awt.Color;
import java.util.HashSet;
import java.util.Set;

public final class ModScreen extends GuiScreen {

    private final class DragMod {
        private final IModule module;
        private boolean moved;
        private int x;
        private int y;

        @java.beans.ConstructorProperties({"module", "x", "y"})
        public DragMod(IModule module, int x, int y) {
            this.module = module;
            this.x = x;
            this.y = y;
        }
    }

    public static final int NORMAL_COLOR = new Color(169, 169, 169, 200).getRGB();
    public static final int HOVER_COLOR = new Color(255, 0, 117, 200).getRGB();

    private final Set<IComponent> components = new HashSet<>();

    private DragMod dragging;

    private boolean open;

    private void addButtons() {
        this.components.add(new MenuComponent());
    }

    @Override
    public void initGui() {
        this.open = true;

        new Thread(() -> {
            while (ModScreen.this.open) {
                for (IComponent component : ModScreen.this.components) {
                    component.tick();
                }

                try {
                    Thread.sleep(10);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();

        this.mc.entityRenderer.setBlur(true);

        if (this.components.isEmpty()) {
            this.addButtons();
        }

        for (IComponent component : this.components) {
            component.onOpen();
        }
    }

    @Override
    public void onGuiClosed() {
        this.open = false;

        this.mc.entityRenderer.setBlur(false);
    }

    @Override
    protected void mouseMovedOrUp(int mouseX, int mouseY, int button) {
        this.dragging = null;

        this.components.forEach(IComponent::onMouseRelease);
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        // Guide lines
        ScaledResolution resolution = new ScaledResolution(this.mc);

        if (this.dragging != null) {
            float pageWidth = resolution.getScaledWidth();
            float pageHeight = resolution.getScaledHeight();

            RenderUtil.drawRect(0, 2, pageWidth, 3, 0xEEE23F3F);
            RenderUtil.drawRect(2, 0, 3, pageHeight, 0xEEE23F3F);

            RenderUtil.drawRect(pageWidth - 2, 0, pageWidth - 3, pageHeight, 0xEEE23F3F);
            RenderUtil.drawRect(0, pageHeight - 2, pageWidth, pageHeight - 3, 0xEEE23F3F);

            RenderUtil.drawRect(0, pageHeight / 2 - 0.5F, pageWidth, pageHeight / 2 + 0.5F, 0xEEE23F3F);
            RenderUtil.drawRect(pageWidth / 2 - 0.5F, 0, pageWidth / 2 + 0.5F, pageHeight, 0xEEE23F3F);
        }

        for (IModule module : Client.getInstance().getModuleManager().getEnabledModules()) {
            module.renderPreview();

            if (module.getWidth() == 0 || module.getHeight() == 0) {
                continue;
            }

            GL11.glPushMatrix();
            {
                boolean mouseOver = this.isMouseOver(module, mouseX, mouseY);
                int borderColor = mouseOver ? HOVER_COLOR : NORMAL_COLOR;

                RenderUtil.drawBorderedRect(module.getX(), module.getY(), module.getX() + module.getWidth(),
                        module.getY() + module.getHeight(), 1, borderColor, 0x1AFFFFFF);
            }
            GL11.glPopMatrix();
        }

        if (this.dragging != null) {
            float setX = mouseX - this.dragging.x;
            float setY = mouseY - this.dragging.y;

            if (mouseX - this.dragging.module.getX() != this.dragging.x ||
                    mouseY - this.dragging.module.getY() != this.dragging.y) {
                this.dragging.moved = true;
            }

            int height = this.dragging.module.getHeight();
            int width = this.dragging.module.getWidth();

            if (setX < 2) {
                setX = 2;
            } else if (setX + width > resolution.getScaledWidth() - 2) {
                setX = resolution.getScaledWidth() - width - 2;
            }

            if (setY < 2) {
                setY = 2;
            } else if (setY + height > resolution.getScaledHeight() - 2) {
                setY = resolution.getScaledHeight() - height - 2;
            }

            this.dragging.module.setX(setX);
            this.dragging.module.setY(setY);

            for (IModule module : Client.getInstance().getModuleManager().getEnabledModules()) {
                if (module == this.dragging.module) {
                    continue;
                }

                if (module.getWidth() == 0 || module.getHeight() == 0) {
                    continue;
                }

                this.snapToModule(module);
            }

            this.snapToGuideLines(resolution);
        }

        if (this.dragging == null) {
            for (IComponent component : this.components) {
                component.draw(mouseX, mouseY);
            }
        }
    }

    @Override
    public void handleMouseInput() {
        super.handleMouseInput();

        if (this.dragging == null) {
            this.components.forEach(IComponent::onMouseEvent);
        }
    }

    @Override
    protected void keyTyped(char c, int key) {
        super.keyTyped(c, key);

        this.components.forEach(iComponent -> iComponent.onKeyPress(key, c));
    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int button) {
        if (this.dragging == null) {
            this.components.forEach(component -> {
                if (component instanceof MenuComponent) {
                    component.onClick(mouseX, mouseY, button);
                    return;
                }

                if (mouseX > component.getX() && mouseX < component.getX() + component.getWidth() &&
                        mouseY > component.getY() && mouseY < component.getY() + component.getHeight()) {
                    component.onClick(mouseX, mouseY, button);
                }
            });
        }

        for (IModule module : Client.getInstance().getModuleManager().getEnabledModules()) {
            if (module instanceof MiniMap) {
                continue;
            }

            if (!this.isMouseOver(module, mouseX, mouseY)) {
                continue;
            }

            if (button != 1) {
                int x = (int) (mouseX - module.getX());
                int y = (int) (mouseY - module.getY());

                this.dragging = new DragMod(module, x, y);
            } else if (module.getSettingMap().size() > 1) {
                MenuComponent menuComponent = (MenuComponent) this.components.toArray(new IComponent[0])[0];
                if (menuComponent.getMenuType() == MenuComponent.EnumMenuType.MODS) {
                    menuComponent.setScrollAmount(0);
                    menuComponent.setSwitchTime(0);
                }

                menuComponent.setMenuType(MenuComponent.EnumMenuType.MOD);
                menuComponent.setEditing(module);
            }
            break;
        }
    }

    private boolean isMouseOver(IModule module, int mouseX, int mouseY) {
        float minX = module.getX();
        float minY = module.getY();

        float maxX = minX + module.getWidth();
        float maxY = minY + module.getHeight();

        return mouseX > minX && mouseY > minY && mouseX < maxX && mouseY < maxY;
    }

    private void snapToModule(IModule module) {
        IModule dragging = this.dragging.module;

        float minToMinX = module.getX() - dragging.getX();
        float maxToMaxX = (module.getX() + module.getWidth()) - (dragging.getX() + dragging.getWidth());

        float maxToMinX = (module.getX() + module.getWidth()) - (dragging.getX());
        float minToMaxX = (module.getX()) - (dragging.getX() + dragging.getWidth());

        float minToMinY = module.getY() - dragging.getY();
        float maxToMaxY = (module.getY() + module.getHeight()) - (dragging.getY() + dragging.getHeight());

        float maxToMinY = (module.getY() + module.getHeight()) - (dragging.getY());
        float minToMaxY = (module.getY()) - (dragging.getY() + dragging.getHeight());

        boolean xSnap = false;
        boolean ySnap = false;

        if (minToMinX >= -2 && minToMinX <= 2) {
            dragging.setX(dragging.getX() + minToMinX);
            xSnap = true;
        }

        if (maxToMaxX >= -2 && maxToMaxX <= 2) {
            if (!xSnap) {
                dragging.setX(dragging.getX() + maxToMaxX);
                xSnap = true;
            }
        }

        if (minToMaxX >= -2 && minToMaxX <= 2) {
            if (!xSnap) {
                dragging.setX(dragging.getX() + minToMaxX);
                xSnap = true;
            }
        }

        if (maxToMinX >= -2 && maxToMinX <= 2) {
            if (!xSnap) {
                dragging.setX(dragging.getX() + maxToMinX);
            }
        }

        if (minToMinY >= -2 && minToMinY <= 2) {
            dragging.setY(dragging.getY() + minToMinY);
            ySnap = true;
        }

        if (maxToMaxY >= -2 && maxToMaxY <= 2) {
            if (!ySnap) {
                dragging.setY(dragging.getY() + maxToMaxY);
                ySnap = true;
            }
        }

        if (minToMaxY >= -2 && minToMaxY <= 2) {
            if (!ySnap) {
                dragging.setY(dragging.getY() + minToMaxY);
                ySnap = true;
            }
        }

        if (maxToMinY >= -2 && maxToMinY <= 2) {
            if (!ySnap) {
                dragging.setY(dragging.getY() + maxToMinY);
            }
        }
    }

    private void snapToGuideLines(ScaledResolution resolution) {
        IModule dragging = this.dragging.module;

        float height = resolution.getScaledHeight() / 2;
        float width = resolution.getScaledWidth() / 2;

        float draggingMinX = dragging.getX();
        float draggingMaxX = draggingMinX + this.dragging.module.getWidth();
        float draggingHalfX = draggingMinX + this.dragging.module.getWidth() / 2;

        float draggingMinY = dragging.getY();
        float draggingMaxY = draggingMinY + this.dragging.module.getHeight();
        float draggingHalfY = draggingMinY + this.dragging.module.getHeight() / 2;

        if (this.checkBounds(draggingMinX, width)) {
            dragging.setX(width);
        }

        if (this.checkBounds(draggingMinY, height)) {
            dragging.setY(height);
        }

        if (this.checkBounds(draggingMaxX, width)) {
            dragging.setX(width - dragging.getWidth());
        }

        if (this.checkBounds(draggingMaxY, height)) {
            dragging.setY(height - dragging.getHeight());
        }

        if (this.checkBounds(draggingHalfX, width)) {
            dragging.setX(width - dragging.getWidth() / 2);
        }

        if (this.checkBounds(draggingHalfY, height)) {
            dragging.setY(height - dragging.getHeight() / 2);
        }
    }

    private boolean checkBounds(float f1, float f2) {
        return f1 >= f2 - 2 && f1 <= f2 + 2;
    }

}
