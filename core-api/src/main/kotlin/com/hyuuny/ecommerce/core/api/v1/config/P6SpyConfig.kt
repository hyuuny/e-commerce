package com.hyuuny.ecommerce.core.api.v1.config

import com.p6spy.engine.logging.Category
import com.p6spy.engine.spy.P6SpyOptions
import com.p6spy.engine.spy.appender.MessageFormattingStrategy
import jakarta.annotation.PostConstruct
import org.hibernate.engine.jdbc.internal.FormatStyle
import org.springframework.context.annotation.Configuration


@Configuration
class P6SpyConfig : MessageFormattingStrategy {

    @PostConstruct
    fun configureLogMessageFormat() {
        P6SpyOptions.getActiveInstance().logMessageFormat = this::class.java.name
    }

    override fun formatMessage(
        connectionId: Int,
        now: String,
        elapsed: Long,
        category: String,
        prepared: String,
        sql: String,
        url: String
    ): String {
        val formattedSql = formatSql(category, sql)
        return "[%s] | %d ms | %s".format(category, elapsed, formattedSql)
    }

    private fun formatSql(category: String, sql: String): String? {
        if (sql.isBlank() || category != Category.STATEMENT.name) {
            return sql
        }

        val trimmedSql = sql.trim().lowercase()
        return when {
            trimmedSql.startsWith("create") ||
                    trimmedSql.startsWith("alter") ||
                    trimmedSql.startsWith("comment") -> FormatStyle.DDL.formatter.format(sql)

            else -> FormatStyle.BASIC.formatter.format(sql)
        }
    }
}

