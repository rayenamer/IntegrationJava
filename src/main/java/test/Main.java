package test;

import services.DiscussionService;
import utils.MyDatabase;

import java.sql.SQLException;

public class Main {

    public static void main(String[] args) {
        DiscussionService ds = new DiscussionService();
        try {
            System.out.println(ds.recuperer());
        } catch (SQLException e) {
            System.err.println(e.getMessage());
        }
    }
}
