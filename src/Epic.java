import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.annotations.Expose;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Objects;

public class Epic extends Task {
    @Expose private ArrayList<Integer> subtasks = new ArrayList<>();
    private LocalDateTime endTime;

    public Epic(String name, String description, TaskStatus status) {
        super(name, description, status);
    }

    public Epic(String name, String description, int id, TaskStatus status, ArrayList<Integer> subtasks) {
        super(name, description, id, status);
        this.subtasks = subtasks;
    }

    public Epic(String name, String description, int id, TaskStatus status, ArrayList<Integer> subtasks, LocalDateTime time, Duration duration) {
        super(name, description, id, status, time, duration);
        this.subtasks = subtasks;
    }

    public void setEndTime(LocalDateTime endTime) {
        this.endTime = endTime;
    }

    @Override
    public LocalDateTime getEndTime() {
        return this.endTime;
    }

    public ArrayList<Integer> getSubtasks() {
        return subtasks;
    }

    public void addSubtask(int uid) {
        if (this.getId() == uid) {
            return;
        }
        subtasks.add(uid);
    }

    public void addSubtasks(ArrayList<Integer> uids) {
        for (Integer uid : uids) {
            addSubtask(uid);
        }
    }

    public void removeSubtask(int uid) {
        subtasks.remove((Integer) uid);
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();

        builder
                .append("Эпик № ").append(this.getId()).append("|")
                .append("Имя : ").append(this.getName()).append("|")
                .append("Описание : ").append(this.getDescription()).append("|");

        if (Objects.nonNull(this.getStartTime())) {
            builder.append("Дата начала  : ").append(this.getStartTime()).append("|");
        }

        if (Objects.nonNull(this.getStartTime())) {
            builder.append("Продолжительность (минут) : ").append(this.getDuration().toMinutes());
        }

        return builder.toString();
    }

    public static Epic fromJSON(String jsonString) {
        Gson gson = new GsonBuilder()
                .serializeNulls()
                .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
                .registerTypeAdapter(Duration.class, new DurationAdapter())
                .create();
        return gson.fromJson(jsonString, Epic.class);
    }
}
