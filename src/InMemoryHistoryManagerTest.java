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

}