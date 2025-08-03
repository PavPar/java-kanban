import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class InMemoryTaskManagerTest {
    TaskManager taskManager;

    @BeforeEach
    public void reset() {
        taskManager = Managers.getDefault();
    }

    @Test
    public void shouldAddTask() {
        Task task = new Task("test", "test", TaskStatus.NEW);

        taskManager.addTask(task);
        Task addedTask = taskManager.getTask(0);

        Assertions.assertNotNull(addedTask);
    }

    @Test
    public void shouldAddEpic() {
        Epic epic = new Epic("test", "test", TaskStatus.NEW);

        taskManager.addEpic(epic);
        Task addedEpic = taskManager.getEpic(0);

        Assertions.assertNotNull(addedEpic);
    }

    @Test
    public void shouldAddSubtask() {
        Epic epic = new Epic("test", "test", TaskStatus.NEW);
        SubTask subTask = new SubTask("test", "test", TaskStatus.NEW, LocalDateTime.now(), Duration.ofMinutes(1));

        subTask.setEpicID(epic.getId());

        taskManager.addEpic(epic);
        taskManager.addSubtask(subTask);
        Task addedSubTask = taskManager.getSubtask(1);

        Assertions.assertNotNull(addedSubTask);
    }

    /*
    проверьте, что задачи с заданным id и сгенерированным id не
    конфликтуют внутри менеджера;
     */
    @Test
    public void shouldNotBeIdConflict() {
        Task taskA = new Task("testA", "test", 1, TaskStatus.NEW);
        Task taskB = new Task("testB", "test", 1, TaskStatus.NEW);

        taskManager.addTask(taskA);
        taskManager.addTask(taskB);

        Task addedTaskA = taskManager.getTask(0);
        Task addedTaskB = taskManager.getTask(1);

        Assertions.assertFalse(addedTaskA.getName().equals(addedTaskB.getName()));
    }

    /*
    создайте тест, в котором проверяется
    неизменность задачи (по всем полям) при добавлении задачи в менеджер
     */
    @Test
    public void shouldRemainSameTaskAfterBeingAddedToManager(){
        Task task = new Task("test","test",TaskStatus.NEW);

        taskManager.addTask(task);
        Task addedTask = taskManager.getTask(0);

        Assertions.assertEquals(addedTask.getName(),task.getName());
        Assertions.assertEquals(addedTask.getDescription(),task.getDescription());
        Assertions.assertEquals(addedTask.getStatus(),task.getStatus());
    }

    @Test
    public void shouldChangeEpicStatusOnNewSubtask(){
        Epic epic = new Epic("test","test",TaskStatus.NEW);

        SubTask subtask_1 = new SubTask("test","test",TaskStatus.IN_PROGRESS,LocalDateTime.now(),Duration.ofMinutes(1));

        subtask_1.setTaskId(epic.getId());

        taskManager.addEpic(epic);
        taskManager.addSubtask(subtask_1);

        Assertions.assertEquals(TaskStatus.IN_PROGRESS, taskManager.getEpics().getFirst().getStatus());
    }

    @Test
    public void shouldChangeEpicStatusOnDoneSubtask(){
        Epic epic = new Epic("test","test",TaskStatus.NEW);

        SubTask subtask_1 = new SubTask("test","test",TaskStatus.DONE,LocalDateTime.now(),Duration.ofMinutes(1));

        subtask_1.setTaskId(epic.getId());

        taskManager.addEpic(epic);
        taskManager.addSubtask(subtask_1);

        Assertions.assertEquals(TaskStatus.DONE, taskManager.getEpics().getFirst().getStatus());
    }

    @Test
    public void shouldNotChangeEpicStatus(){
        Epic epic = new Epic("test","test",TaskStatus.NEW);

        SubTask subtask_1 = new SubTask("test","test",TaskStatus.NEW,LocalDateTime.now(),Duration.ofMinutes(1));
        SubTask subtask_2 = new SubTask("test","test",TaskStatus.NEW,LocalDateTime.now(),Duration.ofMinutes(1));

        subtask_1.setTaskId(epic.getId());
        subtask_2.setTaskId(epic.getId());

        taskManager.addEpic(epic);
        taskManager.addSubtask(subtask_1);
        taskManager.addSubtask(subtask_2);

        Assertions.assertEquals(TaskStatus.NEW, taskManager.getEpics().getFirst().getStatus());
    }

    @Test
    public void shouldChangeToProgress(){
        Epic epic = new Epic("test","test",TaskStatus.NEW);

        SubTask subtask_1 = new SubTask("test","test",TaskStatus.NEW,LocalDateTime.now(),Duration.ofMinutes(1));
        SubTask subtask_2 = new SubTask("test","test",TaskStatus.DONE,LocalDateTime.now(),Duration.ofMinutes(1));

        subtask_1.setTaskId(epic.getId());
        subtask_2.setTaskId(epic.getId());

        taskManager.addEpic(epic);
        taskManager.addSubtask(subtask_1);
        taskManager.addSubtask(subtask_2);

        Assertions.assertEquals(TaskStatus.IN_PROGRESS, taskManager.getEpics().getFirst().getStatus());
    }

    @Test
    public void shouldChangeEpicToDone(){
        Epic epic = new Epic("test","test",TaskStatus.NEW);

        SubTask subtask_1 = new SubTask("test","test",TaskStatus.DONE,LocalDateTime.now(),Duration.ofMinutes(1));
        SubTask subtask_2 = new SubTask("test","test",TaskStatus.DONE,LocalDateTime.now(),Duration.ofMinutes(1));

        subtask_1.setTaskId(epic.getId());
        subtask_2.setTaskId(epic.getId());

        taskManager.addEpic(epic);
        taskManager.addSubtask(subtask_1);
        taskManager.addSubtask(subtask_2);

        Assertions.assertEquals(TaskStatus.DONE, taskManager.getEpics().getFirst().getStatus());
    }

    @Test
    public void shouldChangeEpicToNew(){
        Epic epic = new Epic("test","test",TaskStatus.NEW);

        SubTask subtask_1 = new SubTask("test","test",TaskStatus.DONE,LocalDateTime.now(),Duration.ofMinutes(1));
        SubTask subtask_2 = new SubTask("test","test",TaskStatus.DONE,LocalDateTime.now(),Duration.ofMinutes(1));

        subtask_1.setTaskId(epic.getId());
        subtask_2.setTaskId(epic.getId());

        taskManager.addEpic(epic);
        taskManager.addSubtask(subtask_1);
        taskManager.addSubtask(subtask_2);

        taskManager.deleteSubtask(subtask_1.getId());
        taskManager.deleteSubtask(subtask_2.getId());

        Assertions.assertEquals(TaskStatus.NEW, taskManager.getEpics().getFirst().getStatus());
    }

    @Test
    public void shouldChangeEpicToDoneIfSubtaskDeleted(){
        Epic epic = new Epic("test","test",TaskStatus.NEW);

        SubTask subtask_1 = new SubTask("test","test",TaskStatus.IN_PROGRESS,LocalDateTime.now(),Duration.ofMinutes(1));
        SubTask subtask_2 = new SubTask("test","test",TaskStatus.DONE,LocalDateTime.now(),Duration.ofMinutes(1));

        subtask_1.setTaskId(epic.getId());
        subtask_2.setTaskId(epic.getId());

        taskManager.addEpic(epic);
        taskManager.addSubtask(subtask_1);
        taskManager.addSubtask(subtask_2);

        taskManager.deleteSubtask(subtask_1.getId());

        Assertions.assertEquals(TaskStatus.DONE, taskManager.getEpics().getFirst().getStatus());
    }
}