import java.util.ArrayList;

public class Epic extends Task{
    private ArrayList<Integer> subtasks = new ArrayList<>();

    public Epic(String name, String description, TaskStatus status) {
        super(name, description,  status);
    }

    public Epic(String name, String description,int id, TaskStatus status,ArrayList<Integer> subtasks) {
        super(name, description, id, status);
        this.subtasks = subtasks;
    }

    public ArrayList<Integer> getSubtasks() {
        return subtasks;
    }

    public void addSubtask(int uid){
        if(this.getId() == uid){
            return;
        }
        subtasks.add(uid);
    }

    public void addSubtasks(ArrayList<Integer> uids){
        for(Integer uid: uids){
            addSubtask(uid);
        }
    }

    public void removeSubtask(int uid){
        subtasks.remove((Integer) uid);
    }

    @Override
    public String toString(){
        return "Эпик № "+ this.getId()
                + "|" + "Имя : " + this.getName()
                + "|" + "Описание : "+ this.getDescription()
                + "|" + "Статус : " + this.getStatus();
    }



}
