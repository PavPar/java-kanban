import java.util.Objects;

public class Task {
    private String name;
    private String description;
    private final int uid;
    private TaskStatus status;

    protected final TaskTypes taskType;

    public Task(String name, String description, int uid, TaskStatus status) {
        this.name = name;
        this.description = description;
        this.uid = uid;
        this.status = status;
        this.taskType = TaskTypes.TASK;
    }

    public Task(String name, String description, int uid, TaskStatus status,TaskTypes taskStatus) {
        this.name = name;
        this.description = description;
        this.uid = uid;
        this.status = status;
        this.taskType = taskStatus;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public int getUid() {
        return uid;
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


    public TaskTypes getTaskType() {
        return taskType;
    }



    @Override
    public boolean equals(Object o) {
        if(this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Task task = (Task) o;
        return Objects.equals(name, task.name) && Objects.equals(description, task.description) && Objects.equals(uid, task.uid) && status == task.status;
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, description, uid, status);
    }

    @Override
    public String toString(){
        return "Задача № "+this.uid
                + "|" + "Тип : " + this.taskType
                + "|" + "Имя : " + this.name
                + "|" + "Описание : "+ this.description
                + "|" + "Статус : " + this.status;
    }
}
