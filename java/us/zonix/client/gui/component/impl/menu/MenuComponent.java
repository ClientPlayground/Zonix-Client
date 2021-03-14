package us.zonix.client.gui.component.impl.menu;

import lombok.Getter;
import lombok.Setter;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;
import us.zonix.client.Client;
import us.zonix.client.gui.component.IComponent;
import us.zonix.client.module.IModule;
import us.zonix.client.setting.ISetting;
import us.zonix.client.setting.impl.BooleanSetting;
import us.zonix.client.setting.impl.ColorSetting;
import us.zonix.client.setting.impl.FloatSetting;
import us.zonix.client.setting.impl.LabelSetting;
import us.zonix.client.setting.impl.StringSetting;
import us.zonix.client.setting.impl.TextSetting;
import us.zonix.client.util.RenderUtil;

import java.awt.Color;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public final class MenuComponent implements IComponent {

    private static final ResourceLocation ARROW_RIGHT = new ResourceLocation("icon/caret-right.png");
    private static final ResourceLocation ARROW_LEFT = new ResourceLocation("icon/caret-left.png");
    private static final ResourceLocation TOGGLE_OFF = new ResourceLocation("icon/toggle-off.png");
    private static final ResourceLocation TOGGLE_ON = new ResourceLocation("icon/toggle-on.png");
    private static final ResourceLocation SETTINGS = new ResourceLocation("icon/settings.png");

    private final double PI2 = Math.PI * 2.0;

    @Getter
    @Setter
    private EnumMenuType menuType = EnumMenuType.MODS;
    @Setter
    private FloatSetting draggingSetting;
    @Setter
    private IModule editing;
    @Setter
    private int switchTime = 450;
    @Setter
    private int scrollAmount;
    private boolean hiding;
    private int hidingTicks;
    @Getter
    @Setter
    private int width;
    @Getter
    @Setter
    private int height;
    @Getter
    @Setter
    private int x;
    @Getter
    @Setter
    private int y;

    private static String toHex(Color color) {
        return String.format("#%02x%02x%02x%02x", color.getAlpha(), color.getRed(), color.getGreen(), color.getBlue());
    }

    @Override
    public void setPosition(int x, int y) {
        this.x = x;
        this.y = y;
    }

    @Override
    public void onOpen() {

    }

    @Override
    public void tick() {
        this.hidingTicks++;

        if (this.switchTime <= 450.0F && this.switchTime >= 0) {
            if (this.menuType == EnumMenuType.MODS) {
                this.switchTime -= 25;
            } else {
                this.switchTime += 25;
            }
        }
    }

    @Override
    public void onMouseRelease() {
        this.draggingSetting = null;

        Client.getInstance().getModuleManager().getModules().forEach(module
                -> module.getSettingMap().values().forEach(setting -> {
            if (setting instanceof ColorSetting) {
                ColorSetting colorSetting = (ColorSetting) setting;
                colorSetting.setDraggingHue(false);
                colorSetting.setDraggingSaturation(false);
                colorSetting.setDraggingBrightness(false);
            }
        }));
    }

    @Override
    public void onKeyPress(int code, char c) {
        if (this.menuType == EnumMenuType.MOD) {
            List<ISetting> sortedSettings = this.editing.getSortedSettings();
            for (int i = 0; i < sortedSettings.size(); i++) {
                ISetting setting = sortedSettings.get(i);
                if (setting instanceof TextSetting && ((TextSetting) setting).isEditing()) {
                    String value = setting.getValue().toString();
                    if (code == Keyboard.KEY_BACK) {
                        if (!value.isEmpty()) {
                            ((TextSetting) setting).setValue(value.substring(0, value.length() - 1));
                        }
                    } else if (code == Keyboard.KEY_RETURN) {
                        ((TextSetting) setting).setEditing(false);
                    } else if (code == Keyboard.KEY_TAB) {
                        ((TextSetting) setting).setEditing(false);

                        if (i < sortedSettings.size() - 1) {
                            ISetting next = sortedSettings.get(i + 1);
                            if (next instanceof TextSetting) {
                                ((TextSetting) next).setEditing(true);
                            }
                        }
                        break;
                    } else if (Character.isLetterOrDigit(c) || " []()<>.&%+-_,'".contains(String.valueOf(c))) {
                        ((TextSetting) setting).setValue(value + c);
                    }
                } else if (setting instanceof ColorSetting) {
                    ColorSetting colorSetting = (ColorSetting) setting;

                    if (colorSetting.isTypingColor()) {
                        int index = colorSetting.getTypingIndex();

                        if ("abcdef0123456789".indexOf(c) != -1 && index < 6) {
                            if (Character.isLowerCase(c))
                                c -= 32;

                            colorSetting.getColorChars()[index] = c;
                            colorSetting.setTypingIndex(index + 1);
                        } else if (code == Keyboard.KEY_BACK && index > 0) {
                            colorSetting.setTypingIndex(index - 1);
                        } else if (code == Keyboard.KEY_RETURN) {
                            colorSetting.setTypingColor(false);

                            if (colorSetting.getTypingIndex() == 6) {
                                try {
                                    int color = Integer.parseInt(new String(colorSetting.getColorChars()), 16);
                                    colorSetting.setValue((color & 0x00FFFFFF) | 0xFF000000);
                                } catch (NumberFormatException e) {
                                    // invalid color, do nothing
                                }
                            }

                            colorSetting.setTypingIndex(0);
                        }
                    }
                }
            }
        }
    }

    @Override
    public void onClick(int mouseX, int mouseY, int button) {
        ScaledResolution resolution = new ScaledResolution(Minecraft.getMinecraft());

        switch (this.menuType) {
            case MOD: {
                float boxWidth = 450.0F;
                float boxHeight = 200.0F;
                float minX = resolution.getScaledWidth() / 2 - boxWidth / 2;
                float minY = resolution.getScaledHeight() / 2 - boxHeight / 2;

                float nameWidth = Client.getInstance().getRegularFontRenderer()
                        .getStringWidth(this.editing.getName());

                float x = minX + boxWidth / 2 - nameWidth / 2 - 25.0F;
                float y = minY + 12.0F;
                if (mouseX >= x && mouseX <= x + 16.0F && mouseY >= y && mouseY <= y + 16.0F) {
                    this.menuType = EnumMenuType.MODS;
                    this.scrollAmount = 0;
                    this.switchTime = 450;

                    this.editing.getSettingMap().values().forEach(setting -> {
                        if (setting instanceof ColorSetting) {
                            ColorSetting colorSetting = (ColorSetting) setting;
                            colorSetting.setPicking(false);
                        }
                    });
                }

                List<ISetting> settings = this.editing.getSortedSettings();

                float currentMaxY = minY + (settings.size() * 50.0F) + ((settings.size() - 1) * 10.0F);
                if (currentMaxY > minY + boxHeight) {
                    float translate = this.scrollAmount / 10.0F;
                    minY += translate;
                } else {
                    this.scrollAmount = 0;
                }

                minX += 25.0F;

                boolean switchedSelector = false;
                boolean switchedBoolean = false;

                float startY = minY + 40.0F;
                for (int i = 0; i < settings.size(); i++) {
                    ISetting setting = settings.get(i);
                    if (setting.getName().equals("Enabled")) {
                        continue;
                    }

                    boolean down = true;
                    float itemStart = 120.0F;

                    if (setting instanceof FloatSetting) {
                        switchedBoolean = switchedSelector = false;

                        if (mouseX >= minX + itemStart && mouseX <= minX + boxWidth - 50.0F &&
                                mouseY >= startY + 4.0F && mouseY <= startY + 12.5F) {
                            this.draggingSetting = (FloatSetting) setting;

                            float width = (minX + boxWidth - 50.0F) - (minX + itemStart);

                            float percent = (mouseX - (minX + itemStart)) / width * this.draggingSetting.getMax();
                            this.draggingSetting.setValue(percent);
                        }
                    } else if (setting instanceof LabelSetting) {
                        switchedBoolean = switchedSelector = false;
                    } else if (setting instanceof StringSetting) {
                        switchedBoolean = false;

                        x = minX;
                        if (!(switchedSelector = !switchedSelector)) {
                            x = minX + boxWidth - 100.0F - itemStart;
                        } else {
                            if (i < settings.size() - 1 && settings.get(i + 1).getClass() == setting.getClass()) {
                                down = false;
                            }
                        }

                        int index = ((StringSetting) setting).getIndex();

                        if (mouseX >= x + itemStart - 25.0F && mouseX <= x + itemStart - 15.0F &&
                                mouseY >= startY + 3.0F && mouseY <= startY + 13.0F) {
                            index--;
                        }

                        if (mouseX >= x + itemStart + 45.0F && mouseX <= x + itemStart + 55.0F &&
                                mouseY >= startY + 3.0F && mouseY <= startY + 13.0F) {
                            index++;
                        }

                        int max = ((StringSetting) setting).getOptions().length;
                        if (index < 0) {
                            index = max - 1;
                        } else if (index >= max) {
                            index = 0;
                        }

                        ((StringSetting) setting).setValue(index);
                    } else if (setting instanceof BooleanSetting) {
                        switchedSelector = false;

                        x = minX;
                        if (!(switchedBoolean = !switchedBoolean)) {
                            x = minX + boxWidth - 100.0F - itemStart;
                        } else {
                            if (i < settings.size() - 1 && settings.get(i + 1).getClass() == setting.getClass()) {
                                down = false;
                            }
                        }

                        if (mouseX >= x + itemStart + 8.0F && mouseX <= x + itemStart + 23.0F &&
                                mouseY >= startY + 1.0F && mouseY <= startY + 16.0F) {
                            ((BooleanSetting) setting).setValue(!((Boolean) setting.getValue()));
                        }
                    } else if (setting instanceof ColorSetting) {
                        switchedBoolean = switchedSelector = false;
                        ColorSetting colorSetting = (ColorSetting) setting;

                        if (mouseX >= minX + itemStart + 10.0F && mouseX <= minX + itemStart + 20.0F &&
                                mouseY >= startY + 4.0F && mouseY <= startY + 14.0F) {
                            colorSetting.setPicking(!colorSetting.isPicking());
                        }

                        if (!setting.getName().equals("Background")) {
                            float chromaX = minX + boxWidth - 100.0F - itemStart;

                            if (mouseX >= minX + itemStart + 80.0F && mouseX <= minX + itemStart + 90.0F &&
                                    mouseY >= startY + 4.0F && mouseY <= startY + 14.0F) {
                                colorSetting.setChroma(!colorSetting.isChroma());
                            }
                        }

                        if (colorSetting.isPicking()) {
                            /*itemStart -= 45.0F;
                            startY += 15.0F;

                            float horOffsetStart = minX + itemStart + COLOR_OFFSET - 45;
                            float horOffsetEnd = minX + itemStart + COLOR_OFFSET + 75;
                            float verOffsetStart = startY + COLOR_OFFSET - 95;
                            float verOffsetEnd = startY + COLOR_OFFSET + 20;

                            if (mouseX >= horOffsetStart && mouseX <= horOffsetEnd &&
                                    mouseY >= verOffsetStart && mouseY <= verOffsetEnd) {
                                ((ColorSetting) setting).setDraggingAll(true);
                            }

                            if (mouseX >= horOffsetStart + horOffsetEnd - horOffsetStart + 18 && mouseX <= horOffsetStart + horOffsetEnd - horOffsetStart + 28 &&
                                    mouseY > verOffsetStart && mouseY < verOffsetStart + verOffsetEnd - verOffsetStart) {
                                ((ColorSetting) setting).setDraggingAlpha(true);
                            }

                            if (mouseX >= horOffsetStart + horOffsetEnd - horOffsetStart + 4 && mouseX <= horOffsetStart + horOffsetEnd - horOffsetStart + 14 &&
                                    mouseY > verOffsetStart && mouseY < verOffsetStart + verOffsetEnd - verOffsetStart) {
                                ((ColorSetting) setting).setDraggingHue(true);
                            }

                            startY += 105.0F;*/

                            itemStart -= 45.0f;

                            float cMinX = minX + itemStart + 60.0f;
                            float cMaxX = minX + itemStart + 180.0f;
                            float cMinY = startY + 20.0f;
                            float cMaxY = startY + 140.0f;

                            float centerX = (cMinX + cMaxX) / 2.0f;
                            float centerY = (cMinY + cMaxY) / 2.0f;

                            if (mouseX > centerX - 40.0f && mouseX < centerX + 40.0f && mouseY > centerY - 12.0f && mouseY < centerY + 12.0f) {
                                colorSetting.setTypingColor(true);
                            } else {
                                colorSetting.setTypingColor(false);
                                colorSetting.setTypingIndex(0);
                            }

                            double distance = Math.hypot(mouseX - centerX, mouseY - centerY);

                            // 'click-radius' slightly bigger than the actual width of the line
                            if (distance > 42.0f && distance < 58.0f) {
                                colorSetting.setDraggingHue(true);
                            } else if (mouseY >= cMinY + 10 && mouseY <= cMaxY - 10) {
                                if (mouseX >= cMaxX + 5 && mouseX <= cMaxX + 15) {
                                    colorSetting.setDraggingSaturation(true);
                                } else if (mouseX >= cMaxX + 25 && mouseX <= cMaxX + 35) {
                                    colorSetting.setDraggingBrightness(true);
                                }
                            }

                            startY += 120.0f;
                        }
                    } else if (setting instanceof TextSetting) {
                        if (mouseX >= minX + itemStart && mouseX <= minX + boxWidth - 50.0F &&
                                mouseY >= startY && mouseY <= startY + 15.0F) {
                            ((TextSetting) setting).setEditing(true);
                        } else {
                            ((TextSetting) setting).setEditing(false);
                        }
                    }

                    if (down) {
                        startY += 25.0F;
                    }
                }
                break;
            }
            case MODS: {
                float boxWidth = 450.0F;
                float boxHeight = 210.0F;
                float minX = resolution.getScaledWidth() / 2 - boxWidth / 2;
                float minY = resolution.getScaledHeight() / 2 - boxHeight / 2;

                List<IModule> modules = new ArrayList<>(Client.getInstance().getModuleManager().getModules());
                modules.sort((m1, m2) -> m1.getName().compareToIgnoreCase(m2.getName()));

                int v = 0;
                int h = 0;
                for (IModule module : modules) {
                    float x = minX + 110.0F * h + 10.0F;
                    float y = minY + 50.0F * v + 10.0F;

                    if (mouseX >= x + 32.5F && mouseX <= x + 46.5F &&
                            mouseY >= y + 20.0F && mouseY <= y + 34.0F) {
                        module.setEnabled(!module.isEnabled());
                        return;
                    } else if (mouseX >= x + 57.5F && mouseX <= x + 71.5F &&
                            mouseY >= y + 20.0F && mouseY <= y + 34.0F) {
                        if (module.getSettingMap().size() > 1) {
                            this.menuType = EnumMenuType.MOD;
                            this.scrollAmount = 0;
                            this.editing = module;
                            this.switchTime = 0;
                        }
                        return;
                    }

                    if (++h == 4) {
                        h = 0;
                        v++;
                    }
                }
                break;
            }
        }
    }

    @Override
    public void onMouseEvent() {
        int scroll = Mouse.getEventDWheel();
        if (scroll == 0) {
            return;
        }

        List<Float> sizes = new LinkedList<>();

        switch (this.menuType) {
            case MODS:
                int mods = Client.getInstance().getModuleManager().getModules().size() % 4;

                for (int i = 0; i < mods; i++) {
                    sizes.add(50.0F * i + 10.0F);
                }
                break;
            case MOD:
                boolean switchBoolean = false;
                boolean switchString = false;

                List<ISetting> settings = new LinkedList<>();

                List<ISetting> sortedSettings = this.editing.getSortedSettings();
                for (int i = 0; i < sortedSettings.size(); i++) {
                    ISetting setting = sortedSettings.get(i);
                    if (setting instanceof BooleanSetting) {
                        switchString = false;
                        if ((switchBoolean = !switchBoolean) && i < settings.size() - 1 &&
                                settings.get(i + 1).getClass() == setting.getClass()) {
                            continue;
                        }
                    } else if (setting instanceof StringSetting) {
                        switchBoolean = false;
                        if ((switchString = !switchString) && i < settings.size() - 1 &&
                                settings.get(i + 1).getClass() == setting.getClass()) {
                            continue;
                        }
                    }

                    settings.add(setting);

                    if (setting instanceof ColorSetting && ((ColorSetting) setting).isPicking()) {
                        sizes.add(145.0F);
                    } else {
                        sizes.add(25.0F);
                    }
                }
                break;
        }

        if (sizes.size() > 0) {
            this.scroll(sizes, scroll);
        }
    }

    private void scroll(List<Float> sizes, int scroll) {
        int before = this.scrollAmount;

        this.scrollAmount += scroll;
        if (this.scrollAmount > 0) {
            this.scrollAmount = 0;
        }

        ScaledResolution resolution = new ScaledResolution(Minecraft.getMinecraft());

        float boxHeight = 50.0F;
        float minY = resolution.getScaledHeight() / 2 - boxHeight / 2 + 40.0F;
        float maxY = minY + boxHeight - 20.0F;

        float translate = this.scrollAmount / 10.0F;
        float startY = 60.0F + translate;

        boolean move = false;
        for (Float size : sizes) {
            if (startY + size >= maxY) {
                move = true;
                break;
            }
            startY += size;
        }

        if (!move) {
            this.scrollAmount = before;
        }
    }

    @SuppressWarnings("ConstantConditions")
    private void renderModMenu(ScaledResolution resolution, int mouseX, int mouseY) {
        if (this.editing == null) {
            this.menuType = EnumMenuType.MODS;
            this.scrollAmount = 0;
            return;
        }

        float boxWidth = 450.0F;
        float boxHeight = 200.0F;
        float minX = resolution.getScaledWidth() / 2 - boxWidth / 2;
        float minY = resolution.getScaledHeight() / 2 - boxHeight / 2;

        RenderUtil.drawRoundedRect(minX, minY, minX + boxWidth, minY + boxHeight, 5.0F, 0x77161313);

        RenderUtil.drawCenteredString(Client.getInstance().getRegularFontRenderer(),
                this.editing.getName(), minX + boxWidth / 2,
                minY + 20.0F, 0xFFFFFFFF, false);

        float nameWidth = Client.getInstance().getRegularFontRenderer()
                .getStringWidth(this.editing.getName());

        GL11.glPushMatrix();
        {
            float alpha = 0.7F;

            float x = minX + boxWidth / 2 - nameWidth / 2 - 25.0F;
            float y = minY + 12.0F;
            if (mouseX >= x && mouseX <= x + 16.0F && mouseY >= y && mouseY <= y + 16.0F) {
                alpha = 1.0F;
            }

            GL11.glColor4f(1.0F, 1.0F, 1.0F, alpha);
            RenderUtil.drawSquareTexture(ARROW_LEFT, 8.0F,
                    minX + boxWidth / 2 - nameWidth / 2 - 25.0F, minY + 12.0F);
        }
        GL11.glPopMatrix();

        RenderUtil.startScissorBox(minY + 40.0F, minY + boxHeight - 20.0F, minX, minX + boxWidth);

        List<ISetting> settings = this.editing.getSortedSettings();

        GL11.glPushMatrix();
        float currentMaxY = minY + (settings.size() * 50.0F) + ((settings.size() - 1) * 10.0F);
        if (currentMaxY > minY + boxHeight) {
            float translate = this.scrollAmount / 10.0F;
            minY += translate;
        } else {
            this.scrollAmount = 0;
        }

        minX += 25.0F;

        boolean switchedSelector = false;
        boolean switchedBoolean = false;

        float startY = minY + 40.0F;
        for (int i = 0; i < settings.size(); i++) {
            ISetting setting = settings.get(i);
            if (setting.getName().equals("Enabled")) {
                continue;
            }

            boolean down = true;

            GL11.glPushMatrix();

            float itemStart = 120.0F;

            if (setting instanceof FloatSetting) {
                switchedBoolean = switchedSelector = false;

                RenderUtil.drawString(Client.getInstance().getSmallFontRenderer(), setting.getName(),
                        minX, startY + 6.0F, 0xCCFFFFFF, false);

                float width = (minX + boxWidth - 50.0F) - (minX + itemStart);

                FloatSetting floatSetting = (FloatSetting) setting;

                if (setting == this.draggingSetting) {
                    float percent = (mouseX - (minX + itemStart)) / width * floatSetting.getMax();
                    floatSetting.setValue(percent);
                }

                String value = String.format("%.1f", floatSetting.getValue());
                RenderUtil.drawString(Client.getInstance().getSmallFontRenderer(), value,
                        minX + itemStart - 5.0F - Client.getInstance().getSmallFontRenderer().getStringWidth(value),
                        startY + 6.0F, 0xFFFFFFFF, false);

                float percent = width / 100.0F * (floatSetting.getValue() / floatSetting.getMax() * 100.0F);

                RenderUtil.drawRect(minX + itemStart, startY + 4.0F,
                        minX + boxWidth - 50.0F, startY + 12.5F, 0xA9330000);

                RenderUtil.drawRect(minX + itemStart, startY + 4.0F,
                        minX + itemStart + percent, startY + 12.5F, 0xA9EE3333);

            } else if (setting instanceof LabelSetting) {
                switchedBoolean = switchedSelector = false;

                RenderUtil.drawString(Client.getInstance().getSmallFontRenderer(), setting.getName(),
                        minX - 5.0F, startY + 6.0F, 0xCCFFFFFF, false);

                RenderUtil.drawRect(minX - 5.0F, startY + 14.5F, minX + boxWidth - 45.0F,
                        startY + 15.5F, 0x88CBCBCB);
            } else if (setting instanceof StringSetting) {
                switchedBoolean = false;

                float x = minX;
                if (!(switchedSelector = !switchedSelector)) {
                    x = minX + boxWidth - 100.0F - itemStart;
                } else {
                    if (i < settings.size() - 1 && settings.get(i + 1).getClass() == setting.getClass()) {
                        down = false;
                    }
                }

                RenderUtil.drawString(Client.getInstance().getSmallFontRenderer(), setting.getName(),
                        x, startY + 6.0F, 0xCCFFFFFF, false);

                RenderUtil.drawCenteredString(Client.getInstance().getSmallFontRenderer(),
                        (String) setting.getValue(), x + itemStart + 15.0F,
                        startY + 6.0F + Client.getInstance().getSmallFontRenderer().getHeight() / 2,
                        0xCCFFFFFF, false);

                GL11.glPushMatrix();

                if (mouseX >= x + itemStart + 45.0F && mouseX <= x + itemStart + 55.0F &&
                        mouseY >= startY + 3.0F && mouseY <= startY + 13.0F) {
                    GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
                } else {
                    GL11.glColor4f(1.0F, 1.0F, 1.0F, 0.6F);
                }

                RenderUtil.drawSquareTexture(ARROW_RIGHT, 5.0F, x + itemStart + 45.0F, startY + 3.5F);

                if (mouseX >= x + itemStart - 25.0F && mouseX <= x + itemStart - 15.0F &&
                        mouseY >= startY + 3.0F && mouseY <= startY + 13.0F) {
                    GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
                } else {
                    GL11.glColor4f(1.0F, 1.0F, 1.0F, 0.6F);
                }

                RenderUtil.drawSquareTexture(ARROW_LEFT, 5.0F, x + itemStart - 25.0F, startY + 3.5F);

                GL11.glPopMatrix();

            } else if (setting instanceof BooleanSetting) {
                switchedSelector = false;

                float x = minX;
                if (!(switchedBoolean = !switchedBoolean)) {
                    x = minX + boxWidth - 100.0F - itemStart;
                } else {
                    if (i < settings.size() - 1 && settings.get(i + 1).getClass() == setting.getClass()) {
                        down = false;
                    }
                }

                RenderUtil.drawString(Client.getInstance().getSmallFontRenderer(), setting.getName(),
                        x, startY + 6.0F, 0xCCFFFFFF, false);

                float alpha = 0.6F;
                if (mouseX >= x + itemStart + 8.0F && mouseX <= x + itemStart + 23.0F &&
                        mouseY >= startY + 1.0F && mouseY <= startY + 16.0F) {
                    alpha = 0.8F;
                }

                GL11.glEnable(GL11.GL_BLEND);
                if ((Boolean) setting.getValue()) {
                    GL11.glColor4f(0.0F, 1.0F, 0.0F, alpha);
                    RenderUtil.drawSquareTexture(TOGGLE_ON, 7.5F, x + itemStart + 8.0F, startY + 1.0F);
                } else {
                    GL11.glColor4f(1.0F, 0.0F, 0.0F, alpha);
                    RenderUtil.drawSquareTexture(TOGGLE_OFF, 7.5F, x + itemStart + 8.0F, startY + 1.0F);
                }
                GL11.glDisable(GL11.GL_BLEND);
            } else if (setting instanceof ColorSetting) {
                switchedBoolean = switchedSelector = false;

                RenderUtil.drawString(Client.getInstance().getSmallFontRenderer(), setting.getName(),
                        minX, startY + 7.0F, 0xCCFFFFFF, false);

                RenderUtil.drawBorderedRect(minX + itemStart + 10.0F, startY + 4.0F,
                        minX + itemStart + 20.0F, startY + 14.0F, 1.0F,
                        0xFFFFFFFF, (int) setting.getValue());

                if (!setting.getName().equals("Background")) {
                    //float chromaX = minX + boxWidth - 100.0F - itemStart;

                    RenderUtil.drawString(Client.getInstance().getSmallFontRenderer(), "Chroma",
                            minX + itemStart + 95.0f, startY + 7.0f, 0xCCFFFFFF, false);

                    RenderUtil.drawBorderedRect(minX + itemStart + 80.0f, startY + 4.0f,
                            minX + itemStart + 90.0f, startY + 14.0f, 1.0f,
                            0xFFFFFFFF, ((ColorSetting) setting).isChroma() ? 0xFF000000 : 0xFFFFFFFF);

//					GL11.glPushMatrix();
//					{
//						GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
//						RenderUtil.drawCircle(chromaX + itemStart + 15.0F, startY + 9.0F, 5.0F);
//
//						if (((ColorSetting) setting).isChroma()) {
//							GL11.glColor4f(0.0F, 0.0F, 0.0F, 1.0F);
//							RenderUtil.drawCircle(chromaX + itemStart + 15.0F, startY + 9.0F, 4.0F);
//						}
//					}
//					GL11.glPopMatrix();
                }

                ColorSetting colorSetting = (ColorSetting) setting;

                int selectedColor = colorSetting.getValue();
                // don't display the alpha
                String hexString = String.format("#%06X", selectedColor & 0x00FFFFFF);

                RenderUtil.drawString(Client.getInstance().getSmallFontRenderer(),
                        hexString,
                        minX + itemStart + 25.0F, startY + 7.0F, 0xCCFFFFFF, false);

                if (colorSetting.isPicking()) {
                    itemStart -= 45.0f;

                    float cMinX = minX + itemStart + 60.0f;
                    float cMaxX = minX + itemStart + 180.0f;
                    float cMinY = startY + 20.0f;
                    float cMaxY = startY + 140.0f;

                    float centerX = (cMinX + cMaxX) / 2.0f;
                    float centerY = (cMinY + cMaxY) / 2.0f;
                    float radius = 50.0f;

                    int currentColor = colorSetting.getValue();
                    float[] hsb = Color.RGBtoHSB(currentColor >> 16 & 255, currentColor >> 8 & 255, currentColor & 255, new float[3]);

                    if (colorSetting.isDraggingHue()) {
                        double dx = mouseX - centerX;
                        double dy = mouseY - centerY;
                        double selectedAngle = Math.atan2(dy, dx);

                        if (selectedAngle < 0.0)
                            selectedAngle += PI2;
                        else if (selectedAngle > PI2)
                            selectedAngle -= PI2;

                        colorSetting.setValue(Color.HSBtoRGB((float) (selectedAngle / PI2), hsb[1], hsb[2]));
                    } else if (colorSetting.isDraggingSaturation()) {
                        hsb[1] = 1.0f - Math.min(1.0f, Math.max(0.0f, (cMaxY - 10 - mouseY) / 100.0f));
                        colorSetting.setValue(Color.HSBtoRGB(hsb[0], hsb[1], hsb[2]));
                    } else if (colorSetting.isDraggingBrightness()) {
                        hsb[2] = 1.0f - Math.min(1.0f, Math.max(0.0f, (cMaxY - 10 - mouseY) / 100.0f));
                        colorSetting.setValue(Color.HSBtoRGB(hsb[0], hsb[1], hsb[2]));
                    }

                    // this stuff is edited from RenderUtil#drawCirle
                    GL11.glEnable(GL11.GL_BLEND);
                    GL11.glDisable(GL11.GL_TEXTURE_2D);
                    GL11.glEnable(GL11.GL_LINE_SMOOTH);
                    GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
                    GL11.glLineWidth(10.0f);

                    Tessellator tes = Tessellator.instance;

                    // draw circle
                    tes.startDrawing(GL11.GL_LINE_LOOP);

                    for (double angle = 0.0; angle < PI2; angle += 0.02f) {
                        tes.setColorOpaque_I(Color.HSBtoRGB((float) (angle / PI2), hsb[1], hsb[2]));
                        tes.addVertex(centerX + Math.cos(angle) * radius, centerY + Math.sin(angle) * radius, 0.0F);
                    }

                    tes.draw();

                    double selectedAngle = hsb[0] * PI2;

                    // draw selector line
                    tes.startDrawing(GL11.GL_LINES);
                    tes.setColorRGBA(25, 25, 25, 255);
                    tes.addVertex(centerX + Math.cos(selectedAngle) * (radius - 4.0f), centerY + Math.sin(selectedAngle) * (radius - 4.0f), 0.0);
                    tes.addVertex(centerX + Math.cos(selectedAngle) * (radius + 4.0f), centerY + Math.sin(selectedAngle) * (radius + 4.0f), 0.0);
                    tes.draw();

                    GL11.glLineWidth(8.0f);

                    tes.startDrawing(GL11.GL_LINES);
                    tes.setColorRGBA(200, 200, 200, 255);
                    tes.addVertex(centerX + Math.cos(selectedAngle) * (radius - 3.5f), centerY + Math.sin(selectedAngle) * (radius - 3.5f), 0.0);
                    tes.addVertex(centerX + Math.cos(selectedAngle) * (radius + 3.5f), centerY + Math.sin(selectedAngle) * (radius + 3.5f), 0.0);
                    tes.draw();

                    GL11.glLineWidth(0.5f);

                    for (float sliderY = cMinY + 10.0f; sliderY < cMaxY - 10.0f; sliderY += 0.25f) {
                        float progress = (cMaxY - 10 - sliderY) / 100.0f;

                        tes.startDrawing(GL11.GL_LINES);
                        tes.setColorOpaque_I(Color.HSBtoRGB(hsb[0], progress, 1.0f));
                        tes.addVertex(cMaxX + 5.0, sliderY, 0.0);
                        tes.addVertex(cMaxX + 15.0, sliderY, 0.0);
                        tes.draw();

                        tes.startDrawing(GL11.GL_LINES);
                        tes.setColorOpaque_I(Color.HSBtoRGB(hsb[0], 1.0f, progress));
                        tes.addVertex(cMaxX + 25.0, sliderY, 0.0);
                        tes.addVertex(cMaxX + 35.0, sliderY, 0.0);
                        tes.draw();
                    }

                    RenderUtil.drawHollowRect(cMaxX + 4, cMinY + 9, cMaxX + 16, cMaxY - 9, 1.0f, 0xFF000000);
                    RenderUtil.drawHollowRect(cMaxX + 24, cMinY + 9, cMaxX + 36, cMaxY - 9, 1.0f, 0xFF000000);

                    float saturationY = hsb[1] * (cMaxY - cMinY - 20) + cMinY + 10;
                    float brightnessY = hsb[2] * (cMaxY - cMinY - 20) + cMinY + 10;

                    RenderUtil.drawBorderedRect(cMaxX + 3, saturationY - 2, cMaxX + 17, saturationY + 2, 1.0f, new Color(0xFF191919).getRGB(), 0xFFC8C8C8);
                    RenderUtil.drawBorderedRect(cMaxX + 23, brightnessY - 2, cMaxX + 37, brightnessY + 2, 1.0f, new Color(0xFF191919).getRGB(), 0xFFC8C8C8);

                    GL11.glEnable(GL11.GL_TEXTURE_2D);
                    GL11.glDisable(GL11.GL_BLEND);
                    GL11.glDisable(GL11.GL_LINE_SMOOTH);

                    if (colorSetting.isTypingColor() || mouseX > centerX - 40.0f && mouseX < centerX + 40.0f && mouseY > centerY - 12.0f && mouseY < centerY + 12.0f)
                        RenderUtil.drawRect(centerX - 40.0f, centerY - 12.0f, centerX + 40.0f, centerY + 12.0f, 0xFF111111);

                    if (colorSetting.isTypingColor()) {
                        String displayString = new String(colorSetting.getColorChars(), 0, colorSetting.getTypingIndex());

                        if (System.currentTimeMillis() % 1000L < 500)
                            displayString += '_';

                        RenderUtil.drawString(Client.getInstance().getMediumBoldFontRenderer(), displayString, centerX - 37.0f, centerY - 6.0f, 0xFFFFFFFF, true);
                    } else {
                        RenderUtil.drawString(Client.getInstance().getLargeBoldFontRenderer(), hexString, centerX - 37.0f, centerY - 9.0f, selectedColor, true);
                    }

                    startY += 120.0f;
                }

                /*if (((ColorSetting) setting).isPicking()) {
                    itemStart -= 45.0F;
                    startY += 15.0F;

                    ColorSetting colorSetting = (ColorSetting) setting;

                    float horOffsetStart = minX + itemStart + 56.0F;
                    float horOffsetEnd = minX + itemStart + 176;
                    float verOffsetStart = startY + 5;
                    float verOffsetEnd = startY + 119;
                    float offsetWidth = horOffsetEnd - horOffsetStart;
                    float offsetHeight = verOffsetEnd - verOffsetStart;

                    RenderUtil.drawRect(minX + itemStart + 55, startY + 4,
                            minX + itemStart + 177, startY + 120, 0xCF000000);

                    Tessellator tess = Tessellator.instance;
                    GL11.glDisable(GL11.GL_TEXTURE_2D);
                    tess.startDrawingQuads();
                    GL11.glColor4f(1, 1, 1, 1);
                    tess.addVertex(horOffsetStart, verOffsetEnd, 0.0D);
                    tess.addVertex(horOffsetEnd, verOffsetEnd, 0.0D);
                    tess.addVertex(horOffsetEnd, verOffsetStart, 0.0D);
                    tess.addVertex(horOffsetStart, verOffsetStart, 0.0D);
                    tess.draw();


                    int[] coords = colorSetting.getCoords();

                    int[] colorPos = null;
                    for (int x = 0; x < offsetWidth; ++x) {
                        for (int y = 0; y < offsetHeight; ++y) {
                            int alpha = (int) colorSetting.getAlpha() << 24;
                            int color = Color.HSBtoRGB(colorSetting.getHue(), x / offsetWidth, 1 - (y / offsetHeight)) | alpha;

                            boolean mouseXOver = mouseX >= (horOffsetStart + x) && mouseX <= (horOffsetStart + x + 1);
                            boolean mouseYOver = mouseY <= (verOffsetStart + y + 1) && mouseY > (verOffsetStart + y);
                            boolean overPosition = mouseXOver && mouseYOver;
                            boolean xTooSmall = x == 0 && mouseX < (horOffsetStart) && mouseYOver;
                            boolean yTooSmall = y == 0 && mouseY < (verOffsetStart) && mouseXOver;
                            boolean xTooBig = x == offsetWidth - 1 && mouseX > (horOffsetStart + offsetWidth) &&
                                    mouseYOver;
                            boolean yTooBig = y == offsetHeight - 1 && mouseY > (verOffsetStart + offsetHeight) &&
                                    mouseXOver;

                            if (colorSetting.isDraggingAll() &&
                                    (overPosition || xTooSmall || yTooSmall || xTooBig || yTooBig)) {
                                colorSetting.setValue(color);
                                colorSetting.setCoords(new int[]{x, y});
                            }

                            if (coords != null) {
                                colorPos = coords;
                            } else if (color == (Integer) setting.getValue()) {
                                colorPos = new int[]{x, y};
                            }

                            float[] rgb = new float[3];

                            for (int nig = 0; nig < 3; nig++) {
                                rgb[2 - nig] = (color >> (8 * nig) & 255) / 255.0f;
                            }

                            tess.startDrawingQuads();
                            GL11.glColor4f(rgb[0], rgb[1], rgb[2], 1.0F);
                            {
                                tess.addVertex(horOffsetStart + x, verOffsetStart + y + 1, 0.0D);
                                tess.addVertex(horOffsetStart + x + 1, verOffsetStart + y + 1, 0.0D);
                                tess.addVertex(horOffsetStart + x + 1, verOffsetStart + y, 0.0D);
                                tess.addVertex(horOffsetStart + x, verOffsetStart + y, 0.0D);
                            }
                            tess.draw();
                        }
                    }

                    if (colorPos != null) {
                        GL11.glPushMatrix();
                        {
                            GL11.glColor4f(0.0F, 0.0F, 0.0F, 0.75F);
                            RenderUtil.drawCircle(horOffsetStart + colorPos[0] + 1.115F,
                                    verOffsetStart + colorPos[1] + 1.115F, 4F);

                            GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
                            RenderUtil.drawCircle(horOffsetStart + colorPos[0] + 1.115F,
                                    verOffsetStart + colorPos[1] + 1.115F, 2.7F);
                        }
                        GL11.glPopMatrix();
                    }

                    RenderUtil.drawRect(horOffsetStart + offsetWidth + 4, verOffsetStart - 1,
                            horOffsetStart + offsetWidth + 14, verOffsetStart + 1 + offsetHeight, 0xCF000000);
                    for (int j = 0; j < offsetHeight; ++j) {
                        int RGB = Color.HSBtoRGB((float) j / offsetHeight, 1F, 1F);
                        RenderUtil.drawRect(horOffsetStart + offsetWidth + 5, verOffsetStart + j,
                                horOffsetStart + offsetWidth + 13, verOffsetStart + j + 1, RGB);

                        if (colorSetting.isDraggingHue() &&
                                mouseY >= (verOffsetStart + j) && mouseY <= (verOffsetStart + j + 1)) {
                            int c = colorSetting.getValue();
                            float[] hsb = Color.RGBtoHSB(c >> 16 & 255, c >> 8 & 255, c & 255, null);
                            colorSetting.setValue(Color.HSBtoRGB(colorSetting.getHue(), hsb[1], hsb[2]));
                            colorSetting.setHue(j / offsetHeight);
                        }
                    }

                    float startHueY = -1 + (offsetHeight * ((ColorSetting) setting).getHue());
                    RenderUtil.drawRect(horOffsetStart + offsetWidth + 4, verOffsetStart + startHueY,
                            horOffsetStart + offsetWidth + 14, verOffsetStart + startHueY + 3, 0xCF000000);
                    RenderUtil.drawRect(horOffsetStart + offsetWidth + 4, verOffsetStart + startHueY + 1,
                            horOffsetStart + offsetWidth + 14, verOffsetStart + startHueY + 2, 0xCFFFFFFF);

                    RenderUtil.drawRect(horOffsetStart + offsetWidth + 18, verOffsetStart - 1,
                            horOffsetStart + offsetWidth + 28, verOffsetStart + 1 + offsetHeight, 0xCF000000);

                    boolean left = true;
                    for (int j = 2; j < offsetHeight; j += 4) {
                        if (!left) {
                            RenderUtil.drawRect(horOffsetStart + offsetWidth + 19, verOffsetStart + j,
                                    horOffsetStart + offsetWidth + 23, verOffsetStart + j + 4, 0xFFFFFFFF);
                            RenderUtil.drawRect(horOffsetStart + offsetWidth + 23, verOffsetStart + j,
                                    horOffsetStart + offsetWidth + 27, verOffsetStart + j + 4, 0xFF909090);

                            if (j < offsetHeight - 4) {
                                RenderUtil.drawRect(horOffsetStart + offsetWidth + 19, verOffsetStart + j + 4,
                                        horOffsetStart + offsetWidth + 23, verOffsetStart + j + 8, 0xFF909090);
                                RenderUtil.drawRect(horOffsetStart + offsetWidth + 23, verOffsetStart + j + 4,
                                        horOffsetStart + offsetWidth + 27, verOffsetStart + j + 8, 0xFFFFFFFF);
                            }
                        }
                        left = !left;
                    }

                    for (int j = 0; j < offsetHeight; ++j) {
                        int c = (int) setting.getValue();
                        int rgb = new Color((c >> 16 & 255), (c >> 8 & 255), (c & 255),
                                Math.round(255 - ((j / offsetHeight)) * 255)).getRGB();

                        if (colorSetting.isDraggingAlpha() && mouseY >= verOffsetStart + j && mouseY <= verOffsetStart + j + 1) {
                            colorSetting.setAlpha(j / offsetHeight);
                            colorSetting.setValue(rgb);
                        }

                        RenderUtil.drawRect(horOffsetStart + offsetWidth + 19, verOffsetStart + j,
                                horOffsetStart + offsetWidth + 27, verOffsetStart + j + 1, rgb);
                    }

                    float startAlphaY = -1 + (offsetHeight * ((ColorSetting) setting).getAlpha());
                    RenderUtil.drawRect(horOffsetStart + offsetWidth + 18, verOffsetStart + startAlphaY,
                            horOffsetStart + offsetWidth + 28, verOffsetStart + startAlphaY + 3, 0xCF000000);
                    RenderUtil.drawRect(horOffsetStart + offsetWidth + 18, verOffsetStart + startAlphaY + 1,
                            horOffsetStart + offsetWidth + 28, verOffsetStart + startAlphaY + 2, 0xCFFFFFFF);

                    startY += 105.0F;
                }*/
            } else if (setting instanceof TextSetting) {
                switchedBoolean = switchedSelector = false;

                RenderUtil.drawString(Client.getInstance().getSmallFontRenderer(), setting.getName(),
                        minX, startY + 6.0F, 0xCCFFFFFF, false);

                RenderUtil.drawRect(minX + itemStart, startY, minX + boxWidth - 50.0F, startY + 15.0F, 0x99333333);

                String value = (String) setting.getValue();
                if (((TextSetting) setting).isEditing()) {
                    if (((TextSetting) setting).getValueFlipTime() + 250L < System.currentTimeMillis()) {
                        ((TextSetting) setting).setValued(!((TextSetting) setting).isValued());
                        ((TextSetting) setting).setValueFlipTime(System.currentTimeMillis());
                    }

                    if (((TextSetting) setting).isValued()) {
                        value += "_";
                    }
                }

                RenderUtil.drawString(Client.getInstance().getRegularFontRenderer(),
                        value, minX + itemStart + 2.5F, startY + 4.0F, 0xC9FFFFFF, false);
            }

            GL11.glPopMatrix();

            if (down) {
                startY += 25.0F;
            }
        }
        GL11.glPopMatrix();

        RenderUtil.endScissorBox();
    }

    private void renderMainMenu(ScaledResolution resolution, int mouseX, int mouseY) {
        float boxWidth = 450.0F;
        float boxHeight = 210.0F;
        //		float boxHeight = 160.0F;
        float minX = resolution.getScaledWidth() / 2 - boxWidth / 2;
        float minY = resolution.getScaledHeight() / 2 - boxHeight / 2;
        RenderUtil.drawRoundedRect(minX, minY, minX + boxWidth, minY + boxHeight, 5.0F, 0x77161313);

        RenderUtil.startScissorBox(minY, minY + boxHeight, minX, minX + boxWidth);

        GL11.glPushMatrix();

        List<IModule> modules = new ArrayList<>(Client.getInstance().getModuleManager().getModules());
        modules.sort((m1, m2) -> m1.getName().compareToIgnoreCase(m2.getName()));

        int v = 0;
        int h = 0;

        for (IModule module : modules) {
            float x = minX + 110.0F * h + 10.0F;
            float y = minY + 50.0F * v + 10.0F;

            RenderUtil.drawBorderedRect(x, y, x + 100.0F, y + 40.0F, 1.0F,
                    new Color(13, 13, 13, 180).getRGB(), new Color(20, 20, 20, 140).getRGB());

            RenderUtil.drawCenteredString(Client.getInstance().getRegularFontRenderer(),
                    module.getName(), x + 50.0F, y + 10.0F, 0xFFFFFFFF);

            float alpha = 0.6F;

            if (mouseX >= x + 32.5F && mouseX <= x + 32.5F + 15.0F &&
                    mouseY >= y + 20.0F && mouseY <= y + 20.0F + 15.0F) {
                alpha = 0.8F;
            }

            GL11.glPushMatrix();
            GL11.glEnable(GL11.GL_BLEND);
            if (module.isEnabled()) {
                GL11.glColor4f(0.0F, 1.0F, 0.0F, alpha);
                RenderUtil.drawSquareTexture(TOGGLE_ON, 7.5F, x + 32.5F, y + 20.0F);
            } else {
                GL11.glColor4f(1.0F, 0.0F, 0.0F, alpha);
                RenderUtil.drawSquareTexture(TOGGLE_OFF, 7.5F, x + 32.5F, y + 20.0F);
            }

            if (mouseX >= x + 57.5F && mouseX <= x + 71.5F &&
                    mouseY >= y + 20.0F && mouseY <= y + 34.0F) {
                GL11.glColor4f(1.0F, 1.0F, 1.0F, 0.9F);
            } else {
                GL11.glColor4f(1.0F, 1.0F, 1.0F, 0.6F);
            }

            RenderUtil.drawSquareTexture(SETTINGS, 7.5F, x + 57.5F, y + 20.0F);

            GL11.glDisable(GL11.GL_BLEND);
            GL11.glPopMatrix();

            if (++h == 4) {
                h = 0;
                v++;
            }
        }

        GL11.glPopMatrix();

        RenderUtil.endScissorBox();
    }

    @Override
    public void draw(int mouseX, int mouseY) {
        Minecraft mc = Minecraft.getMinecraft();
        ScaledResolution resolution = new ScaledResolution(mc);

        GL11.glPushMatrix();

        boolean wasHiding = this.hiding;

        this.hiding = mouseY <= 40.0F;

        if (this.hiding) {
            if (!wasHiding) {
                this.hidingTicks = 0;
            }

            GL11.glTranslatef(0.0F, -this.hidingTicks * 2.0F, 0.0F);
        }

        RenderUtil.drawRect(0.0F, 0.0F, resolution.getScaledWidth(), 40.0F, new Color(25, 25, 25, 125).getRGB());
        RenderUtil.drawString(Client.getInstance().getLargeBoldFontRenderer(),
                "Zonix Client", 10.0F, 12.0F, 0xFFFFFFFF);

        RenderUtil.drawCenteredString(Client.getInstance().getMediumBoldFontRenderer(), "Mod Settings",
                resolution.getScaledWidth() / 2, 20, 0xFFF7FFFF);

        GL11.glPopMatrix();

        GL11.glPushMatrix();
        {
            float boxWidth = 450.0F;
            float boxHeight = 220.0F;
            float minX = resolution.getScaledWidth() / 2 - boxWidth / 2;
            float minY = resolution.getScaledHeight() / 2 - boxHeight / 2;

            GL11.glPushMatrix();
            {
                RenderUtil.startScissorBox(minY, minY + boxHeight, minX, minX + boxWidth);
                GL11.glTranslatef(-this.switchTime - 25.0F, 0.0F, 0.0F);
                this.renderMainMenu(resolution, mouseX, mouseY);
                RenderUtil.endScissorBox();
            }
            GL11.glPopMatrix();

            GL11.glPushMatrix();
            {
                RenderUtil.startScissorBox(minY, minY + boxHeight, minX, minX + boxWidth);
                GL11.glTranslatef(450 - this.switchTime + 25.0F, 0.0F, 0.0F);
                this.renderModMenu(resolution, mouseX, mouseY);
                RenderUtil.endScissorBox();
            }
            GL11.glPopMatrix();
        }
        GL11.glPopMatrix();
    }

    public enum EnumMenuType {
        MOD,
        MODS
    }

}
