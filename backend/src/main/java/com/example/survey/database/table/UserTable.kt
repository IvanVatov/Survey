package com.example.survey.database.table

import com.example.model.User
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
    private const val COL_ROLE = "role"

    internal fun createTable() {
        var con: Connection? = null
        var st: Statement? = null

        try {
            con = Database.getConnection()
            st = con.prepareStatement(
                "CREATE TABLE $TABLE_NAME ($COL_USER_NAME TEXT PRIMARY KEY, $COL_PASSWORD TEXT NOT NULL);"
            )
            st.executeUpdate()
        } catch (e: SQLException) {
            logger.log(Level.SEVERE, "createTable", e)
        } finally {
            con?.close()
            st?.close()
        }

    }

    fun getByCredential(credential: AuthCredential): User? {
        var result: User? = null

        var con: Connection? = null
        var st: Statement? = null
        var rs: ResultSet? = null

        try {
            con = Database.getConnection()
            st = con.prepareStatement("SELECT * FROM $TABLE_NAME WHERE $COL_USER_NAME = ? AND $COL_PASSWORD = ?")

            st.setString(1, credential.name)
            st.setString(2, credential.password)

            rs = st.executeQuery()
            if (rs.next()) {
                result = User(rs.getString(COL_USER_NAME))
            }

        } catch (e: SQLException) {
            logger.log(Level.SEVERE, "getByCredential", e)
        } finally {
            con?.close()
            st?.close()
            rs?.close()
        }

        return result
    }

    fun getByUserName(userName: String): User? {
        var result: User? = null

        var con: Connection? = null
        var st: Statement? = null
        var rs: ResultSet? = null

        try {
            con = Database.getConnection()
            st = con.prepareStatement("SELECT * FROM $TABLE_NAME WHERE $COL_USER_NAME = ?")

            st.setString(1, userName)

            rs = st.executeQuery()
            if (rs.next()) {
                result = User(rs.getString(COL_USER_NAME))
            }

        } catch (e: SQLException) {
            logger.log(Level.SEVERE, "getByUserName", e)
        } finally {
            con?.close()
            st?.close()
            rs?.close()
        }

        return result
    }

    fun getAllUsers(): List<User> {
        val result: ArrayList<User> = ArrayList()

        var con: Connection? = null
        var st: Statement? = null
        var rs: ResultSet? = null

        try {
            con = Database.getConnection()
            st = con.prepareStatement("SELECT * FROM $TABLE_NAME")

            rs = st.executeQuery()
            while (rs.next()) {
                result.add(
                    User(
                        rs.getString(COL_USER_NAME)
                    )
                )
            }

        } catch (e: SQLException) {
            logger.log(Level.SEVERE, "getAllUsers", e)
        } finally {
            con?.close()
            st?.close()
            rs?.close()
        }

        return result
    }

}