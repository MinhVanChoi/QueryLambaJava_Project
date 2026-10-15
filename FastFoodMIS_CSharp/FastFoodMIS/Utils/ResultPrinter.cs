using System.Collections;

namespace FastFoodMIS_CSharp.Utils;

public static class ResultPrinter
{
    public static void Print(object? data)
    {
        if (data is null)
        {
            Console.WriteLine("(Không có dữ liệu)");
            return;
        }

        if (data is IDictionary dict)
        {
            int count = 0;
            foreach (DictionaryEntry entry in dict)
            {
                string key = entry.Key is bool b ? (b ? "Có khuyến mãi" : "Không khuyến mãi") : $"{entry.Key}";
                string value = entry.Value is bool ok ? (ok ? "KHỚP" : "LỆCH") : $"{entry.Value}";
                Console.WriteLine($"{key}: {value}");
                count++;
            }
            Console.WriteLine($"Tổng số dòng: {count}");
            return;
        }

        if (data is IEnumerable seq and not string)
        {
            int count = 0;
            foreach (object? item in seq)
            {
                Console.WriteLine(item is KeyValuePair<string, int> kv
                    ? $"{kv.Key}: {kv.Value}"
                    : item?.ToString() ?? "NULL");
                count++;
            }
            if (count == 0) Console.WriteLine("(Không có dữ liệu)");
            Console.WriteLine($"Tổng số dòng: {count}");
            return;
        }
        Console.WriteLine(data);
    }
}
