import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.HashMap;
import java.util.Map;

public class HttpTaskServer {
    private static final int PORT = 8080;
    private static final  TaskManager manager = Managers.getDefault();

    private static final Map<String, HttpHandler> contextPathMap = new HashMap<String, HttpHandler>() {{
        put("/tasks", new TasksHandler(manager));
        put("/epics", new EpicsHandler(manager));
        put("/subtasks", new SubTasksHandler(manager));
        put("/history", new HistoryHandler(manager));
        put("/prioritized", new PrioritizedHandler(manager));
    }};

    public static void main(String[] args) throws IOException {
        HttpServer httpServer = HttpServer.create(new InetSocketAddress(PORT), 0);


        for(String path: contextPathMap.keySet()){
            httpServer.createContext(path, contextPathMap.get(path));
        }

        httpServer.start();

        System.out.println("HTTP-сервер запущен на " + PORT + " порту");
    }
}
