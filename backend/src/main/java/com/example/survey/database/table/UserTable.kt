package com.example.survey.database.table

import com.example.survey.database.Database
import com.example.survey.model.Answer
import com.example.survey.model.UserPrincipal
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
    private const val COL_ACCOUNT = "account"
    private const val COL_PASSWORD = "password"
    private const val COL_AVATAR = "avatar"
    private const val COL_ROLE = "role"

    internal fun createTable() {
        try {
            Database.getConnection().use { con ->
                con.prepareStatement(
                    "CREATE TABLE $TABLE_NAME ($COL_USER_NAME TEXT NOT NULL, $COL_ACCOUNT TEXT PRIMARY KEY, $COL_PASSWORD TEXT NOT NULL, $COL_AVATAR TEXT NOT NULL, $COL_ROLE INTEGER NOT NULL);"
                ).use { st ->
                    st.executeUpdate()
                }
            }
        } catch (e: SQLException) {
            logger.log(Level.SEVERE, "createTable", e)
        }
    }

    fun insert(name: String, account: String, password: String, avatar: String): Int? {
        var result: Int? = null

        Database.getConnection().use { con ->

            con.prepareStatement("INSERT INTO $TABLE_NAME ($COL_USER_NAME, $COL_ACCOUNT, $COL_PASSWORD, $COL_AVATAR, $COL_ROLE) VALUES (?, ?, ?, ?, 0)")
                .use { st ->

                    st.setString(1, name)
                    st.setString(2, account)
                    st.setString(3, password)
                    st.setString(4, avatar)

                    st.executeUpdate()

                    st.generatedKeys.use { rs ->
                        if (rs.next()) {
                            result = rs.getInt(1)
                        }
                    }
                }

        }
        return result
    }

    fun getByCredential(userName: String, password: String): UserPrincipal? {
        var result: UserPrincipal? = null

        try {
            Database.getConnection().use { con ->

                con.prepareStatement("SELECT * FROM $TABLE_NAME WHERE $COL_USER_NAME = ? AND $COL_PASSWORD = ?")
                    .use { st ->

                        st.setString(1, userName)
                        st.setString(2, password)

                        st.executeQuery().use { rs ->
                            if (rs.next()) {
                                result = UserPrincipal(
                                    rs.getString(COL_ACCOUNT),
                                    rs.getString(COL_USER_NAME),
                                    rs.getString(COL_AVATAR),
                                    rs.getInt(COL_ROLE)
                                )
                            }
                        }
                    }
            }

        } catch (e: SQLException) {
            logger.log(Level.SEVERE, "getByCredential", e)
        }

        return result
    }

    fun getByUserName(userName: String): UserPrincipal? {
        var result: UserPrincipal? = null

        try {
            Database.getConnection().use { con ->
                con.prepareStatement("SELECT * FROM $TABLE_NAME WHERE $COL_USER_NAME = ?")
                    .use { st ->

                        st.setString(1, userName)

                        st.executeQuery().use { rs ->
                            if (rs.next()) {
                                result = UserPrincipal(
                                    rs.getString(COL_ACCOUNT),
                                    rs.getString(COL_USER_NAME),
                                    rs.getString(COL_AVATAR),
                                    rs.getInt(COL_ROLE)
                                )
                            }
                        }
                    }
            }
        } catch (e: SQLException) {
            logger.log(Level.SEVERE, "getByUserName", e)
        }

        return result
    }

    fun getAllUsers(): List<UserPrincipal> {
        val result: ArrayList<UserPrincipal> = ArrayList()

        try {
            Database.getConnection().use { con ->
                con.prepareStatement("SELECT * FROM $TABLE_NAME").use { st ->

                    st.executeQuery().use { rs ->
                        while (rs.next()) {
                            result.add(
                                UserPrincipal(
                                    rs.getString(COL_ACCOUNT),
                                    rs.getString(COL_USER_NAME),
                                    rs.getString(COL_AVATAR),
                                    rs.getInt(COL_ROLE)
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