package utils;

import java.util.Random;

public class CodeGenerator {
    public static String genererCode() {
        Random random = new Random();
        int code = 100000 + random.nextInt(900000); // 6 chiffres
        return String.valueOf(code);
    }
}
