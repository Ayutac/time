package org.abos.fabricmc.time.components;

import dev.onyxstudios.cca.api.v3.component.sync.AutoSyncedComponent;
import net.minecraft.nbt.NbtCompound;

public class PassedTimeCounter extends CounterImpl implements CounterComponent, AutoSyncedComponent {

    @Override
    public void readFromNbt(NbtCompound tag) {
        setValue(tag.getInt(TimeComponents.PASSED_TIME_STR));
    }

    @Override
    public void writeToNbt(NbtCompound tag) {
        tag.putInt(TimeComponents.PASSED_TIME_STR, getValue());
    }

}
