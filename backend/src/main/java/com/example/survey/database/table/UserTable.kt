package com.example.survey.database.table

import com.example.survey.database.Database
import com.example.survey.model.UserPrincipal
import java.sql.SQLException
import java.util.logging.Level
import java.util.logging.Logger

object UserTable {

    private val logger = Logger.getLogger(UserTable::class.java.simpleName)

    private const val TABLE_NAME = "user"
    private const val COL_NAME = "name"
    private const val COL_ACCOUNT = "account"
    private const val COL_PASSWORD = "password"
    private const val COL_AVATAR = "avatar"
    private const val COL_ROLE = "role"

    internal fun createTable() {
        try {
            Database.getConnection().use { con ->
                con.prepareStatement(
                    "CREATE TABLE $TABLE_NAME ($COL_NAME TEXT NOT NULL, $COL_ACCOUNT TEXT PRIMARY KEY, $COL_PASSWORD TEXT NOT NULL, $COL_AVATAR TEXT NOT NULL, $COL_ROLE INTEGER NOT NULL);"
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

            con.prepareStatement("INSERT INTO $TABLE_NAME ($COL_NAME, $COL_ACCOUNT, $COL_PASSWORD, $COL_AVATAR, $COL_ROLE) VALUES (?, ?, ?, ?, 0)")
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

    fun updateUser(
        name: String,
        password: String,
        avatar: String,
        role: Int,
        account: String
    ): Int {
        Database.getConnection().use { con ->

            con.prepareStatement("UPDATE $TABLE_NAME SET $COL_NAME = ?, $COL_PASSWORD = ?, $COL_AVATAR = ?, $COL_ROLE = ? WHERE $COL_ACCOUNT = ?")
                .use { ps ->

                    ps.setString(1, name)

                    ps.setString(2, password)
                    ps.setString(3, avatar)
                    ps.setInt(4, role)
                    ps.setString(5, account)

                    return ps.executeUpdate()
                }
        }
    }

    fun delete(account: String): Int {
        Database.getConnection().use { con ->

            con.prepareStatement("DELETE FROM $TABLE_NAME WHERE $COL_ACCOUNT = ?;").use { st ->

                st.setString(1, account)

                return st.executeUpdate()
            }
        }
    }

    fun getByCredential(userName: String, password: String): UserPrincipal? {
        var result: UserPrincipal? = null

        try {
            Database.getConnection().use { con ->

                con.prepareStatement("SELECT * FROM $TABLE_NAME WHERE $COL_NAME = ? AND $COL_PASSWORD = ?")
                    .use { st ->

                        st.setString(1, userName)
                        st.setString(2, password)

                        st.executeQuery().use { rs ->
                            if (rs.next()) {
                                result = UserPrincipal(
                                    rs.getString(COL_ACCOUNT),
                                    rs.getString(COL_NAME),
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

        Database.getConnection().use { con ->
            con.prepareStatement("SELECT * FROM $TABLE_NAME WHERE $COL_NAME = ?")
                .use { st ->

                    st.setString(1, userName)

                    st.executeQuery().use { rs ->
                        if (rs.next()) {
                            result = UserPrincipal(
                                rs.getString(COL_ACCOUNT),
                                rs.getString(COL_NAME),
                                rs.getString(COL_AVATAR),
                                rs.getInt(COL_ROLE)
                            )
                        }
                    }
                }
        }

        return result
    }

    fun getAllUsers(): List<UserPrincipal> {
        val result: ArrayList<UserPrincipal> = ArrayList()

        Database.getConnection().use { con ->
            con.prepareStatement("SELECT * FROM $TABLE_NAME").use { st ->

                st.executeQuery().use { rs ->
                    while (rs.next()) {
                        result.add(
                            UserPrincipal(
                                rs.getString(COL_ACCOUNT),
                                rs.getString(COL_NAME),
                                rs.getString(COL_AVATAR),
                                rs.getInt(COL_ROLE)
                            )
                        )
                    }
                }
            }
        }

        return result
    }

    fun setAdmin(account: String): Int? {
        var result: Int?
        Database.getConnection().use { con ->
            con.prepareStatement(
                "UPDATE $TABLE_NAME SET $COL_ROLE = 1 WHERE $COL_ACCOUNT = ?;"
            ).use { st ->
                st.setString(1, account)
                result = st.executeUpdate()
            }
        }
        return result
    }
}