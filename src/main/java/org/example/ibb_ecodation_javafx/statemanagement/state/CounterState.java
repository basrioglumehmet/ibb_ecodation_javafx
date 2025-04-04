package org.example.ibb_ecodation_javafx.statemanagement.state;


import org.example.ibb_ecodation_javafx.statemanagement.AppState;

public class CounterState extends AppState {
    private final int counter;

    public CounterState(int counter) {
        this.counter = counter;
    }

    public int getCounter() {
        return counter;
    }

    public CounterState increment() {
        return new CounterState(this.counter + 1);
    }

    public CounterState decrement() {
        return new CounterState(this.counter - 1);
    }

    @Override
    public String toString() {
        return "Counter: " + counter;
    }
}
