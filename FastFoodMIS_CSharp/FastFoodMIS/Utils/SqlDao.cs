using System.Globalization;
using System.Reflection;
using MySqlConnector;
using FastFoodMIS_CSharp.Database;

namespace FastFoodMIS_CSharp.Utils;

// Bộ hàm dùng chung để các DAO chỉ tập trung vào SQL của từng thực thể.
internal static class SqlDao
{
    public static List<T> Query<T>(string sql, params (string Name, object? Value)[] args)
        where T : new()
    {
        using var conn = DBConnection.GetConnection();
        using var command = new MySqlCommand(sql, conn);
        Bind(command, args);
        using var reader = command.ExecuteReader();
        var result = new List<T>();
        var props = typeof(T).GetProperties(BindingFlags.Public | BindingFlags.Instance)
            .Where(p => p.CanWrite)
            .ToDictionary(p => p.Name, StringComparer.OrdinalIgnoreCase);

        while (reader.Read())
        {
            var item = new T();
            for (int i = 0; i < reader.FieldCount; i++)
            {
                if (reader.IsDBNull(i) || !props.TryGetValue(reader.GetName(i), out var prop))
                    continue;
                prop.SetValue(item, ConvertValue(reader.GetValue(i), prop.PropertyType));
            }
            result.Add(item);
        }
        return result;
    }

    public static bool Execute(string sql, params (string Name, object? Value)[] args)
    {
        using var conn = DBConnection.GetConnection();
        using var command = new MySqlCommand(sql, conn);
        Bind(command, args);
        return command.ExecuteNonQuery() > 0;
    }

    private static void Bind(MySqlCommand cmd, (string Name, object? Value)[] args)
    {
        foreach (var (name, value) in args)
        {
            object dbValue = value switch
            {
                null => DBNull.Value,
                DateOnly date => date.ToDateTime(TimeOnly.MinValue),
                Enum e => e.ToString(),
                _ => value
            };
            cmd.Parameters.AddWithValue(name, dbValue);
        }
    }

    private static object ConvertValue(object value, Type type)
    {
        Type target = Nullable.GetUnderlyingType(type) ?? type;
        if (target.IsEnum)
            return Enum.Parse(target, Convert.ToString(value, CultureInfo.InvariantCulture)!, true);
        if (target == typeof(DateOnly))
            return DateOnly.FromDateTime(Convert.ToDateTime(value, CultureInfo.InvariantCulture));
        if (target == typeof(DateTime))
            return Convert.ToDateTime(value, CultureInfo.InvariantCulture);
        if (target == typeof(string))
            return Convert.ToString(value, CultureInfo.InvariantCulture)!;
        if (target == typeof(decimal))
            return Convert.ToDecimal(value, CultureInfo.InvariantCulture);
        if (target == typeof(long))
            return Convert.ToInt64(value, CultureInfo.InvariantCulture);
        if (target == typeof(int))
            return Convert.ToInt32(value, CultureInfo.InvariantCulture);
        return Convert.ChangeType(value, target, CultureInfo.InvariantCulture);
    }
}
