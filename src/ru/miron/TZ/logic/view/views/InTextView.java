package ru.miron.TZ.logic.view.views;

import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Scanner;

import ru.miron.TZ.givenClasses.Coordinates;
import ru.miron.TZ.givenClasses.Dragon;
import ru.miron.TZ.givenClasses.DragonCave;
import ru.miron.TZ.givenClasses.DragonType;
import ru.miron.TZ.givenClasses.DragonWithKeyAndOwner;
import ru.miron.TZ.givenClasses.Dragon.DragonBuilder;
import ru.miron.TZ.logic.commands.Command;
import ru.miron.TZ.logic.commands.CommandName;
import ru.miron.TZ.logic.commands.EnterEntry;
import ru.miron.TZ.logic.commands.args.*;
import ru.miron.TZ.logic.util.json.JSONConvertable;
import ru.miron.TZ.logic.view.InView;
import ru.miron.TZ.logic.view.OutView;

import static ru.miron.TZ.logic.commands.CommandName.*;

public class InTextView implements InView {

    /**
     * text stream which should be autoFlushable
     */
    private Scanner sc;

    private OutView outView;

    public InTextView(OutView outView, Scanner scanner) {
        setOutView(outView);
        setSc(scanner);
    }

    public void setSc(Scanner scanner) {
        sc = scanner;
    }

    public void setOutView(OutView outView) throws NullPointerException {
        if (outView == null) {
            throw new NullPointerException();
        }
        this.outView = outView;
    }

    // name, age, description, wingspan
    public static final int FIRST_LINE_DRAGON_ARGS_COUNT = 4;
    // coordinates, type, cave
    public static final int COMPLEX_DRAGON_ARGS_COUNT = 3;

    @Override
    public Command nextCommand(EnterEntry enterEntry) throws NoSuchElementException {
        out:
        while(true) {
            outView.displayEnterCommandMsg();
            String commandLine;
            commandLine = sc.nextLine().trim();
            if (commandLine.isEmpty()) {
                outView.displayEmptyCommandLineError();
                continue out;
            }
            int spaceIndex = commandLine.indexOf(" ");
            String commandNameStr = commandLine.substring(0, spaceIndex == -1 ? commandLine.length() : spaceIndex);
            CommandName commandName;
            try {
                commandName = interpretCommandName(commandNameStr);
            } catch (IllegalArgumentException e) {
                outView.displayWrongCommandNameError(commandNameStr);
                continue out;
            }
            List<String> args = new LinkedList<>();
            if (commandNameStr.length() != commandLine.length()) {
                String[] argsAsStrArray = commandLine.substring(commandNameStr.length(), commandLine.length()).trim().split("\\|");
                for (int i = 0; i < argsAsStrArray.length; i++) {
                    args.add(argsAsStrArray[i].trim());
                }
            }
            if (!isCommandLineArgsSizeOk(commandName, args.size())) {
                outView.displayWrongNumberOfEnteredArgsError(args.size());
                continue out;
            }
            JSONConvertable commandArgsObj = null;
            boolean isEmptyArgsCommand = false;
            switch (commandName) {
                case GET_DRAGONS_WITH_THIS_DESCRIPTION, 
                        REMOVE_DRAGON_BY_KEY -> { // first_arg
                    try {
                        commandArgsObj = getFirstArgCommandArgs(commandName, args.get(0));
                    } catch (IllegalArgumentException e) {
                        continue;
                    }
                }
                case REMOVE_GREATER_DRAGONS, 
                        REMOVE_LOWER_DRAGONS -> { // {element}
                    try {
                        commandArgsObj = getDragonParamsCommandArgs(commandName, args);
                    } catch (IllegalArgumentException e) {
                        continue;
                    }
                }
                case INSERT_DRAGON,
                        REPLACE_IF_DRAGON_IS_GREATER, 
                        UPDATE_DRAGON -> { // first_arg {element}
                    try {
                        commandArgsObj = getFirstArgAndDragonParamsCommandArgs(commandName, args);
                    } catch (IllegalArgumentException e) {
                        continue;
                    }
                }
                case HELP,
                        DRAGONS_INFO,
                        GET_DRAGONS,
                        CLEAR_DRAGONS,
                        EXIT,
                        GET_WINGSPANS_SUM,
                        GET_UNIQUE_NUMBERS_OF_TREASURES -> {// empty args
                    isEmptyArgsCommand = true;
                }
                case REGISTER,
                        SIGN_IN -> { // register and sign in case 
                    return new Command(commandName, getRegisterOrLoginEntry(outView), null);
                }
                default -> {
                    throw new IllegalStateException("Bruh...");
                }
            }
            if (commandArgsObj == null && !isEmptyArgsCommand) {
                continue out;
            }
            return new Command(commandName, enterEntry, commandArgsObj);
        }
    }

    public EnterEntry getRegisterOrLoginEntry(OutView outView) {
        while (true) {
            outView.displayMsgAskingForEnterEntry();
            try {
                return this.getEnterEntry(outView);
            } catch (IllegalStateException e) {
                outView.displayMsgAboutWrongEnterEntry();
                continue;
            }
        }
    }


    /**
     * Gets arg object of commands with first arg and dragon args
     * 
     * @param commandName commandName
     * @param args command args, first is first arg, all after is prim. args you can see here: {@link #setPrimitiveArgsInConsole(DragonBuilder, Iterator)}. {@code Iterator} here is {@code args.iterator()}
     * @return {@code null}, if has troubles with interpreting first arg or dragon params, commandArgs object else
     * @throws NoSuchElementException if scanner hasn't lines to read
     */
    private JSONConvertable getFirstArgAndDragonParamsCommandArgs(CommandName commandName, List<String> args) throws IllegalArgumentException {
        JSONConvertable commandArgsObj;
        var argsIterator = args.iterator();
        var firstArg = argsIterator.next();
        DragonWithKeyAndOwner dragonWithKey;
        switch (commandName) {
            case INSERT_DRAGON:
            case REPLACE_IF_DRAGON_IS_GREATER:
                var key = firstArg;
                Dragon dragon;
                dragon = getDragonWithRequiredFieldsFromConsole(argsIterator);
                dragonWithKey = new DragonWithKeyAndOwner(dragon, key, null);
                switch (commandName) {
                    case INSERT_DRAGON:
                        commandArgsObj = new InsertCommandArgs(dragonWithKey);
                        break;
                    case REPLACE_IF_DRAGON_IS_GREATER:
                        commandArgsObj = new ReplaceIfDragonIsGreaterCommandArgs(key, dragonWithKey);
                        break;
                    default:
                        throw new IllegalStateException("マイロン、これは不可能です！");
                }
                break;
            case UPDATE_DRAGON:
                long id;
                try {
                    id = Long.parseLong(firstArg);
                    if (id > 0 == false) {
                        outView.displayIdIsNotPositiveError(id);
                        return null;
                    }
                } catch (NumberFormatException e) {
                    outView.displayIdNumberFormatError(firstArg);
                    return null;
                }
                try {
                    dragonWithKey = getDragonWithRequiredFieldsAndKeyFromConsole(argsIterator);
                } catch (IllegalArgumentException e) {
                    throw new IllegalArgumentException();
                }
                commandArgsObj = new UpdateCommandArgs(id, dragonWithKey);
                break;
            default:
                throw new IllegalStateException("マイロン、これは不可能です！");
        }
        return commandArgsObj;
    }

    /**
     * Gets arg object of commands with only dragon args
     * @throws IllegalArgumentException if has problems with getting
     */
    private JSONConvertable getDragonParamsCommandArgs(CommandName commandName, List<String> args) throws IllegalArgumentException {
        JSONConvertable commandArgsObj;
        Dragon dragon;
        dragon = getDragonWithRequiredFieldsFromConsole(args.iterator());
        switch (commandName) {
            case REMOVE_LOWER_DRAGONS:
                commandArgsObj = new RemoveLowerOrGreaterDragonsCommandArgs(dragon);
                break;
            case REMOVE_GREATER_DRAGONS:
                commandArgsObj = new RemoveLowerOrGreaterDragonsCommandArgs(dragon);
                break;
            default:
                throw new IllegalStateException("マイロン、これは不可能です！");
        }
        return commandArgsObj;
    }

    /**
     * Gets arg object of commands with only firstArg
     * 
     * @param commandName commandName
     * @param firstArg first arg of the command
     * @return {@code null}, if has troubles with interpreting firstArg, commandArgs object else 
     */
    public JSONConvertable getFirstArgCommandArgs(CommandName commandName, String firstArg) throws IllegalArgumentException {
        JSONConvertable commandArgsObj;
        switch (commandName) {
            case GET_DRAGONS_WITH_THIS_DESCRIPTION:
                String description = firstArg;
                commandArgsObj = new GetDragonsWithThisDescriptionCommandArgs(description);
                break;
            case REMOVE_DRAGON_BY_KEY:
                String key = firstArg;
                commandArgsObj = new RemoveDragonByKeyCommandArgs(key);
                break;
            default:
                throw new IllegalStateException("マイロン、これは不可能です！");
        }
        return commandArgsObj;
    }

    /**
     * @throws IllegalArgumentException if has troubles with setting
     */
    public DragonWithKeyAndOwner getDragonWithRequiredFieldsAndKeyFromConsole(Iterator<String> firstLineArgsIterator) {
        var dragon = getDragonWithRequiredFieldsFromConsole(firstLineArgsIterator);
        var key = getKeyFromConsole();
        return new DragonWithKeyAndOwner(dragon, key, null);
    }

    /**
     * @throws IllegalArgumentException if has troubles with setting
     */
    public String getKeyFromConsole() {
        outView.displayMsgBeforeKeyGetting();
        return sc.nextLine().trim();
    }
    

    /**
     * @throws IllegalArgumentException if has troubles with setting
     */
    public Dragon getDragonWithRequiredFieldsFromConsole(Iterator<String> firstLineArgsIterator) throws IllegalArgumentException {
        DragonBuilder dragonBuilder = new DragonBuilder();
        var settedFirstLineArgs = setFirstLineArgsInConsole(dragonBuilder, firstLineArgsIterator);
        if (!settedFirstLineArgs) {
            throw new IllegalArgumentException();
        }
        setComplexRequiredArgsInConsole(dragonBuilder);
        return dragonBuilder.build();
    }

    /**
     * Gets complex args from console user and puts into dragon builder
     */
    public void setComplexRequiredArgsInConsole(DragonBuilder dragonBuilder) {
        while (true) {
            try {
                dragonBuilder.setCoordinates(getCoordinatesFromConsoleUser());
                dragonBuilder.setDragonType(getDragonTypeFromConsoleUser());
                dragonBuilder.setDragonCave(getDragonCaveFromConsoleUser());
                return; 
            } catch (NoSuchElementException e) {}
        }
    }

    /**
     * Puts first line args from iterator into dragon builder
     * 
     * @param dragonBuilder builder which gets first line args inside
     * @param argsIterator name, age, description and wingspan container (gets from iterator)
     * @return {@code false} if ageString or wingspanString cannot be converted to its containers, it says user about it, {@code true} else
     */
    public boolean setFirstLineArgsInConsole(DragonBuilder dragonBuilder, Iterator<String> argsIterator) {
        return setFirstLineArgsInConsole(dragonBuilder, argsIterator.next(), argsIterator.next(), argsIterator.next(), argsIterator.next());
    }

    /**
     * Puts first line args into dragon builder
     * 
     * @param dragonBuilder builder which gets primitive args inside
     * @param name name placed in the builder
     * @param ageString age placed in the builder
     * @param description description placed in the builder
     * @param wingspanString wingspan placed in the builder
     * @return {@code false} if ageString or wingspanString cannot be converted to its containers, it says user about it, {@code true} else
     */
    public boolean setFirstLineArgsInConsole(DragonBuilder dragonBuilder, String name, String ageString, String description, String wingspanString) {
        long age;
        try {
            age = Long.parseLong(ageString);
            if (age <= 0) {
                outView.displayAgeIsNotPositiveError(age);
                return false;
            }
        } catch (NumberFormatException e) {
            outView.displayAgeNumberFormatError(ageString);
            return false;
        }
        int wingspan;
        try {
            wingspan = Integer.parseInt(wingspanString);
            if (wingspan <= 0) {
                outView.displayWingspanIsNotPositiveError(wingspan);
                return false;
            }
        } catch (NumberFormatException e) {
            outView.displayWingspanNumberFormatError(wingspanString);
            return false;
        }
        dragonBuilder.setAge(age);
        dragonBuilder.setWingspan(wingspan);
        dragonBuilder.setName(name);
        dragonBuilder.setDescription(description);
        return true;
    }



    /**
     * Gets DragonCave from console user
     * 
     * @return received DragonCave
     * @throws NoSuchElementException if scanner hasn't lines to read
     */
    private DragonCave getDragonCaveFromConsoleUser() throws NoSuchElementException {
        while(true) {
            outView.displayMsgBeforeTreasuresNumberGetting();
            String treasuresNumberString = sc.nextLine().trim();
            if (treasuresNumberString.isEmpty()) {
                outView.displayBlankTreasuresNumberStringError();
            } else {
                try {
                    float treasuresNumber = Float.parseFloat(treasuresNumberString);
                    if (treasuresNumber <= 0) {
                        outView.displayTreasuresNumberIsNotPositiveError(treasuresNumber);
                        continue;
                    }
                    return new DragonCave(treasuresNumber);
                } catch (NumberFormatException e) {
                    outView.displayTreasuresNumberFormatError(treasuresNumberString);
                }
            }
        }
    }

    /**
     * Gets DragonType from console user
     * 
     * @return received DragonType
     * @throws NoSuchElementException if scanner hasn't lines to read
     */
    private DragonType getDragonTypeFromConsoleUser() throws NoSuchElementException {
        while (true) {
            outView.displayMsgBeforeDragonTypeGetting();
            String dragonTypeString = sc.nextLine().trim();
            try {
                return consoleStringToDragonType(dragonTypeString);
            } catch (IllegalArgumentException e) {
                outView.displayDragonTypeNameError(dragonTypeString);
            }
        }
    }

    /**
     * Gets coordinates from console user
     * 
     * @return received coordinates
     * @throws NoSuchElementException if scanner hasn't lines to read
     */
    private Coordinates getCoordinatesFromConsoleUser() throws NoSuchElementException {
        outView.displayMsgBeforeCoordinatesGetting();
        float x = -1;
        Long y = -1L;
        boolean ySetted = false;
        do {
            outView.displayMsgBeforeYOxisValueGetting();
            String yString = sc.nextLine();
            if (yString.trim().isEmpty()) {
                outView.displayBlankYOxisStringError();
            } else {
                try {
                    y = Long.parseLong(yString);
                    ySetted = true;
                } catch (NumberFormatException e) {
                    outView.displayYOxisNumberFormatError(yString);
                }
            }
        } while (!ySetted);
        boolean xSetted = false;
        do {
            outView.displayMsgBeforeXOxisValueGetting();
            String xString = sc.nextLine();
            if (xString.trim().isEmpty()) {
                outView.displayBlankXOxisStringError();
            } else {
                try {
                    x = Float.parseFloat(xString);
                    xSetted = true;
                } catch (NumberFormatException e) {
                    outView.displayXOxisNumberFormatError(xString);
                }
            }
        } while (!xSetted);
        return new Coordinates(x, y);
    }

    /**
     * @throws IllegalArgumentException if didn't define dragon type by string 
     */
    private static DragonType consoleStringToDragonType(String string) throws IllegalArgumentException {
        return switch (string.toLowerCase()) {
            case "water" -> DragonType.WATER;
            case "underground" -> DragonType.UNDERGROUND;
            case "air" -> DragonType.AIR;
            case "fire" -> DragonType.FIRE;
            default -> throw new IllegalArgumentException();
        };
    }

    /**
     * Checks args size - every command should have concrete args size
     * 
     * @param commandName which command
     * @param size args size which corresponding to command should be checked
     * @return does args size correspond to command
     */
    public static boolean isCommandLineArgsSizeOk(CommandName commandName, int size) {
        switch (commandName) {
            case HELP:
            case DRAGONS_INFO:
            case GET_DRAGONS:
            case CLEAR_DRAGONS:
            case EXIT:
            case GET_WINGSPANS_SUM:
            case GET_UNIQUE_NUMBERS_OF_TREASURES:
            case REGISTER:
            case SIGN_IN:
                return size == 0;
            case REMOVE_DRAGON_BY_KEY:
            case GET_DRAGONS_WITH_THIS_DESCRIPTION:
                return size == 1;
            case REMOVE_GREATER_DRAGONS:
            case REMOVE_LOWER_DRAGONS:
                return size == FIRST_LINE_DRAGON_ARGS_COUNT;
            case INSERT_DRAGON:
            case UPDATE_DRAGON:
            case REPLACE_IF_DRAGON_IS_GREATER:
                return size == 1 + FIRST_LINE_DRAGON_ARGS_COUNT;
            default:
                throw new IllegalStateException("マイロン、これは不可能です！");
        }
    }

    /**
     * @throws IllegalArgumentException if didn't define command name by string 
     */
    public static CommandName interpretCommandName(String commandName) {
        return switch (commandName) {
            case "help" -> HELP;
            case "info" -> DRAGONS_INFO;
            case "show" -> GET_DRAGONS;
            case "insert" -> INSERT_DRAGON;
            case "update" -> UPDATE_DRAGON;
            case "remove_key" -> REMOVE_DRAGON_BY_KEY;
            case "clear" -> CLEAR_DRAGONS;
            case "exit" -> EXIT;
            case "remove_greater" -> REMOVE_GREATER_DRAGONS;
            case "remove_lower" -> REMOVE_LOWER_DRAGONS;
            case "replace_if_greater" -> REPLACE_IF_DRAGON_IS_GREATER;
            case "sum_of_wingspan" -> GET_WINGSPANS_SUM;
            case "filter_by_description" -> GET_DRAGONS_WITH_THIS_DESCRIPTION;
            case "print_unique_cave" -> GET_UNIQUE_NUMBERS_OF_TREASURES; 
            case "register" -> REGISTER;
            case "login" -> SIGN_IN;
            default -> throw new IllegalArgumentException();
        };
    }

    private static String[] registerStringsInLower = new String[] {
        "r",
        "register",
        "р",
        "регистр",
        "регистрация",
        "зарегистрироваться"
    };

    private static String[] loginStringsInLower = new String[] {
        "l",
        "login",
        "в",
        "войти",
        "вход"
    };

    /**
     * @return REGISTER if register, LOGIN if login, if not of any of this - {@code null}
     * @throws IllegalArgumentException if string didn't match to known cases
     */
    public static RegisterOrLogin getRegisterOrLoginByString(String string) {
        if (isRegisterString(string)) {
            return RegisterOrLogin.REGISTER;
        }
        if (isLoginString(string)) {
            return RegisterOrLogin.LOGIN;
        }
        throw new IllegalArgumentException();
    }

    public static boolean isRegisterString(String string) {
        return isInLower(string, registerStringsInLower);
    }

    public static boolean isLoginString(String string) {
        return isInLower(string, loginStringsInLower);
    }

    public static boolean isInLower(String string, String[] checkIn) {
        string = string.toLowerCase();
        return Arrays.asList(checkIn).contains(string);
    }

    @Override
    public EnterEntry getEnterEntry(OutView outView) {
        while(true) {
            outView.displayLoginGettingMsg();
            String login = sc.nextLine().trim();
            if (login.isEmpty()) {
                outView.displayBlankLoginMsg();
                continue;
            }
            outView.displayPasswordGettingMsg();
            String password = sc.nextLine().trim();
            if (password.isEmpty()) {
                outView.displayBlankPasswordMsg();
                continue;
            }
            return new EnterEntry(login, password);
        }
    }

    @Override
    public RegisterOrLogin chooseRegisterOrLogin() throws IllegalArgumentException {
        return getRegisterOrLoginByString(sc.nextLine().trim());
    }
}
