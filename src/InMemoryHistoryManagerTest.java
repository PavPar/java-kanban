import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

class InMemoryHistoryManagerTest {
    HistoryManager manager;
    @BeforeEach
    public void reset(){
        manager = Managers.getDefaultHistory();
    }

    @Test
    public void shouldStoreTask(){
        Task task = new Task("test","test",TaskStatus.NEW);
        task.setTaskId(1);

        manager.add(task);

        ArrayList<Task> tasks = manager.getHistory();
        Assertions.assertTrue(tasks.contains(task));
    }

    @Test
    public void shouldStoreMultipleTasks(){
        Task task_1 = new Task("test","test",TaskStatus.NEW);
        Task task_2 = new Task("test","test",TaskStatus.NEW);

        task_1.setTaskId(1);
        task_2.setTaskId(2);

        manager.add(task_1);
        manager.add(task_2);

        ArrayList<Task> tasks = manager.getHistory();
        Assertions.assertTrue(tasks.contains(task_1));
        Assertions.assertTrue(tasks.contains(task_2));
    }

    @Test
    public void shouldNotHaveDuplicatesOfTasks(){
        Task task_1 = new Task("test","test",TaskStatus.NEW);
        Task task_1_clone = new Task("test","test",TaskStatus.NEW);

        task_1.setTaskId(1);
        task_1_clone.setTaskId(1);

        manager.add(task_1);
        manager.add(task_1_clone);

        ArrayList<Task> tasks = manager.getHistory();

        Assertions.assertEquals(1, tasks.size());
    }

    @Test
    public void shouldRemoveTaskFromArray(){
        Task task = new Task("test","test",TaskStatus.NEW);
        task.setTaskId(1);
        manager.add(task);

        manager.remove(task);
        ArrayList<Task> tasks = manager.getHistory();


        Assertions.assertFalse(tasks.contains(task));
        Assertions.assertEquals(0,tasks.size());
    }

    @Test
    public void shouldRemoveOnlyTaskThatWasProvided(){
        Task task_1 = new Task("test","test",TaskStatus.NEW);
        Task task_2 = new Task("test","test",TaskStatus.NEW);

        task_1.setTaskId(1);
        task_2.setTaskId(2);

        manager.add(task_1);
        manager.add(task_2);

        manager.remove(task_1);
        ArrayList<Task> tasks = manager.getHistory();

        Assertions.assertFalse(tasks.contains(task_1));
        Assertions.assertTrue(tasks.contains(task_2));
    }

    @Test
    public void shouldHaveCorrectOrder(){
        Task task_1 = new Task("test","test",TaskStatus.NEW);
        Task task_2 = new Task("test","test",TaskStatus.NEW);

        task_1.setTaskId(1);
        task_2.setTaskId(2);

        manager.add(task_1);
        manager.add(task_2);

        ArrayList<Task> tasks = manager.getHistory();

        Assertions.assertEquals(0,tasks.indexOf(task_2));
        Assertions.assertEquals(1,tasks.indexOf(task_1));
    }

    @Test
    public void shouldBubbleTaskToTopAfterAdd(){
        Task task_1 = new Task("test","test",TaskStatus.NEW);
        Task task_2 = new Task("test","test",TaskStatus.NEW);
        Task task_3 = new Task("test","test",TaskStatus.NEW);

        task_1.setTaskId(1);
        task_2.setTaskId(2);
        task_3.setTaskId(3);

        manager.add(task_1);
        manager.add(task_2);
        manager.add(task_3);

        manager.add(task_1);

        ArrayList<Task> tasks = manager.getHistory();

        Assertions.assertEquals(0,tasks.indexOf(task_1));
        Assertions.assertEquals(1,tasks.indexOf(task_3));
        Assertions.assertEquals(2,tasks.indexOf(task_2));
    }


}