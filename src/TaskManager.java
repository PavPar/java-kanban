import java.util.ArrayList;
import java.util.HashMap;

public class TaskManager {
    private int taskCounter = 0;
    private final HashMap<Integer, Task> tasks = new HashMap<>();
    private final HashMap<Integer, SubTask> subTasks = new HashMap<>();
    private final HashMap<Integer, Epic> epics = new HashMap<>();


    public ArrayList<Task> getTasks() {
        return new ArrayList<Task>(tasks.values());
    }

    public ArrayList<SubTask> getSubTasks() {
        return new ArrayList<SubTask>(subTasks.values());
    }

    public ArrayList<Epic> getEpics() {
        return new ArrayList<Epic>(epics.values());
    }

    public Task getTask(int id) {
        return tasks.get(id);
    }

    public SubTask getSubtask(int id) {
        return subTasks.get(id);
    }

    public Epic getEpic(int id) {
        return epics.get(id);
    }

    public ArrayList<SubTask> getEpicSubtasks(int id){
        ArrayList<SubTask> subTaskArrayList = new ArrayList<SubTask>();

        Epic epic = epics.get(id);
        ArrayList<Integer> subtasksIds = epic.getSubtasks();

        for(int subTaskId:subtasksIds){
            subTaskArrayList.add(subTasks.get(subTaskId));
        }

        return subTaskArrayList;
    }
    
    public void resetTasks() {
        tasks.clear();
    }

    public void resetEpics() {
        for(Integer epicID: epics.keySet()){
            this.deleteEpic(epicID);
        }
    }

    public void resetSubtasks() {
        for(Integer subTaskID: subTasks.keySet()){
            this.deleteSubtask(subTaskID);
        }
    }

    public void addTask(Task task) {
        task.setTaskId(getNextTaskId());
        tasks.put(task.getId(), task);
    }

    public void addEpic(Epic epic) {
        epic.setTaskId(getNextTaskId());
        epics.put(epic.getId(), epic);
    }

    public void addSubtask(SubTask subTask) {
        subTask.setTaskId(getNextTaskId());
        Epic epic = epics.get(subTask.getEpicID());
        epic.addSubtask(subTask.getId());
        subTasks.put(subTask.getId(), subTask);
        epic.setStatus(recalculateEpicStatus(epic));
    }

    public void updateTask(Task task) {
        tasks.put(task.getId(), task);
    }

    public void updateEpic(Epic epic) {
        ArrayList<Integer> subtasksUIDs = epic.getSubtasks();

        for (int uid : subtasksUIDs) {
            SubTask subtask = subTasks.get(uid);
            subtask.setStatus(epic.getStatus());
        }

        epics.put(epic.getId(), epic);
    }

    public void updateSubtask(SubTask subTask) {
        subTasks.put(subTask.getId(), subTask);

        Epic epic = epics.get(subTask.getEpicID());
        epic.setStatus(recalculateEpicStatus(epic));
    }

    public void deleteTask(int uid) {
        tasks.remove(uid);
    }

    public void deleteSubtask(int uid) {
        int subTaskEpicUID = subTasks.get(uid).getEpicID();
        Epic epic = epics.get(subTaskEpicUID);

        epic.removeSubtask(uid);
        subTasks.remove(uid);

        epic.setStatus(recalculateEpicStatus(epic));
    }

    public void deleteEpic(int uid) {
        for (int subTaskUID : epics.get(uid).getSubtasks()) {
            subTasks.remove(subTaskUID);
        }
        epics.remove(uid);
    }

    public boolean hasTask(int id) {
        return tasks.containsKey(id);
    }

    public boolean hasSubTask(int id) {
        return subTasks.containsKey(id);
    }

    public boolean hasEpic(int id) {
        return epics.containsKey(id);
    }

    private int getNextTaskId() {
        return taskCounter++;
    }

    private TaskStatus recalculateEpicStatus(Epic epic) {
        ArrayList<Integer> subTaskUIDs = epic.getSubtasks();

        boolean areAllSubtasksComplete = true;
        boolean areAllSubtasksNew = true;

        for (int uid : subTaskUIDs) {
            SubTask subtask = subTasks.get(uid);
            if (subtask.getStatus() != TaskStatus.DONE) {
                areAllSubtasksComplete = false;
            }
            if (subtask.getStatus() != TaskStatus.NEW) {
                areAllSubtasksNew = false;
            }
        }

        if (areAllSubtasksNew) {
            return TaskStatus.NEW;
        }
        if (areAllSubtasksComplete) {
            return TaskStatus.DONE;
        }

        return TaskStatus.IN_PROGRESS;
    }
}
