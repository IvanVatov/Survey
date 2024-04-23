package com.example.survey.database

import com.example.survey.database.table.AnswerTable
import com.example.survey.database.table.QuestionTable
import com.example.survey.database.table.SurveyTable
import com.example.survey.database.table.UserAnswerTable
import com.example.survey.database.table.UserSurveyTable
import com.example.survey.database.table.UserTable
import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import java.sql.Connection
import java.sql.SQLException
import java.util.logging.Level
import java.util.logging.Logger

object Database {

    private val logger = Logger.getLogger(Database::class.java.simpleName)

    private val dataSource: HikariDataSource

    private const val DATABASE_VERSION = 4

    init {
        val config = HikariConfig()
        config.jdbcUrl = "jdbc:sqlite:survey.db"
        dataSource = HikariDataSource(config)

        if (getDatabaseVersion() < DATABASE_VERSION) {

            createTables()

            setDatabaseVersion(DATABASE_VERSION)
        }
    }

    fun getConnection(): Connection {
        return dataSource.connection
    }

    private fun createTables() {
        getConnection().use { con ->
            con.prepareStatement("PRAGMA foreign_keys = ON;").use { ps ->
                ps.execute()
            }
        }
        UserTable.createTable()
        SurveyTable.createTable()
        QuestionTable.createTable()
        AnswerTable.createTable()
        UserSurveyTable.createTable()
        UserAnswerTable.createTable()
    }

    private fun getDatabaseVersion(): Int {
        var result = -1

        try {
            getConnection().use { con ->
                con.prepareStatement("PRAGMA user_version").use { st ->
                    st.executeQuery().use { rs ->
                        if (rs.next()) {
                            result = rs.getInt(1)
                        }
                    }
                }
            }
        } catch (e: SQLException) {
            logger.log(Level.SEVERE, "getDatabaseVersion", e)
        }

        return result
    }

    private fun setDatabaseVersion(version: Int) {
        try {
            getConnection().use { con ->
                con.prepareStatement("PRAGMA user_version = $version").use { st ->
                    st.executeUpdate()
                }
            }

        } catch (e: SQLException) {
            logger.log(Level.SEVERE, "setDatabaseVersion", e)
        }
    }
}