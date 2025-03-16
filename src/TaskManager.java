import java.util.ArrayList;

public interface TaskManager {

     ArrayList<Task> getTasks();
     ArrayList<SubTask> getSubTasks();
     ArrayList<Epic> getEpics();

     Task getTask(int id);
     SubTask getSubtask(int id);
     Epic getEpic(int id);
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

     boolean hasTask(int id);
     boolean hasEpic(int id);
     boolean hasSubTask(int id);
}
