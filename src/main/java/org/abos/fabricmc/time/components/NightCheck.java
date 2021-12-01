package org.abos.fabricmc.time.components;

import net.minecraft.nbt.NbtCompound;

public class NightCheck implements BooleanComponent {

    /**
     * The internal boolean value of this {@link BooleanComponent}.
     */
    private boolean wasNight = false;

    @Override
    public boolean isTrue() {
        return wasNight;
    }

    @Override
    public boolean isFalse() {
        return !wasNight;
    }

    @Override
    public void setValue(boolean value) {
        wasNight = value;
    }

    @Override
    public void readFromNbt(NbtCompound tag) {
        wasNight = tag.getBoolean(TimeComponents.NIGHT_CHECK_STR);
    }

    @Override
    public void writeToNbt(NbtCompound tag) {
        tag.putBoolean(TimeComponents.NIGHT_CHECK_STR, wasNight);
    }
}
