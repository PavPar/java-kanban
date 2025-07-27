import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Objects;

public class Epic extends Task {
    private ArrayList<Integer> subtasks = new ArrayList<>();

    public Epic(String name, String description, TaskStatus status) {
        super(name, description, status);
    }

    public Epic(String name, String description, int id, TaskStatus status, ArrayList<Integer> subtasks) {
        super(name, description, id, status);
        this.subtasks = subtasks;
    }

    public <E> Epic(String name, String description, int id, TaskStatus status, ArrayList<E> es, LocalDateTime time, Duration duration) {
        super(name, description, id, status,time,duration);
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

        if(Objects.nonNull(this.getStartTime())){
            builder.append("Дата начала  : ").append(this.getStartTime()).append("|");
        }

        if(Objects.nonNull(this.getStartTime())) {
            builder.append("Продолжительность (минут) : ").append(this.getDuration().toMinutes());
        }

        return builder.toString();
    }


}
