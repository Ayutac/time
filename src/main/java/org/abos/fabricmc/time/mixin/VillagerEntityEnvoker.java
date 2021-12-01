package org.abos.fabricmc.time.mixin;

import net.minecraft.entity.passive.VillagerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(VillagerEntity.class)
public interface VillagerEntityEnvoker {

    @Invoker("releaseAllTickets")
    public void invokeReleaseAllTickets();

}
