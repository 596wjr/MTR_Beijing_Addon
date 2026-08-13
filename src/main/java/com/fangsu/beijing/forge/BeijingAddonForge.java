package com.fangsu.beijing.forge;

import com.fangsu.beijing.BeijingAddon;
import net.minecraftforge.fml.common.Mod;

/**
 * Forge 入口类。
 *
 * Forge 的 mods.toml 没有 entrypoint 机制：加载器扫描 classpath 上
 * 所有带 @Mod 注解的类来定位模组入口，并调用其无参构造器完成初始化。
 * 本类的 modId 必须与 mods.toml 中 [[mods]].modId 完全一致。
 *
 * 注意：本模组是纯 API 模组，编译产物（class 字节码）不引用任何
 * Minecraft 类，因此 Fabric 版编译的 class 在 Forge 端同样可用。
 */
@Mod("mtr_beijing_addon")
public class BeijingAddonForge {
    public BeijingAddonForge() {
        // 与 Fabric 端 fabric.mod.json 的 main entrypoint 同一时机、同一调用
        BeijingAddon.init();
    }
}
