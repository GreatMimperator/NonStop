package ru.miron.TZ.logic.view;

import java.io.File;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Map;
import java.util.Set;

import ru.miron.TZ.givenClasses.Dragon;
import ru.miron.TZ.givenClasses.DragonCave;
import ru.miron.TZ.givenClasses.DragonWithKeyAndOwner;
import ru.miron.TZ.logic.commands.CommandAnswer;
import ru.miron.TZ.logic.commands.ErrorDescription;
import ru.miron.TZ.logic.commands.specificAnswers.ClearDragonsCommandSpecificAnswer;
import ru.miron.TZ.logic.commands.specificAnswers.DragonsInfoCommandSpecificAnswer;
import ru.miron.TZ.logic.commands.specificAnswers.RemoveGreaterOrLowerDragonsCommandSpecificAnswer;
import ru.miron.TZ.logic.commands.specificAnswers.UniqueNumbersOfTreasuresGettingCommandSpecificAnswer;
import ru.miron.TZ.logic.commands.specificAnswers.WingspansSumGettingCommandSpecificAnswer;

/**
 * @see #ru.miron.TZ.logic.DragonsHashTableControl
 */
public interface OutView {
    /**
     * Displays help
     */
    void displayHelp() throws Exception;

    /**
     * Displays Dragon's map info
     * 
     * @param toDisplayInfoAbout map of Dragons to display
     * @param creationTime creationTime of this map
     */
    void displayInfo(Map<String, Dragon> toDisplayInfoAbout, ZonedDateTime creationTime);

    /**
     * Displays unique DragonCave's
     * 
     * @param caves caves to display
     */
    void displayCaveValues(Set<DragonCave> caves);

    /**
     * Displays key exist exception
     * 
     * @param key key which exists - it shouldn't be
     * @param dragon Dragons which could be added with this key
     */
    void displayKeyExistsException(String key, Dragon dragon);

    /**
     * Displays key doesn't exist exception
     * 
     * @param key key which doesn't exist - it should be
     */
    void displayKeyDoesntExistException(String key);

    /**
     * Displays id doesn't exist exception
     * 
     * @param id id which doesn't exist - it should be
     */
    void displayIdDoesntExistException(Long id);

    /**
     * Displays exit message
     */
    void displayExitMsg();

    /**
     * Displays removed Dragons count got from specific answer
     */
    void displayRemovedCount(RemoveGreaterOrLowerDragonsCommandSpecificAnswer specificAnswer);

    /**
     * Displays cleared Dragons count got from specific answer
     */
    void displayClearedCount(ClearDragonsCommandSpecificAnswer specificAnswer);

    /**
     * Displays state of replacing Dragon
     * 
     * @param replaced if replaced - {@code true}, if didn't - {@code false}
     */
    void displayReplacedStateMsg(boolean replaced);

    /**
     * Displays wingspan sum of Dragons
     * 
     * @param sum sum to display
     */
    void displayWingspanSum(long sum);

    /**
     * Displays missing file from command arg exception
     * 
     * @param fileName name of file (from command arg) doesn't exist
     */
    void displayMissingFileFromCommandArg(String fileName);

    /**
     * Displays missing file from user exception
     * 
     * @param fileName name of file (from user) doesn't exist
     */
    void displayMissingFileFromUser(String fileName);

    /**
     * Displays msg: collection is empty
     */
    void displayEmptyCollectionMsg();

    /**
     * Displays dragon entries with concrete description
     * 
     * @param dragonsEntriesWithDescription entries to display
     */
    void displayDragonsWithDescription(Map<String, Dragon> dragonsEntriesWithDescription);

    /**
     * Displays, that command arg is empty
     */
    void displayEmptyCommandArg();

    /**
     * Displays enter command msg
     */
    void displayEnterCommandMsg();

    /**
     * Displays empty commandLine error
     */
    void displayEmptyCommandLineError();

    /**
     * Displays wrong commandName error
     * 
     * @param enteredCommandNameStr entered "commandName"
     */
    void displayWrongCommandNameError(String enteredCommandNameStr);

    /**
     * Displays wrong number of entered args error
     * 
     * @param size entered args list size
     */
    void displayWrongNumberOfEnteredArgsError(int size);

    /**
     * Displays wrong entered file path error
     * 
     * @param enteredScriptFileName entered "script fileName"
     */
    void displayWrongFilePathError(String enteredScriptFileName);

    /**
     * Displays id is not positive error
     * 
     * @param enteredId entered id which isn't positive
     */
    void displayIdIsNotPositiveError(long enteredId);

    /**
     * Displays wrong id number format error
     * 
     * @param enteredIdString entered "id"
     */
    void displayIdNumberFormatError(String enteredIdString);

    /**
     * Displays age is not positive error
     * 
     * @param enteredAge entered age which isn't positive
     */
    void displayAgeIsNotPositiveError(long enteredAge);


    /**
     * Displays wrong age number format error
     * 
     * @param enteredAgeString entered "age"
     */
    void displayAgeNumberFormatError(String enteredAgeString);

    /**
     * Displays wingspan is not positive error
     * 
     * @param enteredWingspan entered wingspan which isn't positive
     */
    void displayWingspanIsNotPositiveError(int enteredWingspan);

    /**
     * Displays wrong wingspan number format error
     * 
     * @param enteredWingspanString entered "wingspan"
     */
    void displayWingspanNumberFormatError(String enteredWingspanString);

    /**
     * Displays msg before treasuresNumber getting
     */
    void displayMsgBeforeTreasuresNumberGetting();


    /**
     * Displays blank treasuresNumber string error
     */
    void displayBlankTreasuresNumberStringError();

    /**
     * Displays treasuresNumber is not positive error
     * 
     * @param enteredTreasuresNumber entered treasuresNumber which isn't positive
     */
    void displayTreasuresNumberIsNotPositiveError(float enteredTreasuresNumber);

    /**
     * Displays wrong treasures number format error
     * 
     * @param enteredTreasuresNumberString entered "treasuresNumber"
     */
    void displayTreasuresNumberFormatError(String enteredTreasuresNumberString);

    /**
     * Displays msg before key getting
     */
    void displayMsgBeforeKeyGetting();

    /**
     * Displays msg before coordinates object getting
     */
    void displayMsgBeforeCoordinatesGetting();

    /**
     * Displays msg before y oxis value getting
     */
    void displayMsgBeforeYOxisValueGetting();

    /**
     * Displays msg before x oxis value getting
     */
    void displayMsgBeforeXOxisValueGetting();

    /**
     * Displays blank x oxis string error
     */
    void displayBlankXOxisStringError();

    /**
     * Displays blank y oxis string error
     */
    void displayBlankYOxisStringError();

    /**
     * Displays y oxis value format error
     * 
     * @param enteredYString entered "y oxis value"
     */
    void displayYOxisNumberFormatError(String enteredYString);

    /**
     * Displays x oxis value format error
     * 
     * @param enteredXString entered "x oxis value"
     */
    void displayXOxisNumberFormatError(String enteredXString);

    /**
     * Displays msg before dragon type getting
     */
    void displayMsgBeforeDragonTypeGetting();

    /**
     * Displays dragonType name error
     * 
     * @param enteredDragonTypeString entered "dragonType"
     */
    void displayDragonTypeNameError(String enteredDragonTypeString);

    /**
     * Displays that didn't end command
     */
    void displayDidntEndCommandError();

    /**
     * Displays levels of exec left error
     */
    void displayLevelsOfExecLeftError();

    /**
     * Displays repetition in file recursion error
     * 
     * @param repeatedFile repeated file
     */
    void displayRepetitionInFileRecursionError(File repeatedFile);

    /**
     * Displays problems with file msg
     * 
     * @param fileName
     */
    void displayIOExceptionError(String fileName);

    /**
     * Displays illegal file format Error
     */
    void displayIllegalFileFormatError();

    /**
     * Displays smth wrong went
     */
    void displaySmthWentWrong();

    /**
     * Displays success
     */
    void displaySuccess();

    void displayInfo(DragonsInfoCommandSpecificAnswer specificAnswer);

    void displayTreasuresCounts(List<Double> uniqueTreasuresCounts);

    void displayConnectionError();

    void displayMsgAboutWrongRegisterOrLoginChoose();

    void displayMsgAskingForEnterEntry();

    void displayMsgAboutWrongEnterEntry();

    void displayMsgAskingForRegisterOrLogin();

    void displayDragonsWithDescription(List<DragonWithKeyAndOwner> dragons_with_description);

    void displayLoginGettingMsg();

    void displayBlankLoginMsg();

    void displayPasswordGettingMsg();

    void displayBlankPasswordMsg();

    void displayWrongNetAPIMsg();

    void displayErrorWithoutDescriptionMsg();

    /**
     * @throws  IllegalArgumentException if errorDescription is null
     */
    void displayErrorDescripiton(ErrorDescription errorDescription) throws IllegalArgumentException;

    void displayServerKeysDidntSettedMsg();

    void displayRegisterWrongLoginMsg();

    void displaySignInWrongLoginMsg();

    void displayWrongPasswordMsg();

    void displayRegisteredMsg();

    void displaySignedInMsg();

    void displayInsertedMsg();

    void displayDublicateKeyMsg();

    void displayServerKeysWrongValuesMsg();

    void displayUpdatedDragonMsg();

    void displayWrongDragonIdMsg();

    void displayNotYoursDragonMsg();

    void displayRemovedDragonMsg();

    void displayIsntGreaterDragonMsg();

    void displayReplacedDragonMsg();

    void displayWingspansSum(WingspansSumGettingCommandSpecificAnswer specificAnswer);

    void displayUniqueNumbersOfTreasures(UniqueNumbersOfTreasuresGettingCommandSpecificAnswer specificAnswer);

    void displayWrongDragonKeyMsg();

}
