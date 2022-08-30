package ru.miron.TZ.logic.view.views;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.ZonedDateTime;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import ru.miron.TZ.givenClasses.Dragon;
import ru.miron.TZ.givenClasses.DragonCave;
import ru.miron.TZ.givenClasses.DragonType;
import ru.miron.TZ.givenClasses.DragonWithKeyAndOwner;
import ru.miron.TZ.logic.commands.ErrorDescription;
import ru.miron.TZ.logic.commands.specificAnswers.ClearDragonsCommandSpecificAnswer;
import ru.miron.TZ.logic.commands.specificAnswers.DragonsInfoCommandSpecificAnswer;
import ru.miron.TZ.logic.commands.specificAnswers.RemoveGreaterOrLowerDragonsCommandSpecificAnswer;
import ru.miron.TZ.logic.commands.specificAnswers.UniqueNumbersOfTreasuresGettingCommandSpecificAnswer;
import ru.miron.TZ.logic.commands.specificAnswers.WingspansSumGettingCommandSpecificAnswer;
import ru.miron.TZ.logic.util.json.JSON;
import ru.miron.TZ.logic.util.json.types.JSONValue;
import ru.miron.TZ.logic.util.json.types.values.JSONMap;
import ru.miron.TZ.logic.view.OutView;

public class OutTextView implements OutView {
    public static final String EXIT_MSG = "Прощайте...";

    /**
     * text stream which should be autoFlushable
     */
    private PrintStream out;

    /** */
    private final Path commandsDescriptionsPath;

    public OutTextView(PrintStream outputStream, Path commandsDescriptionsPath) {
        setOut(outputStream);
        this.commandsDescriptionsPath = commandsDescriptionsPath;
    }

    public void setOut(PrintStream printStream) {
        out = printStream;
    }

    @Override
    public void displayInfo(Map<String, Dragon> toDisplayInfoAbout, ZonedDateTime creationTime) {
        out.println("Тип используемой коллекции: " + toDisplayInfoAbout.getClass().getName());
        out.println("Дата инициализации: " + creationTime);
        out.println("Количество элементов: " + toDisplayInfoAbout.size());
    }

    private void displayDragon(Dragon dragon) {
        out.printf("\tID: %s\n\tИмя: %s\n\tВозраст: %s\n\tРазмах крыльев: %s\n\tТип: %s\n\tОписание: %s\n\tКоординаты(x и y): %.2f, %s\n\tДата создания: %s\n\tКол-во сокровищ: %.2f\n",
                dragon.getId(), dragon.getName(), dragon.getAge(), dragon.getWingspan(), dragon.getType().getName(), 
                dragon.getDescription(), dragon.getCoordinates().getX(), dragon.getCoordinates().getY(), dragon.getCreationDate(), 
                dragon.getCave().getNumberOfTreasures());
    }

    @Override
    public void displayCaveValues(Set<DragonCave> caves) {
        out.println("Уникальные значения DragonCave: ");
        for (DragonCave cave : caves) {
            out.println(cave);
        }
    }

    @Override
    public void displayKeyExistsException(String key, Dragon dragon) {
        out.println("Ключ " + key + " уже существует");
    }

    @Override
    public void displayKeyDoesntExistException(String key) {
        out.println("Ключа " + key + " нет");
    }

    @Override
    public void displayIdDoesntExistException(Long id) {
        out.println("Элемента с id " + id + " нет");
    }

    @Override
    public void displayExitMsg() {
        out.println(EXIT_MSG);
    }

    @Override
    public void displayClearedCount(ClearDragonsCommandSpecificAnswer specificAnswer) {
        if (specificAnswer.getClearedCount() == 0) {
            out.println("Ничего не было очищено");
        } else {
            out.println("Очищено " + specificAnswer.getClearedCount() + " дракона");
        }
    }

    @Override
    public void displayReplacedStateMsg(boolean replaced) {
        if (replaced) {
            out.println("Замена произошла успешно");
        } else {
            out.println("Замена потерпела неудачу");
        }
    }

    @Override
    public void displayWingspanSum(long sum) {
        out.println("Сумма размахов крыльев: " + sum);
    }

    @Override
    public void displayMissingFileFromCommandArg(String fileName) {
        out.println("Не удалось загрузить скрипт, указанный в аргументе командной строки: " + fileName);
    }

    @Override
    public void displayMissingFileFromUser(String fileName) {
        out.println("Не удалось загрузить скрипт, путь к которому вы указали");
    }

    @Override
    public void displayEmptyCollectionMsg() {
        out.println("Коллекция пуста");
    }

    @Override
    public void displayDragonsWithDescription(Map<String, Dragon> dragonsEntriesWithDescription) {
        if (dragonsEntriesWithDescription.isEmpty()) {
            out.println("Драконов с этим описанием нет");
            return;
        }
        out.println("Драконы с этим описанием: ");
        for (Entry<String, Dragon> dragonEntry : dragonsEntriesWithDescription.entrySet()) {
            out.println();
            out.println("Ключ: " + dragonEntry.getKey());
            out.println("Дракон: ");
            Dragon dragon = dragonEntry.getValue();
            displayDragon(dragon);
        }
    }

    @Override
    public void displayEmptyCommandArg() {
        out.println("Список аргументов командной строки пуст");
    }

    @Override
    public void displayEnterCommandMsg() {
        out.println("Введите команду (список - help, выйти - exit): ");
    }

    @Override
    public void displayEmptyCommandLineError() {
        out.println("Введенная строка команды пуста");
    }

    @Override
    public void displayWrongCommandNameError(String enteredCommandNameStr) {
        out.println("Неверное наименование команды: " + enteredCommandNameStr + ". Узнать список: help");
    }

    @Override
    public void displayWrongNumberOfEnteredArgsError(int size) {
        out.println("Неверное кол-во введенных аргумнетов команды. Узнать формат для каждой команды: help");
    }

    @Override
    public void displayWrongFilePathError(String enteredScriptFileName) {
        out.println("Неверный путь к файлу");
    }

    @Override
    public void displayIdIsNotPositiveError(long enteredId) {
        out.println("Id должен быть положительным");
    }

    @Override
    public void displayIdNumberFormatError(String enteredIdString) {
        out.println("Формат числа при вводе id нарушен - он должен быть в пределах типа long");
    }

    @Override
    public void displayAgeIsNotPositiveError(long enteredAge) {
        out.println("Возраст должен быть положительным");
    }

    @Override
    public void displayAgeNumberFormatError(String enteredAgeString) {
        out.println("Формат числа при вводе возраста нарушен - он должен быть в пределах типа integer");
    }

    @Override
    public void displayWingspanIsNotPositiveError(int enteredWingspan) {
        out.println("Размах крыльев должен быть положительным");
    }

    @Override
    public void displayWingspanNumberFormatError(String enteredWingspanString) {
        out.println("Формат числа при вводе размаха крыльев нарушен - он должен быть в пределах типа integer");
    }

    @Override
    public void displayMsgBeforeTreasuresNumberGetting() {
        out.println("Введите кол-во сокровищ, которое лежит в драконьей пещере (float)");
    }

    @Override
    public void displayBlankTreasuresNumberStringError() {
        out.println("Строка для ввода кол-ва сокровищ пуста");
    }

    @Override
    public void displayTreasuresNumberIsNotPositiveError(float enteredTreasuresNumber) {
        out.println("Кол-во сокровищ должно быть положительным");
    }

    @Override
    public void displayTreasuresNumberFormatError(String treasuresNumberString) {
        out.println("Формат числа при вводе кол-ва сокровищ нарушен - он должен быть в пределах типа float");
    }

    @Override
    public void displayMsgBeforeKeyGetting() {
        out.println("Введите ключ дракона: ");
    }

    @Override
    public void displayMsgBeforeCoordinatesGetting() {
        out.println("Введение данных координат:");
    }

    @Override
    public void displayMsgBeforeYOxisValueGetting() {
        out.println("Введите координату y (long):");
    }

    @Override
    public void displayMsgBeforeXOxisValueGetting() {
        out.println("Введите координату x (float):");
    }

    @Override
    public void displayBlankXOxisStringError() {
        out.println("Строка для ввода координаты x пусто");
    }

    @Override
    public void displayBlankYOxisStringError() {
        out.println("Строка для ввода координаты y пусто");
    }

    @Override
    public void displayYOxisNumberFormatError(String yString) {
        out.println("Формат числа при вводе координаты y нарушен - он должен быть в пределах типа long");
    }

    @Override
    public void displayXOxisNumberFormatError(String enteredXString) {
        out.println("Формат числа при вводе координаты x нарушен - он должен быть в пределах типа float");
    }

    @Override
    public void displayMsgBeforeDragonTypeGetting() {
        out.println("Введите тип Дракона (в любом регистре: " + String.join(", ", (String[]) Arrays.stream(DragonType.values()).map(a -> a.toString()).toArray(String[]::new)) + "): ");   
    }

    @Override
    public void displayDragonTypeNameError(String enteredDragonTypeString) {
        out.println("Ошибка в имени типа дракона");
    }

    @Override
    public void displayDidntEndCommandError() {
        out.println("Команда не была закончена, выполнение прервано");
    }

    @Override
    public void displayLevelsOfExecLeftError() {
        out.println("Уровней вложенности скрипта больше, чем планировалось");
    }

    @Override
    public void displayRepetitionInFileRecursionError(File repeatedFile) {
        out.println("Файл " + repeatedFile.getName() + " повторился в цепочке рекурсии, далее он не будет рассматриваться");
    }

    @Override
    public void displayIOExceptionError(String fileName) {
        out.println("Почини файл, который ты сломал: " + fileName);
    }

    @Override
    public void displayIllegalFileFormatError() {
        out.println("Файл имеет неверный формат! Ищи, что могу сказать");
    }

    @Override
    public void displayHelp() throws IOException {
        JSONValue root = JSON.parse(new String(Files.readAllBytes(commandsDescriptionsPath), StandardCharsets.UTF_8));
        if (root == null || !root.isMap()) {
            System.out.println("Корень файла с помощью поврежден!");
            return;
        }
        JSONMap commands = root.asMap();
        for (Entry<String, JSONValue> commandEntry : commands.getValues().entrySet()) {
            if (!commandEntry.getValue().isMap()) {
                System.out.println("Команда в файле должна быть описана через набор пар ключ-значение!");
                return;
            }
            String commandName = commandEntry.getKey();
            Map<String, JSONValue> commandParamsMap = commandEntry.getValue().asMap().getValues();
            JSONValue commandDescription = commandParamsMap.get("description");
            JSONValue commandCoding = commandParamsMap.get("coding");
            if (commandDescription == null || !commandDescription.isString() ||
                    commandCoding == null || !commandCoding.isString()) {
                System.out.println("Список параметров команды в файле нарушен");
                return;
            }
            System.out.printf("%s: \n\tВызов: \"%s\"\n\tОписание: %s\n", commandName, commandCoding.asString().getValue(), commandDescription.asString().getValue());
        }
    }

    @Override
    public void displaySmthWentWrong() {
        out.println("Что-то пошло не так...");
    }

    @Override
    public void displaySuccess() {
        out.println("Успех!");
    }

    @Override
    public void displayInfo(DragonsInfoCommandSpecificAnswer specificAnswer) {
        out.printf("Тип коллекции: %s\nВремя создания: %s\nРазмер: %s\n", specificAnswer.getCollectionType(), specificAnswer.getCreationDate(), specificAnswer.getDragonsCount());
    }

    @Override
    public void displayTreasuresCounts(List<Double> uniqueTreasuresCounts) {
        out.println("Уникальные кол-ва сокровищ: ");
        for (Double treasureCount : uniqueTreasuresCounts) {
            out.printf("%.2f, ", treasureCount);
        }
        out.println();
    }

    @Override
    public void displayConnectionError() {
        out.println("Время подключения истекло!");
    }

    @Override
    public void displayMsgAboutWrongRegisterOrLoginChoose() {
        out.println("Неверный выбор типа: регистрация или вход");
    }

    @Override
    public void displayMsgAskingForEnterEntry() {
        out.println("Введите данные для входа: ");
    }

    @Override
    public void displayMsgAboutWrongEnterEntry() {
        out.println("Неверные данные для входа");
    }

    @Override
    public void displayMsgAskingForRegisterOrLogin() {
        out.println("Введите тип: вход или регистрация");
    }

    @Override
    public void displayDragonsWithDescription(List<DragonWithKeyAndOwner> dragonsWithDescription) {
        if (dragonsWithDescription.isEmpty()) {
            out.println("Драконов с этим описанием нет");
            return;
        }
        out.println("Драконы с этим описанием: ");
        for (DragonWithKeyAndOwner element : dragonsWithDescription) {
            out.println();
            out.println("Владелец: " + element.getOwnerLogin());
            out.println("Ключ: " + element.getKey());
            out.println("Элемент: ");
            Dragon dragon = element.getDragon();
            displayDragon(dragon);
        }
    }

    @Override
    public void displayLoginGettingMsg() {
        out.println("Введите логин: ");
    }

    @Override
    public void displayBlankLoginMsg() {
        out.println("Логин не может быть пустым");
    }

    @Override
    public void displayPasswordGettingMsg() {
        out.println("Введите пароль: ");
    }

    @Override
    public void displayBlankPasswordMsg() {
        out.println("Пароль не может быть пустым");
    }

    @Override
    public void displayWrongNetAPIMsg() {
        out.println("При общении с сервером возникла ошибка в общении (API)");
    }

    @Override
    public void displayRemovedCount(RemoveGreaterOrLowerDragonsCommandSpecificAnswer specificAnswer) {
        if (specificAnswer.getRemovedCount() == 0) {
            out.println("Ничего не было удалено");
        } else {
            out.println("Было удалено " + specificAnswer.getRemovedCount() + " дракона");
        }
    }

    @Override
    public void displayErrorWithoutDescriptionMsg() {
        out.println("На стороне сервера произошла ошибка без описания");
    }

    @Override
    public void displayErrorDescripiton(ErrorDescription errorDescription) throws IllegalArgumentException {
        out.println(String.format("На стороне сервера произошла ошибка \"%s\".", errorDescription.getErrorType()));
        if (errorDescription.getMessage() != null) {
            out.println(String.format(" Описание: \"%s\".", errorDescription.getMessage()));
        }
    }

    @Override
    public void displayServerKeysDidntSettedMsg() {
        out.println("Некоторые необходимые ключи не были помещены в ответ сервера");
    }

    @Override
    public void displayRegisterWrongLoginMsg() {
        out.println("Такой логин уже существует!");
    }

    @Override
    public void displaySignInWrongLoginMsg() {
        out.println("Такого пользователя не существует!");
    }

    @Override
    public void displayWrongPasswordMsg() {
        out.println("Неверный пароль");
    }

    @Override
    public void displayRegisteredMsg() {
        out.println("Регистрация прошла успешно");
    }

    @Override
    public void displaySignedInMsg() {
        out.println("Вход прошел успешно");
    }

    @Override
    public void displayInsertedMsg() {
        out.println("Вставка прошла успешно");
    }

    @Override
    public void displayDublicateKeyMsg() {
        out.println("Такой ключ уже существует");
    }

    @Override
    public void displayServerKeysWrongValuesMsg() {
        out.println("Некоторе из полученных ключей имеют неверные значения (ошибка API)");
    }

    @Override
    public void displayUpdatedDragonMsg() {
        out.println("Обновление прошло успешно");
        
    }

    @Override
    public void displayWrongDragonIdMsg() {
        out.println("Дракона с таким id не существует");
    }

    @Override
    public void displayWrongDragonKeyMsg() {
        out.println("Дракона с таким key не существует");
    }

    @Override
    public void displayNotYoursDragonMsg() {
        out.println("Дракон не ваш!");
    }

    @Override
    public void displayRemovedDragonMsg() {
        out.println("Удаление прошло успешно");
    }

    @Override
    public void displayIsntGreaterDragonMsg() {
        out.println("Дракон не больше, чем целевой");
    }

    @Override
    public void displayReplacedDragonMsg() {
        out.println("Замена прошла успешно");
    }

    @Override
    public void displayWingspansSum(WingspansSumGettingCommandSpecificAnswer specificAnswer) {
        out.println("Полученная сумма размахов крыльев: " + specificAnswer.getWingspansSum());
    }

    @Override
    public void displayUniqueNumbersOfTreasures(UniqueNumbersOfTreasuresGettingCommandSpecificAnswer specificAnswer) {
        out.println("Уникальные кол-ва сокровищ в пещерах:");
        for (var uniqueCaves : specificAnswer.getDragonCaves())
            out.println("\t" + uniqueCaves.getNumberOfTreasures());
    }

}
