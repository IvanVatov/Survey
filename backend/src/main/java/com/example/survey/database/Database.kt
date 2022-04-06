package com.example.survey.database

import com.example.survey.database.table.*
import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import java.sql.Connection
import java.sql.ResultSet
import java.sql.SQLException
import java.sql.Statement
import java.util.logging.Level
import java.util.logging.Logger

object Database {

    private val logger = Logger.getLogger(Database::class.java.simpleName)

    private val dataSource: HikariDataSource

    private const val DATABASE_VERSION = 3

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
//        UserTable.createTable()
        SurveyTable.createTable()
        QuestionTable.createTable()
        AnswerTable.createTable()
        UserSurveyTable.createTable()
        UserAnswerTable.createTable()
    }

    private fun getDatabaseVersion(): Int {
        var result = -1

        var con: Connection? = null
        var st: Statement? = null
        var rs: ResultSet? = null

        try {
            con = getConnection()
            st = con.prepareStatement("PRAGMA user_version")
            rs = st.executeQuery()

            if (rs.next()) {
                result = rs.getInt(1)
            }
        } catch (e: SQLException) {
            logger.log(Level.SEVERE, "getDatabaseVersion", e)
        } finally {
            con?.close()
            st?.close()
            rs?.close()
        }

        return result
    }

    private fun setDatabaseVersion(version: Int) {
        var con: Connection? = null
        var st: Statement? = null

        try {
            con = getConnection()
            st = con.prepareStatement("PRAGMA user_version = $version")

            st.executeUpdate()

        } catch (e: SQLException) {
            logger.log(Level.SEVERE, "setDatabaseVersion", e)
        } finally {
            con?.close()
            st?.close()
        }
    }
}