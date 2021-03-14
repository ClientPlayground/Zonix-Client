package us.zonix.client.setting.impl;

import lombok.Getter;
import lombok.Setter;
import us.zonix.client.setting.ISetting;

import java.awt.Color;

@Getter
public final class ColorSetting implements ISetting<Integer> {

    private final String name;

    private final Integer defaultValue;

    @Setter
    private int[] coords;
    private char[] colorChars = new char[6];
    @Setter
    private int typingIndex;

    @Setter
    private boolean draggingHue;
    @Setter
    private boolean draggingSaturation;
    @Setter
    private boolean draggingBrightness;
    @Setter
    private boolean picking;
    @Setter
    private boolean typingColor;
    @Setter
    private boolean chroma;

    @Setter
    private float brightness;
    @Setter
    private float saturation;
    @Setter
    private float alpha;
    @Setter
    private float hue;

    @Setter
    private int state;
    private int value;

    public ColorSetting(String name, int defaultValue) {
        this.name = name;

        this.defaultValue = defaultValue;
        this.value = this.defaultValue;

        Color color = new Color(this.defaultValue);
        this.alpha = color.getAlpha();

        float[] floats = Color.RGBtoHSB(color.getRed(), color.getGreen(), color.getBlue(), null);

        this.saturation = floats[1];
        this.brightness = floats[2];
        this.hue = floats[0];

        if (this.name.equals("Background")) {
            this.saturation = 1.0F;
            this.state = 2;
        }
    }

    @Override
    public Integer getValue() {
        int color = this.value;
        if (this.name.equals("Background")) {
            return (color & 0x00FFFFFF) | 0x6F000000;
        }

        if (this.chroma) {
            return value = Color.HSBtoRGB((float) (System.currentTimeMillis() % 1000L) / 1000.0F, 0.8F, 0.8F);
        }

        return color;
    }

    @Override
    public void setValue(Integer integer) {
        this.value = integer;
    }

}
