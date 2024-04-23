package com.example.survey.database.table

import com.example.survey.model.Answer
import com.example.survey.database.Database
import java.sql.Connection
import java.sql.ResultSet
import java.sql.SQLException
import java.sql.Statement
import java.util.logging.Level
import java.util.logging.Logger

object AnswerTable {

    private val logger = Logger.getLogger(AnswerTable::class.java.simpleName)

    const val TABLE_NAME = "answer"
    const val COL_ID = "id"
    const val COL_QUESTION_ID = "question_id"
    const val COL_ANSWER = "answer"

    internal fun createTable() {
        var con: Connection? = null
        var st: Statement? = null

        try {
            con = Database.getConnection()
            st = con.prepareStatement(
                "CREATE TABLE $TABLE_NAME " +
                        "($COL_ID INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        "$COL_QUESTION_ID INTEGER NOT NULL, " +
                        "$COL_ANSWER TEXT NOT NULL, " +
                        "FOREIGN KEY ($COL_QUESTION_ID) REFERENCES ${QuestionTable.TABLE_NAME} (${QuestionTable.COL_ID}) ON DELETE CASCADE);"
            )
            st.executeUpdate()
        } catch (e: SQLException) {
            logger.log(Level.SEVERE, "createTable", e)
        } finally {
            con?.close()
            st?.close()
        }

    }

    fun insert(answer: Answer, questionId: Int): Int? {
        var result: Int? = null

        var con: Connection? = null
        var st: Statement? = null
        var rs: ResultSet? = null

        try {
            con = Database.getConnection()

            st =
                con.prepareStatement("INSERT INTO $TABLE_NAME ($COL_QUESTION_ID, $COL_ANSWER) VALUES (?, ?)")

            st.setInt(1, questionId)
            st.setString(2, answer.answer)

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