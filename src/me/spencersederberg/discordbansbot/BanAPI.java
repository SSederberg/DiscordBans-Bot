package me.spencersederberg.discordbansbot;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class BanAPI {

	private static BanAPI api;
	
	private BanAPI() {}
	
	public static BanAPI getAPI() {
		if(api == null) {
			api = new BanAPI();
		}
		return api;
	}
	
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
	public synchronized static Connection openConnection() {
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
	public synchronized static void closeConnection() {
		try {
			con.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Makes a compact MySQL Statement.
	 * 
	 * @param query - The MySQL statement being executed.
	 * @return - A executed SQL statement
	 *
	 * @exception MySQLException
	 *                The database dun-goofed.
	 **/
	public synchronized void doStatement(String query) {
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
	public synchronized static ResultSet doResultSet(String sql, Object... params) throws SQLException {
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
	 * Adds entry into the ban database about guild ban, regardless if they are new or repeat offenders.
	 * 
	 * @param user - The real name of the user, what a normal Discord user sees
	 * @param discordID - The snowflake ID that is unique to the user
	 * @param guildID - The ID of the guild they were banned from
	 * @throws SQLException - The DB is not online, or the statement is incorrect
	 */
	public synchronized void addBan(String user, String discordID, String guildID, String favicon) throws SQLException {
		System.out.println("addBan Firing");
		if(!userExists(discordID)) {
			api.doStatement("insert into bans (user, discordID, ban_count, lastbandate) VALUES ('" + user + "', '" + discordID + "', '1', CURDATE());");
			api.addPlayerTableBan(discordID, guildID, user);
			api.addPlayerIconTable(discordID, favicon);
		} else {
			api.doStatement("update bans set ban_count = ban_count + 1, lastbandate = CURRENT_DATE where discordID = '" + discordID + "'");
			api.updatePlayerTableBan(discordID, guildID, user);
		}
	}
	
	/**
	 * Creates new table entry tailored to the user's <code>discordID</code> about where they were banned.
	 * 
	 * @param discordID - The snowflake ID that is unique to the user
	 * @param guildID - The ID of the guild they were banned from
	 * @throws SQLException - The DB is not online, or the statement is incorrect
	 */
	public synchronized void addPlayerTableBan(String discordID, String guildID, String username) throws SQLException {
		api.doStatement("CREATE TABLE IF NOT EXISTS `discordbans`.`user_" + discordID + "` (id INT primary key auto_increment, user TEXT NOT NULL, guildID BIGINT NOT NULL, ban_date DATE NOT NULL);");
		api.doStatement("INSERT INTO `user_" + discordID + "` (user, guildID, ban_date) VALUES (' " + username + "','" + guildID + "', CURDATE());");
	}
	
	/**
	 * Adds entry to the user's <code>discordID</code> table about a new guild ban.
	 * 
     * @param discordID - The snowflake ID that is unique to the user
	 * @param guildID - The ID of the guild they were banned from
	 * @throws SQLException - The DB is not online, or the statement is incorrect
	 */
	public synchronized void updatePlayerTableBan(String discordID, String guildID, String user) throws SQLException {
		api.doStatement("INSERT INTO user_" + discordID + " (user, guildID, ban_date) VALUES ('" + user +"', '" + guildID + "', CURDATE());");
	}
	
	
	/**
	 * Removes ban entry related to the user and guild.
	 * 
	 * @param discordID - The snowflake ID that is unique to the user
	 * @param guildID - The ID of the guild they were banned from
	 * @throws SQLException - The DB is not online, or the statement is incorrect
	 */
	public synchronized void removeBan(String discordID, String guildID) throws SQLException {
		api.doStatement("update bans set ban_count = ban_count - 1 where discordID = " + discordID);
		api.removePlayerTableBan(discordID, guildID);
	}
	
	/**
	 * Removes the user's specific ban from their ban list.
	 * 
	 * @param discordID - The snowflake ID that is unique to the user
	 * @param guildID - The ID of the guild they were banned from
	 * @throws SQLException - The DB is not online, or the statement is incorrect
	 */
	public synchronized void removePlayerTableBan(String discordID, String guildID) throws SQLException {
		api.doStatement("delete from user_" + discordID + " where guildID = " + guildID + ";");
	}
	
	/**
	 * When a user is banned, the user's icon will be saved so that the guild admins can make an easier identification.
	 * 
	 * @param discordID - The snowflake ID that is unique to the user
	 * @param favicon_uri - The profile icon DiscordApp has for the user.
	 */
	public synchronized void addPlayerIconTable(String discordID, String favicon_uri) {
		api.doStatement("INSERT INTO icons (discordID, favicon) VALUES ('" + discordID + "', '" + favicon_uri + "');");
	}
	
	/**
	 * Gets the ban count of a user.
	 * @param discordID - The snowflake ID of the user
	 * @return - The number of bans detected.
	 * @throws SQLException - The DB is not online, or the statement is incorrect
	 */
	public synchronized Integer getBanCount(String discordID) throws SQLException {
		openConnection();
		PreparedStatement data = con.prepareStatement("USE discordbans");
		data.executeQuery();
		PreparedStatement ps = con.prepareStatement("select ban_count from bans where discordID = '" + discordID + "'");
		ps.executeQuery();
		ResultSet r = ps.executeQuery();
		r.next();
		int count = r.getInt("ban_count");
		closeConnection();
		return count;
	}
	
	/**
	 * Gets the last known banning date of a user.
	 * @param discordID - The snowflake ID of the user
	 * @return - The last date of banning.
	 * @throws SQLException - The DB is not online, or the statement is incorrect
	 */
	public synchronized String getLastBanDate(String discordID) throws SQLException {
		openConnection();
		PreparedStatement data = con.prepareStatement("USE discordbans");
		data.executeQuery();
		PreparedStatement ps = con.prepareStatement("select lastbandate from bans where discordID = '" + discordID + "';");
		ps.executeQuery();
		ResultSet r = ps.executeQuery();
		r.next();
		String date = r.getString("lastbandate");
		closeConnection();
		return date;
	}
	
	
	/**
	 * Checks against the database is see if the snowflake is associated with a known user.
	 * @param snowflake - The unique snowflake ID every discord user is assigned.
	 * @return - The boolean value if they exist as an entry in the discordbans database.
	 * @throws SQLException - The DB is not online, or the statement is incorrect
	 */
	public synchronized boolean userExists(String snowflake) throws SQLException {
		openConnection();
		PreparedStatement data = con.prepareStatement("USE discordbans");
		data.executeQuery();
		PreparedStatement ps = con.prepareStatement("select discordID from bans where discordID = " + snowflake + ";");
		ResultSet r = ps.executeQuery();
		if(r.next()) {
			return true;
		}
		closeConnection();
		return false;
		
	}
	
	/**
	 * Determines if the bot stays quiet or not when a new user joins the guild.
	 * This is to prevent bot spam that is getting more common with more
	 * popular bot solutions already on Discord.
	 * 
	 * @param isSlient
	 */
	public synchronized void setSlientEntry(boolean isSlient, String guildID) {
	    if(isSlient) {
	        api.doStatement("update slient set val = 1 where guildID = '" + guildID + "';");
	    } else {
	        api.doStatement("update slient set val = 0 where guildID = '" + guildID + "';");
	    }
	}
	
	/**
	 * Checks to see if the bot is allowed to notify users about itself.
	 * @return true or false
	 * @throws SQLException 
	 */
	public synchronized boolean allowSlientEntry(String guildID) throws SQLException {
	    openConnection();
        PreparedStatement data = con.prepareStatement("USE discordbans");
        data.executeQuery();
        PreparedStatement ps = con.prepareStatement("select val from bans where discordID = '" + guildID + "'");
        ps.executeQuery();
        ResultSet r = ps.executeQuery();
        r.next();
        byte count = r.getByte("val");
        closeConnection();
        
        if(count == 0) {
            return false;
        } else if(count == 1) {
            return true;
        } else {
            System.out.println("Error!");
        }
        return true;
	}
	
	/**
	 * If this is the first time DiscordBans-Bot has been 
	 * started, it will attempt to create the needed tables
	 * in a database named discordbans
	 * @throws SQLException - The MySQL platform does not exist/is not online. 
	 */
	public synchronized  static void initDB() {
		Statement s = null;
		
		try {
			openConnection();
			s = con.createStatement();
			s.execute("CREATE DATABASE IF NOT EXISTS discordbans");
			s.execute("USE discordbans");
			s.execute("CREATE TABLE IF NOT EXISTS bans (id INT primary key auto_increment, user text NOT NULL, discordID bigint(20) NOT NULL, ban_count int(11) NOT NULL, lastbandate date NOT NULL );");
			s.execute("CREATE TABLE IF NOT EXISTS icons (id INT primary key auto_increment, discordID BIGINT NOT NULL, favicon TEXT NOT NULL);");
			s.execute("CREATE TABLE IF NOT EXISTS slient (id INT primary key auto_increment, guildID BIGINT NOT NULL, val BIT(1) NOT NULL);");
			closeConnection();
		} catch(SQLException ex) {
			ex.printStackTrace();
			System.exit(-1);
		}
    }
}
