package com.majorpotato.febridge.util;


public class FormatHelper {

    public static String simplifyNumberFormat(int num) {
        int digits = digitCount(num);
        if(digits < 4) return ""+num;
        else if(digits < 7) {
            if(digits > 4) return ""+num/1000+"K";
            else return ""+num/1000+"."+( (num/100) - 10*(num/1000) )+"K";
        }
        else {
            if(digits > 7) return ""+num/1000000+"M";
            else return ""+num/1000000+"."+( (num/100000) - 10*(num/1000000) )+"K";
        }
    }

    public static int digitCount(int num) {
        if (num < 100000)
        {
            // 5 or less
            if (num < 100)
            {
                // 1 or 2
                if (num < 10)
                    return 1;
                else
                    return 2;
            }
            else
            {
                // 3 or 4 or 5
                if (num < 1000)
                    return 3;
                else
                {
                    // 4 or 5
                    if (num < 10000)
                        return 4;
                    else
                        return 5;
                }
            }
        }
        else
        {
            // 6 or more
            if (num < 10000000)
            {
                // 6 or 7
                if (num < 1000000)
                    return 6;
                else
                    return 7;
            }
            else
            {
                // 8 to 10
                if (num < 100000000)
                    return 8;
                else
                {
                    // 9 or 10
                    if (num < 1000000000)
                        return 9;
                    else
                        return 10;
                }
            }
        }
    }

    public static String nicePos2i(int x, int y) {
        return "( "+x+", "+y+" )";
    }
    public static String nicePos3i(int x, int y, int z) {
        return "( "+x+", "+y+", "+z+" )";
    }

}
