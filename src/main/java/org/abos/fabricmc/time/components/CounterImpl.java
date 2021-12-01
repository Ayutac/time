package org.abos.fabricmc.time.components;

/**
 * An implementation of the {@link Counter}, providing all counter behaviour as specified.
 */
public class CounterImpl implements Counter {

    /**
     * The internal counter value.
     */
    private int counter = 0;

    /**
     * Validates a given integer value to be a counter for internal use, i.e. being non-negative.
     * @param counter the value to check
     * @throws IllegalArgumentException If {@link Counter#isCounterValue(int)} returns {@code false}.
     */
    protected void validateCounter(int counter) {
        if (!Counter.isCounterValue(counter))
            throw new IllegalArgumentException("A counter must never be negative!");
    }

    /**
     * Validates the counter associated with this component, i.e. checks if it is non-negative.
     * @throws IllegalStateException If {@link #counter} is less than {@code 0} (shouldn't happen).
     */
    public void validateCounter() {
        if (!Counter.isCounterValue(counter))
            throw new IllegalStateException("counter must never be negative!");
    }

    /**
     * {@inheritDoc}
     * @throws IllegalStateException If {@link #counter} is less than {@code 0} (shouldn't happen).
     */
    @Override
    public int getValue() {
        validateCounter();
        return counter;
    }

    @Override
    public void setValue(int value) {
        validateCounter(value);
        counter = value;
    }

    @Override
    public void reset() {
        counter = 0;
    }

    /**
     * {@inheritDoc}
     * @throws IllegalStateException If {@link #counter} is less than {@code 0} (shouldn't happen).
     */
    @Override
    public boolean isZero() {
        return getValue() == 0;
    }

    /**
     * {@inheritDoc}
     * @throws IllegalStateException If {@link #counter} is less than {@code 0} (shouldn't happen).
     */
    @Override
    public void increment(int value) {
        decrement(-value);
    }

    /**
     * {@inheritDoc}
     * @throws IllegalStateException If {@link #counter} is less than {@code 0} (shouldn't happen).
     */
    @Override
    public void increment() {
        validateCounter();
        if (counter != Integer.MAX_VALUE)
            counter++;
    }

    /**
     * {@inheritDoc}
     * @throws IllegalStateException If {@link #counter} is less than {@code 0} (shouldn't happen).
     */
    @Override
    public void decrement(int value) {
        validateCounter();
        if (value > 0 && value >= counter)
            counter = 0;
        else if (value < 0 && counter - value < 0)
            counter = Integer.MAX_VALUE;
        else
            counter -= value;
    }

    /**
     * {@inheritDoc}
     * @throws IllegalStateException If {@link #counter} is less than {@code 0} (shouldn't happen).
     */
    @Override
    public void decrement() {
        validateCounter();
        if (counter != 0)
            counter--;
    }

}
