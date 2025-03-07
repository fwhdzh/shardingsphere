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

package org.apache.shardingsphere.mode.manager.cluster.coordinator.registry.status.watcher;

import org.apache.shardingsphere.infra.state.StateEvent;
import org.apache.shardingsphere.mode.manager.cluster.coordinator.registry.GovernanceWatcher;
import org.apache.shardingsphere.mode.manager.cluster.coordinator.registry.status.ResourceState;
import org.apache.shardingsphere.mode.manager.cluster.coordinator.registry.status.node.StatusNode;
import org.apache.shardingsphere.mode.repository.cluster.listener.DataChangedEvent;
import org.apache.shardingsphere.mode.repository.cluster.listener.DataChangedEvent.Type;

import java.util.Collection;
import java.util.Collections;
import java.util.Optional;

/**
 * Terminal state changed watcher.
 */
public final class TerminalStateChangedWatcher implements GovernanceWatcher<StateEvent> {
    
    @Override
    public Collection<String> getWatchingKeys() {
        return Collections.singleton(StatusNode.getComputeNodesPath());
    }
    
    @Override
    public Collection<Type> getWatchingTypes() {
        return Collections.singleton(Type.UPDATED);
    }
    
    @Override
    public Optional<StateEvent> createGovernanceEvent(final DataChangedEvent event) {
        return Optional.of(new StateEvent("CIRCUIT_BREAK", ResourceState.DISABLED.toString().equalsIgnoreCase(event.getValue())));
    }
}
