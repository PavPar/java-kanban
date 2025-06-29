import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class InMemoryTaskManager implements TaskManager {
    private int taskCounter = 0;
    private final HashMap<Integer, Task> tasks = new HashMap<>();
    private final HashMap<Integer, SubTask> subTasks = new HashMap<>();
    private final HashMap<Integer, Epic> epics = new HashMap<>();

    private static HistoryManager historyManager = Managers.getDefaultHistory();


    @Override
    public ArrayList<Task> getTasks() {
        return new ArrayList<Task>(tasks.values());
    }

    @Override
    public ArrayList<SubTask> getSubTasks() {
        return new ArrayList<SubTask>(subTasks.values());
    }

    @Override
    public ArrayList<Epic> getEpics() {
        return new ArrayList<Epic>(epics.values());
    }

    @Override
    public List<Task> getHistory(){
        return historyManager.getHistory();
    }

    @Override
    public Task getTask(int id) {
        Task task = tasks.get(id);
        historyManager.add(task);
        return task;
    }

    @Override
    public SubTask getSubtask(int id) {
        SubTask subTask = subTasks.get(id);
        historyManager.add(subTask);
        return subTask;
    }

    @Override
    public Epic getEpic(int id) {
        Epic epic = epics.get(id);
        historyManager.add(epic);
        return epic;
    }

    @Override
    public ArrayList<SubTask> getEpicSubtasks(int id) {
        ArrayList<SubTask> subTaskArrayList = new ArrayList<SubTask>();

        Epic epic = epics.get(id);
        ArrayList<Integer> subtasksIds = epic.getSubtasks();

        for (int subTaskId : subtasksIds) {
            subTaskArrayList.add(subTasks.get(subTaskId));
        }

        return subTaskArrayList;
    }

    @Override
    public void resetTasks() {
        tasks.clear();
    }

    @Override
    public void resetEpics() {
        for (Integer epicID : epics.keySet()) {
            this.deleteEpic(epicID);
        }
    }

    @Override
    public void resetSubtasks() {
        for (Integer subTaskID : subTasks.keySet()) {
            this.deleteSubtask(subTaskID);
        }
    }

    @Override
    public void addTask(Task task) {
        task.setTaskId(getNextTaskId());
        tasks.put(task.getId(), task);
    }

    @Override
    public void addEpic(Epic epic) {
        epic.setTaskId(getNextTaskId());
        epics.put(epic.getId(), epic);
    }

    @Override
    public void addSubtask(SubTask subTask) {
        subTask.setTaskId(getNextTaskId());
        Epic epic = epics.get(subTask.getEpicID());
        epic.addSubtask(subTask.getId());
        subTasks.put(subTask.getId(), subTask);
        epic.setStatus(recalculateEpicStatus(epic));
    }

    @Override
    public void updateTask(Task task) {
        historyManager.remove(tasks.get(task.getId()));
        tasks.put(task.getId(), task);
    }

    @Override
    public void updateEpic(Epic epic) {
        ArrayList<Integer> subtasksUIDs = epic.getSubtasks();

        for (int uid : subtasksUIDs) {
            SubTask subtask = subTasks.get(uid);
            subtask.setStatus(epic.getStatus());
            historyManager.remove(subTasks.get(uid));
        }

        historyManager.remove(epics.get(epic.getId()));
        epics.put(epic.getId(), epic);
    }

    @Override
    public void updateSubtask(SubTask subTask) {
        historyManager.remove(subTasks.get(subTask.getId()));
        subTasks.put(subTask.getId(), subTask);

        historyManager.remove(epics.get(subTask.getEpicID()));
        Epic epic = epics.get(subTask.getEpicID());
        epic.setStatus(recalculateEpicStatus(epic));
    }

    @Override
    public void deleteTask(int uid) {
        historyManager.remove(tasks.get(uid));
        tasks.remove(uid);
    }

    @Override
    public void deleteSubtask(int uid) {
        historyManager.remove(subTasks.get(uid));
        int subTaskEpicUID = subTasks.get(uid).getEpicID();
        Epic epic = epics.get(subTaskEpicUID);

        epic.removeSubtask(uid);
        subTasks.remove(uid);

        epic.setStatus(recalculateEpicStatus(epic));
    }

    @Override
    public void deleteEpic(int uid) {
        for (int subTaskUID : epics.get(uid).getSubtasks()) {
            historyManager.remove(subTasks.get(subTaskUID));
            subTasks.remove(subTaskUID);
        }
        historyManager.remove(epics.get(uid));
        epics.remove(uid);
    }

    @Override
    public boolean hasTask(int id) {
        return tasks.containsKey(id);
    }

    @Override
    public boolean hasSubTask(int id) {
        return subTasks.containsKey(id);
    }

    @Override
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
