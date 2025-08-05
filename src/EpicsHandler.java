import com.google.gson.*;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;

public class EpicsHandler extends BaseHttpHandler implements HttpHandler {
    TaskManager manager;

    EpicsHandler(TaskManager manager) {
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
            ArrayList<Epic> epics = this.manager.getEpics();

            Gson gson = new GsonBuilder()
                    .serializeNulls()
                    .setPrettyPrinting()
                    .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
                    .registerTypeAdapter(Duration.class, new DurationAdapter())
                    .create();


            this.sendText(exchange,gson.toJson(epics));
            return;
        }

        if (uriParts.length == 3) {
            int id = -1;
            try {
                id = Integer.parseInt(uriParts[2]);
            } catch (NumberFormatException e) {
                throw new IncorectURIPathException("Неправильный тип в пути" + uri);
            }

            Optional<Epic> epic = this.manager.getEpic(id);

            if (epic.isPresent()) {
                this.sendText(exchange, epic.get().toJSON());
                return;
            }
            throw new NotFoundException(String.format("Эпик с номером %s не найден", id));
        }

        if (uriParts.length == 4 && uriParts[3].equals("subtasks")) {
            int id = -1;
            try {
                id = Integer.parseInt(uriParts[2]);
            } catch (NumberFormatException e) {
                throw new IncorectURIPathException("Неправильный тип в пути" + uri);
            }


            if (this.manager.hasEpic(id)) {
                ArrayList<SubTask> epicSubtasks = this.manager.getEpicSubtasks(id);

                Gson gson = new GsonBuilder()
                        .serializeNulls()
                        .setPrettyPrinting()
                        .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
                        .registerTypeAdapter(Duration.class, new DurationAdapter())
                        .create();

                this.sendText(exchange, gson.toJson(epicSubtasks));
                return;
            }
            throw new NotFoundException(String.format("Эпик с номером %s не найден", id));
        }

        throw new UnknownURIPathException("Неизвестный путь" + uri);
    }

    private void handlePOSTMethod(HttpExchange exchange) throws IOException {
        String uri = exchange.getRequestURI().toString();
        String[] uriParts = uri.split("/");

        if(uriParts.length > 2) {
            throw new UnknownURIPathException("Неизвестный путь" + uri);
        }

        try {
            InputStream inputStream = exchange.getRequestBody();
            String jsonBody = new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);

            if(!hasNecessaryFields(jsonBody)){
                throw new BadDataException("Некорректные данные");
            }
            Epic parsedEpic = Epic.fromJSON(jsonBody);

            if (getIdFromBody(jsonBody) < 0) {
                this.manager.addEpic(parsedEpic);
                this.sendCreated(exchange);

            } else {
                if(!this.manager.hasEpic(parsedEpic.getId())){
                    throw new NotFoundException(String.format("Эпик с номером %s не найден", parsedEpic.getId()));
                }
                this.manager.updateEpic(parsedEpic);
                this.sendCreated(exchange,"Успешно обновлен");
            }

        } catch (JsonSyntaxException e) {
            this.sendBadType(exchange, e.toString());
        }catch (TaskTimeOverlappingException e){
            this.sendHasOverlaps(exchange);
        }catch (NotFoundException e){
            this.sendNotFound(exchange,e.getMessage());
        }catch (BadDataException | SubtaskAlreadyInEpicException| NoSubtaskException e ){
            this.sendBadType(exchange,e.getMessage());
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
                if(!this.manager.hasEpic(id)){
                    throw new NotFoundException(String.format("Эпик с номером %s не найдена", id));
                }

                this.manager.deleteEpic(id);

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

    private boolean hasNecessaryFields(String jsonBody){
        JsonElement jsonElement = JsonParser.parseString(jsonBody);
        if (!jsonElement.isJsonObject()) {
            throw new JsonSyntaxException("Не объект");
        }

        try {
            jsonElement.getAsJsonObject().get("name").getAsString();
            jsonElement.getAsJsonObject().get("description").getAsString();
            jsonElement.getAsJsonObject().get("status").getAsString();
            jsonElement.getAsJsonObject().get("duration").getAsLong();
            jsonElement.getAsJsonObject().get("startTime").getAsString();

            jsonElement.getAsJsonObject().get("subtasks").getAsJsonArray();

        } catch (NullPointerException e) {
            return false;
        }
        return true;
    }
}
