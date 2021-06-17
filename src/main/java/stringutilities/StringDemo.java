/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package stringutilities;

/**
 *
 * @author Mohamed
 */
public class StringDemo {
    public static void main(String[] args) {
        String string1 = "MOhamed";
        String string2 = "ElMesawy";

        String longer = StringUtils.betterString(string1, string2, (s1, s2) ->  s1.length() >= s2.length());
        
        System.out.println("The longest string is: " + longer + ", has " + longer.length() + " characters.");
        
        boolean result = StringUtils.hasOnlyAlphabets("*mmm");
        System.out.println("Has only alphabets: " + result);
        
    }
}
