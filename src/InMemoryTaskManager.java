import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;

public class InMemoryTaskManager implements TaskManager {
    private int taskCounter = 0;
    private final HashMap<Integer, Task> tasks = new HashMap<>();
    private final HashMap<Integer, SubTask> subTasks = new HashMap<>();
    private final HashMap<Integer, Epic> epics = new HashMap<>();

    private final TreeSet<Task> tasksTreeMapByDate = new TreeSet<>(Comparator.comparing(((taskA) -> taskA.getStartTime())));

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
    public List<Task> getHistory() {
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
        if(Objects.nonNull(task.getStartTime())){
            tasksTreeMapByDate.add(task);
        }
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

        if(Objects.nonNull(subTask.getStartTime())){
            tasksTreeMapByDate.add(subTask);
        }

        recalculateEpic(epic);
    }

    @Override
    public ArrayList<Task> getPrioritizedTasks() {
        return new ArrayList<Task>(tasksTreeMapByDate);
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

        recalculateEpic(epic);
    }

    @Override
    public void deleteTask(int uid) {
        historyManager.remove(tasks.get(uid));
        tasksTreeMapByDate.remove(tasks.get(uid));
        tasks.remove(uid);
    }

    @Override
    public void deleteSubtask(int uid) {
        historyManager.remove(subTasks.get(uid));
        int subTaskEpicUID = subTasks.get(uid).getEpicID();
        Epic epic = epics.get(subTaskEpicUID);

        epic.removeSubtask(uid);
        tasksTreeMapByDate.remove(subTasks.get(uid));
        subTasks.remove(uid);

        recalculateEpic(epic);
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

    @Override
    public boolean areTasksTimeOverlapping(Task taskA, Task taskB) {
        LocalDateTime taskAStartTime = taskA.getStartTime();
        LocalDateTime taskAEndTime = taskA.getEndTime();

        LocalDateTime taskBStartTime = taskB.getStartTime();
        LocalDateTime taskBEndTime = taskB.getEndTime();


        return (taskAStartTime.isBefore(taskBStartTime) || taskAStartTime.isEqual(taskBStartTime)) && (taskAEndTime.isAfter(taskBStartTime) || taskAEndTime.isEqual(taskBStartTime)) ||
                (taskAStartTime.isBefore(taskBEndTime) || taskAStartTime.isEqual(taskBEndTime)) && (taskAEndTime.isAfter(taskBEndTime) || taskAEndTime.isEqual(taskBEndTime));

    }

    private int getNextTaskId() {
        return taskCounter++;
    }

    private void recalculateEpic(Epic epic){
        epic.setStatus(recalculateEpicStatus(epic));

        Optional<LocalDateTime> newStartTime = getEpicStartTime(epic);
        if (newStartTime.isPresent()) {
            epic.setStartTIme(newStartTime.get());

            Duration epicDuration = Duration.ofMinutes(getSubtasksTotalDuration(epic));

            epic.setEndTime(epic.getStartTime().plus(epicDuration));
            epic.setDuration(epicDuration);
        } else {
            epic.setStartTIme(null);
            epic.setEndTime(null);
            epic.setDuration(Duration.ofMinutes(0));
        }
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

    private Long getSubtasksTotalDuration(Epic epic) {
        return epic.getSubtasks().stream()
                .map(subTasks::get)
                .filter(Objects::nonNull)
                .map((s) -> s.getDuration().toMinutes())
                .reduce(0L, Long::sum);
    }

    private Optional<LocalDateTime> getEpicStartTime(Epic epic) {
        return epic.getSubtasks().stream()
                .map(subTasks::get)
                .filter(Objects::nonNull)
                .map(Task::getStartTime)
                .sorted()
                .findFirst();
    }


}
