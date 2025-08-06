import com.google.gson.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.AfterEach;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class HttpTaskManagersServerTaskHandlerTest {
    TaskManager manager = new InMemoryTaskManager();
    HttpTaskServer taskServer = new HttpTaskServer(manager);

    @BeforeEach
    public void setUp() throws IOException {
        manager.deleteTasks();
        manager.deleteSubtasks();
        manager.deleteEpics();
        taskServer.start();
    }

    @AfterEach
    public void shutDown() {
        taskServer.stop();
    }

    @Test
    public void testAddTask() throws IOException, InterruptedException {
        Task task = new Task("test_1", "Testing test_1", TaskStatus.NEW, LocalDateTime.now(), Duration.ofMinutes(5));
        String taskJson = task.toJSONIgnoringNotExposedValues();

        HttpResponse<String> response = sendTaskToServer(taskJson);

        assertEquals(201, response.statusCode());

        List<Task> tasksFromManager = manager.getTasks();

        assertNotNull(tasksFromManager, "Задачи не возвращаются");
        assertEquals(1, tasksFromManager.size(), "Некорректное количество задач");
        assertEquals(task.getName(), tasksFromManager.get(0).getName(), "Некорректное имя задачи");
    }

    @Test

    public void testUpdateTask() throws IOException, InterruptedException {
        Task task = new Task("test_1", "Testing test_1", TaskStatus.NEW, LocalDateTime.now(), Duration.ofMinutes(5));
        String taskJson = task.toJSONIgnoringNotExposedValues();

        sendTaskToServer(taskJson);

        task.setName("test_1-changed");
        HttpResponse<String> response = sendTaskToServer(task.toJSON());
        assertEquals(201, response.statusCode());

        Task addedTask = manager.getTask(0).get();
        assertEquals(task.getName(), addedTask.getName());
    }

    @Test

    public void testNotFoundTask() throws IOException, InterruptedException {
        HttpResponse<String> response = getTaskFromServer("0");
        assertEquals(404, response.statusCode());
    }

    @Test
    public void testGetTask() throws IOException, InterruptedException {
        Task task = new Task("test_1", "Testing test_1", TaskStatus.NEW, LocalDateTime.now(), Duration.ofMinutes(5));
        String taskJson = task.toJSONIgnoringNotExposedValues();
        sendTaskToServer(taskJson);
        HttpResponse<String> response = getTaskFromServer("0");

        assertEquals(200, response.statusCode());
        JsonElement jsonElement = JsonParser.parseString(response.body());

        if (!jsonElement.isJsonObject()) { // проверяем, точно ли мы получили JSON-объект
            throw new Error("Ответ от сервера не соответствует ожидаемому.");
        }

        JsonObject jsonObject = jsonElement.getAsJsonObject();
        String name = jsonObject.get("name").getAsString();

        assertEquals(task.getName(), name);
    }

    @Test
    public void testDeleteTask() throws IOException, InterruptedException {
        Task task = new Task("test_1", "Testing test_1", TaskStatus.NEW, LocalDateTime.now(), Duration.ofMinutes(5));
        String taskJson = task.toJSONIgnoringNotExposedValues();
        sendTaskToServer(taskJson);
        HttpResponse<String> response = deleteTaskFromServer("0");
        assertEquals(200, response.statusCode());

        assertTrue(manager.getTask(0).isEmpty());
    }

    @Test
    public void testGetAllTasks() throws IOException, InterruptedException {
        Task task_1 = new Task("test_1", "Testing test_1", TaskStatus.NEW, LocalDateTime.now(), Duration.ofMinutes(1));
        Task task_2 = new Task("test_2", "Testing test_2", TaskStatus.NEW, LocalDateTime.now().plusMinutes(5), Duration.ofMinutes(1));
        String taskJson_1 = task_1.toJSONIgnoringNotExposedValues();
        String taskJson_2 = task_2.toJSONIgnoringNotExposedValues();

        sendTaskToServer(taskJson_1);
        sendTaskToServer(taskJson_2);

        HttpResponse<String> response = getAllTasksFromServer();
        assertEquals(200, response.statusCode());
        JsonElement jsonElement = JsonParser.parseString(response.body());

        if (!jsonElement.isJsonArray()) { // проверяем, точно ли мы получили JSON-объект
            throw new Error("Ответ от сервера не соответствует ожидаемому.");
        }

        JsonArray jsonArray = jsonElement.getAsJsonArray();
        String name_1 = jsonArray.get(0).getAsJsonObject().get("name").getAsString();
        String name_2 = jsonArray.get(1).getAsJsonObject().get("name").getAsString();

        System.out.println(manager.getTasks());
        assertEquals(2, manager.getTasks().size());

        assertEquals(task_1.getName(), name_1);
        assertEquals(task_2.getName(), name_2);
    }

    @Test
    public void testOverlap() throws IOException, InterruptedException {
        Task task_1 = new Task("test_1", "Testing test_1", TaskStatus.NEW, LocalDateTime.now(), Duration.ofMinutes(1));
        Task task_2 = new Task("test_2", "Testing test_2", TaskStatus.NEW, LocalDateTime.now().plusMinutes(1), Duration.ofMinutes(1));
        String taskJson_1 = task_1.toJSONIgnoringNotExposedValues();
        String taskJson_2 = task_2.toJSONIgnoringNotExposedValues();

        sendTaskToServer(taskJson_1);
        HttpResponse<String> response = sendTaskToServer(taskJson_2);

        assertEquals(406, response.statusCode());

        assertEquals(1, manager.getTasks().size());
    }


    private HttpResponse<String> sendTaskToServer(String taskJson) throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();

        URI postTaskUri = URI.create("http://localhost:8080/tasks");

        HttpRequest request = HttpRequest.newBuilder().uri(postTaskUri).POST(HttpRequest.BodyPublishers.ofString(taskJson)).build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        return response;
    }

    private HttpResponse<String> getTaskFromServer(String id) throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();

        URI postTaskUri = URI.create("http://localhost:8080/tasks/" + id);

        HttpRequest request = HttpRequest.newBuilder().uri(postTaskUri).GET().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        return response;
    }

    private HttpResponse<String> deleteTaskFromServer(String id) throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();

        URI postTaskUri = URI.create("http://localhost:8080/tasks/" + id);

        HttpRequest request = HttpRequest.newBuilder().uri(postTaskUri).DELETE().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        return response;
    }

    private HttpResponse<String> getAllTasksFromServer() throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();

        URI postTaskUri = URI.create("http://localhost:8080/tasks/");

        HttpRequest request = HttpRequest.newBuilder().uri(postTaskUri).GET().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        return response;
    }
}