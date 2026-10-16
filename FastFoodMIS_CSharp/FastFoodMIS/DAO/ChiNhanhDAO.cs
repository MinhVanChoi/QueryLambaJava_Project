
using MySqlConnector;
using FastFoodMIS_CSharp.Database;
using FastFoodMIS_CSharp.Models;

namespace FastFoodMIS_CSharp.DAO;

public class ChiNhanhDAO
{
    // 1. Lấy tất cả chi nhánh
    public List<ChiNhanh> FindAll()
    {
        List<ChiNhanh> list = new();

        const string sql = @"
            SELECT maCN, tenCN, diaChi, soDienThoai
            FROM chi_nhanh
            ORDER BY maCN";

        using var conn = DBConnection.GetConnection();
        using var cmd = new MySqlCommand(sql, conn);
        using var reader = cmd.ExecuteReader();

        while (reader.Read())
        {
            list.Add(MapRow(reader));
        }

        return list;
    }

    // 2. Tìm chi nhánh theo mã
    public ChiNhanh? FindById(string maCN)
    {
        const string sql = @"
            SELECT maCN, tenCN, diaChi, soDienThoai
            FROM chi_nhanh
            WHERE LOWER(maCN) = LOWER(@maCN)
            LIMIT 1";

        using var conn = DBConnection.GetConnection();
        using var cmd = new MySqlCommand(sql, conn);

        cmd.Parameters.AddWithValue("@maCN", maCN);

        using var reader = cmd.ExecuteReader();

        if (reader.Read())
        {
            return MapRow(reader);
        }

        return null;
    }

    // 3. Thêm chi nhánh
    public bool Insert(ChiNhanh cn)
    {
        const string sql = @"
            INSERT INTO chi_nhanh
                (maCN, tenCN, diaChi, soDienThoai)
            VALUES
                (@maCN, @tenCN, @diaChi, @soDienThoai)";

        using var conn = DBConnection.GetConnection();
        using var cmd = new MySqlCommand(sql, conn);

        cmd.Parameters.AddWithValue("@maCN", cn.MaCN);
        cmd.Parameters.AddWithValue("@tenCN", cn.TenCN);
        cmd.Parameters.AddWithValue("@diaChi", cn.DiaChi);
        cmd.Parameters.AddWithValue(
            "@soDienThoai", cn.SoDienThoai);

        return cmd.ExecuteNonQuery() > 0;
    }

    // 4. Cập nhật chi nhánh
    public bool Update(ChiNhanh cn)
    {
        const string sql = @"
            UPDATE chi_nhanh
            SET tenCN = @tenCN,
                diaChi = @diaChi,
                soDienThoai = @soDienThoai
            WHERE maCN = @maCN";

        using var conn = DBConnection.GetConnection();
        using var cmd = new MySqlCommand(sql, conn);

        cmd.Parameters.AddWithValue("@maCN", cn.MaCN);
        cmd.Parameters.AddWithValue("@tenCN", cn.TenCN);
        cmd.Parameters.AddWithValue("@diaChi", cn.DiaChi);
        cmd.Parameters.AddWithValue(
            "@soDienThoai", cn.SoDienThoai);

        return cmd.ExecuteNonQuery() > 0;
    }

    // 5. Xóa chi nhánh
    public bool Delete(string maCN)
    {
        const string sql = @"
            DELETE FROM chi_nhanh
            WHERE maCN = @maCN";

        using var conn = DBConnection.GetConnection();
        using var cmd = new MySqlCommand(sql, conn);

        cmd.Parameters.AddWithValue("@maCN", maCN);

        return cmd.ExecuteNonQuery() > 0;
    }

    // 6. Chuyển dữ liệu MySQL sang Model
    private static ChiNhanh MapRow(MySqlDataReader reader)
    {
        return new ChiNhanh
        {
            MaCN = reader.GetString("maCN"),
            TenCN = reader.GetString("tenCN"),
            DiaChi = reader.GetString("diaChi"),
            SoDienThoai = reader.GetString("soDienThoai")
        };
    }
}
