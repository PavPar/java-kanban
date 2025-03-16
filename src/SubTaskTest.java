import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class SubTaskTest {
    SubTask subTask;
    @BeforeEach
    public void reset(){
        subTask = new SubTask("test","test",1,TaskStatus.NEW);
    }

    @Test
    public void shouldCorrectlyCompareObjectsWithSameId() {
        SubTask subtaskA = new SubTask("test", "test", 1, TaskStatus.NEW);
        SubTask subtaskB = new SubTask("test", "test", 1, TaskStatus.NEW);

        Assertions.assertEquals(subtaskA, subtaskB);
    }

    @Test
    public void shouldNotBePossibleToAssignToSelf(){
        subTask = new SubTask("test","test",1,TaskStatus.NEW);
        subTask.setEpicID(1);
        Assertions.assertNotEquals(subTask.getId(),subTask.getEpicID());
    }
}