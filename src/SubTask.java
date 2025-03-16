public class SubTask extends Task{
    private int epicID;

    public SubTask(String name, String description, TaskStatus status) {
        super(name, description, status);
    }

    public SubTask(String name, String description, int id, TaskStatus status) {
        super(name, description, id, status);
    }

    public int getEpicID() {
        return epicID;
    }

    public void setEpicID(int epicID){
        if(epicID == this.getId()){
            return;
        }
        this.epicID = epicID;
    }

    @Override
    public String toString(){
        return "Подзадача № "+ this.getId()
                + "|" + "Имя : " + this.getName()
                + "|" + "Описание : "+ this.getDescription()
                + "|" + "Статус : " + this.getStatus();
    }
}
