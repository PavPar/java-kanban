import java.util.ArrayList;
import java.util.Scanner;

public class Main {
    private static Scanner scanner = new Scanner(System.in);
    private static TaskManager taskManager = new TaskManager();

    public static void main(String[] args) {
        int input;
        while (true) {
            printMenu();
            input = scanner.nextInt();
            switch (input) {
                case 0:
                    return;
                case 1:
                    printTasks();
                    break;
                case 2:
                    taskManager.clearTasks();
                    System.out.println("Все задачи удалены");
                    break;
                case 3:
                    createTaskDialog();
                    System.out.println("Задача добавлена");
                    break;
                case 4:
                    updateTask();
                    break;
                case 5:
                    deleteTaskByUIDDialog();
                    break;
                case 6:
                    getTaskByUIDDialog();
                    break;
                case 7:
                    getEpicSubtasksByUID();
                    break;
                default:
                    System.out.println("Такой опции нет");
            }
        }

    }

    private static void printMenu() {
        System.out.println();
        System.out.println("Выберите действие :");
        System.out.println("0 - Выйти из системы"); // +
        System.out.println("========================");
        System.out.println("1 - Получить список всех задач"); // +
        System.out.println("2 - Удалить все задачи"); // +
        System.out.println("========================");
        System.out.println("3 - Создать задачу");
        System.out.println("4 - Обновить задачу");
        System.out.println("5 - Удалить задачу по ID");
        System.out.println("6 - Получить задачу по ID"); // +
        System.out.println("7 - Получить задачи эпика по ID");
        System.out.println();
    }

    private static void printTasks() {
        System.out.println("Обычные задачи: ");
        System.out.println(taskManager.getTasks());
        System.out.println("Эпики: ");
        System.out.println(taskManager.getEpics());
        System.out.println("Подзадачи эпиков: ");
        System.out.println(taskManager.getSubTasks());
    }

    private static void createTaskDialog() {
        System.out.println("Выберете тип задачи");
        System.out.println("1 - Задача");
        System.out.println("2 - Эпик");
        System.out.println("3 - Подзадача");

        int input = scanner.nextInt();
        switch (input) {
            case 1: {
                createTask();
                break;
            }
            case 2: {
                createEpic();
                break;
            }
            case 3: {
                createSubTask();
                break;
            }
            default:
                System.out.println("Такого типа нет");
        }
    }

    private static void createTask() {
        System.out.println("Введите имя задачи:");
        String name = scanner.next();

        System.out.println("Введите описание задачи:");
        String description = scanner.next();

        Task task = new Task(name, description, TaskManager.getNextTaskUID(), TaskStatus.NEW);
        taskManager.addTask(task);
    }

    private static void createEpic() {
        System.out.println("Введите имя эпика:");
        String name = scanner.next();

        System.out.println("Введите описание эпика:");
        String description = scanner.next();

        Epic epic = new Epic(name, description, TaskManager.getNextTaskUID(), TaskStatus.NEW);
        taskManager.addEpic(epic);
    }

    private static void createSubTask() {
        System.out.println("Введите имя подзадачи:");
        String name = scanner.next();

        System.out.println("Введите описание подзадачи:");
        String description = scanner.next();

        System.out.println("Введите UID эпика:");
        int epicUID = scanner.nextInt();

        if (!taskManager.hasTask(epicUID, TaskType.EPIC)) {
            System.out.println("Такого эпика не существует");
            return;
        }

        SubTask subTask = new SubTask(name, description, TaskManager.getNextTaskUID(), TaskStatus.NEW, epicUID);
        taskManager.addSubtask(subTask);
    }

    private static void getTaskByUIDDialog() {
        System.out.println("Введите UID задачи :");
        int input = scanner.nextInt();

        ArrayList<Task> tasks = taskManager.findTask(input);
        if (tasks.size() != 1) {
            System.out.println("Не удалось найти задачу с UID - " + input);
            return;
        }
        System.out.println(tasks.get(0).toString());
    }

    private static void getEpicSubtasksByUID() {
        System.out.println("Введите UID эпика :");
        int input = scanner.nextInt();

        if(!taskManager.hasTask(input,TaskType.EPIC)){
            System.out.println("Не удалось найти эпик с UID - " + input);
            return;
        };

        System.out.println("Эпик:");
        Epic epic = taskManager.getEpic(input);

        System.out.println(epic);
        ArrayList<Integer> subTasksUIDs = epic.getSubtasks();

        System.out.println("Подзадачи:");
        for(int uid : subTasksUIDs){
            System.out.println(taskManager.getSubtask(uid));
        }

    }

    private static void deleteTaskByUIDDialog() {
        System.out.println("Введите UID задачи :");
        int input = scanner.nextInt();

        taskManager.deleteTask(input);
    }

    private static void updateTask() {
        System.out.println("Введите UID задачи :");
        int taskUID = scanner.nextInt();

        ArrayList<Task> tasks = taskManager.findTask(taskUID);

        if (tasks.isEmpty()) {
            System.out.println("Такого UID нет");
            return;
        }
        Task task = tasks.get(0);

        System.out.println("Найден элемент: ");
        System.out.println(task);


        switch (task.getTaskType()) {
            case TASK:
                Task newTask = new Task(task.getName(), task.getDescription(), task.getUid(), task.getStatus(), task.getTaskType());
                taskUpdateDialog(newTask);
                taskManager.updateTask(newTask);
                break;
            case EPIC:
                Epic originalEpic = taskManager.getEpic(task.getUid());
                Epic newEpic = new Epic(task.getName(), task.getDescription(), task.getUid(), task.getStatus(),originalEpic.getSubtasks());
                taskUpdateDialog(newEpic);
                taskManager.updateEpic(newEpic);
                break;
            case SUBTASK:
                SubTask originalSubtask = taskManager.getSubtask(task.getUid());
                SubTask newSubtask = new SubTask(task.getName(), task.getDescription(), task.getUid(), task.getStatus(),originalSubtask.getEpicUID());
                taskUpdateDialog(newSubtask);
                taskManager.updateSubtask(newSubtask);
                break;
            default:
                System.out.println("Неизвестный тип задачи");
        }

    }

    private static void taskUpdateDialog(Task newTask) {
        System.out.println("Какое свойство вы хотите поменять?");
        System.out.println("1 - Имя");
        System.out.println("2 - Описание");

        if(newTask.getTaskType() != TaskType.EPIC){
            System.out.println("3 - Статус");
        }

        int input = scanner.nextInt();
        switch (input) {
            case 1: {
                System.out.println("Введите имя");
                String name = scanner.next();
                newTask.setName(name);
                break;
            }
            case 2: {
                System.out.println("Введите описание");
                String description = scanner.next();
                newTask.setDescription(description);
                break;
            }
            case 3: {
                if(newTask.taskType == TaskType.EPIC){
                    System.out.println("Редактирование этого свойства запрещено");
                }
                System.out.println("Выберите статус");
                System.out.println("1 - NEW");
                System.out.println("2 - IN PROGRESS");
                System.out.println("3 - DONE");

                int statusInput = scanner.nextInt();

                switch (statusInput){
                    case 1:
                        newTask.setStatus(TaskStatus.NEW);
                        break;
                    case 2:
                        newTask.setStatus(TaskStatus.IN_PROGRESS);
                        break;
                    case 3:
                        newTask.setStatus(TaskStatus.DONE);
                        break;
                    default:
                        System.out.println("Такой опции нет");
                }

                break;
            }
            default:
                System.out.println("Такого свойства нет");
        }
    }
}
