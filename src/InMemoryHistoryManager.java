import java.util.ArrayList;

public class InMemoryHistoryManager implements HistoryManager{
    private final int HISTORY_LIST_MAX_SIZE = 10;
    private final ArrayList<Task> history = new ArrayList<>();

    @Override
    public ArrayList<Task> getHistory(){
        return history;
    };

    @Override
    public void add(Task task){
        history.add(0,task);
        if(history.size() > HISTORY_LIST_MAX_SIZE){
            history.remove(history.size() -1 );
        }
    }

}
