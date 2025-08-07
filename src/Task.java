import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;
import com.google.gson.annotations.Expose;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Objects;

public class Task {
    @Expose private String name;
    @Expose private String description;
    private int id;
    @Expose private TaskStatus status;
    @Expose private Duration duration = Duration.ofMinutes(0);
    @Expose private LocalDateTime startTime;

    public Task(String name, String description, int uid, TaskStatus status) {
        this.name = name;
        this.description = description;
        this.id = uid;
        this.status = status;
    }

    public Task(String name, String description, int uid, TaskStatus status, LocalDateTime startTime, Duration duration) {
        this.name = name;
        this.description = description;
        this.id = uid;
        this.status = status;
        this.startTime = startTime;
        this.duration = duration;
    }

    public Task(String name, String description, TaskStatus status) {
        this.name = name;
        this.description = description;
        this.status = status;
    }

    public Task(String name, String description, TaskStatus status, LocalDateTime startTime, Duration duration) {
        this.name = name;
        this.description = description;
        this.status = status;
        this.startTime = startTime;
        this.duration = duration;
    }

    public void setTaskId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public int getId() {
        return id;
    }

    public TaskStatus getStatus() {
        return status;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setStatus(TaskStatus status) {
        this.status = status;
    }

    public LocalDateTime getEndTime() {
        return startTime.plus(duration);
    }

    public void setStartTIme(LocalDateTime startTime) {
        this.startTime = startTime;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public Duration getDuration() {
        return duration;
    }

    public void setDuration(Duration duration) {
        this.duration = duration;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Task task = (Task) o;
        return Objects.equals(name, task.name) && Objects.equals(description, task.description) && Objects.equals(id, task.id) && status == task.status;
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, description, id, status);
    }

    @Override
    public String toString() {
         DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("dd:MM:yyyy HH:mm");

        if(Objects.nonNull(this.startTime)){
            return "Задача № " + this.id
                    + "|" + "Имя : " + this.name
                    + "|" + "Описание : " + this.description
                    + "|" + "Статус : " + this.status
                    + "|" + "Дата начала  : " + dateTimeFormatter.format(this.startTime)
                    + "|" + "Продолжительность (минут) : " + this.duration.toMinutes();

        }

        return "Задача № " + this.id
                + "|" + "Имя : " + this.name
                + "|" + "Описание : " + this.description
                + "|" + "Статус : " + this.status;
    }

    public String toJSON(){
        Gson gson = new GsonBuilder()
                .serializeNulls()
                .setPrettyPrinting()
                .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
                .registerTypeAdapter(Duration.class, new DurationAdapter())
                .create();
        return gson.toJson(this);
    }
    public String toJSONIgnoringNotExposedValues(){
        Gson gson = new GsonBuilder()
                .serializeNulls()
                .setPrettyPrinting()
                .excludeFieldsWithoutExposeAnnotation()
                .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
                .registerTypeAdapter(Duration.class, new DurationAdapter())
                .create();
        return gson.toJson(this);
    }

    public static Task fromJSON(String taskJSONString) throws JsonSyntaxException {
        Gson gson = new GsonBuilder()
                .serializeNulls()
                .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
                .registerTypeAdapter(Duration.class, new DurationAdapter())
                .create();
        return gson.fromJson(taskJSONString,Task.class);

    }
}
