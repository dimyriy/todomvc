package org.dimyriy.todomvc.model;

import javax.persistence.*;

/**
 * @author dimyriy
 * @date 16/02/16
 */
@Entity
@SequenceGenerator(allocationSize = 1, name = "uniq_id", sequenceName = "uniq_id")
public class Todo {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "uniq_id")
    private long id;
    @Column
    private String title;
    @Column
    private Boolean completed;

    public Todo() {
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Boolean isCompleted() {
        return completed;
    }

    public void setCompleted(boolean completed) {
        this.completed = completed;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }
}
