import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

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

public class HttpTaskManagersServerSubTasksHandlerTest {
    TaskManager manager = new InMemoryTaskManager();
    HttpTaskServer taskServer = new HttpTaskServer(manager);

    @BeforeEach
    public void setUp() throws IOException {
        manager.deleteTasks();
        manager.deleteSubtasks();
        manager.deleteEpics();
        Epic epic = new Epic(
                "test_1",
                "Testing test_1",
                0,
                TaskStatus.NEW,
                new ArrayList<>(),
                LocalDateTime.now(),
                Duration.ofMinutes(1)
        );
        manager.addEpic(epic);
        taskServer.start();
    }

    @AfterEach
    public void shutDown() {
        taskServer.stop();
    }

    @Test
    public void testSubtaskAdd() throws IOException, InterruptedException {
        SubTask subTask = new SubTask("subtask-1", "test subtask desc", 0,
                TaskStatus.NEW, LocalDateTime.now(), Duration.ofMinutes(1));
        subTask.setEpicID(0);

        String subtaskJSONString = subTask.toJSONIgnoringNotExposedValues();

        HttpResponse<String> response = postToServer(subtaskJSONString);

        assertEquals(201, response.statusCode());

        List<SubTask> subtasks = manager.getSubTasks();

        assertNotNull(subtasks, "Задачи не возвращаются");
        assertEquals(1, subtasks.size(), "Некорректное количество задач");
        assertEquals(subTask.getName(), subtasks.get(0).getName(), "Некорректное имя задачи");
    }

    @Test
    public void testSubtaskGet() throws IOException, InterruptedException {
        SubTask subTask = new SubTask("subtask-1", "test subtask desc", 0,
                TaskStatus.NEW, LocalDateTime.now(), Duration.ofMinutes(1));
        subTask.setEpicID(0);

        postToServer(subTask.toJSONIgnoringNotExposedValues());
        HttpResponse<String> response = getFromServerById("1");

        assertEquals(200, response.statusCode());
        JsonElement jsonElement = JsonParser.parseString(response.body());

        if (!jsonElement.isJsonObject()) { // проверяем, точно ли мы получили JSON-объект
            throw new Error("Ответ от сервера не соответствует ожидаемому.");
        }

        JsonObject jsonObject = jsonElement.getAsJsonObject();
        String name = jsonObject.get("name").getAsString();

        assertEquals(subTask.getName(), name);
    }

    @Test
    public void testSubtaskDelete() throws IOException, InterruptedException {
        SubTask subTask = new SubTask("subtask-1", "test subtask desc", 0,
                TaskStatus.NEW, LocalDateTime.now(), Duration.ofMinutes(1));
        subTask.setEpicID(0);


        postToServer(subTask.toJSONIgnoringNotExposedValues());
        HttpResponse<String> response = deleteFromServerById("1");
        assertEquals(200, response.statusCode());

        assertTrue(manager.getSubtask(0).isEmpty());
    }

    @Test

    public void testUpdateTask() throws IOException, InterruptedException {
        SubTask subTask = new SubTask("subtask-1", "test subtask desc", 1,
                TaskStatus.NEW, LocalDateTime.now(), Duration.ofMinutes(1));
        subTask.setEpicID(0);


        postToServer(subTask.toJSONIgnoringNotExposedValues());

        subTask.setName("test_1-changed");
        HttpResponse<String> response = postToServer(subTask.toJSON());
        assertEquals(201, response.statusCode());

        SubTask addedTask = manager.getSubtask(1).get();
        assertEquals(subTask.getName(), addedTask.getName());
    }


    @Test
    public void testSubtaskGetAll() throws IOException, InterruptedException {
        SubTask subTask_1 = new SubTask("subtask-1", "test subtask desc", 0,
                TaskStatus.NEW, LocalDateTime.now(), Duration.ofMinutes(1));
        SubTask subTask_2 = new SubTask("subtask-2", "test subtask desc", 1,
                TaskStatus.NEW, LocalDateTime.now().plusMinutes(2), Duration.ofMinutes(1));

        subTask_1.setEpicID(0);
        subTask_2.setEpicID(0);

        postToServer(subTask_1.toJSONIgnoringNotExposedValues());
        postToServer(subTask_2.toJSONIgnoringNotExposedValues());

        HttpResponse<String> response = getAllFromServer();
        assertEquals(200, response.statusCode());

        JsonElement jsonElement = JsonParser.parseString(response.body());

        if (!jsonElement.isJsonArray()) { // проверяем, точно ли мы получили JSON-объект
            throw new Error("Ответ от сервера не соответствует ожидаемому.");
        }

        JsonArray jsonArray = jsonElement.getAsJsonArray();
        String name_1 = jsonArray.get(0).getAsJsonObject().get("name").getAsString();
        String name_2 = jsonArray.get(1).getAsJsonObject().get("name").getAsString();


        assertEquals(2, manager.getSubTasks().size());

        assertEquals(subTask_1.getName(), name_1);
        assertEquals(subTask_2.getName(), name_2);
    }


    @Test
    public void testOverlap() throws IOException, InterruptedException {
        SubTask subTask_1 = new SubTask("subtask-1", "test subtask desc", 0,
                TaskStatus.NEW, LocalDateTime.now(), Duration.ofMinutes(1));
        SubTask subTask_2 = new SubTask("subtask-2", "test subtask desc", 1,
                TaskStatus.NEW, LocalDateTime.now().plusMinutes(0), Duration.ofMinutes(1));

        subTask_1.setEpicID(0);
        subTask_2.setEpicID(0);


        postToServer(subTask_1.toJSONIgnoringNotExposedValues());
        HttpResponse<String> response = postToServer(subTask_2.toJSONIgnoringNotExposedValues());

        assertEquals(406, response.statusCode());

        assertEquals(1, manager.getSubTasks().size());
    }

    private HttpResponse<String> postToServer(String taskJson) throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();
        System.out.println(taskJson);
        URI postTaskUri = URI.create("http://localhost:8080/subtasks");

        HttpRequest request = HttpRequest.newBuilder().uri(postTaskUri).POST(HttpRequest.BodyPublishers.ofString(taskJson)).build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        return response;
    }

    private HttpResponse<String> getFromServerById(String id) throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();

        URI postTaskUri = URI.create("http://localhost:8080/subtasks/" + id);

        HttpRequest request = HttpRequest.newBuilder().uri(postTaskUri).GET().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        return response;
    }

    private HttpResponse<String> deleteFromServerById(String id) throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();

        URI postTaskUri = URI.create("http://localhost:8080/subtasks/" + id);

        HttpRequest request = HttpRequest.newBuilder().uri(postTaskUri).DELETE().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        return response;
    }

    private HttpResponse<String> getAllFromServer() throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();

        URI postTaskUri = URI.create("http://localhost:8080/subtasks/");

        HttpRequest request = HttpRequest.newBuilder().uri(postTaskUri).GET().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        return response;
    }
}