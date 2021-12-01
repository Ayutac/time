package org.abos.fabricmc.time.mixin;

import net.minecraft.entity.mob.HoglinEntity;
import net.minecraft.server.world.ServerWorld;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(HoglinEntity.class)
public interface HoglinEntityEnvoker {

    @Invoker("zombify")
    public void invokeZombify(ServerWorld world);

}
