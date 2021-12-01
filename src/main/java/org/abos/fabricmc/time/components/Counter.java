package org.abos.fabricmc.time.components;

/**
 * An object associated with a counter value, i.e. a non-negative integer that will not overflow.
 */
public interface Counter {

    /**
     * Checks if a given int value can serve as a counter value, i.e. if it is non-negative.
     * @param value the value to check
     * @return {@code true} if the specified value is non-negative
     */
    public static boolean isCounterValue(int value) {
        return value >= 0;
    }

    /**
     * The value of this counter.
     * @return the stored value, guaranteed to be non-negative
     * @see #isZero()
     */
    public int getValue();

    /**
     * Sets the counter to the specified value.
     * @param value the new value of this counter
     * @throws IllegalArgumentException If {@link Counter#isCounterValue(int)} returns {@code false}.
     */
    public void setValue(int value);

    /**
     * If this counter is zero. Equivalent to calling {@code getCounter() == 0}.
     * @return {@code true} if the associated counter is equal to {@code 0}, else {@code false}.
     * @see #getValue()
     */
    public boolean isZero();

    /**
     * Increments the counter by the specified value, but keeping it between {@code 0} and {@link Integer#MAX_VALUE}.
     * No overflow will occur. Equivalent to calling {@code decrement(-value)}.
     * @see #increment()
     * @see #decrement(int)
     */
    public void increment(int value);

    /**
     * Increments the counter if no overflow would occur, else the counter doesn't change.
     * Equivalent to calling {@code increment(1)}.
     * @see #increment(int)
     */
    public void increment();

    /**
     * Decrements the counter by the specified value, but keeping it between {@code 0} and {@link Integer#MAX_VALUE}.
     * No overflow will occur. Equivalent to calling {@code increment(-value)}.
     * @see #decrement()
     * @see #increment(int)
     */
    public void decrement(int value);

    /**
     * Decrements the counter if it will stay non-negative, else the counter doesn't change.
     * Equivalent to calling {@code decrement(1)}.
     * @see #decrement(int)
     */
    public void decrement();

    /**
     * Sets the counter to {@code 0}. {@link #isZero()} is guaranteed to return {@code true} after this methods
     * returns. Equivalent to calling {@code decrement(getValue)}.
     */
    public void reset();

}
