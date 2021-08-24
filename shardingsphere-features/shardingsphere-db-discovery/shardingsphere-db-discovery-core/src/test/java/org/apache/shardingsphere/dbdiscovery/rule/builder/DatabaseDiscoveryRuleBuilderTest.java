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

package org.apache.shardingsphere.dbdiscovery.rule.builder;

import org.apache.shardingsphere.dbdiscovery.api.config.DatabaseDiscoveryRuleConfiguration;
import org.apache.shardingsphere.dbdiscovery.api.config.rule.DatabaseDiscoveryDataSourceRuleConfiguration;
import org.apache.shardingsphere.dbdiscovery.rule.DatabaseDiscoveryRule;
import org.apache.shardingsphere.infra.config.properties.ConfigurationProperties;
import org.apache.shardingsphere.infra.database.type.DatabaseType;
import org.apache.shardingsphere.infra.rule.builder.ShardingSphereRulesBuilderMaterials;
import org.apache.shardingsphere.infra.rule.builder.scope.SchemaRuleBuilder;
import org.apache.shardingsphere.infra.spi.ShardingSphereServiceLoader;
import org.apache.shardingsphere.infra.spi.ordered.OrderedSPIRegistry;
import org.junit.Test;

import javax.sql.DataSource;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public final class DatabaseDiscoveryRuleBuilderTest {
    
    static {
        ShardingSphereServiceLoader.register(SchemaRuleBuilder.class);
    }
    
    @SuppressWarnings({"rawtypes", "unchecked"})
    @Test
    public void assertBuild() {
        DatabaseDiscoveryRuleConfiguration ruleConfig = mock(DatabaseDiscoveryRuleConfiguration.class);
        DatabaseDiscoveryDataSourceRuleConfiguration dataSourceRuleConfig = new DatabaseDiscoveryDataSourceRuleConfiguration("name", Collections.singletonList("name"), "discoveryTypeName");
        when(ruleConfig.getDataSources()).thenReturn(Collections.singletonList(dataSourceRuleConfig));
        SchemaRuleBuilder builder = OrderedSPIRegistry.getRegisteredServices(SchemaRuleBuilder.class, Collections.singletonList(ruleConfig)).get(ruleConfig);
        Map<String, DataSource> dataSourceMap = new HashMap<>(1, 1);
        dataSourceMap.put("primaryDataSourceName", mock(DataSource.class));
        assertThat(builder.build(new ShardingSphereRulesBuilderMaterials("test_schema", Collections.emptyList(), mock(DatabaseType.class),
                dataSourceMap, new ConfigurationProperties(new Properties())), ruleConfig, Collections.emptyList()), instanceOf(DatabaseDiscoveryRule.class));
    }
}
