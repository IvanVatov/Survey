package com.example.survey.database.table

import com.example.survey.model.UserSurvey
import com.example.survey.database.Database
import java.sql.Connection
import java.sql.ResultSet
import java.sql.SQLException
import java.sql.Statement
import java.util.logging.Level
import java.util.logging.Logger

object UserSurveyTable {
    private val logger = Logger.getLogger(UserSurveyTable::class.java.simpleName)

    const val TABLE_NAME = "user_survey"
    const val COL_ID = "id"
    const val COL_SURVEY_ID = "survey_id"
    const val COL_USER_NAME = "user_name"

    internal fun createTable() {
        var con: Connection? = null
        var st: Statement? = null

        try {
            con = Database.getConnection()
            st = con.prepareStatement(
                "CREATE TABLE $TABLE_NAME " +
                        "($COL_ID INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        "$COL_SURVEY_ID INTEGER NOT NULL, " +
                        "$COL_USER_NAME TEXT NOT NULL, " +
                        "FOREIGN KEY ($COL_SURVEY_ID) REFERENCES ${SurveyTable.TABLE_NAME} (${SurveyTable.COL_ID}));"
            )
            st.executeUpdate()
        } catch (e: SQLException) {
            logger.log(Level.SEVERE, "createTable", e)
        } finally {
            con?.close()
            st?.close()
        }

    }

    fun insert(userSurvey: UserSurvey): Int? {
        var result: Int? = null

        var con: Connection? = null
        var st: Statement? = null
        var rs: ResultSet? = null

        try {
            con = Database.getConnection()

            st =
                con.prepareStatement("INSERT INTO $TABLE_NAME ($COL_SURVEY_ID, $COL_USER_NAME) VALUES (?, ?)")

            st.setInt(1, userSurvey.surveyId)
            st.setString(2, userSurvey.userName)

            st.executeUpdate()

            rs = st.getGeneratedKeys()
            if (rs.next()) {
                result = rs.getInt(1)
            }
        } catch (e: SQLException) {
            logger.log(Level.SEVERE, "insert", e)
        } finally {
            con?.close()
            st?.close()
            rs?.close()
        }

        return result
    }
}