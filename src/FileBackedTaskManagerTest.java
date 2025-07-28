import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;

class FileBackedTaskManagerTest {
    FileBackedTaskManager manager;

    @BeforeEach
    public void reset() {
        manager = Managers.getFileBackendTaskManager();
        File file = new File("test.csv");
        file.delete();
    }


    @Test
    public void shouldStoreTask() {
        Task task = new Task("test", "test", TaskStatus.NEW);

        File file = new File("test.csv");

        manager.addTask(task);
        FileBackedTaskManager manager = FileBackedTaskManager.loadFromFile(file);

        ArrayList<Task> tasks = manager.getTasks();

        Assertions.assertEquals(1, tasks.size());
        Assertions.assertEquals(0, tasks.getFirst().getId());
    }

    @Test
    public void shouldStoreEpic() {
        Epic epic = new Epic("test", "test", TaskStatus.NEW);

        File file = new File("test.csv");

        manager.addEpic(epic);
        FileBackedTaskManager manager = FileBackedTaskManager.loadFromFile(file);

        ArrayList<Epic> tasks = manager.getEpics();

        Assertions.assertEquals(1, tasks.size());
        Assertions.assertEquals(0, tasks.getFirst().getId());
    }

    @Test
    public void shouldStoreEpicAndSubtask() {
        Epic epic = new Epic("test", "test", TaskStatus.NEW);
        SubTask subTask = new SubTask("test", "test", TaskStatus.NEW, LocalDateTime.now(), Duration.ofMinutes(10));

        File file = new File("test.csv");

        manager.addEpic(epic);
        manager.addSubtask(subTask);

        subTask.setEpicID(epic.getId());
        epic.addSubtask(subTask.getId());

        FileBackedTaskManager manager = FileBackedTaskManager.loadFromFile(file);

        ArrayList<Epic> epics = manager.getEpics();
        ArrayList<SubTask> subTasks = manager.getSubTasks();

        Assertions.assertEquals(1, epics.size());
        Assertions.assertEquals(1, subTasks.size());

        Assertions.assertEquals(0, epics.getFirst().getId());
        Assertions.assertEquals(1, subTasks.getFirst().getId());

        Assertions.assertEquals(1, epics.getFirst().getSubtasks().getFirst());
        Assertions.assertEquals(0, subTasks.getFirst().getEpicID());
    }


    @Test
    public void shouldStoreMultipleEpicsAndSubtasks() throws IOException {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder
                .append("id,type,name,status,description,startDate,duration,epic").append("\n")
                .append("0,EPIC,1,NEW,1,10.01.2000 00:00,50,").append("\n")
                .append("1,EPIC,1,NEW,1,05.01.2000 10:00,112,").append("\n")
                .append("2,EPIC,1,NEW,1,null,0,").append("\n")
                .append("3,SUBTASK,1,NEW,1,10.01.2000 00:00,50,0").append("\n")
                .append("4,SUBTASK,1,NEW,1,10.01.2000 10:00,100,1").append("\n")
                .append("5,SUBTASK,1,NEW,1,05.01.2000 10:00,12,1").append("\n");

        File file = File.createTempFile("test_1", ".csv");
        FileWriter wr = new FileWriter(file.getPath());
        wr.append(stringBuilder.toString());
        wr.close();

        FileBackedTaskManager manager = FileBackedTaskManager.loadFromFile(file);

        ArrayList<Epic> epics = manager.getEpics();
        ArrayList<SubTask> subTasks = manager.getSubTasks();

        Assertions.assertEquals(3, epics.size());
        Assertions.assertEquals(3, subTasks.size());

        Assertions.assertEquals(0, epics.get(0).getId());
        Assertions.assertEquals(1, epics.get(1).getId());
        Assertions.assertEquals(2, epics.get(2).getId());

        Assertions.assertEquals(3, subTasks.get(0).getId());
        Assertions.assertEquals(4, subTasks.get(1).getId());
        Assertions.assertEquals(5, subTasks.get(2).getId());

        Assertions.assertEquals(subTasks.get(0).getId(), epics.get(0).getSubtasks().getFirst());

        Assertions.assertEquals(subTasks.get(1).getId(), epics.get(1).getSubtasks().get(0));
        Assertions.assertEquals(subTasks.get(2).getId(), epics.get(1).getSubtasks().get(1));

    }

    @Test
    public void shouldStoreMultipleTasks() {
        File file = new File("test.csv");
        Task task_1 = new Task("test", "test", TaskStatus.NEW);
        Task task_2 = new Task("test", "test", TaskStatus.NEW);

        task_1.setTaskId(1);
        task_2.setTaskId(2);

        manager.addTask(task_1);
        manager.addTask(task_2);

        FileBackedTaskManager manager = FileBackedTaskManager.loadFromFile(file);

        ArrayList<Task> tasks = manager.getTasks();

        Assertions.assertEquals(2, tasks.size());
        Assertions.assertEquals(0, tasks.get(0).getId());
        Assertions.assertEquals(1, tasks.get(1).getId());
    }
}