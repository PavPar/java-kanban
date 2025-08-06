import com.google.gson.*;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;

public class SubTasksHandler extends BaseHttpHandler implements HttpHandler {
    TaskManager manager;

    SubTasksHandler(TaskManager manager) {
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
            ArrayList<SubTask> subTasks = this.manager.getSubTasks();

            Gson gson = new GsonBuilder()
                    .serializeNulls()
                    .setPrettyPrinting()
                    .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
                    .registerTypeAdapter(Duration.class, new DurationAdapter())
                    .create();


            this.sendText(exchange, gson.toJson(subTasks));
            return;
        }

        if (uriParts.length == 3) {
            int id = -1;
            try {
                id = Integer.parseInt(uriParts[2]);
            } catch (NumberFormatException e) {
                throw new IncorectURIPathException("Неправильный тип в пути" + uri);
            }

            Optional<SubTask> subtask = this.manager.getSubtask(id);

            if (subtask.isPresent()) {
                this.sendText(exchange, subtask.get().toJSON());
                return;
            }
            throw new NotFoundException(String.format("Задача с номером %s не найдена", id));
        }

        throw new UnknownURIPathException("Неизвестный путь" + uri);
    }

    private void handlePOSTMethod(HttpExchange exchange) throws IOException {
        String uri = exchange.getRequestURI().toString();
        String[] uriParts = uri.split("/");

        if (uriParts.length > 2) {
            throw new UnknownURIPathException("Неизвестный путь" + uri);
        }

        try {
            InputStream inputStream = exchange.getRequestBody();
            String jsonBody = new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);

            if (!hasNecessaryFields(jsonBody)) {
                throw new BadDataException("Некорректные данные");
            }

            SubTask parsedSubTask = SubTask.fromJSON(jsonBody);

            if (getIdFieldFromBody(jsonBody, "id") < 0 || getIdFieldFromBody(jsonBody, "epicID") < 0) {
                if (this.manager.areTaskOverLappingCheck(parsedSubTask, false)) {
                    throw new TaskTimeOverlappingException("Пересечение времени Subtask");
                }

                if (!this.manager.hasEpic(parsedSubTask.getEpicID())) {
                    throw new NotFoundException("Нет эпика с id " + parsedSubTask.getEpicID());
                }

                this.manager.addSubtask(parsedSubTask);
            } else {
                if (!this.manager.hasSubTask(parsedSubTask.getId())) {
                    throw new NotFoundException(String.format("Задача с номером %s не найдена", parsedSubTask.getId()));
                }

                if (this.manager.areTaskOverLappingCheck(parsedSubTask, true)) {
                    throw new TaskTimeOverlappingException("Пересечение времени Subtask");
                }

                if (!this.manager.hasEpic(parsedSubTask.getEpicID())) {
                    throw new NotFoundException("Нет эпика с id " + parsedSubTask.getEpicID());
                }


                this.manager.updateSubtask(parsedSubTask);
            }

            this.sendCreated(exchange);
        } catch (JsonSyntaxException e) {
            this.sendBadType(exchange, e.toString());
        } catch (TaskTimeOverlappingException e) {
            this.sendHasOverlaps(exchange);
        } catch (NotFoundException e) {
            this.sendNotFound(exchange, e.getMessage());
        } catch (BadDataException e) {
            this.sendBadType(exchange, e.getMessage());
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
                if (!this.manager.hasSubTask(id)) {
                    throw new NotFoundException(String.format("Задача с номером %s не найдена", id));
                }

                this.manager.deleteSubtask(id);

                this.sendOk(exchange);
            } catch (NotFoundException e) {
                this.sendNotFound(exchange, e.getMessage());
            }

        }

        throw new UnknownURIPathException("Неизвестный путь" + uri);
    }

    private int getIdFieldFromBody(String jsonBody, String field) {
        JsonElement jsonElement = JsonParser.parseString(jsonBody);
        if (!jsonElement.isJsonObject()) {
            throw new JsonSyntaxException("Не объект");
        }

        try {
            return jsonElement.getAsJsonObject().get(field).getAsInt();
        } catch (NullPointerException e) {
            return -1;
        }
    }

    private boolean hasNecessaryFields(String jsonBody) {
        JsonElement jsonElement = JsonParser.parseString(jsonBody);
        if (!jsonElement.isJsonObject()) {
            throw new JsonSyntaxException("Не объект");
        }

        try {
            jsonElement.getAsJsonObject().get("name").getAsString();
            jsonElement.getAsJsonObject().get("epicID").getAsInt();
            jsonElement.getAsJsonObject().get("description").getAsString();
            jsonElement.getAsJsonObject().get("status").getAsString();
            jsonElement.getAsJsonObject().get("duration").getAsLong();
            jsonElement.getAsJsonObject().get("startTime").getAsString();

        } catch (NullPointerException e) {
            return false;
        }
        return true;
    }
}
