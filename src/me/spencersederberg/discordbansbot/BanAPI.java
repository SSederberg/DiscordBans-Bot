package me.spencersederberg.discordbansbot;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class BanAPI {

	public static Connection con = null;

	// In an actual production setting, there would be better security.
	private static final String DB_Address = "jdbc:mysql://localhost:3306/";
    private static final String DB_Password = "";
    
    /**
	 * Connects the bot to the local MySQL Database
	 * 
	 * @exception MySQLException
	 *                Failed to connect to database.
	 * @exception ClassNotFoundException
	 *                com.mysql.jdbc.Driver isn`t a resource.
	 **/

	public static Connection openConnection() {
		try {
			Class.forName("com.mysql.jdbc.Driver");
			con = DriverManager.getConnection(DB_Address, "root", DB_Password);
		} catch (SQLException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	/**
	 * Closes the MySQL Database connection, as it`s no longer needed.
	 * 
	 * @exception SQLException - Something gun-goofed, or the connection was already closed.
	 * 
	 **/

	public static void closeConnection() {
		try {
			con.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Makes a compact MySQL Statement.
	 * 
	 * @param argument - The statement being executed.
	 * @param db - The database the argument will be using.
	 * @return - A executed SQL statement
	 *
	 * @exception MySQLException
	 *                The database dun-goofed.
	 */
	public void doStatement(String query) {
		Statement st = null;
		try {
			openConnection();
			st = con.createStatement();
			st.execute("use discordbans");
			st.execute(query);
			st.close();
			closeConnection();
		} catch (SQLException | NullPointerException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Gets a ResultSet Array from a select statement.
	 * 
	 * @param sql - The select MySQL Statement
	 * @param params - The specific objects being looked for
	 */
	public static ResultSet doResultSet(String sql, Object... params) throws SQLException {
		openConnection();
		PreparedStatement query = con.prepareStatement(sql);
		query.executeQuery("USE discordbans");
		int index = 1;
		for (Object par : params) {
			if (par instanceof String) {
				query.setString(index, (String) par);
			} else if (par instanceof Integer) {
				query.setInt(index, (Integer) par);
			} else {
				System.out.println("Unknown type! " + par);
			}
			index++;
		}
		return query.executeQuery();
	}

	/**
	 * Adds entry into the ban database about guild ban, regardless if they new or repeat offenders.
	 * 
	 * @param user - The real name of the user, what a normal Discord user sees
	 * @param discordID - The snowflake ID that is unique to the user
	 * @param guildID - The ID of the guild they were banned from
	 * @throws SQLException - The DB is not online, or the statement is incorrect
	 */
	public void addBan(String user, String discordID, String guildID) throws SQLException {
		if(!userExists(discordID)) {
			doStatement("insert into bans (user, discordID, ban_count, lastbandate) VALUES (`" + user + "`, `" + discordID + "`, `1`, CURRENT_DATE");
			addPlayerTableBan(discordID, guildID);
		} else {
			doStatement("update bans set ban_count = ban_count + 1, lastbandate = CURRENT_DATE where discordID = `" + discordID + "`");
			updatePlayerTableBan(discordID, guildID);
		}
	}
	
	/**
	 * Creates new table entry tailored to the user's <code>discordID</code> about where they were banned.
	 * 
	 * @param discordID - The snowflake ID that is unique to the user
	 * @param guildID - The ID of the guild they were banned from
	 * @throws SQLException - The DB is not online, or the statement is incorrect
	 */
	public void addPlayerTableBan(String discordID, String guildID) throws SQLException {
		doStatement("CREATE TABLE IF NOT EXISTS `discordbans`.`user_" + discordID + "` (`guildID` BIGINT NOT NULL, `ban_date` DATE NOT NULL);");
		doStatement("INSERT INTO `" + discordID + "` (`guildID`, `ban_date`) VALUES (`" + guildID + "`, CURDATE());");
	}
	
	/**
	 * Adds entry to the user's <code>discordID</code> table about a new guild ban.
	 * 
     * @param discordID - The snowflake ID that is unique to the user
	 * @param guildID - The ID of the guild they were banned from
	 * @throws SQLException - The DB is not online, or the statement is incorrect
	 */
	public void updatePlayerTableBan(String discordID, String guildID) throws SQLException {
		doStatement("INSERT INTO `" + discordID + "` (`guildID`, `ban_date`) VALUES (`" + guildID + "`, CURDATE());");
	}
	
	
	/**
	 * Removes ban entry related to the user and guild.
	 * 
	 * @param discordID - The snowflake ID that is unique to the user
	 * @param guildID - The ID of the guild they were banned from
	 * @throws SQLException - The DB is not online, or the statement is incorrect
	 */
	public void removeBan(String discordID, String guildID) throws SQLException {
		doStatement("update bans set ban_count = ban_count - 1 where discordID = `" + discordID + "`");
		removePlayerTableBan(discordID, guildID);
	}
	
	/**
	 * Removes the user's specific ban from their ban list.
	 * 
	 * @param discordID - The snowflake ID that is unique to the user
	 * @param guildID - The ID of the guild they were banned from
	 * @throws SQLException - The DB is not online, or the statement is incorrect
	 */
	public void removePlayerTableBan(String discordID, String guildID) throws SQLException {
		doStatement("delete from `" + discordID + "` where guildID = `" + guildID + "`;");
	}
	
	/**
	 * When a user is banned, the user's icon will be saved so that the guild admins can make an easier identification.
	 * 
	 * @param discordID - The snowflake ID that is unique to the user
	 * @param favicon_uri - The profile icon DiscordApp has for the user.
	 */
	public void addPlayerIconTable(String discordID, String favicon_uri) {
		doStatement("INSERT INTO icons (discordID, favicon) VALUES (`" + discordID + "`, `" + favicon_uri + "`);");
	}
	
	/**
	 * Gets the ban count of a user.
	 * @param discordID - The snowflake ID of the user
	 * @return - The number of bans detected.
	 * @throws SQLException
	 */
	public static Integer getBanCount(String discordID) throws SQLException {
		openConnection();
		PreparedStatement data = con.prepareStatement("USE discordbans");
		data.executeQuery();
		PreparedStatement ps = con.prepareStatement("select ban_count from bans where discordID = `" + discordID + "`");
		ps.executeQuery();
		ResultSet r = ps.executeQuery();
		r.next();
		return (Integer) r.getObject("ban_count");
	}
	
	/**
	 * Gets the last known banning date of a user.
	 * @param discordID - The snowflake ID of the user
	 * @return - The last date of banning.
	 * @throws SQLException
	 */
	public String getLastBanDate(String discordID) throws SQLException {
		openConnection();
		PreparedStatement data = con.prepareStatement("USE discordbans");
		data.executeQuery();
		PreparedStatement ps = con.prepareStatement("select `lastbandate` from bans where discordID = `" + discordID + "`;");
		ps.executeQuery();
		ResultSet r = ps.executeQuery();
		r.next();
		closeConnection();
		return r.getString("lastbandate");
	}
	
	public boolean userExists(String snowflake) throws SQLException {
		openConnection();
		PreparedStatement data = con.prepareStatement("USE discordbans");
		data.executeQuery();
		PreparedStatement ps = con.prepareStatement("select `discordID` from bans where discordID = `" + snowflake + "`;");
		ResultSet r = ps.executeQuery();
		if(r.next()) {
			return true;
		}
		closeConnection();
		return false;
		
	}
	
	public void testDB(String test) throws SQLException, NullPointerException {
		
			doStatement("INSERT INTO `test` (`discordID`) VALUES ('" + test + "');");
//			System.out.println("Added " + s  + " " + "("+ s.getEffectiveName() + ")"  + " to the test db!");
	}
	
	public static void initDB() throws NullPointerException {
		Statement s = null;
		
		try {
			openConnection();
			s = con.createStatement();
			s.execute("CREATE DATABASE IF NOT EXISTS discordbans");
			s.execute("USE discordbans");
			s.execute("CREATE TABLE IF NOT EXISTS bans (id INT primary key auto_increment, `name` text NOT NULL, `discordID` bigint(20) NOT NULL, `ban_count` int(11) NOT NULL, `lastbandate` date NOT NULL );");
			s.execute("CREATE TABLE IF NOT EXISTS test (`discordID` bigint(20) not null);");
			s.execute("CREATE TABLE IF NOT EXISTS icons (`discordID` BIGINT NOT NULL, `favicon` TEXT NOT NULL);");
			closeConnection();
		} catch(SQLException ex) {
			ex.printStackTrace();
		}
	}
}
