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

import org.sonar.api.CoreProperties;
import org.sonar.api.PropertyType;
import org.sonar.api.batch.InstantiationStrategy;
import org.sonar.api.batch.ScannerSide;
import org.sonar.api.config.Configuration;
import org.sonar.api.config.PropertyDefinition;
import org.sonar.api.resources.Qualifiers;

import javax.annotation.CheckForNull;

import java.util.ArrayList;
import java.util.List;

@InstantiationStrategy(InstantiationStrategy.PER_BATCH)
@ScannerSide()
public class JazzRtcConfiguration {

  private static final String CATEGORY_JAZZ = "Jazz RTC";
  private static final long CMD_TIMEOUT = 60_000;
  public static final String USER_PROP_KEY = "sonar.jazzrtc.username";
  public static final String REPOSITORY_PROP_KEY = "sonar.jazzrtc.repository";
  public static final String PASSWORD_PROP_KEY = "sonar.jazzrtc.password.secured";

  private final Configuration  settings;

  public JazzRtcConfiguration(Configuration settings) {
    this.settings = settings;
  }

  public static List<PropertyDefinition> getProperties() {
    ArrayList<PropertyDefinition> properties = new ArrayList<>();

    properties.add(PropertyDefinition.builder(USER_PROP_KEY)
        .name("Username")
        .description("Username to be used for Jazz RTC authentication")
        .type(PropertyType.STRING)
        .onQualifiers(Qualifiers.PROJECT)
        .category(CoreProperties.CATEGORY_SCM)
        .subCategory(CATEGORY_JAZZ)
        .index(0)
        .build());

    properties.add(PropertyDefinition.builder(PASSWORD_PROP_KEY)
        .name("Password")
        .description("Password to be used for Jazz RTC authentication")
        .type(PropertyType.PASSWORD)
        .onQualifiers(Qualifiers.PROJECT)
        .category(CoreProperties.CATEGORY_SCM)
        .subCategory(CATEGORY_JAZZ)
        .index(1)
        .build());

    properties.add(PropertyDefinition.builder(REPOSITORY_PROP_KEY)
        .name("Repository URL")
        .description("Repository URL to be used for Jazz RTC authentication")
        .type(PropertyType.STRING)
        .onQualifiers(Qualifiers.PROJECT)
        .category(CoreProperties.CATEGORY_SCM)
        .subCategory(CATEGORY_JAZZ)
        .index(0)
        .build());

    return properties;
  }

  @CheckForNull
  public String username() {
    return settings.get(USER_PROP_KEY).orElse(null);
  }

  @CheckForNull
  public String password() {
    return settings.get(PASSWORD_PROP_KEY).orElse(null);
  }

  @CheckForNull
  public String repository() {
    return settings.get(REPOSITORY_PROP_KEY).orElse(null);
  }
  
  public long commandTimeout() {
    return CMD_TIMEOUT;
  }

}
