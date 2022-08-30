package ru.miron.TZ.logic.view;

import java.util.NoSuchElementException;

import ru.miron.TZ.logic.commands.Command;
import ru.miron.TZ.logic.commands.EnterEntry;

// Cannot extend Iterator<Command> because of specification breaking:
// https://rules.sonarsource.com/java/RSPEC-1849
public interface InView {
    /**
     * Gets next command. Client can interpret
     * {@link ru.miron.TZ.logic.commands.CommandName#EXIT}
     * as an exit signal
     * 
     * @return next command, null if the iteration has no next command
     * @throws NoSuchElementException if the command didn't end
     */
    Command nextCommand(EnterEntry enterEntry) throws NoSuchElementException;

    public enum RegisterOrLogin {
        REGISTER,
        LOGIN;
    }

    /**
     * @throws IllegalStateException if had any troubles with getting 
     */
    EnterEntry getEnterEntry(OutView outView) throws IllegalStateException;

    /**
     * @throws IllegalArgumentException if had any troubles with getting 
     */
    RegisterOrLogin chooseRegisterOrLogin() throws IllegalArgumentException;

    EnterEntry getRegisterOrLoginEntry(OutView outView);
}
