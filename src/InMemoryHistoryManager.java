import java.util.ArrayList;
import java.util.HashMap;

public class InMemoryHistoryManager implements HistoryManager{
    private final HashMap<Integer,Node<Task>> history = new HashMap<>();

    private Node<Task> head;
    private Node<Task> tail;

    @Override
    public ArrayList<Task> getHistory(){
        ArrayList<Task> result = new ArrayList<>();
        if(tail == null){
            return result;
        }
        Node<Task> currentNode = tail;

        do{
            result.add(currentNode.data);
            currentNode = currentNode.prev;
        }while(currentNode != null);
        return  result;
    }

    @Override
    public void remove(Task task) {
        Node<Task> node = history.get(task.getId());
        if(node == null){
            return;
        }
        history.remove(task.getId());

        Node<Task> previousNode = node.prev;
        Node<Task> nextNode = node.next;

        if(previousNode != null){
            previousNode.next = nextNode;
        }

        if(previousNode == null){
            head = nextNode;
        }

        if(nextNode == null){
            tail = previousNode;
        }

        if(nextNode != null) {
            nextNode.prev = previousNode;
        }
    };

    @Override
    public void add(Task task){
        this.removeTaskIfExists(task);
        Node<Task> newNode =new Node<>(task);
        history.put(task.getId(),newNode);

        if(head == null || tail == null){
            head = newNode;
            tail = newNode;
            return;
        }

        newNode.prev = tail;
        tail.next = newNode;
        tail = newNode;
    }

    private void removeTaskIfExists(Task task){
        Node<Task> existingTask = history.get(task.getId());

        if(existingTask != null){
            this.remove(task);
        }
    }
}
