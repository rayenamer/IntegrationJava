package test;

import services.ReplyService;
import utils.MyDatabase;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;

public class TestReply {
    public static void main(String[] args) {
        try {
            Connection cnx = MyDatabase.getInstance().getCnx();
            DatabaseMetaData metaData = cnx.getMetaData();
            
            // Check if reply table exists
            ResultSet tables = metaData.getTables(null, null, "reply", null);
            if (!tables.next()) {
                System.out.println("Reply table does not exist!");
                return;
            }
            
            // Get columns of reply table
            ResultSet columns = metaData.getColumns(null, null, "reply", null);
            System.out.println("Reply table columns:");
            while (columns.next()) {
                String columnName = columns.getString("COLUMN_NAME");
                String columnType = columns.getString("TYPE_NAME");
                System.out.println(columnName + " (" + columnType + ")");
            }
            
            // Test ReplyService
            ReplyService replyService = new ReplyService();
            System.out.println("\nTesting ReplyService.recuperer():");
            System.out.println(replyService.recuperer());
            
        } catch (SQLException e) {
            System.err.println("Error: " + e.getMessage());
            e.printStackTrace();
        }
    }
} 