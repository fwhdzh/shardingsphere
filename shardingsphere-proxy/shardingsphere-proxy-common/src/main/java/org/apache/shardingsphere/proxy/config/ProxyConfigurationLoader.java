/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.apache.shardingsphere.proxy.config;

import com.google.common.base.Preconditions;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.apache.shardingsphere.authority.yaml.config.YamlAuthorityRuleConfiguration;
import org.apache.shardingsphere.governance.core.yaml.pojo.YamlGovernanceConfiguration;
import org.apache.shardingsphere.infra.yaml.config.pojo.YamlRuleConfiguration;
import org.apache.shardingsphere.infra.yaml.engine.YamlEngine;
import org.apache.shardingsphere.proxy.config.yaml.YamlProxyRuleConfiguration;
import org.apache.shardingsphere.proxy.config.yaml.YamlProxyServerConfiguration;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.Optional;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * Proxy configuration loader.
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class ProxyConfigurationLoader {
    
    private static final String SERVER_CONFIG_FILE = "server.yaml";
    
    private static final Pattern RULE_CONFIG_FILE_PATTERN = Pattern.compile("config-.+\\.yaml");
    
    /**
     * Load configuration of ShardingSphere-Proxy.
     *
     * @param path configuration path of ShardingSphere-Proxy
     * @return configuration of ShardingSphere-Proxy
     * @throws IOException IO exception
     */
    public static YamlProxyConfiguration load(final String path) throws IOException {
        YamlProxyServerConfiguration serverConfig = loadServerConfiguration(getResourceFile(String.join("/", path, SERVER_CONFIG_FILE)));
        File configPath = getResourceFile(path);
        Collection<YamlProxyRuleConfiguration> ruleConfigs = loadRuleConfigurations(configPath);
        // TODO use SPI with pluggable
        boolean containsGovernance = serverConfig.getRules().stream().anyMatch(each -> each instanceof YamlGovernanceConfiguration);
        Preconditions.checkState(!ruleConfigs.isEmpty() || containsGovernance, "Can not find any valid rule configurations file in path `%s`.", configPath.getPath());
        return new YamlProxyConfiguration(serverConfig, ruleConfigs.stream().collect(Collectors.toMap(
                YamlProxyRuleConfiguration::getSchemaName, each -> each, (oldValue, currentValue) -> oldValue, LinkedHashMap::new)));
    }
    
    private static File getResourceFile(final String path) {
        URL url = ProxyConfigurationLoader.class.getResource(path);
        return null == url ? new File(path) : new File(url.getFile());
    }
    
    private static YamlProxyServerConfiguration loadServerConfiguration(final File yamlFile) throws IOException {
        YamlProxyServerConfiguration result = YamlEngine.unmarshal(yamlFile, YamlProxyServerConfiguration.class);
        Preconditions.checkNotNull(result, "Server configuration file `%s` is invalid.", yamlFile.getName());
        // TODO use SPI with pluggable
        boolean containsGovernance = result.getRules().stream().anyMatch(each -> each instanceof YamlGovernanceConfiguration);
        YamlRuleConfiguration authorityRuleConfig = result.getRules().stream().filter(ruleConfig -> ruleConfig instanceof YamlAuthorityRuleConfiguration).findAny().orElse(null);
        Preconditions.checkState(containsGovernance || null != authorityRuleConfig, "Authority configuration is invalid.");
        return result;
    }
    
    private static Collection<YamlProxyRuleConfiguration> loadRuleConfigurations(final File configPath) throws IOException {
        Collection<String> loadedSchemaNames = new HashSet<>();
        Collection<YamlProxyRuleConfiguration> result = new LinkedList<>();
        for (File each : findRuleConfigurationFiles(configPath)) {
            loadRuleConfiguration(each).ifPresent(yamlProxyRuleConfig -> {
                Preconditions.checkState(loadedSchemaNames.add(yamlProxyRuleConfig.getSchemaName()), "Schema name `%s` must unique at all rule configurations.", yamlProxyRuleConfig.getSchemaName());
                result.add(yamlProxyRuleConfig);
            });
        }
        return result;
    }
    
    private static Optional<YamlProxyRuleConfiguration> loadRuleConfiguration(final File yamlFile) throws IOException {
        YamlProxyRuleConfiguration result = YamlEngine.unmarshal(yamlFile, YamlProxyRuleConfiguration.class);
        if (null == result) {
            return Optional.empty();
        }
        Preconditions.checkNotNull(result.getSchemaName(), "Property `schemaName` in file `%s` is required.", yamlFile.getName());
        Preconditions.checkState(!result.getDataSources().isEmpty(), "Data sources configuration in file `%s` is required.", yamlFile.getName());
        return Optional.of(result);
    }
    
    private static File[] findRuleConfigurationFiles(final File path) {
        return path.listFiles(each -> RULE_CONFIG_FILE_PATTERN.matcher(each.getName()).matches());
    }
}
