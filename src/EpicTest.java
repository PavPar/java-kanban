import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

class EpicTest {
    Epic epic;

    @BeforeEach
    public void populate() {
        epic = new Epic("test", "test", 1,TaskStatus.NEW,new ArrayList<>());
    }

    @Test
    public void shouldCreateEpicWithPredefinedSubtasks() {
        ArrayList<Integer> subtasks = new ArrayList<Integer>();
        subtasks.add(1);
        subtasks.add(2);

        Epic epicPredefined = new Epic("test","test",1,TaskStatus.NEW,subtasks);

        ArrayList<Integer> epicSubtasks = epicPredefined.getSubtasks();
        boolean hasAllSubtasks = epicSubtasks.contains(subtasks.get(0)) && epicSubtasks.contains(subtasks.get(1));
        Assertions.assertTrue(hasAllSubtasks);
    }

    @Test
    public void shouldCreateEpicWithPredefinedId() {
        ArrayList<Integer> subtasks = new ArrayList<Integer>();
        subtasks.add(1);
        subtasks.add(2);

        Epic epicPredefined = new Epic("test","test",1,TaskStatus.NEW,subtasks);

        Assertions.assertEquals(1, epicPredefined.getId());
    }

    @Test
    public void shouldChangeStatus() {
        epic.setStatus(TaskStatus.IN_PROGRESS);
        Assertions.assertEquals(TaskStatus.IN_PROGRESS, epic.getStatus());
    }

    @Test
    public void shouldAddSubtaskId() {
        epic.addSubtask(2);
        Assertions.assertTrue(epic.getSubtasks().contains(2));
    }

    @Test
    public void shouldRemoveCorrectSubtaskId() {
        epic.addSubtask(1);
        epic.addSubtask(2);
        epic.removeSubtask(1);
        Assertions.assertTrue(epic.getSubtasks().contains(2) && epic.getSubtasks().size() == 1);
    }

    @Test
    public void shouldCorrectlyCompareObjectsWithSameId(){
        Epic epicA = new Epic("test","test",1,TaskStatus.NEW,new ArrayList<Integer>());
        Epic epicB = new Epic("test","test",1,TaskStatus.NEW,new ArrayList<Integer>());

        Assertions.assertEquals(epicA,epicB);
    }

    @Test
    public void shouldNotBePossibleToAssignToSelf(){
        epic.addSubtask(epic.getId());
        Assertions.assertEquals(0,epic.getSubtasks().size());
    }
}