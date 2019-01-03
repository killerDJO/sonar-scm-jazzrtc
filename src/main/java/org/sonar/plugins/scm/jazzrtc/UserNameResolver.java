/*
 * SonarQube :: Plugins :: SCM :: Jazz RTC
 * Copyright (C) 2014-2016 SonarSource SA
 * mailto:contact AT sonarsource DOT com
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */
package org.sonar.plugins.scm.jazzrtc;

import com.ibm.team.repository.client.*;
import com.ibm.team.repository.client.internal.TeamRepository;
import com.ibm.team.repository.client.login.UsernameAndPasswordLoginInfo;
import com.ibm.team.repository.common.IContributorHandle;
import com.ibm.team.repository.common.IContributorIdentity;
import com.ibm.team.repository.common.IContributorRecord;
import com.ibm.team.repository.common.model.query.BaseContributorQueryModel;
import com.ibm.team.repository.common.query.IItemQuery;
import com.ibm.team.repository.common.query.IItemQueryPage;
import com.ibm.team.repository.common.query.ast.IPredicate;
import com.ibm.team.repository.common.service.IQueryService;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.sonar.api.utils.log.Logger;
import org.sonar.api.utils.log.Loggers;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * Created by yevhenii.andrushchak on 2018-12-31.
 */
public class UserNameResolver {
    private static final Logger LOG = Loggers.get(UserNameResolver.class);
    private static final IProgressMonitor progressMonitor = new NullProgressMonitor();
    private static final Map<String, String> displayNameToLoginMap = new HashMap<String,String>();

    private static ITeamRepository repository = null;

    public UserNameResolver(final JazzRtcConfiguration config) {
        if (repository != null) {
            // Platform already initialized
            return;
        }

        if (config.username() == null || config.password() == null || config.repository() == null) {
            LOG.warn("Unable to log in. Please provide username, password and jazz server URL.");
            return;
        }

        TeamPlatform.startup();
        try {
            repository = TeamPlatform.getTeamRepositoryService().getTeamRepository(config.repository());
            repository.registerLoginHandler(new ILoginHandler2() {
                @Override
                public ILoginInfo2 challenge(ITeamRepository repo) {
                    return new UsernameAndPasswordLoginInfo(config.username(), config.password());
                }
            });
            repository.login(progressMonitor);
        } catch (Exception e) {
            LOG.error("Exception logging into RTC: " + e.getMessage());
        }
    }

    public String resolveUserId(String username) {
        if(displayNameToLoginMap.containsKey(username)) {
            return displayNameToLoginMap.get(username);
        }

        try {
            String login = findUserIdByName(username);
            if(login != null) {
                displayNameToLoginMap.put(username, login);
                return login;
            }

            return username;

        }
        catch (Exception exception) {
            LOG.error("Error resolving username: " + exception.getMessage());
            return username;
        }
    }

    private String findUserIdByName(String name) throws Exception {
        LOG.info("Resolving username {}", name);

        final IItemQuery query = IItemQuery.FACTORY.newInstance(BaseContributorQueryModel.ContributorQueryModel.ROOT);
        final IPredicate predicate = BaseContributorQueryModel.ContributorQueryModel.ROOT.name()._eq(query.newStringArg());
        final IItemQuery filtered = (IItemQuery) query.filter(predicate);
        final IQueryService qs = ((TeamRepository) repository).getQueryService();
        final IItemQueryPage page = qs.queryItems(filtered, new Object[] { name }, 100);

        final List<?> handles = page.getItemHandles();
        if(handles.isEmpty()){
            LOG.warn("No users found in RTC for the following username: {}", name);
            return null;
        }

        if(handles.size() > 1) {
            LOG.warn("Multiple users found in RTC for the following username: {}", name);
            return null;
        }

        final IContributorHandle handle = (IContributorHandle) handles.get(0);
        IContributorManager contributorManager = repository.contributorManager();
        IContributorIdentity identity = IContributorIdentity.FACTORY.create(handle.getItemId());
        IContributorRecord record = contributorManager.fetchContributorRecordByIdentity(identity, IItemManager.DEFAULT , null);
        String userId = record.getUserIds().get(0).getUserId();
        LOG.info("User {} has been resolved to the following login: {}", name, userId);
        return userId;
    }
}
