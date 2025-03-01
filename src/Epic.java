import java.util.ArrayList;

public class Epic extends Task{
    private ArrayList<Integer> subtasks = new ArrayList<>();

    public ArrayList<Integer> getSubtasks() {
        return subtasks;
    }

    public void addSubtask(int uid){
        subtasks.add(uid);
    }

    public void removeSubtask(int uid){
        subtasks.remove((Integer) uid);
    }


    public Epic(String name, String description, int uid, TaskStatus status) {
        super(name, description, uid, status, TaskType.EPIC);
    }

    public Epic(String name, String description, int uid, TaskStatus status,ArrayList<Integer> subtasks) {
        super(name, description, uid, status, TaskType.EPIC);
        this.subtasks = subtasks;
    }
}
