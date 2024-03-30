package com.example.survey.database.table

import com.example.survey.model.Answer
import com.example.survey.model.Question
import com.example.survey.model.Survey
import com.example.survey.database.Database
import com.example.survey.model.DashboardInfo
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
    const val COL_OWNER = "owner"
    const val COL_NAME = "name"

    internal fun createTable() {
        var con: Connection? = null
        var st: Statement? = null

        try {
            con = Database.getConnection()
            st = con.prepareStatement(
                "CREATE TABLE $TABLE_NAME (" +
                        "$COL_ID INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        "$COL_OWNER TEXT NOT NULL, " +
                        "$COL_NAME TEXT NOT NULL, " +
                        "FOREIGN KEY ($COL_OWNER) REFERENCES ${UserTable.TABLE_NAME} (${UserTable.COL_ACCOUNT}) ON DELETE CASCADE);"
            )
            st.executeUpdate()
        } catch (e: SQLException) {
            logger.log(Level.SEVERE, "createTable", e)
        } finally {
            con?.close()
            st?.close()
        }

    }

    fun insert(survey: Survey, owner: String): Int? {
        var result: Int? = null

        var con: Connection? = null
        var st: Statement? = null
        var rs: ResultSet? = null

        try {
            con = Database.getConnection()

            st =
                con.prepareStatement("INSERT INTO $TABLE_NAME ($COL_OWNER, $COL_NAME) VALUES (?, ?)")

            st.setString(1, owner)
            st.setString(2, survey.name)

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

    fun deleteAdmin(surveyId: Int): Int {
        Database.getConnection().use { con ->

            con.prepareStatement("DELETE FROM $TABLE_NAME WHERE $COL_ID = ?;").use { st ->

                st.setInt(1, surveyId)

                return st.executeUpdate()
            }
        }
    }

    fun deleteUser(surveyId: Int, account: String): Int {
        Database.getConnection().use { con ->

            con.prepareStatement("DELETE FROM $TABLE_NAME WHERE $COL_ID = ? AND $COL_OWNER = ?;")
                .use { st ->

                    st.setInt(1, surveyId)
                    st.setString(2, account)

                    return st.executeUpdate()
                }
        }
    }

    fun getDashboardInfo(): DashboardInfo {

        var users = 0
        var surveys = 0
        var questions = 0
        var answers = 0
        var answered = 0

        Database.getConnection().use { con ->

            con.prepareStatement(
                "SELECT '$TABLE_NAME' AS name, COUNT(*) AS count FROM $TABLE_NAME " +
                        "UNION ALL " +
                        "SELECT '${UserTable.TABLE_NAME}', COUNT(*) FROM ${UserTable.TABLE_NAME} " +
                        "UNION ALL " +
                        "SELECT '${QuestionTable.TABLE_NAME}', COUNT(*) FROM ${QuestionTable.TABLE_NAME} " +
                        "UNION ALL " +
                        "SELECT '${AnswerTable.TABLE_NAME}', COUNT(*) FROM ${AnswerTable.TABLE_NAME} " +
                        "UNION ALL " +
                        "SELECT '${UserAnswerTable.TABLE_NAME}', COUNT(*) FROM ${UserAnswerTable.TABLE_NAME};"
            ).use { st ->
                st.executeQuery().use { rs ->
                    while (rs.next()) {
                        when (rs.getString("name")) {
                            TABLE_NAME -> surveys = rs.getInt("count")
                            UserTable.TABLE_NAME -> users = rs.getInt("count")
                            QuestionTable.TABLE_NAME -> questions = rs.getInt("count")
                            AnswerTable.TABLE_NAME -> answers = rs.getInt("count")
                            UserAnswerTable.TABLE_NAME -> answered = rs.getInt("count")
                        }
                    }
                }
            }
        }
        return DashboardInfo(users, surveys, questions, answers, answered)
    }

    fun getById(surveyId: Int): Survey? {
        var result: Survey? = null

        val helperList = ArrayList<SurveyDbHelper>()


        Database.getConnection().use { con ->
            con.prepareStatement(
                "SELECT s.$COL_ID, s.$COL_NAME, s.$COL_OWNER " +
                        "q.${QuestionTable.COL_ID} AS 'qId', q.${QuestionTable.COL_QUESTION}, q.${QuestionTable.COL_IS_SINGLE}, " +
                        "a.${AnswerTable.COL_ID} AS aId, a.${AnswerTable.COL_ANSWER}, CASE WHEN uac.cnt IS NULL THEN 0 ELSE uac.cnt END AS cnt " +
                        "FROM $TABLE_NAME s " +
                        "INNER JOIN ${QuestionTable.TABLE_NAME} q ON s.$COL_ID = q.${QuestionTable.COL_SURVEY_ID} " +
                        "INNER JOIN ${AnswerTable.TABLE_NAME} a ON q.$COL_ID = a.${AnswerTable.COL_QUESTION_ID} " +
                        "LEFT JOIN (SELECT ${UserAnswerTable.COL_ANSWER_ID}, COUNT(${UserAnswerTable.COL_USER_SURVEY_ID}) AS cnt FROM ${UserAnswerTable.TABLE_NAME} " +
                        "GROUP BY ${UserAnswerTable.COL_ANSWER_ID}) uac " +
                        "ON a.id = uac.${UserAnswerTable.COL_ANSWER_ID} " +
                        "WHERE s.$COL_ID = ? " +
                        "ORDER BY q.${QuestionTable.COL_ID}, a.${AnswerTable.COL_ID};"
            ).use { st ->

                st.setInt(1, surveyId)

                st.executeQuery().use { rs ->

                    while (rs.next()) {
                        helperList.add(
                            SurveyDbHelper(
                                rs.getInt(COL_ID),
                                rs.getString(COL_NAME),
                                rs.getString(COL_OWNER),
                                rs.getInt("qId"),
                                rs.getString(QuestionTable.COL_QUESTION),
                                rs.getInt(QuestionTable.COL_IS_SINGLE),
                                rs.getInt("aId"),
                                rs.getString(AnswerTable.COL_ANSWER),
                                rs.getInt("cnt")
                            )
                        )
                    }
                }
            }
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
                    answers.add(Answer(it.answerId, it.answer, it.count))
                } else {
                    questions.add(Question(questionId, question, isSingle == 1, answers))

                    answers = ArrayList()

                    questionId = it.questionId
                    question = it.question
                    isSingle = it.isSingle

                    answers.add(Answer(it.answerId, it.answer, it.count))
                }
            }

            questions.add(Question(questionId, question, isSingle == 1, answers))

            result = Survey(first.id, first.name, first.owner, questions)

        }

        return result
    }

    fun getAll(): List<Survey> {
        val result = ArrayList<Survey>()

        Database.getConnection().use { con ->
            con.prepareStatement(
                "SELECT $COL_ID, $COL_NAME, $COL_OWNER " +
                        "FROM $TABLE_NAME " +
                        "ORDER BY $COL_ID;"
            ).use { st ->

                st.executeQuery().use { rs ->

                    while (rs.next()) {
                        result.add(
                            Survey(
                                rs.getInt(COL_ID),
                                rs.getString(COL_NAME),
                                rs.getString(COL_OWNER),
                                emptyList()
                            )
                        )
                    }
                }
            }
        }

        return result
    }

    fun getAllUser(account: String): List<Survey> {
        val result = ArrayList<Survey>()

        Database.getConnection().use { con ->
            con.prepareStatement(
                "SELECT $COL_ID, $COL_NAME, $COL_OWNER " +
                        "FROM $TABLE_NAME " +
                        "WHERE $COL_OWNER = ? " +
                        "ORDER BY $COL_ID;"
            ).use { st ->
                st.setString(1, account)

                st.executeQuery().use { rs ->

                    while (rs.next()) {
                        result.add(
                            Survey(
                                rs.getInt(COL_ID),
                                rs.getString(COL_NAME),
                                rs.getString(COL_OWNER),
                                emptyList()
                            )
                        )
                    }
                }
            }
        }

        return result
    }

    private class SurveyDbHelper(
        val id: Int,
        val name: String,
        val owner: String,
        val questionId: Int,
        val question: String,
        val isSingle: Int,
        val answerId: Int,
        val answer: String,
        val count: Int
    )
}