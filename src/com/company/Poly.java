package com.company;

import java.security.AlgorithmConstraints;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Scanner;
import java.util.function.ObjDoubleConsumer;

/**
 * Created by forandroid on 16-9-21.
 * expression between +,witch means * contained
 */
public class Poly implements Cloneable{

    int prenum;

   String Garbgeb1;
    // the Item is expression between *


    public ArrayList<Item> it;

    public Object clone() throws CloneNotSupportedException{
        Poly cloned = new Poly();
        cloned.prenum = prenum;
        for (Item i:it) {
            cloned.it.add((Item)i.clone());
        }
        return cloned;
    }


    Poly () {
        prenum = 1;
        it = new ArrayList<Item>();
    }

    private void create_var (String name, int power) {
        it.add(new Item(name,power));
    }

    private Item find_variable (String name) {
        for(int i = 0; i<it.size();i++) {
            if (it.get(i).name.equals(name))
                return it.get(i);
        }
        return null;
    }

    private void change_exp (String name ,int power) {
        Item item = find_variable(name);
        if (item != null)
            item.power += power;
        else create_var(name,power);
    }

    private char n_string  (String s,int n){
        return s.charAt(n);
    }

    private char first_string (String s) {
            return n_string(s, 0);
    }

    private int findnumber (String s) {
        int anspos = 0;
        if (first_string(s) == '-') {
            anspos++;
            if (!((n_string(s,anspos) <= '9' && n_string(s,anspos) >= '0')))
                return -1;
        }

        for (; anspos<s.length();anspos++) {
            if (!((n_string(s,anspos) <= '9' && n_string(s,anspos) >= '0')))
               break;
        }
        int ans = Integer.parseInt(s.substring(0,anspos));
        return ans;
    }

    private int how_long_the_number (String string,int start) {
        if (n_string(string,start) == '-') start++;
        for (int i = start; i<string.length();i++) {
            if (!((n_string(string,i) <= '9' && n_string(string,i) >= '0')))
                return i;
        }
        return string.length();
    }

    private int find_right_braket (String s) {
        int num = 0;
        for (int i = 0; i < s.length(); i++) {
            if (s.charAt(i) == '(') num++;
            if (s.charAt(i) == ')') {
                num--;
                if (num == 0) return i;
            }
         }

        return s.length();
    }

    private String bracket_expression_from_left (String s){
            return s.substring(0,(1 + find_right_braket(s)));
    }

    private int have_power_or_not_judge (String name,String power) {
        if ((first_string(power)) == '^') {
            change_exp(name,(findnumber(power.substring(1))));
            return name.length()+how_long_the_number(power,1);
        }
        else {
            change_exp(name,1);
            return name.length();
        }
    }

    private int have_power_or_not (String name,String power) {
        if (power.equals(""))
            return have_power_or_not_judge(name,"1");
        else
            return have_power_or_not_judge(name,power);

    }

    private int number_add_to_list (String string) throws Exception {
        char ch = first_string(string);
        if ('a' <= ch && ch <= 'z')
            return have_power_or_not(string.substring(0,1),string.substring(1));

        else if (ch == '(') {
            return have_power_or_not(bracket_expression_from_left(string),string.substring(find_right_braket(string)+1));
        } else throw new Exception("not legal in number-started expression");

    }

    private void start_with_number (String string) throws Exception{
        int prenum = findnumber(string);
        this.prenum *= prenum;
        for (int i = how_long_the_number(string,0); i<string.length();) {
            i += number_add_to_list(string.substring(i));
        }
    }

    private String sym_name (String s) {
        char ch = first_string(s);
        if (ch == '(') return s.substring(0,(find_right_braket(s)+1));
        else {
	    int pos2 = s.indexOf('(');
            int pos1 = s.indexOf('^');
            if (pos1==-1 && pos2 == -1 ) return s;
            if (pos1 == -1) return s.substring(0,pos2);
            if (pos2 == -1) return s.substring(0,pos1);
            return s.substring(0,Math.min(pos1,pos2));
        }
    }

    private int symbol_add_to_list(String s){
        String name = sym_name(s);
        return have_power_or_not(name,s.substring(name.length()));
    }

    private void start_with_symbol (String string)  {
        for (int i = 0; i<string.length();)
            i +=symbol_add_to_list (string.substring(i));
    }

    private void minmor (String string) throws Exception{
        char first = first_string(string);
        if (first >= '0' && first <= '9' || first == '-')
           start_with_number(string);
        if ('a' <= first && first <= 'z' || first == '(')
            start_with_symbol(string);
    }

    private ArrayList<String> bracket_split (String origin,char sym) {
        ArrayList<String> ans = new ArrayList<String>();
        int num = 0,start_point = 0;
        for (int  i = 0;  i < origin.length();  i++) {
            if (n_string(origin, i) == '(') num++;
            if (n_string(origin, i) == ')') num--;
            if (n_string(origin, i) == sym && num == 0) {
                ans.add(origin.substring(start_point, i));
                start_point = i + 1;
            }
        }
        ans.add(origin.substring(start_point,origin.length()));
        return ans;

    }


    public void morpheme (String string,int pos_or_neg) throws Exception{
        this.prenum = pos_or_neg;
        for (String s: bracket_split(string,'*')) {
            minmor(s);
        }

        this.it.sort(new Comparator<Item>() {
            @Override
            public int compare(Item o1, Item o2) {
                return o1.name.compareTo(o2.name);
            }
        });
    }

    public Poly remove (Item it) {
        Poly ans = new Poly();
        ans.prenum = this.prenum;
        ans.it = this.it;
        ans.it.remove(it);
        return ans;

    }

    public void print_out () {
        System.out.println(prenum);
        for (Item i: it) {
            System.out.printf("%s %s\n",i.name,i.power);
        }

    }
}

