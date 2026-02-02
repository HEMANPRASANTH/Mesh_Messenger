package com.bitchat.android.services

import com.bitchat.android.model.GroupInfo
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.concurrent.ConcurrentHashMap

/**
 * Manages discovered groups on the mesh network
 */
object GroupManager {
    private val _groups = MutableStateFlow<Map<String, GroupInfo>>(emptyMap())
    val groups: StateFlow<Map<String, GroupInfo>> = _groups.asStateFlow()

    private val groupsMap = ConcurrentHashMap<String, GroupInfo>()

    fun addGroup(group: GroupInfo) {
        // Prevent duplicates or stale overwrites if we tracked version (impl simplified)
        if (!groupsMap.containsKey(group.id)) {
            groupsMap[group.id] = group
            _groups.value = groupsMap.toMap()
        }
    }

    fun getGroupsByRegion(): Map<String, List<GroupInfo>> {
        return groupsMap.values.groupBy { it.region }
    }
    
    fun getAllGroups(): List<GroupInfo> {
        return groupsMap.values.toList()
    }
}
