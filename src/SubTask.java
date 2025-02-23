public class SubTask extends Task{
    private final int epicUID;

    public int getEpicUID() {
        return epicUID;
    }

    public SubTask(String name, String description, int uid, TaskStatus status, int epicUID) {
        super(name, description, uid, status,TaskTypes.SUBTASK);
        this.epicUID = epicUID;
    }
}
