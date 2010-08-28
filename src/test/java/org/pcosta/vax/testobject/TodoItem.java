package org.pcosta.vax.testobject;

import java.util.Date;

import org.pcosta.vax.annotation.Value;

public class TodoItem {

    private String task;

    private Date dueDate;

    private int priority;

    @Value
    public String getTask() {
        return task;
    }

    public void setTask(final String task) {
        this.task = task;
    }

    @Value
    public Date getDueDate() {
        return dueDate;
    }

    public void setDueDate(final Date dueDate) {
        this.dueDate = dueDate;
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
        return "TodoItem [task=" + task + ", dueDate=" + dueDate + ", priority=" + priority + "]";
    }
}
