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

package org.apache.shardingsphere.encrypt.rule.builder;

import org.apache.shardingsphere.encrypt.api.config.EncryptRuleConfiguration;
import org.apache.shardingsphere.encrypt.rule.EncryptRule;
import org.apache.shardingsphere.infra.database.type.DatabaseType;
import org.apache.shardingsphere.infra.rule.builder.scope.SchemaRuleBuilder;
import org.apache.shardingsphere.infra.spi.ShardingSphereServiceLoader;
import org.apache.shardingsphere.infra.spi.ordered.OrderedSPIRegistry;
import org.junit.Test;

import java.util.Collections;

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;

public final class EncryptRuleBuilderTest {
    
    static {
        ShardingSphereServiceLoader.register(SchemaRuleBuilder.class);
    }
    
    @SuppressWarnings({"rawtypes", "unchecked"})
    @Test
    public void assertBuild() {
        EncryptRuleConfiguration ruleConfig = mock(EncryptRuleConfiguration.class);
        SchemaRuleBuilder builder = OrderedSPIRegistry.getRegisteredServices(SchemaRuleBuilder.class, Collections.singletonList(ruleConfig)).get(ruleConfig);
        assertThat(builder.build("", Collections.emptyMap(), mock(DatabaseType.class), ruleConfig, Collections.emptyList()), instanceOf(EncryptRule.class));
    }
}
