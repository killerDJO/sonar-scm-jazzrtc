package org.sonar.plugins.scm.jazzrtc;

import org.sonar.api.utils.command.Command;
import org.sonar.api.utils.System2;

/**
 * Created by yevhenii.andrushchak on 2018-12-21.
 */
public class LscmCommandCreator {
    private final System2 system;
    private final JazzRtcConfiguration config;

    public LscmCommandCreator(System2 system, JazzRtcConfiguration config){
        this.system = system;
        this.config = config;
    }

    public Command createLscmCommand(String ...args) {
        String lscmPath = config.lscmPath();
        Command command = Command.create(lscmPath == null ? "lscm" : lscmPath);
        // SONARSCRTC-3 and SONARSCRTC-6
        if(system.isOsWindows()) {
            command.setNewShell(true);
        }

        command.addArguments(args);

        return command;
    }
}
