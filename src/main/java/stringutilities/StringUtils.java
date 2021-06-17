/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package stringutilities;

import java.util.function.BiPredicate;

/**
 *
 * @author Mohamed
 */
public class StringUtils {
    public static String betterString(String s1, String s2, BiPredicate<String, String> predicate){
        if (predicate.test(s1, s2)) 
            return s1;
        return s2;
    }
    
    public static boolean hasOnlyAlphabets(String s){
        return s.chars().allMatch(Character::isLetter);
    }
    
}
