import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;

import java.time.Duration;
import java.time.LocalDateTime;

public class SubTask extends Task{
    private int epicID;

    public SubTask(String name, String description, int id, TaskStatus status, LocalDateTime startTime, Duration duration) {
        super(name, description, id, status,startTime,duration);
    }

    public SubTask(String name, String description, TaskStatus taskStatus, LocalDateTime startTime, Duration duration) {
        super(name, description, taskStatus,startTime,duration);
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
                + "|" + "Статус : " + this.getStatus()
                + "|" + "Дата начала  : " + this.getStartTime()
                + "|" + "Продолжительность (минут) : " + this.getDuration().toMinutes();
    }


    public static SubTask fromJSON(String taskJSONString) throws JsonSyntaxException {
        Gson gson = new GsonBuilder()
                .serializeNulls()
                .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
                .registerTypeAdapter(Duration.class, new DurationAdapter())
                .create();
        return gson.fromJson(taskJSONString,SubTask.class);

    }
}
