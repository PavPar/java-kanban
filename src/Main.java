import org.w3c.dom.html.HTMLIsIndexElement;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;


public class Main {
    private static Scanner scanner = new Scanner(System.in);
    private static TaskManager taskManager = Managers.getFileBackendTaskManager();

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
                    clearTaskManagerDialog();
                    break;
                case 3:
                    createTaskDialog();
                    break;
                case 4:
                    updateTask();
                    break;
                case 5:
                    deleteTaskByIDDialog();
                    break;
                case 6:
                    getTaskByIDDialog();
                    break;
                case 7:
                    getEpicSubtasksByID();
                    break;
                case 8:
                    displayHistory();
                    break;
                default:
                    System.out.println("Такой опции нет");
            }
        }

    }

    private static void printMenu() {
        System.out.println();
        System.out.println("Выберите действие :");
        System.out.println("0 - Выйти из системы");
        System.out.println("========================");
        System.out.println("1 - Получить список всех задач");
        System.out.println("2 - Удалить все задачи определенного типа");
        System.out.println("========================");
        System.out.println("3 - Создать задачу");
        System.out.println("4 - Обновить задачу");
        System.out.println("5 - Удалить задачу по ID");
        System.out.println("6 - Получить задачу по ID");
        System.out.println("7 - Получить задачи эпика по ID");
        System.out.println("8 - Получить историю обращений");
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

        Task task = new Task(name, description, TaskStatus.NEW);
        taskManager.addTask(task);
        System.out.println("Задача добавлена");
    }

    private static void createEpic() {
        System.out.println("Введите имя эпика:");
        String name = scanner.next();

        System.out.println("Введите описание эпика:");
        String description = scanner.next();

        Epic epic = new Epic(name, description, TaskStatus.NEW);
        taskManager.addEpic(epic);
        System.out.println("Эпик добавлен");
    }

    private static void createSubTask() {
        System.out.println("Введите имя подзадачи:");
        String name = scanner.next();

        System.out.println("Введите описание подзадачи:");
        String description = scanner.next();

        System.out.println("Введите ID эпика:");
        int epicID = scanner.nextInt();

        if (!taskManager.hasEpic(epicID)) {
            System.out.println("Такого эпика не существует");
            return;
        }

        SubTask subTask = new SubTask(name, description, TaskStatus.NEW);
        subTask.setEpicID(epicID);
        taskManager.addSubtask(subTask);
        System.out.println("Подзадача добавлена");
    }

    private static void getTaskByIDDialog() {
        System.out.println("Введите ID задачи :");
        int inputTaskID = scanner.nextInt();

        if (taskManager.hasTask(inputTaskID)) {
            Task task = taskManager.getTask(inputTaskID);
            System.out.println(task.toString());
            return;
        }

        if (taskManager.hasEpic(inputTaskID)) {
            Epic epic = taskManager.getEpic(inputTaskID);
            System.out.println(epic.toString());
            return;
        }

        if (taskManager.hasSubTask(inputTaskID)) {
            SubTask subTask = taskManager.getSubtask(inputTaskID);
            System.out.println(subTask.toString());
            return;
        }

        System.out.println("Не удалось найти задачу с ID - " + inputTaskID);
    }

    private static void getEpicSubtasksByID() {
        System.out.println("Введите ID эпика :");
        int inputEpicID = scanner.nextInt();

        if (!taskManager.hasEpic(inputEpicID)) {
            System.out.println("Не удалось найти эпик с ID - " + inputEpicID);
            return;
        }
        ;
        Epic epic = taskManager.getEpic(inputEpicID);

        System.out.println("Эпик:");
        System.out.println(epic);

        System.out.println("Подзадачи:");
        for (SubTask subTask : taskManager.getEpicSubtasks(inputEpicID)) {
            System.out.println(subTask);
        }
    }

    private static void deleteTaskByIDDialog() {
        System.out.println("Введите ID задачи :");
        int inputTaskID = scanner.nextInt();

        if (taskManager.hasTask(inputTaskID)) {
            taskManager.deleteTask(inputTaskID);

            return;
        }

        if (taskManager.hasEpic(inputTaskID)) {
            taskManager.deleteEpic(inputTaskID);
            return;
        }

        if (taskManager.hasSubTask(inputTaskID)) {
            taskManager.deleteSubtask(inputTaskID);
            return;
        }

        System.out.println("Не удалось найти задачу с ID - " + inputTaskID);
    }

    private static void updateTask() {
        System.out.println("Введите ID задачи :");
        int inputTaskID = scanner.nextInt();

        boolean isTask = taskManager.hasTask(inputTaskID);
        boolean isEpic = taskManager.hasEpic(inputTaskID);
        boolean isSubtask = taskManager.hasSubTask(inputTaskID);

        if (isTask) {
            Task task = taskManager.getTask(inputTaskID);
            System.out.println("Найден элемент: ");
            System.out.println(task);

            Task newTask = new Task(task.getName(), task.getDescription(), task.getId(), task.getStatus());
            taskUpdateDialog(newTask, true);
            taskManager.updateTask(newTask);
            return;
        }

        if (isEpic) {
            Epic epic = taskManager.getEpic(inputTaskID);
            System.out.println("Найден элемент: ");
            System.out.println(epic);

            Epic newEpic = new Epic(epic.getName(), epic.getDescription(), epic.getId(), epic.getStatus(), epic.getSubtasks());
            taskUpdateDialog(newEpic, false);
            taskManager.updateEpic(newEpic);
            return;
        }

        if (isSubtask) {
            SubTask subtask = taskManager.getSubtask(inputTaskID);
            SubTask newSubtask = new SubTask(subtask.getName(), subtask.getDescription(), subtask.getId(), subtask.getStatus());
            newSubtask.setEpicID(subtask.getEpicID());
            taskUpdateDialog(newSubtask, true);
            taskManager.updateSubtask(newSubtask);

            return;
        }

        System.out.println("Такого ID нет");
    }

    private static void taskUpdateDialog(Task newTask, boolean canChangeStatus) {
        System.out.println("Какое свойство вы хотите поменять?");
        System.out.println("1 - Имя");
        System.out.println("2 - Описание");

        if (canChangeStatus) {
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
                if (!canChangeStatus) {
                    System.out.println("Редактирование этого свойства запрещено");
                }
                System.out.println("Выберите статус");
                System.out.println("1 - NEW");
                System.out.println("2 - IN PROGRESS");
                System.out.println("3 - DONE");

                int statusInput = scanner.nextInt();

                switch (statusInput) {
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

    private static void clearTaskManagerDialog() {
        System.out.println("Выберите тип для очистки");
        System.out.println("1 - Задачи");
        System.out.println("2 - Эпики");
        System.out.println("3 - Подзадачи");

        int choiceInput = scanner.nextInt();
        switch (choiceInput) {
            case 1:
                taskManager.resetTasks();
                return;
            case 2:
                taskManager.resetEpics();
                return;
            case 3:
                taskManager.resetSubtasks();
                return;
            default:
                System.out.println("Неизвестный тип");
        }
    }

    private static void displayHistory() {
        List<Task> history = taskManager.getHistory();

        System.out.println("История обращений");

        int index = 0;
        for (Task task : history) {
            System.out.println(++index + " - " + task.toString());
        }

    }
}
