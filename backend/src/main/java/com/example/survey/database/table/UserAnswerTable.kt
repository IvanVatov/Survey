package com.example.survey.database.table

import com.example.survey.database.Database
import com.example.survey.model.UserAnswer
import java.sql.Connection
import java.sql.ResultSet
import java.sql.SQLException
import java.sql.Statement
import java.util.logging.Level
import java.util.logging.Logger

object UserAnswerTable {

    private val logger = Logger.getLogger(UserAnswerTable::class.java.simpleName)

    const val TABLE_NAME = "user_answer"
    const val COL_USER_SURVEY_ID = "user_survey_id"
    const val COL_ANSWER_ID = "answer_id"

    internal fun createTable() {
        var con: Connection? = null
        var st: Statement? = null

        try {
            con = Database.getConnection()
            st = con.prepareStatement(
                "CREATE TABLE $TABLE_NAME " +
                        "($COL_USER_SURVEY_ID INTEGER NOT NULL, " +
                        "$COL_ANSWER_ID INTEGER NOT NULL, " +
                        "FOREIGN KEY ($COL_USER_SURVEY_ID) REFERENCES ${UserSurveyTable.TABLE_NAME} (${UserSurveyTable.COL_ID}) ON DELETE CASCADE, " +
                        "FOREIGN KEY ($COL_ANSWER_ID) REFERENCES ${AnswerTable.TABLE_NAME} (${AnswerTable.COL_ID}) ON DELETE CASCADE);"
            )
            st.executeUpdate()
        } catch (e: SQLException) {
            logger.log(Level.SEVERE, "createTable", e)
        } finally {
            con?.close()
            st?.close()
        }

    }

    fun insert(userAnswer: UserAnswer, userSurveyId: Int): Int? {
        var result: Int? = null

        var con: Connection? = null
        var st: Statement? = null

        try {
            con = Database.getConnection()

            st =
                con.prepareStatement("INSERT INTO $TABLE_NAME ($COL_USER_SURVEY_ID, $COL_ANSWER_ID) VALUES (?, ?)")

            st.setInt(1, userSurveyId)
            st.setInt(2, userAnswer.id)

            st.executeUpdate()

        } catch (e: SQLException) {
            logger.log(Level.SEVERE, "insert", e)
        } finally {
            con?.close()
            st?.close()
        }

        return result
    }

    fun getCountForAnswer(answerId: Int): Int {
        var result = 0

        var con: Connection? = null
        var st: Statement? = null
        var rs: ResultSet? = null

        try {
            con = Database.getConnection()
            st = con.prepareStatement("SELECT COUNT(*) FROM $TABLE_NAME WHERE $COL_ANSWER_ID = ?")

            st.setInt(1, answerId)

            rs = st.executeQuery()
            if (rs.next()) {
                result = rs.getInt(1)
            }

        } catch (e: SQLException) {
            logger.log(Level.SEVERE, "getCountForAnswer", e)
        } finally {
            con?.close()
            st?.close()
            rs?.close()
        }

        return result
    }
}