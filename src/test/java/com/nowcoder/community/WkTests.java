package com.nowcoder.community;

import java.io.IOException;

public class WkTests {

    public static void main(String[] args) {
        String cmd = "D:/ProgrameFiles/Tools/wkhtmltopdf/bin/wkhtmltoimage --quality 75  https://www.nowcoder.com d:/ProgramData/nowcoder/wkhtmltopdf/image/3.png";
        try {
            Runtime.getRuntime().exec(cmd);
            System.out.println("ok.");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
