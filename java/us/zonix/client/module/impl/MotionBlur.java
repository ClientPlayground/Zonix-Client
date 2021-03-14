package us.zonix.client.module.impl;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.EntityRenderer;
import net.minecraft.client.resources.IResource;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.client.resources.data.IMetadataSection;
import net.minecraft.client.shader.ShaderGroup;
import net.minecraft.util.ResourceLocation;
import org.apache.commons.io.IOUtils;
import us.zonix.client.module.modules.AbstractModule;
import us.zonix.client.setting.impl.FloatSetting;
import us.zonix.client.setting.impl.LabelSetting;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Locale;
import java.util.Set;

public class MotionBlur
        extends AbstractModule {
    public static FloatSetting BLUR_AMOUNT = new FloatSetting("Blur Amount", Float.valueOf(0.1f), Float.valueOf(10.0f), Float.valueOf(1.0f));

    public MotionBlur() {
        super("Motion Blur");
        this.addSetting(new LabelSetting("General Settings"));
        this.addSetting(BLUR_AMOUNT);
    }

    @Override
    public void setEnabled(boolean enabled) {
        super.setEnabled(enabled);
        Minecraft mc = Minecraft.getMinecraft();
        EntityRenderer entityRenderer = mc.entityRenderer;
        try {
            if (entityRenderer.theShaderGroup != null) {
                entityRenderer.theShaderGroup.deleteShaderGroup();
            }
            if (enabled) {
                entityRenderer.theShaderGroup = new ShaderGroup(mc.getTextureManager(), mc.getResourceManager(), mc.getFramebuffer(), new ResourceLocation("motionblur", "motionblur"));
                entityRenderer.theShaderGroup.createBindFramebuffers(mc.displayWidth, mc.displayHeight);
            }
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static class MotionBlurResourceManager
            implements IResourceManager {
        @Override
        public Set getResourceDomains() {
            return null;
        }

        @Override
        public IResource getResource(ResourceLocation p_110536_1_) throws IOException {
            return new MotionBlurResource();
        }

        @Override
        public List getAllResources(ResourceLocation p_135056_1_) throws IOException {
            return null;
        }
    }

    public static class MotionBlurResource
            implements IResource {
        private static final String JSON = "{\"targets\":[\"swap\",\"previous\"],\"passes\":[{\"name\":\"phosphor\",\"intarget\":\"minecraft:main\",\"outtarget\":\"swap\",\"auxtargets\":[{\"name\":\"PrevSampler\",\"id\":\"previous\"}],\"uniforms\":[{\"name\":\"Phosphor\",\"values\":[%.2f, %.2f, %.2f]}]},{\"name\":\"blit\",\"intarget\":\"swap\",\"outtarget\":\"previous\"},{\"name\":\"blit\",\"intarget\":\"swap\",\"outtarget\":\"minecraft:main\"}]}";

        @Override
        public InputStream getInputStream() {
            double amount = 0.7 + (double)BLUR_AMOUNT.getValue().floatValue() / 100.0 * 3.0 - 0.01;
            return IOUtils.toInputStream((String)String.format(Locale.ENGLISH, JSON, amount, amount, amount));
        }

        @Override
        public boolean hasMetadata() {
            return false;
        }

        @Override
        public IMetadataSection getMetadata(String p_110526_1_) {
            return null;
        }
    }

}

