import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

 class TaskManagerTest<T extends TaskManager>{
    TaskManager taskManager;
    final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm");
    @BeforeEach
    public void reset() {
        taskManager = Managers.getDefault();
    }

    @Test
    public void shouldCorrectlyCheckIntervalTest_1() {
        LocalDateTime timeA =  LocalDateTime.parse("01.01.2000 10:00",formatter);
        Duration durationA = Duration.ofMinutes(5);

        LocalDateTime timeB =  LocalDateTime.parse("01.01.2000 10:00",formatter);
        Duration durationB = Duration.ofMinutes(5);

        Task task_A = new Task("test", "test", TaskStatus.NEW,timeA,durationA);
        Task task_B = new Task("test", "test", TaskStatus.NEW,timeB,durationB);

        Assertions.assertTrue(taskManager.areTasksTimeOverlapping(task_A,task_B));
    }

    @Test
    public void shouldCorrectlyCheckIntervalTest_2() {
        LocalDateTime timeA =  LocalDateTime.parse("01.01.2000 10:00",formatter);
        Duration durationA = Duration.ofMinutes(5);

        LocalDateTime timeB =  LocalDateTime.parse("01.01.2000 11:00",formatter);
        Duration durationB = Duration.ofMinutes(5);

        Task task_A = new Task("test", "test", TaskStatus.NEW,timeA,durationA);
        Task task_B = new Task("test", "test", TaskStatus.NEW,timeB,durationB);

        Assertions.assertFalse(taskManager.areTasksTimeOverlapping(task_A,task_B));
    }

    @Test
    public void shouldCorrectlyCheckIntervalTest_3() {
        LocalDateTime timeA =  LocalDateTime.parse("01.01.2000 11:00",formatter);
        Duration durationA = Duration.ofMinutes(5);

        LocalDateTime timeB =  LocalDateTime.parse("01.01.2000 10:00",formatter);
        Duration durationB = Duration.ofMinutes(60);

        Task task_A = new Task("test", "test", TaskStatus.NEW,timeA,durationA);
        Task task_B = new Task("test", "test", TaskStatus.NEW,timeB,durationB);

        Assertions.assertTrue(taskManager.areTasksTimeOverlapping(task_A,task_B));
    }

    @Test
    public void shouldCorrectlyCheckIntervalTest_4() {
        LocalDateTime timeA =  LocalDateTime.parse("01.01.2000 10:00",formatter);
        Duration durationA = Duration.ofMinutes(60);

        LocalDateTime timeB =  LocalDateTime.parse("01.01.2000 11:00",formatter);
        Duration durationB = Duration.ofMinutes(5);

        Task task_A = new Task("test", "test", TaskStatus.NEW,timeA,durationA);
        Task task_B = new Task("test", "test", TaskStatus.NEW,timeB,durationB);

        Assertions.assertTrue(taskManager.areTasksTimeOverlapping(task_A,task_B));
    }
}