import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

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
        SubTask subTask = new SubTask("test", "test", TaskStatus.NEW);

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
}