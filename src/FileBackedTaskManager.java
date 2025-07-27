import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Objects;

public class FileBackedTaskManager extends InMemoryTaskManager implements TaskManager {
    private static final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm");
    private static final HashMap<CSVFields,Integer> CSVKeyIndexValue = new LinkedHashMap<>();

    FileBackedTaskManager(){
        super();
        CSVKeyIndexValue.put(CSVFields.id,0);
        CSVKeyIndexValue.put(CSVFields.type,1);
        CSVKeyIndexValue.put(CSVFields.name,2);
        CSVKeyIndexValue.put(CSVFields.status,3);
        CSVKeyIndexValue.put(CSVFields.description,4);
        CSVKeyIndexValue.put(CSVFields.startDate,5);
        CSVKeyIndexValue.put(CSVFields.duration,6);
        CSVKeyIndexValue.put(CSVFields.epic,7);
    }
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


    public static FileBackedTaskManager loadFromFile(File file) {
        FileBackedTaskManager newManager = new FileBackedTaskManager();
        try {
            ArrayList<String> lines = readFromFile(file.toPath(), true);
            HashMap<Integer, ArrayList<Integer>> epicSubtaskMap = new HashMap<>();

            for (String line : lines) {
                String[] values = parseString(line);
                TaskType type = getTaskTypeFromString(values);


                switch (type) {
                    case TaskType.TASK:
                        newManager.addTask(getTaskFromString(values));
                        break;
                    case TaskType.EPIC:
                        newManager.addEpic(getEpicFromString(values));
                        break;
                    case TaskType.SUBTASK:
                        SubTask subTask = getSubtaskFromString(values);

                        epicSubtaskMap.putIfAbsent(subTask.getEpicID(), new ArrayList<>());
                        epicSubtaskMap.get(subTask.getEpicID()).add(subTask.getId());

                        newManager.addSubtask(subTask);
                        break;
                }
            }

            for (Epic epic : newManager.getEpics()) {
                if (epicSubtaskMap.containsKey(epic.getId())) {
                    epic.addSubtasks(epicSubtaskMap.get(epic.getId()));
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
        return newManager;
    }


    private static String[] parseString(String string) {
        return string.split(",");
    }

    private static TaskType getTaskTypeFromString(String[] values) {
        return TaskType.valueOf(values[1]);
    }

    private static Task getTaskFromString(String[] values) {
        int id = Integer.parseInt(values[CSVKeyIndexValue.get(CSVFields.id)]);
        String name = values[CSVKeyIndexValue.get(CSVFields.name)];
        TaskStatus status = TaskStatus.valueOf(values[CSVKeyIndexValue.get(CSVFields.status)]);
        String description = values[CSVKeyIndexValue.get(CSVFields.description)];
        String startDateString = values[CSVKeyIndexValue.get(CSVFields.startDate)];
        if(startDateString.equals("null")){
            return new Task(name, description, id, status);
        }

        LocalDateTime time = LocalDateTime.parse(startDateString,dateTimeFormatter);
        Duration duration = Duration.ofMinutes(Integer.parseInt(values[CSVKeyIndexValue.get(CSVFields.duration)]));

        return new Task(name, description, id, status,time,duration);
    }

    private static Epic getEpicFromString(String[] values) {
        int id = Integer.parseInt(values[CSVKeyIndexValue.get(CSVFields.id)]);
        String name = values[CSVKeyIndexValue.get(CSVFields.name)];
        TaskStatus status = TaskStatus.valueOf(values[CSVKeyIndexValue.get(CSVFields.status)]);
        String description = values[CSVKeyIndexValue.get(CSVFields.description)];

        String startDateString = values[CSVKeyIndexValue.get(CSVFields.startDate)];
        if(startDateString.equals("null")){
            return new Epic(name, description, id, status, new ArrayList<>());
        }

        LocalDateTime time = LocalDateTime.parse(startDateString,dateTimeFormatter);
        Duration duration = Duration.ofMinutes(Integer.parseInt(values[CSVKeyIndexValue.get(CSVFields.duration)]));

        return new Epic(name,description,id,status,new ArrayList<>(),time,duration);
    }

    private static SubTask getSubtaskFromString(String[] values) {
        int id = Integer.parseInt(values[CSVKeyIndexValue.get(CSVFields.id)]);
        String name = values[CSVKeyIndexValue.get(CSVFields.name)];
        TaskStatus status = TaskStatus.valueOf(values[CSVKeyIndexValue.get(CSVFields.status)]);
        String description = values[CSVKeyIndexValue.get(CSVFields.description)];

        int epicId = Integer.parseInt(values[CSVKeyIndexValue.get(CSVFields.epic)]);

        String startDateString = values[CSVKeyIndexValue.get(CSVFields.startDate)];
        LocalDateTime time = LocalDateTime.parse(startDateString,dateTimeFormatter);
        Duration duration = Duration.ofMinutes(Integer.parseInt(values[CSVKeyIndexValue.get(CSVFields.duration)]));

        SubTask task = new SubTask(name, description, id, status,time,duration);
        task.setEpicID(epicId);
        return task;

    }

    private StringBuilder transformTaskToString(Task task, TaskType type) {
        StringBuilder builder = new StringBuilder();
        char separator = ',';
        builder.append(task.getId()).append(separator)
                .append(type).append(separator)
                .append(task.getName()).append(separator)
                .append(task.getStatus()).append(separator)
                .append(task.getDescription()).append(separator);
        if (Objects.nonNull(task.getStartTime())) {
            builder.append(dateTimeFormatter.format(task.getStartTime())).append(separator);
        } else {
            builder.append(task.getStartTime()).append(separator);
        }
        builder.append(task.getDuration().toMinutes()).append(separator);
        return builder;
    }

    private StringBuilder transformTaskToString(Task task) {
        return transformTaskToString(task, TaskType.TASK);
    }

    private StringBuilder transformTaskToString(Epic epic) {
        return transformTaskToString(epic, TaskType.EPIC);
    }

    private StringBuilder transformTaskToString(SubTask subTask) {
        StringBuilder builder = transformTaskToString(subTask, TaskType.SUBTASK);
        builder.append(subTask.getEpicID());
        return builder;
    }

    private void createCSVFile(Path path) throws IOException {
        Files.createFile(path);
    }

    private void writeLinesToFile(Path path, String line, boolean append) throws IOException {
        try (BufferedWriter bwr = new BufferedWriter(new FileWriter(path.toString(), append))) {
            bwr.append(line);
            bwr.append("\n");
        } catch (IOException e) {
            throw new ManagerSaveException("Не удалось записать файл по причине - " + e.getMessage());
        } catch (Throwable e) {
            System.out.println("Произошла непредвиденная ошибка - " + e.getMessage());
        }
    }

    private void writeLinesToFile(Path path, ArrayList<String> lines, boolean append) throws IOException {
        try (BufferedWriter bwr = new BufferedWriter(new FileWriter(path.toString(), append))) {
            for (String line : lines) {
                bwr.append(line);
                bwr.append("\n");
            }
        } catch (IOException e) {
            throw new ManagerSaveException("Не удалось записать файл по причине - " + e.getMessage());
        } catch (Throwable e) {
            System.out.println("Произошла непредвиденная ошибка - " + e.getMessage());
        }
    }

    private static ArrayList<String> readFromFile(Path path, boolean ignoreFirstLine) throws IOException {
        ArrayList<String> lines = new ArrayList<>();
        boolean ignoreHead = ignoreFirstLine;
        try (BufferedReader br = new BufferedReader(new FileReader(path.toString()))) {
            while (br.ready()) {
                String line = br.readLine();
                if (ignoreHead) {
                    ignoreHead = false;
                    continue;
                }
                lines.add(line);
            }


        } catch (IOException e) {
            throw new ManagerSaveException("Не удалось считать файл по причине - " + e.getMessage());
        } catch (Throwable e) {
            System.out.println("Произошла непредвиденная ошибка - " + e.getMessage());
        }
        return lines;
    }

    private String createFileHeader() {
        StringBuilder builder = new StringBuilder();
        char separator = ',';

        int index = 0;
        for(CSVFields key : CSVKeyIndexValue.keySet()){
            if(index == CSVKeyIndexValue.keySet().size() - 1){
                builder.append(key);
                continue;
            }
            builder.append(key).append(separator);
            index++;
        }
        return builder.toString();
    }

    private void save() {
        try {
            Path path = Paths.get("test.csv");
            if (!Files.exists(path)) {
                createCSVFile(path);
            }

            writeLinesToFile(path, createFileHeader(), false);
            ArrayList<String> lines = new ArrayList<>();

            for (Task task : this.getTasks()) {
                lines.add(transformTaskToString(task).toString());
            }


            for (Epic epic : this.getEpics()) {
                lines.add(transformTaskToString(epic).toString());
            }


            for (SubTask subtask : this.getSubTasks()) {
                lines.add(transformTaskToString(subtask).toString());
            }


            writeLinesToFile(path, lines, true);
        } catch (IOException e) {
            throw new ManagerSaveException("Не удалось сохранить данные!");
        }
    }
}
