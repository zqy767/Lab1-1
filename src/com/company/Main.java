package com.company;
import com.sun.org.apache.xerces.internal.impl.dv.xs.IntegerDV;
import com.sun.org.apache.xerces.internal.impl.xpath.regex.Match;

import java.util.ArrayList;
import java.util.regex.*;
import  java.util.Scanner;
public class Main {


    public static void main(String[] args) {
        Scanner in = new Scanner(System.in);
        ArrayList<Var> vars = new ArrayList<>();
        Pattern simplify = Pattern.compile("^!simplify\\s(\\w+=\\d+\\s?)+");
        Pattern simplify_s = Pattern.compile("\\w+=\\d+");
        Pattern derivative = Pattern.compile("^!d/d\\s\\w+");
	   String Garbgeb1;
        String exp = in.nextLine();
        Union u = new Union();
        while (!exp.equals("exit")) {

            try {
                if (exp.charAt(0) != '!') {
                    u.ans.clear();
                    exp = exp.replace(" ", "");
                    u.turn_string_to_list(exp);
                    System.out.println(u.list_to_string());
                } else {
                    Matcher s_m = simplify.matcher(exp);
                    Matcher s_m_s = simplify_s.matcher(exp);
                    Matcher d_m = derivative.matcher(exp);

                    if (s_m.find()) {
                        Union tmp_sim =(Union) u.clone();
                        while (s_m_s.find()) {
                            String tmp = s_m_s.group();
                            int pos = tmp.indexOf('=');
                            String name = tmp.substring(0, pos);
                            int power = Integer.parseInt(tmp.substring(pos + 1));
                            vars.add(new Var(name, power));
                        }
                        String fin = tmp_sim.simply(vars);
                        if (tmp_sim.list_to_string().equals(u.list_to_string()))
                            System.out.println("no value has been found in early expression!");
                        else
                            System.out.println(fin);
                        vars.clear();
                    } else if (d_m.find()) {

                        String tmp = exp.substring(5);
                        Union tmp_sim;
                        tmp_sim = (Union) u.clone();
                        String fin = tmp_sim.derivative(tmp);
                        if (tmp_sim.list_to_string().equals(u.list_to_string()))
                          System.out.println("no value has been found in early expression!");
                       else
                            System.out.println(fin);
                    }
                    else System.out.println("wrong command!");
                }
            }catch(Exception e){
                    System.out.println("wrong format");

            }
           exp = in.nextLine();
        }


    }
}
