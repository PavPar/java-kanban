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
                    System.out.println(taskManager.getTasks());
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

        Task epic = new Epic(name, description, TaskManager.getNextTaskUID(), TaskStatus.NEW);
        taskManager.addTask(epic);
    }

    private static void createSubTask() {
        System.out.println("Введите имя подзадачи:");
        String name = scanner.next();

        System.out.println("Введите описание подзадачи:");
        String description = scanner.next();

        System.out.println("Введите UID эпика:");
        int epicUID = scanner.nextInt();

        if(!taskManager.hasTask(epicUID)){
            System.out.println("Такого эпика не существует");
            return;
        }

        Task epic = new SubTask(name, description, TaskManager.getNextTaskUID(), TaskStatus.NEW,epicUID);
        taskManager.addTask(epic);
    }

    private static void getTaskByUIDDialog() {
        System.out.println("Введите UID задачи :");
        int input = scanner.nextInt();

        ArrayList<Task> tasks = taskManager.getTaskByUID(input);
        if (tasks.size() != 1) {
            System.out.println("Не удалось найти задачу с UID - " + input);
            return;
        }
        System.out.println(tasks.get(0).toString());
    }

    private static void deleteTaskByUIDDialog() {
        System.out.println("Введите UID задачи :");
        int input = scanner.nextInt();

        taskManager.deleteTask(input);
    }

    private static void updateTask() {
        System.out.println("Введите UID задачи :");
        int taskUID = scanner.nextInt();

        ArrayList<Task> tasks = taskManager.getTaskByUID(taskUID);
        Task task = tasks.get(0);
        Task newTask = new Task(task.getName(), task.getDescription(), task.getUid(), task.getStatus(),task.getTaskType());

        System.out.println("Найден элемент: ");
        System.out.println(task);

        System.out.println("Какое свойство вы хотите поменять?");
        System.out.println("1 - Имя");
        System.out.println("2 - Описание");
        System.out.println("3 - Статус");

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
                System.out.println("Введите статус");
                String status = scanner.next();
                newTask.setStatus(TaskStatus.valueOf(status));
                break;
            }
            default:
                System.out.println("Такого свойства нет");
        }


        taskManager.updateTask(newTask);
    }
}
