package com.example.survey.database.table

import com.example.survey.model.User
import com.example.survey.api.AuthCredential
import com.example.survey.database.Database
import java.sql.Connection
import java.sql.ResultSet
import java.sql.SQLException
import java.sql.Statement
import java.util.logging.Level
import java.util.logging.Logger

object UserTable {

    private val logger = Logger.getLogger(UserTable::class.java.simpleName)

    private const val TABLE_NAME = "user"
    private const val COL_USER_NAME = "userName"
    private const val COL_PASSWORD = "password"
    private const val COL_AVATAR = "avatar"
    private const val COL_ROLE = "role"

    internal fun createTable() {
        try {
            Database.getConnection().use { con ->
                con.prepareStatement(
                    "CREATE TABLE $TABLE_NAME ($COL_USER_NAME TEXT PRIMARY KEY, $COL_PASSWORD TEXT NOT NULL, $COL_AVATAR TEXT NOT NULL, $COL_ROLE INTEGER NOT NULL);"
                ).use { st ->
                    st.executeUpdate()
                }
            }
        } catch (e: SQLException) {
            logger.log(Level.SEVERE, "createTable", e)
        }
    }

    fun getByCredential(credential: AuthCredential): User? {
        var result: User? = null

        try {
            Database.getConnection().use { con ->

                con.prepareStatement("SELECT * FROM $TABLE_NAME WHERE $COL_USER_NAME = ? AND $COL_PASSWORD = ?")
                    .use { st ->

                        st.setString(1, credential.name)
                        st.setString(2, credential.password)

                        st.executeQuery().use { rs ->
                            if (rs.next()) {
                                result = User(rs.getString(COL_USER_NAME))
                            }
                        }
                    }
            }

        } catch (e: SQLException) {
            logger.log(Level.SEVERE, "getByCredential", e)
        }

        return result
    }

    fun getByUserName(userName: String): User? {
        var result: User? = null

        try {
            Database.getConnection().use { con ->
                con.prepareStatement("SELECT * FROM $TABLE_NAME WHERE $COL_USER_NAME = ?")
                    .use { st ->

                        st.setString(1, userName)

                        st.executeQuery().use { rs ->
                            if (rs.next()) {
                                result = User(rs.getString(COL_USER_NAME))
                            }
                        }
                    }
            }
        } catch (e: SQLException) {
            logger.log(Level.SEVERE, "getByUserName", e)
        }

        return result
    }

    fun getAllUsers(): List<User> {
        val result: ArrayList<User> = ArrayList()

        try {
            Database.getConnection().use { con ->
                con.prepareStatement("SELECT * FROM $TABLE_NAME").use { st ->

                    st.executeQuery().use { rs ->
                        while (rs.next()) {
                            result.add(
                                User(
                                    rs.getString(COL_USER_NAME)
                                )
                            )
                        }
                    }
                }
            }

        } catch (e: SQLException) {
            logger.log(Level.SEVERE, "getAllUsers", e)
        }

        return result
    }

}