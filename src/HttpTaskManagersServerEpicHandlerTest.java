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
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class HttpTaskManagersServerEpicHandlerTest {
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
    public void testEpicNotFound() throws IOException, InterruptedException {
        HttpResponse<String> response = getFromServerById("0");
        assertEquals(404, response.statusCode());
    }

    @Test
    public void testEpicAdd() throws IOException, InterruptedException {
        Epic epic = new Epic(
                "test_1",
                "Testing test_1",
                0,
                TaskStatus.NEW,
                new ArrayList<>(),
                LocalDateTime.now(),
                Duration.ofMinutes(1)
        );

        String epicJson = epic.toJSONIgnoringNotExposedValues();
        HttpResponse<String> response = postToServer(epicJson);
        System.out.println(response.body());
        assertEquals(201, response.statusCode());

        List<Epic> tasksFromManager = manager.getEpics();

        assertNotNull(tasksFromManager, "Задачи не возвращаются");
        assertEquals(1, tasksFromManager.size(), "Некорректное количество задач");
        assertEquals(epic.getName(), tasksFromManager.get(0).getName(), "Некорректное имя задачи");
    }

    @Test
    public void testEpicGet() throws IOException, InterruptedException {
        Epic epic = new Epic(
                "test_1",
                "Testing test_1",
                0,
                TaskStatus.NEW,
                new ArrayList<>(),
                LocalDateTime.now(),
                Duration.ofMinutes(1)
        );

        String epicJSONString = epic.toJSONIgnoringNotExposedValues();
        postToServer(epicJSONString);
        HttpResponse<String> response = getFromServerById("0");

        assertEquals(200, response.statusCode());
        JsonElement jsonElement = JsonParser.parseString(response.body());

        if (!jsonElement.isJsonObject()) { // проверяем, точно ли мы получили JSON-объект
            throw new Error("Ответ от сервера не соответствует ожидаемому.");
        }

        JsonObject jsonObject = jsonElement.getAsJsonObject();
        String name = jsonObject.get("name").getAsString();

        assertEquals(epic.getName(), name);
    }

    @Test
    public void testEpicDelete() throws IOException, InterruptedException {
        Epic epic = new Epic(
                "test_1",
                "Testing test_1",
                0,
                TaskStatus.NEW,
                new ArrayList<>(),
                LocalDateTime.now(),
                Duration.ofMinutes(1)
        );

        String epicJSONString = epic.toJSONIgnoringNotExposedValues();
        postToServer(epicJSONString);
        HttpResponse<String> response = deleteFromServerById("0");
        assertEquals(200, response.statusCode());

        assertTrue(manager.getEpic(0).isEmpty());
    }

    @Test
    public void testEpicGetAll() throws IOException, InterruptedException {
        Epic epic_1 = new Epic(
                "test_2",
                "Testing test_1",
                0,
                TaskStatus.NEW,
                new ArrayList<>(),
                LocalDateTime.now(),
                Duration.ofMinutes(1)
        );
        Epic epic_2 = new Epic(
                "test_2",
                "Testing test_2",
                0,
                TaskStatus.NEW,
                new ArrayList<>(),
                LocalDateTime.now(),
                Duration.ofMinutes(1)
        );

        String epicJSONString_1 = epic_1.toJSONIgnoringNotExposedValues();
        String epicJSONString_2 = epic_2.toJSONIgnoringNotExposedValues();

        postToServer(epicJSONString_1);
        postToServer(epicJSONString_2);

        HttpResponse<String> response = getAllFromServer();
        assertEquals(200, response.statusCode());

        JsonElement jsonElement = JsonParser.parseString(response.body());

        if (!jsonElement.isJsonArray()) { // проверяем, точно ли мы получили JSON-объект
            throw new Error("Ответ от сервера не соответствует ожидаемому.");
        }

        JsonArray jsonArray = jsonElement.getAsJsonArray();
        String name_1 = jsonArray.get(0).getAsJsonObject().get("name").getAsString();
        String name_2 = jsonArray.get(1).getAsJsonObject().get("name").getAsString();


        assertEquals(2, manager.getEpics().size());

        assertEquals(epic_1.getName(), name_1);
        assertEquals(epic_2.getName(), name_2);
    }

    @Test
    public void testEpicGetSubtasks() throws IOException, InterruptedException {
        Epic epic = new Epic(
                "test_1",
                "Testing test_1",
                0,
                TaskStatus.NEW,
                new ArrayList<>(),
                LocalDateTime.now(),
                Duration.ofMinutes(1)
        );

        SubTask subTask_1 = new SubTask("subtask-1", "test subtask desc", 0,
                TaskStatus.NEW, LocalDateTime.now(), Duration.ofMinutes(1));
        SubTask subTask_2 = new SubTask("subtask-2", "test subtask desc", 1,
                TaskStatus.NEW, LocalDateTime.now().plusMinutes(2), Duration.ofMinutes(1));

        subTask_1.setEpicID(0);
        subTask_2.setEpicID(0);

        manager.addEpic(epic);
        manager.addSubtask(subTask_1);
        manager.addSubtask(subTask_2);

        HttpResponse<String> response = getSubtasksFromServer("0");
        assertEquals(200, response.statusCode());

        JsonElement jsonElement = JsonParser.parseString(response.body());

        if (!jsonElement.isJsonArray()) { // проверяем, точно ли мы получили JSON-объект
            throw new Error("Ответ от сервера не соответствует ожидаемому.");
        }

        JsonArray jsonArray = jsonElement.getAsJsonArray();
        String name_1 = jsonArray.get(0).getAsJsonObject().get("name").getAsString();
        String name_2 = jsonArray.get(1).getAsJsonObject().get("name").getAsString();


        assertEquals(subTask_1.getName(), name_1);
        assertEquals(subTask_2.getName(), name_2);
    }

    @Test

    public void testUpdateTask() throws IOException, InterruptedException {
        Epic epic = new Epic(
                "test_1",
                "Testing test_1",
                0,
                TaskStatus.NEW,
                new ArrayList<>(),
                LocalDateTime.now(),
                Duration.ofMinutes(1)
        );
        postToServer(epic.toJSONIgnoringNotExposedValues());

        epic.setEndTime(LocalDateTime.now());
        epic.setName("test_1-changed");
        HttpResponse<String> response = postToServer(epic.toJSON());
        assertEquals(201, response.statusCode());

        Epic addedTask = manager.getEpic(0).get();
        assertEquals(epic.getName(), addedTask.getName());
    }

    private HttpResponse<String> postToServer(String taskJson) throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();
        System.out.println(taskJson);
        URI postTaskUri = URI.create("http://localhost:8080/epics");

        HttpRequest request = HttpRequest.newBuilder().uri(postTaskUri).POST(HttpRequest.BodyPublishers.ofString(taskJson)).build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        return response;
    }

    private HttpResponse<String> getFromServerById(String id) throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();

        URI postTaskUri = URI.create("http://localhost:8080/epics/" + id);

        HttpRequest request = HttpRequest.newBuilder().uri(postTaskUri).GET().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        return response;
    }

    private HttpResponse<String> deleteFromServerById(String id) throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();

        URI postTaskUri = URI.create("http://localhost:8080/epics/" + id);

        HttpRequest request = HttpRequest.newBuilder().uri(postTaskUri).DELETE().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        return response;
    }

    private HttpResponse<String> getAllFromServer() throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();

        URI postTaskUri = URI.create("http://localhost:8080/epics/");

        HttpRequest request = HttpRequest.newBuilder().uri(postTaskUri).GET().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        return response;
    }

    private HttpResponse<String> getSubtasksFromServer(String id) throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();

        URI postTaskUri = URI.create("http://localhost:8080/epics/" + id + "/subtasks");

        HttpRequest request = HttpRequest.newBuilder().uri(postTaskUri).GET().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        return response;
    }
}