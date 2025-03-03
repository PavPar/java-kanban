import java.util.Objects;

public class Task {
    private String name;
    private String description;
    private int id;
    private TaskStatus status;


    public Task(String name, String description, int uid, TaskStatus status) {
        this.name = name;
        this.description = description;
        this.id = uid;
        this.status = status;
    }

    public Task(String name, String description, TaskStatus status) {
        this.name = name;
        this.description = description;
        this.status = status;
    }

    public void setTaskId(int id){
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



    @Override
    public boolean equals(Object o) {
        if(this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Task task = (Task) o;
        return Objects.equals(name, task.name) && Objects.equals(description, task.description) && Objects.equals(id, task.id) && status == task.status;
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, description, id, status);
    }

    @Override
    public String toString(){
        return "Задача № "+this.id
                + "|" + "Имя : " + this.name
                + "|" + "Описание : "+ this.description
                + "|" + "Статус : " + this.status;
    }
}
