package org.abos.fabricmc.time.mixin;

import net.minecraft.entity.mob.PiglinEntity;
import net.minecraft.server.world.ServerWorld;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(PiglinEntity.class)
public interface PiglinEntityEnvoker {

    @Invoker("zombify")
    public void invokeZombify(ServerWorld world);

}
