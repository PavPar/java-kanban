import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.LocalDateTime;

class SubTaskTest {
    SubTask subTask;
    @BeforeEach
    public void reset(){
        subTask = new SubTask("test","test",1,TaskStatus.NEW, LocalDateTime.now(), Duration.ofMinutes(1));
    }

    @Test
    public void shouldCorrectlyCompareObjectsWithSameId() {
        SubTask subtaskA = new SubTask("test", "test", 1, TaskStatus.NEW, LocalDateTime.now(), Duration.ofMinutes(1));
        SubTask subtaskB = new SubTask("test", "test", 1, TaskStatus.NEW, LocalDateTime.now(), Duration.ofMinutes(1));

        Assertions.assertEquals(subtaskA, subtaskB);
    }

    @Test
    public void shouldNotBePossibleToAssignToSelf(){
        subTask = new SubTask("test","test",1,TaskStatus.NEW, LocalDateTime.now(), Duration.ofMinutes(1));
        subTask.setEpicID(1);
        Assertions.assertNotEquals(subTask.getId(),subTask.getEpicID());
    }
}