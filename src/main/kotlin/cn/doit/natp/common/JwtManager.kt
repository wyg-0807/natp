@file:Suppress("UNCHECKED_CAST")

package cn.doit.natp.common

import cn.hutool.core.date.DateUtil
import io.jsonwebtoken.JwtException
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.security.Keys
import java.util.*
import javax.xml.bind.DatatypeConverter

class JwtManager {

    companion object {
        private val me = JwtManager()

        private val secretKey = Keys.hmacShaKeyFor(DatatypeConverter.parseBase64Binary(Configs.jwtConfig["secret"]))
        private val timeToLiveMinutes = (Configs.jwtConfig["timeToLiveMinutes"] ?: "0").toInt()

        fun me() = me

        const val HEADER = "Authorization"
    }

    fun parseJwtToken(token: String): String? {
        return try {
            Jwts.parser().setSigningKey(secretKey).parseClaimsJws(token).body.subject
        } catch (e: JwtException) {
            null
        }
    }

    fun createJwtToken(string: String): String {
        val now = Date()

        val jwtBuilder = Jwts.builder().setSubject(string).setIssuedAt(now).signWith(secretKey)
        if (timeToLiveMinutes > 0) {
            jwtBuilder.setExpiration(DateUtil.offsetMinute(now, timeToLiveMinutes))
        }

        return jwtBuilder.compact()
    }

}
