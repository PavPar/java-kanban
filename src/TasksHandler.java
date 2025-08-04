import com.google.gson.*;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;

public class TasksHandler extends BaseHttpHandler implements HttpHandler {
    TaskManager manager;

    TasksHandler(TaskManager manager) {
        this.manager = manager;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String method = exchange.getRequestMethod();
        try {
            switch (method) {
                case "GET":
                    handleGETMethod(exchange);
                    break;
                case "POST":
                    handlePOSTMethod(exchange);
                    break;
                case "DELETE":
                    handleDELETEMethod(exchange);
                    break;
                default:
                    this.sendNotFound(exchange);
            }
        } catch (UnknownURIPathException | NotFoundException e) {
            this.sendNotFound(exchange, e.getMessage());
        } catch (IncorectURIPathException e) {
            this.sendBadType(exchange, e.getMessage());
        } catch (Throwable e) {
            this.sendFatalServerError(exchange, e.toString());
        }
    }

    private void handleGETMethod(HttpExchange exchange) throws IOException, UnknownURIPathException, IncorectURIPathException {
        String uri = exchange.getRequestURI().toString();
        String[] uriParts = uri.split("/");

        if (uriParts.length == 2) {
            ArrayList<Task> tasks = this.manager.getTasks();

            Gson gson = new GsonBuilder()
                    .serializeNulls()
                    .setPrettyPrinting()
                    .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
                    .registerTypeAdapter(Duration.class, new DurationAdapter())
                    .create();


            this.sendText(exchange,gson.toJson(tasks));
            return;
        }

        if (uriParts.length == 3) {
            int id = -1;
            try {
                id = Integer.parseInt(uriParts[2]);
            } catch (NumberFormatException e) {
                throw new IncorectURIPathException("Неправильный тип в пути" + uri);
            }

            Optional<Task> task = this.manager.getTask(id);

            if (task.isPresent()) {
                this.sendText(exchange, task.get().toJSON());
                return;
            }
            throw new NotFoundException(String.format("Задача с номером %s не найдена", id));
        }

        throw new UnknownURIPathException("Неизвестный путь" + uri);
    }

    private void handlePOSTMethod(HttpExchange exchange) throws IOException {
        String uri = exchange.getRequestURI().toString();

        try {
            InputStream inputStream = exchange.getRequestBody();
            String jsonBody = new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);

            Task parsedTask = Task.fromJSON(jsonBody);


            if (getIdFromBody(jsonBody) < 0) {
                if (this.manager.areTaskOverLappingCheck(parsedTask,false)) {
                    throw new TaskTimeOverlappingException("Пересечение времени Task");
                }

                this.manager.addTask(parsedTask);
            } else {
                if (this.manager.areTaskOverLappingCheck(parsedTask,true)) {
                    throw new TaskTimeOverlappingException("Пересечение времени Task");
                }
                if(!this.manager.hasTask(parsedTask.getId())){
                    throw new NotFoundException(String.format("Задача с номером %s не найдена", parsedTask.getId()));
                }
                this.manager.updateTask(parsedTask);
            }

            this.sendCreated(exchange);
        } catch (JsonSyntaxException e) {
            this.sendBadType(exchange, e.toString());
        }catch (TaskTimeOverlappingException e){
            this.sendHasOverlaps(exchange);
        }catch (NotFoundException e){
            this.sendNotFound(exchange,e.getMessage());
        }
    }

    private void handleDELETEMethod(HttpExchange exchange) throws IOException {
        String uri = exchange.getRequestURI().toString();
        String[] uriParts = uri.split("/");

        if (uriParts.length == 3) {
            int id = -1;
            try {
                id = Integer.parseInt(uriParts[2]);
            } catch (NumberFormatException e) {
                throw new IncorectURIPathException("Неправильный тип в пути" + uri);
            }

            try {
                if(!this.manager.hasTask(id)){
                    throw new NotFoundException(String.format("Задача с номером %s не найдена", id));
                }

                this.manager.deleteTask(id);

                this.sendOk(exchange);
            }catch (NotFoundException e){
                this.sendNotFound(exchange,e.getMessage());
            }

        }

        throw new UnknownURIPathException("Неизвестный путь" + uri);
    }

    private int getIdFromBody(String jsonBody) {
        JsonElement jsonElement = JsonParser.parseString(jsonBody);
        if (!jsonElement.isJsonObject()) {
            throw new JsonSyntaxException("Не объект");
        }

        try {
            return jsonElement.getAsJsonObject().get("id").getAsInt();
        } catch (NullPointerException e) {
            return -1;
        }
    }
}
