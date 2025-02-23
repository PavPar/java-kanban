import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;

public class TaskManager {
    private final String ERR_MSG_NO_TASK_FOUND = "Элемента с таким id не существует";
    private final String ERR_MSG_DUPLICATE_UID = "Элемент с таким id уже существует";

    private static int taskCounter = 0;
    private final HashMap<Integer,Task> tasks = new HashMap<>();

    public static int getNextTaskUID() {
        return taskCounter++;
    }

    public HashMap<Integer,Task> getTasks(){
        return tasks;
    }

    public void clearTasks(){
        tasks.clear();
        taskCounter = 0;
    }

    public ArrayList<Task> getTaskByUID(int uid){
        ArrayList<Task> taskArrayList = new ArrayList<>();
        for(Task task : tasks.values()){
            if(task.getUid() ==  uid){

               taskArrayList.add(task);
               return taskArrayList;
            }
        }
        return taskArrayList;
    }

    public void addTask(Task task){
        if(tasks.containsKey(task.getUid())){
            System.out.println(ERR_MSG_DUPLICATE_UID);
            return;
        }
        if (task.getTaskType() == TaskTypes.SUBTASK) {
            SubTask subTask = (SubTask) task;
            Epic epic = (Epic) tasks.get(subTask.getEpicUID());
            epic.addSubtask(subTask.getUid());
        }
        tasks.put(task.getUid(),task);
    }

    public void updateTask(Task task){
        if(!tasks.containsKey(task.getUid())){
            System.out.println(ERR_MSG_NO_TASK_FOUND);
            return;
        }
        switch (task.getTaskType()){
            case TASK:
                tasks.put(task.getUid(),task);
                break;
            case EPIC:
                updateEpic(task);
                break;
            case SUBTASK:
                updateSubtask(task);
                break;
            default:
                System.out.println("Неизвестный тип задачи");
        }
    }

    public void deleteTask(int uid){
        if(!tasks.containsKey(uid)){
            System.out.println(ERR_MSG_NO_TASK_FOUND);
            return;
        }
        tasks.remove(uid);
    }

    private void updateEpic(Task task){
        Epic epic = (Epic) task;
        ArrayList<Integer> subtasks = epic.getSubtasks();

        for(int uid : subtasks){
            SubTask subtask = (SubTask) tasks.get(uid);
            subtask.setStatus(task.getStatus());
        }

        epic.setStatus(task.getStatus());
    }

    private void updateSubtask(Task task){
        SubTask subTask = (SubTask) task;
        Epic epic = (Epic)tasks.get(subTask.getEpicUID());

        subTask.setStatus(task.getStatus());
        epic.setStatus(recalculateEpicStatus(epic));
    }

    private TaskStatus recalculateEpicStatus(Epic epic){
        ArrayList<Integer> subtasks = epic.getSubtasks();

        boolean areAllSubtasksComplete = true;
        boolean areAllSubtasksNew = true;

        for(int uid : subtasks){
            SubTask subtask = (SubTask) tasks.get(uid);
            if(subtask.getStatus() != TaskStatus.DONE){
                areAllSubtasksComplete = false;
            }
            if(subtask.getStatus() != TaskStatus.NEW){
                areAllSubtasksNew = false;
            }
        }

        if(areAllSubtasksNew){
            return TaskStatus.NEW;
        }
        if(areAllSubtasksComplete){
            return TaskStatus.DONE;
        }

        return TaskStatus.IN_PROGRESS;
    }

    public boolean hasTask(int id){
        return tasks.containsKey(id);
    }
}
