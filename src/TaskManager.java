import java.util.ArrayList;
import java.util.HashMap;

public class TaskManager {
    private final String ERR_MSG_NO_TASK_FOUND = "Элемента с таким id не существует";
    private final String ERR_MSG_DUPLICATE_UID = "Элемент с таким id уже существует";

    private static int taskCounter = 0;
    private final HashMap<Integer,Task> tasks = new HashMap<>();
    private final HashMap<Integer,SubTask> subTasks = new HashMap<>();
    private final HashMap<Integer,Epic> epics = new HashMap<>();

    public static int getNextTaskUID() {
        return taskCounter++;
    }

    public HashMap<Integer,Task> getTasks(){
        return tasks;
    }
    public HashMap<Integer,SubTask> getSubTasks(){
        return subTasks;
    }
    public HashMap<Integer,Epic> getEpics(){
        return epics;
    }

    public Task getTask(int uid){
        return tasks.get(uid);
    }

    public SubTask getSubtask(int uid){
        return subTasks.get(uid);
    }

    public Epic getEpic(int uid){
        return epics.get(uid);
    }

    public void clearTasks(){
        tasks.clear();
        epics.clear();
        subTasks.clear();
        taskCounter = 0;
    }

    public ArrayList<Task> findTask(int uid){
        ArrayList<Task> taskArrayList = new ArrayList<>();

        for(Task task : tasks.values()){
            if(task.getUid() ==  uid){

               taskArrayList.add(task);
               return taskArrayList;
            }
        }

        for(Task task : epics.values()){
            if(task.getUid() ==  uid){

                taskArrayList.add(task);
                return taskArrayList;
            }
        }

        for(Task task : subTasks.values()){
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
        if (task.getTaskType() == TaskType.SUBTASK) {
            SubTask subTask = (SubTask) task;
            Epic epic = (Epic) tasks.get(subTask.getEpicUID());
            epic.addSubtask(subTask.getUid());
        }
        tasks.put(task.getUid(),task);
    }

    public void addEpic(Epic task){
        if(epics.containsKey(task.getUid())){
            System.out.println(ERR_MSG_DUPLICATE_UID);
            return;
        }

        epics.put(task.getUid(),task);
    }

    public void addSubtask(SubTask subTask){
        if(subTasks.containsKey(subTask.getUid())){
            System.out.println(ERR_MSG_DUPLICATE_UID);
            return;
        }

        Epic epic = epics.get(subTask.getEpicUID());
        epic.addSubtask(subTask.getUid());
        subTasks.put(subTask.getUid(),subTask);
        epic.setStatus(recalculateEpicStatus(epic));
    }

    public void updateTask(Task task){
        if(!tasks.containsKey(task.getUid())){
            System.out.println(ERR_MSG_NO_TASK_FOUND);
            return;
        }
        tasks.put(task.getUid(),task);
    }

    public void deleteTask(int uid){
        ArrayList<Task> foundTasks = findTask(uid);

        if(foundTasks.isEmpty()){
            System.out.println(ERR_MSG_NO_TASK_FOUND);
            return;
        }

        Task task = foundTasks.get(0);

        switch (task.getTaskType()){
            case TASK:
                tasks.remove(uid);
                return;
            case SUBTASK:
                int subTaskEpicUID = subTasks.get(uid).getEpicUID();
                Epic epic = epics.get(subTaskEpicUID);

                epic.removeSubtask(uid);
                subTasks.remove(uid);

                epic.setStatus(recalculateEpicStatus(epic));
                return;
            case EPIC:
                for(int subTaskUID : epics.get(uid).getSubtasks()){
                    subTasks.remove(subTaskUID);
                }
                epics.remove(uid);
                return;
            default:
                System.out.println("Нет такого типа таска");
        }

    }

    public void updateEpic(Epic epic){
        ArrayList<Integer> subtasksUIDs = epic.getSubtasks();

        for(int uid : subtasksUIDs){
            SubTask subtask = subTasks.get(uid);
            subtask.setStatus(epic.getStatus());
        }

        epics.put(epic.getUid(),epic);
    }

    public void updateSubtask(SubTask subTask){
        subTasks.put(subTask.getUid(),subTask);

        Epic epic = epics.get(subTask.getEpicUID());
        epic.setStatus(recalculateEpicStatus(epic));
    }

    private TaskStatus recalculateEpicStatus(Epic epic){
        ArrayList<Integer> subTaskUIDs = epic.getSubtasks();

        boolean areAllSubtasksComplete = true;
        boolean areAllSubtasksNew = true;

        for(int uid : subTaskUIDs){
            SubTask subtask =  subTasks.get(uid);
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

    public boolean hasTask(int id, TaskType taskType){
        switch (taskType){
            case TASK:
                return tasks.containsKey(id);
            case SUBTASK:
                return subTasks.containsKey(id);
            case EPIC:
                return epics.containsKey(id);
            default:
                System.out.println("Нет такого типа таска");
                return false;
        }

    }
}
