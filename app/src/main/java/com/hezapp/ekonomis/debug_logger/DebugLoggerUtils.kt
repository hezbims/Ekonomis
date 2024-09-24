package com.hezapp.ekonomis.debug_logger

import com.hezapp.ekonomis.core.domain.profile.entity.ProfileEntity
import com.hezapp.ekonomis.core.domain.profile.entity.ProfileType

class DebugLoggerUtils {
    companion object {
        fun logAllProfiles(profiles: List<ProfileEntity>): String {
            val result = profiles.joinToString("\n\n") {
                it.toLogString()
            }
            return result
        }

        private fun ProfileEntity.toLogString(): String {
            return """id : $id
name : $name
profile type : ${type.toLogString()}
""".trimIndent()
        }

        private fun ProfileType.toLogString(): String {
            return when (this) {
                ProfileType.SUPPLIER -> "Penjual"
                ProfileType.CUSTOMER -> "Pembeli"
            }
        }
    }
}