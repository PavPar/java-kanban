import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;

public class FileBackedTaskManager extends  InMemoryTaskManager implements TaskManager{
    @Override
    public void addTask(Task task) {
        super.addTask(task);
        save();
    }

    @Override
    public void addSubtask(SubTask subtask) {
        super.addSubtask(subtask);
        save();
    }

    @Override
    public void addEpic(Epic epic) {
        super.addEpic(epic);
        save();
    }

    public void save(){
        try{
            Path path = Paths.get("test.csv");
            if(!Files.exists(path)){
                createCSVFile(path);
            }

            writeLinesToFile(path,createFileHeader(),false);
            ArrayList<String> lines = new ArrayList<>();

            for (Task task : this.getTasks()){
                lines.add(transformTaskToString(task).toString());
            };

            for (Epic epic : this.getEpics()){
                lines.add(transformTaskToString(epic).toString());
            };

            for (SubTask subtask : this.getSubTasks()){
                lines.add(transformTaskToString(subtask).toString());
            };

            writeLinesToFile(path,lines,true);
        }catch (IOException e){
            throw new ManagerSaveException("Не удалось сохранить данные!");
        }
    }


    static ArrayList<String> loadFromFile(File file){
        try{
            return readFromFile(file.toPath(),false);
        }catch (IOException e){
            e.printStackTrace();
        }
        return null;
    }

    private StringBuilder transformTaskToString(Task task,TaskType type){
        StringBuilder builder = new StringBuilder();
        char separator = ',';
        builder.append(task.getId()).append(separator)
                .append(type).append(separator)
                .append(task.getName()).append(separator)
                .append(task.getStatus()).append(separator)
                .append(task.getDescription()).append(separator);
        return builder;
    }

    private StringBuilder transformTaskToString(Task task ){
        return transformTaskToString(task,TaskType.TASK);
    }

    private StringBuilder transformTaskToString(Epic epic){
        return transformTaskToString(epic,TaskType.EPIC);
    }

    private StringBuilder transformTaskToString(SubTask subTask){
        StringBuilder builder = transformTaskToString(subTask,TaskType.SUBTASK);
        builder.append(subTask.getEpicID());
        return builder;
    }

    private void createCSVFile(Path path) throws IOException {
        Files.createFile(path);
    }

    private void writeLinesToFile(Path path, String line, boolean append) throws IOException{
        try(BufferedWriter bwr = new BufferedWriter(new FileWriter(path.toString(),append))){
            bwr.append(line);
            bwr.append("\n");
        }catch (IOException e){
            throw new ManagerSaveException("Не удалось записать файл по причине - " + e.getMessage());
        }catch (Throwable e){
            System.out.println("Произошла непредвиденная ошибка - " + e.getMessage());
        }
    }

    private void writeLinesToFile(Path path, ArrayList<String> lines, boolean append) throws IOException{
        try(BufferedWriter bwr = new BufferedWriter(new FileWriter(path.toString(),append))){
            for(String line : lines){
                bwr.append(line);
                bwr.append("\n");
            }
        }catch (IOException e){
            throw new ManagerSaveException("Не удалось записать файл по причине - " + e.getMessage());
        }catch (Throwable e){
            System.out.println("Произошла непредвиденная ошибка - " + e.getMessage());
        }
    }

    private static ArrayList<String> readFromFile(Path path, boolean ignoreFirstLine) throws IOException{
        ArrayList<String> lines = new ArrayList<>();
        boolean ignoreHead = ignoreFirstLine;
        try(BufferedReader br = new BufferedReader(new FileReader(path.toString()))){
            while (br.ready()){
                if(ignoreHead){
                    ignoreHead = false;
                    continue;
                }
                lines.add(br.readLine());
            }


        }catch (IOException e){
            throw new ManagerSaveException("Не удалось считать файл по причине - " + e.getMessage());
        }catch (Throwable e){
            System.out.println("Произошла непредвиденная ошибка - " + e.getMessage());
        }
        return lines;
    }

    private String createFileHeader(){
        StringBuilder builder = new StringBuilder();
        char separator = ',';
        builder.append("id").append(separator)
                .append("type").append(separator)
                .append("name").append(separator)
                .append("status").append(separator)
                .append("description").append(separator)
                .append("epic");
        return builder.toString();
    }
}
