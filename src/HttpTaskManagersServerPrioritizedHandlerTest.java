import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
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

import static org.junit.jupiter.api.Assertions.assertEquals;

public class HttpTaskManagersServerPrioritizedHandlerTest {
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
    public void testGetEmptyPrioritized() throws IOException, InterruptedException {
        HttpResponse<String> response = getFromServer();

        assertEquals(200, response.statusCode());

    }

    @Test
    public void testGetPrioritized() throws IOException, InterruptedException {
        Task task_1 = new Task("test_1", "Testing test_1", TaskStatus.NEW, LocalDateTime.now(), Duration.ofMinutes(5));
        Task task_2 = new Task("test_1", "Testing test_1", TaskStatus.NEW, LocalDateTime.now().plusMinutes(6), Duration.ofMinutes(5));

        manager.addTask(task_1);
        manager.addTask(task_2);

        manager.getTask(0);
        manager.getTask(1);

        HttpResponse<String> response = getFromServer();
        assertEquals(200, response.statusCode());

        JsonElement jsonElement = JsonParser.parseString(response.body());

        if (!jsonElement.isJsonArray()) { // проверяем, точно ли мы получили JSON-объект
            throw new Error("Ответ от сервера не соответствует ожидаемому.");
        }

        JsonArray jsonArray = jsonElement.getAsJsonArray();
        String name_1 = jsonArray.get(0).getAsJsonObject().get("name").getAsString();
        String name_2 = jsonArray.get(1).getAsJsonObject().get("name").getAsString();

        assertEquals(task_1.getName(), name_1);
        assertEquals(task_2.getName(), name_2);
    }

    private HttpResponse<String> getFromServer() throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();

        URI postTaskUri = URI.create("http://localhost:8080/prioritized");

        HttpRequest request = HttpRequest.newBuilder().uri(postTaskUri).GET().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        return response;
    }
}