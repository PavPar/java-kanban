import com.sun.net.httpserver.HttpExchange;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

public class BaseHttpHandler {
    protected void sendText(HttpExchange h, String text) throws IOException {
        byte[] resp = text.getBytes(StandardCharsets.UTF_8);
        h.getResponseHeaders().add("Content-Type", "application/json;charset=utf-8");
        h.sendResponseHeaders(200, resp.length);
        h.getResponseBody().write(resp);
        h.close();
    }

    private void sendBasicStatusCodeMessage(HttpExchange h,int rCode, String text) throws IOException {
        byte[] resp = text.getBytes(StandardCharsets.UTF_8);
        h.getResponseHeaders().add("Content-Type", "text/plain;charset=utf-8");
        h.sendResponseHeaders(rCode, resp.length);
        h.getResponseBody().write(resp);
        h.close();
    }

    public void sendNotFound(HttpExchange h, String text) throws IOException {
        this.sendBasicStatusCodeMessage(h,404,text);
    }

    public void sendNotFound(HttpExchange h) throws IOException {
        this.sendBasicStatusCodeMessage(h,404,"Ресурс не найден");
    }

    public void sendOk(HttpExchange h, String text) throws IOException {
        this.sendBasicStatusCodeMessage(h,200,text);
    }

    public void sendOk(HttpExchange h) throws IOException {
        this.sendBasicStatusCodeMessage(h,200,"Операция успешно выполнена");
    }

    public void sendCreated(HttpExchange h, String text) throws IOException {
        this.sendBasicStatusCodeMessage(h,201,text);
    }

    public void sendCreated(HttpExchange h) throws IOException {
        this.sendBasicStatusCodeMessage(h,201,"Успешно создано");
    }

    public void sendHasOverlaps(HttpExchange h, String text) throws IOException {
        this.sendBasicStatusCodeMessage(h,406,text);
    }

    public void sendHasOverlaps(HttpExchange h) throws IOException {
        this.sendBasicStatusCodeMessage(h,406,"Задачи пересекаются");
    }

    public void sendBadType(HttpExchange h, String text) throws IOException {
        this.sendBasicStatusCodeMessage(h,400,text);
    }

    public void sendBadType(HttpExchange h) throws IOException {
        this.sendBasicStatusCodeMessage(h,400,"Некорректные данные");
    }

    public void sendFatalServerError(HttpExchange h, String text) throws IOException {
        this.sendBasicStatusCodeMessage(h,500,text);
    }

    public void sendFatalServerError(HttpExchange h) throws IOException {
        this.sendBasicStatusCodeMessage(h,500,"Произошла ошибка сервера");
    }

}
