package org.pcosta.vax.testobject;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.pcosta.vax.annotation.Value;

public class TodoList {

    private final List<TodoItem> items = new ArrayList<TodoItem>();

    @Value(recurse = true, collection = true)
    public List<TodoItem> getItems() {
        return items;
    }

    public void addItem(final String task, final Date dueDate, final int priority) {
        final TodoItem item = new TodoItem();
        item.setTask(task);
        item.setDueDate(dueDate);
        item.setPriority(priority);
        items.add(item);
    }

    @Override
    public String toString() {
        return "TodoList [items=" + items + "]";
    }

}
