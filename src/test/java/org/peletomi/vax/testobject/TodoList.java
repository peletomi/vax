package org.peletomi.vax.testobject;

import java.util.ArrayList;
import java.util.List;

import org.peletomi.vax.annotation.Value;

public class TodoList {

    private final List<TodoItem> items = new ArrayList<TodoItem>();

    @Value(name = "item", recurse = true, collection = true)
    public List<TodoItem> getItems() {
        return items;
    }

    public void addItem(final String task, final int priority) {
        final TodoItem item = new TodoItem();
        item.setTask(task);
        item.setPriority(priority);
        items.add(item);
    }

    @Override
    public String toString() {
        return "TodoList [items=" + items + "]";
    }

}
