package com.example.survey.database.table

import com.example.model.Answer
import com.example.model.Question
import com.example.model.Survey
import com.example.survey.database.Database
import java.sql.Connection
import java.sql.ResultSet
import java.sql.SQLException
import java.sql.Statement
import java.util.logging.Level
import java.util.logging.Logger

object SurveyTable {

    val logger = Logger.getLogger(SurveyTable::class.java.simpleName)

    const val TABLE_NAME = "survey"
    const val COL_ID = "id"
    const val COL_NAME = "name"

    internal fun createTable() {
        var con: Connection? = null
        var st: Statement? = null

        try {
            con = Database.getConnection()
            st = con.prepareStatement(
                "CREATE TABLE $TABLE_NAME (" +
                        "$COL_ID INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        "$COL_NAME TEXT NOT NULL);"
            )
            st.executeUpdate()
        } catch (e: SQLException) {
            logger.log(Level.SEVERE, "createTable", e)
        } finally {
            con?.close()
            st?.close()
        }

    }

    fun insert(survey: Survey): Int? {
        var result: Int? = null

        var con: Connection? = null
        var st: Statement? = null
        var rs: ResultSet? = null

        try {
            con = Database.getConnection()

            st =
                con.prepareStatement("INSERT INTO $TABLE_NAME ($COL_NAME) VALUES (?)")

            st.setString(1, survey.name)

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

    fun getById(surveyId: Int): Survey? {
        var result: Survey? = null

        val helperList = ArrayList<SurveyDbHelper>()

        var con: Connection? = null
        var st: Statement? = null
        var rs: ResultSet? = null

        try {
            con = Database.getConnection()
            st = con.prepareStatement(
                "SELECT s.$COL_ID, s.$COL_NAME, " +
                        "q.${QuestionTable.COL_ID} AS 'qId', q.${QuestionTable.COL_QUESTION}, q.${QuestionTable.COL_IS_SINGLE}, " +
                        "a.${AnswerTable.COL_ID} AS aId, a.${AnswerTable.COL_ANSWER} " +
                        "FROM $TABLE_NAME s " +
                        "INNER JOIN ${QuestionTable.TABLE_NAME} q ON s.$COL_ID = q.${QuestionTable.COL_SURVEY_ID} " +
                        "INNER JOIN ${AnswerTable.TABLE_NAME} a ON q.$COL_ID = a.${AnswerTable.COL_QUESTION_ID} " +
                        "WHERE s.$COL_ID = ? " +
                        "ORDER BY q.${QuestionTable.COL_ID}, a.${AnswerTable.COL_ID};"
            )

            st.setInt(1, surveyId)

            rs = st.executeQuery()

            while (rs.next()) {
                helperList.add(
                    SurveyDbHelper(
                        rs.getInt(COL_ID),
                        rs.getString(COL_NAME),
                        rs.getInt("qId"),
                        rs.getString(QuestionTable.COL_QUESTION),
                        rs.getInt(QuestionTable.COL_IS_SINGLE),
                        rs.getInt("aId"),
                        rs.getString(AnswerTable.COL_ANSWER)
                    )
                )
            }

        } catch (e: SQLException) {
            logger.log(Level.SEVERE, "getById", e)
        } finally {
            con?.close()
            st?.close()
            rs?.close()
        }

        if (helperList.isNotEmpty()) {
            val questions = ArrayList<Question>()

            val first = helperList.first()

            var questionId: Int = first.questionId
            var question: String = first.question
            var isSingle: Int = first.isSingle

            var answers = ArrayList<Answer>()

            helperList.forEach {
                if (questionId == it.questionId) {
                    answers.add(Answer(it.answerId, it.answer))
                } else {
                    questions.add(Question(questionId, question, isSingle == 1, answers))

                    answers = ArrayList()

                    questionId = it.questionId
                    question = it.question
                    isSingle = it.isSingle

                    answers.add(Answer(it.answerId, it.answer))
                }
            }

            questions.add(Question(questionId, question, isSingle == 1, answers))

            result = Survey(first.id, first.name, questions)

        }

        return result
    }

    fun getAll(): List<Survey> {
        val result = ArrayList<Survey>()

        var con: Connection? = null
        var st: Statement? = null
        var rs: ResultSet? = null

        try {
            con = Database.getConnection()
            st = con.prepareStatement(
                "SELECT $COL_ID, $COL_NAME " +
                        "FROM $TABLE_NAME " +
                        "ORDER BY $COL_ID;"
            )

            rs = st.executeQuery()

            while (rs.next()) {
                result.add(
                    Survey(
                        rs.getInt(COL_ID),
                        rs.getString(COL_NAME),
                        emptyList()
                    )
                )
            }

        } catch (e: SQLException) {
            logger.log(Level.SEVERE, "getAll", e)
        } finally {
            con?.close()
            st?.close()
            rs?.close()
        }

        return result
    }

    private class SurveyDbHelper(
        val id: Int,
        val name: String,
        val questionId: Int,
        val question: String,
        val isSingle: Int,
        val answerId: Int,
        val answer: String
    )
}