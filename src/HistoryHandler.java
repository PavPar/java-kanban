import com.google.gson.*;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;

public class HistoryHandler extends BaseHttpHandler implements HttpHandler {
    TaskManager manager;

    HistoryHandler(TaskManager manager) {
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
            List<Task> history = this.manager.getHistory();

            Gson gson = new GsonBuilder()
                    .serializeNulls()
                    .setPrettyPrinting()
                    .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
                    .registerTypeAdapter(Duration.class, new DurationAdapter())
                    .create();


            this.sendText(exchange,gson.toJson(history));
            return;
        }

        throw new UnknownURIPathException("Неизвестный путь" + uri);
    }


}
