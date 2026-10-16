
using MySqlConnector;

namespace FastFoodMIS_CSharp.Database;

public static class DBConnection
{
    public static MySqlConnection GetConnection()
    {
        string host = Environment.GetEnvironmentVariable(
            "FASTFOOD_DB_HOST") ?? "localhost";

        string database = Environment.GetEnvironmentVariable(
            "FASTFOOD_DB_NAME") ?? "fastfood_db";

        string user = Environment.GetEnvironmentVariable(
            "FASTFOOD_DB_USER") ?? "root";

        string password = Environment.GetEnvironmentVariable(
            "FASTFOOD_DB_PASSWORD") ?? "";

        string? portText = Environment.GetEnvironmentVariable(
            "FASTFOOD_DB_PORT");

        uint port = uint.TryParse(portText, out uint parsedPort)
            ? parsedPort
            : 3306;

        var builder = new MySqlConnectionStringBuilder
        {
            Server = host,
            Port = port,
            Database = database,
            UserID = user,
            Password = password,
            CharacterSet = "utf8mb4",
            SslMode = MySqlSslMode.Preferred,
            ConnectionTimeout = 5
        };

        var connection = new MySqlConnection(
            builder.ConnectionString);

        connection.Open();

        return connection;
    }
}
