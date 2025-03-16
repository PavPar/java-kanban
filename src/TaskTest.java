import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

class TaskTest {
    @Test
    public void shouldCorrectlyCompareObjectsWithSameId() {
        Task taskA = new Task("test", "test", 1, TaskStatus.NEW);
        Task taskB = new Task("test", "test", 1, TaskStatus.NEW);

        Assertions.assertEquals(taskA, taskB);
    }
}