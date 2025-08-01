import java.util.ArrayList;

public interface HistoryManager {
    void add(Task task);
    ArrayList<Task> getHistory();
    void remove(Task task);
}
