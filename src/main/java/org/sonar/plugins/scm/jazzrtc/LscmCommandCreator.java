package org.sonar.plugins.scm.jazzrtc;

import org.sonar.api.utils.command.Command;
import org.sonar.api.utils.System2;

/**
 * Created by yevhenii.andrushchak on 2018-12-21.
 */
public class LscmCommandCreator {
    private final System2 system;

    public LscmCommandCreator(System2 system){
        this.system = system;
    }

    public Command createLscmCommand(String ...args) {
        Command command = Command.create("lscm");
        // SONARSCRTC-3 and SONARSCRTC-6
        if(system.isOsWindows()) {
            command.setNewShell(true);
        }

        command.addArguments(args);

        return command;
    }
}
