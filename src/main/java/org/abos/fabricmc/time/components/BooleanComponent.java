package org.abos.fabricmc.time.components;

import dev.onyxstudios.cca.api.v3.component.Component;

/**
 * A component associated with a boolean value.
 */
public interface BooleanComponent extends Component {

    /**
     * Returns the value of the underlying boolean. Equivalent to {@code !isFalse()}.
     * @return {@code true} if the boolean is {@code true}, else {@code false}.
     * @see #isFalse()
     */
    public boolean isTrue();

    /**
     * Returns the negated value of the underlying boolean. Equivalent to {@code !isTrue()}.
     * @return {@code true} if the boolean is {@code false}, else {@code false}.
     * @see #isTrue()
     */
    public boolean isFalse();

    /**
     * Sets the boolean value associated to this component.
     * @param value the new boolean value
     * @see #isTrue()
     * @see #isFalse()
     */
    public void setValue(boolean value);

}
