package com.example.DTOs;

public class EventStatistics {
    private Long total;
    private Long planned;
    private Long inProgress;
    private Long completed;
    private Long cancelled;
    private Long deleted;

    public EventStatistics() {
    }

    public Long getTotal() {
        return total;
    }

    public void setTotal(Long total) {
        this.total = total;
    }

    public Long getPlanned() {
        return planned;
    }

    public void setPlanned(Long planned) {
        this.planned = planned;
    }

    public Long getInProgress() {
        return inProgress;
    }

    public void setInProgress(Long inProgress) {
        this.inProgress = inProgress;
    }

    public Long getCompleted() {
        return completed;
    }

    public void setCompleted(Long completed) {
        this.completed = completed;
    }

    public Long getCancelled() {
        return cancelled;
    }

    public void setCancelled(Long cancelled) {
        this.cancelled = cancelled;
    }

    public Long getDeleted() {
        return deleted;
    }

    public void setDeleted(Long deleted) {
        this.deleted = deleted;
    }
}
