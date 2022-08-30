package ru.miron.TZ;

import java.io.File;
import java.io.IOException;
import java.net.ConnectException;
import java.nio.file.Path;
import java.util.Scanner;

import ru.miron.TZ.logic.*;
import ru.miron.TZ.logic.commands.*;
import ru.miron.TZ.logic.commands.CommandAnswerWithoutArgs.EnterState;
import ru.miron.TZ.logic.commands.specificAnswers.*;
import ru.miron.TZ.logic.dao.*;
import ru.miron.TZ.logic.view.*;
import ru.miron.TZ.logic.view.InView.RegisterOrLogin;
import ru.miron.TZ.logic.view.views.*;

public class Main {
    public static Path configPath;

    static {
        configPath = new File("config.json").toPath();
    }

    public static void main(String[] args) throws Exception {
        OutView outView = new OutTextView(System.out, new File("commands_description.json").toPath());
        InView inView = new InTextView(outView, new Scanner(System.in, "IBM866"));
        Config config = Config.init(configPath);
        UDPClient udpClient = new UDPClient(config, config.connectionTryWaiting);
        EnterEntry enterEntry = login(udpClient, outView, inView);
        Command nextCommand;
        while ((nextCommand = inView.nextCommand(enterEntry)).getCommandName() != CommandName.EXIT) {
            if (nextCommand.getCommandName() == CommandName.HELP) {
                outView.displayHelp();
                continue;
            }
            CommandAnswer commandAnswer;
            try {
                commandAnswer = udpClient.tryToGetCommandAnswer(nextCommand);
            } catch (ConnectException e) {
                outView.displayConnectionError();
                continue;
            } catch (IllegalArgumentException e) {
                outView.displayWrongNetAPIMsg();
                continue;
            }
            var commandName = nextCommand.getCommandName();
            var newEnterEntry = nextCommand.getEnterEntry();
            var enterState = commandAnswer.getCommandAnswerWithoutArgs().getEnterState();
            setEnterEntryIfLogonOrRegistered(enterEntry, newEnterEntry, commandName, enterState);
            showCommandAnswer(nextCommand.getCommandName(), commandAnswer, outView);
        }
        outView.displayExitMsg();
    }

    private static void setEnterEntryIfLogonOrRegistered(EnterEntry enterEntry, EnterEntry newEnterEntry, CommandName commandName, EnterState newEnterState) {
        switch (commandName) {
            case REGISTER, SIGN_IN -> {
                if (newEnterState == EnterState.ENTERED) {
                    enterEntry.setEntry(newEnterEntry);
                }
            }
            default -> {
                return;
            }
        }
    }

    private static EnterEntry login(UDPClient udpClient, OutView outView, InView inView) throws IOException {
        while (true) {
            outView.displayMsgAskingForRegisterOrLogin();
            RegisterOrLogin registerOrLogin = getRegisterOrLoginChoice(outView, inView);
            CommandName commandName = getCommandNameFromRegisterOrLogin(registerOrLogin);
            EnterEntry enterEntry = inView.getRegisterOrLoginEntry(outView);
            Command commandToSend = new Command(commandName, enterEntry, null);
            CommandAnswer commandAnswer;
            try {
                commandAnswer = udpClient.tryToGetCommandAnswer(commandToSend);
            } catch (ConnectException e) {
                outView.displayConnectionError();
                continue;
            } catch (IllegalArgumentException e) {
                outView.displayWrongNetAPIMsg();
                continue;
            }
            showCommandAnswer(commandToSend.getCommandName(), commandAnswer, outView);
            var commandAnswerWithoutArgs = commandAnswer.getCommandAnswerWithoutArgs();
            if (!commandAnswerWithoutArgs.getIsError() && commandAnswerWithoutArgs.getEnterState() == EnterState.ENTERED) {
                return enterEntry; 
            }
        }
    }

    private static CommandName getCommandNameFromRegisterOrLogin(RegisterOrLogin registerOrLogin) {
        return switch (registerOrLogin) {
            case REGISTER -> CommandName.REGISTER;
            case LOGIN -> CommandName.SIGN_IN;
        };
    }


    private static RegisterOrLogin getRegisterOrLoginChoice(OutView outView, InView inView) {
        while (true) {
            try {
                return inView.chooseRegisterOrLogin();
            } catch (IllegalArgumentException e) {
                outView.displayMsgAboutWrongRegisterOrLoginChoose();
                continue;
            }
        }
    }


    /**
     * @throws ClassCastException if command specific answer doesn't correspond commandName
     */
    private static void showCommandAnswer(CommandName commandName, CommandAnswer commandAnswer, OutView outView) throws ClassCastException {
        var errorFlag = commandAnswer.getCommandAnswerWithoutArgs().getIsError();
        if (errorFlag) {
            var errorDescription = commandAnswer.getCommandAnswerWithoutArgs().getErrorDescription();
            if (errorDescription == null) {
                outView.displayErrorWithoutDescriptionMsg();
            } else {
                outView.displayErrorDescripiton(errorDescription);
            }
            return;
        }
        var enterState = commandAnswer.getCommandAnswerWithoutArgs().getEnterState();
        switch (enterState) {
            case ENTERED ->{
                break;
            }
            case WRONG_LOGIN -> {
                switch (commandName) {
                    case REGISTER -> {
                        outView.displayRegisterWrongLoginMsg();
                    }
                    case SIGN_IN -> {
                        outView.displaySignInWrongLoginMsg();
                    }
                    default -> {
                        throw new IllegalStateException();
                    }
                }
                return;
            }
            case WRONG_PASSWORD -> {
                outView.displayWrongPasswordMsg();
                return;
            }
        }
        switch (commandName) { // todo: свернуть в ф-ции
            case REGISTER -> {
                outView.displayRegisteredMsg();
            }
            case SIGN_IN -> {
                outView.displaySignedInMsg();
            }
            case INSERT_DRAGON -> {
                var state = commandAnswer.getCommandAnswerWithoutArgs().getState();
                if (state == null) {
                    outView.displayServerKeysDidntSettedMsg();
                }
                switch (state) {
                    case "inserted" -> {
                        outView.displayInsertedMsg();
                    }
                    case "dublicate key" -> {
                        outView.displayDublicateKeyMsg();
                    }
                    default -> {
                        outView.displayServerKeysWrongValuesMsg();
                    }
                }
            }
            case GET_DRAGONS -> {
                var specificAnswer = (DragonsGettingCommandSpecificAnswer) commandAnswer.getCommandSpecificAnswerObj();
                outView.displayDragonsWithDescription(specificAnswer.getDragons());
            }
            case DRAGONS_INFO -> {
                var specificAnswer = (DragonsInfoCommandSpecificAnswer) commandAnswer.getCommandSpecificAnswerObj();
                outView.displayInfo(specificAnswer);
            }
            case UPDATE_DRAGON -> {
                var state = commandAnswer.getCommandAnswerWithoutArgs().getState();
                if (state == null) {
                    outView.displayServerKeysDidntSettedMsg();
                }
                switch (state) {
                    case "updated" -> {
                        outView.displayUpdatedDragonMsg();
                    }
                    case "wrong id" -> {
                        outView.displayWrongDragonIdMsg();
                    }
                    case "not yours" -> {
                        outView.displayNotYoursDragonMsg();
                    }
                    default -> {
                        outView.displayServerKeysWrongValuesMsg();
                    }
                }
            }
            case REMOVE_DRAGON_BY_KEY -> {
                var state = commandAnswer.getCommandAnswerWithoutArgs().getState();
                if (state == null) {
                    outView.displayServerKeysDidntSettedMsg();
                }
                switch (state) {
                    case "removed" -> {
                        outView.displayRemovedDragonMsg();
                    }
                    case "wrong key" -> {
                        outView.displayWrongDragonKeyMsg();
                    }
                    case "not yours" -> {
                        outView.displayNotYoursDragonMsg();
                    }
                    default -> {
                        outView.displayServerKeysWrongValuesMsg();
                    }
                }
            }
            case CLEAR_DRAGONS -> {
                var specificAnswer = (ClearDragonsCommandSpecificAnswer) commandAnswer.getCommandSpecificAnswerObj();
                outView.displayClearedCount(specificAnswer);
            }
            case REMOVE_GREATER_DRAGONS, REMOVE_LOWER_DRAGONS -> {
                var specificAnswer = (RemoveGreaterOrLowerDragonsCommandSpecificAnswer) commandAnswer.getCommandSpecificAnswerObj();
                outView.displayRemovedCount(specificAnswer);
            }
            case REPLACE_IF_DRAGON_IS_GREATER -> {
                var state = commandAnswer.getCommandAnswerWithoutArgs().getState();
                if (state == null) {
                    outView.displayServerKeysDidntSettedMsg();
                }
                switch (state) {
                    case "replaced" -> {
                        outView.displayReplacedDragonMsg();
                    }
                    case "isn't greater" -> {
                        outView.displayIsntGreaterDragonMsg();
                    }
                    case "wrong key" -> {
                        outView.displayWrongDragonKeyMsg();
                    }
                    case "not yours" -> {
                        outView.displayNotYoursDragonMsg();
                    }
                    default -> {
                        outView.displayServerKeysWrongValuesMsg();
                    }
                }
            }
            case GET_WINGSPANS_SUM -> {
                var specificAnswer = (WingspansSumGettingCommandSpecificAnswer) commandAnswer.getCommandSpecificAnswerObj();
                outView.displayWingspansSum(specificAnswer);
            }
            case GET_DRAGONS_WITH_THIS_DESCRIPTION -> {
                var specificAnswer = (DragonsWithThisDescriptionGettingCommandSpecificAnswer) commandAnswer.getCommandSpecificAnswerObj();
                outView.displayDragonsWithDescription(specificAnswer.getDragons());
            }
            case GET_UNIQUE_NUMBERS_OF_TREASURES -> {
                var specificAnswer = (UniqueNumbersOfTreasuresGettingCommandSpecificAnswer) commandAnswer.getCommandSpecificAnswerObj();
                outView.displayUniqueNumbersOfTreasures(specificAnswer);;
            } 
            default -> 
                throw new IllegalStateException();
        }
    }
}