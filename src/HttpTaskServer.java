import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.HashMap;
import java.util.Map;

public class HttpTaskServer {
    private final int PORT = 8080;
    private TaskManager manager;

    private HttpServer httpServer;

    private Map<String, HttpHandler> getContextMap(TaskManager manager){
        return new HashMap<String, HttpHandler>() {{
            put("/tasks", new TasksHandler(manager));
            put("/epics", new EpicsHandler(manager));
            put("/subtasks", new SubTasksHandler(manager));
            put("/history", new HistoryHandler(manager));
            put("/prioritized", new PrioritizedHandler(manager));
        }};
    }

    public HttpTaskServer(TaskManager manager) {
        this.manager = manager;
    }


    public HttpServer start() throws IOException {
        httpServer = HttpServer.create(new InetSocketAddress(PORT), 0);
        Map<String,HttpHandler> contextPathMap = getContextMap(this.manager);
        for (String path : contextPathMap.keySet()) {
            httpServer.createContext(path, contextPathMap.get(path));
        }

        httpServer.start();

        System.out.println("HTTP-сервер запущен на " + PORT + " порту");
        return httpServer;
    }

    public void stop() {
        httpServer.stop(1);
    }


    public static void main(String[] args) throws IOException {

        HttpTaskServer server = new HttpTaskServer(Managers.getDefault());
        server.start();
    }
}
