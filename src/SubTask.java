public class SubTask extends Task{
    private final int epicID;

    public SubTask(String name, String description, TaskStatus status, int epicID) {
        super(name, description, status);
        this.epicID = epicID;
    }

    public SubTask(String name, String description, int id, TaskStatus status, int epicID) {
        super(name, description, id, status);
        this.epicID = epicID;
    }

    public int getEpicID() {
        return epicID;
    }

    @Override
    public String toString(){
        return "Подзадача № "+ this.getId()
                + "|" + "Имя : " + this.getName()
                + "|" + "Описание : "+ this.getDescription()
                + "|" + "Статус : " + this.getStatus();
    }
}
