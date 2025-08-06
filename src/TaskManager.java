import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public interface TaskManager {

     ArrayList<Task> getTasks();
     ArrayList<SubTask> getSubTasks();
     ArrayList<Epic> getEpics();

     Optional<Task> getTask(int id);
     Optional<SubTask> getSubtask(int id);
     Optional<Epic> getEpic(int id);
     ArrayList<SubTask> getEpicSubtasks(int id);

     void resetTasks();
     void resetEpics();
     void resetSubtasks();

     void addTask(Task task);
     void addEpic(Epic epic);
     void addSubtask(SubTask subTask);

     void updateTask(Task task);
     void updateEpic(Epic epic);
     void updateSubtask(SubTask subTask);

     void deleteTask(int uid);
     void deleteEpic(int uid);
     void deleteSubtask(int uid);

     void deleteTasks();
     void deleteEpics();
     void deleteSubtasks();

     boolean hasTask(int id);
     boolean hasEpic(int id);
     boolean hasSubTask(int id);

     List<Task> getHistory();
     List<Task> getPrioritizedTasks();
     boolean areTasksTimeOverlapping(Task a,Task b);
     boolean areTaskOverLappingCheck(Task a,Boolean ignoreSelf);
}
