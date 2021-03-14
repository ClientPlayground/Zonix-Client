package us.zonix.client.module;

import us.zonix.client.module.impl.*;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class ModuleManager {

    private final Map<Class<? extends IModule>, IModule> moduleClassMap = new HashMap<>();
    private final Map<String, IModule> moduleNameMap = new HashMap<>();

    public ModuleManager() {
        this.register(new ArmorStatus());
        this.register(new ComboDisplay());
        this.register(new Coordinates());
        this.register(new CPS());
        this.register(new DirectionHUD());
        this.register(new FPS());
        this.register(new Keystrokes());
        this.register(new FPSBoost());
        this.register(new PotionCounter());
        this.register(new PotionEffects());
        this.register(new ReachDisplay());
        this.register(new Scoreboard());
        this.register(new TimeChanger());
        this.register(new ToggleSneak());
        this.register(new MiniMap());
    }

    public List<IModule> getEnabledModules() {
        List<IModule> modules = new ArrayList<>();
        for (IModule module : this.moduleNameMap.values()) {
            if (module.isEnabled()) {
                modules.add(module);
            }
        }
        return modules;
    }

    public Collection<IModule> getModules() {
        return this.moduleNameMap.values();
    }

    @SuppressWarnings("unchecked")
    public <T extends IModule> T getModule(Class<T> clazz) {
        return (T) this.moduleClassMap.get(clazz);
    }

    @SuppressWarnings("unchecked")
    public <T extends IModule> T getModule(String name) {
        return (T) this.moduleNameMap.get(name.toLowerCase());
    }

    private void register(IModule module) {
        this.moduleNameMap.put(module.getName().toLowerCase(), module);
        this.moduleClassMap.put(module.getClass(), module);
    }

}
