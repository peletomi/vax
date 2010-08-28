package org.pcosta.vax.testobject;

import org.pcosta.vax.annotation.Value;

public class TodoItem {

    private String task;

    private int priority;

    @Value
    public String getTask() {
        return task;
    }

    public void setTask(final String task) {
        this.task = task;
    }

    @Value
    public int getPriority() {
        return priority;
    }

    public void setPriority(final int priority) {
        this.priority = priority;
    }

    @Override
    public String toString() {
        return "TodoItem [task=" + task + ", priority=" + priority + "]";
    }

}
