package com.example.survey.database.table

import com.example.survey.model.Question
import com.example.survey.database.Database
import java.sql.Connection
import java.sql.ResultSet
import java.sql.SQLException
import java.sql.Statement
import java.util.logging.Level
import java.util.logging.Logger

object QuestionTable {

    private val logger = Logger.getLogger(SurveyTable::class.java.simpleName)

    const val TABLE_NAME = "question"
    const val COL_ID = "id"
    const val COL_SURVEY_ID = "survey_id"
    const val COL_QUESTION = "question"
    const val COL_IS_SINGLE = "is_single"

    internal fun createTable() {
        var con: Connection? = null
        var st: Statement? = null

        try {
            con = Database.getConnection()
            st = con.prepareStatement(
                "CREATE TABLE $TABLE_NAME " +
                        "($COL_ID INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        "$COL_SURVEY_ID INTEGER NOT NULL, " +
                        "$COL_QUESTION TEXT NOT NULL, " +
                        "$COL_IS_SINGLE INTEGER NOT NULL, " +
                        "FOREIGN KEY ($COL_SURVEY_ID) REFERENCES ${SurveyTable.TABLE_NAME} (${SurveyTable.COL_ID}) ON DELETE CASCADE);"
            )
            st.executeUpdate()
        } catch (e: SQLException) {
            logger.log(Level.SEVERE, "createTable", e)
        } finally {
            con?.close()
            st?.close()
        }

    }

    fun insert(question: Question, surveyId: Int): Int? {
        var result: Int? = null

        var con: Connection? = null
        var st: Statement? = null
        var rs: ResultSet? = null

        try {
            con = Database.getConnection()

            st =
                con.prepareStatement("INSERT INTO $TABLE_NAME ($COL_SURVEY_ID, $COL_QUESTION, $COL_IS_SINGLE) VALUES (?, ?, ?)")

            st.setInt(1, surveyId)
            st.setString(2, question.question)
            st.setInt(3, if (question.isSingle) 1 else 0)

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