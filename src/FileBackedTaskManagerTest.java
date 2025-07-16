import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.util.ArrayList;

class FileBackedTaskManagerTest {
    FileBackedTaskManager manager;
    @BeforeEach
    public void reset(){
        manager = Managers.getFileBackendTaskManager();
        File file = new File("test.csv");
        file.delete();
    }



    @Test
    public void shouldStoreTask(){
        Task task = new Task("test","test",TaskStatus.NEW);
        task.setTaskId(1);
        File file = new File("test.csv");

        manager.addTask(task);
        ArrayList<String> lines = FileBackedTaskManager.loadFromFile(file);

        Assertions.assertNotNull(lines);
        Assertions.assertEquals(2, lines.size());
    }

    @Test
    public void shouldStoreMultipleTasks(){
        File file = new File("test.csv");
        Task task_1 = new Task("test","test",TaskStatus.NEW);
        Task task_2 = new Task("test","test",TaskStatus.NEW);

        task_1.setTaskId(1);
        task_2.setTaskId(2);

        manager.addTask(task_1);
        manager.addTask(task_2);

        ArrayList<String> lines = FileBackedTaskManager.loadFromFile(file);

        Assertions.assertNotNull(lines);
        Assertions.assertEquals(3, lines.size());
    }

}