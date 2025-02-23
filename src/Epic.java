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
        subtasks.remove(uid);
    }


    public Epic(String name, String description, int uid, TaskStatus status) {
        super(name, description, uid, status,TaskTypes.EPIC);
    }
}
